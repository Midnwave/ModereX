package com.blockforge.moderex.replay.block;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.util.UUID;

/**
 * Data class representing a block change record in a replay session.
 * Tracks block modifications for accurate replay visualization.
 */
public class BlockLogEntry {

    private final long timestamp;
    private final String sessionId;
    private final UUID playerUuid;
    private final String playerName;
    private final Action action;
    private final String worldName;
    private final int x, y, z;
    private final Material oldMaterial;
    private final String oldBlockData;
    private final Material newMaterial;
    private final String newBlockData;

    public enum Action {
        PLACE,
        BREAK,
        PISTON_EXTEND,
        PISTON_RETRACT,
        EXPLOSION,
        BURN,
        FADE,
        GROW,
        SPREAD,
        FORM
    }

    private BlockLogEntry(Builder builder) {
        this.timestamp = builder.timestamp;
        this.sessionId = builder.sessionId;
        this.playerUuid = builder.playerUuid;
        this.playerName = builder.playerName;
        this.action = builder.action;
        this.worldName = builder.worldName;
        this.x = builder.x;
        this.y = builder.y;
        this.z = builder.z;
        this.oldMaterial = builder.oldMaterial;
        this.oldBlockData = builder.oldBlockData;
        this.newMaterial = builder.newMaterial;
        this.newBlockData = builder.newBlockData;
    }

    // Getters
    public long getTimestamp() { return timestamp; }
    public String getSessionId() { return sessionId; }
    public UUID getPlayerUuid() { return playerUuid; }
    public String getPlayerName() { return playerName; }
    public Action getAction() { return action; }
    public String getWorldName() { return worldName; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }
    public Material getOldMaterial() { return oldMaterial; }
    public String getOldBlockData() { return oldBlockData; }
    public Material getNewMaterial() { return newMaterial; }
    public String getNewBlockData() { return newBlockData; }

    /**
     * Get a location key for this block (worldName:x:y:z).
     */
    public String getLocationKey() {
        return worldName + ":" + x + ":" + y + ":" + z;
    }

    /**
     * Serialize this entry to a string for storage.
     */
    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append(timestamp).append("|");
        sb.append(sessionId).append("|");
        sb.append(playerUuid != null ? playerUuid.toString() : "").append("|");
        sb.append(playerName != null ? playerName : "").append("|");
        sb.append(action.ordinal()).append("|");
        sb.append(worldName).append("|");
        sb.append(x).append(",").append(y).append(",").append(z).append("|");
        sb.append(oldMaterial != null ? oldMaterial.name() : "AIR").append("|");
        sb.append(oldBlockData != null ? oldBlockData : "").append("|");
        sb.append(newMaterial != null ? newMaterial.name() : "AIR").append("|");
        sb.append(newBlockData != null ? newBlockData : "");
        return sb.toString();
    }

    /**
     * Deserialize an entry from a stored string.
     */
    public static BlockLogEntry deserialize(String data) {
        String[] parts = data.split("\\|", 11);
        if (parts.length < 11) return null;

        try {
            long timestamp = Long.parseLong(parts[0]);
            String sessionId = parts[1];
            UUID playerUuid = parts[2].isEmpty() ? null : UUID.fromString(parts[2]);
            String playerName = parts[3].isEmpty() ? null : parts[3];
            Action action = Action.values()[Integer.parseInt(parts[4])];
            String worldName = parts[5];
            String[] coords = parts[6].split(",");
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);
            int z = Integer.parseInt(coords[2]);
            Material oldMaterial = Material.matchMaterial(parts[7]);
            String oldBlockData = parts[8].isEmpty() ? null : parts[8];
            Material newMaterial = Material.matchMaterial(parts[9]);
            String newBlockData = parts[10].isEmpty() ? null : parts[10];

            return new Builder()
                    .timestamp(timestamp)
                    .sessionId(sessionId)
                    .player(playerUuid, playerName)
                    .action(action)
                    .location(worldName, x, y, z)
                    .oldBlock(oldMaterial, oldBlockData)
                    .newBlock(newMaterial, newBlockData)
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

    public static class Builder {
        private long timestamp = System.currentTimeMillis();
        private String sessionId;
        private UUID playerUuid;
        private String playerName;
        private Action action;
        private String worldName;
        private int x, y, z;
        private Material oldMaterial;
        private String oldBlockData;
        private Material newMaterial;
        private String newBlockData;

        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder player(UUID uuid, String name) {
            this.playerUuid = uuid;
            this.playerName = name;
            return this;
        }

        public Builder action(Action action) {
            this.action = action;
            return this;
        }

        public Builder location(String worldName, int x, int y, int z) {
            this.worldName = worldName;
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }

        public Builder oldBlock(Material material, String blockData) {
            this.oldMaterial = material;
            this.oldBlockData = blockData;
            return this;
        }

        public Builder oldBlock(Material material, BlockData blockData) {
            this.oldMaterial = material;
            this.oldBlockData = blockData != null ? blockData.getAsString() : null;
            return this;
        }

        public Builder newBlock(Material material, String blockData) {
            this.newMaterial = material;
            this.newBlockData = blockData;
            return this;
        }

        public Builder newBlock(Material material, BlockData blockData) {
            this.newMaterial = material;
            this.newBlockData = blockData != null ? blockData.getAsString() : null;
            return this;
        }

        public BlockLogEntry build() {
            return new BlockLogEntry(this);
        }
    }
}

