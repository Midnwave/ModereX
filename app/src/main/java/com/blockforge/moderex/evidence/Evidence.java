package com.blockforge.moderex.evidence;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Evidence data model for the ModereX evidence management system.
 * Supports MP4, MKV, PNG, and JPG files up to 300MB.
 */
public class Evidence {

    private final String id;
    private UUID playerUuid;           // The player this evidence is about (optional)
    private String playerName;
    private final UUID uploaderUuid;   // Who uploaded the evidence
    private final String uploaderName;
    private final String fileName;
    private final String filePath;     // Relative path within evidence directory
    private final FileType fileType;
    private final long fileSize;       // Size in bytes
    private final String mimeType;
    private String linkedPunishmentId; // Optional link to a punishment
    private String description;
    private List<String> tags;
    private Source source;
    private final long createdAt;

    public enum FileType {
        VIDEO_MP4("mp4", "video/mp4"),
        VIDEO_MKV("mkv", "video/x-matroska"),
        VIDEO_MOV("mov", "video/quicktime"),
        IMAGE_PNG("png", "image/png"),
        IMAGE_JPG("jpg", "image/jpeg"),
        IMAGE_JPEG("jpeg", "image/jpeg");

        private final String extension;
        private final String mimeType;

        FileType(String extension, String mimeType) {
            this.extension = extension;
            this.mimeType = mimeType;
        }

        public String getExtension() { return extension; }
        public String getMimeType() { return mimeType; }

        public boolean isVideo() {
            return this == VIDEO_MP4 || this == VIDEO_MKV || this == VIDEO_MOV;
        }

        public boolean isImage() {
            return this == IMAGE_PNG || this == IMAGE_JPG || this == IMAGE_JPEG;
        }

        public static FileType fromExtension(String ext) {
            String lower = ext.toLowerCase().replace(".", "");
            for (FileType type : values()) {
                if (type.extension.equals(lower)) {
                    return type;
                }
            }
            return null;
        }

        public static FileType fromMimeType(String mime) {
            for (FileType type : values()) {
                if (type.mimeType.equals(mime)) {
                    return type;
                }
            }
            return null;
        }
    }

    public enum Source {
        UPLOAD,           // Direct upload via web panel
        ACTIVITY_LOG,     // Selected from activity log as evidence
        AUTOMOD_TRIGGER,  // Auto-captured from automod
        CHAT_HISTORY,     // Imported from chat history
        COMMAND_HISTORY,  // Imported from command history
        REPLAY_EXPORT,    // Exported from replay system
        EXTERNAL,         // External source (URL, etc.)
        MEDAL_IMPORT      // Imported from Medal.tv
    }

    // Maximum file size - configurable up to HARD_LIMIT
    public static final long HARD_LIMIT_MB = 1000; // 1GB absolute max
    public static final long HARD_LIMIT = HARD_LIMIT_MB * 1024 * 1024;
    public static final long DEFAULT_MAX_SIZE_MB = 250;
    public static long MAX_FILE_SIZE = DEFAULT_MAX_SIZE_MB * 1024 * 1024;

    /**
     * Update max file size from config. Respects hard limit.
     */
    public static void setMaxFileSizeMB(long mb) {
        mb = Math.min(mb, HARD_LIMIT_MB);
        mb = Math.max(mb, 1); // At least 1MB
        MAX_FILE_SIZE = mb * 1024 * 1024;
    }

    /**
     * Create new evidence with auto-generated ID.
     */
    public Evidence(UUID uploaderUuid, String uploaderName, String fileName,
                    String filePath, FileType fileType, long fileSize, String mimeType) {
        this.id = generateId();
        this.uploaderUuid = uploaderUuid;
        this.uploaderName = uploaderName;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.tags = new ArrayList<>();
        this.source = Source.UPLOAD;
        this.createdAt = System.currentTimeMillis();
    }

    /**
     * Create evidence with existing ID (for loading from database).
     */
    public Evidence(String id, UUID uploaderUuid, String uploaderName, String fileName,
                    String filePath, FileType fileType, long fileSize, String mimeType, long createdAt) {
        this.id = id;
        this.uploaderUuid = uploaderUuid;
        this.uploaderName = uploaderName;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.tags = new ArrayList<>();
        this.source = Source.UPLOAD;
        this.createdAt = createdAt;
    }

