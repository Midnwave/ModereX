# Changelog

All notable changes to ModereX will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- **External Anticheat API**: Third-party anticheat plugins can now register their checks with ModereX
  - `ExternalAnticheatProvider` interface for integration
  - `AnticheatCheck` class for defining custom checks
  - `AnticheatViolation` class for reporting violations
  - `AnticheatRegistry` for managing external providers
  - Full documentation with usage examples

- **Private Message Monitoring**: Staff can now monitor player private messages
  - New setting in Staff Settings GUI (Notifications tab)
  - Web panel integration with Private Message Alerts dropdown
  - Configurable levels: Everyone, Watchlist Only, Off
  - Shows `[WL]` prefix for watched players

- **New `/seen` Command**: View comprehensive player information
  - Shows player online status, UUID, first join, and last seen
  - Displays IP address (with permission)
  - Shows active punishment status and history summary
  - Quick action buttons for `/check`, `/history`, `/mx commandhistory`
  - Aliases: `/playerinfo`, `/lastseen`

- **Clickable Page Navigation**: WorldGuard-style navigation for paginated commands
  - `/mx commandhistory` - [◀ Prev] [Next ▶] clickable buttons
  - `/modlog` - Same navigation with hover tooltips

- **Staff Mode for ModLog**: View actions BY a staff member
  - Use `/modlog <player> -staff` to see punishments issued by that staff
  - Clickable case IDs that link to `/viewpunishment`
  - Color-coded status indicators (Active/Expired/Removed)

- **Configurable Join/Leave Messages**: Global server setting for join/leave visibility
  - Options: ALL, MODERATORS_ONLY, OFF
  - Moved from per-staff setting to global config
  - Vanilla join/leave message suppression option

- **Startup Banner**: Added copyright and developer info to console startup
  - Shows version, copyright, and Discord links
  - BlockForge Studios and ADF Industries credits

### Changed
- **Punishment Broadcasts**: Updated to include ModereX prefix and case IDs
  - New format: `[ModereX] <staff> muted <player> for <duration> | Case: <case_id>`
  - Reason shown on second line
  - Improved color formatting with gradients

- **Tab Completion**: Case IDs now show actual recent cases instead of just "MX-"
  - ViewPunishment command shows up to 50 recent case IDs
  - Filtered by typing prefix

- **Punishment History Truncation**: Increased reason display from 30 to 40 characters
  - Applies to ModLog GUI and Analytics GUI

- **Join/Leave Priority**: Watchlist messages now take priority over regular join/leave

### Fixed
- **GrimAC Rules Registration**: Anticheat rules now pre-register on plugin load
  - Fixed issue where rules wouldn't appear until first alert
  - All known checks registered immediately when anticheat hooks

- **Staff GUI**: Removed redundant join/leave personal setting
  - Now controlled by global server config instead

- **Web Panel Sync**: Staff settings properly sync between game and web panel
  - Removed obsolete joinLeaveMessages setting from frontend

### Removed
- Per-staff join/leave message setting (replaced by global config)

## [1.0.0] - Initial Release

### Features
- Comprehensive punishment system (Ban, Mute, Warn, Kick, IP-based)
- Web Panel with real-time WebSocket connection
- Automod with configurable rules and chat filtering
- Anticheat integration (Grim, Vulcan, Matrix, Spartan, NCP, Themis, etc.)
- Staff tools (Vanish, Disguise, Staff Chat, Staff Mode)
- Watchlist system for monitoring suspicious players
- Replay recording for player activity
- Multi-language support
- SQLite and MySQL database support
- BungeeCord/Velocity proxy support
- PlaceholderAPI and LuckPerms integration

---

**Copyright (c) 2026 BlockForge Studios & ADF Industries**

- BlockForge Discord: https://discord.gg/jQGMhKA5m6
- ADF Industries Discord: https://discord.gg/qWpcRmDW2P
