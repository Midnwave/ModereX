package com.blockforge.moderex.util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Resolves various target formats to player information.
 * Supports:
 * - Player names
 * - UUIDs (32 or 36 character format)
 * - IP addresses (including wildcards like 192.168.1.*)
 * - Punishment IDs (numeric)
 */
public class TargetResolver {

    private static final Pattern UUID_32 = Pattern.compile("^[0-9a-f]{32}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern UUID_36 = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern IP_ADDRESS = Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.(\\d{1,3}|\\*)$");
    private static final Pattern PUNISHMENT_ID = Pattern.compile("^\\d+$");

    private final String input;
    private TargetType type;
    private UUID uuid;
    private String name;
    private String ip;
    private Long punishmentId;

    public TargetResolver(String input) {
        this.input = input;
        resolve();
    }

    private void resolve() {
        if (input == null || input.isEmpty()) {
            type = TargetType.INVALID;
            return;
        }

        // Check for UUID (32 character)
        if (UUID_32.matcher(input).matches()) {
            type = TargetType.UUID;
            uuid = parseUuid32(input);
            return;
        }

        // Check for UUID (36 character with dashes)
        if (UUID_36.matcher(input).matches()) {
            type = TargetType.UUID;
            try {
                uuid = UUID.fromString(input);
            } catch (IllegalArgumentException e) {
                type = TargetType.INVALID;
            }
            return;
        }

        // Check for IP address (including wildcards)
        if (IP_ADDRESS.matcher(input).matches()) {
            type = TargetType.IP;
            ip = input;
            return;
        }

        // Check for punishment ID
        if (PUNISHMENT_ID.matcher(input).matches()) {
            try {
                punishmentId = Long.parseLong(input);
                type = TargetType.PUNISHMENT_ID;
                return;
            } catch (NumberFormatException e) {
                // Fall through to name
            }
        }

        // Default to player name
        type = TargetType.NAME;
        name = input;

        // Try to resolve UUID from name
        OfflinePlayer player = Bukkit.getOfflinePlayer(input);
        if (player != null && (player.hasPlayedBefore() || player.isOnline())) {
            uuid = player.getUniqueId();
            name = player.getName() != null ? player.getName() : input;
        }
    }

    /**
     * Parse 32-character UUID string to UUID object
     */
    private UUID parseUuid32(String uuid32) {
        String uuid36 = uuid32.substring(0, 8) + "-"
                + uuid32.substring(8, 12) + "-"
                + uuid32.substring(12, 16) + "-"
                + uuid32.substring(16, 20) + "-"
                + uuid32.substring(20, 32);
        try {
            return UUID.fromString(uuid36);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public TargetType getType() {
        return type;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public Long getPunishmentId() {
        return punishmentId;
    }

    public String getInput() {
        return input;
    }

    public boolean isValid() {
        return type != TargetType.INVALID;
    }

    public boolean isPlayer() {
        return type == TargetType.NAME || type == TargetType.UUID;
    }

    public boolean isIp() {
        return type == TargetType.IP;
    }

    public boolean isPunishmentId() {
        return type == TargetType.PUNISHMENT_ID;
    }

    /**
     * Check if the IP pattern matches the given IP address
     * Supports wildcards (e.g., 192.168.1.* matches 192.168.1.100)
     */
    public boolean matchesIp(String targetIp) {
        if (!isIp() || targetIp == null) {
            return false;
        }

        String pattern = ip.replace(".", "\\.").replace("*", "\\d{1,3}");
        return targetIp.matches(pattern);
    }

    public enum TargetType {
        NAME,
        UUID,
        IP,
        PUNISHMENT_ID,
        INVALID
    }

    /**
     * Get a display name for the target (for messages)
     */
    public String getDisplayName() {
        return switch (type) {
            case NAME -> name != null ? name : input;
            case UUID -> name != null ? name : uuid.toString();
            case IP -> ip;
            case PUNISHMENT_ID -> "Punishment #" + punishmentId;
            case INVALID -> input;
        };
    }

    /**
     * Create a resolver and attempt to resolve player UUID
     * Returns null if target is not a player or cannot be resolved
     */
    public static UUID resolvePlayerUuid(String input) {
        TargetResolver resolver = new TargetResolver(input);
        return resolver.isPlayer() ? resolver.getUuid() : null;
    }

    /**
     * Create a resolver and get display name
     */
    public static String resolveDisplayName(String input) {
        return new TargetResolver(input).getDisplayName();
    }
}
