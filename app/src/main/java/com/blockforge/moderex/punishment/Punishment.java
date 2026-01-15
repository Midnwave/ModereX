package com.blockforge.moderex.punishment;

import java.util.UUID;

public class Punishment {

    private int id;
    private String caseId;
    private UUID playerUuid;
    private String playerName;
    private PunishmentType type;
    private String reason;
    private UUID staffUuid;
    private String staffName;
    private long createdAt;
    private long expiresAt; // -1 for permanent
    private boolean active;
    private UUID removedByUuid;
    private String removedByName;
    private Long removedAt;
    private String removedReason;
    private String ipAddress;
    private String server;

    public Punishment() {
    }

    // Builder pattern for easier construction
    public static Builder builder() {
        return new Builder();
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public void setPlayerUuid(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public PunishmentType getType() {
        return type;
    }

    public void setType(PunishmentType type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public UUID getStaffUuid() {
        return staffUuid;
    }

    public void setStaffUuid(UUID staffUuid) {
        this.staffUuid = staffUuid;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public UUID getRemovedByUuid() {
        return removedByUuid;
    }

    public void setRemovedByUuid(UUID removedByUuid) {
        this.removedByUuid = removedByUuid;
    }

    public String getRemovedByName() {
        return removedByName;
    }

    public void setRemovedByName(String removedByName) {
        this.removedByName = removedByName;
    }

    public Long getRemovedAt() {
        return removedAt;
    }

    public void setRemovedAt(Long removedAt) {
        this.removedAt = removedAt;
    }

    public String getRemovedReason() {
        return removedReason;
    }

    public void setRemovedReason(String removedReason) {
        this.removedReason = removedReason;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public boolean isPermanent() {
        return expiresAt == -1;
    }

    public boolean isExpired() {
        if (isPermanent()) return false;
        return System.currentTimeMillis() >= expiresAt;
    }

    public long getRemainingTime() {
        if (isPermanent()) return -1;
        long remaining = expiresAt - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    public static class Builder {
        private final Punishment punishment = new Punishment();

        public Builder caseId(String caseId) {
            punishment.caseId = caseId;
            return this;
        }

        public Builder playerUuid(UUID uuid) {
            punishment.playerUuid = uuid;
            return this;
        }

        public Builder playerName(String name) {
            punishment.playerName = name;
            return this;
        }

        public Builder type(PunishmentType type) {
            punishment.type = type;
            return this;
        }

        public Builder reason(String reason) {
            punishment.reason = reason;
            return this;
        }

        public Builder staffUuid(UUID uuid) {
            punishment.staffUuid = uuid;
            return this;
        }

        public Builder staffName(String name) {
            punishment.staffName = name;
            return this;
        }

        public Builder createdAt(long timestamp) {
            punishment.createdAt = timestamp;
            return this;
        }

        public Builder expiresAt(long timestamp) {
            punishment.expiresAt = timestamp;
            return this;
        }

        public Builder permanent() {
            punishment.expiresAt = -1;
            return this;
        }

        public Builder active(boolean active) {
            punishment.active = active;
            return this;
        }

        public Builder ipAddress(String ip) {
            punishment.ipAddress = ip;
            return this;
        }

        public Builder server(String server) {
            punishment.server = server;
            return this;
        }

        public Punishment build() {
            if (punishment.createdAt == 0) {
                punishment.createdAt = System.currentTimeMillis();
            }
            if (punishment.active == false && punishment.expiresAt == 0) {
                punishment.active = true;
            }
            return punishment;
        }
    }
}
