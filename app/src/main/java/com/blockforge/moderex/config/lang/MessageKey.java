package com.blockforge.moderex.config.lang;

public enum MessageKey {

    // General
    PREFIX("general.prefix"),
    NO_PERMISSION("general.no-permission"),
    PLAYER_NOT_FOUND("general.player-not-found"),
    PLAYER_ONLY("general.player-only"),
    INVALID_DURATION("general.invalid-duration"),
    INVALID_ARGUMENTS("general.invalid-arguments"),
    RELOAD_SUCCESS("general.reload-success"),
    PLUGIN_INFO("general.plugin-info"),

    // Mute
    MUTE_SUCCESS("mute.success"),
    MUTE_BROADCAST("mute.broadcast"),
    MUTE_MESSAGE("mute.message"),
    MUTE_ALREADY_MUTED("mute.already-muted"),
    UNMUTE_SUCCESS("unmute.success"),
    UNMUTE_BROADCAST("unmute.broadcast"),
    UNMUTE_NOT_MUTED("unmute.not-muted"),
    MUTED_CHAT_ATTEMPT("mute.chat-attempt"),

    // Ban
    BAN_SUCCESS("ban.success"),
    BAN_BROADCAST("ban.broadcast"),
    BAN_DISCONNECT("ban.disconnect"),
    BAN_ALREADY_BANNED("ban.already-banned"),
    UNBAN_SUCCESS("unban.success"),
    UNBAN_BROADCAST("unban.broadcast"),
    UNBAN_NOT_BANNED("unban.not-banned"),

    // IP Ban
    IPBAN_SUCCESS("ipban.success"),
    IPBAN_BROADCAST("ipban.broadcast"),
    IPBAN_DISCONNECT("ipban.disconnect"),

    // Kick
    KICK_SUCCESS("kick.success"),
    KICK_BROADCAST("kick.broadcast"),
    KICK_DISCONNECT("kick.disconnect"),

    // Warn
    WARN_SUCCESS("warn.success"),
    WARN_BROADCAST("warn.broadcast"),
    WARN_MESSAGE("warn.message"),
    WARN_CLEARED("warn.cleared"),
    WARN_CLEARED_BROADCAST("warn.cleared-broadcast"),

    // Staff Chat
    STAFFCHAT_FORMAT("staffchat.format"),
    STAFFCHAT_ENABLED("staffchat.enabled"),
    STAFFCHAT_DISABLED("staffchat.disabled"),
    STAFFCHAT_HELP_REQUEST("staffchat.help-request"),

    // Vanish
    VANISH_ENABLED("vanish.enabled"),
    VANISH_DISABLED("vanish.disabled"),

    // Chat
    CHAT_DISABLED("chat.disabled"),
    CHAT_ENABLED("chat.enabled"),
    CHAT_SLOWMODE_SET("chat.slowmode-set"),
    CHAT_SLOWMODE_MESSAGE("chat.slowmode-message"),
    CHAT_CLEARED("chat.cleared"),
    CHAT_DISABLED_MESSAGE("chat.disabled-message"),

    // Command Blacklist
    CMD_BLACKLIST_SUCCESS("cmdblacklist.success"),
    CMD_BLACKLIST_REMOVED("cmdblacklist.removed"),
    CMD_BLACKLIST_BLOCKED("cmdblacklist.blocked"),

    // Modlog
    MODLOG_HEADER("modlog.header"),
    MODLOG_ENTRY("modlog.entry"),
    MODLOG_EMPTY("modlog.empty"),
    MODLOG_FOOTER("modlog.footer"),

    // Punish GUI
    GUI_PUNISH_TITLE("gui.punish.title"),
    GUI_PUNISH_SELECT_TITLE("gui.punish.select-title"),
    GUI_MUTE_TITLE("gui.mute.title"),
    GUI_BAN_TITLE("gui.ban.title"),

    // Automod
    AUTOMOD_BLOCKED("automod.blocked"),
    AUTOMOD_ALERT("automod.alert"),
    AUTOMOD_SPAM_BLOCKED("automod.spam-blocked"),

    // Anticheat
    ANTICHEAT_ALERT("anticheat.alert"),

    // Watchlist
    WATCHLIST_ALERT("watchlist.alert"),

