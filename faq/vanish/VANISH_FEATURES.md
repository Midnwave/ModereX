# ModereX Advanced Vanish System

## Overview

ModereX now includes a comprehensive vanish system with level-based visibility, plugin hooks for join/leave messages, LuckPerms integration, and admin commands to manage vanished players.

## Features

### 🎯 Level-Based Visibility System

The level-based visibility system allows staff members to see vanished players based on their permission level.

#### How It Works

- Each player has a **vanish level** determined by their permissions
- Each player has a **see level** that determines which vanished players they can see
- Staff can only see vanished players with a **lower vanish level** than their see level
- Opped players have level 100 by default and can see everyone

#### Permission Format

**Vanish Level:**
- `moderex.vanish.level.<number>` - Sets the player's vanish level
- Example: `moderex.vanish.level.3` gives vanish level 3

**See Level:**
- `moderex.vanish.see.level.<number>` - Allows seeing vanished players up to that level
- Example: `moderex.vanish.see.level.2` allows seeing vanished players with level 1 or 2
- If not set, defaults to `(vanish level - 1)`

#### Example Scenarios

**Scenario 1: Moderator vs Admin**
- Moderator has `moderex.vanish.level.1`
- Admin has `moderex.vanish.level.3`
- When Moderator vanishes (level 1), Admin can see them
- When Admin vanishes (level 3), Moderator cannot see them

**Scenario 2: Custom See Level**
- Player has `moderex.vanish.level.2` and `moderex.vanish.see.level.3`
- They can vanish at level 2
- They can see vanished players up to level 3

**Scenario 3: Opped Players**
- Opped players automatically get level 100
- They can see all vanished players regardless of level

### 📢 Fake Join/Leave Messages

When enabled, ModereX can send fake join/leave messages to non-staff players when someone vanishes or unvanishes, making it appear as if they logged out/in.

#### Configuration

```yaml
vanish:
  fake-messages:
    enabled: false
    join-message: '&e{player} joined the game'
    leave-message: '&e{player} left the game'
```

#### How It Works

- When a player **vanishes**: A fake leave message is broadcast to all non-staff players
- When a player **unvanishes**: A fake join message is broadcast to all non-staff players
- Staff members with `moderex.command.vanish` permission don't see these messages

### 🔌 Plugin Hooks

ModereX can hook into popular join/leave message plugins to suppress messages for vanished players.

#### Supported Plugins

