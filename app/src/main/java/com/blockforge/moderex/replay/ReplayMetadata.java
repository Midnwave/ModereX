package com.blockforge.moderex.replay;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents metadata for a recorded replay session.
 */
public class ReplayMetadata {

    public enum ReplayStatus {
        RECORDING,
        COMPLETE,
        FAILED,
        DELETED
    }

    private String id;
    private String name;
    private UUID creatorUuid;
    private String creatorName;
    private long createdAt;
    private long updatedAt;
    private int duration; // seconds
    private long fileSize; // bytes
    private ReplayStatus status;
    private List<String> playerUuids; // Players involved in the replay
    private List<String> playerNames; // Player names for display
    private List<String> tags; // Categorization tags
    private String notes; // Staff comments
    private List<String> linkedPunishments; // Linked punishment case IDs
    private String world;
    private double x, y, z;

    public ReplayMetadata() {
        this.id = UUID.randomUUID().toString().substring(0, 12);
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.status = ReplayStatus.RECORDING;
        this.playerUuids = new ArrayList<>();
        this.playerNames = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.linkedPunishments = new ArrayList<>();
    }

    public ReplayMetadata(String name, UUID creatorUuid, String creatorName) {
        this();
        this.name = name;
        this.creatorUuid = creatorUuid;
        this.creatorName = creatorName;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getCreatorUuid() { return creatorUuid; }
    public void setCreatorUuid(UUID creatorUuid) { this.creatorUuid = creatorUuid; }

    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) {
        this.duration = duration;
        this.updatedAt = System.currentTimeMillis();
    }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
        this.updatedAt = System.currentTimeMillis();
    }

    public ReplayStatus getStatus() { return status; }
    public void setStatus(ReplayStatus status) {
        this.status = status;
        this.updatedAt = System.currentTimeMillis();
    }

    public List<String> getPlayerUuids() { return playerUuids; }
    public void setPlayerUuids(List<String> playerUuids) { this.playerUuids = playerUuids; }

    public List<String> getPlayerNames() { return playerNames; }
    public void setPlayerNames(List<String> playerNames) { this.playerNames = playerNames; }

    public void addPlayer(UUID uuid, String name) {
        if (!playerUuids.contains(uuid.toString())) {
            playerUuids.add(uuid.toString());
            playerNames.add(name);
            this.updatedAt = System.currentTimeMillis();
        }
    }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public void addTag(String tag) {
        if (!tags.contains(tag.toLowerCase())) {
            tags.add(tag.toLowerCase());
            this.updatedAt = System.currentTimeMillis();
        }
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) {
        this.notes = notes;
        this.updatedAt = System.currentTimeMillis();
    }

    public List<String> getLinkedPunishments() { return linkedPunishments; }
    public void setLinkedPunishments(List<String> linkedPunishments) { this.linkedPunishments = linkedPunishments; }

    public void linkPunishment(String caseId) {
        if (!linkedPunishments.contains(caseId)) {
            linkedPunishments.add(caseId);
            this.updatedAt = System.currentTimeMillis();
        }
    }

    public String getWorld() { return world; }
    public void setWorld(String world) { this.world = world; }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getZ() { return z; }
    public void setZ(double z) { this.z = z; }

    public void setLocation(String world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Get formatted duration string (e.g., "1:30" for 90 seconds)
     */
    public String getFormattedDuration() {
        int mins = duration / 60;
        int secs = duration % 60;
        return String.format("%d:%02d", mins, secs);
    }

    /**
     * Get formatted file size (e.g., "1.5 MB")
     */
    public String getFormattedFileSize() {
        if (fileSize < 1024) return fileSize + " B";
        if (fileSize < 1024 * 1024) return String.format("%.1f KB", fileSize / 1024.0);
        return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
    }

    /**
     * Convert to JSON for web panel/storage
     */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", id);
        json.addProperty("name", name);
        json.addProperty("creatorUuid", creatorUuid != null ? creatorUuid.toString() : null);
        json.addProperty("creatorName", creatorName);
        json.addProperty("createdAt", createdAt);
        json.addProperty("updatedAt", updatedAt);
        json.addProperty("duration", duration);
        json.addProperty("formattedDuration", getFormattedDuration());
        json.addProperty("fileSize", fileSize);
        json.addProperty("formattedFileSize", getFormattedFileSize());
        json.addProperty("status", status.name());

        JsonArray players = new JsonArray();
        for (int i = 0; i < playerUuids.size(); i++) {
            JsonObject player = new JsonObject();
            player.addProperty("uuid", playerUuids.get(i));
            player.addProperty("name", i < playerNames.size() ? playerNames.get(i) : "Unknown");
            players.add(player);
        }
        json.add("players", players);

        JsonArray tagsArray = new JsonArray();
        for (String tag : tags) tagsArray.add(tag);
        json.add("tags", tagsArray);

        json.addProperty("notes", notes);

        JsonArray punishments = new JsonArray();
        for (String caseId : linkedPunishments) punishments.add(caseId);
        json.add("linkedPunishments", punishments);

        json.addProperty("world", world);
        json.addProperty("x", x);
        json.addProperty("y", y);
        json.addProperty("z", z);

        return json;
    }

    /**
     * Create from JSON
     */
    public static ReplayMetadata fromJson(JsonObject json) {
        ReplayMetadata meta = new ReplayMetadata();
        meta.id = json.get("id").getAsString();
        meta.name = json.has("name") && !json.get("name").isJsonNull() ? json.get("name").getAsString() : null;
        meta.creatorUuid = json.has("creatorUuid") && !json.get("creatorUuid").isJsonNull()
                ? UUID.fromString(json.get("creatorUuid").getAsString()) : null;
        meta.creatorName = json.has("creatorName") && !json.get("creatorName").isJsonNull()
                ? json.get("creatorName").getAsString() : null;
        meta.createdAt = json.has("createdAt") ? json.get("createdAt").getAsLong() : System.currentTimeMillis();
        meta.updatedAt = json.has("updatedAt") ? json.get("updatedAt").getAsLong() : System.currentTimeMillis();
        meta.duration = json.has("duration") ? json.get("duration").getAsInt() : 0;
        meta.fileSize = json.has("fileSize") ? json.get("fileSize").getAsLong() : 0;
        meta.status = json.has("status") ? ReplayStatus.valueOf(json.get("status").getAsString()) : ReplayStatus.COMPLETE;

        if (json.has("playerUuids") && json.get("playerUuids").isJsonArray()) {
            for (var elem : json.getAsJsonArray("playerUuids")) {
                meta.playerUuids.add(elem.getAsString());
            }
        }
        if (json.has("playerNames") && json.get("playerNames").isJsonArray()) {
            for (var elem : json.getAsJsonArray("playerNames")) {
                meta.playerNames.add(elem.getAsString());
            }
        }
        // Also support nested players array format
        if (json.has("players") && json.get("players").isJsonArray()) {
            for (var elem : json.getAsJsonArray("players")) {
                JsonObject player = elem.getAsJsonObject();
                if (player.has("uuid")) meta.playerUuids.add(player.get("uuid").getAsString());
                if (player.has("name")) meta.playerNames.add(player.get("name").getAsString());
            }
        }

        if (json.has("tags") && json.get("tags").isJsonArray()) {
            for (var elem : json.getAsJsonArray("tags")) {
                meta.tags.add(elem.getAsString());
            }
        }

        meta.notes = json.has("notes") && !json.get("notes").isJsonNull() ? json.get("notes").getAsString() : null;

        if (json.has("linkedPunishments") && json.get("linkedPunishments").isJsonArray()) {
            for (var elem : json.getAsJsonArray("linkedPunishments")) {
                meta.linkedPunishments.add(elem.getAsString());
            }
        }

        meta.world = json.has("world") && !json.get("world").isJsonNull() ? json.get("world").getAsString() : null;
        meta.x = json.has("x") ? json.get("x").getAsDouble() : 0;
        meta.y = json.has("y") ? json.get("y").getAsDouble() : 0;
        meta.z = json.has("z") ? json.get("z").getAsDouble() : 0;

        return meta;
    }

    /**
     * Convert lists to JSON strings for database storage
     */
    public String getPlayerUuidsJson() {
        JsonArray arr = new JsonArray();
        for (String uuid : playerUuids) arr.add(uuid);
        return arr.toString();
    }

    public String getPlayerNamesJson() {
        JsonArray arr = new JsonArray();
        for (String name : playerNames) arr.add(name);
        return arr.toString();
    }

    public String getTagsJson() {
        JsonArray arr = new JsonArray();
        for (String tag : tags) arr.add(tag);
        return arr.toString();
    }

    public String getLinkedPunishmentsJson() {
        JsonArray arr = new JsonArray();
        for (String caseId : linkedPunishments) arr.add(caseId);
        return arr.toString();
    }

    /**
     * Parse JSON array string to list
     */
    public static List<String> parseJsonArray(String json) {
        List<String> list = new ArrayList<>();
        if (json == null || json.isEmpty()) return list;
        try {
            JsonArray arr = JsonParser.parseString(json).getAsJsonArray();
            for (var elem : arr) list.add(elem.getAsString());
        } catch (Exception ignored) {}
        return list;
    }

    @Override
    public String toString() {
        return String.format("Replay[%s: %s, %s, %d players, %s]",
                id, name, status, playerUuids.size(), getFormattedDuration());
    }
}
