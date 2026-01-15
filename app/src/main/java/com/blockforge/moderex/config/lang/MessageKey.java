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

    // Commands
    CMD_HISTORY_HEADER("cmdhistory.header"),
    CMD_HISTORY_ENTRY("cmdhistory.entry"),
    CMD_HISTORY_EMPTY("cmdhistory.empty"),
    CMD_HISTORY_FOOTER("cmdhistory.footer"),

    // Analytics
    ANALYTICS_HEADER("analytics.header");

    private final String path;

    MessageKey(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
