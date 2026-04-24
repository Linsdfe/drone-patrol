@echo off
echo ========================================
echo Drone Patrol System - Frontend Startup
echo ========================================
echo.

REM Check Node.js
echo [1/3] Checking Node.js environment...
node -v
if errorlevel 1 (
    echo ERROR: Node.js not found!
    pause
    exit /b 1
)
echo OK
echo.

REM Check npm
echo [2/3] Checking npm environment...
npm -v
if errorlevel 1 (
    echo ERROR: npm not found!
    pause
    exit /b 1
)
echo OK
echo.

REM Go to web directory
echo [3/3] Preparing frontend service...
cd /d "%~dp0web"
echo Current directory: %cd%
echo.

REM Check node_modules
if not exist "node_modules" (
    echo First run, installing dependencies...
    echo (If slow, run first: npm config set registry https://registry.npmmirror.com)
    echo.
    npm install
    if errorlevel 1 (
        echo Dependency installation failed!
        pause
        exit /b 1
    )
    echo.
)

REM Start dev server
echo Starting frontend dev server (port 3000)...
echo Please ensure backend is running!
echo.
npm run dev

pause
