#!/usr/bin/env python3
"""
ModereX Panel Preview Server

Serves the actual panel files from source with a mock WebSocket layer
so the panel can be previewed without a running Minecraft server.

Usage:
    python preview_panel.py [--port PORT]

All panel assets are loaded from ../app/src/main/resources/panel/ so
changes to the panel source are reflected immediately on refresh.
"""

import http.server
import os
import sys
import json
import argparse
import webbrowser
import threading
from pathlib import Path

PANEL_DIR = Path(__file__).resolve().parent.parent / "app" / "src" / "main" / "resources" / "panel"

MIME_TYPES = {
    ".html": "text/html; charset=utf-8",
    ".css": "text/css; charset=utf-8",
    ".js": "application/javascript; charset=utf-8",
    ".json": "application/json; charset=utf-8",
    ".png": "image/png",
    ".jpg": "image/jpeg",
    ".jpeg": "image/jpeg",
    ".gif": "image/gif",
    ".svg": "image/svg+xml",
    ".ico": "image/x-icon",
    ".woff": "font/woff",
    ".woff2": "font/woff2",
    ".ttf": "font/ttf",
    ".mp3": "audio/mpeg",
    ".ogg": "audio/ogg",
    ".wav": "audio/wav",
}

# The mock WebSocket script that gets injected into index.html.
# This overrides the WebSocket class so the panel "connects" to a fake server.
MOCK_SCRIPT = r"""
<script>
(function() {
  'use strict';

  // ---- Mock Data ----

  const SERVER_NAME = 'TEST';
  const SERVER_VERSION = '2.0.0';
  const MC_VERSION = '1.21.4';

  const MOCK_PLAYERS = [
    {
      uuid: '00000000-0000-0000-0000-000000000001',
      name: 'TestAccount1',
      displayName: 'TestAccount1',
      ip: '127.0.0.1',
      platform: 'JAVA',
      isBedrock: false,
      isVanished: false,
      world: 'world',
      joinedAt: Date.now() - 3600000,
      ping: 42,
      punishments: { bans: 0, mutes: 0, warns: 1, kicks: 0 },
      isWatched: false,
      version: '1.21.4',
      rank: 'Member',
      prefix: '',
      suffix: ''
    },
    {
      uuid: '00000000-0000-0000-0000-000000000002',
      name: '_TestAccount2',
      displayName: '_TestAccount2',
      ip: '127.0.0.2',
      platform: 'BEDROCK',
      isBedrock: true,
      isVanished: false,
      world: 'world',
      joinedAt: Date.now() - 1800000,
      ping: 78,
      punishments: { bans: 0, mutes: 1, warns: 0, kicks: 0 },
      isWatched: true,
      watchReason: 'Suspicious behavior',
      version: '1.21.40',
      rank: 'Member',
      prefix: '',
      suffix: '',
      floodgatePrefix: '.'
    }
  ];

  const MOCK_PUNISHMENTS = [
    {
      caseId: 1,
      playerUuid: '00000000-0000-0000-0000-000000000001',
      playerName: 'TestAccount1',
      type: 'WARN',
      reason: 'Mild toxicity in chat',
      staff: 'Console',
      staffUuid: '00000000-0000-0000-0000-000000000000',
      timestamp: Date.now() - 86400000,
      active: true,
      duration: -1,
      expiry: -1,
      server: SERVER_NAME
    },
    {
      caseId: 2,
      playerUuid: '00000000-0000-0000-0000-000000000002',
      playerName: '_TestAccount2',
      type: 'MUTE',
      reason: 'Spamming in chat',
      staff: 'TestAccount1',
      staffUuid: '00000000-0000-0000-0000-000000000001',
      timestamp: Date.now() - 43200000,
      active: true,
      duration: 3600000,
      expiry: Date.now() + 3600000,
      server: SERVER_NAME
    },
    {
      caseId: 3,
      playerUuid: 'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
      playerName: 'OldPlayer99',
      type: 'BAN',
      reason: 'Hacking - KillAura detected',
      staff: 'Console',
      staffUuid: '00000000-0000-0000-0000-000000000000',
      timestamp: Date.now() - 604800000,
      active: true,
      duration: -1,
      expiry: -1,
      server: SERVER_NAME
    },
    {
      caseId: 4,
      playerUuid: 'deadbeef-dead-beef-dead-beefdeadbeef',
      playerName: 'Griefer_X',
      type: 'IPBAN',
      reason: 'Repeated grief + ban evasion',
      staff: 'TestAccount1',
      staffUuid: '00000000-0000-0000-0000-000000000001',
      timestamp: Date.now() - 259200000,
      active: true,
      duration: -1,
      expiry: -1,
      ip: '192.168.1.100',
      server: SERVER_NAME
    },
    {
      caseId: 5,
      playerUuid: 'cafebabe-cafe-babe-cafe-babecafebabe',
      playerName: 'ToxicChatter',
      type: 'KICK',
      reason: 'AFK too long',
      staff: 'Console',
      staffUuid: '00000000-0000-0000-0000-000000000000',
      timestamp: Date.now() - 172800000,
      active: false,
      duration: 0,
      expiry: -1,
      server: SERVER_NAME
    }
  ];

  const MOCK_AUTOMOD_RULES = [
    {
      id: 'spam_filter',
      name: 'Spam Filter',
      description: 'Filters rapid message spam and repeated messages',
      enabled: true,
      builtin: true,
      locked: false,
      conditions: ['RAPID_MESSAGES', 'REPEATED_MESSAGE'],
      action: 'MUTE',
      actionDuration: '5m',
      threshold: 5,
      thresholdWindow: 10,
      message: 'You have been muted for spamming.'
    },
    {
      id: 'ad_filter',
      name: 'Advertising Filter',
      description: 'Blocks server advertisements and IPs in chat',
      enabled: true,
      builtin: true,
      locked: false,
      conditions: ['CONTAINS_IP', 'CONTAINS_URL'],
      action: 'WARN',
      actionDuration: '',
      threshold: 1,
      thresholdWindow: 0,
      message: 'Advertising is not allowed.'
    },
    {
      id: 'custom_caps',
      name: 'Caps Lock Filter',
      description: 'Prevents excessive use of capital letters',
      enabled: false,
      builtin: false,
      locked: false,
      conditions: ['EXCESSIVE_CAPS'],
      action: 'CANCEL',
      actionDuration: '',
      threshold: 1,
      thresholdWindow: 0,
      message: 'Please reduce the amount of capital letters.'
    }
  ];

  const MOCK_TEMPLATES = [
    { id: 'spam', name: 'Spam', reason: 'Chat spam', type: 'MUTE', duration: '10m' },
    { id: 'advertising', name: 'Advertising', reason: 'Server advertising', type: 'BAN', duration: '7d' },
    { id: 'hacking', name: 'Hacking', reason: 'Use of illegal modifications', type: 'BAN', duration: 'permanent' },
    { id: 'toxicity', name: 'Toxicity', reason: 'Toxic behavior', type: 'WARN', duration: '' },
    { id: 'grief', name: 'Griefing', reason: 'Intentional destruction of builds', type: 'BAN', duration: '30d' }
  ];

  const MOCK_WATCHLIST = [
    {
      uuid: '00000000-0000-0000-0000-000000000002',
      name: '_TestAccount2',
      reason: 'Suspicious behavior',
      addedBy: 'Console',
      addedAt: Date.now() - 7200000,
      online: true
    }
  ];

  const MOCK_SETTINGS = {
    serverName: SERVER_NAME,
    version: SERVER_VERSION,
    mcVersion: MC_VERSION,
    chatEnabled: true,
    slowmodeSeconds: 0,
    language: 'en',
    timezone: 'UTC',
    debug: false,
    maxPlayers: 100,
    onlineMode: true,
    geyserEnabled: true
  };

  const MOCK_USER_SETTINGS = {
    chatAlerts: true,
    soundEnabled: true,
    watchlistToasts: true,
    staffChatNotifications: true,
    punishmentAlerts: true,
    automodAlerts: true,
    anticheatAlerts: true,
    compactMode: false,
    theme: 'default',
    themeColor: '#2d7aed',
    backgroundPattern: 'aurora',
    watchlistAlerts: true,
    deviceTrustEnabled: false,
    permissions: [
      'moderex.ban', 'moderex.mute', 'moderex.kick', 'moderex.warn',
      'moderex.history', 'moderex.watchlist', 'moderex.automod',
      'moderex.webpanel', 'moderex.staffchat', 'moderex.anticheat',
      'moderex.admin', 'moderex.templates', 'moderex.actions',
      'moderex.settings', 'moderex.ipban', 'moderex.unban',
      'moderex.unmute'
    ],
    // Alert level defaults
    banAlerts: 'EVERYONE',
    kickAlerts: 'EVERYONE',
    muteAlerts: 'EVERYONE',
    warnAlerts: 'EVERYONE',
    pardonAlerts: 'EVERYONE',
    automodAlerts: 'EVERYONE',
    anticheatAlerts: 'EVERYONE',
    nicknameAlerts: 'EVERYONE',
    commandAlerts: 'BLACKLISTED_ONLY',
    joinLeaveAlerts: 'EVERYONE',
    lagAlerts: true,
    // Web notification modes
    webNotifyPunishments: 'toast',
    webNotifyAutomod: 'toast',
    webNotifyAnticheat: 'toast',
    webNotifyWatchlist: 'toast',
    webNotifyStaffChat: 'toast',
    webNotifyCommands: 'toast',
    webNotifyNickname: 'toast',
    webNotifyLag: 'toast',
    webToastPosition: 'TOP_RIGHT',
    webAlertDurationSeconds: 10,
    // Sound settings
    webSoundPunishments: true,
    webSoundAutomod: true,
    webSoundAnticheat: true,
    webSoundWatchlist: true,
    webSoundStaffChat: true,
    webSoundCommands: true,
    webSoundNickname: true,
    webSoundLag: true,
    alertRateLimitSeconds: 5,
    alertRateLimitMax: 3
  };


  // ---- Mock WebSocket ----

  const _RealWebSocket = window.WebSocket;
  let mockInstance = null;

  class MockWebSocket {
    constructor(url) {
      this.url = url;
      this.readyState = 0; // CONNECTING
      this.onopen = null;
      this.onmessage = null;
      this.onclose = null;
      this.onerror = null;
      mockInstance = this;

      // Simulate connection after a brief delay
      setTimeout(() => {
        this.readyState = 1; // OPEN
        if (this.onopen) this.onopen({ type: 'open' });
      }, 100);
    }

    send(raw) {
      let msg;
      try { msg = JSON.parse(raw); } catch { return; }
      const type = msg.type;
      const data = msg.data || {};

      // Handle requests with simulated delay
      setTimeout(() => this._handleMessage(type, data), 50 + Math.random() * 100);
    }

    close() {
      this.readyState = 3;
      if (this.onclose) this.onclose({ code: 1000, reason: 'Normal' });
    }

    _reply(type, data) {
      if (this.readyState !== 1) return;
      const payload = JSON.stringify({ type, data });
      if (this.onmessage) {
        this.onmessage({ data: payload });
      }
    }

    _handleMessage(type, data) {
      switch (type) {
        case 'AUTH_TOKEN':
        case 'AUTH_CODE':
        case 'AUTH_SESSION':
        case 'AUTH_TRUSTED_DEVICE':
        case 'AUTH_CONSOLE':
          this._reply('AUTH_SUCCESS', {
            playerName: 'TestAccount1',
            playerUuid: '00000000-0000-0000-0000-000000000001',
            username: 'TestAccount1',
            uuid: '00000000-0000-0000-0000-000000000001',
            authMethod: 'TOKEN',
            sessionId: 'preview-session-' + Date.now(),
            token: 'preview-token-mock',
            permanentToken: 'preview-token-mock',
            platform: 'JAVA',
            isBedrock: false,
            rank: 'Admin',
            prefix: '\u00a7c[Admin] ',
            suffix: '',
            deviceTrustEnabled: false
          });
          break;

        case 'HEARTBEAT':
          this._reply('HEARTBEAT', {});
          break;

        case 'PING':
          this._reply('PONG', { timestamp: data.timestamp || Date.now() });
          break;

        case 'GET_DATA':
        case 'GET_PLAYERS':
          this._reply('PLAYERS_DATA', {
            players: MOCK_PLAYERS,
            serverName: SERVER_NAME,
            maxPlayers: 100,
            tps: 19.98,
            mspt: 2.3,
            uptime: 86400000,
            version: MC_VERSION
          });
          break;

        case 'GET_STATS':
          this._reply('SERVER_STATUS', {
            online: MOCK_PLAYERS.length,
            max: 100,
            tps: 19.98,
            mspt: 2.3,
            vanished: 0,
            watchlistCount: 1,
            uptime: 86400000,
            version: MC_VERSION,
            serverName: SERVER_NAME,
            motd: 'A ModereX Test Server',
            memoryUsed: 1024,
            memoryMax: 4096
          });
          break;

        case 'GET_PUNISHMENTS':
        case 'GET_MODLOG':
          this._reply('PUNISHMENTS_DATA', {
            punishments: MOCK_PUNISHMENTS,
            total: MOCK_PUNISHMENTS.length,
            page: data.page || 1,
            totalPages: 1
          });
          break;

        case 'GET_PLAYER_DETAILS': {
          const player = MOCK_PLAYERS.find(p => p.uuid === data.uuid);
          if (player) {
            this._reply('PLAYER_DETAILS', {
              ...player,
              firstJoin: Date.now() - 86400000 * 30,
              lastSeen: Date.now(),
              playtime: 360000000,
              allPunishments: MOCK_PUNISHMENTS.filter(p => p.playerUuid === player.uuid),
              notes: [],
              country: 'United States',
              city: 'New York'
            });
          } else {
            this._reply('ERROR', { code: 'PLAYER_NOT_FOUND', message: 'Player not found' });
          }
          break;
        }

        case 'GET_WATCHLIST':
          this._reply('WATCHLIST_DATA', { watchlist: MOCK_WATCHLIST });
          break;

        case 'GET_AUTOMOD_RULES':
          this._reply('AUTOMOD_RULES_DATA', { rules: MOCK_AUTOMOD_RULES });
          break;

        case 'GET_TEMPLATES':
          this._reply('TEMPLATES_DATA', { templates: MOCK_TEMPLATES });
          break;

        case 'GET_USER_SETTINGS':
          this._reply('USER_SETTINGS_DATA', MOCK_USER_SETTINGS);
          break;

        case 'GET_SETTINGS':
        case 'GET_SERVER_SETTINGS':
          this._reply('SERVER_SETTINGS', MOCK_SETTINGS);
          break;

        case 'GET_MESSAGES':
          this._reply('MESSAGES_DATA', { messages: {} });
          break;

        case 'GET_LOGS':
          this._reply('LOGS_DATA', { logs: [], total: 0 });
          break;

        case 'GET_ANTICHEAT_CONFIG':
        case 'GET_ANTICHEAT_INFO':
          this._reply('ANTICHEAT_INFO', {
            enabled: false,
            detected: [],
            alertsEnabled: false
          });
          break;

        case 'GET_ANTICHEAT_ALERTS':
          this._reply('ANTICHEAT_ALERTS', { checks: {} });
          break;

        case 'GET_STAFF_ALERT_PREFS':
          this._reply('STAFF_ALERT_PREFS', { prefs: {} });
          break;

        case 'GET_ALERT_PRESETS':
          this._reply('ALERT_PRESETS', { presets: [] });
          break;

        case 'GET_LUCKPERMS_STATUS':
          this._reply('LUCKPERMS_STATUS', { available: false });
          break;

        case 'GET_MODERATION_PLUGINS':
          this._reply('MODERATION_PLUGINS', { plugins: [] });
          break;

        case 'GET_GEYSER_STATUS':
          this._reply('GEYSER_STATUS', { enabled: true, prefix: '.' });
          break;

        case 'GET_COMMAND_HISTORY':
          this._reply('COMMAND_HISTORY_DATA', { commands: [], total: 0 });
          break;

        case 'GET_AUTOMOD_LOGS':
          this._reply('AUTOMOD_LOGS_DATA', { logs: [], total: 0 });
          break;

        case 'GET_DEV_CHECKLIST':
          this._reply('DEV_CHECKLIST_DATA', { items: [] });
          break;

        case 'UPDATE_USER_SETTINGS':
          // Merge into mock settings
          Object.assign(MOCK_USER_SETTINGS, data);
          this._reply('SUCCESS', { message: 'User settings saved' });
          break;

        case 'CREATE_PUNISHMENT':
          this._reply('SUCCESS', { message: 'Punishment created (preview mode)' });
          this._reply('PUNISHMENT_CREATED', {
            caseId: MOCK_PUNISHMENTS.length + 1,
            playerName: data.playerName || 'Unknown',
            playerUuid: data.playerUuid || 'unknown',
            type: data.type || 'WARN',
            reason: data.reason || 'No reason',
            staff: 'TestAccount1',
            staffUuid: '00000000-0000-0000-0000-000000000001',
            timestamp: Date.now(),
            active: true,
            duration: -1,
            server: SERVER_NAME
          });
          break;

        case 'SEND_STAFFCHAT':
          this._reply('STAFFCHAT_MESSAGE', {
            sender: 'TestAccount1',
            senderUuid: '00000000-0000-0000-0000-000000000001',
            message: data.message || '',
            timestamp: Date.now(),
            fromPanel: true
          });
          break;

        case 'EXECUTE_COMMAND':
          this._reply('SUCCESS', { message: 'Command executed (preview mode)' });
          break;

        default:
          // Generic success for unknown commands
          this._reply('SUCCESS', { message: 'OK (preview mode)' });
          break;
      }
    }
  }

  // Replace global WebSocket
  MockWebSocket.CONNECTING = 0;
  MockWebSocket.OPEN = 1;
  MockWebSocket.CLOSING = 2;
  MockWebSocket.CLOSED = 3;
  window.WebSocket = MockWebSocket;

  // Periodic fake events
  let eventInterval = null;
  function startFakeEvents() {
    const messages = [
      'Has anyone seen diamonds near spawn?',
      'Good morning everyone!',
      'Can someone help me with this build?',
      'The server is running great today',
      'Anyone want to trade?'
    ];
    let msgIndex = 0;

    eventInterval = setInterval(() => {
      if (!mockInstance || mockInstance.readyState !== 1) return;

      // Rotate through periodic fake broadcasts
      const rand = Math.random();
      if (rand < 0.3) {
        // Staff chat message
        mockInstance._reply('STAFFCHAT_MESSAGE', {
          sender: '_TestAccount2',
          senderUuid: '00000000-0000-0000-0000-000000000002',
          message: messages[msgIndex % messages.length],
          timestamp: Date.now(),
          fromPanel: false
        });
        msgIndex++;
      }
    }, 30000); // Every 30 seconds
  }

  // Start fake events after the page loads
  setTimeout(startFakeEvents, 3000);

  console.log('%c[ModereX Preview] Mock WebSocket layer active', 'color: #f59e0b; font-weight: bold;');
  console.log('%c[ModereX Preview] Server: ' + SERVER_NAME + ' | Players: TestAccount1 (Java), _TestAccount2 (Bedrock)', 'color: #86868b;');
})();
</script>
"""


