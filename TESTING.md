# ModereX Developer Testing Checklist

This document provides a comprehensive testing checklist for verifying ModereX functionality and database synchronization.

## Prerequisites

1. **Test Server Setup**
   - Paper/Spigot 1.21+ server
   - ModereX plugin installed
   - At least one anticheat installed (GrimAC recommended)
   - LuckPerms for permission testing (optional)

2. **Database Configuration**
   - SQLite (default): No additional setup needed
   - MySQL: Configure `config.yml` with database credentials

3. **Web Panel Access**
   - Note the WebSocket port (default: 8080)
   - Generate connect code with `/mx connect`

---

## Core Functionality Tests

### Punishment System

- [ ] **Mute Command**
  - `/mute <player> <duration> <reason>` creates mute
  - Muted player cannot chat
  - Muted player can use signs (if configured)
  - Mute expires after duration
  - Database: Check `moderex_punishments` table

- [ ] **Ban Command**
  - `/ban <player> <duration> <reason>` creates ban
  - Banned player cannot join server
  - Ban message shows case ID and appeal info
  - Ban expires after duration
  - Database: Check `moderex_punishments` table

- [ ] **Warn Command**
  - `/warn <player> <reason>` creates warning
  - Warning notification sent to player
  - Warning history accessible via `/history`
  - Database: Check `moderex_punishments` table (type = WARN)

- [ ] **Kick Command**
  - `/kick <player> <reason>` removes player
  - Disconnect message shows reason
  - Kick logged in punishment history

- [ ] **IP Ban Command**
  - `/ipban <player> <duration> <reason>` creates IP ban
  - All accounts on that IP blocked
  - Database: Check IP stored correctly

- [ ] **Punishment Removal**
  - `/unmute <player>` removes active mute
  - `/unban <player>` removes active ban
  - `/clearwarnings <player>` removes warnings
  - Original records retained with `removed` flag

### Staff Settings

- [ ] **Staff Settings GUI** (`/mx settings`)
  - All tabs render correctly (Notifications, Alerts, Anticheat, Personal, Vanish)
  - Settings save to database when clicking "Save"
  - Settings persist after server restart

- [ ] **Per-Check Alert Preferences**
  - Navigate to Anticheat tab > My Alert Preferences
  - Configure individual check thresholds
  - Verify alerts respect configured thresholds
  - Web panel shows same preferences

- [ ] **Private Message Monitoring**
  - Enable PM alerts in Staff Settings
  - Verify staff receives PM notifications
  - Watchlist players show [WL] prefix

---

## Database Sync Tests

### SQLite

```sql
-- Verify tables exist
.tables

-- Check punishments
SELECT * FROM moderex_punishments ORDER BY created_at DESC LIMIT 10;

-- Check staff settings
SELECT * FROM moderex_staff_settings;

-- Check automod rules
SELECT * FROM moderex_automod_rules;

-- Check anticheat rules
SELECT * FROM moderex_anticheat_rules;
```

### MySQL

```sql
-- Verify tables exist
SHOW TABLES LIKE 'moderex_%';

-- Check punishments
SELECT * FROM moderex_punishments ORDER BY created_at DESC LIMIT 10;

-- Check staff settings
SELECT uuid, updated_at FROM moderex_staff_settings;

-- Verify JSON settings
SELECT uuid, JSON_EXTRACT(settings, '$.anticheatAlerts') as ac_alerts
FROM moderex_staff_settings;
```

### Cross-Server Sync (BungeeCord/Velocity)

- [ ] Punishment on Server A reflects on Server B
- [ ] Staff settings sync across servers
- [ ] Staff chat messages relay to all servers
- [ ] Vanish state syncs across servers

---

## Web Panel Tests

### Connection

- [ ] `/mx connect` generates 6-character code
- [ ] Code expires after 5 minutes
- [ ] Valid code authenticates successfully
- [ ] Invalid/expired code shows error

### Real-time Updates

- [ ] Punishments appear in web panel immediately
- [ ] Staff settings changes sync to game
- [ ] Anticheat alerts show in real-time
- [ ] Player activity updates live

