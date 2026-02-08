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
  }
  // Older changelogs removed - keeping only last 3 builds
];
