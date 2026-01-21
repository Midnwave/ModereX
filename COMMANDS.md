# ModereX Commands Reference

> **Note:** This is a local documentation file for development reference. All commands support flags like `-s` (silent) to hide broadcasts.

---

## Table of Contents
- [Main Command](#main-command)
- [Punishment Commands](#punishment-commands)
- [Unpunishment Commands](#unpunishment-commands)
- [Check Commands](#check-commands)
- [History Commands](#history-commands)
- [List Commands](#list-commands)
- [IP & Account Commands](#ip--account-commands)
- [Admin Commands](#admin-commands)
- [Utility Commands](#utility-commands)
- [Staff Commands](#staff-commands)
- [Disguise Commands](#disguise-commands)
- [Shorthand Aliases](#shorthand-aliases)
- [Permissions](#permissions)

---

## Main Command

### /moderex (alias: /mx)
Main administrative command with subcommands.

**Usage:** `/mx <subcommand> [args]`
**Permission:** `moderex.admin`

**Subcommands:**
- `/mx reload` - Reload configuration
- `/mx connect` - Get web panel connect code
- `/mx settings` - Open settings GUI
- `/mx automod` - Open automod GUI
- `/mx chat <on|off|slowmode [seconds]>` - Chat management
- `/mx mutesettings` - Configure mute settings
- `/mx warningsettings` - Configure warning settings
- `/mx analytics` - View moderation analytics

---

## Punishment Commands

### /ban
Ban a player from the server.

**Usage:** `/ban <player> [duration] [reason] [-s]`
**Permission:** `moderex.ban`

**Examples:**
- `/ban Player123` - Permanent ban
- `/ban Player123 7d` - 7 day ban
- `/ban Player123 1mo3d Griefing -s` - Silent 1 month 3 day ban

### /tempban
Temporarily ban a player (duration required).

**Usage:** `/tempban <player> <duration> [reason] [-s]`
**Permission:** `moderex.tempban`

### /mute
Mute a player, preventing them from chatting.

**Usage:** `/mute <player> [duration] [reason] [-s]`
**Permission:** `moderex.mute`

### /tempmute
Temporarily mute a player (duration required).

**Usage:** `/tempmute <player> <duration> [reason] [-s]`
**Permission:** `moderex.tempmute`

### /warn
Warn a player.

**Usage:** `/warn <player> [duration] [reason] [-s]`
**Permission:** `moderex.warn`

### /kick
Kick a player from the server.

**Usage:** `/kick <player> [reason] [-s]`
**Permission:** `moderex.kick`

### /kickall
Kick all players from the server.

**Usage:** `/kickall [reason] [-s]`
**Permission:** `moderex.kickall`

### /ipban (aliases: /banip, /ban-ip)
Ban a player by IP address.

**Usage:** `/ipban <player> [duration] [reason] [-s]`
**Permission:** `moderex.ipban`

### /ipmute (alias: /muteip)
Mute a player by IP address.

**Usage:** `/ipmute <player> [duration] [reason] [-s]`
**Permission:** `moderex.ipmute`

### /punish
Open punishment GUI for a player.

**Usage:** `/punish [player]`
**Permission:** `moderex.punish`

---

## Unpunishment Commands

### /unban
Remove an active ban. Also removes IP bans.

**Usage:** `/unban <player|caseId> [reason] [-s]`
**Permission:** `moderex.unban`

**Examples:**
- `/unban Player123` - Unban by name
- `/unban #123` - Unban by case ID
- `/unban Player123 Appeal accepted -s` - Silent with reason

### /unmute
Remove an active mute.

**Usage:** `/unmute <player|caseId> [reason] [-s]`
**Permission:** `moderex.unmute`

### /unwarn
Remove an active warning.

**Usage:** `/unwarn <player|caseId> [reason] [-s]`
**Permission:** `moderex.unwarn`

### /clearwarnings
Clear all warnings for a player.

**Usage:** `/clearwarnings <player>`
**Permission:** `moderex.clearwarnings`

---

## Check Commands

### /check
Check comprehensive player information (punishments, warnings, IP, etc.).

**Usage:** `/check <player>`
**Permission:** `moderex.check`

### /checkban
Check if a player is banned.

**Usage:** `/checkban <player|caseId>`
**Permission:** `moderex.checkban`

### /checkmute
Check if a player is muted.

**Usage:** `/checkmute <player|caseId>`
**Permission:** `moderex.checkmute`

### /checkwarn
Check active warnings for a player.

**Usage:** `/checkwarn <player|caseId>`
**Permission:** `moderex.checkwarn`

---

## History Commands

### /history (alias: /hist)
View punishment history for a player.

**Usage:** `/history <player> [type]`
**Permission:** `moderex.history`

**Types:** `ban`, `mute`, `warn`, `kick`, `all`

### /staffhistory (alias: /staffhist)
View punishments executed by a staff member.

**Usage:** `/staffhistory <staff> [type]`
**Permission:** `moderex.staffhistory`

### /modlog
View moderation log for a player.

**Usage:** `/modlog <player> [filter]`
**Permission:** `moderex.modlog`

### /cmdhistory
View command history for a player.

**Usage:** `/cmdhistory <player> [page]`
**Permission:** `moderex.cmdhistory`

---

## List Commands

### /banlist
View a paginated list of active bans.

**Usage:** `/banlist [page]`
**Permission:** `moderex.banlist`

### /mutelist
View a paginated list of active mutes.

**Usage:** `/mutelist [page]`
**Permission:** `moderex.mutelist`

### /warnlist
View a paginated list of active warnings.

**Usage:** `/warnlist [page]`
**Permission:** `moderex.warnlist`

### /warnings
View active warnings for yourself or a player.

**Usage:** `/warnings [player]`
**Permission:** `moderex.warnings`

---

## IP & Account Commands

### /dupeip (aliases: /alts, /checkalts)
Display associated accounts of a user or IP.

**Usage:** `/dupeip <player|ip>`
**Permission:** `moderex.dupeip`

### /iphistory
Display IP history for a user.

**Usage:** `/iphistory <player|ip>`
**Permission:** `moderex.iphistory`

### /ipreport
Show duplicate IPs among online players.

**Usage:** `/ipreport`
**Permission:** `moderex.ipreport`

### /geoip
Display a user's country based on IP.

**Usage:** `/geoip <player>`
**Permission:** `moderex.geoip`

### /lastuuid
Display a user's UUID.

**Usage:** `/lastuuid <player>`
**Permission:** `moderex.lastuuid`

### /namehistory
Display previous usernames for a player.

**Usage:** `/namehistory <player>`
**Permission:** `moderex.namehistory`

---

## Admin Commands

### /lockdown
Enable or disable server lockdown mode.

**Usage:** `/lockdown [server:local|global] [end]`
**Permission:** `moderex.lockdown`

**Examples:**
- `/lockdown` - Toggle local lockdown
- `/lockdown global` - Enable global lockdown (all servers)
- `/lockdown end` - End lockdown

### /prunehistory
Remove old inactive punishments from history.

**Usage:** `/prunehistory <player> [duration]`
**Permission:** `moderex.prunehistory`

### /staffrollback
Rollback all punishments executed by a staff member.

**Usage:** `/staffrollback <staff> [duration]`
**Permission:** `moderex.staffrollback`

### /cmdblacklist
Blacklist a command for a player.

**Usage:** `/cmdblacklist <player> <command> [duration]`
**Permission:** `moderex.cmdblacklist`

### /cmdunblacklist
Remove command blacklist from a player.

**Usage:** `/cmdunblacklist <player> <command>`
**Permission:** `moderex.cmdunblacklist`

---

## Utility Commands

### /staffchat (alias: /sc)
Staff chat toggle or send message.

**Usage:** `/staffchat <on|off|message>`
**Permission:** `moderex.staffchat`

**Examples:**
- `/sc on` - Enable staff chat mode
- `/sc off` - Disable staff chat mode
- `/sc Hey staff!` - Send message to staff chat

### /staffhelp
Request help from staff.

**Usage:** `/staffhelp`
**Permission:** `moderex.staffhelp`

### /watchlist (alias: /wl)
Manage watchlist of suspicious players.

**Usage:** `/watchlist [gui|add|remove|list|note|check] [player] [reason/note]`
**Permission:** `moderex.command.watchlist`

**Subcommands:**
- `/wl gui` - Open watchlist GUI
- `/wl add <player> [reason]` - Add player to watchlist
- `/wl remove <player>` - Remove from watchlist
- `/wl list` - List all watched players
- `/wl note <player> <note>` - Add note to watched player
- `/wl check <player>` - Check if player is watched

### /vanish (alias: /v)
Toggle vanish mode and manage vanished players.

**Usage:** `/vanish [toggle|enable|disable|player|list] [player]`
**Permission:** `moderex.vanish`

**Subcommands:**
- `/v` - Toggle your vanish
- `/v enable` - Enable vanish
- `/v disable` - Disable vanish
- `/v <player>` - Toggle another player's vanish
- `/v list` - List vanished players

---

## Disguise Commands

### /disguise (alias: /d)
Disguise as another player.

**Usage:** `/disguise [name] [rank] | gui | remove`
**Permission:** `moderex.disguise`

**Examples:**
- `/d` or `/d gui` - Open disguise GUI
- `/d PlayerName` - Disguise with random name
- `/d PlayerName vip` - Disguise with VIP rank
- `/d remove` - Remove disguise

### /disguisename (alias: /dname)
Change disguise name.

**Usage:** `/disguisename <name>`
**Permission:** `moderex.disguise`

### /disguiseskin (alias: /dskin)
Change disguise skin.

**Usage:** `/disguiseskin <playerName>`
**Permission:** `moderex.disguise`

---

## Shorthand Aliases

These are quick shortcuts for common commands:

| Shorthand | Full Command |
|-----------|--------------|
| `/mban`   | `/ban`       |
| `/munban` | `/unban`     |
| `/mmute`  | `/mute`      |
| `/munmute`| `/unmute`    |
| `/mwarn`  | `/warn`      |
| `/munwarn`| `/unwarn`    |
| `/mkick`  | `/kick`      |

---

## Duration Format

Durations can be specified in various formats:
- `s` - Seconds (e.g., `30s`)
- `m` - Minutes (e.g., `10m`)
- `h` - Hours (e.g., `2h`)
- `d` - Days (e.g., `7d`)
- `w` - Weeks (e.g., `2w`)
- `mo` - Months (e.g., `1mo`)
- `y` - Years (e.g., `1y`)

**Combined:** `1mo3d12h` = 1 month, 3 days, 12 hours

**Permanent:** Use `perm`, `permanent`, or no duration

---

## Permissions

### Wildcard Permissions
- `moderex.*` - All permissions
- `moderex.command.*` - All commands
- `moderex.notify.*` - All notifications
- `moderex.bypass.*` - All bypasses

### Bypass Permissions
- `moderex.bypass.mute` - Chat while muted
- `moderex.bypass.slowmode` - Bypass slowmode
- `moderex.bypass.chatdisable` - Chat when disabled
- `moderex.bypass.automod` - Bypass automod filters

### Notification Permissions
- `moderex.notify.punishments` - See punishment broadcasts
- `moderex.notify.automod` - See automod alerts
- `moderex.notify.anticheat` - See anticheat alerts
- `moderex.notify.staffchat` - Access staff chat

### Vanish Permissions
- `moderex.vanish` - Toggle vanish
- `moderex.vanish.others` - Vanish other players
- `moderex.vanish.list` - View vanished players list
- `moderex.vanish.level.<n>` - Vanish level
- `moderex.vanish.see.level.<n>` - See vanished up to level
- `moderex.vanish.pickup` - Pick up items while vanished
- `moderex.vanish.chat` - Chat while vanished
- `moderex.vanish.place` - Place blocks while vanished
- `moderex.vanish.break` - Break blocks while vanished
- `moderex.vanish.attack` - Attack while vanished
- `moderex.vanish.flight` - Fly when vanished
- `moderex.vanish.spectator` - Double-sneak spectator

### Admin Permissions
- `moderex.admin` - Access /mx commands
- `moderex.webpanel` - Connect to web panel

---

## Flags

Most commands support these flags:
- `-s` or `-S` or `--silent` - Execute silently (no broadcast)
- `-h` or `--hide` - Hide from public announcements

---

*Last Updated: 2026-01-21*
