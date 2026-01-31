# ModereX Permissions

This document contains all permissions used by ModereX. When editing permissions, **always update the corresponding commands, GUIs, and web panel checks**.

> **Note:** Server operators (OP) bypass all permission checks.

---

## Wildcard Permissions

| Permission | Description |
|------------|-------------|
| `moderex.*` | Access to all ModereX features |
| `moderex.command.*` | Access to all ModereX commands |
| `moderex.bypass.*` | Bypass all restrictions |

---

## Punishment Permissions

### Issuing Punishments

| Permission | Description |
|------------|-------------|
| `moderex.ban` | Ban players permanently and temporarily |
| `moderex.tempban` | Only temp ban players (perm ban shows no permission) |
| `moderex.ipban` | Ban players by IP address |
| `moderex.mute` | Mute players permanently and temporarily |
| `moderex.tempmute` | Only temp mute players (perm mute shows no permission) |
| `moderex.ipmute` | Mute players by IP address |
| `moderex.warn` | Warn players |
| `moderex.kick` | Kick players |
| `moderex.punish` | Open the punishment GUI |

**GUI Behavior:** When opening punishment GUI, show "No Permission" for punishment types the player cannot use.

### Removing Punishments

| Permission | Description |
|------------|-------------|
| `moderex.unban` | Remove an active ban |
| `moderex.unmute` | Remove an active mute |
| `moderex.unwarn` | Remove an active warning |
| `moderex.clearwarnings` | Clear all warnings for a player |

**Note:** These commands log the action to the database as a pardon with case ID tracking.

### Punishment Modifiers

| Permission | Description |
|------------|-------------|
| `moderex.punish.delete` | Delete punishments from database and player history |
| `moderex.punish.modify` | Edit punishments via case ID, open edit GUI, save changes |

---

## Punishment Flag Permissions

| Permission | Description |
|------------|-------------|
| `moderex.flag.silent` | Use -s flag for silent punishments |
| `moderex.flag.extrasilent` | Use -es flag for extra silent punishments |
| `moderex.flag.public` | Use -p flag for public announcements |
| `moderex.flag.global` | Use -g flag for global (cross-server) punishments |
| `moderex.flag.hidden` | Use -h flag to hide from punishment lists |
| `moderex.flag.skip` | Use -skip flag to skip confirmation |

---

## History, Log and Player Information Permissions

### Punishment History

| Permission | Description |
|------------|-------------|
| `moderex.history.*` | View all punishment history for a player |
| `moderex.history.warns` | Only view warnings a player has |
| `moderex.history.kicks` | Only view kicks a player has |
| `moderex.history.bans` | Only view bans a player has |
| `moderex.history.mutes` | Only view mutes a player has |
| `moderex.history.pardons` | View pardon/revocation history |

**Commands:** `/history`, `/checkbans`, `/checkmutes`, `/checkwarns`, `/mutelist`, etc.

### Other History Types

| Permission | Description |
|------------|-------------|
| `moderex.history.nick` | View nickname history for a player |
| `moderex.history.automod` | View automod flag logs for a player |
| `moderex.history.commands` | View past commands ran by a player |
| `moderex.history.chat` | View past chat history for a player |

### Player Information

| Permission | Description |
|------------|-------------|
| `moderex.info.ip` | View IP address of a player |
| `moderex.info.uuid` | View UUID of a player |
| `moderex.info.nick` | View current nickname of a player |
| `moderex.info.joindate` | View when the player first joined |
| `moderex.info.time` | View how long the player has been online/offline |
| `moderex.info.namehistory` | View previous usernames for a player |

### Information Commands

| Permission | Description |
|------------|-------------|
| `moderex.command.seen` | Use /seen command and view player info |
| `moderex.command.lastuuid` | Display player's UUID |
| `moderex.command.viewpunishment` | View detailed punishment info by case ID |
| `moderex.ipreport` | Generate IP reports |
| `moderex.geoip` | View geographic IP information |
| `moderex.dupeip` | Check for duplicate IPs |