1. **EssentialsX**
2. **custom-join-messages** ([GitHub](https://github.com/Insprill/custom-join-messages))
3. **AlonsoJoin** ([GitHub](https://github.com/AlonsoAliaga/AlonsoJoin))

#### Configuration

```yaml
vanish:
  plugin-hooks:
    essentialsx:
      enabled: false
      hide-messages: true

    custom-join-messages:
      enabled: false
      hide-messages: true

    alonsojoin:
      enabled: false
      hide-messages: true
```

#### How It Works

- When enabled, ModereX registers event listeners for the target plugin
- When a vanished player joins/leaves, the message is suppressed at highest priority
- Works alongside the fake message system

### 🎨 LuckPerms Integration

ModereX can automatically modify player prefixes and suffixes when they vanish.

#### Configuration

```yaml
vanish:
  luckperms:
    enabled: false
    vanish-prefix: '&7[V] '
    vanish-suffix: ''
    modify-tab: true
```

#### How It Works

- When a player **vanishes**: The configured prefix/suffix is added (priority 100)
- When a player **unvanishes**: The temporary prefix/suffix is removed
- Supports color codes and placeholders
- Optionally modifies tab list display

#### Example

With `vanish-prefix: '&7[V] '`:
- Normal: `[Admin] Steve`
- Vanished: `[V] [Admin] Steve`

### 📋 Vanish List Command

View all vanished players with detailed information.

#### Command Usage

```
/vanish list
```

#### Features

- Shows vanished players visible to you (respects level-based visibility)
- Displays vanish level for each player
- Shows how long they've been vanished
- Configurable format

#### Configuration

```yaml
vanish:
  list:
    respect-visibility: true  # Only show players you can see
    show-levels: true          # Display vanish levels
    format: '&7- &f{player} &8(Level {level})'
```

#### Placeholders

- `{player}` - Player name
- `{level}` - Vanish level
- `{time}` - Duration (e.g., "5m 30s", "2h 15m", "1d 3h")

### 🛠️ Admin Commands

Comprehensive commands to manage vanished players.

#### Command Syntax

```bash
/vanish                          # Toggle vanish for yourself
/vanish <player>                 # Toggle vanish for a player
/vanish toggle <player>          # Toggle vanish for a player
/vanish enable <player>          # Enable vanish for a player
/vanish disable <player>         # Disable vanish for a player
/vanish on <player>              # Alias for enable
/vanish off <player>             # Alias for disable
/vanish player <player>          # Alias for toggle
/vanish list                     # List vanished players
```

#### Permissions

- `moderex.command.vanish` - Use /vanish on yourself
- `moderex.command.vanish.others` - Use /vanish on other players

#### Tab Completion

All commands support tab completion for:
- Subcommands (toggle, enable, disable, player, list)
- Online player names (respects permissions)

## Configuration Reference

### Complete Config Section

```yaml
vanish:
  # Hide vanished players from tab list
  hide-from-tablist: true

  # Open containers silently (no animation/sound for others)
  silent-containers: true

  # Disable footstep particles/sounds
  no-footsteps: true

  # Use packet-level vanishing (NMS) instead of Bukkit hidePlayer()
  use-packet-level: true

  # Debug packet filtering (log filtered packets [very verbose])
  debug-packet-filtering: false

  # Plugin API whitelist
  api-whitelist: []

  # ============================================
  # FAKE JOIN/LEAVE MESSAGES
  # ============================================
  fake-messages:
    enabled: false
    join-message: '&e{player} joined the game'
    leave-message: '&e{player} left the game'

  # ============================================
  # PLUGIN HOOKS
  # ============================================
  plugin-hooks:
    essentialsx:
      enabled: false
      hide-messages: true

    custom-join-messages:
      enabled: false
      hide-messages: true

    alonsojoin:
      enabled: false
      hide-messages: true

  # ============================================
  # LEVEL-BASED VISIBILITY SYSTEM
  # ============================================
  levels:
    enabled: true
    default-level: 1
    op-level: 100
    staff-visibility:
      can-see-lower-levels: true

  # ============================================
  # LUCKPERMS INTEGRATION
  # ============================================
  luckperms:
    enabled: false
    vanish-prefix: '&7[V] '
    vanish-suffix: ''
    modify-tab: true

  # ============================================
  # VANISH LIST COMMAND
  # ============================================
  list:
    respect-visibility: true
    show-levels: true
    format: '&7- &f{player} &8(Level {level})'
```

## Permission Nodes

### Basic Permissions

```yaml
moderex.command.vanish           # Use /vanish on yourself
moderex.command.vanish.others    # Use /vanish on other players
```

### Level-Based Permissions

```yaml
# Vanish Level (determines your vanish level)
moderex.vanish.level.1
moderex.vanish.level.2
moderex.vanish.level.3
# ... up to any number

# See Level (determines who you can see)
moderex.vanish.see.level.1
moderex.vanish.see.level.2
moderex.vanish.see.level.3
# ... up to any number
```

### Example Permission Setup

**Helper (Level 1 Vanish):**
```yaml
permissions:
  - moderex.command.vanish
  - moderex.vanish.level.1
```

**Moderator (Level 2 Vanish, See Level 1):**
```yaml
permissions:
  - moderex.command.vanish
  - moderex.command.vanish.others
  - moderex.vanish.level.2
  - moderex.vanish.see.level.1
```

**Admin (Level 3 Vanish, See Level 2):**
```yaml
permissions:
  - moderex.command.vanish
  - moderex.command.vanish.others
  - moderex.vanish.level.3
  - moderex.vanish.see.level.2
```

**Owner (Op - Level 100):**
- Automatically gets level 100 from being opped
- Can see all vanished players

## Usage Examples

### Example 1: Setup Staff Hierarchy

**Goal:** Create a 3-tier staff system where:
- Helpers (level 1) can vanish but can't see other vanished staff
- Mods (level 2) can see vanished helpers
- Admins (level 3) can see vanished helpers and mods

**Setup:**

```bash
# Helpers
lp group helper permission set moderex.vanish.level.1

# Moderators
lp group moderator permission set moderex.vanish.level.2
lp group moderator permission set moderex.vanish.see.level.1

# Admins
lp group admin permission set moderex.vanish.level.3
lp group admin permission set moderex.vanish.see.level.2
```

### Example 2: Fake Join/Leave Messages

**Goal:** Make it appear like staff members are logging out when they vanish

**Setup:**

```yaml
vanish:
  fake-messages:
    enabled: true
    join-message: '&e{player} joined the game'
    leave-message: '&e{player} left the game'
```

**Result:**
- When a staff member types `/vanish`, regular players see: `Steve left the game`
- When they unvanish, regular players see: `Steve joined the game`

### Example 3: EssentialsX Integration

**Goal:** Suppress EssentialsX join messages for vanished players

**Setup:**

```yaml
vanish:
  plugin-hooks:
    essentialsx:
      enabled: true
      hide-messages: true
```

**Result:**
- Vanished players joining/leaving don't trigger EssentialsX messages
- Works alongside fake messages if enabled

### Example 4: LuckPerms Vanish Indicator

**Goal:** Add a [V] prefix to vanished players in chat

**Setup:**

```yaml
vanish:
  luckperms:
    enabled: true
    vanish-prefix: '&7[V] '
    modify-tab: true
```

**Result:**
- Normal: `[Admin] Steve: hello`
- Vanished: `[V] [Admin] Steve: hello`

## API Usage

### For Plugin Developers

If you're developing a plugin that needs to check vanish status:

```java
import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.staff.VanishManager;

// Get the vanish manager
VanishManager vanishManager = ModereX.getInstance().getVanishManager();

// Check if a player is vanished
boolean isVanished = vanishManager.isVanished(player);

// Check if observer can see target
boolean canSee = vanishManager.canSeeVanished(observer, target);

// Get vanish level
int level = vanishManager.getVanishLevel(player);

// Get visible vanished players
List<Player> visible = vanishManager.getVisibleVanishedPlayers(observer);
```

## Technical Details

### Classes

**Core Classes:**
- `VanishManager` - Main vanish logic with level-based visibility
- `VanishLevel` - Handles level calculations and visibility checks
- `VanishPluginHookManager` - Manages plugin integration hooks
- `VanishCommand` - Command handler with all subcommands

**Plugin Hooks:**
- `VanishPluginHook` - Interface for plugin hooks
- `AbstractVanishPluginHook` - Base implementation
- `EssentialsXHook` - EssentialsX integration
- `CustomJoinMessagesHook` - custom-join-messages integration
- `AlonsoJoinHook` - AlonsoJoin integration

### Features Summary

✅ Level-based visibility system with permission nodes
✅ Fake join/leave messages for non-staff players
✅ Plugin hooks for EssentialsX, custom-join-messages, AlonsoJoin
✅ LuckPerms prefix/suffix integration
✅ Admin commands (toggle, enable, disable, list)
✅ Vanish list with duration tracking
✅ Full tab completion support
✅ Respects packet-level vanishing
✅ Configurable visibility rules
✅ Staff notification system

## Migration Guide

### From Basic Vanish

If you were using the basic `/vanish` command:

1. **No changes required** - `/vanish` still works the same for toggling
2. **New features available** - Enable fake messages, plugin hooks, or LuckPerms as needed
3. **Add level permissions** - Grant `moderex.vanish.level.X` to create hierarchy

### From Other Vanish Plugins

**From SuperVanish/PremiumVanish:**
1. Remove the old plugin
2. Configure ModereX vanish settings
3. Set up level-based permissions if desired
4. Enable plugin hooks if you use EssentialsX/custom join messages

**Advantages:**
- No external dependencies (no ProtocolLib needed)
- More powerful level-based visibility
- Better integration with ModereX punishment system
- Plugin hook support for join messages

## Troubleshooting

### Issue: Staff can't see each other when vanished

**Solution:** Grant appropriate see level permissions:
```bash
lp user <player> permission set moderex.vanish.see.level.X
```

### Issue: Fake messages not working

**Check:**
1. Is `fake-messages.enabled` set to `true`?
2. Are the message formats correct?
3. Are staff members seeing the messages? (They shouldn't)

### Issue: Plugin hook not working

**Check:**
1. Is the target plugin installed?
2. Is the hook enabled in config?
3. Check console for "[VanishHook]" messages
4. Verify the plugin name matches exactly

### Issue: LuckPerms prefix not applying

**Check:**
1. Is LuckPerms installed?
2. Is `luckperms.enabled` set to `true`?
3. Check console for prefix application messages
4. Verify LuckPerms is using meta priorities correctly

## Support

For issues or questions:
- GitHub: https://github.com/blockforge/moderex
- Documentation: See VANISH_API.md for detailed API info

---

**Built by BlockForge Studios for ModereX**