    // Web Panel
    WEBPANEL_CONNECTED("webpanel.connected"),
    WEBPANEL_DISCONNECTED("webpanel.disconnected"),

    // Update Checker
    UPDATE_AVAILABLE("update.available"),
    UPDATE_CURRENT("update.current"),

    // GitHub Auto-Update
    GITHUB_UPDATE_AVAILABLE("github.update_available"),
    GITHUB_UPDATE_DOWNLOADED("github.update_downloaded"),
    GITHUB_UPDATE_PENDING("github.update_pending"),
    GITHUB_UPDATE_DOWNLOADING("github.update_downloading"),
    GITHUB_UPDATE_FAILED("github.update_failed"),

    // Commands
    CMD_HISTORY_HEADER("cmdhistory.header"),
    CMD_HISTORY_ENTRY("cmdhistory.entry"),
    CMD_HISTORY_EMPTY("cmdhistory.empty"),
    CMD_HISTORY_FOOTER("cmdhistory.footer"),

    // Analytics
    ANALYTICS_HEADER("analytics.header"),

    // IP Mute
    IPMUTE_SUCCESS("ipmute.success"),
    IPMUTE_BROADCAST("ipmute.broadcast"),
    IPMUTE_MESSAGE("ipmute.message"),
    IPMUTE_ALREADY_MUTED("ipmute.already-muted"),

    // Temporary Punishments
    TEMPBAN_SUCCESS("tempban.success"),
    TEMPBAN_BROADCAST("tempban.broadcast"),
    TEMPBAN_DURATION_REQUIRED("tempban.duration-required"),
    TEMPMUTE_SUCCESS("tempmute.success"),
    TEMPMUTE_BROADCAST("tempmute.broadcast"),
    TEMPMUTE_DURATION_REQUIRED("tempmute.duration-required"),

    // Unwarn
    UNWARN_SUCCESS("unwarn.success"),
    UNWARN_BROADCAST("unwarn.broadcast"),
    UNWARN_NOT_WARNED("unwarn.not-warned"),
    UNWARN_BY_ID("unwarn.by-id"),

    // Check Commands
    CHECKBAN_HEADER("checkban.header"),
    CHECKBAN_BANNED("checkban.banned"),
    CHECKBAN_NOT_BANNED("checkban.not-banned"),
    CHECKBAN_DETAILS("checkban.details"),
    CHECKMUTE_HEADER("checkmute.header"),
    CHECKMUTE_MUTED("checkmute.muted"),
    CHECKMUTE_NOT_MUTED("checkmute.not-muted"),
    CHECKMUTE_DETAILS("checkmute.details"),
    CHECKWARN_HEADER("checkwarn.header"),
    CHECKWARN_HAS_WARNINGS("checkwarn.has-warnings"),
    CHECKWARN_NO_WARNINGS("checkwarn.no-warnings"),
    CHECKWARN_WARNING_ENTRY("checkwarn.warning-entry"),
    CHECK_HEADER("check.header"),
    CHECK_FOOTER("check.footer"),
    CHECK_STATUS("check.status"),
    CHECK_UUID("check.uuid"),
    CHECK_BAN_ACTIVE("check.ban-active"),
    CHECK_BAN_NONE("check.ban-none"),
    CHECK_MUTE_ACTIVE("check.mute-active"),
    CHECK_MUTE_NONE("check.mute-none"),
    CHECK_WARNINGS("check.warnings"),
    CHECK_WARNINGS_NONE("check.warnings-none"),
    CHECK_IP("check.ip"),
    CHECK_IP_OFFLINE("check.ip-offline"),
    CHECK_REGION("check.region"),
    CHECK_ALTS("check.alts"),
    CHECK_TOTAL_PUNISHMENTS("check.total-punishments"),

