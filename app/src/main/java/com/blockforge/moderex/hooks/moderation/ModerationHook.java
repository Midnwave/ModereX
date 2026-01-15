package com.blockforge.moderex.hooks.moderation;

import java.util.List;
import java.util.UUID;

public interface ModerationHook {

    String getPluginName();

    boolean isAvailable();

    List<ExternalPunishment> getPunishments(UUID uuid);

    List<ExternalPunishment> getActivePunishments(UUID uuid);

    boolean importPunishment(ExternalPunishment punishment);

    int importAllPunishments(UUID uuid);

    default int getTotalPunishmentCount() {
        return 0; // Default implementation returns 0
    }

    class ExternalPunishment {
        private final String source;            // Plugin name
        private final UUID playerUuid;
        private final String playerName;
        private final String type;              // BAN, MUTE, KICK, WARN
        private final String reason;
        private final String staff;             // Staff name
        private final long createdAt;           // Timestamp when created
        private final long expiresAt;           // Timestamp when expires (-1 for permanent)
        private final boolean active;           // Is this punishment still active?
        private final String serverId;          // Server ID if available
        private final String ipAddress;         // IP address if available

        public ExternalPunishment(String source, UUID playerUuid, String playerName, String type,
                                  String reason, String staff, long createdAt, long expiresAt,
                                  boolean active, String serverId, String ipAddress) {
            this.source = source;
            this.playerUuid = playerUuid;
            this.playerName = playerName;
            this.type = type;
            this.reason = reason;
            this.staff = staff;
            this.createdAt = createdAt;
            this.expiresAt = expiresAt;
            this.active = active;
            this.serverId = serverId;
            this.ipAddress = ipAddress;
        }

        // Getters
        public String getSource() { return source; }
        public UUID getPlayerUuid() { return playerUuid; }
        public String getPlayerName() { return playerName; }
        public String getType() { return type; }
        public String getReason() { return reason; }
        public String getStaff() { return staff; }
        public long getCreatedAt() { return createdAt; }
        public long getExpiresAt() { return expiresAt; }
        public boolean isActive() { return active; }
        public String getServerId() { return serverId; }
        public String getIpAddress() { return ipAddress; }
        public boolean isPermanent() { return expiresAt == -1; }
    }
}
