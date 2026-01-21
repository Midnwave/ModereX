# ModereX Moderation Commands Implementation Guide

## Overview

This document describes the complete moderation command system for ModereX 2.0, including the flag-based architecture and comprehensive command suite.

## ✅ Completed Components

### 1. Core Utilities

#### **FlagParser** (`util/FlagParser.java`)
- Parses command flags from arguments
- Supports short flags: `-s`, `-I`, `-g`, etc.
- Supports long flags: `--delete`, `--sender=value`, etc.
- Handles flag values with colons: `-s:true`, `-s:$silent`
- Supports `--` to ignore flags after that point
- Provides convenience methods for all flag types

#### **TargetResolver** (`util/TargetResolver.java`)
- Resolves multiple target formats:
  - Player names (online/offline)
  - UUIDs (32 or 36 character format)
  - IP addresses (including wildcards like `192.168.1.*`)
  - Punishment IDs (numeric)
- Returns target type and appropriate identifiers
- Handles UUID lookup for offline players

#### **DurationParser** (`util/DurationParser.java`)
- Supports time units: `s`, `m`, `h`, `d`, `w`, `mo`, `y`
- Example: `1y6mo2w3d` = 1 year, 6 months, 2 weeks, 3 days
- Handles "permanent" keyword for indefinite punishments

#### **PunishmentContext** (`commands/moderation/base/PunishmentContext.java`)
- Builder pattern for punishment command context
- Holds target, flags, duration, reason, executor information
- Provides convenient accessor methods for all context data

#### **PunishmentCommandBase** (`commands/moderation/base/PunishmentCommandBase.java`)
- Base class for all punishment commands
- Provides common flag permission checking
- Reduces code duplication across commands
- Handles console-only flag validation

#### **TabCompletionHelper** (`commands/moderation/base/TabCompletionHelper.java`)
- Provides reusable tab completion for punishment commands
- Suggests player names, durations, flags
- Supports custom completion providers

### 2. Completed Punishment Commands

#### Core Punishment Commands
- ✅ **BanCommand** - `/ban <player> [duration] [reason] [flags]`
  - Permanent or temporary bans
  - Supports all standard flags
  - Template integration
- ✅ **MuteCommand** - `/mute <player> [duration] [reason] [flags]`
  - Permanent or temporary mutes
  - Prevents player from chatting
- ✅ **WarnCommand** - `/warn <player> [duration] [reason] [flags]`
  - Issues warnings to players
  - Tracks warning count and expiration
- ✅ **KickCommand** - `/kick <player> [reason] [flags]`
  - Removes player from server with message

#### Temporary Punishment Variants
- ✅ **TempBanCommand** - `/tempban <player> <duration> [reason] [flags]`
  - Requires duration parameter
  - Convenient shorthand for temporary bans
- ✅ **TempMuteCommand** - `/tempmute <player> <duration> [reason] [flags]`
  - Requires duration parameter
  - Convenient shorthand for temporary mutes

#### IP-Based Punishment Commands
- ✅ **IPBanCommand** - `/ipban <player> [duration] [reason] [flags]`
  - Bans both UUID and IP address
  - Alias: `/banip`, `/ban-ip`
- ✅ **IPMuteCommand** - `/ipmute <player> [duration] [reason] [flags]`
  - Mutes both UUID and IP address
  - Alias: `/muteip`

#### Unpunishment Commands
- ✅ **UnbanCommand** - `/unban <player|id> [reason] [flags]`
  - Removes active bans
  - Supports target by player name, UUID, or punishment ID
- ✅ **UnmuteCommand** - `/unmute <player|id> [reason] [flags]`
  - Removes active mutes
- ✅ **UnwarnCommand** - `/unwarn <player|id> [reason] [flags]`
  - Removes warnings

#### Template-Based GUI Command
- ✅ **PunishCommand** - `/punish [player]`
  - Opens GUI for selecting punishment templates
  - If player specified, opens direct punishment GUI
  - GUI-based template selection

### 3. Completed Check Commands

- ✅ **CheckCommand** - `/check <player>`
  - Comprehensive player info display
  - Shows active ban, mute, and warning status
  - Displays UUID, IP address, region
  - Shows alt account count and total punishments
- ✅ **CheckBanCommand** - `/checkban <player>`
  - Shows detailed ban status
  - Displays ban details if active
- ✅ **CheckMuteCommand** - `/checkmute <player>`
  - Shows detailed mute status
- ✅ **CheckWarnCommand** - `/checkwarn <player>`
  - Shows all active warnings with details