    // History Commands
    HISTORY_HEADER("history.header"),
    HISTORY_ENTRY("history.entry"),
    HISTORY_EMPTY("history.empty"),
    HISTORY_FOOTER("history.footer"),
    HISTORY_TYPE_FILTER("history.type-filter"),
    STAFFHISTORY_HEADER("staffhistory.header"),
    STAFFHISTORY_ENTRY("staffhistory.entry"),
    STAFFHISTORY_EMPTY("staffhistory.empty"),
    STAFFHISTORY_FOOTER("staffhistory.footer"),
    WARNINGS_HEADER("warnings.header"),
    WARNINGS_ENTRY("warnings.entry"),
    WARNINGS_EMPTY("warnings.empty"),
    WARNINGS_SELF_EMPTY("warnings.self-empty"),
    WARNINGS_COUNT("warnings.count"),
    BANLIST_HEADER("banlist.header"),
    BANLIST_ENTRY("banlist.entry"),
    BANLIST_EMPTY("banlist.empty"),
    BANLIST_FOOTER("banlist.footer"),
    MUTELIST_HEADER("mutelist.header"),
    MUTELIST_ENTRY("mutelist.entry"),
    MUTELIST_EMPTY("mutelist.empty"),
    MUTELIST_FOOTER("mutelist.footer"),
    WARNLIST_HEADER("warnlist.header"),
    WARNLIST_ENTRY("warnlist.entry"),
    WARNLIST_EMPTY("warnlist.empty"),
    WARNLIST_FOOTER("warnlist.footer"),

    // Account & IP Commands
    DUPEIP_HEADER("dupeip.header"),
    DUPEIP_ENTRY("dupeip.entry"),
    DUPEIP_IP_ENTRY("dupeip.ip-entry"),
    DUPEIP_EMPTY("dupeip.empty"),
    DUPEIP_COUNT("dupeip.count"),
    DUPEIP_SHARED_IP("dupeip.shared-ip"),
    IPHISTORY_HEADER("iphistory.header"),
    IPHISTORY_ENTRY("iphistory.entry"),
    IPHISTORY_EMPTY("iphistory.empty"),
    IPHISTORY_COUNT("iphistory.count"),
    IPHISTORY_CONSOLE_ONLY("iphistory.console-only"),
    IPREPORT_HEADER("ipreport.header"),
    IPREPORT_ENTRY("ipreport.entry"),
    IPREPORT_PLAYER_ENTRY("ipreport.player-entry"),
    IPREPORT_EMPTY("ipreport.empty"),
    IPREPORT_COUNT("ipreport.count"),
    GEOIP_HEADER("geoip.header"),
    GEOIP_COUNTRY("geoip.country"),
    GEOIP_REGION("geoip.region"),
    GEOIP_CITY("geoip.city"),
    GEOIP_IP("geoip.ip"),
    GEOIP_NOT_AVAILABLE("geoip.not-available"),
    GEOIP_DATABASE_MISSING("geoip.database-missing"),
    LASTUUID_FORMAT("lastuuid.format"),
    LASTUUID_NOT_FOUND("lastuuid.not-found"),
    NAMEHISTORY_HEADER("namehistory.header"),
    NAMEHISTORY_ENTRY("namehistory.entry"),
    NAMEHISTORY_CURRENT("namehistory.current"),
    NAMEHISTORY_EMPTY("namehistory.empty"),
    NAMEHISTORY_NOT_AVAILABLE("namehistory.not-available"),

    // Admin Commands
    LOCKDOWN_ENABLED("lockdown.enabled"),
    LOCKDOWN_DISABLED("lockdown.disabled"),
    LOCKDOWN_BROADCAST_ENABLED("lockdown.broadcast-enabled"),
    LOCKDOWN_BROADCAST_DISABLED("lockdown.broadcast-disabled"),
    LOCKDOWN_KICK_MESSAGE("lockdown.kick-message"),
    LOCKDOWN_ALREADY_ENABLED("lockdown.already-enabled"),
    LOCKDOWN_ALREADY_DISABLED("lockdown.already-disabled"),
    LOCKDOWN_STATUS("lockdown.status"),
    LOCKDOWN_SCOPE_LOCAL("lockdown.scope-local"),
    LOCKDOWN_SCOPE_GLOBAL("lockdown.scope-global"),
    PRUNEHISTORY_SUCCESS("prunehistory.success"),
    PRUNEHISTORY_CONFIRM("prunehistory.confirm"),
    PRUNEHISTORY_EMPTY("prunehistory.empty"),
    PRUNEHISTORY_DURATION_INFO("prunehistory.duration-info"),
    STAFFROLLBACK_SUCCESS("staffrollback.success"),
    STAFFROLLBACK_CONFIRM("staffrollback.confirm"),
    STAFFROLLBACK_EMPTY("staffrollback.empty"),
    STAFFROLLBACK_DURATION_INFO("staffrollback.duration-info"),
    STAFFROLLBACK_BROADCAST("staffrollback.broadcast"),

