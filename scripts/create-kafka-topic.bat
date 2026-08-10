@echo off
setlocal

REM Change this if your Kafka installation is in another location
cd C:\Users\eduards.stulpe\kafka

echo Checking if Kafka topic exists...

bin\windows\kafka-topics.bat ^
--list ^
--bootstrap-server localhost:9092 | findstr /x "order-created" >nul

if %ERRORLEVEL% EQU 0 (
    echo Topic 'order-created' already exists. Skipping creation.
) else (
    echo Creating Kafka topic: order-created

    call bin\windows\kafka-topics.bat ^
    --create ^
    --topic order-created ^
    --bootstrap-server localhost:9092

    if errorlevel 1 (
        echo Failed to create topic.
        pause
        exit /b 1
    )

    echo Topic created successfully.
)

echo.
echo Current Kafka topics:

call bin\windows\kafka-topics.bat ^
--list ^
--bootstrap-server localhost:9092

endlocal