    private String generateId() {
        // Format: EV-XXXXXXXX (8 random hex characters)
        return "EV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Getters
    public String getId() { return id; }
    public UUID getPlayerUuid() { return playerUuid; }
    public String getPlayerName() { return playerName; }
    public UUID getUploaderUuid() { return uploaderUuid; }
    public String getUploaderName() { return uploaderName; }
    public String getFileName() { return fileName; }
    public String getFilePath() { return filePath; }
    public FileType getFileType() { return fileType; }
    public long getFileSize() { return fileSize; }
    public String getMimeType() { return mimeType; }
    public String getLinkedPunishmentId() { return linkedPunishmentId; }
    public String getDescription() { return description; }
    public List<String> getTags() { return tags; }
    public Source getSource() { return source; }
    public long getCreatedAt() { return createdAt; }

    // Setters
    public void setPlayerUuid(UUID playerUuid) { this.playerUuid = playerUuid; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public void setLinkedPunishmentId(String id) { this.linkedPunishmentId = id; }
    public void setDescription(String description) { this.description = description; }
    public void setTags(List<String> tags) { this.tags = tags != null ? tags : new ArrayList<>(); }
    public void addTag(String tag) { if (!tags.contains(tag)) tags.add(tag); }
    public void removeTag(String tag) { tags.remove(tag); }
    public void setSource(Source source) { this.source = source; }

    /**
     * Get formatted file size (e.g., "12.5 MB").
     */
    public String getFormattedSize() {
        if (fileSize < 1024) return fileSize + " B";
        if (fileSize < 1024 * 1024) return String.format("%.1f KB", fileSize / 1024.0);
        if (fileSize < 1024 * 1024 * 1024) return String.format("%.1f MB", fileSize / (1024.0 * 1024));
        return String.format("%.2f GB", fileSize / (1024.0 * 1024 * 1024));
    }

    /**
     * Convert to JSON for web panel.
     */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", id);
        json.addProperty("playerUuid", playerUuid != null ? playerUuid.toString() : null);
        json.addProperty("playerName", playerName);
        json.addProperty("uploaderUuid", uploaderUuid.toString());
        json.addProperty("uploaderName", uploaderName);
        json.addProperty("fileName", fileName);
        json.addProperty("filePath", filePath);
        json.addProperty("fileType", fileType.name());
        json.addProperty("fileSize", fileSize);
        json.addProperty("formattedSize", getFormattedSize());
        json.addProperty("mimeType", mimeType);
        json.addProperty("linkedPunishmentId", linkedPunishmentId);
        json.addProperty("description", description);
        json.addProperty("source", source.name());
        json.addProperty("createdAt", createdAt);
        json.addProperty("isVideo", fileType.isVideo());
        json.addProperty("isImage", fileType.isImage());

        com.google.gson.JsonArray tagsArray = new com.google.gson.JsonArray();
        for (String tag : tags) {
            tagsArray.add(tag);
        }
        json.add("tags", tagsArray);

        return json;
    }

    /**
     * Create from JSON (for loading from database).
     */
    public static Evidence fromJson(JsonObject json) {
        Evidence evidence = new Evidence(
                json.get("id").getAsString(),
                UUID.fromString(json.get("uploaderUuid").getAsString()),
                json.get("uploaderName").getAsString(),
                json.get("fileName").getAsString(),
                json.get("filePath").getAsString(),
                FileType.valueOf(json.get("fileType").getAsString()),
                json.get("fileSize").getAsLong(),
                json.get("mimeType").getAsString(),
                json.get("createdAt").getAsLong()
        );

        if (json.has("playerUuid") && !json.get("playerUuid").isJsonNull()) {
            evidence.setPlayerUuid(UUID.fromString(json.get("playerUuid").getAsString()));
        }
        if (json.has("playerName") && !json.get("playerName").isJsonNull()) {
            evidence.setPlayerName(json.get("playerName").getAsString());
        }
        if (json.has("linkedPunishmentId") && !json.get("linkedPunishmentId").isJsonNull()) {
            evidence.setLinkedPunishmentId(json.get("linkedPunishmentId").getAsString());
        }
        if (json.has("description") && !json.get("description").isJsonNull()) {
            evidence.setDescription(json.get("description").getAsString());
        }
        if (json.has("source") && !json.get("source").isJsonNull()) {
            evidence.setSource(Source.valueOf(json.get("source").getAsString()));
        }
        if (json.has("tags") && json.get("tags").isJsonArray()) {
            List<String> tags = new ArrayList<>();
            for (var elem : json.get("tags").getAsJsonArray()) {
                tags.add(elem.getAsString());
            }
            evidence.setTags(tags);
        }

        return evidence;
    }
}
