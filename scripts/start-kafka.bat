@echo off
cd /d ""%USERPROFILE%\kafka"

echo Checking if Kafka is already running...

netstat -ano | findstr ":9092" | findstr "LISTENING" >nul

if %ERRORLEVEL%==0 (
    echo Kafka is already running on port 9092.
    exit /b 0
)

echo Kafka is not running. Starting Kafka...
call bin\windows\kafka-server-start.bat config\server.properties