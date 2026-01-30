package com.blockforge.moderex.log;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents a single activity log entry.
 */
public class ActivityLogEntry {

    private long id;
    private final UUID playerUuid;
    private final String playerName;
    private final ActivityType type;
    private final String content;
    private final String extra; // Extra data like location for signs, old username for username changes
    private final long timestamp;
    private final String server;

    public enum ActivityType {
        // Player Activity Types
        CHAT("chat", "a:chat"),
        COMMAND("command", "a:cmd"),
        SIGN("sign", "a:sign"),
        ITEM_DROP("item_drop", "a:-item"),
        ITEM_PICKUP("item_pickup", "a:+item"),
        ITEM_USE("item_use", "a:use"),
        ITEM_CONSUME("item_consume", "a:consume"),
        PROJECTILE("projectile", "a:proj"),
        ANVIL_RENAME("anvil_rename", "a:anvil"),
        SESSION_JOIN("session_join", "a:+session"),
        SESSION_QUIT("session_quit", "a:-session"),
        USERNAME_CHANGE("username_change", "a:username"),
        NICKNAME_CHANGE("nickname_change", "a:nick"),
        IP_CHANGE("ip_change", "a:ip"),

        // Punishment Types (received by player)
        PUNISHMENT_BAN("punishment_ban", "p:ban"),
        PUNISHMENT_MUTE("punishment_mute", "p:mute"),
        PUNISHMENT_WARN("punishment_warn", "p:warn"),
        PUNISHMENT_KICK("punishment_kick", "p:kick"),
        PUNISHMENT_IPBAN("punishment_ipban", "p:ipban"),
        PUNISHMENT_IPMUTE("punishment_ipmute", "p:ipmute"),
        PUNISHMENT_UNBAN("punishment_unban", "p:unban"),
        PUNISHMENT_UNMUTE("punishment_unmute", "p:unmute"),
        PUNISHMENT_UNWARN("punishment_unwarn", "p:unwarn"),

        // Automod & Anticheat (triggered for player)
        AUTOMOD_TRIGGER("automod_trigger", "a:automod"),
        ANTICHEAT_ALERT("anticheat_alert", "a:anticheat"),

        // Watchlist Alert (when watched players perform actions)
        WATCHLIST_ALERT("watchlist_alert", "w:alert"),

        // Staff Action Types (performed by staff)
        STAFF_PUNISHMENT("staff_punishment", "s:punish"),
        STAFF_PARDON("staff_pardon", "s:pardon"),
        STAFF_WATCHLIST_ADD("staff_watchlist_add", "s:+watch"),
        STAFF_WATCHLIST_REMOVE("staff_watchlist_remove", "s:-watch"),
        STAFF_WATCHLIST_NOTE("staff_watchlist_note", "s:watchnote"),
        STAFF_CMD_BLACKLIST("staff_cmd_blacklist", "s:+cmdbl"),
        STAFF_CMD_UNBLACKLIST("staff_cmd_unblacklist", "s:-cmdbl"),
        STAFF_DISGUISE_START("staff_disguise_start", "s:+disguise"),
        STAFF_DISGUISE_END("staff_disguise_end", "s:-disguise"),
        STAFF_VANISH_ENABLE("staff_vanish_enable", "s:+vanish"),
        STAFF_VANISH_DISABLE("staff_vanish_disable", "s:-vanish"),
        STAFF_REPLAY_START("staff_replay_start", "s:+replay"),
        STAFF_REPLAY_STOP("staff_replay_stop", "s:-replay"),
        STAFF_SLOWMODE_UPDATE("staff_slowmode_update", "s:slowmode"),
        STAFF_CHAT_LOCK("staff_chat_lock", "s:chatlock"),
        STAFF_LOCKDOWN("staff_lockdown", "s:lockdown"),
        STAFF_AUTOMOD_UPDATE("staff_automod_update", "s:automod"),
        STAFF_CLEAR_CHAT("staff_clear_chat", "s:clearchat"),
        STAFF_KICK_ALL("staff_kick_all", "s:kickall");

        private final String key;
        private final String shortKey;

        ActivityType(String key, String shortKey) {
            this.key = key;
            this.shortKey = shortKey;
        }

        public String getKey() {
            return key;
        }

        public String getShortKey() {
            return shortKey;
        }

        public boolean isPlayerActivity() {
            return this.key.startsWith("a:") || this == CHAT || this == COMMAND || this == SIGN ||
                   this == ITEM_DROP || this == ITEM_PICKUP || this == ITEM_USE || this == ITEM_CONSUME ||
                   this == PROJECTILE || this == ANVIL_RENAME || this == SESSION_JOIN || this == SESSION_QUIT ||
                   this == USERNAME_CHANGE || this == NICKNAME_CHANGE || this == IP_CHANGE;
        }

        public boolean isPunishment() {
            return this.key.startsWith("punishment_") || this.shortKey.startsWith("p:");
        }

        public boolean isStaffAction() {
            return this.shortKey.startsWith("s:");
        }