### 4. Completed History Commands

- ✅ **HistoryCommand** - `/history <player> [type] [page]`
  - Shows complete punishment history
  - Optional type filter (ban, mute, warn, kick)
  - Paginated results
- ✅ **StaffHistoryCommand** - `/staffhistory <staff> [type] [page]`
  - Shows punishments issued by a staff member
  - Tracks staff moderation actions
- ✅ **IPHistoryCommand** - `/iphistory <player>`
  - Shows IP address history for a player
  - Console-only by default for privacy
- ✅ **NameHistoryCommand** - `/namehistory <player>`
  - Displays previous usernames
  - Uses Mojang API for lookup
- ✅ **WarningsCommand** - `/warnings [player]`
  - Lists active warnings
  - If no player specified, shows own warnings

### 5. Completed List Commands

- ✅ **BanListCommand** - `/banlist [page]`
  - Lists all active bans
  - Paginated display
- ✅ **MuteListCommand** - `/mutelist [page]`
  - Lists all active mutes
- ✅ **WarnListCommand** - `/warnlist [page]`
  - Lists all active warnings

### 6. Completed Account & IP Commands

- ✅ **DupeIPCommand** - `/dupeip <player|ip>`
  - Shows alt accounts for a player
  - Aliases: `/alts`, `/checkalts`
  - Displays shared IP addresses
- ✅ **IPReportCommand** - `/ipreport`
  - Shows duplicate IPs among online players
  - Useful for finding alts during sessions
- ✅ **GeoIPCommand** - `/geoip <player>`
  - **FULLY IMPLEMENTED** with MaxMind GeoLite2 integration
  - Displays country, region, city with flag emoji
  - Requires GeoLite2-City.mmdb database
- ✅ **LastUUIDCommand** - `/lastuuid <player>`
  - Displays player's UUID
  - Works with offline players

### 7. Completed Admin Commands

- ✅ **ClearWarningsCommand** - `/clearwarnings <player> [--confirm]`
  - Removes all warnings for a player
  - Requires confirmation flag
- ✅ **KickAllCommand** - `/kickall [reason] [flags]`
  - Kicks all players from server
  - Excludes staff with bypass permission
- ✅ **LockdownCommand** - `/lockdown [scope] [end]`
  - Enables/disables server lockdown
  - Scopes: `local` (this server) or `global` (all servers)
  - Prevents non-staff from joining
- ✅ **ModLogCommand** - `/modlog <player> [page]`
  - Opens moderation log GUI or displays in chat
  - Shows comprehensive punishment history
- ✅ **PruneHistoryCommand** - `/prunehistory <player> [duration] [--confirm]`
  - Removes old/inactive punishments
  - Optional duration filter (default: all inactive)
- ✅ **StaffRollbackCommand** - `/staffrollback <staff> [duration] [--confirm]`
  - Reverts all punishments by a staff member
  - Optional time window (e.g., last 24h)

### 8. ModereX Main Command Subcommands

- ✅ **ModereXCommand** - `/moderex <subcommand>`
  - Main administrative command hub

#### Implemented Subcommands:
- ✅ `/moderex allow <add|check|remove> <player>` - Manage bypass list
- ✅ `/moderex unlink <player>` - Remove IP associations
- ✅ `/moderex reload` - Reload configurations
- ✅ `/moderex info` - Database connection pool stats
- ✅ `/moderex servers` - List connected servers (proxy mode)
- ✅ `/moderex reveal <ID>` - Convert randomized punishment ID
- ✅ `/moderex broadcast <message>` - Broadcast to all servers
- ✅ `/moderex timezone [timezone]` - View/set timezone
- ✅ `/moderex reset-database [--confirm]` - Clear all data (console only)
- ✅ `/moderex reset-templates [player] [--confirm]` - Reset template progression
- ✅ `/moderex add-login <player>` - Add login records
- ✅ `/moderex accept` - Accept disclaimer (first time setup)
- ✅ `/moderex license` - Show license information
- ✅ `/moderex upgrade` - Force database schema upgrade

### 9. Utility Commands

- ✅ **VanishCommand** - `/vanish [player] [level]`
  - Toggle vanish mode for staff
  - Supports vanish levels
- ✅ **StaffChatCommand** - `/staffchat [message]`
  - Toggle staff chat or send message
  - Alias: `/sc`
- ✅ **StaffHelpCommand** - `/staffhelp`
  - Shows staff command help
- ✅ **CmdBlacklistCommand** - `/cmdblacklist <player> <command>`
  - Prevents player from using specific commands
