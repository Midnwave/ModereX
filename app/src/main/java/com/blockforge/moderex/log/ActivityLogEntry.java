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
        USERNAME_CHANGE("username_change", "a:username");

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
        };
    }

    /**
     * Get the color for this activity type.
     */
    public String getTypeColor() {
        return switch (type) {
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
