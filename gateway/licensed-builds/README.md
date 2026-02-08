# Licensed Builds Directory

This directory stores licensed dev builds of ModereX generated via the gateway build script.

## Usage

Generate a licensed build:
```bash
cd gateway
npm run build-licensed <license-token> [tester-name]
```

Example:
```bash
npm run build-licensed a1b2c3d4-e5f6-7g8h-9i0j-k1l2m3n4o5p6 "John Doe"
```

## Contents

Each licensed build includes:
- `ModereX-licensed-<short-token>.jar` - The licensed plugin JAR
- `ModereX-licensed-<short-token>.json` - Build metadata

## Distribution

Send the JAR file to the tester via secure channel.

**Important:**
- Do NOT share license tokens publicly
- Each JAR is watermarked with a unique token
- Tokens can be revoked via admin panel if leaked

## File Management

Old licensed builds can be safely deleted to save disk space.
Build metadata is also stored in the gateway database for tracking.
