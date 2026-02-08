#!/bin/bash

# Generate RSA key pair for license API
echo "Generating RSA-2048 key pair..."

# Generate private key
openssl genrsa -out private_key.pem 2048

# Extract public key
openssl rsa -in private_key.pem -pubout -out public_key.pem

echo ""
echo "✓ Keys generated successfully!"
echo ""
echo "Private key: private_key.pem (keep this SECRET!)"
echo "Public key:  public_key.pem (embed in plugin JAR)"
echo ""
echo "Next steps:"
echo "1. Set private key as Cloudflare secret: wrangler secret put PRIVATE_KEY < private_key.pem"
echo "2. Copy public_key.pem to: app/src/main/resources/license-public-key.pem"
echo "3. Generate admin secret: uuidgen (or use: openssl rand -hex 32)"
echo "4. Set admin secret: wrangler secret put ADMIN_SECRET"