        public boolean isAutomodOrAnticheat() {
            return this == AUTOMOD_TRIGGER || this == ANTICHEAT_ALERT;
        }

        public static ActivityType fromKey(String key) {
            if (key == null) return null;
            String lower = key.toLowerCase();
            for (ActivityType type : values()) {
                if (type.key.equals(lower) || type.shortKey.equals(lower) ||
                    type.name().equalsIgnoreCase(lower)) {
                    return type;
                }
            }
            // Handle short aliases
            return switch (lower) {
                case "chat", "c" -> CHAT;
                case "command", "cmd", "commands" -> COMMAND;
                case "sign", "signs" -> SIGN;
                case "-item", "drop", "dropped" -> ITEM_DROP;
                case "+item", "pickup", "picked" -> ITEM_PICKUP;
                case "use", "used" -> ITEM_USE;
                case "consume", "consumed", "eat", "ate" -> ITEM_CONSUME;
                case "proj", "projectile", "throw", "shot" -> PROJECTILE;
                case "anvil", "anvil_rename", "rename" -> ANVIL_RENAME;
                case "+session", "join", "login" -> SESSION_JOIN;
                case "-session", "quit", "logout", "leave" -> SESSION_QUIT;
                case "username", "name", "namechange" -> USERNAME_CHANGE;
                case "nick", "nickname" -> NICKNAME_CHANGE;
                case "ip" -> IP_CHANGE;
                case "ban", "banned" -> PUNISHMENT_BAN;
                case "mute", "muted" -> PUNISHMENT_MUTE;
                case "warn", "warned", "warning" -> PUNISHMENT_WARN;
                case "kick", "kicked" -> PUNISHMENT_KICK;
                case "ipban" -> PUNISHMENT_IPBAN;
                case "ipmute" -> PUNISHMENT_IPMUTE;
                case "unban", "unbanned", "pardon" -> PUNISHMENT_UNBAN;
                case "unmute", "unmuted" -> PUNISHMENT_UNMUTE;
                case "unwarn", "unwarned" -> PUNISHMENT_UNWARN;
                case "automod" -> AUTOMOD_TRIGGER;
                case "anticheat", "ac" -> ANTICHEAT_ALERT;
                case "watchlist", "watch" -> STAFF_WATCHLIST_ADD;
                case "disguise" -> STAFF_DISGUISE_START;
                case "vanish" -> STAFF_VANISH_ENABLE;
                case "replay" -> STAFF_REPLAY_START;
                case "slowmode" -> STAFF_SLOWMODE_UPDATE;
                case "lockdown" -> STAFF_LOCKDOWN;
                default -> null;
            };
        }
    }

    public ActivityLogEntry(UUID playerUuid, String playerName, ActivityType type,
                           String content, String extra, long timestamp, String server) {
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.type = type;
        this.content = content;
        this.extra = extra;
        this.timestamp = timestamp;
        this.server = server;
    }

    public ActivityLogEntry(long id, UUID playerUuid, String playerName, ActivityType type,
                           String content, String extra, long timestamp, String server) {
        this(playerUuid, playerName, type, content, extra, timestamp, server);
        this.id = id;
    }

    // Getters
    public long getId() { return id; }
    public UUID getPlayerUuid() { return playerUuid; }
    public String getPlayerName() { return playerName; }
    public ActivityType getType() { return type; }
    public String getContent() { return content; }
    public String getExtra() { return extra; }
    public long getTimestamp() { return timestamp; }
    public String getServer() { return server; }

    public void setId(long id) { this.id = id; }

