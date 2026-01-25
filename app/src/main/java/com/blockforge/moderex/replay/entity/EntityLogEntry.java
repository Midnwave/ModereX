package com.blockforge.moderex.replay.entity;

import org.bukkit.entity.EntityType;

import java.util.UUID;

/**
 * Data class representing an entity event in a replay session.
 * Tracks entity spawns, deaths, damage, interactions, etc.
 */
public class EntityLogEntry {

    private final long timestamp;
    private final String sessionId;
    private final UUID entityUuid;
    private final EntityType entityType;
    private final String entityName; // Custom name or type name
    private final Action action;
    private final String worldName;
    private final double x, y, z;
    private final float yaw, pitch;
    private final String data; // Additional data (damage amount, killer, etc.)
    private final UUID involvedPlayerUuid; // Player involved in this action (if any)

    public enum Action {
        SPAWN,
        DEATH,
        REMOVE,
        DAMAGE_TAKEN,
        DAMAGE_DEALT,
        INTERACT,
        MOUNT,
        DISMOUNT,
        PICKUP, // Entity picks up item
        TARGET_CHANGE,
        LEASH,
        UNLEASH,
        TAME,
        BREED,
        TRANSFORM, // e.g., zombie to drowned
        EXPLODE,
        PROJECTILE_LAUNCH,
        PROJECTILE_HIT
    }

    private EntityLogEntry(Builder builder) {
        this.timestamp = builder.timestamp;
        this.sessionId = builder.sessionId;
        this.entityUuid = builder.entityUuid;
        this.entityType = builder.entityType;
        this.entityName = builder.entityName;
        this.action = builder.action;
        this.worldName = builder.worldName;
        this.x = builder.x;
        this.y = builder.y;
        this.z = builder.z;
        this.yaw = builder.yaw;
        this.pitch = builder.pitch;
        this.data = builder.data;
        this.involvedPlayerUuid = builder.involvedPlayerUuid;
    }

    // Getters
    public long getTimestamp() { return timestamp; }
    public String getSessionId() { return sessionId; }
    public UUID getEntityUuid() { return entityUuid; }
    public EntityType getEntityType() { return entityType; }
    public String getEntityName() { return entityName; }
    public Action getAction() { return action; }
    public String getWorldName() { return worldName; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
    public String getData() { return data; }
    public UUID getInvolvedPlayerUuid() { return involvedPlayerUuid; }

    public String getLocationKey() {
        return worldName + ":" + (int)x + ":" + (int)y + ":" + (int)z;
    }

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append(timestamp).append("|");
        sb.append(sessionId).append("|");
        sb.append(entityUuid != null ? entityUuid.toString() : "").append("|");
        sb.append(entityType != null ? entityType.name() : "UNKNOWN").append("|");
        sb.append(entityName != null ? entityName : "").append("|");
        sb.append(action.ordinal()).append("|");
        sb.append(worldName).append("|");
        sb.append(String.format("%.2f,%.2f,%.2f,%.2f,%.2f", x, y, z, yaw, pitch)).append("|");
        sb.append(data != null ? data.replace("|", "\\|") : "").append("|");
        sb.append(involvedPlayerUuid != null ? involvedPlayerUuid.toString() : "");
        return sb.toString();
    }

    public static EntityLogEntry deserialize(String data) {
        String[] parts = data.split("\\|", -1);
        if (parts.length < 9) return null;

        try {
            long timestamp = Long.parseLong(parts[0]);
            String sessionId = parts[1];
            UUID entityUuid = parts[2].isEmpty() ? null : UUID.fromString(parts[2]);
            EntityType entityType;
            try {
                entityType = EntityType.valueOf(parts[3]);
            } catch (IllegalArgumentException e) {
                entityType = EntityType.UNKNOWN;
            }
            String entityName = parts[4].isEmpty() ? null : parts[4];
            Action action = Action.values()[Integer.parseInt(parts[5])];
            String worldName = parts[6];
            String[] pos = parts[7].split(",");
            double x = Double.parseDouble(pos[0]);
            double y = Double.parseDouble(pos[1]);
            double z = Double.parseDouble(pos[2]);
            float yaw = Float.parseFloat(pos[3]);
            float pitch = Float.parseFloat(pos[4]);
            String actionData = parts[8].isEmpty() ? null : parts[8].replace("\\|", "|");
            UUID involvedPlayerUuid = parts.length > 9 && !parts[9].isEmpty() ? UUID.fromString(parts[9]) : null;

            return new Builder()
                    .timestamp(timestamp)
                    .sessionId(sessionId)
                    .entity(entityUuid, entityType, entityName)
                    .action(action)
                    .location(worldName, x, y, z, yaw, pitch)
                    .data(actionData)
                    .involvedPlayer(involvedPlayerUuid)
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

    public static class Builder {
        private long timestamp = System.currentTimeMillis();
        private String sessionId;
        private UUID entityUuid;
        private EntityType entityType;
        private String entityName;
        private Action action;
        private String worldName;
        private double x, y, z;
        private float yaw, pitch;
        private String data;
        private UUID involvedPlayerUuid;

        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder entity(UUID uuid, EntityType type, String name) {
            this.entityUuid = uuid;
            this.entityType = type;
            this.entityName = name;
            return this;
        }

        public Builder action(Action action) {
            this.action = action;
            return this;
        }

        public Builder location(String worldName, double x, double y, double z, float yaw, float pitch) {
            this.worldName = worldName;
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
            return this;
        }

        public Builder location(org.bukkit.Location loc) {
            if (loc != null && loc.getWorld() != null) {
                this.worldName = loc.getWorld().getName();
                this.x = loc.getX();
                this.y = loc.getY();
                this.z = loc.getZ();
                this.yaw = loc.getYaw();
                this.pitch = loc.getPitch();
            }
            return this;
        }

        public Builder data(String data) {
            this.data = data;
            return this;
        }

        public Builder involvedPlayer(UUID playerUuid) {
            this.involvedPlayerUuid = playerUuid;
            return this;
        }

        public EntityLogEntry build() {
            return new EntityLogEntry(this);
        }
    }
}
