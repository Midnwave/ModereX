# ModereX Dev Build License System - Deployment Guide

## Overview

This guide covers deploying the complete dev build license system for ModereX, including:
- Cloudflare Workers API for license validation
- Gateway integration for license management
- Admin panel UI for creating and managing licenses
- Plugin license validation system

---

## Prerequisites

- Cloudflare account with Workers enabled
- Wrangler CLI installed (`npm install -g wrangler`)
- Node.js 18+ for gateway
- Java 21 for plugin builds
- OpenSSL for generating RSA keys

---

## Part 1: Generate RSA Key Pair

The license system uses RSA-2048 signing to prevent API response tampering.

```bash
cd cloudflare-worker

# Generate RSA-2048 private key
openssl genrsa -out private_key.pem 2048

# Extract public key
openssl rsa -in private_key.pem -pubout -out public_key.pem
```

**Files created:**
- `private_key.pem` - Keep SECRET! Used by Cloudflare Worker to sign responses
- `public_key.pem` - Embedded in plugin JAR to verify signatures

**Copy public key to plugin:**
```bash
cp public_key.pem ../app/src/main/resources/license-public-key.pem
```

---

## Part 2: Deploy Cloudflare Worker

### 2.1 Create KV Namespace

```bash
cd cloudflare-worker

# Production namespace
wrangler kv:namespace create LICENSE_TOKENS

# Preview namespace (for testing)
wrangler kv:namespace create LICENSE_TOKENS --preview
```

Copy the namespace IDs from the output and update `wrangler.toml`:

```toml
kv_namespaces = [
  { binding = "LICENSE_TOKENS", id = "YOUR_PRODUCTION_ID_HERE", preview_id = "YOUR_PREVIEW_ID_HERE" }
]
```

### 2.2 Set Environment Secrets

```bash
# Set private key (paste entire PEM including headers)
wrangler secret put PRIVATE_KEY
# Paste contents of private_key.pem when prompted

# Set admin secret (generate a strong UUID)
wrangler secret put ADMIN_SECRET
# Use: uuidgen (macOS/Linux) or openssl rand -hex 32
```

**Save the ADMIN_SECRET** - you'll need it for the gateway configuration!

### 2.3 Configure Custom Domain

In `wrangler.toml`, update the routes section:

```toml
routes = [
  { pattern = "license.moderex.net/*", zone_name = "moderex.net" }
]
```

### 2.4 Deploy

```bash
wrangler deploy
```

Verify deployment:
```bash
curl https://license.moderex.net/validate \
  -H "Content-Type: application/json" \
  -d '{"token":"test"}'

# Should return: {"valid":false,"error":"Invalid token"}
```

---

## Part 3: Configure Gateway

### 3.1 Add Admin Secret to Gateway

The gateway needs the ADMIN_SECRET to communicate with the license API.

**Option 1: Environment Variable**
```bash
export CLOUDFLARE_ADMIN_SECRET="your-admin-secret-from-step-2.2"
node gateway.js
```

**Option 2: Hardcode in gateway.js**

Find the license management functions and update:

```javascript
const CLOUDFLARE_ADMIN_SECRET = 'your-admin-secret-here';

// In createDevLicense function
fetch('https://license.moderex.net/admin/create', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'X-Admin-Secret': CLOUDFLARE_ADMIN_SECRET
  },
  body: JSON.stringify(data)
})
```

### 3.2 Test Gateway License Functions

Start the gateway:
```bash
cd gateway
npm start
```

Access admin panel:
```
https://moderex.net/admin
```

Try creating a test license to verify integration.

---

## Part 4: Build Licensed JAR

### 4.1 Via Gateway Admin Panel (Recommended)

1. Open `https://moderex.net/admin`
2. Navigate to "Dev Licenses"
3. Click "Generate License"
4. Fill in tester details
5. Click "Build Licensed JAR"
6. Download from `gateway/licensed-builds/`