- ✅ **CmdHistoryCommand** - `/cmdhistory <player> [page]`
  - Shows command usage history
- ✅ **CmdUnblacklistCommand** - `/cmdunblacklist <player> <command>`
  - Removes command blacklist

### 10. Disguise System Commands

- ✅ **DisguiseCommand** - `/disguise [player] [rank]`
  - Opens disguise GUI or directly disguises
  - Change name, skin, and rank
- ✅ **DisguiseNameCommand** - `/disguisename <name>`
  - Change displayed name only
- ✅ **DisguiseSkinCommand** - `/disguiseskin <player>`
  - Change skin to match another player

## Command Syntax

### General Format
```
/<punishment> <player> [<duration | reason>]{reason} [flags]
```

### Supported Flags

| Flag | Description | Permission |
|------|-------------|------------|
| `-d`, `--delete` | Delete a specific entry | `moderex.delete` / `moderex.delete.own` |
| `-g` | Global punishment (shortcut for `server:global`) | `moderex.server.global` |
| `-I` | IP-based punishment (UUID+IP) | `moderex.ipban` / `moderex.ipmute` / `moderex.ipwarn` |
| `-m`, `--modify` | Edit existing punishment in place | `moderex.modify` / `moderex.modify.own` |
| `-N` | Prevent overriding existing punishments | N/A |
| `-p` | Public punishment (override silent default) | `moderex.public` |
| `-s` | Silent punishment (suppress broadcast) | `moderex.notify.silent` |
| `-S` | Extra silent (console only) | `moderex.extrasilent` |
| `--sender=<name>` | Set custom executor name | Console only |
| `--sender-uuid=<uuid>` | Set custom executor UUID | Console only |
| `--server-origin=<server>` | Set specific server origin | `moderex.admin` |
| `--confirm` | Confirm destructive operations | N/A |
| `--hide` | Disable broadcast & notifications entirely | `moderex.admin` |
| `--skip` | Skip punishment actions | `moderex.admin` |
| `--no-queue` | Disable unban queue | N/A |
| `--` | Ignore flags after this parameter | N/A |

## Implementation Patterns

### Standard Punishment Command Pattern

All punishment commands follow this structure:

```java
public class ExampleCommand extends PunishmentCommandBase {

    public ExampleCommand(ModereX plugin) {
        super(plugin, "moderex.permission", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        FlagParser flagParser = new FlagParser(args);
        List<String> regularArgs = flagParser.getRegularArgs();

        if (regularArgs.isEmpty()) {
            sendMessage(sender, "<red>Usage: /example <player> ...");
            return;
        }

        if (!(sender instanceof ConsoleCommandSender)) {
            if (flagParser.getSender() != null || flagParser.getSenderUuid() != null) {
                sendMessage(sender, MessageKey.NO_PERMISSION);
                return;
            }
        }

        if (!checkFlagPermissions(sender, flagParser)) {
            return;
        }

        TargetResolver target = new TargetResolver(regularArgs.get(0));
        if (!target.isValid() || !target.isPlayer()) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", regularArgs.get(0));
            return;
        }

        long duration = parseDuration(regularArgs);
        String reason = parseReason(regularArgs);

        PunishmentContext context = PunishmentContext.builder(sender)
                .target(target)
                .flags(flagParser)
                .duration(duration)
                .reason(reason)
                .build();

        if (flagParser.isDelete()) {
            handleDelete(context);
            return;
        }
        if (flagParser.isModify()) {
            handleModify(context);
            return;
        }

        executePunishment(context);
    }
}
```

## Target Resolution Examples

```java
// Player name
TargetResolver target = new TargetResolver("Notch");

// UUID (32 chars)
TargetResolver target = new TargetResolver("069a79f444e94726a5befca90e38aaf5");

// UUID (36 chars with dashes)
TargetResolver target = new TargetResolver("069a79f4-44e9-4726-a5be-fca90e38aaf5");

// IP address
TargetResolver target = new TargetResolver("192.168.1.100");

// Wildcard IP
TargetResolver target = new TargetResolver("192.168.1.*");

// Punishment ID
TargetResolver target = new TargetResolver("12345");
```

## Flag Usage Examples

