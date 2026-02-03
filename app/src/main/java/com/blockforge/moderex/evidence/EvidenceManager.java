package com.blockforge.moderex.evidence;

import com.blockforge.moderex.ModereX;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages evidence files and metadata for the ModereX evidence system.
 * Handles file storage, database operations, and retrieval.
 */
public class EvidenceManager {

    private final ModereX plugin;
    private final Path evidenceDirectory;
    private final Map<String, Evidence> cache = new ConcurrentHashMap<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // Allowed file extensions
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("mp4", "mkv", "mov", "png", "jpg", "jpeg");

    public EvidenceManager(ModereX plugin) {
        this.plugin = plugin;
        this.evidenceDirectory = plugin.getDataFolder().toPath().resolve("evidence");

        try {
            Files.createDirectories(evidenceDirectory);
        } catch (IOException e) {
            plugin.logError("Failed to create evidence directory", e);
        }
    }

    /**
     * Initialize the evidence system, including database table creation.
     */
    public void initialize() {
        createTable();
        loadCache();
        plugin.getLogger().info("Evidence system initialized");
    }

    private void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS moderex_evidence (
                id VARCHAR(36) PRIMARY KEY,
                player_uuid VARCHAR(36),
                player_name VARCHAR(16),
                uploader_uuid VARCHAR(36) NOT NULL,
                uploader_name VARCHAR(16) NOT NULL,
                file_name VARCHAR(255) NOT NULL,
                file_path VARCHAR(512) NOT NULL,
                file_type VARCHAR(32) NOT NULL,
                file_size BIGINT NOT NULL,
                mime_type VARCHAR(64),
                linked_punishment_id VARCHAR(36),
                description TEXT,
                tags TEXT,
                source VARCHAR(32),
                created_at BIGINT NOT NULL
            )
            """;

        try {
            plugin.getDatabaseManager().update(sql);
        } catch (SQLException e) {
            plugin.logError("Failed to create evidence table", e);
        }
    }

    private void loadCache() {
        CompletableFuture.runAsync(() -> {
            try {
                plugin.getDatabaseManager().query("SELECT * FROM moderex_evidence", rs -> {
                    while (rs.next()) {
                        Evidence evidence = evidenceFromResultSet(rs);
                        cache.put(evidence.getId(), evidence);
                    }
                    return null;
                });

                plugin.logDebug("[Evidence] Loaded " + cache.size() + " evidence entries");
            } catch (SQLException e) {
                plugin.logError("Failed to load evidence cache", e);
            }
        });
    }

    /**
     * Upload and store a new evidence file.
     *
     * @param uploaderUuid The UUID of the uploader
     * @param uploaderName The name of the uploader
     * @param fileName The original file name
     * @param inputStream The file data stream
     * @param fileSize The size of the file in bytes
     * @return CompletableFuture with the created Evidence, or null on failure
     */
    public CompletableFuture<Evidence> uploadEvidence(UUID uploaderUuid, String uploaderName,
                                                       String fileName, InputStream inputStream, long fileSize) {
        return CompletableFuture.supplyAsync(() -> {
            // Validate file size
            if (fileSize > Evidence.MAX_FILE_SIZE) {
                plugin.logDebug("[Evidence] File too large: " + fileSize + " bytes (max: " + Evidence.MAX_FILE_SIZE + ")");
                return null;
            }

            // Get file extension
            String extension = getFileExtension(fileName).toLowerCase();
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                plugin.logDebug("[Evidence] Invalid file extension: " + extension);
                return null;
            }

            // Determine file type
            Evidence.FileType fileType = Evidence.FileType.fromExtension(extension);
            if (fileType == null) {
                plugin.logDebug("[Evidence] Unknown file type for extension: " + extension);
                return null;
            }

            // Generate unique file path
            String uniqueFileName = UUID.randomUUID().toString() + "." + extension;
            Path targetPath = evidenceDirectory.resolve(uniqueFileName);

            try {
                // Copy file to evidence directory
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);

                // Verify file was saved correctly
                if (!Files.exists(targetPath) || Files.size(targetPath) != fileSize) {
                    plugin.logDebug("[Evidence] File save verification failed");
                    Files.deleteIfExists(targetPath);
                    return null;
                }

                // Create evidence record
                Evidence evidence = new Evidence(
                        uploaderUuid, uploaderName, fileName,
                        uniqueFileName, fileType, fileSize, fileType.getMimeType()
                );

                // Save to database
                saveEvidence(evidence);

                // Add to cache
                cache.put(evidence.getId(), evidence);

                plugin.logDebug("[Evidence] Uploaded: " + evidence.getId() + " (" + evidence.getFormattedSize() + ")");
                return evidence;

            } catch (IOException e) {
                plugin.logError("Failed to save evidence file", e);
                return null;
            }
        });
    }

    /**
     * Save evidence metadata to database.
     */
    private void saveEvidence(Evidence evidence) {
        String sql = """
            INSERT OR REPLACE INTO moderex_evidence
            (id, player_uuid, player_name, uploader_uuid, uploader_name, file_name, file_path,
             file_type, file_size, mime_type, linked_punishment_id, description, tags, source, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try {
            plugin.getDatabaseManager().update(sql,
                    evidence.getId(),
                    evidence.getPlayerUuid() != null ? evidence.getPlayerUuid().toString() : null,
                    evidence.getPlayerName(),
                    evidence.getUploaderUuid().toString(),
                    evidence.getUploaderName(),
                    evidence.getFileName(),
                    evidence.getFilePath(),
                    evidence.getFileType().name(),
                    evidence.getFileSize(),
                    evidence.getMimeType(),
                    evidence.getLinkedPunishmentId(),
                    evidence.getDescription(),
                    String.join(",", evidence.getTags()),
                    evidence.getSource().name(),
                    evidence.getCreatedAt()
            );
        } catch (SQLException e) {
            plugin.logError("Failed to save evidence to database", e);
        }
    }

