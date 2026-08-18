# run-services.ps1

$ErrorActionPreference = "Stop"

$root = $PSScriptRoot

Write-Host "Project root: $root" -ForegroundColor Green

Write-Host ""
Write-Host "Starting order-service..." -ForegroundColor Cyan

Start-Process powershell -ArgumentList @(
    "-NoExit",
    "-Command",
    "Set-Location '$root'; & '.\gradlew.bat' :order-service:bootRun"
)

Start-Sleep -Seconds 10

Write-Host ""
Write-Host "Starting inventory-service..." -ForegroundColor Cyan

Start-Process powershell -ArgumentList @(
    "-NoExit",
    "-Command",
    "Set-Location '$root'; & '.\gradlew.bat' :inventory-service:bootRun"
)

Start-Sleep -Seconds 10

Write-Host ""
Write-Host "Starting dlt-monitor..." -ForegroundColor Cyan

Start-Process powershell -ArgumentList @(
    "-NoExit",
    "-Command",
    "Set-Location '$root'; & '.\gradlew.bat' :dlt-monitor:bootRun"
)

Start-Sleep -Seconds 10

Write-Host ""
Write-Host "Starting notification-service..." -ForegroundColor Cyan

Start-Process powershell -ArgumentList @(
    "-NoExit",
    "-Command",
    "Set-Location '$root'; & '.\gradlew.bat' :notification-service:bootRun"
)

Write-Host ""
Write-Host "All services started." -ForegroundColor Green