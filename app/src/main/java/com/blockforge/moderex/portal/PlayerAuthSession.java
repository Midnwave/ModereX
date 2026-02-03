package com.blockforge.moderex.portal;

import com.google.gson.JsonObject;

import java.util.UUID;

/**
 * Represents an authentication session for the player punishment portal.
 * Sessions are 10-character alphanumeric strings that expire after a configured time.
 */
public class PlayerAuthSession {

    private final String id;
    private final UUID playerUuid;
    private final String caseId;
    private final long createdAt;
    private final long expiresAt;
    private String deviceHash;
    private boolean active;

    public PlayerAuthSession(String id, UUID playerUuid, String caseId,
                             long createdAt, long expiresAt, String deviceHash, boolean active) {
        this.id = id;
        this.playerUuid = playerUuid;
        this.caseId = caseId;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.deviceHash = deviceHash;
        this.active = active;
    }

    // Getters
    public String getId() { return id; }
    public UUID getPlayerUuid() { return playerUuid; }
    public String getCaseId() { return caseId; }
    public long getCreatedAt() { return createdAt; }
    public long getExpiresAt() { return expiresAt; }
    public String getDeviceHash() { return deviceHash; }
    public boolean isActive() { return active; }

    // Setters
    public void setDeviceHash(String deviceHash) { this.deviceHash = deviceHash; }
    public void setActive(boolean active) { this.active = active; }

    /**
     * Check if this session has expired.
     */
    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }

    /**
     * Get remaining time until expiry in milliseconds.
     */
    public long getRemainingTimeMs() {
        return Math.max(0, expiresAt - System.currentTimeMillis());
    }

    /**
     * Get remaining time formatted as "Xh Ym".
     */
    public String getRemainingTimeFormatted() {
        long remaining = getRemainingTimeMs();
        if (remaining <= 0) return "Expired";

        long hours = remaining / (1000 * 60 * 60);
        long minutes = (remaining % (1000 * 60 * 60)) / (1000 * 60);

        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }
        return minutes + "m";
    }

    /**
     * Convert to JSON for API responses.
     */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", id);
        json.addProperty("playerUuid", playerUuid.toString());
        json.addProperty("caseId", caseId);
        json.addProperty("createdAt", createdAt);
        json.addProperty("expiresAt", expiresAt);
        json.addProperty("deviceHash", deviceHash);
        json.addProperty("active", active);
        json.addProperty("expired", isExpired());
        json.addProperty("remainingTime", getRemainingTimeFormatted());
        return json;
    }

    @Override
    public String toString() {
        return "PlayerAuthSession{" +
                "id='" + id + '\'' +
                ", playerUuid=" + playerUuid +
                ", caseId='" + caseId + '\'' +
                ", active=" + active +
                ", expired=" + isExpired() +
                '}';
    }
}
