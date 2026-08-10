# run-services.ps1

$ErrorActionPreference = "Stop"

$root = $PSScriptRoot

Write-Host "Project root: $root" -ForegroundColor Green

Write-Host ""
Write-Host "Starting order-service..." -ForegroundColor Green

Start-Process powershell -ArgumentList @(
    "-NoExit",
    "-Command",
    "Set-Location '$root'; & '.\gradlew.bat' :order-service:bootRun"
)

Start-Sleep -Seconds 10

Write-Host ""
Write-Host "Starting inventory-service..." -ForegroundColor Green

Start-Process powershell -ArgumentList @(
    "-NoExit",
    "-Command",
    "Set-Location '$root'; & '.\gradlew.bat' :inventory-service:bootRun"
)

Write-Host ""
Write-Host "Both services started." -ForegroundColor Green