    /**
     * Update evidence metadata.
     */
    public CompletableFuture<Boolean> updateEvidence(Evidence evidence) {
        return CompletableFuture.supplyAsync(() -> {
            saveEvidence(evidence);
            cache.put(evidence.getId(), evidence);
            return true;
        });
    }

    /**
     * Delete evidence and its file.
     */
    public CompletableFuture<Boolean> deleteEvidence(String evidenceId) {
        return CompletableFuture.supplyAsync(() -> {
            Evidence evidence = cache.get(evidenceId);
            if (evidence == null) {
                return false;
            }

            // Delete file
            try {
                Path filePath = evidenceDirectory.resolve(evidence.getFilePath());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                plugin.logError("Failed to delete evidence file", e);
            }

            // Delete from database
            try {
                plugin.getDatabaseManager().update("DELETE FROM moderex_evidence WHERE id = ?", evidenceId);
            } catch (SQLException e) {
                plugin.logError("Failed to delete evidence from database", e);
                return false;
            }

            // Remove from cache
            cache.remove(evidenceId);

            plugin.logDebug("[Evidence] Deleted: " + evidenceId);
            return true;
        });
    }

    /**
     * Get evidence by ID.
     */
    public Evidence getEvidence(String id) {
        return cache.get(id);
    }

    /**
     * Get the file path for evidence.
     */
    public Path getEvidenceFile(String id) {
        Evidence evidence = cache.get(id);
        if (evidence == null) return null;
        return evidenceDirectory.resolve(evidence.getFilePath());
    }

    /**
     * Get all evidence.
     */
    public Collection<Evidence> getAllEvidence() {
        return Collections.unmodifiableCollection(cache.values());
    }

    /**
     * Get evidence for a specific player.
     */
    public List<Evidence> getEvidenceByPlayer(UUID playerUuid) {
        return cache.values().stream()
                .filter(e -> playerUuid.equals(e.getPlayerUuid()))
                .sorted((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()))
                .toList();
    }

    /**
     * Get evidence linked to a punishment.
     */
    public List<Evidence> getEvidenceByPunishment(String punishmentId) {
        return cache.values().stream()
                .filter(e -> punishmentId.equals(e.getLinkedPunishmentId()))
                .sorted((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()))
                .toList();
    }

    /**
     * Get evidence by uploader.
     */
    public List<Evidence> getEvidenceByUploader(UUID uploaderUuid) {
        return cache.values().stream()
                .filter(e -> uploaderUuid.equals(e.getUploaderUuid()))
                .sorted((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()))
                .toList();
    }

    /**
     * Get evidence by file type.
     */
    public List<Evidence> getEvidenceByType(Evidence.FileType type) {
        return cache.values().stream()
                .filter(e -> e.getFileType() == type)
                .sorted((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()))
                .toList();
    }

    /**
     * Search evidence by tags.
     */
    public List<Evidence> searchByTags(String... tags) {
        Set<String> searchTags = Set.of(tags);
        return cache.values().stream()
                .filter(e -> e.getTags().stream().anyMatch(searchTags::contains))
                .sorted((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()))
                .toList();
    }

    /**
     * Get recent evidence (newest first).
     */
    public List<Evidence> getRecentEvidence(int limit) {
        return cache.values().stream()
                .sorted((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()))
                .limit(limit)
                .toList();
    }

    /**
     * Get total storage used by evidence in bytes.
     */
    public long getTotalStorageUsed() {
        return cache.values().stream()
                .mapToLong(Evidence::getFileSize)
                .sum();
    }

    /**
     * Get evidence count.
     */
    public int getEvidenceCount() {
        return cache.size();
    }

    private Evidence evidenceFromResultSet(java.sql.ResultSet rs) throws java.sql.SQLException {
        Evidence evidence = new Evidence(
                rs.getString("id"),
                UUID.fromString(rs.getString("uploader_uuid")),
                rs.getString("uploader_name"),
                rs.getString("file_name"),
                rs.getString("file_path"),
                Evidence.FileType.valueOf(rs.getString("file_type")),
                rs.getLong("file_size"),
                rs.getString("mime_type"),
                rs.getLong("created_at")
        );

        String playerUuid = rs.getString("player_uuid");
        if (playerUuid != null) {
            evidence.setPlayerUuid(UUID.fromString(playerUuid));
        }
        evidence.setPlayerName(rs.getString("player_name"));
        evidence.setLinkedPunishmentId(rs.getString("linked_punishment_id"));
        evidence.setDescription(rs.getString("description"));

        String tagsStr = rs.getString("tags");
        if (tagsStr != null && !tagsStr.isEmpty()) {
            evidence.setTags(new ArrayList<>(Arrays.asList(tagsStr.split(","))));
        }

        String source = rs.getString("source");
        if (source != null) {
            evidence.setSource(Evidence.Source.valueOf(source));
        }

        return evidence;
    }

    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot < 0) return "";
        return fileName.substring(lastDot + 1);
    }

    /**
     * Validate if a file extension is allowed.
     */
    public static boolean isAllowedExtension(String extension) {
        return ALLOWED_EXTENSIONS.contains(extension.toLowerCase().replace(".", ""));
    }

    /**
     * Validate if a file size is within limits.
     */
    public static boolean isValidFileSize(long size) {
        return size > 0 && size <= Evidence.MAX_FILE_SIZE;
    }

    /**
     * Get the evidence directory path.
     */
    public Path getEvidenceDirectory() {
        return evidenceDirectory;
    }

    /**
     * Shutdown and cleanup.
     */
    public void shutdown() {
        cache.clear();
    }
}