### Settings Sync

- [ ] Change alert preference in web panel
- [ ] Verify change in Staff Settings GUI
- [ ] Change setting in-game
- [ ] Verify change reflects in web panel

---

## Automod Tests

### Chat Filters

- [ ] **Spam Protection**
  - Rapid messages trigger spam filter
  - Similar messages detected
  - Configuration changes apply immediately

- [ ] **Caps Filter**
  - Excessive CAPS converted to lowercase
  - Minimum length respected
  - Percentage threshold configurable

- [ ] **Word Filter**
  - Blacklisted words blocked/replaced
  - Exclusion phrases work correctly
  - Regex patterns supported

### Anticheat Rules

- [ ] **Rule Creation** (via Automod GUI)
  - Select anticheat and check
  - Configure threshold and time window
  - Set auto-punishment type and duration

- [ ] **Rule Triggering**
  - Alerts count toward threshold
  - Auto-punishment executes at threshold
  - Violation count resets after time window

- [ ] **Shift-Click Alert Settings**
  - Shift+click on anticheat rule opens alert settings
  - Can configure personal alert level
  - Presets (High/Medium/Low) apply correctly

---

## Anticheat Integration Tests

### Alert Processing

- [ ] Alerts from GrimAC captured correctly
- [ ] Check name and VL extracted
- [ ] Alert respects staff notification preferences
- [ ] Minimum VL threshold filters low-level alerts

### External Anticheat API

```java
// Test external registration
ExternalAnticheatProvider provider = new TestAnticheatProvider();
ModereXAPI.getInstance().registerAnticheat(provider);

// Verify checks registered
// Check alerts flow through system
```

---

## Command Tests

### Utility Commands

- [ ] `/seen <player>` shows player info
- [ ] `/history <player>` shows punishment history
- [ ] `/modlog <player>` shows mod log with pagination
- [ ] `/modlog <player> -staff` shows actions BY staff
- [ ] `/mx commandhistory <player>` with clickable navigation
- [ ] `/check <player>` opens check GUI

### Tab Completion

- [ ] `/viewpunishment` shows recent case IDs
- [ ] Case IDs filter by typed prefix
- [ ] All commands show appropriate completions

---

## GUI Tests

### Pagination

- [ ] ModLog GUI pages correctly
- [ ] Automod GUI pages correctly
- [ ] Anticheat Rules GUI pages correctly
- [ ] Page navigation buttons work

### Item Interactions

- [ ] Left-click actions work
- [ ] Right-click actions work
- [ ] Shift-click actions work
- [ ] Sound feedback plays

---

## Performance Tests

### Large Dataset

- [ ] Load 1000+ punishments, verify GUI responsiveness
- [ ] Check database query performance
- [ ] Verify async operations don't block main thread

### Memory

- [ ] No memory leaks after extended use
- [ ] GUI listeners properly cleaned up
- [ ] WebSocket connections properly closed

---

## Cleanup Verification

After testing, verify:

- [ ] Test punishments can be removed
- [ ] Test players can be unbanned
- [ ] Database integrity maintained
- [ ] No orphaned records

---

## Test Data Scripts

### Create Test Punishments

```bash
# In-game commands
/mute TestPlayer 1h Test mute
/warn TestPlayer Test warning
/ban TestPlayer 1d Test ban
```

### Verify Database

```bash
# SQLite
sqlite3 plugins/ModereX/database.db "SELECT COUNT(*) FROM moderex_punishments"

# MySQL
mysql -e "SELECT COUNT(*) FROM moderex_punishments" moderex_db
```

---

## Reporting Issues

When reporting bugs, include:
1. Server version (Paper/Spigot + MC version)
2. ModereX version
3. Database type (SQLite/MySQL)
4. Relevant console errors
5. Steps to reproduce
6. Expected vs actual behavior

---

**Copyright (c) 2026 BlockForge Studios & ADF Industries**

- BlockForge Discord: https://discord.gg/jQGMhKA5m6
- ADF Industries Discord: https://discord.gg/qWpcRmDW2P