```bash
# Silent ban
/ban Notch 7d Hacking -s

# Global mute
/mute Notch 1d Spamming -g

# IP ban with custom sender (console only)
/ban Notch permanent Banned --sender=AutoMod -I

# Delete a punishment
/unban #12345 -d

# Modify existing punishment
/ban Notch 14d Updated reason -m

# Extra silent with server origin
/ban Notch 30d Severe violation -S --server-origin=lobby

# Multiple flags
/ban Notch 1y Cheating -g -I -s

# Ignore flags in reason
/ban Notch -- Reason with -flags in it
```

## Database Integration

All commands integrate with the database system for:

1. **Punishment Storage** - All punishments persisted to database
2. **IP Tracking** - Player IP addresses tracked for alt detection
3. **History Queries** - Fast retrieval of punishment history
4. **Template System** - Progressive punishment escalation
5. **Cross-Server Sync** - Network-wide punishment coordination (with proxy mode)
6. **Modify/Delete Operations** - Update or remove existing punishments

## Advanced Features

### Template System
- Punishment templates in `templates.yml`
- Progressive escalation (warn → kick → tempban → ban)
- Per-template progression tracking
- GUI-based template selection via `/punish`

### GeoIP Integration
- MaxMind GeoLite2 database support
- Country, region, city lookup
- Flag emoji display
- Download database from: https://dev.maxmind.com/geoip/geolite2-free-geolocation-data

### Disguise System
- Packet-level name/skin spoofing
- LuckPerms rank integration
- Persistent disguise state
- Join/quit message spoofing
- GUI-based disguise management

### Vanish System
- Multiple vanish levels
- Granular permissions (pickup, chat, break, place, attack)
- Flight toggle on vanish
- Double-sneak spectator mode
- PlaceholderAPI integration

### Proxy Support
- Cross-server punishment synchronization
- Global lockdown coordination
- Server list with player counts
- Network-wide broadcasts

## Permission Nodes

### Command Permissions
```yaml
# Core punishment commands
moderex.ban: true
moderex.mute: true
moderex.warn: true
moderex.kick: true
moderex.unban: true
moderex.unmute: true

# IP-based punishments
moderex.ipban: true
moderex.ipmute: true

# Check commands
moderex.check: true
moderex.checkban: true
moderex.checkmute: true
moderex.checkwarn: true

# History commands
moderex.history: true
moderex.staffhistory: true
moderex.iphistory: true
moderex.namehistory: true

# Admin commands
moderex.geoip: true
moderex.dupeip: true
moderex.ipreport: true
moderex.clearwarnings: true
moderex.kickall: true
moderex.lockdown: true
moderex.prunehistory: true
moderex.staffrollback: true

# Flag permissions
moderex.notify.silent: true
moderex.extrasilent: true
moderex.server.global: true
moderex.delete: true
moderex.delete.own: true
moderex.modify: true
moderex.modify.own: true
moderex.admin: true
```

## Configuration

### Required Setup

1. **Database Configuration** (`config.yml`)
   - SQLite (default) or MySQL
   - Connection pool settings

2. **GeoIP Setup** (optional)
   ```yaml
   geoip:
     enabled: true
     database-path: 'plugins/ModereX/GeoLite2-City.mmdb'
   ```

3. **Proxy Configuration** (optional)
   ```yaml
   proxy:
     enabled: true
     type: bungeecord  # or velocity
   ```

4. **Disguise Configuration** (optional)
   ```yaml
   disguise:
     enabled: true
     use-luckperms-groups: true
     persistent: true
   ```

## Testing Checklist

For each command, verify:
- ✅ Basic execution works
- ✅ All flag combinations work correctly
- ✅ Permissions are properly checked
- ✅ Tab completion works
- ✅ Error messages are clear
- ✅ Target resolution works for all formats
- ✅ Duration parsing works correctly
- ✅ Broadcasts respect silence flags
- ✅ Database persistence works
- ✅ Cross-server sync works (if applicable)

## Status Summary

**Total Commands Implemented: 50+**

- ✅ Core Punishment System (12 commands)
- ✅ Check Commands (4 commands)
- ✅ History Commands (5 commands)
- ✅ List Commands (3 commands)
- ✅ Account/IP Commands (5 commands)
- ✅ Admin Commands (7 commands)
- ✅ ModereX Subcommands (13 subcommands)
- ✅ Utility Commands (7 commands)
- ✅ Disguise Commands (3 commands)

**All planned moderation commands are now implemented and functional.**

## Notes

- All commands use CompletableFuture for async database operations
- Message keys defined in `messages/en_US.yml`
- Permission nodes documented in `plugin.yml`
- Commands support MiniMessage formatting
- Audit logging for all punishment actions
- Full PlaceholderAPI integration
- Web panel integration for remote management
