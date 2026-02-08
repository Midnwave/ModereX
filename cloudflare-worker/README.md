# ModereX License API - Cloudflare Worker

This Cloudflare Worker provides license validation for ModereX dev builds.

## Deployment

### Prerequisites
1. Install wrangler CLI: `npm install -g wrangler`
2. Login to Cloudflare: `wrangler login`

### Setup Steps

1. **Create KV Namespace:**
   ```bash
   wrangler kv:namespace create LICENSE_TOKENS
   wrangler kv:namespace create LICENSE_TOKENS --preview
   ```

   Update the `id` and `preview_id` in `wrangler.toml` with the output IDs.

2. **Generate RSA Key Pair:**
   ```bash
   # Generate private key
   openssl genrsa -out private_key.pem 2048

   # Extract public key
   openssl rsa -in private_key.pem -pubout -out public_key.pem
   ```

3. **Set Environment Secrets:**
   ```bash
   # Set private key (paste entire PEM including headers)
   wrangler secret put PRIVATE_KEY

   # Set admin secret (generate a UUID)
   wrangler secret put ADMIN_SECRET
   ```

4. **Deploy:**
   ```bash
   wrangler deploy
   ```

5. **Configure DNS:**
   - Add a DNS record in Cloudflare dashboard:
     - Type: `CNAME`
     - Name: `license`
     - Target: `moderex-license-api.workers.dev` (or your worker subdomain)
     - Proxy: Enabled (orange cloud)

## API Endpoints

### POST /validate
Validate a license token.

**Request:**
```json
{
  "token": "uuid-v4",
  "serverId": "server-id",
  "serverName": "My Server",
  "version": "2.0dev-288"
}
```

**Response (Success):**
```json
{
  "valid": true,
  "expiresAt": 1234567890,
  "features": {},
  "timestamp": 1234567890,
  "signature": "base64-rsa-signature"
}
```

**Response (Error):**
```json
{
  "valid": false,
  "error": "License revoked"
}
```

### POST /heartbeat
Update license heartbeat (called every 30 mins by plugin).

**Request:**
```json
{
  "token": "uuid-v4",
  "serverId": "server-id",
  "players": 42,
  "uptime": 3600000
}
```

**Response:**
```json
{
  "stillValid": true,
  "timestamp": 1234567890
}
```

### POST /admin/create
Create a new license token (requires `X-Admin-Secret` header).

**Request:**
```json
{
  "maxServers": 1,
  "expiresAt": 1234567890,
  "note": "Internal testing - John",
  "createdBy": "admin@example.com"
}
```

**Response:**
```json
{
  "success": true,
  "token": "generated-uuid-v4",
  "data": { ... }
}
```

### POST /admin/revoke
Revoke a license token (requires `X-Admin-Secret` header).

**Request:**
```json
{
  "token": "uuid-v4"
}
```

**Response:**
```json
{
  "success": true,
  "message": "License revoked"
}
```

### GET /admin/list
List all license tokens (requires `X-Admin-Secret` header).

**Response:**
```json
{
  "licenses": [
    {
      "token": "uuid-v4",
      "active": true,
      "createdAt": 1234567890,
      "createdBy": "admin@example.com",
      "expiresAt": null,
      "maxServers": 1,
      "note": "Testing",
      "lastValidated": 1234567890,
      "lastHeartbeat": 1234567890,
      "lastServerId": "server-id",
      "lastServerName": "My Server",
      "lastVersion": "2.0dev-288",
      "lastPlayers": 42,
      "lastUptime": 3600000
    }
  ]
}
```

## Testing

```bash
# Test validation (replace with actual token)
curl -X POST https://license.moderex.net/validate \
  -H "Content-Type: application/json" \
  -d '{"token":"test-token","serverId":"test-server","serverName":"Test","version":"2.0dev-288"}'

# Test admin create (replace with your ADMIN_SECRET)
curl -X POST https://license.moderex.net/admin/create \
  -H "Content-Type: application/json" \
  -H "X-Admin-Secret: your-secret-here" \
  -d '{"maxServers":1,"note":"Test license","createdBy":"admin@example.com"}'

# Test admin list
curl -X GET https://license.moderex.net/admin/list \
  -H "X-Admin-Secret: your-secret-here"
```

## Security Notes

- Private key is stored in Cloudflare's encrypted secrets storage
- Admin secret should be a strong UUID stored securely
- Rate limiting: 10 validation requests per minute per token
- All responses are signed with RSA-2048 to prevent tampering
- CORS enabled for gateway integration

## Public Key Distribution

The `public_key.pem` file must be embedded in the ModereX plugin JAR at:
`app/src/main/resources/license-public-key.pem`

This allows the plugin to verify response signatures from the API.