### 4.2 Via Command Line

```bash
cd gateway
npm run build-licensed <license-token> "Tester Name"
```

### 4.3 Manual Gradle Build

```bash
cd app
./gradlew buildLicensed -PlicenseToken=your-token-here
```

Output: `releases/licensed/ModereX-licensed-<short-token>.jar`

---

## Part 5: Testing the License System

### 5.1 Test Unlicensed Build (Production)

```bash
cd app
./gradlew clean shadowJar
```

Start a test server with the JAR - should run normally without license checks.

Console output:
```
[License] Running as unlicensed build (production mode)
```

### 5.2 Test Licensed Build

1. Generate a test license via admin panel
2. Build licensed JAR with that token
3. Start a test server with the licensed JAR

**Expected console output:**
```
[License] Running as licensed dev build - validating...
[License] License token loaded: a1b2c3d4...e5f6
[License] Validation successful
[License] License valid - no expiration
```

### 5.3 Test License Revocation

1. Start server with licensed JAR (should start successfully)
2. In admin panel, revoke the license
3. Wait 30 minutes (or restart server)
4. Server should disable itself with error message

**Expected console output:**
```
╔═══════════════════════════════════════════════════════════╗
║                                                           ║
║              LICENSE VALIDATION FAILED                    ║
║                                                           ║
║  This is a licensed development build of ModereX.        ║
║  The license token embedded in this build is invalid,    ║
║  revoked, or expired.                                    ║
║                                                           ║
║  The plugin will now disable itself.                     ║
║                                                           ║
╚═══════════════════════════════════════════════════════════╝
```

### 5.4 Test Build Script

```bash
cd gateway

# Generate a test license first via admin panel
# Then build with that token:
npm run build-licensed <test-token> "QA Tester"
```

Verify:
- JAR appears in `gateway/licensed-builds/`
- Metadata JSON file created
- Build logged in gateway database

---

## Part 6: Production Workflow

### 6.1 Creating a Dev License

1. Admin opens `https://moderex.net/admin`
2. Navigate to "Dev Licenses" tab
3. Click "Generate License"
4. Enter:
   - Tester Name: "John Doe"
   - Max Servers: 1
   - Expires At: (optional) 30 days from now
   - Note: "Internal QA testing"
5. Click "Generate License"

Token is created in Cloudflare Workers KV and gateway database.

### 6.2 Building Licensed JAR

**From Admin Panel:**
1. In "Build Licensed JAR" section
2. Select the license token from dropdown
3. Enter tester name
4. Click "Build JAR"
5. Wait for build to complete (~2-3 minutes)
6. Download from `gateway/licensed-builds/` directory

**Distribution:**
- Send JAR to tester via secure channel (Google Drive, Discord DM, etc.)
- Instruct tester to place in `plugins/` folder
- No additional configuration needed

### 6.3 Monitoring Active Licenses

Admin panel shows for each license:
- Token (masked: `a1b2c3d4...e5f6`)
- Tester name
- Status (Active/Revoked/Expired)
- Created date
- Expiry date
- Last heartbeat (when server last checked in)

### 6.4 Revoking a License

If a JAR is leaked or tester access should be removed:

1. Find license in admin panel
2. Click "Revoke" button
3. Confirm revocation
4. Next time the server validates (within 30 mins), plugin will disable itself

---

## Security Best Practices

### For Admins

✅ **DO:**
- Keep RSA private key secret
- Use strong ADMIN_SECRET (UUID or 32+ random chars)
- Rotate ADMIN_SECRET periodically
- Track which tester has which token
- Revoke licenses when testing is complete
- Use short expiry dates (7-30 days)

❌ **DON'T:**
- Share private key or ADMIN_SECRET
- Commit secrets to git
- Reuse license tokens
- Leave expired licenses active

### For Testers

✅ **DO:**
- Keep licensed JAR private
- Report if JAR is accidentally leaked
- Remove JAR when done testing