### Staff History

| Permission | Description |
|------------|-------------|
| `moderex.staffhistory` | View punishments issued by a staff member (includes modlog) |

---

## Watchlist Permissions

| Permission | Description |
|------------|-------------|
| `moderex.watchlist.add` | Add players to the watchlist |
| `moderex.watchlist.remove` | Remove players from the watchlist |
| `moderex.history.watchlist.*` | View all watchlist history types |
| `moderex.history.watchlist.warns` | View watchlist player warning history |
| `moderex.history.watchlist.kicks` | View watchlist player kick history |
| `moderex.history.watchlist.bans` | View watchlist player ban history |
| `moderex.history.watchlist.mutes` | View watchlist player mute history |
| `moderex.history.watchlist.automod` | View watchlist player automod history |
| `moderex.history.watchlist.commands` | View watchlist player command history |
| `moderex.history.watchlist.chat` | View watchlist player chat history |

---

## Command Blacklist Permissions

| Permission | Description |
|------------|-------------|
| `moderex.cmdblacklist` | Blacklist players from using a command |
| `moderex.cmdunblacklist` | Remove command blacklist from players |

---

## Activity Log Permissions

| Permission | Description |
|------------|-------------|
| `moderex.log` | View activity log (shows entries based on history permissions) |
| `moderex.log.teleport` | Teleport to locations from activity log |

**Note:** What a player can see in the activity log is determined by their `moderex.history.*` permissions.

---

## Notification/Alert Permissions

| Permission | Description |
|------------|-------------|
| `moderex.alerts.*` | See all alerts and notifications |
| `moderex.alerts.punishments` | See punishment broadcasts |
| `moderex.alerts.automod` | See automod trigger alerts |
| `moderex.alerts.anticheat` | See anticheat alerts |
| `moderex.alerts.staffchat` | See staff chat messages |
| `moderex.alerts.silent` | See silent punishment notifications |
| `moderex.alerts.joinleave` | See player join/leave alerts |
| `moderex.alerts.watchlist` | See watchlist player alerts |
| `moderex.alerts.lag` | See server lag alerts |
| `moderex.alerts.nickname` | See nickname change alerts |
| `moderex.alerts.commands` | See command execution alerts |

### Staff Chat

| Permission | Description |
|------------|-------------|
| `moderex.command.staffchat` | Send messages to staff chat |

---

## Automod Permissions

| Permission | Description |
|------------|-------------|
| `moderex.automod.*` | Full access to automod system |
| `moderex.automod.view` | View automod configuration |
| `moderex.automod.edit` | Configure automod rules |
| `moderex.automod.create` | Create new automod rules |
| `moderex.automod.delete` | Delete automod rules |

---

## Web Panel Access

| Permission | Description |
|------------|-------------|
| `moderex.webpanel` | Access to the web panel |

**Note:** This is a protected permission - OPs do not automatically have it.

### Web Panel - Punishment Form

| Permission | Web Panel Behavior |
|------------|-------------------|
| `moderex.punish` | Open punishment form (any type) |
| `moderex.ban` | Ban option available (permanent) |
| `moderex.tempban` | Ban option available (temp only, no "perm" duration) |
| `moderex.mute` | Mute option available (permanent) |
| `moderex.tempmute` | Mute option available (temp only, no "perm" duration) |
| `moderex.warn` | Warn option available |
| `moderex.kick` | Kick option available |

**No permissions:** Form shows "No Permission" overlay, quick action buttons are disabled.

### Web Panel - Punishment History

| Permission | Web Panel Behavior |
|------------|-------------------|
| `moderex.history.bans` | View bans in history table, show Ban filter button |
| `moderex.history.mutes` | View mutes in history table, show Mute filter button |
| `moderex.history.warns` | View warnings in history table, show Warn filter button |
| `moderex.history.kicks` | View kicks in history table, show Kick filter button |
| `moderex.history.pardons` | View pardons section in player profile |

