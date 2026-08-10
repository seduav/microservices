@echo off
setlocal

cd /d ""%USERPROFILE%\kafka"

for /f %%i in ('bin\windows\kafka-storage.bat random-uuid') do set CLUSTER_ID=%%i

echo Formatting Kafka with Cluster ID: %CLUSTER_ID%

call bin\windows\kafka-storage.bat format ^
-t %CLUSTER_ID% ^
-c config\server.properties ^
--standalone

endlocal