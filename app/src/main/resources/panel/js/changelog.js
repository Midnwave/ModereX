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
    build: 288,
    version: "2.0dev-288",
    date: "2026-02-08",
    title: "Dev Build License System + Admin Panel Overhaul",
    sections: [
      { type: "new", title: "New Features", items: [
        "**Dev Build License System** - Licensed development builds now require validation via Cloudflare Workers API with RSA-2048 signature verification",
        "**Admin Panel Licenses** - Gateway admin panel now supports creating, revoking, and managing dev build licenses with token watermarking",
        "**Licensed JAR Builder** - Admin panel can generate licensed builds directly from gateway with embedded unique tokens",
        "**Server Suspension** - Admins can now suspend servers from the gateway admin panel, preventing reconnection until unsuspended"
      ]},
      { type: "fixed", title: "Bug Fixes", items: [
        "**Gateway CPU Stats** - CPU usage now shows actual calculated percentage instead of hardcoded 0%",
        "**Connection History** - Connection history chart now displays real historical data stored in database instead of repeated current snapshot",
        "**Gateway Metrics** - Metrics now stored hourly in gateway_metrics_history table for accurate trend tracking"
      ]},
      { type: "technical", title: "Technical Changes", items: [
        "**LicenseManager** - Validates license tokens on plugin startup and periodically (every 30 minutes) with automatic plugin disabling if invalid",
        "**LicenseValidator** - RSA signature verification ensures Cloudflare Workers API responses cannot be tampered with or faked",
        "**Gradle buildLicensed** - New Gradle task embeds unique license tokens into JAR files at build time",
        "**Gateway Build Script** - Node.js script (build-licensed.js) automates cloning, building, and storing licensed JARs",
        "**Metrics Collection** - Gateway now stores snapshots every hour with 7-day retention and automatic cleanup",
        "**CPU Calculation** - Real CPU usage calculated using process.cpuUsage() with user + system time tracking",
        "**Suspension System** - Database-backed server suspension with immediate connection blocking and admin panel UI"
      ]},
      { type: "permissions", title: "New Permissions", items: [
        "No new player-facing permissions - License system operates at plugin/server level",
        "Gateway admin access controlled via Cloudflare Access email domain authentication"
      ]},
      { type: "config", title: "Configuration", items: [
        "**Cloudflare Workers** - License API requires KV namespace (LICENSE_TOKENS) and environment secrets (PRIVATE_KEY, ADMIN_SECRET)",
        "**Gateway Database** - New tables: license_builds, suspended_servers, gateway_metrics_history",
        "**Build Properties** - Licensed builds store token in license-token.properties and public key in license-public-key.pem"
      ]}
    ]
  },
  {
    build: 286,
    version: "2.0dev-286",
    date: "2026-02-08",
    title: "Permissions Tab Overhaul & LuckPerms Auto-Sync",
    sections: [
      { type: "new", title: "New Features", items: [
        "**LuckPerms Auto-Sync** - Permission changes, rank assignments, and player group changes now automatically sync to LuckPerms in real-time",
        "**Enhanced Player Search** - Player assignment search now shows larger avatars, UUIDs, and smooth hover animations with chevron indicators",
        "**Click Animations** - All permission toggle buttons now have smooth click animations and visual feedback"
      ]},
      { type: "improved", title: "Improvements", items: [
        "**Red Deny Button** - Deny permission (X) button is now properly colored red with red glow when active for better visibility",
        "**Permission Button Styling** - Added glow effects to active allow/deny buttons, hover transforms, and ripple-like click animations",
        "**Player Rank Display** - Player rank assignment header now has gradient background, larger avatar with border, and shows full UUID",
        "**Rank Chip Interactions** - Player rank chips now have hover lift effect, smooth remove button rotation, and better visual hierarchy",
        "**Search Results UI** - Improved player search dropdown with borders, hover indicators, avatar scaling, and left accent bar"
      ]},
      { type: "technical", title: "Technical Changes", items: [
        "**syncRankToLuckPerms()** - New method automatically syncs rank permissions, display name, prefix, suffix, and weight to LuckPerms groups",
        "**syncPlayerRankToLuckPerms()** - New method automatically adds/removes LuckPerms group inheritance nodes when players are assigned/removed from ranks",
        "**Real-Time Sync** - setRankPermission(), setPlayerRank(), and removePlayerRank() now trigger auto-sync if LuckPerms is detected"
      ]}
    ]
  },
  {
    build: 285,
    version: "2.0dev-285",
    date: "2026-02-08",
    title: "Evidence Fix, Admin Panel Fix & Getting Started Rewrite",
    sections: [
      { type: "new", title: "New Features", items: [
        "**Getting Started Rewrite** - Expanded from 11 to 21 comprehensive sections with detailed guides for every feature",
        "**New Guide Sections** - Added First Steps, Templates, Evidence System, Watchlist, Server Status, Replays, Messages, Permissions, Shortcuts, Do's & Don'ts"
      ]},
      { type: "fixed", title: "Bug Fixes", items: [
        "**Evidence Linking** - Evidence files and activity logs are now properly attached to punishments created from the web panel",
        "**Admin Dashboard Activity** - Activity items now show full text content instead of just timestamps",
        "**Admin Gateway Health** - Gateway status now shows correct uptime, health status, memory and message rate",
        "**Admin Version Chart** - Version distribution chart now populated from connected server data",
        "**Admin Connection Chart** - Connection history placeholder now shows actual chart data"
      ]},
      { type: "technical", title: "Technical Changes", items: [
        "**Activity Log Storage** - Added getEntryById() for looking up individual log entries by database ID",
        "**Gateway Health Data** - Added healthy, cpuUsage, memoryUsage, messagesPerSecond fields to gateway health messages",
        "**Dashboard Data** - Added versionDistribution and connectionHistory to dashboard data, mapped audit log to activity format"
      ]}
    ]
  },
  {
    build: 284,
    version: "2.0dev-284",
    date: "2026-02-08",
    title: "Replay 3D Overhaul & Gateway Player Profile Fix",
    sections: [
      {
        type: "new",
        title: "New Features",
        items: [
          "**Real Block Textures** - Replay viewer now loads actual Minecraft block textures from PrismarineJS atlas with per-face UV mapping",
          "**Shader-Based Tiling** - Custom atlas shader enables greedy mesh merging for textured blocks with correct UV wrapping",
          "**Noise-Grain Sound Engine** - All replay sounds rebuilt using multi-grain filtered noise synthesis for realistic Minecraft-like audio"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Player Model** - Correct Steve proportions, body pivot for sneaking, shoulder/hip limb pivots, proper overlay layers",
          "**Skin UV Mapping** - Fixed face order mapping for models rotated to face -Z (left/right/front/back corrected)",
          "**Walk Animation** - Calibrated to Minecraft walk speed (4.317 b/s) with natural sinusoidal limb swing and speed scaling",
          "**Punishment Dropdowns** - Template and punishment type dropdowns now stretch full width inside modals"
        ]
      },
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Gateway Player Profile** - Fixed player details (IP, nicknames, commands, automod, sessions) showing empty on gateway panel",
          "**Permission Check** - Gateway session now correctly found for permission-gated handler methods"
        ]
      }
    ]
  },
  {
    build: 280,
    version: "2.0dev-280",
    date: "2026-02-08",
    title: "Replay Viewer Overhaul & Config Permission Gates",
    sections: [
      {
        type: "new",
        title: "New Features",
        items: [
          "**Config Permission Gates** - Each config section is now gated behind its permission with visual lock overlay and backend enforcement",
          "**Skin Loading** - Replay viewer now loads real Minecraft player skins via multi-proxy fallback (Crafatar, mc-heads, Visage)"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Replay Performance** - Added dirty-flag rendering, frustum culling, batched terrain loading, and O(1) block lookups",
          "**Walk Animation** - Arms and legs now rotate from shoulder/hip pivot points for more realistic movement",
          "**Skin Cache** - Loaded skins are cached globally so revisiting players no longer re-fetches textures",
          "**Admin Panel** - Removed ADMIN_DEV_KEY requirement for gateway admin connections"
        ]
      },
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Memory Leaks** - Fixed missing disposal of fallback ground, player textures, and renderer WebGL context on viewer cleanup",
          "**Panel Dropdowns** - Fixed custom select dropdowns not rendering in gateway and standalone panel",
          "**Discord Modal** - Fixed close button layout in Discord support modal",
          "**Logo Color** - Fixed hardcoded blue gradient in sidebar logo, now uses theme variables"
        ]
      },
      {
        type: "permissions",
        title: "New Permissions",
        items: [
          "**moderex.admin.chat** - Required to configure chat management settings",
          "**moderex.admin.kickall** - Required to use kick all functionality",
          "**moderex.admin.discord** - Required to configure Discord integration"
        ]
      }
    ]
  },
  {
    build: 278,
    version: "2.0dev-278",
    date: "2026-02-07",
    title: "Configuration & Settings Fixes",
    sections: [
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Server Status Polling** - Increased refresh rate from 2s to 1s for more responsive monitoring",
          "**Loading Bar** - No longer flashes during server status polling (excluded frequent requests)",
          "**Discord Integration** - Added Discord invite link field for ban/kick appeal messages",
          "**Configuration Permissions** - Each config section now gated behind its permission (warning, mute, lockdown, etc.)"
        ]
      },
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Panel Notifications** - Removed empty/unused Panel Notifications card from My Settings"
        ]
      }
    ]
  },
  {
    build: 277,
    version: "2.0dev-277",
    date: "2026-02-07",
    title: "Web Panel UI Overhaul",
    sections: [
      {
        type: "new",
        title: "New Features",
        items: [
          "**Custom Duration Picker** - Structured time picker with Years/Months/Weeks/Days/Hours/Minutes fields replaces free-text input",
          "**Permanent Punishment Toggle** - Permission-gated permanent option in duration picker (requires moderex.punish.permanent)",
          "**Custom Dropdown Menus** - All native selects replaced with styled custom dropdowns matching panel theme",
          "**Permission Search Filter** - Search bar to filter permissions by name in the permissions tab",
          "**Permission Bulk Actions** - Allow All and Reset All buttons per permission category",
          "**Permission Count Badges** - Green/red badges showing allowed/denied count per category",
          "**Citizens Download Link** - Replay settings now shows download link when Citizens is not detected"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Permission Categories** - Icons for each category, shorter permission names with full name in tooltip",
          "**Reason Column** - Increased truncation limit from 15 to 40 characters with wider column",
          "**CSS Variables** - Replaced 100+ hardcoded color values with CSS custom properties for consistent theming",
          "**Duration Auto-Hide** - Duration picker automatically hidden for Kick/Warn punishment types",
          "**Replay Duration** - Fixed duration calculation from server timestamps instead of missing field"
        ]
      },
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Modal Close** - All modals now close when clicking outside (overlay click-to-close)",
          "**Replay Toggles** - Fixed broken toggle switches using wrong CSS class (switch vs toggle-switch-label)",
          "**Replay Duration** - Fixed duration showing 0:00 by calculating from startTime/endTime",
          "**Discord Form** - Removed duplicate CSS toggle definition causing inconsistent styling",
          "**Template Duration** - Templates now properly populate the duration picker fields"
        ]
      }
    ]
  },
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
  }
  // Older changelogs removed - keeping only last 3 builds
];
