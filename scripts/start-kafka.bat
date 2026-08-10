@echo off
cd /d ""%USERPROFILE%\kafka"

call bin\windows\kafka-server-start.bat config\server.properties