    // ModereX Subcommands
    MODEREX_HELP_HEADER("moderex.help-header"),
    MODEREX_HELP_ENTRY("moderex.help-entry"),
    MODEREX_HELP_FOOTER("moderex.help-footer"),
    MODEREX_ALLOW_ADDED("moderex.allow.added"),
    MODEREX_ALLOW_REMOVED("moderex.allow.removed"),
    MODEREX_ALLOW_CHECK_ALLOWED("moderex.allow.check-allowed"),
    MODEREX_ALLOW_CHECK_NOT_ALLOWED("moderex.allow.check-not-allowed"),
    MODEREX_ALLOW_LIST_HEADER("moderex.allow.list-header"),
    MODEREX_ALLOW_LIST_ENTRY("moderex.allow.list-entry"),
    MODEREX_ALLOW_LIST_EMPTY("moderex.allow.list-empty"),
    MODEREX_UNLINK_SUCCESS("moderex.unlink.success"),
    MODEREX_UNLINK_CONFIRM("moderex.unlink.confirm"),
    MODEREX_UNLINK_NO_ASSOCIATIONS("moderex.unlink.no-associations"),
    MODEREX_INFO_HEADER("moderex.info.header"),
    MODEREX_INFO_CONNECTION_POOL("moderex.info.connection-pool"),
    MODEREX_INFO_ACTIVE_CONNECTIONS("moderex.info.active-connections"),
    MODEREX_INFO_IDLE_CONNECTIONS("moderex.info.idle-connections"),
    MODEREX_INFO_TOTAL_PUNISHMENTS("moderex.info.total-punishments"),
    MODEREX_INFO_ACTIVE_BANS("moderex.info.active-bans"),
    MODEREX_INFO_ACTIVE_MUTES("moderex.info.active-mutes"),
    MODEREX_INFO_ACTIVE_WARNINGS("moderex.info.active-warnings"),
    MODEREX_INFO_DATABASE_TYPE("moderex.info.database-type"),
    MODEREX_SERVERS_HEADER("moderex.servers.header"),
    MODEREX_SERVERS_ENTRY("moderex.servers.entry"),
    MODEREX_SERVERS_EMPTY("moderex.servers.empty"),
    MODEREX_SERVERS_LOCAL("moderex.servers.local"),
    MODEREX_REVEAL_FORMAT("moderex.reveal.format"),
    MODEREX_REVEAL_NOT_FOUND("moderex.reveal.not-found"),
    MODEREX_BROADCAST_SENT("moderex.broadcast.sent"),
    MODEREX_BROADCAST_FORMAT("moderex.broadcast.format"),
    MODEREX_BROADCAST_RECEIVED("moderex.broadcast.received"),
    MODEREX_TIMEZONE_CURRENT("moderex.timezone.current"),
    MODEREX_TIMEZONE_SET("moderex.timezone.set"),
    MODEREX_TIMEZONE_INVALID("moderex.timezone.invalid"),
    MODEREX_TIMEZONE_LIST_HEADER("moderex.timezone.list-header"),
    MODEREX_TIMEZONE_LIST_ENTRY("moderex.timezone.list-entry"),
    MODEREX_RESET_DATABASE_SUCCESS("moderex.reset-database.success"),
    MODEREX_RESET_DATABASE_CONFIRM("moderex.reset-database.confirm"),
    MODEREX_RESET_DATABASE_CONSOLE_ONLY("moderex.reset-database.console-only"),
    MODEREX_RESET_TEMPLATES_SUCCESS("moderex.reset-templates.success"),
    MODEREX_RESET_TEMPLATES_SUCCESS_ALL("moderex.reset-templates.success-all"),
    MODEREX_RESET_TEMPLATES_CONFIRM("moderex.reset-templates.confirm"),
    MODEREX_RESET_TEMPLATES_CONFIRM_ALL("moderex.reset-templates.confirm-all"),
    MODEREX_ADD_LOGIN_SUCCESS("moderex.add-login.success"),
    MODEREX_ADD_LOGIN_CONSOLE_ONLY("moderex.add-login.console-only"),
    MODEREX_ACCEPT_SUCCESS("moderex.accept.success"),
    MODEREX_ACCEPT_ALREADY_ACCEPTED("moderex.accept.already-accepted"),
    MODEREX_ACCEPT_CONSOLE_ONLY("moderex.accept.console-only"),
    MODEREX_LICENSE_HEADER("moderex.license.header"),
    MODEREX_LICENSE_LICENSE_TYPE("moderex.license.license-type"),
    MODEREX_LICENSE_VERSION("moderex.license.version"),
    MODEREX_LICENSE_AUTHOR("moderex.license.author"),
    MODEREX_LICENSE_WEBSITE("moderex.license.website"),
    MODEREX_UPGRADE_SUCCESS("moderex.upgrade.success"),
    MODEREX_UPGRADE_ALREADY_LATEST("moderex.upgrade.already-latest"),
    MODEREX_UPGRADE_CONSOLE_ONLY("moderex.upgrade.console-only"),
    MODEREX_UPGRADE_IN_PROGRESS("moderex.upgrade.in-progress"),

