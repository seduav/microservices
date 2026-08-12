# connect-orders.ps1

# ============================================================
# 0. If elevated, launch a NON-ELEVATED PowerShell through
#    the normal Windows Explorer shell.
# ============================================================

$identity = [Security.Principal.WindowsIdentity]::GetCurrent()
$principal = New-Object Security.Principal.WindowsPrincipal($identity)

$isElevated = $principal.IsInRole(
    [Security.Principal.WindowsBuiltInRole]::Administrator
)

if ($isElevated) {

    Write-Host ""
    Write-Host "IntelliJ is running as Administrator." -ForegroundColor Yellow
    Write-Host "Opening a normal PowerShell terminal..." -ForegroundColor Cyan
    Write-Host ""

    $scriptPath = $MyInvocation.MyCommand.Path

    # Create a temporary .cmd file.
    # Explorer will launch this using its normal (non-elevated) token.
    $tempCmd = Join-Path $env:TEMP "connect-orders-normal.cmd"

    @"
@echo off
title PostgreSQL Orders
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "$scriptPath"
pause
"@ | Set-Content -Path $tempCmd -Encoding ASCII

    # Ask the normal Windows Explorer shell to open the CMD file.
    # Explorer normally runs at the user's non-elevated integrity level.
    Start-Process `
        -FilePath "explorer.exe" `
        -ArgumentList "`"$tempCmd`""

    Write-Host "Normal terminal requested." -ForegroundColor Green
    Write-Host "Closing elevated instance..." -ForegroundColor Gray

    exit 0
}

# ============================================================
# We should now be running non-elevated
# ============================================================

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host " PostgreSQL Orders Connection" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# ============================================================
# 1. Find newest PostgreSQL installation
# ============================================================

$postgresRoot = "C:\Program Files\PostgreSQL"

$postgres = Get-ChildItem `
    -Path $postgresRoot `
    -Directory `
    -ErrorAction SilentlyContinue |
    Where-Object {
        $_.Name -match '^\d+$' -and
        (Test-Path (Join-Path $_.FullName "bin\psql.exe"))
    } |
    Sort-Object { [int]$_.Name } -Descending |
    Select-Object -First 1

if (-not $postgres) {
    Write-Host "ERROR: PostgreSQL installation not found." -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

$version     = $postgres.Name
$installPath = $postgres.FullName
$binPath     = Join-Path $installPath "bin"
$psqlExe     = Join-Path $binPath "psql.exe"
$pgCtlExe    = Join-Path $binPath "pg_ctl.exe"

Write-Host "PostgreSQL version : $version" -ForegroundColor Green
Write-Host "Installation       : $installPath" -ForegroundColor Gray

# ============================================================
# 2. Find data directory
# ============================================================

$dataDir = Join-Path $installPath "data"

if (-not (Test-Path (Join-Path $dataDir "PG_VERSION"))) {

    Write-Host "Searching for data directory..." -ForegroundColor Yellow

    $pgVersionFile = Get-ChildItem `
        -Path $installPath `
        -Filter "PG_VERSION" `
        -Recurse `
        -File `
        -ErrorAction SilentlyContinue |
        Select-Object -First 1

    if ($pgVersionFile) {
        $dataDir = $pgVersionFile.Directory.FullName
    }
}

if (-not (Test-Path (Join-Path $dataDir "PG_VERSION"))) {

    Write-Host ""
    Write-Host "ERROR: PostgreSQL data directory not found." -ForegroundColor Red
    Write-Host ""
    Write-Host "Expected:" -ForegroundColor Yellow
    Write-Host "$installPath\data" -ForegroundColor White
    Write-Host ""

    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "Data directory     : $dataDir" -ForegroundColor Gray

# ============================================================
# 3. Detect PostgreSQL port
# ============================================================

$port = 5432
$configFile = Join-Path $dataDir "postgresql.conf"

if (Test-Path $configFile) {

    $portSetting = Select-String `
        -Path $configFile `
        -Pattern '^\s*port\s*=\s*(\d+)' |
        Select-Object -First 1

    if ($portSetting) {
        $port = [int]$portSetting.Matches[0].Groups[1].Value
    }
}

Write-Host "PostgreSQL port    : $port" -ForegroundColor Green

# ============================================================
# 4. Check if PostgreSQL is already running
# ============================================================

Write-Host ""
Write-Host "Checking localhost:$port ..." -ForegroundColor Cyan

$connection = Test-NetConnection `
    -ComputerName localhost `
    -Port $port `
    -WarningAction SilentlyContinue

if ($connection.TcpTestSucceeded) {

    Write-Host "PostgreSQL is already running." -ForegroundColor Green

}
else {

    Write-Host "PostgreSQL is not running." -ForegroundColor Yellow
    Write-Host "Starting PostgreSQL..." -ForegroundColor Cyan
    Write-Host ""

    if (-not (Test-Path $pgCtlExe)) {
        Write-Host "ERROR: pg_ctl.exe not found." -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit 1
    }

    $logFile = Join-Path $dataDir "postgres-start.log"

    & $pgCtlExe `
        -D $dataDir `
        -l $logFile `
        -w `
        start

    if ($LASTEXITCODE -ne 0) {

        Write-Host ""
        Write-Host "ERROR: PostgreSQL failed to start." -ForegroundColor Red
        Write-Host ""

        if (Test-Path $logFile) {
            Write-Host "Last PostgreSQL log entries:" -ForegroundColor Yellow
            Get-Content $logFile -Tail 30
        }

        Write-Host ""
        Read-Host "Press Enter to exit"
        exit 1
    }

    Write-Host ""
    Write-Host "PostgreSQL started successfully." -ForegroundColor Green
}

# ============================================================
# 5. Connect to orders
# ============================================================

Write-Host ""
Write-Host "Connecting to database 'orders'..." -ForegroundColor Cyan
Write-Host ""

& $psqlExe `
    -h localhost `
    -p $port `
    -U postgres `
    -d orders

Write-Host ""
Read-Host "Press Enter to close"