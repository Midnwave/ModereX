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
