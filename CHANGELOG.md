# Changelog

All notable changes to ModereX will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Dev Build 329] - 2026-03-11

### Added
- **AI Moderation Overhaul**: Complete rewrite of the AI moderation system
  - 16 violation categories with per-category severity (NONE/LOW/MEDIUM/HIGH/CRITICAL)
  - Escalation manager with configurable tiers (Warning → Mute 5m → Mute 30m → Tempban 1h → Tempban 1d)
  - 500-message context window per player for AI pattern detection across messages
  - Dry-run mode for testing AI moderation without actually blocking content
  - 4 new moderation presets: Anarchy Server, Roleplay Server, Competitive Server, Educational Server
  - Review queue for FLAG_FOR_REVIEW items with approve/dismiss/undo actions
  - Discord webhook integration for moderation alerts (configurable severity threshold)
  - Full analytics suite: hourly breakdown, category breakdown, top offenders, risk scores, trends
  - Per-player violation history with paginated log viewer
  - Auto-detect violation categories from AI response (profanity, slurs, harassment, toxicity, etc.)

- **Skin Scanning**: Detect inappropriate player skins via Mojang API
  - Auto-scan on join (configurable)
  - Staff notifications for flagged skins
  - Cached results (1-hour TTL)
  - New permission: `moderex.ai.skin`

- **In-Game Security GUI** (`/mx security`): Full security management from in-game
  - Raid protection toggle
  - Gateway connection status
  - Emergency lockdown mode
  - Web panel token management (revoke all tokens)
  - Permission reference viewer (shows which permissions you have)
  - New permissions: `moderex.security`, `moderex.security.lockdown`, `moderex.security.tokens`

- **In-Game Rules Editor GUI** (`/mx rules`): Full CRUD for server rules
  - Create, edit, delete, and reorder rules
  - Toggle rules on/off
  - Category cycling (General, Chat, Gameplay, PvP, Building, Community)
  - Batch AI description generation for all rules
  - Re-present rules to all online players
  - New permissions: `moderex.rules.manage`, `moderex.rules.ai`

- **Gateway Staff Link Reminder**: Staff with `moderex.staff` who haven't linked their account get a reminder every 30 minutes to run `/mx link`

- **New AI Moderation Permissions**:
  - `moderex.ai.*` — Full AI moderation access
  - `moderex.ai.manage` — Configure presets, escalation, and settings
  - `moderex.ai.bypass` — Bypass all AI moderation checks
  - `moderex.ai.analytics` — View analytics and risk scores
  - `moderex.ai.review` — Access review queue
  - `moderex.ai.sandbox` — Use sandbox/dry-run testing
  - `moderex.ai.skin` — Manage skin scanning

### Changed
- **OllamaClient → AIClient**: Renamed across entire codebase to remove Ollama branding
  - Class: `OllamaClient` → `AIClient`
  - Getter: `getOllamaClient()` → `getAIClient()`
  - Gateway env vars: `OLLAMA_ENDPOINT` → `AI_ENDPOINT`, `OLLAMA_MODEL` → `AI_MODEL`, `OLLAMA_API_KEY` → `AI_API_KEY`
  - All comments and error messages updated
  - Default model unchanged: `nemotron-3-nano:30b-cloud`

- **Web Panel Sidebar Reorganized**: Merged Communication and Server groups
  - Staff Chat moved into Monitoring section
  - Server Rules moved into Configuration section
  - Cleaner sidebar with fewer top-level groups

### Fixed
- **Loading Bar Infinite Spin**: Fixed loading bar never hiding on first gateway connection
  - `connected` and `server_online` WebSocket messages now properly reset the loading bar counter
  - Also fixes the same issue on server reconnection

---

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

- **Per-Check Anticheat Alert Settings**: Customize alert notifications per anticheat check
  - Shift+click on anticheat rules in Automod GUI opens alert settings
  - Configure alert level (Everyone, Watchlist Only, Off)
  - Set threshold count and time window for batching alerts
  - Quick presets for High/Medium/Low priority configurations
  - Settings sync between in-game GUI and web panel

- **Startup Banner**: Added copyright and developer info to console startup
  - Shows version, copyright, and Discord links
  - BlockForge Studios and ADF Industries credits

### Changed
- **Unified Anticheat Settings Tab**: Reorganized Staff Settings anticheat tab
  - Quick access to personal alert preferences and automod rules from same view
  - Added minimum VL threshold setting
  - Per-anticheat toggles with shift+click to adjust min VL
  - Quick enable/disable all buttons
  - Helpful "How It Works" information section

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

- **Template Sync**: Punishment templates now sync in real-time between game and web panel
  - In-game template changes (create, update, delete) broadcast to web panel
  - Web panel template changes reflect in-game immediately

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
