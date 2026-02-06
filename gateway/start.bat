@echo off
echo.
echo  ModereX Gateway Server
echo  =======================
echo.

REM Check if Node.js is installed
where node >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo  ERROR: Node.js is not installed!
    echo  Download from: https://nodejs.org
    echo.
    pause
    exit /b 1
)

REM Check if node_modules exists
if not exist "node_modules" (
    echo  Installing dependencies...
    npm install
    echo.
)

echo  Starting gateway on port 3000...
echo  Press Ctrl+C to stop
echo.

node gateway.js
