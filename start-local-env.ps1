# start-local-env.ps1

$ErrorActionPreference = "Stop"

# Repository root (where this script is located)
$repoRoot = $PSScriptRoot

Write-Host "Repository root: $repoRoot" -ForegroundColor Green

Set-Location $repoRoot

Start-Process cmd.exe -ArgumentList "/c `"$repoRoot\scripts\start-kafka.bat`""

Write-Host ""
Write-Host "Waiting for Kafka to become available..." -ForegroundColor Yellow

$maxAttempts = 60
$attempt = 0
$kafkaReady = $false

while (-not $kafkaReady -and $attempt -lt $maxAttempts) {

    $attempt++

    $connection = Test-NetConnection `
        -ComputerName "localhost" `
        -Port 9092 `
        -WarningAction SilentlyContinue

    if ($connection.TcpTestSucceeded) {
        $kafkaReady = $true
        break
    }

    Write-Host "Kafka not ready yet... attempt $attempt/$maxAttempts"

    Start-Sleep -Seconds 5
}

if (-not $kafkaReady) {
    Write-Host ""
    Write-Host "Kafka failed to start." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Kafka is running." -ForegroundColor Green

$topicScript = Join-Path $repoRoot "scripts\create-kafka-topic.bat"

if (!(Test-Path $topicScript)) {
    Write-Host "Cannot find Kafka topic script:" -ForegroundColor Red
    Write-Host $topicScript
    exit 1
}

cmd /c $topicScript

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "Failed to create Kafka topic." -ForegroundColor Red
    exit 1
}


Write-Host ""
Write-Host "Starting Spring Boot services..." -ForegroundColor Green

$servicesScript = Join-Path $repoRoot "run-services.ps1"

if (!(Test-Path $servicesScript)) {
    Write-Host "Cannot find services script:" -ForegroundColor Red
    Write-Host $servicesScript
    exit 1
}

& $servicesScript

Write-Host ""
Write-Host "Local environment started successfully." -ForegroundColor Green