❌ **DON'T:**
- Share licensed JAR publicly
- Upload to public GitHub repos
- Post JAR download links

---

## Troubleshooting

### License validation fails on startup

**Symptom:** Plugin disables with "LICENSE VALIDATION FAILED"

**Causes:**
1. License token revoked → Check admin panel
2. License expired → Check expiry date
3. Network issue → Check if server can reach `license.moderex.net`
4. Wrong token in JAR → Rebuild with correct token

**Debug:**
Enable verbose logging to see validation details.

### Build script fails

**Symptom:** `npm run build-licensed` exits with error

**Causes:**
1. Gradle not installed → Install Gradle 8+
2. Java not found → Install JDK 21
3. Git not available → Install git or use local repo copy
4. Invalid token → Check token format (UUID v4)

**Fix:** Check script output for specific error.

### Admin panel can't create licenses

**Symptom:** "Error creating license" in admin panel

**Causes:**
1. Cloudflare Worker not deployed
2. ADMIN_SECRET mismatch
3. KV namespace not bound
4. Network issue

**Debug:**
1. Check Cloudflare Workers logs
2. Verify ADMIN_SECRET in both gateway and CF Worker
3. Test CF Worker with curl

### Heartbeat warnings in console

**Symptom:** `[License] Heartbeat failed - may indicate revoked license`

**Causes:**
1. Temporary network issue
2. License was revoked
3. CF Worker rate limit hit

**Action:**
- If occasional: ignore (normal for network blips)
- If persistent: check license status in admin panel

---

## Architecture Diagram

```
┌─────────────────┐
│  Admin Panel    │ ── Create/Revoke ──┐
│  (moderex.net)  │                     │
└─────────────────┘                     ▼
                              ┌──────────────────────┐
┌─────────────────┐           │  Cloudflare Workers  │
│  Gateway Server │ ── Auth ──│   (license API)      │
│  (Node.js)      │           │   ┌──────────────┐   │
└─────────────────┘           │   │  KV Storage  │   │
        │                     │   │   (tokens)   │   │
        │                     │   └──────────────┘   │
        │                     └──────────────────────┘
        │                                ▲
        │ Build                          │ Validate
        │ Licensed                       │ (RSA signed)
        ▼ JAR                            │
┌─────────────────┐                     │
│  Tester Server  │─────────────────────┘
│  (Minecraft)    │  Heartbeat every 30m
│  ModereX Plugin │
└─────────────────┘
```

---

## Files Reference

### Cloudflare Worker
- `cloudflare-worker/license-api.js` - Main API code
- `cloudflare-worker/wrangler.toml` - Configuration
- `cloudflare-worker/private_key.pem` - **SECRET** - Do not commit!
- `cloudflare-worker/public_key.pem` - Public key for signature verification

### Gateway
- `gateway/scripts/build-licensed.js` - Build automation script
- `gateway/licensed-builds/` - Output directory for licensed JARs
- `gateway/gateway.js` - License management handlers (lines 2800-3050)

### Plugin
- `app/src/main/java/com/blockforge/moderex/license/LicenseManager.java`
- `app/src/main/java/com/blockforge/moderex/license/LicenseValidator.java`
- `app/src/main/resources/license-token.properties` - Embedded token (generated at build)
- `app/src/main/resources/license-public-key.pem` - Public key for verification
- `app/build.gradle` - `buildLicensed` task (line 128-166)

### Admin Panel
- `website/admin/index.html` - Licenses page (lines 525-570)
- `website/admin/js/admin.js` - License management functions (lines 810-1000)
- `website/admin/css/admin.css` - License UI styles (lines 1140-1270)

---

## Support

If you encounter issues not covered in this guide:

1. Check Cloudflare Workers logs for API errors
2. Check gateway console output for build errors
3. Check Minecraft server console for validation errors
4. Verify all secrets and keys are correctly configured

For questions, contact the ModereX development team.