    /**
     * Format the timestamp for display.
     */
    public String getFormattedTime(ZoneId timezone) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                .withZone(timezone);
        return formatter.format(Instant.ofEpochMilli(timestamp));
    }

    /**
     * Format the full date/time for hover tooltip.
     */
    public String getFormattedFullDate(ZoneId timezone) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")
                .withZone(timezone);
        return formatter.format(Instant.ofEpochMilli(timestamp));
    }

    /**
     * Get the type display name for messages.
     */
    public String getTypeDisplayName() {
        return switch (type) {
            // Player Activity
            case CHAT -> "Chat";
            case COMMAND -> "Command";
            case SIGN -> "Sign";
            case ITEM_DROP -> "Dropped";
            case ITEM_PICKUP -> "Picked Up";
            case ITEM_USE -> "Used";
            case ITEM_CONSUME -> "Consumed";
            case PROJECTILE -> "Projectile";
            case ANVIL_RENAME -> "Renamed";
            case SESSION_JOIN -> "Joined";
            case SESSION_QUIT -> "Left";
            case USERNAME_CHANGE -> "Username";
            case NICKNAME_CHANGE -> "Nickname";
            case IP_CHANGE -> "IP Change";
            // Punishments
            case PUNISHMENT_BAN -> "Banned";
            case PUNISHMENT_MUTE -> "Muted";
            case PUNISHMENT_WARN -> "Warned";
            case PUNISHMENT_KICK -> "Kicked";
            case PUNISHMENT_IPBAN -> "IP Banned";
            case PUNISHMENT_IPMUTE -> "IP Muted";
            case PUNISHMENT_UNBAN -> "Unbanned";
            case PUNISHMENT_UNMUTE -> "Unmuted";
            case PUNISHMENT_UNWARN -> "Unwarned";
            // Automod & Anticheat
            case AUTOMOD_TRIGGER -> "Automod";
            case ANTICHEAT_ALERT -> "Anticheat";
            // Watchlist
            case WATCHLIST_ALERT -> "Watchlist";
            // Staff Actions
            case STAFF_PUNISHMENT -> "Punished";
            case STAFF_PARDON -> "Pardoned";
            case STAFF_WATCHLIST_ADD -> "Watchlist Add";
            case STAFF_WATCHLIST_REMOVE -> "Watchlist Remove";
            case STAFF_WATCHLIST_NOTE -> "Watchlist Note";
            case STAFF_CMD_BLACKLIST -> "Cmd Blacklist";
            case STAFF_CMD_UNBLACKLIST -> "Cmd Unblacklist";
            case STAFF_DISGUISE_START -> "Disguised";
            case STAFF_DISGUISE_END -> "Undisguised";
            case STAFF_VANISH_ENABLE -> "Vanished";
            case STAFF_VANISH_DISABLE -> "Unvanished";
            case STAFF_REPLAY_START -> "Replay Start";
            case STAFF_REPLAY_STOP -> "Replay Stop";
            case STAFF_SLOWMODE_UPDATE -> "Slowmode";
            case STAFF_CHAT_LOCK -> "Chat Lock";
            case STAFF_LOCKDOWN -> "Lockdown";
            case STAFF_AUTOMOD_UPDATE -> "Automod Update";
            case STAFF_CLEAR_CHAT -> "Clear Chat";
            case STAFF_KICK_ALL -> "Kick All";
        };
    }

    /**
     * Get the color for this activity type.
     */
    public String getTypeColor() {
        return switch (type) {
            // Player Activity
            case CHAT -> "<aqua>";
            case COMMAND -> "<yellow>";
            case SIGN -> "<gold>";
            case ITEM_DROP -> "<red>";
            case ITEM_PICKUP -> "<green>";
            case ITEM_USE -> "<light_purple>";
            case ITEM_CONSUME -> "<dark_green>";
            case PROJECTILE -> "<blue>";
            case ANVIL_RENAME -> "<dark_aqua>";
            case SESSION_JOIN -> "<green>";
            case SESSION_QUIT -> "<red>";
            case USERNAME_CHANGE -> "<gold>";
            case NICKNAME_CHANGE -> "<gold>";
            case IP_CHANGE -> "<gray>";
            // Punishments (red spectrum for negative, green for positive)
            case PUNISHMENT_BAN, PUNISHMENT_IPBAN -> "<dark_red>";
            case PUNISHMENT_MUTE, PUNISHMENT_IPMUTE -> "<red>";
            case PUNISHMENT_WARN -> "<gold>";
            case PUNISHMENT_KICK -> "<red>";
            case PUNISHMENT_UNBAN, PUNISHMENT_UNMUTE, PUNISHMENT_UNWARN -> "<green>";
            // Automod & Anticheat
            case AUTOMOD_TRIGGER -> "<#ff6b6b>";
            case ANTICHEAT_ALERT -> "<#ff4757>";
            // Watchlist
            case WATCHLIST_ALERT -> "<gold>";
            // Staff Actions
            case STAFF_PUNISHMENT -> "<dark_red>";
            case STAFF_PARDON -> "<green>";
            case STAFF_WATCHLIST_ADD -> "<gold>";
            case STAFF_WATCHLIST_REMOVE -> "<green>";
            case STAFF_WATCHLIST_NOTE -> "<yellow>";
            case STAFF_CMD_BLACKLIST -> "<red>";
            case STAFF_CMD_UNBLACKLIST -> "<green>";
            case STAFF_DISGUISE_START, STAFF_DISGUISE_END -> "<light_purple>";
            case STAFF_VANISH_ENABLE, STAFF_VANISH_DISABLE -> "<gray>";
            case STAFF_REPLAY_START, STAFF_REPLAY_STOP -> "<aqua>";
            case STAFF_SLOWMODE_UPDATE -> "<yellow>";
            case STAFF_CHAT_LOCK -> "<red>";
            case STAFF_LOCKDOWN -> "<dark_red>";
            case STAFF_AUTOMOD_UPDATE -> "<gold>";
            case STAFF_CLEAR_CHAT -> "<gray>";
            case STAFF_KICK_ALL -> "<dark_red>";
        };
    }

    @Override
    public String toString() {
        return "ActivityLogEntry{" +
                "id=" + id +
                ", playerUuid=" + playerUuid +
                ", playerName='" + playerName + '\'' +
                ", type=" + type +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
