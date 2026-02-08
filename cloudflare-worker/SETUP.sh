#!/bin/bash

# ModereX Cloudflare Worker Setup Script
# Run this script to set up the license system

echo "========================================"
echo "ModereX License System Setup"
echo "========================================"
echo ""

# Check if wrangler is installed
if ! command -v wrangler &> /dev/null; then
    echo "ERROR: wrangler is not installed!"
    echo "Install it with: npm install -g wrangler"
    exit 1
fi

echo "[1/4] Creating KV namespace..."
echo ""
echo "Creating production namespace..."
wrangler kv namespace create LICENSE_TOKENS
echo ""
echo "Creating preview namespace..."
wrangler kv namespace create LICENSE_TOKENS --preview
echo ""
echo "Copy the IDs above and update wrangler.toml"
read -p "Press Enter when you've updated wrangler.toml..."

echo ""
echo "[2/4] Setting private key secret..."
echo ""
echo "Paste the ENTIRE contents of private_key.pem when prompted:"
wrangler secret put PRIVATE_KEY

echo ""
echo "[3/4] Setting admin secret..."
echo ""
echo "Paste this secret when prompted:"
echo "16a72a240d6934b3ddc1730e16bc83cafbb01912ac2a435b05f95e0e0ac0727d"
echo ""
echo "(This is already configured in your gateway.js)"
wrangler secret put ADMIN_SECRET

echo ""
echo "[4/4] Deploying worker..."
wrangler deploy

if [ $? -eq 0 ]; then
    echo ""
    echo "========================================"
    echo "SUCCESS! License system is deployed!"
    echo "========================================"
    echo ""
    echo "Next steps:"
    echo "1. Test the API: curl https://license.moderex.net/validate -H \"Content-Type: application/json\" -d '{\"token\":\"test\"}'"
    echo "2. Open your admin panel and try creating a license"
    echo "3. Build a licensed JAR and test on a server"
    echo ""
else
    echo ""
    echo "========================================"
    echo "DEPLOYMENT FAILED"
    echo "========================================"
    echo "Check the error above and fix any issues"
    echo ""
fi
