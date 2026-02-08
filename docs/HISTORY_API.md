# History API

ModereX provides comprehensive history tracking for players, commands, and moderation actions.

## Overview

The History API allows you to:
- Query punishment history for players
- Track command execution history
- View activity logs and automod triggers
- Access data via in-game commands, web panel, or API

## In-Game Commands

### /history (or /punishments)
View a player's punishment history.

```
/history <player> [page]
```

**Aliases:** `/hist`, `/punishments`, `/phistory`

**Permissions:** `moderex.history`

### /staffhistory
View punishments issued by a specific staff member.

```
/staffhistory <staff> [page]
```

**Permissions:** `moderex.staffhistory`

### /iphistory
View punishment history for an IP address.

```
/iphistory <ip> [page]
```

**Permissions:** `moderex.iphistory`

### /namehistory
View all known usernames for a player.

```
/namehistory <player>
```

**Permissions:** `moderex.namehistory`

### /cmdhistory
View command execution history for a player.

```
/cmdhistory <player> [page]
```

**Permissions:** `moderex.cmdhistory`

## Web Panel API

The web panel uses WebSocket messages to retrieve history data.

### GET_PLAYER_DETAILS

Request player details including punishment history.

**Request:**
```json
{
  "type": "GET_PLAYER_DETAILS",
  "data": {
    "uuid": "player-uuid-here"
  }
}
```

**Response:**
```json
{
  "type": "PLAYER_DETAILS",
  "data": {
    "uuid": "player-uuid",
    "name": "PlayerName",
    "nickname": "DisplayName",
    "online": true,
    "firstPlayed": 1609459200000,
    "lastPlayed": 1704067200000,
    "watched": false,
    "geyser": false,
    "platform": "Java",
    "muted": false,
    "banned": false,
    "warnings": 2,
    "punishments": [...],
    "recentCommands": [...],
    "chatLogs": [...],
    "automodLogs": [...],
    "ipHistory": [...]
  }
}
```

### GET_PUNISHMENTS

Retrieve recent punishments with filtering.

**Request:**
```json
{
  "type": "GET_PUNISHMENTS",
  "data": {
    "limit": 100,
    "type": "BAN",
    "active": true
  }
}
```

**Response:**
```json
{
  "type": "PUNISHMENTS_DATA",
  "data": [
    {
      "caseId": 123,
      "type": "BAN",
      "playerUuid": "uuid",
      "playerName": "PlayerName",
      "staffUuid": "staff-uuid",
      "staffName": "StaffName",
      "reason": "Cheating",
      "createdAt": 1704067200000,
      "expiresAt": -1,
      "active": true,
      "permanent": true
    }
  ]
}
```

### GET_COMMAND_HISTORY

Retrieve paginated command history.

**Request:**
```json
{
  "type": "GET_COMMAND_HISTORY",
  "data": {
    "page": 1,
    "limit": 50,
    "search": "ban"
  }
}
```

**Response:**
```json
{
  "type": "COMMAND_HISTORY_DATA",
  "data": {
    "commands": [...],
    "total": 150,
    "page": 1,
    "totalPages": 3
  }
}
```

### GET_AUTOMOD_LOGS

Retrieve paginated automod trigger logs.

**Request:**
```json
{
  "type": "GET_AUTOMOD_LOGS",
  "data": {
    "page": 1,
    "limit": 50,
    "search": "spam"
  }
}
```

**Response:**
```json
{
  "type": "AUTOMOD_LOGS_DATA",
  "data": {
    "logs": [...],
    "total": 200,
    "page": 1,
    "totalPages": 4
  }
}
```

## Java API

Access history programmatically via the plugin's managers.

### PunishmentManager

```java
// Get player's punishment history
plugin.getPunishmentManager().getPunishments(UUID playerUuid)
    .thenAccept(punishments -> {
        for (Punishment p : punishments) {
            // Process punishment
        }
    });

// Get active punishments
Punishment activeBan = plugin.getPunishmentManager()
    .getActivePunishment(playerUuid, PunishmentType.BAN);

// Get recent punishments
plugin.getPunishmentManager().getRecentPunishments(100)
    .thenAccept(recent -> {
        // Process recent punishments
    });
```

### ActivityLogManager

```java
// Log activity
plugin.getActivityLogManager().log(player, ActivityType.COMMAND, "/ban player");

// Query activity logs
plugin.getActivityLogManager().getRecentLogs(50, "command")
    .thenAccept(logs -> {
        for (ActivityLogEntry entry : logs) {
            // Process log entry
        }
    });
```

## Retention Settings

Configure how long different log types are kept in `config.yml`:

```yaml
retention:
  chat: 30          # Days to keep chat logs
  commands: 30      # Days to keep command logs
  punishments: -1   # Keep forever (-1)
  pardons: -1       # Keep forever
  automod: 30       # Days to keep automod logs
  anticheat: 30     # Days to keep anticheat logs
  staff-actions: 90 # Days to keep staff action logs
  watchlist: 90     # Days to keep watchlist logs
```

Use `-1` to keep logs forever, or any positive number for days to retain.

## Activity Types

The activity log tracks the following types:

| Type | Description |
|------|-------------|
| COMMAND | Command execution |
| CHAT | Chat messages |
| PUNISHMENT_BAN | Ban issued |
| PUNISHMENT_MUTE | Mute issued |
| PUNISHMENT_WARN | Warning issued |
| PUNISHMENT_KICK | Kick issued |
| PUNISHMENT_UNBAN | Ban revoked |
| PUNISHMENT_UNMUTE | Mute revoked |
| PUNISHMENT_UNWARN | Warning revoked |
| AUTOMOD_TRIGGER | Automod rule triggered |
| ANTICHEAT_ALERT | Anticheat flag |
| NICKNAME_CHANGE | Player nickname changed |
| USERNAME_CHANGE | Player username changed |
| IP_CHANGE | Player IP address changed |
| SESSION_JOIN | Player joined server |
| SESSION_QUIT | Player left server |
| STAFF_WATCHLIST_ADD | Player added to watchlist |
| STAFF_WATCHLIST_REMOVE | Player removed from watchlist |
