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