class PreviewHandler(http.server.BaseHTTPRequestHandler):
    """HTTP handler that serves panel files with mock injection."""

    def log_message(self, fmt, *args):
        # Quieter logging
        if args and isinstance(args[0], str) and args[0].startswith("GET /js/") or \
           args and isinstance(args[0], str) and args[0].startswith("GET /css/"):
            return
        super().log_message(fmt, *args)

    def do_GET(self):
        path = self.path.split("?")[0]  # Strip query string

        # /api/config - return mock config pointing to our mock WS
        if path == "/api/config":
            config = {
                "host": "localhost",
                "wsPort": 0,  # Not used - WebSocket is mocked
                "serverName": "TEST",
                "serverVersion": "2.0.0",
                "aiEnabled": False,
            }
            body = json.dumps(config).encode("utf-8")
            self.send_response(200)
            self.send_header("Content-Type", "application/json; charset=UTF-8")
            self.send_header("Access-Control-Allow-Origin", "*")
            self.send_header("Content-Length", str(len(body)))
            self.end_headers()
            self.wfile.write(body)
            return

        # Resolve to panel directory
        if path == "/" or path == "":
            path = "/index.html"

        # Security: prevent path traversal
        if ".." in path:
            self.send_error(403, "Forbidden")
            return

        file_path = PANEL_DIR / path.lstrip("/")

        if not file_path.is_file():
            self.send_error(404, f"Not Found: {path}")
            return

        ext = file_path.suffix.lower()
        content_type = MIME_TYPES.get(ext, "application/octet-stream")

        try:
            data = file_path.read_bytes()

            # Inject mock script into index.html before the first <script> tag
            if path == "/index.html":
                html = data.decode("utf-8")
                # Insert mock script right before the first JS script tag
                # so it overrides WebSocket before any panel scripts run
                inject_point = html.find('<script src="js/')
                if inject_point == -1:
                    inject_point = html.find("<script")
                if inject_point != -1:
                    html = html[:inject_point] + MOCK_SCRIPT + "\n" + html[inject_point:]
                data = html.encode("utf-8")

            self.send_response(200)
            self.send_header("Content-Type", content_type)
            self.send_header("Access-Control-Allow-Origin", "*")
            self.send_header("Content-Length", str(len(data)))
            self.send_header("Cache-Control", "no-cache")
            self.end_headers()
            self.wfile.write(data)

        except Exception as e:
            self.send_error(500, f"Internal Server Error: {e}")

    def do_OPTIONS(self):
        self.send_response(204)
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Access-Control-Allow-Methods", "GET, OPTIONS")
        self.send_header("Access-Control-Allow-Headers", "Content-Type")
        self.end_headers()


def main():
    parser = argparse.ArgumentParser(description="ModereX Panel Preview Server")
    parser.add_argument("--port", type=int, default=8090, help="Port to serve on (default: 8090)")
    parser.add_argument("--no-open", action="store_true", help="Don't auto-open browser")
    args = parser.parse_args()

    if not PANEL_DIR.is_dir():
        print(f"Error: Panel directory not found at {PANEL_DIR}")
        print("Make sure you're running this from the tools/ directory.")
        sys.exit(1)

    server = http.server.HTTPServer(("0.0.0.0", args.port), PreviewHandler)
    url = f"http://localhost:{args.port}"

    print(f"ModereX Panel Preview Server")
    print(f"  Panel source: {PANEL_DIR}")
    print(f"  Serving at:   {url}")
    print(f"  Server name:  TEST")
    print(f"  Players:      TestAccount1 (Java), _TestAccount2 (Bedrock)")
    print(f"  Press Ctrl+C to stop\n")

    if not args.no_open:
        threading.Timer(0.5, lambda: webbrowser.open(url)).start()

    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("\nShutting down.")
        server.shutdown()


if __name__ == "__main__":
    main()
