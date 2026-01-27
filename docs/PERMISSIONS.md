# ModereX Permissions Reference

Complete reference for all ModereX permissions. This document covers command permissions, notification permissions, bypass permissions, and planned future permissions for granular web panel access.

---

## Table of Contents

- [Wildcard Permissions](#wildcard-permissions)
- [Punishment Commands](#punishment-commands)
- [Unpunishment Commands](#unpunishment-commands)
- [Punishment Flags](#punishment-flags)
- [Check & History Commands](#check--history-commands)
- [Account & IP Commands](#account--ip-commands)
- [Staff Tools](#staff-tools)
- [Vanish System](#vanish-system)
- [Disguise System](#disguise-system)
- [Administrative](#administrative)
- [Bypass Permissions](#bypass-permissions)
- [Notification Permissions](#notification-permissions)
- [Web Panel Permissions](#web-panel-permissions)
- [Planned Permissions (Future)](#planned-permissions-future)

---

## Wildcard Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `moderex.*` | Access to ALL ModereX features | op |
| `moderex.command.*` | Access to all ModereX commands | op |
| `moderex.notify.*` | Receive all staff notifications | op |
| `moderex.bypass.*` | Bypass all restrictions | op |

---

## Punishment Commands

### Ban Commands

| Permission | Description | Command | Default |
|------------|-------------|---------|---------|
| `moderex.ban` | Ban players permanently or temporarily | `/ban <player> [duration] [reason]` | op |
| `moderex.tempban` | Temporarily ban players (duration required) | `/tempban <player> <duration> [reason]` | op |
| `moderex.ipban` | Ban players by IP address (affects all accounts) | `/ipban <player> [duration] [reason]` | op |

### Mute Commands

| Permission | Description | Command | Default |
|------------|-------------|---------|---------|
| `moderex.mute` | Mute players permanently or temporarily | `/mute <player> [duration] [reason]` | op |
| `moderex.tempmute` | Temporarily mute players (duration required) | `/tempmute <player> <duration> [reason]` | op |
| `moderex.ipmute` | Mute players by IP address | `/ipmute <player> [duration] [reason]` | op |

### Other Punishments

| Permission | Description | Command | Default |
|------------|-------------|---------|---------|
| `moderex.warn` | Issue warnings to players | `/warn <player> [duration] [reason]` | op |
| `moderex.ipwarn` | Warn players by IP address | `/ipwarn <player> [duration] [reason]` | op |
| `moderex.kick` | Kick players from the server | `/kick <player> [reason]` | op |
| `moderex.punish` | Open the punishment GUI | `/punish [player]` | op |

---

## Unpunishment Commands

| Permission | Description | Command | Default |
|------------|-------------|---------|---------|
| `moderex.unban` | Remove active bans | `/unban <player\|caseId> [reason]` | op |
| `moderex.unmute` | Remove active mutes | `/unmute <player\|caseId> [reason]` | op |
| `moderex.unwarn` | Remove active warnings | `/unwarn <player\|caseId> [reason]` | op |
| `moderex.clearwarnings` | Clear ALL warnings for a player | `/clearwarnings <player>` | op |

---

## Punishment Flags

These permissions control access to punishment command flags.

| Permission | Flag | Description | Default |
|------------|------|-------------|---------|
| `moderex.delete` | `-d` | Delete punishments entirely (removes from history) | op |
| `moderex.delete.own` | `-d` | Delete only your own punishments | op |
| `moderex.modify` | `-m` | Modify existing punishments | op |
| `moderex.modify.own` | `-m` | Modify only your own punishments | op |
| `moderex.server.global` | `-g` | Create network-wide global punishments | op |
| `moderex.public` | `-p` | Override silent defaults, make punishment public | op |
| `moderex.notify.silent` | `-s` | Create silent punishments (staff-only notification) | op |
| `moderex.extrasilent` | `-S` | Create extra-silent punishments (no notifications) | op |
| `moderex.admin` | `--hide`, `--skip`, `--server-origin` | Administrative flags for special cases | op |

---

## Check & History Commands

### Player Checks

| Permission | Description | Command | Default |
|------------|-------------|---------|---------|
| `moderex.check` | View comprehensive player information panel | `/check <player>` | op |
| `moderex.check.ip` | See IP addresses in `/check` output | - | op |
| `moderex.checkban` | Check if a player is banned | `/checkban <player\|caseId>` | op |
| `moderex.checkmute` | Check if a player is muted | `/checkmute <player\|caseId>` | op |
| `moderex.checkwarn` | View active warnings for a player | `/checkwarn <player\|caseId>` | op |

### History & Lists

| Permission | Description | Command | Default |
|------------|-------------|---------|---------|
| `moderex.history` | View punishment history for a player | `/history <player> [type]` | op |
| `moderex.staffhistory` | View punishments issued by a staff member | `/staffhistory <staff> [type]` | op |
| `moderex.warnings` | View your own active warnings | `/warnings [player]` | **true** |
| `moderex.banlist` | View paginated list of active bans | `/banlist [page]` | op |
| `moderex.mutelist` | View paginated list of active mutes | `/mutelist [page]` | op |
| `moderex.warnlist` | View paginated list of active warnings | `/warnlist [page]` | op |
| `moderex.command.viewpunishment` | View detailed punishment info by case ID | `/viewpunishment <caseId>` | op |
| `moderex.modlog` | View moderation activity log | `/modlog <player> [filter]` | op |

---

## Account & IP Commands

> **Sensitive Data**: These commands expose player IP addresses and account associations.

| Permission | Description | Command | Default |
|------------|-------------|---------|---------|
| `moderex.dupeip` | Check for alt accounts sharing IPs | `/dupeip <player\|ip>` | op |
| `moderex.iphistory` | View complete IP history for a player | `/iphistory <player>` | op |
| `moderex.ipreport` | Show duplicate IPs among online players | `/ipreport` | op |
| `moderex.geoip` | View player's country/region from IP | `/geoip <player>` | op |
| `moderex.lastuuid` | Display player's UUID | `/lastuuid <player>` | op |
| `moderex.namehistory` | View previous usernames for a player | `/namehistory <player>` | op |

---

## Staff Tools

### Staff Chat

| Permission | Description | Command | Default |
|------------|-------------|---------|---------|
| `moderex.staffchat` | Use staff chat | `/staffchat <message>` or `/sc` | op |
| `moderex.staffhelp` | Request help from online staff | `/staffhelp` | **true** |

### Watchlist

| Permission | Description | Command | Default |
|------------|-------------|---------|---------|
| `moderex.command.watchlist` | Manage watchlist of suspicious players | `/watchlist [add\|remove\|list\|note]` | op |

### Command Blacklist

| Permission | Description | Command | Default |
|------------|-------------|---------|---------|
| `moderex.cmdblacklist` | Blacklist commands for specific players | `/cmdblacklist <player> <command> [duration]` | op |
| `moderex.cmdunblacklist` | Remove command blacklists | `/cmdunblacklist <player> <command>` | op |
| `moderex.cmdhistory` | View command history for a player | `/cmdhistory <player> [page]` | op |

### Activity Logs

| Permission | Description | Command | Default |
|------------|-------------|---------|---------|
| `moderex.log` | View player activity logs | `/log <player> [action:type] [time:duration]` | op |
| `moderex.log.view` | Alias for `moderex.log` | - | op |
| `moderex.log.teleport` | Teleport to locations from activity log | Click in log GUI | op |

### Player Info

| Permission | Description | Command | Default |
|------------|-------------|---------|---------|
| `moderex.command.seen` | View when a player was last online | `/seen <player>` | op |
| `moderex.command.seen.ip` | See IP address in `/seen` output | - | op |

### Rules

| Permission | Description | Command | Default |
|------------|-------------|---------|---------|
| `moderex.rules` | View server rules | `/rules [page\|category]` | **true** |

---

## Vanish System

### Core Vanish

| Permission | Description | Default |
|------------|-------------|---------|
| `moderex.vanish` | Toggle vanish for yourself | op |
| `moderex.vanish.others` | Toggle vanish for other players | op |
| `moderex.command.vanish` | Use the vanish command | op |
| `moderex.command.vanish.others` | Vanish/unvanish other players | op |

### Vanish Levels

Vanish levels create a hierarchy - higher levels can see lower levels.

| Permission | Description | Default |
|------------|-------------|---------|
| `moderex.vanish.level.1` | Vanish at level 1 (basic) | false |
| `moderex.vanish.level.2` | Vanish at level 2 | false |
| `moderex.vanish.level.3` | Vanish at level 3 | false |
| `moderex.vanish.level.*` | Vanish at any level | false |
| `moderex.vanish.see.level.1` | See players vanished at level 1 | false |
| `moderex.vanish.see.level.2` | See players vanished at level 2 | false |
| `moderex.vanish.see.level.3` | See players vanished at level 3 | false |
| `moderex.vanish.see.level.*` | See all vanished players | false |

### Vanish Behaviors

| Permission | Description | Default |
|------------|-------------|---------|
| `moderex.vanish.pickup` | Pick up items while vanished | false |
| `moderex.vanish.chat` | Chat while vanished | false |
| `moderex.vanish.place` | Place blocks while vanished | false |
| `moderex.vanish.break` | Break blocks while vanished | false |
| `moderex.vanish.attack` | Attack entities while vanished | false |
| `moderex.vanish.mobattack` | Be attacked by mobs while vanished | false |
| `moderex.vanish.flight` | Automatically enable flight when vanishing | op |
| `moderex.vanish.keepfly` | Keep flight after unvanishing | op |
| `moderex.vanish.spectator` | Double-sneak to toggle spectator mode | op |

---

## Disguise System

| Permission | Description | Default |
|------------|-------------|---------|
| `moderex.disguise` | Use disguise commands | op |
| `moderex.command.disguise` | Access `/disguise` command | op |
| `moderex.disguise.rank.<rank>` | Disguise as a specific rank (e.g., `moderex.disguise.rank.vip`) | false |
| `moderex.disguise.rank.*` | Disguise as any rank | false |

---

## Administrative

### Core Admin

| Permission | Description | Command | Default |
|------------|-------------|---------|---------|
| `moderex.admin` | Full administrative access | Various | op |
| `moderex.command.admin` | Admin-only commands (replay, etc.) | `/replay`, `/mx` subcommands | op |

### Server Management

| Permission | Description | Command | Default |
|------------|-------------|---------|---------|
| `moderex.lockdown` | Enable/disable server lockdown | `/lockdown [local\|global] [end]` | op |
| `moderex.lockdown.bypass` | Join during lockdown | - | op |
| `moderex.prunehistory` | Remove old punishments from history | `/prunehistory <player> [duration]` | op |
| `moderex.staffrollback` | Rollback all punishments by a staff member | `/staffrollback <staff> [duration]` | op |

### Configuration

| Permission | Description | Command | Default |
|------------|-------------|---------|---------|
| `moderex.reload` | Reload plugin configuration | `/mx reload` | op |
| `moderex.info` | View database and plugin info | `/mx info` | op |
| `moderex.servers` | View connected proxy servers | `/mx servers` | op |
| `moderex.reveal` | Reveal randomized punishment IDs | `/mx reveal <id>` | op |
| `moderex.broadcast` | Network-wide broadcasts | `/mx broadcast <message>` | op |
| `moderex.timezone` | Manage timezone settings | `/mx timezone` | op |
| `moderex.reset-templates` | Reset punishment template progression | `/mx reset-templates <player>` | op |
| `moderex.allow` | Manage allowed users list | `/mx allow` | op |
| `moderex.unlink` | Unlink IP associations | `/mx unlink` | op |

---

## Bypass Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `moderex.bypass.*` | Bypass all restrictions | op |
| `moderex.bypass.mute` | Chat while muted | false |
| `moderex.bypass.slowmode` | Bypass chat slowmode | op |
| `moderex.bypass.chatdisable` | Chat when chat is globally disabled | op |
| `moderex.bypass.automod` | Bypass all automod filters | op |
| `moderex.bypass.automod.nickname` | Bypass nickname automod checks | op |

---

## Notification Permissions

These control which staff notifications a player receives.

| Permission | Description | Default |
|------------|-------------|---------|
| `moderex.notify.*` | Receive all notifications | op |
| `moderex.notify.punishments` | See punishment broadcasts | op |
| `moderex.notify.automod` | See automod trigger alerts | op |
| `moderex.notify.anticheat` | See anticheat violation alerts | op |
| `moderex.notify.staffchat` | Access to staff chat messages | op |
| `moderex.notify.broadcast` | Receive network-wide broadcasts | op |
| `moderex.notify.silent` | See silent punishment notifications | op |

---

## Web Panel Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `moderex.webpanel` | Connect to and use the web panel | op |

---

## Planned Permissions (Future)

The following permissions are planned for future releases to provide granular control over web panel access and sensitive data visibility.

### Web Panel Access Control

```yaml
# Page Access
moderex.panel.dashboard          # View dashboard
moderex.panel.players            # View players list
moderex.panel.punishments        # View punishments list
moderex.panel.templates          # View/manage templates
moderex.panel.automod            # View/manage automod rules
moderex.panel.settings           # Access settings page
moderex.panel.logs               # View activity logs
moderex.panel.devtools           # Access developer tools

# Player Data Visibility
moderex.panel.view.ip            # See player IP addresses
moderex.panel.view.alts          # See alt account associations
moderex.panel.view.geoip         # See player location data
moderex.panel.view.history       # See full punishment history
moderex.panel.view.sessions      # See login session history

# Actions
moderex.panel.action.punish      # Create punishments from panel
moderex.panel.action.revoke      # Revoke punishments from panel
moderex.panel.action.kick        # Kick players from panel
moderex.panel.action.watchlist   # Manage watchlist from panel
moderex.panel.action.staffchat   # Send staff chat from panel
moderex.panel.action.clearChat   # Clear chat from panel
moderex.panel.action.lockdown    # Toggle lockdown from panel
moderex.panel.action.update      # Trigger plugin updates

# Template Management
moderex.panel.templates.create   # Create new templates
moderex.panel.templates.edit     # Edit existing templates
moderex.panel.templates.delete   # Delete templates

# Automod Management
moderex.panel.automod.create     # Create automod rules
moderex.panel.automod.edit       # Edit automod rules
moderex.panel.automod.delete     # Delete automod rules
moderex.panel.automod.toggle     # Enable/disable rules

# Settings
moderex.panel.settings.chat      # Modify chat settings
moderex.panel.settings.mute      # Modify mute settings
moderex.panel.settings.anticheat # Modify anticheat settings
```

### Sensitive Data Tiers

```yaml
# Tier 1: Basic Staff (Helper/Trial Mod)
moderex.data.tier1:
  - View player names and UUIDs
  - View punishment reasons
  - View warning counts

# Tier 2: Moderator
moderex.data.tier2:
  - Everything in Tier 1
  - View punishment history
  - View first/last join times
  - View session counts

# Tier 3: Senior Staff
moderex.data.tier3:
  - Everything in Tier 2
  - View IP addresses
  - View alt accounts
  - View GeoIP data
  - View login sessions

# Tier 4: Administrator
moderex.data.tier4:
  - Everything in Tier 3
  - View staff action history
  - View audit logs
  - Access developer tools
```

---

## Permission Setup Examples

### Helper/Trial Moderator

```yaml
permissions:
  - moderex.warn
  - moderex.kick
  - moderex.check
  - moderex.history
  - moderex.staffchat
  - moderex.notify.punishments
  - moderex.notify.staffchat
```

### Moderator

```yaml
permissions:
  - moderex.ban
  - moderex.mute
  - moderex.warn
  - moderex.kick
  - moderex.tempmute
  - moderex.tempban
  - moderex.unban
  - moderex.unmute
  - moderex.unwarn
  - moderex.check
  - moderex.check.ip
  - moderex.history
  - moderex.dupeip
  - moderex.vanish
  - moderex.staffchat
  - moderex.command.watchlist
  - moderex.notify.*
  - moderex.webpanel
```

### Senior Moderator

```yaml
permissions:
  - moderex.command.*
  - moderex.notify.*
  - moderex.vanish.*
  - moderex.webpanel
  - -moderex.admin  # Exclude admin
  - -moderex.staffrollback  # Exclude dangerous commands
```

### Administrator

```yaml
permissions:
  - moderex.*
```

---

## Notes

1. **Default Values**: `op` means operators have the permission by default. `true` means all players have it. `false` means no one has it by default.

2. **Wildcard Children**: Wildcard permissions (ending in `.*`) automatically grant all child permissions.

3. **Negative Permissions**: Use `-permission.node` in most permission plugins to explicitly deny a permission.

4. **LuckPerms Integration**: ModereX integrates with LuckPerms for rank display and permission checks.

---

*Last updated: 2026-01-27*
*ModereX Version: 2.0-dev*
