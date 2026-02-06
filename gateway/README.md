# ModereX Gateway Server

Routes web panel traffic between browsers and Minecraft servers.

## Quick Start (Windows)

The easiest way to start the gateway on Windows is using the provided startup scripts:

### Method 1: Double-Click (Easiest)
1. Navigate to the project root: `C:\projects\Moderex\`
2. Double-click `start-gateway.bat`
3. Wait for the tunnel URL to appear
4. Copy the tunnel URL shown in the console

### Method 2: PowerShell Script
```powershell
cd C:\projects\Moderex
.\start-gateway.ps1
```

The script will:
- ✓ Check Node.js is installed
- ✓ Check cloudflared is installed
- ✓ Start the gateway server on port 3000
- ✓ Create a Cloudflare Quick Tunnel
- ✓ Display the public tunnel URL
- ✓ Keep both services running until you press Ctrl+C

**Example Output:**
```
============================================
  Gateway is Live!
============================================

Tunnel URL:
  https://availability-reasoning-reflects-surround.trycloudflare.com

WebSocket Endpoints:
  MC Servers: wss://availability-reasoning-reflects-surround.trycloudflare.com/server
  Panels:     wss://availability-reasoning-reflects-surround.trycloudflare.com/panel/{serverId}

Gateway Dashboard:
  http://localhost:3000/health

Press Ctrl+C to stop both services...
```

## Manual Quick Start

### Prerequisites
- Node.js 18+ installed ([download](https://nodejs.org))
- (Optional) Cloudflare account for tunnel

### 1. Install Dependencies

```bash
cd gateway
npm install
```

### 2. Run Gateway

```bash
npm start
```

Gateway runs on port 3000 by default.

### 3. Test It

Open browser: http://localhost:3000/health

You should see:
```json
{
  "status": "ok",
  "servers": 0,
  "clients": 0,
  "uptime": 5.123
}
```

## Architecture

```
Browser (Staff Panel)                    Minecraft Server
        │                                       │
        │ wss://gateway.moderex.net/panel/abc123
        │                                       │
        ▼                                       │
┌───────────────────────────────────────────────┴───────┐
│                   ModereX Gateway                      │
│                                                        │
│  ┌─────────────┐         ┌─────────────────────────┐  │
│  │ Browser     │◄───────►│  Server Registry        │  │
│  │ Connections │         │  Map<serverId, ws>      │  │
│  └─────────────┘         └─────────────────────────┘  │
│                                    ▲                   │
└────────────────────────────────────┼───────────────────┘
                                     │
                                     │ wss://gateway.moderex.net/server
                                     │
                              Minecraft Server
                              (ModereX Plugin)
```

## Endpoints

| Endpoint | Purpose |
|----------|---------|
| `GET /health` | Health check (JSON) |
| `GET /api/servers` | List connected servers |
| `GET /api/server/{id}` | Check if server exists |
| `WS /server` | MC server plugin connection |
| `WS /panel/{serverId}` | Browser panel connection |

## Protocol

### MC Server → Gateway

**Registration:**
```json
{
  "type": "register",
  "serverId": "abc12345",
  "serverName": "My Server",
  "version": "2.0.0",
  "players": 10
}
```

**Heartbeat (every 30s):**
```json
{
  "type": "heartbeat",
  "players": 15
}
```

**Response to browser:**
```json
{
  "type": "panel_response",
  "clientId": "client_abc123",
  "...": "response data"
}
```

**Broadcast to all browsers:**
```json
{
  "type": "broadcast",
  "data": { "...": "data to send to all connected browsers" }
}
```

### Browser → Gateway

Any message sent is forwarded to the MC server with `clientId` attached:
```json
{
  "type": "request_players",
  "clientId": "client_abc123"  // Added by gateway
}
```

### Gateway → Browser

**Connection successful:**
```json
{
  "type": "connected",
  "serverId": "abc12345",
  "serverName": "My Server"
}
```

**Server disconnected:**
```json
{
  "type": "server_disconnected",
  "message": "Minecraft server disconnected from gateway"
}
```

**Error:**
```json
{
  "type": "error",
  "code": "SERVER_NOT_FOUND",
  "message": "Server not connected to gateway"
}
```

## Configuration

Set via environment variables:

| Variable | Default | Description |
|----------|---------|-------------|
| `PORT` | 3000 | Server port |

Example:
```bash
PORT=8080 npm start
```

## Exposing to Internet

### Option A: Cloudflare Tunnel (Recommended)

1. Install cloudflared:
   - Windows: `winget install Cloudflare.cloudflared`
   - Mac: `brew install cloudflared`
   - Linux: Download from [releases](https://github.com/cloudflare/cloudflared/releases)

2. Login to Cloudflare:
   ```bash
   cloudflared tunnel login
   ```

3. Create tunnel:
   ```bash
   cloudflared tunnel create moderex-gateway
   ```

4. Create config file `~/.cloudflared/config.yml`:
   ```yaml
   tunnel: <your-tunnel-id>
   credentials-file: ~/.cloudflared/<your-tunnel-id>.json

   ingress:
     - hostname: gateway.moderex.net
       service: http://localhost:3000
     - hostname: panel.moderex.net
       service: http://localhost:3000
     - service: http_status:404
   ```

5. Run tunnel:
   ```bash
   cloudflared tunnel run moderex-gateway
   ```

6. Add DNS records in Cloudflare dashboard:
   - `gateway` → CNAME → `<tunnel-id>.cfargotunnel.com`
   - `panel` → CNAME → `<tunnel-id>.cfargotunnel.com`

### Option B: Port Forwarding

1. Forward port 3000 on your router
2. Access via `http://your-public-ip:3000`

Note: You'll also need SSL (Let's Encrypt) for production.

## Running as a Service

### Linux (systemd)

Create `/etc/systemd/system/moderex-gateway.service`:
```ini
[Unit]
Description=ModereX Gateway Server
After=network.target

[Service]
Type=simple
User=moderex
WorkingDirectory=/opt/moderex-gateway
ExecStart=/usr/bin/node gateway.js
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Enable and start:
```bash
sudo systemctl enable moderex-gateway
sudo systemctl start moderex-gateway
```

### Windows

Use [NSSM](https://nssm.cc/) or PM2:
```bash
npm install -g pm2
pm2 start gateway.js --name moderex-gateway
pm2 save
pm2 startup
```

### PM2 (Cross-platform)

```bash
npm install -g pm2
pm2 start gateway.js --name moderex-gateway
pm2 save
pm2 startup  # Follow instructions to enable auto-start
```

## Development

Run with auto-reload:
```bash
npm run dev
```

## Logs

Gateway logs to stdout. Key events:
- `[Server] abc12345 registered: My Server` - MC server connected
- `[Server] abc12345 disconnected` - MC server disconnected
- `[Browser] client_xyz connected to server abc12345` - Browser connected
- `[Browser] client_xyz disconnected` - Browser disconnected

## Troubleshooting

### "Server not found" error
- MC server hasn't connected to gateway yet
- Check MC server logs for gateway connection errors
- Verify server ID is correct (8 characters, case-insensitive)

### Frequent disconnections
- Check network stability
- Increase heartbeat timeout in gateway.js
- Check for firewall interference

### Can't connect from MC server
- Ensure gateway URL is correct in plugin config
- Check firewall allows outbound WebSocket connections
- Try connecting to gateway health endpoint first
