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
