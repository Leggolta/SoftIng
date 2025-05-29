@echo off
cd /d %~dp0
echo I'm in: %cd%
echo Running mvn javafx:run...
mvn javafx:run
pause