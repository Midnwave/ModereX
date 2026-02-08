@echo off
REM ModereX Cloudflare Worker Setup Script
REM Run this script to set up the license system

echo ========================================
echo ModereX License System Setup
echo ========================================
echo.

REM Check if wrangler is installed
where wrangler >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: wrangler is not installed!
    echo Install it with: npm install -g wrangler
    pause
    exit /b 1
)

echo [1/4] Creating KV namespace...
echo.
echo Run this command and copy the production ID:
echo wrangler kv namespace create LICENSE_TOKENS
echo.
echo Then run this for preview:
echo wrangler kv namespace create LICENSE_TOKENS --preview
echo.
pause

echo.
echo [2/4] Setting private key secret...
echo.
echo Run this command and paste the ENTIRE contents of private_key.pem:
echo wrangler secret put PRIVATE_KEY
echo.
echo TIP: Open private_key.pem, copy everything (including BEGIN/END lines)
pause

echo.
echo [3/4] Setting admin secret...
echo.
echo Run this command and paste this secret:
echo wrangler secret put ADMIN_SECRET
echo.
echo YOUR ADMIN SECRET:
echo 16a72a240d6934b3ddc1730e16bc83cafbb01912ac2a435b05f95e0e0ac0727d
echo.
echo (This is already configured in your gateway.js)
pause

echo.
echo [4/4] Deploying worker...
wrangler deploy
echo.

if %errorlevel% equ 0 (
    echo ========================================
    echo SUCCESS! License system is deployed!
    echo ========================================
    echo.
    echo Next steps:
    echo 1. Test the API: curl https://license.moderex.net/validate -H "Content-Type: application/json" -d "{\"token\":\"test\"}"
    echo 2. Open your admin panel and try creating a license
    echo 3. Build a licensed JAR and test on a server
    echo.
) else (
    echo ========================================
    echo DEPLOYMENT FAILED
    echo ========================================
    echo Check the error above and fix any issues
    echo.
)

pause
