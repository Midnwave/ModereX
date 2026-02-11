# ModereX Dev Build - Tester Guide

Welcome to the ModereX dev build testing program! This guide covers everything you need to install, configure, and use the plugin.

**Current Version:** 2.0dev
**Requires:** Paper/Spigot 1.21+
**Java:** 21+

---

## Table of Contents

- [Installation](#installation)
- [Configuration](#configuration)
- [Getting Started](#getting-started)
- [Commands](#commands)
- [Permissions](#permissions)
- [Web Panel](#web-panel)
- [Features](#features)
- [Reporting Issues](#reporting-issues)

---

## Installation

1. Drop `ModereX-licensed-XXXXXXXX.jar` into your server's `plugins/` folder
2. Start (or restart) the server
3. ModereX will generate its config at `plugins/ModereX/config.yml`
4. Configure to your liking (see below), then `/mx reload`

**Dependencies (optional but recommended):**
- [LuckPerms](https://luckperms.net/) - For rank display, permission management, and web panel rank sync
- An anticheat plugin (Grim, Vulcan, Matrix, etc.) - ModereX auto-detects and integrates

---

## Configuration

The main config is at `plugins/ModereX/config.yml`. Key sections:

### General
```yaml
general:
  language: en_US          # en_US, es_ES, de_DE, fr_FR, pt_BR, zh_CN
  timezone: America/Chicago # Your timezone
  debug: false
```

### Web Panel
```yaml
webpanel:
  enabled: true            # Enable the staff web panel
  port: 8080               # Port for HTTP + WebSocket
  host: ''                 # Public IP (leave empty to auto-detect)
  server-name: 'My Server' # Name shown in the panel
```

### Gateway (Easy Panel Access)
```yaml
gateway:
  enabled: true            # Connects to gateway.moderex.net
  # When enabled, your panel is accessible at: panel.moderex.net/{server-id}/
  # No port forwarding or SSL setup required!
```

### Database
```yaml
database:
  type: sqlite             # sqlite or mysql
  mysql:
    host: localhost
    port: 3306
    database: moderex
    username: root
    password: ''
```

### Anticheat Integration
```yaml
anticheat:
  auto-detect: true        # Automatically hooks into installed anticheats
  provider: auto           # auto, grim, vulcan, matrix, spartan, ncp, themis, foxaddition, lightac
  rebrand-alerts: true     # Show ModereX-styled alerts instead of native ones
  alerts:
    enabled: true
    min-vl: 5              # Minimum violation level before alerting
    cooldown: 5            # Seconds between alerts for same player
```

### Warning Escalation
```yaml
warnings:
  default-expiry: 30d
  auto-punishments:
    3:                     # After 3 warnings in 7 days...
      within: 7d
      action: mute
      duration: 1h
    5:
      within: 14d
      action: mute
      duration: 1d
    7:
      within: 30d
      action: ban
      duration: 7d
    10:                    # 10 warnings ever = permban
      within: 0
      action: ban
      duration: permanent
```

### Vanish
```yaml
vanish:
  hide-from-tablist: true
  silent-containers: true
  no-footsteps: true
  save-vanish-state: true  # Persist across restarts
  use-packet-level: true   # NMS-level hiding (most thorough)
  levels:
    enabled: true          # Level-based visibility hierarchy
```

### Replay System
```yaml
replay:
  enabled: true
  record-on-anticheat: true   # Auto-record on AC alerts
  record-watchlist: true       # Auto-record watchlisted players
  max-duration-seconds: 300
  max-stored: 1000
```

---

## Getting Started

### First-Time Setup

1. **Grant yourself permissions:** If you're OP, you already have all permissions (except `moderex.webpanel` which must be explicitly granted)
2. **Set up the web panel:**
   - Run `/mx connect` for a quick 30-minute panel link
   - Or `/mx gettoken` for a permanent access token
3. **Test basic commands:** `/ban`, `/mute`, `/warn`, `/check`, `/vanish`

### Connecting to the Web Panel

**Option A: Via Gateway (Recommended)**
- Ensure `gateway.enabled: true` in config
- The panel URL will be: `panel.moderex.net/{your-server-id}/`
- Your server ID is logged on startup: `[ModereX] [Identity] Server ID: abc1def2`

**Option B: Direct Access**
- Ensure `webpanel.enabled: true` and port 8080 is accessible
- Visit `http://your-server-ip:8080`
- Authenticate with your token from `/mx gettoken`

---

## Commands

### Punishment Commands

| Command | Permission | Description |
|---------|-----------|-------------|
| `/ban <player> [duration] [reason]` | `moderex.ban` | Ban a player (no duration = permanent) |
| `/tempban <player> <duration> [reason]` | `moderex.tempban` | Temporary ban (duration required) |
| `/mute <player> [duration] [reason]` | `moderex.mute` | Mute a player |
| `/tempmute <player> <duration> [reason]` | `moderex.tempmute` | Temporary mute |
| `/warn <player> [duration] [reason]` | `moderex.warn` | Warn a player |
| `/kick <player> [reason]` | `moderex.kick` | Kick a player |
| `/ipban <player> [duration] [reason]` | `moderex.ipban` | IP ban (affects all accounts on that IP) |
| `/ipmute <player> [duration] [reason]` | `moderex.ipmute` | IP mute |
| `/punish [player]` | `moderex.punish` | Open punishment GUI |

### Unpunishment Commands

| Command | Permission | Description |
|---------|-----------|-------------|
| `/unban <player\|caseId> [reason]` | `moderex.unban` | Remove a ban |
| `/unmute <player\|caseId> [reason]` | `moderex.unmute` | Remove a mute |
| `/unwarn <player\|caseId> [reason]` | `moderex.unwarn` | Remove a warning |
| `/clearwarnings <player>` | `moderex.clearwarnings` | Clear all warnings |

### Check & History Commands

| Command | Permission | Description |
|---------|-----------|-------------|
| `/check <player>` | `moderex.check` | View comprehensive player info panel |
| `/checkban <player\|caseId>` | `moderex.checkban` | Check if player is banned |
| `/checkmute <player\|caseId>` | `moderex.checkmute` | Check if player is muted |
| `/checkwarn <player\|caseId>` | `moderex.checkwarn` | Check active warnings |
| `/history <player> [type]` | `moderex.history` | View punishment history |
| `/staffhistory <staff> [type]` | `moderex.staffhistory` | View staff's issued punishments |
| `/viewpunishment <caseId>` | `moderex.command.viewpunishment` | View punishment details by case ID |
| `/modlog <player> [filter]` | `moderex.modlog` | View moderation log |

### IP & Account Commands

| Command | Permission | Description |
|---------|-----------|-------------|
| `/dupeip <player\|ip>` | `moderex.dupeip` | Find alt accounts sharing IPs |
| `/iphistory <player>` | `moderex.iphistory` | View IP history |
| `/ipreport` | `moderex.ipreport` | Show duplicate IPs among online players |
| `/geoip <player>` | `moderex.geoip` | View player country/region |
| `/lastuuid <player>` | `moderex.lastuuid` | Display player UUID |
| `/namehistory <player>` | `moderex.namehistory` | View previous usernames |

### Staff Tools

| Command | Permission | Description |
|---------|-----------|-------------|
| `/staffchat <message>` (or `/sc`) | `moderex.staffchat` | Staff-only chat channel |
| `/vanish` (or `/v`) | `moderex.vanish` | Toggle vanish mode |
| `/watchlist [add\|remove\|list\|note] [player]` | `moderex.command.watchlist` | Manage suspicious players |
| `/seen <player>` | `moderex.command.seen` | View when player was last online |
| `/log <player> [action:type] [time:duration]` | `moderex.log` | View activity logs |
| `/cmdblacklist <player> <command> [duration]` | `moderex.cmdblacklist` | Block commands for a player |
| `/disguise [name] [rank]` | `moderex.disguise` | Disguise as another player |
| `/rules` | `moderex.rules` | View server rules |

### Admin Commands

| Command | Permission | Description |
|---------|-----------|-------------|
| `/mx reload` | `moderex.reload` | Reload configuration |
| `/mx connect` | `moderex.webpanel` | Get quick 30-min web panel link |
| `/mx gettoken` | `moderex.webpanel` | Generate permanent panel token |
| `/mx replay [start\|stop\|play\|list]` | `moderex.command.admin` | Replay system |
| `/mx chat [enable\|disable\|slowmode\|clear]` | `moderex.admin` | Chat management |
| `/mx automod` | `moderex.admin` | Open automod configuration GUI |
| `/mx settings` | `moderex.admin` | Open staff settings GUI |
| `/mx broadcast <message>` | `moderex.broadcast` | Network-wide broadcast |
| `/lockdown [local\|global] [end]` | `moderex.lockdown` | Server lockdown mode |
| `/staffrollback <staff> [duration]` | `moderex.staffrollback` | Rollback a staff member's punishments |
| `/prunehistory <player> [duration]` | `moderex.prunehistory` | Remove old punishment history |

### Command Flags

Most punishment commands support these flags:

| Flag | Permission | Description |
|------|-----------|-------------|
| `-s` | `moderex.notify.silent` | Silent (staff-only notification) |
| `-S` | `moderex.extrasilent` | Extra-silent (no notifications at all) |
| `-g` | `moderex.server.global` | Global (all servers on network) |
| `-p` | `moderex.public` | Override silent defaults, make public |
| `-d` | `moderex.delete` | Delete punishment from history entirely |
| `-m` | `moderex.modify` | Modify an existing punishment |

### Duration Format

| Unit | Example | Meaning |
|------|---------|---------|
| `s` | `30s` | 30 seconds |
| `m` | `10m` | 10 minutes |
| `h` | `2h` | 2 hours |
| `d` | `7d` | 7 days |
| `w` | `2w` | 2 weeks |
| `mo` | `1mo` | 1 month |
| `y` | `1y` | 1 year |

**Combined:** `1mo3d12h` = 1 month, 3 days, 12 hours
**Permanent:** Use `perm`, `permanent`, or omit duration

---

## Permissions

### Quick Setup (LuckPerms)

**Helper/Trial Mod:**
```
/lp group helper permission set moderex.warn true
/lp group helper permission set moderex.kick true
/lp group helper permission set moderex.check true
/lp group helper permission set moderex.history true
/lp group helper permission set moderex.staffchat true
/lp group helper permission set moderex.notify.punishments true
/lp group helper permission set moderex.notify.staffchat true
```

**Moderator:**
```
/lp group moderator permission set moderex.ban true
/lp group moderator permission set moderex.mute true
/lp group moderator permission set moderex.warn true
/lp group moderator permission set moderex.kick true
/lp group moderator permission set moderex.tempmute true
/lp group moderator permission set moderex.tempban true
/lp group moderator permission set moderex.unban true
/lp group moderator permission set moderex.unmute true
/lp group moderator permission set moderex.unwarn true
/lp group moderator permission set moderex.check true
/lp group moderator permission set moderex.check.ip true
/lp group moderator permission set moderex.history true
/lp group moderator permission set moderex.dupeip true
/lp group moderator permission set moderex.vanish true
/lp group moderator permission set moderex.staffchat true
/lp group moderator permission set moderex.command.watchlist true
/lp group moderator permission set moderex.notify.* true
/lp group moderator permission set moderex.webpanel true
```

**Senior Moderator:**
```
/lp group seniormod permission set moderex.command.* true
/lp group seniormod permission set moderex.notify.* true
/lp group seniormod permission set moderex.vanish.* true
/lp group seniormod permission set moderex.webpanel true
```

**Administrator:**
```
/lp group admin permission set moderex.* true
```

### Wildcard Permissions

| Permission | Description |
|------------|-------------|
| `moderex.*` | Everything |
| `moderex.command.*` | All commands |
| `moderex.notify.*` | All staff notifications |
| `moderex.bypass.*` | All bypass permissions |
| `moderex.vanish.*` | All vanish features |
| `moderex.alerts.*` | All alert types |
| `moderex.admin.*` | All admin config sections |

### Important Notes

- **OP** gets all permissions **except** `moderex.webpanel` (must be explicitly granted)
- **Negative permissions** work: `-moderex.admin` denies admin access even with `moderex.*`
- Wildcards cascade: `moderex.command.*` grants `moderex.ban`, `moderex.mute`, etc.

### Bypass Permissions

| Permission | Description |
|------------|-------------|
| `moderex.bypass.mute` | Chat while muted |
| `moderex.bypass.slowmode` | Bypass chat slowmode |
| `moderex.bypass.chatdisable` | Chat when globally disabled |
| `moderex.bypass.automod` | Bypass all automod filters |

### Notification Permissions

| Permission | Description |
|------------|-------------|
| `moderex.notify.punishments` | See punishment broadcasts |
| `moderex.notify.automod` | See automod trigger alerts |
| `moderex.notify.anticheat` | See anticheat violation alerts |
| `moderex.notify.staffchat` | Access staff chat messages |
| `moderex.notify.silent` | See silent punishment notifications |

### Vanish Permissions

| Permission | Description |
|------------|-------------|
| `moderex.vanish` | Toggle vanish |
| `moderex.vanish.others` | Vanish other players |
| `moderex.vanish.level.<n>` | Vanish at level N |
| `moderex.vanish.see.level.<n>` | See vanished players up to level N |
| `moderex.vanish.pickup` | Pick up items while vanished |
| `moderex.vanish.chat` | Chat while vanished |
| `moderex.vanish.place` | Place blocks while vanished |
| `moderex.vanish.break` | Break blocks while vanished |
| `moderex.vanish.attack` | Attack entities while vanished |
| `moderex.vanish.flight` | Auto-enable flight when vanishing |
| `moderex.vanish.spectator` | Double-sneak to toggle spectator mode |

### Web Panel Config Permissions

These control which config sections staff can edit in the web panel:

| Permission | Config Section |
|------------|---------------|
| `moderex.admin.warnings` | Warning settings |
| `moderex.admin.mutes` | Mute settings |
| `moderex.admin.lockdown` | Server lockdown |
| `moderex.admin.notifications` | Notification config |
| `moderex.admin.activitylog` | Activity log config |
| `moderex.admin.evidence` | Evidence config |
| `moderex.admin.commandblacklist` | Command blacklist |
| `moderex.admin.permissions` | Rank & permission management |
| `moderex.anticheat.configure` | Anticheat integration |

### Alert Permissions

Staff can configure each alert type (Everyone / Watchlist Only / Off) in the settings GUI:

| Permission | Alert Type |
|------------|-----------|
| `moderex.alerts.ban` | Ban alerts |
| `moderex.alerts.kick` | Kick alerts |
| `moderex.alerts.mute` | Mute alerts |
| `moderex.alerts.warn` | Warning alerts |
| `moderex.alerts.pardon` | Pardon/unban/unmute alerts |
| `moderex.alerts.anticheat` | Anticheat violation alerts |
| `moderex.alerts.automod` | Automod trigger alerts |
| `moderex.alerts.commands` | Command monitoring alerts |
| `moderex.alerts.joinleave` | Join/leave alerts |
| `moderex.alerts.watchlist` | Watchlist activity alerts |
| `moderex.alerts.staffchat` | Staff chat messages |

---

## Web Panel

The web panel is a browser-based staff interface for managing your server.

### Features
- Real-time dashboard with online players, punishments, and server stats
- Issue punishments directly from the browser
- View player profiles, history, IPs, and alt accounts
- Staff chat integration
- Activity log viewer
- Automod rule management
- Punishment templates
- Server configuration
- Evidence system (attach screenshots/files to punishments)
- Replay viewer

### Access Methods

1. **Gateway (easiest):** `panel.moderex.net/{server-id}/` - no port forwarding needed
2. **Direct:** `http://your-ip:8080` - requires port 8080 open
3. **Quick link:** Run `/mx connect` in-game for a 30-minute auth link
4. **Permanent token:** Run `/mx gettoken` for a reusable token

### Web Panel Permission

`moderex.webpanel` must be **explicitly granted** - it is NOT included with OP or `moderex.*` for security.

```
/lp user <player> permission set moderex.webpanel true
```

---

## Features

### Punishment Templates
Create reusable punishment presets with escalating severity. Templates auto-escalate based on how many times a player has been punished for the same category.

### Automod
Configure automatic chat filters with regex patterns, word lists, and actions. Manage via `/mx automod` GUI or the web panel.

### Replay System
Record and replay player actions (movement, block breaks, combat). Recordings can trigger automatically from anticheat alerts or watchlist status. Uses Citizens NPCs for playback.

### Vanish System
Full vanish with packet-level hiding, level-based visibility hierarchy, silent containers, spectator mode toggle, and granular action permissions.

### Disguise System
Disguise as other players with custom names, skins, and ranks. Integrates with LuckPerms for rank selection.

### Activity Logging
Comprehensive logging of chat, commands, signs, items, sessions, and more. Searchable via `/log` command with filters.

### Evidence System
Attach files, screenshots, and activity log entries to punishments as evidence. Configurable requirements and file size limits.

### Player Portal
Players can view their own punishment details at a unique URL generated by `/mx portal`. Shows punishment reason, duration, evidence, and appeal info.

### Anticheat Integration
Auto-detects and hooks into Grim, Vulcan, Matrix, Spartan, NCP, Themis, FoxAddition, and LightAC. Rebrands alerts to ModereX format with configurable thresholds.

---

## Reporting Issues

As a dev build tester, your feedback is invaluable. When reporting issues:

1. **Describe the bug** - What happened vs. what you expected
2. **Steps to reproduce** - How to trigger the issue
3. **Server info** - Paper/Spigot version, Java version, other plugins
4. **Errors** - Check `logs/latest.log` for stack traces
5. **Screenshots** - Of the issue or web panel errors (browser console: F12)

**Debug mode:** Set `general.debug: true` in config.yml for verbose logging, then reproduce the issue and share the log.

---

*ModereX Dev Build - Confidential. Do not redistribute.*