    // Punishment Flags
    FLAGS_DELETE_SUCCESS("flags.delete-success"),
    FLAGS_DELETE_REVERTED_TEMPLATE("flags.delete-reverted-template"),
    FLAGS_MODIFY_SUCCESS("flags.modify-success"),
    FLAGS_NO_OVERRIDE("flags.no-override"),
    FLAGS_GLOBAL("flags.global"),
    FLAGS_SILENT("flags.silent"),
    FLAGS_EXTRA_SILENT("flags.extra-silent"),
    FLAGS_HIDDEN("flags.hidden"),
    DELETE_SUCCESS("flags.delete-success"),
    DELETE_REVERTED_TEMPLATE("flags.delete-reverted-template"),
    MODIFY_SUCCESS("flags.modify-success"),

    // Target Resolution
    TARGET_INVALID("target.invalid"),
    TARGET_NOT_FOUND("target.not-found"),
    TARGET_OFFLINE_NO_IP("target.offline-no-ip"),
    TARGET_MULTIPLE_FOUND("target.multiple-found"),

    // Errors
    ERROR_DATABASE("error.database"),
    ERROR_INTERNAL("error.internal"),
    ERROR_NO_PERMISSION_FLAG("error.no-permission-flag"),
    ERROR_CONSOLE_ONLY_FLAG("error.console-only-flag"),
    ERROR_INVALID_PAGE("error.invalid-page"),
    ERROR_PLAYER_NOT_ONLINE("error.player-not-online"),
    ERROR_INVALID_IP("error.invalid-ip"),
    ERROR_INVALID_UUID("error.invalid-uuid"),
    ERROR_PUNISHMENT_NOT_FOUND("error.punishment-not-found"),
    ERROR_NO_ACTIVE_PUNISHMENT("error.no-active-punishment"),
    PLAYER_NOT_ONLINE("error.player-not-online"),
    PUNISHMENT_NOT_FOUND("error.punishment-not-found"),

    // AFK
    AFK_KICK_MESSAGE("afk.kick-message"),
    AFK_KICK_REASON("afk.kick-reason"),
    AFK_KICK_BROADCAST("afk.kick-broadcast"),

    // Activity Log (/log command)
    LOG_HEADER("log.header"),
    LOG_ENTRY("log.entry"),
    LOG_EMPTY("log.empty"),
    LOG_FOOTER("log.footer"),
    LOG_FILTER_INFO("log.filter-info"),

    // Replay
    REPLAY_NOT_AVAILABLE("replay.not-available"),
    REPLAY_STARTED("replay.started"),
    REPLAY_STOPPED("replay.stopped"),
    REPLAY_NOT_FOUND("replay.not-found"),
    REPLAY_PAUSED("replay.paused"),
    REPLAY_RESUMED("replay.resumed"),

    // General aliases for common patterns
    GENERAL_NO_PERMISSION("general.no-permission"),
    GENERAL_PLAYER_NOT_FOUND("general.player-not-found"),
    GENERAL_INVALID_ARGUMENTS("general.invalid-arguments");

    private final String path;

    MessageKey(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
