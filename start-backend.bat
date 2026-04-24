@echo off
echo ========================================
echo Drone Patrol System - Backend Startup
echo ========================================
echo.

REM Check Java
echo [1/3] Checking Java environment...
java -version
if errorlevel 1 (
    echo ERROR: Java not found!
    pause
    exit /b 1
)
echo OK
echo.

REM Check Maven
echo [2/3] Checking Maven environment...
mvn -version
if errorlevel 1 (
    echo ERROR: Maven not found!
    pause
    exit /b 1
)
echo OK
echo.

REM Go to server directory
echo [3/3] Starting backend service...
cd /d "%~dp0server"
echo Current directory: %cd%
echo.

REM Start Spring Boot
echo Starting Spring Boot app (port 8080)...
echo Please ensure MySQL and Redis are running!
echo.
mvn clean spring-boot:run

pause
