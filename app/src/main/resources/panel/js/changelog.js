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
    build: 324,
    version: "2.0dev-324",
    date: "2026-02-14",
    title: "Permission System Overhaul & Staff Audit Log",
    sections: [
      {
        type: "new",
        title: "New Features",
        items: [
          "**Staff Audit Log** - New audit log tab tracks all staff actions (automod changes, template edits, config updates, rank changes)",
          "**Permission Rename** - Activity log permissions renamed from moderex.history.* to moderex.logs.* (more intuitive)",
          "**Granular Audit Permissions** - 8 new moderex.stafflogs.* permissions for viewing specific staff action types",
          "**Audit Log Export** - Export audit logs to CSV with moderex.stafflogs.export permission",
          "**Debug Mode Hints** - Debug mode now shows required permissions in error messages and locked features (config.yml: general.debug)"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Staff Action Logging** - All web panel admin actions now logged: automod (3 actions), templates (3 actions), config (5 sections), ranks (3 actions), permissions (2 actions)",
          "**Permission Logging** - 21 new activity types track staff configuration changes and administrative actions",
          "**Permission Hints** - When debug mode enabled, config page locked features show exact permission required",
          "**Permission Debugging** - Enhanced hasPermission() logs all permission checks when debug mode enabled"
        ]
      },
      {
        type: "permissions",
        title: "New Permissions",
        items: [
          "**moderex.logs.*** - Renamed from moderex.history.* (includes .chat, .commands, .automod, .bans, .mutes, .warns, .kicks, .nick, .sessions)",
          "**moderex.stafflogs.*** - View all staff action logs",
          "**moderex.stafflogs.automod** - View automod rule changes",
          "**moderex.stafflogs.templates** - View template changes",
          "**moderex.stafflogs.config** - View configuration changes",
          "**moderex.stafflogs.punishments** - View punishment actions",
          "**moderex.stafflogs.permissions** - View rank/permission changes",
          "**moderex.stafflogs.webpanel** - View web panel actions",
          "**moderex.stafflogs.commands** - View admin command usage",
          "**moderex.stafflogs.export** - Export audit logs to CSV"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "**Backend** - Added GET_AUDIT_LOG WebSocket handler with permission-based filtering",
          "**Database** - Activity log now stores 21 additional staff action types",
          "**Permission Validation** - All web panel handlers verified for proper permission checks",
          "**Debug Mode API** - Added PermissionUtil.isDebugMode() and sendPermissionDenied() helpers"
        ]
      }
    ]
  },
  {
    build: 323,
    version: "2.0dev-323",
    date: "2026-02-11",
    title: "Panel URL Path Fix & Dev Build Guide",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Panel URL Routing** - Fixed panel not loading when URL has trailing slash (e.g. /z392j/)"
        ]
      },
      {
        type: "new",
        title: "New Features",
        items: [
          "**Dev Build Guide** - Added comprehensive tester guide with permissions, commands, and setup instructions"
        ]
      }
    ]
  },
  {
    build: 319,
    version: "2.0dev-319",
    date: "2026-02-10",
    title: "Website Account Dropdown & Admin Panel Fixes",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Account Dropdown** - Fixed dropdown not showing on website (CSS class mismatch between JS and CSS)",
          "**Dropdown Styling** - Log Out button now properly styled to match Admin Panel link",
          "**Admin User Actions** - Fixed UUID not being sent to gateway for user details, password reset, and account delete"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Admin Button** - Admin accounts see Admin Panel link in account dropdown",
          "**Click Outside** - Clicking outside the account dropdown properly closes it"
        ]
      }
    ]
  },
  {
    build: 318,
    version: "2.0dev-318",
    date: "2026-02-10",
    title: "Website Auth Fixes & Gateway .env Loading",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Website Reviews** - Fixed session token key mismatch preventing reviews from logged-in users",
          "**Gateway .env Loading** - Gateway now reads .env file for ADMIN_UUIDS and other config (was ignoring it)",
          "**Turnstile Error 400020** - Turnstile script only loads when site key is configured (no more console spam)",
          "**Browser Cache** - Added cache-busting version parameter to script tags"
        ]
      }
    ]
  },
  {
    build: 317,
    version: "2.0dev-317",
    date: "2026-02-10",
    title: "Security Hardening, Replay Fix & Gateway Improvements",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Replay Terrain Rendering** - Fixed THREE.js shader error (vMapUv undeclared) that prevented all terrain/blocks from rendering",
          "**Replay Skin Loading** - Reordered skin proxies to avoid CORS failures on Crafatar",
          "**Website Sign-In** - Fixed hardcoded gateway URL that broke all website-gateway communication",
          "**Website Sign-Out** - Now revokes server-side sessions and clears all stored data",
          "**Offline Server Names** - Servers that disconnect now retain their name instead of showing Unknown Server"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Admin Panel Security** - Removed globally-exposed functions from browser console (event delegation)",
          "**Replay Loading Animation** - Smooth slide transitions between loading phases",
          "**Promo Ads Simplified** - Apply Now links directly to Discord invite",
          "**Admin Nav Button** - Admin accounts now see Admin Panel link in website navigation"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "**Gateway** - Added isAdmin to session validate response, known_servers DB table for offline name persistence",
          "**THREE.js r128 Compat** - Fixed colorSpace to encoding, vMapUv to vUv for shader compatibility"
        ]
      }
    ]
  },
  {
    build: 315,
    version: "2.0dev-315",
    date: "2026-02-10",
    title: "Dev Tester Ads, Turnstile Fix & Server Switch Fix",
    sections: [
      {
        type: "new",
        title: "New Features",
        items: [
          "**Dev Build Tester Ads** - Tester application promotions on server start, staff join, update command, Discord webhook, and web panel dashboard",
          "**Website Announcement Bar** - Dismissible promo bar on the landing page for dev tester applications"
        ]
      },
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Server Switch** - Fixed persistent redirect loop after authenticating on gateway panel (use WebSocket switch instead of page reload)",
          "**Cloudflare Turnstile** - Fixed error 400020 by using valid Cloudflare test site key"
        ]
      }
    ]
  },
  {
    build: 313,
    version: "2.0dev-313",
    date: "2026-02-10",
    title: "Security, Bug Fixes & Admin Overhaul",
    sections: [
      {
        type: "new",
        title: "New Features",
        items: [
          "**Cloudflare Turnstile** - Bot protection added to website sign-in (CAPTCHA verification required)",
          "**Admin Panel Auth** - UUID-based admin login with TOTP two-factor authentication",
          "**Admin Users Tab** - View, search, and manage registered user accounts from the admin panel",
          "**Mojang UUID Validation** - Password auth now validates official Minecraft/Floodgate UUIDs, cracked accounts use token auth",
          "**Admin Animated Background** - Professional blue/purple gradient with floating particle effects"
        ]
      },
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Announcements** - Fixed admin announcements not reaching web panels (case mismatch in message type)",
          "**Server Switch** - Fixed infinite reload loop when switching between servers on the gateway panel",
          "**Replay Terrain** - Fixed blocks not rendering in 3D replay viewer (race condition with chunk data)"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Token Auth** - Marked as deprecated (Legacy) with warning notice, password auth recommended",
          "**Admin Security** - Removed Cloudflare Access dependency, admin UUIDs stored in .env file",
          "**Feature Flags** - Removed placeholder feature flags tab from admin panel"
        ]
      }
    ]
  },
  {
    build: 312,
    version: "2.0dev-312",
    date: "2026-02-10",
    title: "Website Auth, Reviews & UUID Cleanup",
    sections: [
      {
        type: "new",
        title: "New Features",
        items: [
          "**Website Auth** - Added password-based sign-in to moderex.net with account dropdown in nav bar",
          "**Review System** - Users can rate ModereX 1-5 stars with a description on the homepage",
          "**Reviews API** - New GET/POST /api/reviews gateway endpoints for the review board"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Token Auth** - Removed UUID dev-login from the panel token auth tab",
          "**CORS** - Added Authorization header to gateway CORS for Bearer token support"
        ]
      }
    ]
  },
  {
    build: 311,
    version: "2.0dev-311",
    date: "2026-02-10",
    title: "Gateway Auth Routing Fix",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Gateway Auth** - Fixed AUTH_PASSWORD error by routing all gateway connections through global auth first",
          "**Server Redirect** - Clicking a server now does a full page redirect to /{serverId} instead of WebSocket switch",
          "**Trailing Slash** - Fixed URLs with trailing slash (e.g. /z392j/) breaking the panel"
        ]
      }
    ]
  },
  {
    build: 310,
    version: "2.0dev-310",
    date: "2026-02-09",
    title: "Sign Out & Server Redirect Fix",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Sign Out** - Fixed Log Out button calling the wrong function; now properly clears all auth data, fingerprint, and reloads",
          "**Server Redirect** - URL now updates to /{serverId}/ after switching servers from the server list",
          "**Auto-Switch** - Fixed connectGlobalPanel not storing pending server ID for auto-switch after auth"
        ]
      }
    ]
  },
  {
    build: 309,
    version: "2.0dev-309",
    date: "2026-02-09",
    title: "Auth, Security & UX Fixes",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Password Auth via Gateway** - Fixed 'Authentication method not supported via gateway: AUTH_PASSWORD' error when connecting via IP",
          "**Sign Out** - Sign out now fully clears device fingerprint, cached data, and reloads the page to prevent data leakage",
          "**Security Bypass** - Panel data no longer loads or is visible until fully authenticated (prevents inspect-element bypass)"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Gateway Server Access** - panel.moderex.net now always shows server list first; clicking a server auto-authenticates and redirects",
          "**Theme Color Storage** - Theme color is now stored in the gateway database when using gateway mode, persisting across sessions",
          "**Update Banner** - Added dismiss button to the plugin update notification bar"
        ]
      }
    ]
  },
  {
    build: 308,
    version: "2.0dev-308",
    date: "2026-02-09",
    title: "3D Replay Viewer Overhaul",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Block Rendering** - Fixed blocks not rendering due to GitHub CDN rate-limiting; switched to jsDelivr CDN with retry logic",
          "**Loading Bar** - Fixed replay loading bar never updating; now shows real progress for texture loading (0-40%) and terrain meshing (40-100%)",
          "**Sneak Pose** - Fixed sneak body tilt (now accurate 30 degrees) and Y offset (-6.2 pixels) matching Minecraft"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Walk Animation** - Accurate 2.0 cycles/sec walk cycle with 57-degree arm swing amplitude matching Minecraft",
          "**Idle Animation** - Players now have subtle arm bob animation when standing still",
          "**Attack Animation** - 250ms arm swing animation triggered by combat actions",
          "**Material Sounds** - Block break/place and footstep sounds now vary by material (stone, wood, grass, sand, gravel, glass, metal, cloth, snow)",
          "**Equipment Rendering** - Players now show held items as colored boxes and armor as colored overlays on body parts",
          "**Terrain Performance** - Uses requestIdleCallback for non-blocking chunk mesh building to prevent browser lag"
        ]
      },
      {
        type: "new",
        title: "New Features",
        items: [
          "**Equipment Data** - Backend now sends mainHand, offHand, and armor data in replay snapshots",
          "**Missing Blocks** - Added stripped logs, bamboo, cherry, tuff variants, suspicious sand/gravel to texture map"
        ]
      }
    ]
  },
  {
    build: 307,
    version: "2.0dev-307",
    date: "2026-02-09",
    title: "Secure Link Authentication System",
    sections: [
      {
        type: "new",
        title: "New Features",
        items: [
          "**Secure Link Auth** - New `/mx link` command generates a 10-digit code for secure account linking via moderex.net/link",
          "**Password Authentication** - Panel now supports username + password login with Argon2id hashing",
          "**Device Fingerprint Auto Sign-In** - Trusted devices can skip the login page automatically",
          "**Account Security Settings** - Change password, toggle auto sign-in, and revoke all sessions from the Settings page",
          "**Link Page** - New moderex.net/link page with 4-step animated flow: code entry, identity confirm, password creation, success"
        ]
      },
      {
        type: "improved",
        title: "Improvements",
        items: [
          "**Tabbed Login** - Panel login now has Sign In (password) and Legacy Token tabs",
          "**Session Management** - Persistent sessions with 30-day expiry, stored as SHA-256 hashes server-side",
          "**Announcement Broadcast** - Admin announcements now reach all connected web panels (browser + global panel clients)"
        ]
      },
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Announcement Delivery** - Fixed announcements only being sent to MC servers, not browser panel clients"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "**Argon2id Hashing** - All passwords hashed with Argon2id (memoryCost=65536, timeCost=3)",
          "**Rate Limiting** - 5 code verify attempts/IP/10min, 5 password failures → 15-min lockout",
          "**Gateway HTTP API** - 8 new REST endpoints for link verification, registration, auth, and session management",
          "**Legacy Deprecation** - `/mx gettoken` and `/mx revoketoken` marked as legacy with deprecation notices"
        ]
      }
    ]
  },
  {
    build: 296,
    version: "2.0dev-296",
    date: "2026-02-08",
    title: "License Signature Fix",
    sections: [
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**License Verification** - Fixed RSA signature verification failing due to JSON serialization mismatch between Worker and plugin",
          "**Key Pair Regeneration** - Regenerated RSA key pair to ensure public/private keys match"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "**Canonical JSON** - Both Worker and plugin now use alphabetically sorted keys for deterministic signature verification",
          "**CI Licensed Builds** - GitHub Actions now produces both licensed and unlicensed JARs on every push"
        ]
      }
    ]
  },
  {
    build: 295,
    version: "2.0dev-295",
    date: "2026-02-08",
    title: "Licensed Build Auto-Updates",
    sections: [
      {
        type: "new",
        title: "New Features",
        items: [
          "**Licensed Build Auto-Update** - Licensed dev builds now auto-update from GitHub while preserving the license token",
          "**JAR Download Button** - Admin panel shows a download button after building licensed JARs"
        ]
      },
      {
        type: "fixed",
        title: "Bug Fixes",
        items: [
          "**Download URL** - Fixed JAR download using tunnel URL to bypass Cloudflare Access"
        ]
      },
      {
        type: "technical",
        title: "Technical Changes",
        items: [
          "**License Patching** - Auto-updater patches license-token.properties into downloaded JARs via ZIP filesystem",
          "**LicenseManager API** - Added getLicenseToken() and getBuildTimestamp() getters"
        ]
      }
    ]
  },
  {
    build: 291,
    version: "2.0dev-291",
    date: "2026-02-08",
    title: "UI Modernization & Polish (Part 3)",
    sections: [
      { type: "improved", title: "Admin Panel Improvements", items: [
        "**Build Progress Bar** - Licensed JAR builds now show real-time progress with visual feedback",
        "**Progress Stages** - Build progress shows current stage (verifying, preparing, building, finalizing) with percentage",
        "**Confirmation Modal** - Building a JAR now requires confirmation before starting the 1-2 minute process",
        "**Auto-hide Progress** - Progress bar automatically hides 2 seconds after build completes"
      ]},
      { type: "technical", title: "Technical Changes", items: [
        "**Progress Reporting** - Build script now outputs structured JSON progress messages at each build stage",
        "**WebSocket Progress** - Gateway parses progress messages and forwards them to admin panel in real-time",
        "**Gradient Progress** - Progress bar uses primary→accent gradient with smooth width transitions",
        "**Build States** - Tracks 7 build stages: init (0%), verify (10%), prepare (20%), clone (30%), build (50%), finalize (85%), cleanup (95%), complete (100%)"
      ]}
    ]
  },
  {
    build: 290,
    version: "2.0dev-290",
    date: "2026-02-08",
    title: "UI Modernization & Polish (Part 2)",
    sections: [
      { type: "improved", title: "UI Improvements", items: [
        "**Unified Notification System** - Update banners now stay visible (no dismiss), announcements remain dismissible and stack below updates",
        "**Stacked Banners** - Announcement banners automatically adjust position when update banner is showing using smooth transitions",
        "**Connection History Chart** - Replaced basic bar chart with professional line graph using Chart.js with smooth curves and tooltips",
        "**Interactive Charts** - Connection History now features hover tooltips, legend, gradient fills, and responsive scaling"
      ]},
      { type: "technical", title: "Technical Changes", items: [
        "**Chart.js Integration** - Added Chart.js v4.4.0 for admin panel analytics with line graphs",
        "**Banner Offset Class** - Added .announcementBanner.offset CSS class for proper banner stacking with transition support",
        "**Chart Instance Management** - Connection chart properly destroys previous instances before creating new ones to prevent memory leaks",
        "**Canvas Rendering** - Replaced inline-styled bar chart divs with canvas element for better performance and visuals"
      ]}
    ]
  },
  {
    build: 289,
    version: "2.0dev-289",
    date: "2026-02-08",
    title: "UI Modernization & Polish (Part 1)",
    sections: [
      { type: "improved", title: "UI Improvements", items: [
        "**Custom Modals** - Replaced all browser confirm() dialogs with beautiful styled modals that match the panel theme",
        "**Modal Types** - Modals now have three visual styles (default, warning, danger) with appropriate icons and color schemes",
        "**Smooth Animations** - Modal overlays fade in with backdrop blur, content scales smoothly, and clicking outside dismisses",
        "**ESC Key Support** - Press Escape to quickly cancel any confirmation modal"
      ]},
      { type: "fixed", title: "Bug Fixes", items: [
        "**License Dates** - Fixed 'Invalid Date' error in admin panel licenses table caused by database field name mismatch (snake_case → camelCase)"
      ]},
      { type: "technical", title: "Technical Changes", items: [
        "**showConfirm() Function** - New reusable modal system with Promise-based async/await support for cleaner code",
        "**Gateway Date Mapping** - sendLicensesList() now properly maps database column names (created_at → createdAt) for frontend consumption",
        "**Modal CSS** - Added .mx-modal-overlay, .mx-modal, .mx-modal-header/.body/.footer classes with full theming support",
        "**Async Functions** - Updated 9 functions to async to support await showConfirm() (stress tests, rank management, imports)"
      ]}
    ]
  },
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
