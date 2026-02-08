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
- [Replay Commands](#replay-commands)
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

**Moderation:**
- `/mx ban <player> [duration] [reason]` - Ban a player
- `/mx unban <player> [reason]` - Unban a player
- `/mx mute <player> [duration] [reason]` - Mute a player
- `/mx unmute <player> [reason]` - Unmute a player
- `/mx kick <player> [reason]` - Kick a player
- `/mx warn <player> [duration] [reason]` - Warn a player
- `/mx clearwarnings <player>` - Clear all warnings
- `/mx ipban <player> [duration] [reason]` - IP ban a player
- `/mx punish [player]` - Open punishment GUI
- `/mx modlog [player]` - View moderation log (alias: `/mx history`)

**Staff:**
- `/mx staffchat [message]` - Toggle or send staff chat (alias: `/mx sc`)
- `/mx vanish` - Toggle vanish mode (alias: `/mx v`)

**Admin:**
- `/mx reload` - Reload configuration
- `/mx chat <enable|disable|slowmode|clear>` - Chat management
- `/mx automod` - Open automod GUI
- `/mx anticheat` - Configure anticheat alerts (alias: `/mx ac`)
- `/mx sendalert <player> <anticheat> <check> [vl]` - Send test anticheat alert
- `/mx settings` - Open staff settings GUI
- `/mx analytics` - View moderation analytics
- `/mx mutesettings` - Configure mute settings
- `/mx warningsettings` - Configure warning settings
- `/mx update` - Check GitHub for updates and auto-download

**Web Panel:**
- `/mx connect` - Get quick web panel link (30 minute expiry)
- `/mx gettoken` - Generate permanent web access token
- `/mx revoketoken` - Revoke your permanent token
- `/mx sessions` - View web session status

**Replay System:**
- `/mx replay` - Open replay browser GUI
- `/mx replay start <player>` - Start recording a player
- `/mx replay stop <player>` - Stop recording a player
- `/mx replay play <sessionId>` - Play back a replay
- `/mx replay list` - List saved replays
- `/mx replay search <player>` - Find replays for a player
- `/mx replay delete <sessionId>` - Delete a replay
- `/mx replay status` - Show replay system status

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
- `/unban MX-A1B2C3` - Unban by case ID
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

### /viewpunishment (aliases: /vp, /case, /punishinfo)
View detailed information about a punishment by its case ID.

**Usage:** `/viewpunishment <caseId>`
**Permission:** `moderex.command.viewpunishment`

**Examples:**
- `/viewpunishment MX-A1B2C3` - View punishment details
- `/vp MX-A1B2C3` - Shorthand alias
- `/case MX-A1B2C3` - Alternative alias

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

### /seen (aliases: /playerinfo, /lastseen)
View when a player was last seen and detailed player info.

**Usage:** `/seen <player>`
**Permission:** `moderex.command.seen`

**Features:**
- Shows online/offline status
- First join and last seen timestamps
- Current punishment status (banned/muted)
- Punishment history summary
- Watchlist status
- Quick action buttons (Check, History, Commands)
- IP address (requires `moderex.command.seen.ip`)

### /rules (alias: /serverrules)
View server rules.

**Usage:** `/rules [page|rule#|category]`
**Permission:** `moderex.rules`

**Examples:**
- `/rules` - View first page of rules
- `/rules 2` - View specific rule number
- `/rules page:2` - View second page
- `/rules chat` - View rules in chat category

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

## Staff Commands

### /staffmode
Toggle staff mode (special inventory, tools, etc.).

**Usage:** `/staffmode`
**Permission:** `moderex.command.staffmode`

---

## Replay Commands

The replay system allows recording and playback of player actions for review.

### /mx replay
Open replay browser GUI or manage replays.

**Usage:** `/mx replay [subcommand] [args]`
**Permission:** `moderex.command.admin`

**Subcommands:**
- `/mx replay` - Open replay browser GUI
- `/mx replay start <player>` - Start recording a player
- `/mx replay stop <player>` - Stop recording a player
- `/mx replay play <sessionId>` - Play back a recorded session
- `/mx replay list` - List all saved replays
- `/mx replay search <player>` - Find replays for a specific player
- `/mx replay delete <sessionId>` - Delete a replay
- `/mx replay status` - Show replay system status

**Notes:**
- Replays can be triggered automatically by anticheat alerts or watchlist status
- Uses Citizens NPCs for playback visualization
- Supports playback controls (play, pause, speed, skip)

---

## Disguise Commands

### /disguise (alias: /d)
Disguise as another player.

**Usage:** `/disguise [name] [rank] | gui | remove`
**Permission:** `moderex.disguise`

**Examples:**
- `/d` or `/d gui` - Open disguise GUI
- `/d PlayerName` - Disguise with specified name
- `/d PlayerName vip` - Disguise with VIP rank
- `/d remove` - Remove disguise

### /undisguise
Remove your current disguise.

**Usage:** `/undisguise`
**Permission:** `moderex.command.disguise`

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
- `moderex.notify.broadcast` - Receive network broadcasts

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
- `moderex.vanish.mobattack` - Be attacked by mobs while vanished
- `moderex.vanish.flight` - Fly when vanished
- `moderex.vanish.keepfly` - Keep flight after unvanishing
- `moderex.vanish.spectator` - Double-sneak spectator toggle

### Admin Permissions
- `moderex.admin` - Access /mx admin commands
- `moderex.webpanel` - Connect to web panel
- `moderex.command.staffmode` - Use staff mode

### Other Permissions
- `moderex.command.viewpunishment` - View punishment details by case ID
- `moderex.command.seen` - Use /seen command
- `moderex.command.seen.ip` - View IP in /seen command
- `moderex.command.watchlist` - Manage watchlist
- `moderex.rules` - View server rules
- `moderex.disguise` - Use disguise commands
- `moderex.disguise.rank.*` - Disguise as any rank

---

## Flags

Most commands support these flags:
- `-s` or `-S` or `--silent` - Execute silently (no broadcast)
- `-h` or `--hide` - Hide from public announcements
- `-g` - Global punishment (all servers)
- `-p` - Override silent defaults (public)
- `-d` - Delete punishment (requires `moderex.delete`)
- `-m` - Modify punishment (requires `moderex.modify`)

---

*Last Updated: 2026-01-23*
