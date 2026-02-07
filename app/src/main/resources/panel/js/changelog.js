/**
 * ModereX Web Panel Changelog
 *
 * IMPORTANT: Update this file with each new build!
 * Add new changelog entries at the TOP of the CHANGELOGS array.
 *
 * Each entry should have:
 * - build: The build number (integer)
 * - version: Version string (e.g., "2.0dev-169")
 * - date: Release date (YYYY-MM-DD)
 * - title: Short title for the update
 * - sections: Array of changelog sections with type and items
 */

window.MX_CHANGELOGS = [
  {
    build: 270,
    version: "2.0dev-270",
    date: "2026-02-07",
    title: "Spigot Compatibility & Evidence Viewer",
    sections: [
      {
        type: "new",
        title: "New Features",
        items: [
          "**Spigot Support** - Plugin now runs on both Paper and Spigot servers with full functionality",
          "**Evidence Viewer** - Punishment details now always show evidence section with image viewer, video player, and activity log display",
          "**Empty Evidence State** - Clean \"No evidence attached\" message when punishment has no evidence",
          "**Evidence Count Badge** - Evidence header shows count badge when items are present"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Activity Logs** - Color-coded badges by type (Chat, Command, Automod, Anticheat) with left-border accent",
          "**Image Evidence** - Hover overlay with \"View Full Size\" prompt, graceful load failure handling",
          "**Video Evidence** - Card layout with play icon, filename, file size, and hover animation",
          "**Panel Version** - Version now derived from plugin build number instead of separate properties file"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "**Cross-Platform Msg Utility** - New Msg wrapper class routes Paper-specific API calls through correct platform",
          "**Adventure API Bundled** - Adventure library now shaded into JAR for Spigot runtime support",
          "**Split Chat Listeners** - Paper and Spigot each get their own optimized chat event handler",
          "**BukkitAudiences** - Message delivery on Spigot uses adventure-platform-bukkit for Component serialization"
        ]
      }
    ]
  },
  {
    build: 255,
    version: "2.0dev-255",
    date: "2026-02-07",
    title: "3D Replay Viewer Overhaul",
    sections: [
      {
        type: "new",
        title: "New Features",
        items: [
          "**Action Feed** - Player actions (chat, block break/place, combat, items, etc.) now appear as toasts overlaid on the 3D viewer during playback",
          "**Glass Controls Bar** - Redesigned playback controls with gradient overlay, camera mode pills, and speed pills",
          "**Loading Progress Bar** - Terrain loading now shows animated progress bar instead of just a spinner",
          "**Info HUD** - Compact glass-effect heads-up display showing player position, action, and movement state",
          "**Keyboard Shortcuts** - Space (play/pause), Left/Right arrows (skip 5s), 1-5 keys (speed presets), Escape (close)",
          "**mx debug integrations** - New subcommand showing all detected plugin hooks with versions and status"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Smooth Player Movement** - Players now interpolate smoothly between capture snapshots using linear interpolation with binary search",
          "**Angle-Aware Rotation** - Player yaw interpolation uses shortest-path wrapping to prevent 360° spins",
          "**Velocity-Based Animation** - Walk animation speed driven by actual movement velocity instead of fixed oscillation",
          "**Block Color Registry** - Added 150+ missing block colors (all wool, concrete, terracotta, glazed terracotta, coral, copper, deepslate variants)",
          "**Heuristic Fallbacks** - Unknown blocks matched by suffix patterns (walls, stairs, slabs, fences, glass, etc.) instead of showing magenta",
          "**Default Block Color** - Unknown blocks now render as neutral gray instead of bright magenta",
          "**Camera Follow Mode** - Follow camera now smoothly lerps to player position instead of snapping"
        ]
      },
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Magenta Blocks** - Fixed hundreds of common Minecraft blocks rendering as bright magenta due to missing color entries",
          "**Choppy Movement** - Fixed player models snapping between 100ms capture intervals instead of interpolating smoothly"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "**Pre-Indexed Snapshots** - Snapshots now indexed by player UUID at load time for O(log n) binary search per frame",
          "**Action Events API** - New getActionsInRange() method for querying player actions within time windows",
          "**Texture Map Expansion** - Added CDN texture mappings for wool, concrete, terracotta, deepslate, nether blocks, and more"
        ]
      }
    ]
  },
  {
    build: 254,
    version: "2.0dev-254",
    date: "2026-02-06",
    title: "Textured Block Rendering & BlueMap Integration",
    sections: [
      {
        type: "new",
        title: "New Features",
        items: [
          "**Texture Atlas** - 3D replay viewer now renders actual Minecraft block textures loaded from CDN (120+ block types)",
          "**BlueMap Integration** - When BlueMap is installed, replay viewer loads map tiles for far-range terrain backdrop",
          "**BlueMap Tile Proxy** - CORS proxy endpoint for fetching BlueMap tiles through the plugin web server",
          "**BlueMap Integrations Card** - BlueMap status shown on integrations tab with version, map count, and port info"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Greedy Meshing** - Terrain mesher now generates UV coordinates for textured blocks with per-face AO shading",
          "**Voice Chat Status** - Simple Voice Chat integration now properly shows status on integrations tab",
          "**Rendering Fallback** - Three-tier fallback: BlueMap tiles > CDN texture atlas > solid vertex colors"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "**BlueMapHook** - Reflection-based BlueMap detection (no compile dependency), reads web port and discovers map IDs",
          "**BLUEMAP_STATUS** - New WebSocket handler for querying BlueMap availability and configuration",
          "**Texture Atlas System** - Builds combined atlas from individual 16x16 block textures via prismarine-minecraft-data CDN"
        ]
      }
    ]
  },
  {
    build: 253,
    version: "2.0dev-253",
    date: "2026-02-06",
    title: "Security Hardening",
    sections: [
      {
        type: "fixed",
        title: "Security Fixes",
        items: [
          "**Path Traversal** - Fixed directory traversal in gateway panel file serving and plugin static file handler",
          "**XSS Prevention** - Fixed DOM-based XSS in player drawer onclick handlers and portal script injection",
          "**Response Splitting** - Sanitized evidence filenames in HTTP Content-Disposition headers",
          "**Timing Attack** - Challenge answer validation now uses constant-time comparison",
          "**Evidence Upload** - Added filename sanitization (path components, null bytes, length limit)",
          "**MIME Type Injection** - Escaped MIME types in portal evidence display to prevent attribute injection",
          "**Integer Overflow** - Evidence upload size check now uses long to prevent bypass via overflow"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Gateway Rate Limiting** - Device fingerprint auth now rate-limited with exponential backoff",
          "**Gateway Message Size** - WebSocket messages capped at 2MB to prevent memory exhaustion",
          "**Admin Auth** - Email domain validation now uses exact domain match instead of suffix match",
          "**CORS Headers** - Added Vary: Origin and X-Content-Type-Options: nosniff headers"
        ]
      }
    ]
  },
  {
    build: 252,
    version: "2.0dev-252",
    date: "2026-02-05",
    title: "Global Token System & Server List",
    sections: [
      {
        type: "new",
        title: "New Features",
        items: [
          "**Global Token System** - One token works across all servers connected to the gateway, no need to re-authenticate per server",
          "**Server List Page** - Visit panel.moderex.net to see all your servers in one place with status, player count, and rank",
          "**Gateway Database** - SQLite database on the gateway stores tokens, permissions, and settings persistently",
          "**Permission Sync** - Server permissions automatically sync to gateway on startup and when LuckPerms changes are detected",
          "**Settings Sync** - Color scheme and device fingerprints sync to both gateway and server databases"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Multi-Server Navigation** - 'Servers' button in profile dropdown lets you switch servers without re-authenticating",
          "**Logo Click** - Clicking the sidebar logo in gateway mode returns to the server list",
          "**Server Card UI** - Server IDs are blurred by default for privacy, click to reveal and copy",
          "**Token Dual-Write** - Tokens sync to gateway when generated or revoked via /mx gettoken and /mx revoketoken",
          "**Device Trust Sync** - Trusted device fingerprints sync to gateway for cross-server device auth"
        ]
      },
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Admin Panel** - Fixed 11 out of 14 broken features due to message type mismatches between frontend and gateway",
          "**Gateway SQLite** - Fixed SQLite crash on startup by using sql.js (pure JS) with better-sqlite3 as optional native fallback",
          "**Admin Auth** - Added missing admin_auth handler so Cloudflare Access authentication works properly",
          "**Admin Dashboard** - Added get_dashboard_data handler for server stats and activity overview",
          "**Admin Announcements** - Fixed type mismatch (announcements_list), added deactivate handler",
          "**Admin Audit Log** - Fixed field name mismatch (entries vs logs), added export handler"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "**Gateway DB Tables** - Added global_tokens, server_access, and user_settings tables",
          "**sql.js Wrapper** - Created better-sqlite3 compatible API wrapper for sql.js with auto-save to disk",
          "**GatewayClient** - Added syncAllPermissions, registerGlobalToken, revokeGlobalToken, syncUserSettings methods",
          "**LuckPerms Listener** - Permission change events trigger automatic gateway sync",
          "**Global Pre-Auth** - Gateway sends pre-auth to MC server for seamless server switching",
          "**WebSocket Global Mode** - New /panel/ root endpoint for server list page connections"
        ]
      }
    ]
  },
  {
    build: 251,
    version: "2.0dev-251",
    date: "2026-02-05",
    title: "Disconnect Handling & Error Pages",
    sections: [
      {
        type: "new",
        title: "New Features",
        items: [
          "**Cloudflare-Style Error Pages** - Server Not Found (404) and Gateway Error (502) now show professional full-page error screens with connection chain visualization",
          "**Server Online Detection** - Gateway now broadcasts when a Minecraft server reconnects, allowing the panel to auto-detect and re-authenticate"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Server Offline Overlay** - Redesigned with pulsing indicator instead of spinner, no more 'Reconnecting...' messages during silent wait",
          "**Auto Re-authentication** - When a server comes back online, the panel automatically re-authenticates using saved tokens",
          "**Gateway Mode Disconnect** - Server offline handled separately from direct mode disconnects for cleaner UX",
          "**Silent Retry** - Fixed 5-second retry interval when gateway connection drops during server offline state"
        ]
      },
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**CSS Variable Bug** - Fixed undefined --bg-card variable in server offline overlay, now uses --surface"
        ]
      }
    ]
  },
  {
    build: 250,
    version: "2.0dev-250",
    date: "2026-02-05",
    title: "3D Terrain Replay Viewer",
    sections: [
      {
        type: "new",
        title: "New Features",
        items: [
          "**3D Replay Viewer** - View replay recordings in a full 3D environment with actual Minecraft terrain rendered in the browser",
          "**Chunk Terrain Capture** - Automatically captures surrounding terrain (7x7 chunk area) when a replay recording starts",
          "**Greedy Mesh Rendering** - Optimized voxel rendering engine with 150+ block types, face culling, and face-based ambient shading",
          "**Free Camera Mode** - WASD + mouse controls for FPS-style free camera navigation through the terrain",
          "**Follow Camera Mode** - Camera automatically tracks the recorded player during playback",
          "**Block Change Replay** - Block breaks and placements are replayed at the correct timestamps on the terrain"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Replay Viewer Modal** - Fullscreen 3D viewer with timeline scrubbing, play/pause, skip, and 0.25x-4x speed control",
          "**Player Models** - Animated Minecraft player models with walk cycles, sneaking, and name tags",
          "**Orbit Camera** - Drag to rotate, scroll to zoom orbit camera as default viewing mode",
          "**Terrain Fallback** - Older replays without terrain data gracefully fall back to a flat ground plane"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "**MXCHUNK Binary Format** - Custom palette-based chunk serialization with GZIP compression (~200KB-1MB for 49 chunks)",
          "**Three.js Integration** - WebGL-based 3D rendering loaded via CDN for the replay viewer",
          "**Block Log Transmission** - Block change logs now included in replay data responses for terrain updates"
        ]
      },
      {
        type: "config",
        title: "Configuration Options",
        items: [
          "**replay-chunk-capture-enabled** - Enable/disable terrain capture on replay start (default: true)",
          "**replay-chunk-capture-radius** - Chunk capture radius 0-6, where 3 = 7x7 chunks (default: 3)"
        ]
      }
    ]
  },
  {
    build: 249,
    version: "2.0dev-249",
    date: "2026-02-05",
    title: "Security Hardening & Permission System",
    sections: [
      {
        type: "new",
        title: "New Features",
        items: [
          "**Built-in Permission System** - Full rank management system with inheritance, drag-reorder, and web panel tab",
          "**Default Ranks** - Helper, Moderator, Admin, Developer, and Owner ranks created on first startup",
          "**Permissions Tab** - Visual rank editor with permission toggles, player assignment, and LuckPerms export",
          "**License Acceptance** - First-time users must accept the software license before accessing the panel",
          "**Auth CAPTCHA Challenge** - Math challenge after 3 failed login attempts to prevent brute force",
          "**Gateway Rate Limiting** - Max 3 browser connections per IP, 100 messages/sec per connection"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Gateway Authentication** - Auto-generated shared secret prevents server impersonation",
          "**Token Encryption** - Permanent tokens are now AES-GCM encrypted in localStorage",
          "**Session Storage** - Session tokens moved to sessionStorage (cleared when tab closes)",
          "**Token Expiration** - Permanent tokens now expire after 90 days",
          "**Exponential Backoff** - Progressive delay between failed auth attempts"
        ]
      },
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Disconnect Loop** - Fixed web panel constantly disconnecting/reconnecting on direct IP:port connections",
          "**Connect Code RNG** - Fixed connect codes using weak Random instead of SecureRandom",
          "**Gateway Client IDs** - Fixed browser client IDs using Math.random instead of crypto.randomBytes"
        ]
      },
      {
        type: "permissions",
        title: "New Permissions",
        items: [
          "**moderex.admin.permissions** - Required to manage ranks and permissions via web panel"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "**Permission Fallback** - hasViewPermission now uses built-in permission system instead of returning true when LuckPerms is unavailable",
          "**Gateway Secret DB** - Server secrets stored as SHA-256 hashes in gateway SQLite database",
          "**New DB Tables** - moderex_ranks, moderex_rank_permissions, moderex_rank_inheritance, moderex_player_ranks, moderex_license_acceptance"
        ]
      }
    ]
  },
  {
    build: 248,
    version: "2.0dev-248",
    date: "2026-02-05",
    title: "Automod Persistence Fix",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Automod Rules** - Fixed rules resetting on page reload by unifying serialization to include all fields (spam, caps, AFK, filter mode, conditions, etc.)",
          "**Automod Broadcast** - Fixed broadcast method using incomplete serialization, now uses same full serializer as single-rule updates"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "**Code Deduplication** - Removed ~200 lines of duplicated automod serialization code in favor of single serializeRule() method"
        ]
      }
    ]
  },
  {
    build: 247,
    version: "2.0dev-247",
    date: "2026-02-05",
    title: "Critical Bug Fixes",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Server Status** - Fixed INTERNAL_ERROR when loading server status on gateway connections",
          "**Entity/Chunk Breakdown** - Fixed async thread safety errors for entity and chunk data",
          "**Panel Version** - Fixed version check sending malformed WebSocket messages",
          "**Template CRUD** - Fixed create, edit, and delete templates sending [object Object] instead of proper data",
          "**Replay Operations** - Fixed all replay WebSocket messages (start, rename, delete, settings) sending as objects",
          "**Evidence Upload** - Fixed evidence upload and fetch sending double-wrapped JSON",
          "**User Settings Sync** - Fixed watchlist toast setting sync sending malformed data",
          "**Discord Popup** - Removed server descriptions from Discord links"
        ]
      },
      {
        type: "new",
        title: "New Features",
        items: [
          "**Replay Settings** - Added GET_REPLAY_SETTINGS WebSocket handler for replay configuration"
        ]
      }
    ]
  },
  {
    build: 246,
    version: "2.0dev-246",
    date: "2026-02-05",
    title: "Mega Update: Performance, Integrations & Polish",
    sections: [
      {
        type: "new",
        title: "New Features",
        items: [
          "**Template Favorites** - Star your most-used punishment templates for quick access",
          "**Heap Memory Graph** - Real-time heap memory visualization with Eden/Old Gen breakdown",
          "**GC Activity Graph** - Garbage collection pause time tracking and visualization",
          "**Server Health Score** - Server-calculated 0-100 health score with TPS/Memory/Entity/Chunk weighting",
          "**3-Tier TPS Alerts** - Mild (below 18), Warning (below 15), and Critical (below 10) alert levels",
          "**Simple Voice Chat Integration** - Auto-detect Simple Voice Chat plugin with web panel status",
          "**Voice Chat Status** - See if Simple Voice Chat is installed on the integrations tab",
          "**Command Blacklist Web Panel** - Full CRUD management of per-player command blacklists from the web panel",
          "**Kick All Countdown** - 10-second countdown with cancel option before kicking all players",
          "**Citizens Detection on Replay Tab** - Shows Citizens plugin status directly in the replay section"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Search Permission Filtering** - Search results now respect user permissions",
          "**MX-Case Live Search** - Search for punishments by case ID (MX-xxx) in the live search dropdown",
          "**Search Result Glow** - Animated 3-pulse glow effect when scrolling to search results",
          "**Template Type Filtering** - Template dropdown auto-filters by selected punishment type",
          "**Sound Volume** - Increased notification sound volume (0.55 to 0.85 multiplier)",
          "**Automod Caps Filter** - Now properly logs to activity log and triggers auto-punishments",
          "**Automod Dual Logging** - Each automod incident logs both a flag entry and action entry",
          "**Gateway Status Caching** - Server status data cached for immediate delivery to new connections",
          "**Alert Thresholds** - Configured thresholds now actually apply to the monitoring system",
          "**Replay List** - Shows active recording count and Citizens availability"
        ]
      },
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Evidence Display in Gateway Mode** - Inline evidence images now load correctly through the gateway",
          "**Expired Punishment Revoke** - Expired punishments can no longer be revoked (shows proper badge)",
          "**Gateway Kick All Dispatch** - KICK_ALL_COUNTDOWN and KICK_ALL_CANCEL now work through gateway connections"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "**Staff Commands for Spigot** - /fly, /broadcast, /invsee, /echest now registered for Spigot servers",
          "**SimpleVoiceChatHook** - New hook class for Simple Voice Chat soft dependency",
          "**ServerStatusManager** - Added health score calculation, GC history tracking, memory history, 3-tier alerts",
          "**HybridPanelServer** - Added GET_VOICECHAT_STATUS, CMD blacklist CRUD, replay Citizens status endpoints",
          "**ReplayManager** - Added getActiveRecordingCount() method",
          "**Template favorites table** - New moderex_template_favorites database table"
        ]
      },
      {
        type: "permissions",
        title: "New Permissions",
        items: [
          "**moderex.command.fly** - Toggle flight mode",
          "**moderex.command.fly.others** - Toggle flight for other players",
          "**moderex.command.broadcast** - Send server broadcast",
          "**moderex.command.invsee** - View player inventory",
          "**moderex.command.echest** - View player ender chest",
          "**moderex.command.echest.others** - View other players' ender chests",
          "**moderex.exempt.invsee** - Exempt from /invsee",
          "**moderex.exempt.echest** - Exempt from /echest",
          "**moderex.exempt.bypass** - Bypass exemptions"
        ]
      }
    ]
  },
  {
    build: 244,
    version: "2.0dev-244",
    date: "2026-02-05",
    title: "Admin Panel & Announcement System",
    sections: [
      {
        type: "new",
        title: "New Features",
        items: [
          "**Admin Panel System** - Internal dashboard for ModereX developers at admin.moderex.net",
          "**Global Announcement Banners** - Broadcast announcements to all connected web panels",
          "**Announcement Types** - 5 banner styles: Info (blue), Feature (green), Warning (orange), Maintenance (purple), Critical (red)",
          "**Scheduled Announcements** - Create announcements that trigger at a specific time",
          "**Gateway Admin API** - WebSocket endpoints for admin panel communication",
          "**Announcement Persistence** - SQLite database for storing announcements on gateway"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Announcement Banner UI** - Slide-down banner with dismiss button and action links",
          "**Critical Alert Animation** - Pulsing animation for urgent announcements",
          "**Gateway Health Dashboard** - Real-time gateway stats for admins",
          "**Admin Audit Log** - Track all admin actions with timestamps"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "**GatewayClient** - Added admin_announcement message handler",
          "**HybridPanelServer** - Added broadcastToAllClients method for global notifications",
          "**Gateway SQLite** - Database tables for announcements and audit logs",
          "**LocalStorage** - Dismissed announcements tracked per-panel"
        ]
      },
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Missing World Import** - Fixed compilation error in HybridPanelServer"
        ]
      }
    ]
  },
  {
    build: 243,
    version: "2.0dev-243",
    date: "2026-02-05",
    title: "Configuration Tab Revamp & Staff Commands",
    sections: [
      {
        type: "new",
        title: "New Features",
        items: [
          "**Warning Escalation System** - Customizable point-based warning tiers with automatic punishment escalation",
          "**Warning Categories** - Define custom warning categories with configurable point values",
          "**Activity Log Configuration** - Enable/disable logging types and set retention periods per category",
          "**Evidence Configuration** - Set max file size, activity log entries, and require evidence for punishments",
          "**Staff Commands** - Added /fly, /broadcast, /invsee, /echest commands"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Mute Settings** - Added Staff Can See toggle for muted player messages",
          "**Configuration UI** - Expanded Configuration tab with activity log and evidence settings",
          "**Settings Sync** - All configuration settings now sync between web panel and config.yml"
        ]
      },
      {
        type: "permissions",
        title: "New Permissions",
        items: [
          "**moderex.admin.warnings** - Configure warning escalation settings",
          "**moderex.admin.activitylog** - Configure activity log settings",
          "**moderex.admin.evidence** - Configure evidence settings",
          "**moderex.command.fly** - Toggle flight mode",
          "**moderex.command.fly.others** - Toggle flight for other players",
          "**moderex.command.broadcast** - Send server broadcasts",
          "**moderex.command.invsee** - View player inventories",
          "**moderex.command.echest** - View player ender chests",
          "**moderex.command.echest.others** - View other players' ender chests"
        ]
      }
    ]
  },
  {
    build: 242,
    version: "2.0dev-242",
    date: "2026-02-05",
    title: "Gateway Mode Fixes & Debug Improvements",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Gateway Broadcasts** - Fixed event broadcasts (automod alerts, punishments, etc.) not reaching gateway-connected clients",
          "**Panel Version Gateway** - Fixed GET_PANEL_VERSION being blocked before authentication in gateway mode",
          "**Server Status Gateway** - Added debug logging for server status requests in gateway mode"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Debug Logging** - Added detailed logging for automod rule saves and loads to trace persistence issues",
          "**Automod Persistence** - Added logging to trace save/load cycle for troubleshooting"
        ]
      }
    ]
  },
  {
    build: 241,
    version: "2.0dev-241",
    date: "2026-02-05",
    title: "Configuration Tab Revamp & Getting Started Guide",
    sections: [
      {
        type: "new",
        title: "New Features",
        items: [
          "**Getting Started Guide** - Comprehensive guide tab with table of contents, search, and collapsible sections",
          "**Server Lockdown** - Full lockdown system with timer, custom MOTD, and kick message",
          "**Command Blacklist** - Block specific commands with custom denial messages",
          "**Notification Configuration** - Configure join/leave message visibility",
          "**Discord Support** - New Discord button with links to BlockForge Studios and ADF Industries servers"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Configuration Tab** - Renamed from Server Actions with expanded features",
          "**Expired Punishments** - Badge now shows 'Expired' in orange, revoke button disabled",
          "**Notification Sounds** - Increased volume multiplier from 0.3 to 0.55 for better audibility",
          "**Gateway Panel Version** - Deferred version request until gateway connection confirmed"
        ]
      },
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Automod Alerts** - Added missing AUTOMOD_ALERT WebSocket handler for activity log display",
          "**Expired Punishment Revoke** - Backend and frontend now block revoking expired punishments",
          "**Panel Version Gateway** - Fixed version request failing before gateway connection"
        ]
      },
      {
        type: "permissions",
        title: "New Permissions",
        items: [
          "**moderex.admin.lockdown** - Enable/disable server lockdown",
          "**moderex.admin.commandblacklist** - Configure command blacklist",
          "**moderex.admin.notifications** - Configure notification settings",
          "**moderex.bypass.commandblacklist** - Bypass command blacklist"
        ]
      }
    ]
  },
  {
    build: 240,
    version: "2.0dev-240",
    date: "2026-02-04",
    title: "Gateway Bug Fixes & Improvements",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Panel Version in Gateway** - Fixed panel version showing 'Loading' in gateway mode",
          "**Rank Display** - Fixed LuckPerms rank not showing in gateway mode authentication",
          "**Automod Broadcasts** - Fixed automod rule updates not broadcasting to gateway clients"
        ]
      },
      {
        type: "new",
        title: "New Features",
        items: [
          "**Evidence in Gateway Mode** - Upload and view evidence files through gateway using WebSocket-based Base64 transfer",
          "**Server Offline Overlay** - Non-dismissable overlay when server goes offline with automatic silent reconnection"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Silent Reconnection** - Auto-reconnect in background without sounds or toast notifications",
          "**Reconnect Status** - Shows attempt count and retry timing during reconnection"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "**GET_PANEL_VERSION Handler** - New WebSocket handler for panel version in gateway mode",
          "**UPLOAD_EVIDENCE_WS Handler** - New WebSocket handler for evidence upload in gateway mode",
          "**GET_EVIDENCE_FILE Handler** - New WebSocket handler for evidence file retrieval in gateway mode",
          "**Max Reconnect Attempts** - Limited auto-reconnect to 100 attempts before showing disconnect overlay"
        ]
      }
    ]
  },
  {
    build: 239,
    version: "2.0dev-239",
    date: "2026-02-04",
    title: "Database Limits & Premium System",
    sections: [
      {
        type: "new",
        title: "New Features",
        items: [
          "**Database Size Limits** - Free tier limited to 25MB database with warnings at 80% and 90%",
          "**Premium System** - Gateway-validated premium status bypasses database limits",
          "**Database Status** - Panel now shows database usage percentage and status in server settings"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Gateway Stability** - Fixed automod rule updates timing out through gateway",
          "**Panel Authentication** - Fixed server name, plugin version, and rank not showing in gateway mode",
          "**Self-Toast Prevention** - Broadcast messages now include sender info to prevent self-notifications",
          "**Loading Bar** - Changed gradient to consistent blue theme instead of pink"
        ]
      },
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Gateway Actions** - Fixed watchlist, checklist, templates, and settings actions not working via gateway",
          "**Automod Broadcasts** - Fixed self-created rules showing toast notifications to the creator"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "**Removed Same-Port Mode** - Experimental Netty injection mode completely removed for stability",
          "**Premium Caching** - Premium status cached for 24 hours if gateway becomes unavailable"
        ]
      },
      {
        type: "config",
        title: "Configuration",
        items: [
          "**Removed webpanel.same-port** - This experimental option has been removed",
          "**Config version updated to 17** - Auto-migration will update your config"
        ]
      }
    ]
  },
  {
    build: 226,
    version: "2.0dev-226",
    date: "2026-02-04",
    title: "Gateway Infrastructure",
    sections: [
      {
        type: "new",
        title: "New Features",
        items: [
          "**ModereX Gateway** - Panel now accessible via panel.moderex.net without port forwarding or SSL setup",
          "**UUID-style Server ID** - New server ID format: XXXXX-XXXXX-XXXXX-XXXXX-XXXXX for better uniqueness",
          "**Progressive URL Shortening** - Access panel via short URLs like panel.moderex.net/abc12/ that auto-expand if needed",
          "**Gateway Commands** - New /mx panel, /mx gateway status, /mx gateway reconnect commands"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Panel Frontend** - Auto-detects gateway mode vs direct connection mode",
          "**Server Offline Handling** - Shows friendly overlay when MC server disconnects from gateway",
          "**Opt-out Support** - Set gateway.enabled: false in config to disable ModereX.net integration"
        ]
      },
      {
        type: "config",
        title: "Configuration",
        items: [
          "**gateway.enabled** - Enable/disable gateway connection (default: true)",
          "**gateway.url** - Gateway WebSocket URL (default: wss://gateway.moderex.net/server)",
          "**gateway.reconnect-delay-seconds** - Base delay between reconnection attempts",
          "**gateway.debug-logging** - Enable verbose gateway logs in console"
        ]
      },
      {
        type: "permissions",
        title: "Permissions",
        items: [
          "**moderex.command.gateway** - Required for /mx gateway commands"
        ]
      }
    ]
  },
  {
    build: 221,
    version: "2.0dev-221",
    date: "2026-02-03",
    title: "Evidence System & Portal Update",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Evidence Persistence** - Fixed activity log evidence not being saved to database when creating punishments",
          "**Evidence Retrieval** - Added endpoint to serve uploaded evidence files with proper MIME types",
          "**Player Portal Evidence** - Added evidence display section to player punishment portal"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Punishment Details** - Reorganized into 3 vertical sections (Players, Info, Evidence) with scrollbar",
          "**Evidence Display** - Activity log evidence now shows timestamp, type icon, and content preview"
        ]
      },
      {
        type: "new",
        title: "New Features",
        items: [
          "**Medal Clip Import** - Import video clips from Medal.tv as evidence via URL",
          "**Player Portal Tabs** - Portal now has Punishments and Settings tabs",
          "**Punishment Timeline** - Visual timeline showing all player's punishment history",
          "**Portal Settings** - Color scheme picker, browser notifications, device trust options",
          "**Debug Command** - Added /mx debug authsession command for testing player portal sessions"
        ]
      },
      {
        type: "permissions",
        title: "Permissions",
        items: [
          "**moderex.admin** - Required for /mx debug authsession command"
        ]
      }
    ]
  },
  {
    build: 219,
    version: "2.0dev-219",
    date: "2026-02-03",
    title: "Evidence Display Fix",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Evidence Display** - Fixed evidence not appearing in punishment details modal",
          "**Backend Evidence Data** - Punishments now include evidence array in API responses",
          "**Activity Log Evidence** - Properly parse activity log snapshots for display"
        ]
      }
    ]
  },
  {
    build: 218,
    version: "2.0dev-218",
    date: "2026-02-02",
    title: "Evidence System & Player Portal",
    sections: [
      {
        type: "new",
        title: "Evidence System",
        items: [
          "**Evidence Selector** - Attach activity log entries as evidence when creating punishments",
          "**File Uploads** - Upload images (PNG, JPG) and videos (MP4, MKV, MOV) as punishment evidence",
          "**Upload Progress** - Real-time upload progress bar with file size display",
          "**Custom Video Player** - Themed video player with playback speed control (0.25x-2x)",
          "**Custom Image Lightbox** - Full-screen image viewer with zoom and download options",
          "**Search Filters** - Use before:YYYY-MM-DD and after:YYYY-MM-DD in activity log search",
          "**In-Game Evidence** - Java players use chat UI, Bedrock players use chest GUI"
        ]
      },
      {
        type: "new",
        title: "Player Punishment Portal",
        items: [
          "**Player Portal** - Punished players can view their punishment details and evidence",
          "**Auth Sessions** - Secure 10-character session tokens with 12-hour expiry",
          "**Portal Links** - Ban/kick screens and mute/warn messages include portal links",
          "**Color Schemes** - Players can customize their portal theme",
          "**Device Trust** - Option to trust devices for future logins"
        ]
      },
      {
        type: "improved",
        title: "Punishment Messages",
        items: [
          "**Ban/Kick Screens** - Show up to 3 evidence entries with portal link for more",
          "**Mute Messages** - Players notified when muted with evidence portal link",
          "**Warn Messages** - Warning messages include evidence portal link when applicable"
        ]
      },
      {
        type: "config",
        title: "New Configuration",
        items: [
          "**evidence.max-file-size-mb** - Maximum upload size (default: 250MB, hard limit: 1000MB)",
          "**evidence.require-evidence** - Require evidence for all punishments (default: false)",
          "**evidence.max-activity-log-evidence** - Max activity log entries per punishment (default: 5)",
          "**player-portal.enabled** - Enable/disable player portal (default: true)",
          "**player-portal.session-expiry-hours** - Session expiry time (default: 12 hours)"
        ]
      }
    ]
  },
  {
    build: 216,
    version: "2.0dev-216",
    date: "2026-02-02",
    title: "Activity Log & Punishment Revamp",
    sections: [
      {
        type: "new",
        title: "Activity Log Page",
        items: [
          "**Database-Backed Logs** - New Activity Log page replaces Live Logs with database persistence",
          "**Permission-Filtered** - Only shows activity types you have permission to view",
          "**Search Filters** - Use player:name and type:chat style filters",
          "**Real-Time Updates** - New activity entries pushed via WebSocket",
          "**Pagination** - Browse through historical activity logs"
        ]
      },
      {
        type: "improved",
        title: "Punishment Form",
        items: [
          "**Multi-Player Selection** - Select multiple players without toggle, auto-detects mass punishment",
          "**Dynamic Titles** - Title updates: 'New Punishment' → 'New Ban' → 'Ban - Player' → 'Mass Ban - 3 Players'",
          "**Smart Search** - Player search only shows after typing 2+ characters",
          "**Better Styling** - Solid backgrounds, hover glow effects, player avatars in results",
          "**Player Tags** - Selected players shown as removable tags with avatars"
        ]
      },
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Automod Logs** - Fixed permission mismatch preventing automod logs from displaying",
          "**Create Rule** - Editor popup now opens reliably after creating new automod rule",
          "**Database Debug** - Added automod alerts viewer in Developer Tools"
        ]
      },
      {
        type: "permissions",
        title: "New Permissions",
        items: [
          "**moderex.history.sessions** - View player session history (join/leave activity logs)"
        ]
      }
    ]
  },
  {
    build: 215,
    version: "2.0dev-215",
    date: "2026-01-31",
    title: "Punishment Form Revamp",
    sections: [
      {
        type: "new",
        title: "Mass Punishment UI",
        items: [
          "**Mass Punishment Toggle** - Switch between single and mass punishment modes",
          "**Multi-Player Selector** - Add/remove multiple players with tag-style chips",
          "**Execute via Web Panel** - Mass punishments can now be issued directly from the panel"
        ]
      },
      {
        type: "improved",
        title: "Form Styling",
        items: [
          "**Modern Dropdowns** - Improved select styling with hover effects",
          "**Cleaner Title** - Removed '=' from punishment title (now uses · separator)",
          "**Character Counter** - Reason field shows remaining characters",
          "**Execute Button** - Disabled state with helpful messages when invalid"
        ]
      }
    ]
  },
  {
    build: 214,
    version: "2.0dev-214",
    date: "2026-01-31",
    title: "Mass Punishments & Permission Fixes",
    sections: [
      {
        type: "new",
        title: "Mass Punishment Commands",
        items: [
          "**/masswarn** - Warn multiple players at once (comma-separated)",
          "**/massmute** - Mute multiple players at once",
          "**/masskick** - Kick multiple online players at once",
          "**/massban** - Ban multiple players at once",
          "**/massunwarn, /massunmute, /massunban** - Undo by batch ID"
        ]
      },
      {
        type: "improved",
        title: "Permission System",
        items: [
          "**OP Bypass** - Server operators now get all permissions automatically",
          "**15s Refresh** - Permission refresh interval changed from 5s to 15s",
          "**New Permissions** - Added moderex.staff, moderex.staffchat, moderex.template.*, moderex.mass.*"
        ]
      },
      {
        type: "fixed",
        title: "UI Improvements",
        items: [
          "**Staff Chat Overlay** - Proper styled permission box when lacking access",
          "**Command Blacklist Overlay** - Proper styled permission box",
          "**Watchlist Overlay** - Consistent styling with other pages"
        ]
      }
    ]
  },
  {
    build: 213,
    version: "2.0dev-213",
    date: "2026-01-31",
    title: "Staff Chat, Permissions & GUI Revamp",
    sections: [
      {
        type: "new",
        title: "Staff Chat System",
        items: [
          "**Staff Chat Page** - New dedicated page for staff chat with full history",
          "**Database Storage** - Staff chat messages now persist in database",
          "**Lazy Loading** - Load more messages by scrolling up (like Discord)",
          "**Permission Gating** - Requires moderex.staffchat permission to use"
        ]
      },
      {
        type: "new",
        title: "Template Permissions",
        items: [
          "**moderex.template.create** - Create new punishment templates",
          "**moderex.template.edit** - Edit existing punishment templates",
          "**moderex.template.delete** - Delete punishment templates",
          "**View Always Available** - All staff can view templates, CRUD requires permissions",
          "**/mx templates** - New command to open templates GUI in-game"
        ]
      },
      {
        type: "new",
        title: "Punishment GUI Revamp",
        items: [
          "**New Layout** - Templates at top, duration modifiers, action buttons at bottom",
          "**Duration Modifiers** - Quick +/- 1h, 1d, 1w, 1mo buttons",
          "**Info Display** - Center item shows full punishment details with lore wrapping",
          "**Mass Punishments** - Add multiple players to punish at once from GUI",
          "**Template Preview** - Click template to see values before applying"
        ]
      },
      {
        type: "improved",
        title: "Permission Improvements",
        items: [
          "**moderex.staff** - Required for /mx commands and viewing online staff",
          "**moderex.staffchat** - Separate permission for staff chat access",
          "**History Commands** - Each check command requires specific history permission",
          "**Permission Refresh** - All pages now re-render when permissions change"
        ]
      },
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Tooltip System** - Custom JS tooltip fixes hover glitch on disabled buttons",
          "**Sound Error** - Fixed IncompatibleClassChangeError with Sound.valueOf",
          "**Automod Logging** - Automod triggers now properly saved to activity logs",
          "**Automod Messages** - Updated blocked message and alert format with rule + message",
          "**Activity Logs** - Now show type labels (Chat, Command, Sign, etc.)"
        ]
      },
      {
        type: "improved",
        title: "UI Improvements",
        items: [
          "**Permission Overlays** - Generic messages instead of showing permission names",
          "**Page Titles** - Remain visible when lacking permission (only content hidden)",
          "**Check Buttons** - Expanded from [H][C][A][P][W] to full names",
          "**Removed** - Unused Watchlist Alerts toggle from My Settings"
        ]
      },
      {
        type: "permissions",
        title: "New Permissions",
        items: [
          "**moderex.staff** - Access to /mx commands and see online staff",
          "**moderex.staffchat** - Use the staff chat system",
          "**moderex.template.create** - Create punishment templates",
          "**moderex.template.edit** - Edit punishment templates",
          "**moderex.template.delete** - Delete punishment templates"
        ]
      }
    ]
  },
  {
    build: 212,
    version: "2.0dev-212",
    date: "2026-01-31",
    title: "Permission UI Polish",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Tooltip Glitch** - Fixed hover glitch near automod Add Rule button by switching to native browser tooltips",
          "**Automod No-View** - Shows clean empty state instead of blurred rules when lacking view permission",
          "**Native Tooltips** - Permission tooltips now follow mouse cursor naturally (browser title attribute)"
        ]
      },
      {
        type: "new",
        title: "Permission Overlays",
        items: [
          "**Anticheat Page** - Full-page permission overlay when lacking moderex.alerts.anticheat",
          "**Watchlist Page** - Full-page permission overlay when lacking all watchlist permissions"
        ]
      },
      {
        type: "improved",
        title: "UI Improvements",
        items: [
          "Simplified tooltip CSS - removed complex pseudo-element positioning",
          "Cleaner no-permission states across all pages",
          "Consistent permission overlay styling"
        ]
      }
    ]
  },
  {
    build: 211,
    version: "2.0dev-211",
    date: "2026-01-31",
    title: "Extended Permission System",
    sections: [
      {
        type: "new",
        title: "Automod Permissions",
        items: [
          "**moderex.automod.view** - View automod rule details (blurred without permission)",
          "**moderex.automod.edit** - Edit existing automod rules",
          "**moderex.automod.create** - Create new automod rules (Add Rule button)",
          "**moderex.automod.delete** - Delete automod rules",
          "**View-Only Mode** - Rule editor opens in read-only mode without edit permission"
        ]
      },
      {
        type: "new",
        title: "Watchlist Permissions",
        items: [
          "**moderex.watchlist.add** - Add players to watchlist",
          "**moderex.watchlist.remove** - Remove players from watchlist",
          "**Smart Toggle** - Watchlist toggle adapts based on add/remove permissions",
          "**Alert Filtering** - Watchlist alerts filtered by type permissions (warns, kicks, bans, etc.)"
        ]
      },
      {
        type: "improved",
        title: "UI Improvements",
        items: [
          "**Watchlist Page** - Removed Watchlist Toasts toggle, Test Alert, and Clear Alerts buttons",
          "**Custom Tooltips** - Disabled buttons show permission explanations on hover",
          "**Anticheat Section** - Settings page shows overlay when lacking moderex.alerts.anticheat",
          "**Command Blacklist** - Permission-aware add/remove with proper UI feedback"
        ]
      },
      {
        type: "technical",
        title: "Backend",
        items: [
          "Backend validation for automod create/edit/delete operations",
          "Specific permission checks replace generic moderex.admin.automod",
          "Watchlist add/remove operations validated server-side"
        ]
      },
      {
        type: "permissions",
        title: "New Permissions",
        items: [
          "**moderex.automod.view** - View automod rule details",
          "**moderex.automod.edit** - Modify existing automod rules",
          "**moderex.automod.create** - Create new automod rules",
          "**moderex.automod.delete** - Delete automod rules",
          "**moderex.watchlist.add** - Add players to watchlist",
          "**moderex.watchlist.remove** - Remove players from watchlist",
          "**moderex.alerts.anticheat** - Configure anticheat alert integration"
        ]
      }
    ]
  },
  {
    build: 210,
    version: "2.0dev-210",
    date: "2026-01-31",
    title: "Permission System Refinements",
    sections: [
      {
        type: "improved",
        title: "Permission Enforcement",
        items: [
          "**Auto-Refresh** - Permissions now refresh every 5 seconds with change detection logging",
          "**Dashboard** - Recent Punishments section now respects history permissions",
          "**Player Profile** - Chat logs, commands, and automod buttons require proper permissions",
          "**Filter Buttons** - Hidden when user lacks permission for that punishment type"
        ]
      },
      {
        type: "fixed",
        title: "UI Improvements",
        items: [
          "**CSS Variables** - Fixed no-permission styles to use existing color scheme",
          "**Table States** - Separate 'no permission' vs 'no results' messages",
          "**Punish Button** - Renamed from 'Action' to 'Punish' with permission awareness",
          "**Punish Player Button** - Now disabled when user has no punishment permissions",
          "**Removed** - 'Remove' button and 'Closed' badge from punishment table (use Details instead)"
        ]
      },
      {
        type: "technical",
        title: "Backend",
        items: [
          "Added permission validation for chat logs (moderex.history.chat)",
          "Added permission validation for command history (moderex.history.commands)",
          "Added permission validation for automod logs (moderex.history.automod)"
        ]
      }
    ]
  },
  {
    build: 208,
    version: "2.0dev-208",
    date: "2026-01-31",
    title: "Web Panel Permission Enforcement",
    sections: [
      {
        type: "new",
        title: "New Features",
        items: [
          "**Pardon Logging** - Punishment revocations are now logged to database with full tracking",
          "**New Permission: moderex.history.pardons** - Control who can view pardon/revocation history",
          "**No Permission Overlays** - Forms and modals show clean 'No Permission' overlay when access denied",
          "**Filtered Filter Buttons** - Punishment history filter buttons only show for types user can view"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Backend Permission Validation** - All WebSocket actions now validate permissions server-side",
          "**Punishment Form** - Dropdown only shows punishment types user has permission for",
          "**Duration Validation** - Users with tempban/tempmute cannot enter 'perm' duration",
          "**Player Profile** - Quick action buttons disabled for punishment types without permission",
          "**Active Punishments** - Revoke buttons check permission per punishment type",
          "**Past Violations** - Section filtered by type permission, shows lock message if no access",
          "**Pardons Section** - Requires moderex.history.pardons permission to view",
          "**Details Modal** - Revoke button disabled with lock icon if user lacks revoke permission",
          "**History Table** - Server-side filtering prevents unauthorized data from being sent"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "**moderex_pardons table** - New database table tracking punishment revocations",
          "**createPunishment()** - Backend permission check before executing punishment",
          "**revokePunishment()** - Backend permission check and pardon logging",
          "**sendPunishments()** - Server-side filtering by moderex.history.* permissions",
          "**Helper Functions** - canIssuePunishment(), canRevokePunishment(), canViewHistoryType()",
          "**Debug Logging** - Permission denials logged to console for troubleshooting"
        ]
      },
      {
        type: "permissions",
        title: "Permission Updates",
        items: [
          "**moderex.history.pardons** - New permission to view pardon history in player profile"
        ]
      }
    ]
  },
  {
    build: 207,
    version: "2.0dev-207",
    date: "2026-01-30",
    title: "Permissions System Overhaul",
    sections: [
      {
        type: "new",
        title: "New Features",
        items: [
          "**Complete Permissions Overhaul** - New structured permission system with clear categories",
          "**Wildcard Permissions** - Support for moderex.*, moderex.command.*, moderex.bypass.*, etc.",
          "**Info Permissions** - New moderex.info.* permissions for viewing player data (IP, UUID, nicknames)",
          "**History Permissions** - New moderex.history.* permissions for viewing logs (chat, commands, automod)",
          "**Punishment Permissions** - Granular permissions for each punishment type (ban, mute, kick, warn)",
          "**Flag Permissions** - New moderex.flag.* permissions for punishment flags (silent, public, global)",
          "**Automod Permissions** - Separate view/edit/create/delete permissions for automod management"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Web Panel Permission Enforcement** - Backend now filters data based on viewer permissions",
          "**Frontend Permission Checks** - UI elements hidden/disabled based on permissions",
          "**Toggle Styling** - Toggle switches now use primary color instead of green",
          "**Nickname Section** - Always visible with 'No history' message when empty",
          "**IP Section** - Shows 'No permission' message when user lacks moderex.info.ip",
          "**OP Bypass** - Operators bypass all permission checks (except moderex.webpanel)"
        ]
      },
      {
        type: "permissions",
        title: "Permission Changes",
        items: [
          "**moderex.info.ip** - View player IP addresses (replaces moderex.view.ip)",
          "**moderex.info.uuid** - View player UUIDs (replaces moderex.view.uuid)",
          "**moderex.info.nick** - View current player nicknames",
          "**moderex.history.chat** - View chat history (replaces moderex.view.chathistory)",
          "**moderex.history.commands** - View command history (replaces moderex.view.commandhistory)",
          "**moderex.history.automod** - View automod logs (replaces moderex.view.automod)",
          "**moderex.history.nick** - View nickname change history",
          "**moderex.flag.silent** - Use -s flag (replaces moderex.notify.silent)",
          "**moderex.flag.public** - Use -p flag (replaces moderex.public)",
          "**moderex.flag.global** - Use -g flag (replaces moderex.server.global)",
          "**moderex.punish.delete** - Delete punishments (replaces moderex.delete)",
          "**moderex.punish.modify** - Modify punishments (replaces moderex.modify)"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "**PermissionUtil** - Enhanced wildcard permission checking",
          "**HybridPanelServer** - Permission-based data filtering for sendPlayerList/sendPlayerDetails",
          "**app.js** - Updated canView() function to use new permission mapping"
        ]
      }
    ]
  },
  {
    build: 206,
    version: "2.0dev-206",
    date: "2026-01-30",
    title: "Major Update - Automod, Nicknames & Permissions",
    sections: [
      {
        type: "new",
        title: "New Features",
        items: [
          "**Nickname History** - Player profile now shows nickname change history with expandable view",
          "**Nick History Command** - `/nickhistory <player>` shows nickname history with pagination and GUI",
          "**Automod History Command** - `/automodhistory <player>` shows automod triggers with pagination",
          "**Automod History GUI** - Use `-gui` flag to open a chest GUI for history commands",
          "**Special Characters Filter** - Built-in rule blocking 1000+ unicode/resource pack characters",
          "**Applies-To Setting** - Automod rules can now target Chat Only, Nicknames Only, or Both",
          "**Live VL Updates** - Anticheat alerts update VL in real-time without extra sounds",
          "**Clickable Toast** - Rule creation toast now opens editor when clicked",
          "**Permission-Gated UI** - Sections locked/disabled when user lacks permission",
          "**Essentials Integration** - Integrations tab shows Essentials status and nickname features",
          "**Database Debug** - Developer Tools now has database inspection section with table stats",
          "**Command Blacklist Config** - Config option to prevent blacklisting ModereX commands",
          "**Watchlist Alert Logging** - Watchlist alerts now persist to database (WATCHLIST_ALERT type)"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Anticheat Alerts** - Now show actual check name instead of 'Unknown'",
          "**Toggle Animations** - Smoother toggle switch animations with green on-state glow",
          "**Click to Edit** - Rule cards now show 'Click to edit' hint on hover",
          "**High Ping Warning** - Status chip turns red when ping exceeds 300ms",
          "**Watchlist Permissions** - Granular permissions for add/remove/view watchlist",
          "**View Permissions** - Separate permissions for viewing player info types"
        ]
      },
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Anticheat Check Names** - Fixed checkName vs check field mismatch",
          "**Chip Styles** - Added missing .chip.bad CSS for high ping indicator"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "Removed simulated 'trading?' chat messages from demo mode",
          "Added activeAnticheatAlerts Map for live VL tracking",
          "Added permission helpers: requirePermission(), canView(), lockSection()",
          "Added no-permission CSS with striped overlay and tooltip"
        ]
      },
      {
        type: "permissions",
        title: "New Permissions",
        items: [
          "`moderex.log.automod` - View automod trigger history",
          "`moderex.log.nicknames` - View nickname change history",
          "`moderex.view.*` - View all player information",
          "`moderex.view.punishments` - View punishment history",
          "`moderex.view.chathistory` - View chat history",
          "`moderex.view.commandhistory` - View command history",
          "`moderex.view.automod` - View automod triggers",
          "`moderex.view.nicknames` - View nickname history",
          "`moderex.view.ip` - View player IP (sensitive)",
          "`moderex.view.uuid` - View player UUID",
          "`moderex.view.playtime` - View playtime stats",
          "`moderex.view.sessions` - View session history",
          "`moderex.watchlist.view` - View watchlist entries",
          "`moderex.watchlist.add` - Add players to watchlist",
          "`moderex.watchlist.remove` - Remove from watchlist"
        ]
      }
    ]
  },
  {
    build: 205,
    version: "2.0dev-205",
    date: "2026-01-30",
    title: "Rule Editor Improvements",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Rule Editor** - Editor now opens immediately after creating a new rule"
        ]
      }
    ]
  },
  {
    build: 202,
    version: "2.0dev-202",
    date: "2026-01-30",
    title: "Automod & Alert Improvements",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Rule Card Descriptions** - Fixed filter phrases not showing in rule cards until edited",
          "**Automod Exclusions** - Fixed exclusions to use exact phrase matching (word boundaries)",
          "**Auto-Save Debounce** - Added 300ms debounce to prevent rapid duplicate saves"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Watchlist Button** - Clicking watchlist on alerts now adds directly without form popup",
          "**Rule Creation** - Opens editor form 1 second after creating new rule",
          "**Removed Regex** - Removed unused regex pattern support from automod rules"
        ]
      }
    ]
  },
  {
    build: 201,
    version: "2.0dev-201",
    date: "2026-01-30",
    title: "Permissions & Server Rules",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Automod Permissions** - Fixed OP players not having automod permissions",
          "**Delete Rule Conflict** - Fixed server rules delete conflicting with automod delete",
          "**Loading Bar** - Fixed fade-out animation sliding instead of fading"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Delete Confirmation** - Server rules now use custom styled confirmation popup",
          "**Auto-Save Debounce** - Added 300ms debounce to prevent rapid-fire rule saves"
        ]
      },
      {
        type: "permissions",
        title: "New Permissions",
        items: [
          "**moderex.admin.automod** - Configure automod rules via web panel"
        ]
      }
    ]
  },
  {
    build: 200,
    version: "2.0dev-200",
    date: "2026-01-29",
    title: "Automod Broadcast Fix",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Automod Create/Update** - Removed duplicate broadcasts that caused disconnects",
          "**Automod Delete** - Fixed delete not broadcasting to other clients"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "Removed all auto-broadcasts from AutomodManager (saveRule, deleteRule, createRule)",
          "HybridPanelServer now controls all broadcasts - single rule per operation",
          "Create/Update broadcasts full rule data, Delete broadcasts just the rule ID"
        ]
      }
    ]
  },
  {
    build: 200,
    version: "2.0dev-200",
    date: "2026-01-29",
    title: "Cumulative VL Tracking",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Anticheat VL Count** - Alerts now show cumulative VL (x1, x2, x3...) instead of always x1",
          "**VL Decay** - VL resets after 5 minutes of no violations for that check"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "Added cumulative VL tracking per player per check in AnticheatAlertManager",
          "VL is incremented on each flag and used for alerts, web panel, and automod",
          "Player VL data is cleared when they leave or after 5 min idle"
        ]
      }
    ]
  },
  {
    build: 199,
    version: "2.0dev-199",
    date: "2026-01-29",
    title: "Automod Delta Updates",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Automod Performance** - Rule create/update/delete now use delta updates instead of broadcasting all rules",
          "**Disconnect Issues** - Reduced payload size for automod operations, preventing WebSocket disconnects"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "createRule no longer broadcasts (saveRule handles it after properties are set)",
          "deleteRule now broadcasts only the deleted rule ID instead of all rules",
          "Added broadcastRuleDeleted() for efficient delete notifications"
        ]
      }
    ]
  },
  {
    build: 198,
    version: "2.0dev-198",
    date: "2026-01-29",
    title: "Anticheat Alert Debug Build",
    sections: [
      {
        type: "technical",
        title: "Debug Logging",
        items: [
          "Added console logging to trace anticheat alert settings flow",
          "Logs show raw setting value, parsed value, and full staffSettings state",
          "Check browser console to diagnose alert settings issues"
        ]
      }
    ]
  },
  {
    build: 197,
    version: "2.0dev-197",
    date: "2026-01-29",
    title: "Grim Alert Hiding Fix",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Grim Integration** - Hide Grim's native chat alerts without breaking setbacks/teleports",
          "**No Config Modification** - ModereX no longer modifies Grim's punishments.yml"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "Cancel FlagEvent to hide alerts (setbacks happen before the event fires)",
          "Removed dependency on modifying Grim config files"
        ]
      }
    ]
  },
  {
    build: 195,
    version: "2.0dev-195",
    date: "2026-01-29",
    title: "Loading Bar & Watchlist Fixes",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Loading Bar Duplicates** - Fixed loading bar showing duplicate/ghost bars during rapid requests",
          "**Watchlist Punish Modal** - Removed custom modal, now uses regular punishment form with player pre-selected"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "Loading bar now fully resets state (count, timeouts, classes) on new requests",
          "Added separate cleanup timeout tracking to prevent lingering fade effects"
        ]
      }
    ]
  },
  {
    build: 194,
    version: "2.0dev-194",
    date: "2026-01-29",
    title: "Settings Fix & Refresh Button",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Settings 'Not Set' Fix** - Alert level settings no longer show 'Not Set' after page refresh",
          "**Loading Bar Reset** - Loading bar now properly resets when a new request starts"
        ]
      },
      {
        type: "new",
        title: "New Features",
        items: [
          "**My Settings Refresh Button** - Added refresh button to re-pull settings from the database"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "Alert level values converted to uppercase when received from server (DB stores lowercase)",
          "Added toUpper() helper for consistent alert level formatting"
        ]
      }
    ]
  },
  {
    build: 192,
    version: "2.0dev-192",
    date: "2026-01-29",
    title: "Delta Updates for Automod Rules",
    sections: [
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Delta Updates** - Rule changes now broadcast only the changed rule instead of all rules",
          "**Scalability** - Can now handle 500+ rules without WebSocket issues",
          "**Reduced Bandwidth** - Single rule updates are ~99% smaller than full broadcasts"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "Added broadcastSingleRuleUpdate() method for efficient delta updates",
          "Added serializeRule() helper to reuse rule serialization logic",
          "saveRule() now uses single rule broadcast instead of full refresh"
        ]
      }
    ]
  },
  {
    build: 190,
    version: "2.0dev-190",
    date: "2026-01-29",
    title: "Anticheat Rules Broadcast Optimization",
    sections: [
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Broadcast Optimization** - Significantly reduced automod rules payload when anticheat plugins (Grim, etc.) are enabled",
          "**Faster Sync** - Anticheat rules now send only essential fields, reducing JSON size by ~70%"
        ]
      },
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Disconnect with Grim** - Fixed WebSocket disconnects when updating automod rules while Grim is enabled"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "Anticheat rules skip full serialization, only include essential fields",
          "Non-anticheat rules continue to include all fields for full editing support"
        ]
      }
    ]
  },
  {
    build: 189,
    version: "2.0dev-189",
    date: "2026-01-29",
    title: "Function Reference Fix",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Disconnect on Save** - Fixed undefined renderAutomod function causing WebSocket disconnect"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "Changed renderAutomod() call to correct renderRules() function"
        ]
      }
    ]
  },
  {
    build: 188,
    version: "2.0dev-188",
    date: "2026-01-29",
    title: "New Rule Creation Fix",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**New Rule Not Found** - Fixed 'Rule not found' error when updating newly created automod rules",
          "**CREATE vs UPDATE** - New rules now correctly use CREATE_AUTOMOD_RULE instead of UPDATE"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "Frontend now detects new rules by temp ID prefix and uses appropriate endpoint",
          "Temp rule IDs are mapped to server IDs after creation for future updates"
        ]
      }
    ]
  },
  {
    build: 187,
    version: "2.0dev-187",
    date: "2026-01-29",
    title: "Automod Rule Response Format Fix",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Automod Rule Disconnect** - Fixed WebSocket disconnect when saving automod rules",
          "**Response Format** - AUTOMOD_RULE_UPDATED/CREATED/DELETED now use correct data structure"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "Fixed backend to nest response data under 'data' property as expected by frontend",
          "Applied fix to UPDATE, CREATE, and DELETE automod rule handlers"
        ]
      }
    ]
  },
  {
    build: 184,
    version: "2.0dev-184",
    date: "2026-01-29",
    title: "Automod Stability & API Fixes",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**WebSocket Disconnect Fix** - Fixed 1006 disconnect when saving automod rules (null safety in broadcast)",
          "**Plugin Version API** - Fixed /api/plugin-version returning 404 error",
          "**Broadcast Stability** - Rules broadcast now handles null values gracefully"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Automod Page Load** - Now requests fresh rules from database when opening automod page",
          "**Loading Feedback** - Loading bar appears while fetching automod rules from server"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "Added null safety checks for blacklistedWords, exclusionWords, exclusionPhrases arrays",
          "Added sendPluginVersionResponse() to HybridPanelServer",
          "Each rule serialization now has individual error handling",
          "Added GET_AUTOMOD_RULES request when navigating to automod page"
        ]
      }
    ]
  },
  {
    build: 182,
    version: "2.0dev-182",
    date: "2026-01-29",
    title: "Automod Rule Sync & Loading Improvements",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Automod Rule Disconnect** - Fixed disconnect when updating anticheat rules (string ID parse error)",
          "**Rule Data Sync** - Blacklisted phrases and exceptions now properly sync between panel and database",
          "**Changelog Order** - Multiple changelogs now display oldest first, newest last"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Loading Bar** - Progress bar now appears when saving/loading automod rules",
          "**Rule Editor** - Edit modal now properly displays blacklisted phrases and exceptions from database"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "Added debug logging for automod rule save operations",
          "Fixed `saveRule()` to route ANTICHEAT type rules correctly",
          "Fixed `deleteRule()` to handle both numeric and string rule IDs",
          "Updated frontend to read `blacklistedPhrases` array directly from rule data",
          "Added loading bar to `saveAutomodRuleFromEditor()` function"
        ]
      }
    ]
  },
  {
    build: 181,
    version: "2.0dev-181",
    date: "2026-01-29",
    title: "Alert System Improvements & Action Buttons",
    sections: [
      {
        type: "new",
        title: "New Features",
        items: [
          "**Alert Action Buttons** - Automod and anticheat alerts now have Punish and Watchlist buttons",
          "**Alert Detail Modal** - Click View on punishments to see full details (reason, duration, staff, etc.)",
          "**Alert Rate Limiting** - Configure cooldown to prevent alert spam from same player",
          "**Action Forms** - Quick punish or add to watchlist directly from alert notifications"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Better Alert Text** - Alerts now show 'Player Banned: PlayerName' format with reason and duration",
          "**Alert Click Behavior** - Alerts no longer dismiss on click, only X button dismisses",
          "**Checklist Animations** - Satisfying pop and glow animations when checking items complete",
          "**Removed Old Settings** - Cleaned up unused alert settings from My Settings page",
          "**Searchable Settings** - All new alert configurations now appear in global search"
        ]
      },
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Anticheat Alerts** - Fixed alerts not showing when notification mode was 'off' in database",
          "**Alert Level Check** - Now properly checks alert level (everyone/watchlist) instead of notification mode"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "Added `showAlertDetailModal()` for punishment details display",
          "Added `showAlertActionModal()` for punish/watchlist quick actions",
          "Added `alertRateLimiter` object to track and limit alerts per player",
          "Added rate limit settings: `alertRateLimitSeconds` and `alertRateLimitMax`",
          "Updated all alert handlers to use new text formatting",
          "Removed old unused settings UI and related functions"
        ]
      }
    ]
  },
  {
    build: 180,
    version: "2.0dev-180",
    date: "2026-01-29",
    title: "Auto-Refresh Settings on Page Open",
    sections: [
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Settings Sync** - My Settings page now refreshes settings from database when opened",
          "**Visual Sync** - Settings UI always reflects the actual database values"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "Added `GET_USER_SETTINGS` request when navigating to My Settings page",
          "Ensures frontend state matches backend database on every page visit"
        ]
      }
    ]
  },
  {
    build: 179,
    version: "2.0dev-179",
    date: "2026-01-29",
    title: "Settings Persistence & Developer Tools Search",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Toast Position Persistence** - Notification position now persists correctly after page refresh",
          "**Settings Format Conversion** - Fixed database format conversion for toast position settings"
        ]
      },
      {
        type: "new",
        title: "New Features",
        items: [
          "**Developer Tools Search** - Developer Tools now appears in global search results",
          "**Searchable Dev Features** - Search for debug permissions, test alerts, stress tests, and more"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "Added Developer Tools to searchablePages array",
          "Added 8 new searchable settings for Developer Tools sections",
          "Added IDs to all Developer Tools cards for scroll-to functionality",
          "Fixed toast position conversion from `TOP_RIGHT` to `top-right` on settings load"
        ]
      }
    ]
  },
  {
    build: 178,
    version: "2.0dev-178",
    date: "2026-01-29",
    title: "Fix Offline Player Alert Permissions",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Offline Player Alerts** - Web panel users who are offline in-game now receive alerts correctly",
          "**LuckPerms Integration** - Alert permission checks now use LuckPerms for offline players",
          "**Fallback Permissions** - Authenticated users without LuckPerms now receive all alerts by default"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "Updated `hasAlertPermission()` to check LuckPerms for offline players",
          "Added debug logging to trace permission check flow",
          "Added fallback to grant alert permissions to authenticated users without LuckPerms"
        ]
      }
    ]
  },
  {
    build: 177,
    version: "2.0dev-177",
    date: "2026-01-29",
    title: "Loading Bar & Alert Debug Improvements",
    sections: [
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Loading Bar for All Requests** - Loading bar now appears when fetching any data from the server",
          "**Alert System Debug** - Added console logging to trace alert delivery issues",
          "**Better Player ID Handling** - Alert toasts now correctly use player UUID for avatar display"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "WebSocket `send()` now triggers loading bar for GET_ requests and data operations",
          "Added `shouldShowLoadingBar()` and `shouldHideLoadingBar()` helpers for request/response tracking",
          "Added debug console.log statements to `showPanelAlert`, `alertToast`, and `CUSTOM_ALERT` handlers",
          "Fixed player ID lookup in CUSTOM_ALERT to use `data.playerUuid` directly as fallback"
        ]
      }
    ]
  },
  {
    build: 176,
    version: "2.0dev-176",
    date: "2026-01-29",
    title: "Alert Toast Visual Redesign",
    sections: [
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Alert Toast Redesign** - Alert toasts now match the watchlist alert bar style with smooth slide-in animations",
          "**View & Dismiss Buttons** - Alert toasts now have dedicated View and Dismiss buttons like the alert bar",
          "**Player Avatar Support** - Alert toasts display player avatar when player ID is available",
          "**Type-Specific Styling** - Each alert type (ban, kick, mute, etc.) has matching border and icon colors"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "Replaced bounce animation with smooth cubic-bezier slide transition matching alertBar",
          "Updated HTML structure to use `alert-toast-left`, `alert-toast-text`, and `alert-toast-actions` layout",
          "Added per-type CSS styling for border glow, icon background, player name color, and progress bar",
          "Progress bar now matches alert type color for visual consistency"
        ]
      }
    ]
  },
  {
    build: 175,
    version: "2.0dev-175",
    date: "2026-01-29",
    title: "Offline Player Permission Check via LuckPerms",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Offline Permission Check** - Web panel users no longer need to be online in-game to have permissions checked",
          "**LuckPerms Integration** - Uses LuckPerms API to check permissions for offline players",
          "**Debug Tool Fix** - Debug permissions now correctly shows user info from `state.currentUser`"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "Backend `getUserPermissions()` now checks online players via Bukkit, offline via LuckPerms",
          "Falls back to granting all permissions if LuckPerms not available (web panel users already authenticated)",
          "Fixed debug tool to use `state.currentUser` instead of `state.user`"
        ]
      }
    ]
  },
  {
    build: 174,
    version: "2.0dev-174",
    date: "2026-01-29",
    title: "Frontend Permission Storage Fix & Debug Tools",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Frontend Permission Storage** - Fixed frontend not storing permissions array from backend (was the actual cause of 'No Permission' display)",
          "**USER_SETTINGS_DATA Handler** - Now properly extracts and stores `data.permissions` to `state.permissions`"
        ]
      },
      {
        type: "new",
        title: "New Features",
        items: [
          "**Debug Permissions Tool** - New button in Developer Tools to check current permission state",
          "**Refresh Permissions** - Request fresh permissions from server with debug output",
          "**System Message Output** - Permissions debug shows results in bottom system messages"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "Added `state.permissions` storage in USER_SETTINGS_DATA handler",
          "Added console logging for permission receive events",
          "Added `debugCheckPermissions()` and `debugRefreshPermissions()` functions",
          "Added Debug Permissions section to Developer Tools page"
        ]
      }
    ]
  },
  {
    build: 171,
    version: "2.0dev-171",
    date: "2026-01-29",
    title: "Web Panel Permission Sync Fix",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Settings Permission Display** - Fixed settings page showing 'No Permission' for all users by sending permissions array to frontend",
          "**Permission Check** - Backend now properly sends user's alert permissions when loading settings"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "Added `getUserPermissions()` method to fetch and send user permissions to web panel",
          "Added all staff and alert permissions to `plugin.yml` and `paper-plugin.yml`",
          "Permissions are now sent in USER_SETTINGS_DATA response for frontend permission checks"
        ]
      },
      {
        type: "permissions",
        title: "Permissions Added to Plugin",
        items: [
          "`moderex.staff` - Master staff permission for settings storage",
          "`moderex.alerts.*` - Wildcard for all alert permissions",
          "`moderex.alerts.ban`, `kick`, `mute`, `warn`, `pardon` - Punishment alerts",
          "`moderex.alerts.anticheat`, `automod`, `commands`, `nickname` - Detection alerts",
          "`moderex.alerts.joinleave`, `lag`, `watchlist`, `staffchat` - Other alerts"
        ]
      }
    ]
  },
  {
    build: 170,
    version: "2.0dev-170",
    date: "2026-01-29",
    title: "Permission-Filtered Alerts & OP Bypass",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Permission-Filtered Alerts** - Web panel alerts now respect user permissions (users only see alerts they have permission for)",
          "**OP Bypass Security** - OPs now properly bypass all permissions except `moderex.webpanel` (protected permission)"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Changelog Modal** - Now displays ModereX logo instead of generic gift icon",
          "**Alert Broadcasting** - Backend now checks permissions before sending alerts to each user"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "Added `PermissionUtil` utility class for centralized permission checking with OP bypass",
          "Added `broadcastWithPermission()` method for permission-filtered alert broadcasting",
          "Added `hasAlertPermission()` method to check user permissions by UUID",
          "Protected permissions (like `moderex.webpanel`) are never bypassed by OP status"
        ]
      }
    ]
  },
  {
    build: 169,
    version: "2.0dev-169",
    date: "2026-01-29",
    title: "Alert System Redesign & Staff Settings Overhaul",
    sections: [
      {
        type: "new",
        title: "New Features",
        items: [
          "**Comprehensive Alert System** - Complete redesign of the alert notification system with granular control per alert type",
          "**Staff Settings Page Redesign** - All alert configurations now on a single page for easier navigation",
          "**Custom Alert Toasts** - New non-transparent, animated toast notifications with priority display",
          "**Toast Position Configuration** - Choose where alerts appear: Top Right, Bottom Right, Top Left, or Bottom Left",
          "**Alert Duration Setting** - Configure how long alerts stay visible (1-60 seconds, default 10s)",
          "**Per-Alert Sound Settings** - Enable/disable notification sounds for each alert type individually",
          "**Nickname Alert System** - New alerts for inappropriate nickname detection via automod",
          "**Changelog System** - Track and display version updates (you're reading it now!)"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Alert Type Permissions** - Each alert type now has its own permission node (`moderex.alerts.*`)",
          "**Command Alerts** - Added \"Blacklisted Only\" option as default for command monitoring",
          "**Database Sync** - All staff settings now properly sync to database for persistence",
          "**In-Game GUI** - Single-page staff settings with permission-based item display",
          "**Debug Logging** - Enhanced debug messages for settings sync when debug mode is enabled",
          "**MX SendAlert Command** - Updated with all new alert types and improved tab completion"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "Added `StaffSettings.CommandAlertLevel` enum with EVERYONE, WATCHLIST_ONLY, BLACKLISTED_ONLY, OFF options",
          "Added `StaffSettings.ToastPosition` enum for web panel toast positioning",
          "Added 8 new `webSound*` boolean fields for per-alert-type sound configuration",
          "Added 3 new `webNotify*` fields for Commands, Nickname, and Lag notifications",
          "Updated `AlertManager.AlertType` with all punishment types and permissions",
          "HybridPanelServer now syncs all 30+ alert-related settings bidirectionally",
          "Added `moderex_changelog_reads` table for tracking read changelogs per user"
        ]
      },
      {
        type: "permissions",
        title: "New Permissions",
        items: [
          "`moderex.staff` - Master staff permission for settings storage",
          "`moderex.alerts.*` - Wildcard for all alert permissions",
          "`moderex.alerts.ban` - See ban alerts",
          "`moderex.alerts.kick` - See kick alerts",
          "`moderex.alerts.mute` - See mute alerts",
          "`moderex.alerts.warn` - See warn alerts",
          "`moderex.alerts.pardon` - See pardon/unban/unmute alerts",
          "`moderex.alerts.commands` - See command monitoring alerts",
          "`moderex.alerts.nickname` - See nickname alerts",
          "`moderex.alerts.joinleave` - See join/leave alerts (in-game only)",
          "`moderex.alerts.lag` - See server lag/status alerts"
        ]
      },
      {
        type: "config",
        title: "Configuration Options",
        items: [
          "**Alert Levels**: Everyone, Watchlist Only, Off (per alert type)",
          "**Command Alert Levels**: Everyone, Watchlist Only, Blacklisted Only, Off",
          "**Toast Position**: top-right, bottom-right, top-left, bottom-left",
          "**Alert Duration**: 1-60 seconds",
          "**Sound Toggles**: Individual on/off for Punishments, Automod, Anticheat, Watchlist, Staff Chat, Commands, Nickname, Lag"
        ]
      }
    ]
  }
  // Future changelogs will be added here at the top
];

/**
 * Get all unread changelogs for the current user
 * @param {number[]} readBuilds - Array of build numbers the user has read
 * @returns {Object[]} Array of unread changelog entries
 */
window.getUnreadChangelogs = function(readBuilds = []) {
  return window.MX_CHANGELOGS.filter(log => !readBuilds.includes(log.build));
};

/**
 * Get the latest changelog
 * @returns {Object} The most recent changelog entry
 */
window.getLatestChangelog = function() {
  return window.MX_CHANGELOGS[0];
};

/**
 * Get changelog by build number
 * @param {number} build - The build number
 * @returns {Object|null} The changelog entry or null
 */
window.getChangelogByBuild = function(build) {
  return window.MX_CHANGELOGS.find(log => log.build === build) || null;
};