**No permissions:** Table shows "No permission to view punishment history" message, filter buttons hidden.

### Web Panel - Punishment Details

| Permission | Web Panel Behavior |
|------------|-------------------|
| `moderex.command.viewpunishment` | View punishment details modal |
| `moderex.unban` | Revoke button for bans |
| `moderex.unmute` | Revoke button for mutes |
| `moderex.unwarn` | Remove button for warnings |
| `moderex.punish.delete` | Delete punishment button |

**No permissions:** Details button disabled, Revoke/Remove buttons show lock icon.

### Web Panel - Player Profile

| Permission | Web Panel Behavior |
|------------|-------------------|
| `moderex.ban` or `moderex.tempban` | Quick Ban button enabled |
| `moderex.mute` or `moderex.tempmute` | Quick Mute button enabled |
| `moderex.warn` | Quick Warn button enabled |
| `moderex.history.*` | View all punishment sections |
| `moderex.history.pardons` | View Pardons section |
| `moderex.info.ip` | View IP address and IP history |
| `moderex.history.nick` | View nickname history |

**No permissions:** Respective buttons show lock icon, sections show "No permission" message.

---

## Plugin Administration

| Permission | Description |
|------------|-------------|
| `moderex.reload` | Reload the plugin configuration |

---

## Vanish Permissions

| Permission | Description |
|------------|-------------|
| `moderex.command.vanish` | Vanish yourself |
| `moderex.command.vanish.others` | Vanish other players |
| `moderex.vanish.attack` | Attack while vanished |
| `moderex.vanish.break` | Break blocks while vanished |
| `moderex.vanish.place` | Place blocks while vanished |
| `moderex.vanish.chat` | Chat while vanished |
| `moderex.vanish.pickup` | Pick up items while vanished |
| `moderex.vanish.mobattack` | Be attacked by mobs while vanished |
| `moderex.vanish.flight` | Fly while vanished |
| `moderex.vanish.keepfly` | Keep flight when unvanishing |
| `moderex.vanish.spectator` | Enter spectator mode while vanished |
| `moderex.vanish.level.<level>` | Access specific vanish level |
| `moderex.vanish.see.level.<level>` | See players vanished at specific level |

---

## Disguise Permissions

| Permission | Description |
|------------|-------------|
| `moderex.command.disguise` | Use disguise command |
| `moderex.disguise.rank.<rank>` | Access specific disguise rank |

---

## Bypass Permissions

| Permission | Description |
|------------|-------------|
| `moderex.bypass.*` | Bypass all restrictions |
| `moderex.bypass.automod` | Bypass all automod flags |
| `moderex.bypass.lockdown` | Bypass server lockdown |
| `moderex.bypass.chatdisable` | Bypass chat disable |
| `moderex.bypass.chatslowmode` | Bypass chat slowmode |
| `moderex.bypass.mute` | Bypass mute restrictions |
| `moderex.bypass.afk` | Bypass AFK detection |

---

## Permission Checking Order

1. Check if player is OP (bypass all except protected permissions)
2. Check for wildcard permission (`moderex.*`)
3. Check for category wildcard (`moderex.command.*`, etc.)
4. Check for specific permission

---

## Web Panel Permission Sync

Permissions are synced to the web panel via LuckPerms API. The web panel checks permissions before:
- Displaying sensitive information (IP, UUID, etc.)
- Allowing punishment actions
- Showing history/logs
- Enabling configuration changes

---

## Adding New Permissions

When adding a new permission:
1. Add it to this document in the appropriate section
2. Update the corresponding command in `commands/` directory
3. Update any related GUI classes in `gui/` directory
4. Update `HybridPanelServer.java` for web panel enforcement
5. Update `app.js` for frontend permission checks
6. Update `PermissionUtil.java` if special handling is needed
