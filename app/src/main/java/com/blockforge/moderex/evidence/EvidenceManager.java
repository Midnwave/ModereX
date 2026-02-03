package com.blockforge.moderex.evidence;

import com.blockforge.moderex.ModereX;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    // Medal.tv URL patterns
    private static final Pattern MEDAL_URL_PATTERN = Pattern.compile(
            "https?://(?:www\\.)?medal\\.tv/(?:clips/|games/[^/]+/clips/|u/[^/]+/clips/)([a-zA-Z0-9]+)"
    );
    private static final Pattern MEDAL_VIDEO_URL_PATTERN = Pattern.compile(
            "\"contentUrl\"\\s*:\\s*\"([^\"]+\\.mp4[^\"]*)\""
    );
    private static final int MEDAL_DOWNLOAD_TIMEOUT_MS = 60000; // 60 seconds

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
     * Import a video clip from Medal.tv.
     *
     * @param medalUrl The Medal.tv URL
     * @param uploaderUuid The UUID of the uploader
     * @param uploaderName The name of the uploader
     * @param progressCallback Optional callback for download progress (0-100)
     * @return CompletableFuture with the result containing Evidence or error message
     */
    public CompletableFuture<MedalImportResult> importFromMedal(String medalUrl, UUID uploaderUuid,
                                                                  String uploaderName, Consumer<Integer> progressCallback) {
        return CompletableFuture.supplyAsync(() -> {
            plugin.logDebug("[Medal] Starting import from: " + medalUrl);

            // Parse Medal URL to extract clip ID
            String clipId = parseMedalUrl(medalUrl);
            if (clipId == null) {
                return MedalImportResult.error("Invalid Medal.tv URL format. Supported formats: " +
                        "medal.tv/clips/xxx, medal.tv/games/xxx/clips/xxx, medal.tv/u/xxx/clips/xxx");
            }

            plugin.logDebug("[Medal] Extracted clip ID: " + clipId);

            // Fetch the Medal page to find the video URL
            String videoUrl;
            try {
                videoUrl = fetchMedalVideoUrl(medalUrl);
                if (videoUrl == null) {
                    return MedalImportResult.error("Could not find video URL. The clip may be private or deleted.");
                }
            } catch (IOException e) {
                plugin.logError("Failed to fetch Medal page", e);
                return MedalImportResult.error("Failed to fetch Medal page: " + e.getMessage());
            }

            plugin.logDebug("[Medal] Found video URL: " + videoUrl);

            // Download the video
            try {
                Evidence evidence = downloadMedalVideo(videoUrl, clipId, uploaderUuid, uploaderName, progressCallback);
                if (evidence == null) {
                    return MedalImportResult.error("Failed to download video. File may be too large.");
                }

                // Tag it with Medal metadata
                evidence.setSource(Evidence.Source.MEDAL_IMPORT);
                evidence.addTag("medal");
                evidence.addTag("clip:" + clipId);
                evidence.setDescription("Imported from Medal.tv: " + medalUrl);

                // Update in database
                saveEvidence(evidence);

                plugin.logDebug("[Medal] Successfully imported: " + evidence.getId());
                return MedalImportResult.success(evidence);

            } catch (IOException e) {
                plugin.logError("Failed to download Medal video", e);
                return MedalImportResult.error("Failed to download video: " + e.getMessage());
            }
        });
    }

    /**
     * Parse a Medal.tv URL to extract the clip ID.
     *
     * @param url The Medal URL
     * @return The clip ID, or null if invalid
     */
    public String parseMedalUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        Matcher matcher = MEDAL_URL_PATTERN.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * Check if a URL is a valid Medal.tv URL.
     */
    public boolean isMedalUrl(String url) {
        return parseMedalUrl(url) != null;
    }

    /**
     * Fetch the Medal page and extract the direct video URL.
     */
    private String fetchMedalVideoUrl(String medalUrl) throws IOException {
        URL url = new URL(medalUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        try {
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                plugin.logDebug("[Medal] HTTP response code: " + responseCode);
                return null;
            }

            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
            }

            // Look for video URL in JSON-LD or og:video meta tag
            String html = content.toString();

            // Try JSON-LD contentUrl first
            Matcher matcher = MEDAL_VIDEO_URL_PATTERN.matcher(html);
            if (matcher.find()) {
                String videoUrl = matcher.group(1).replace("\\u0026", "&");
                return videoUrl;
            }

            // Try og:video meta tag
            Pattern ogVideoPattern = Pattern.compile("<meta[^>]+property=\"og:video\"[^>]+content=\"([^\"]+)\"");
            matcher = ogVideoPattern.matcher(html);
            if (matcher.find()) {
                return matcher.group(1);
            }

            // Try data-video-src attribute
            Pattern dataSrcPattern = Pattern.compile("data-video-src=\"([^\"]+\\.mp4[^\"]*)\"");
            matcher = dataSrcPattern.matcher(html);
            if (matcher.find()) {
                return matcher.group(1);
            }

            return null;
        } finally {
            conn.disconnect();
        }
    }

    /**
     * Download a Medal video and create evidence record.
     */
    private Evidence downloadMedalVideo(String videoUrl, String clipId, UUID uploaderUuid,
                                         String uploaderName, Consumer<Integer> progressCallback) throws IOException {
        URL url = new URL(videoUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        conn.setConnectTimeout(MEDAL_DOWNLOAD_TIMEOUT_MS);
        conn.setReadTimeout(MEDAL_DOWNLOAD_TIMEOUT_MS);

        try {
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                plugin.logDebug("[Medal] Video download HTTP response: " + responseCode);
                return null;
            }

            long contentLength = conn.getContentLengthLong();

            // Check file size
            if (contentLength > Evidence.MAX_FILE_SIZE) {
                plugin.logDebug("[Medal] Video too large: " + contentLength + " bytes");
                return null;
            }

            // Generate unique file name
            String uniqueFileName = UUID.randomUUID().toString() + ".mp4";
            Path targetPath = evidenceDirectory.resolve(uniqueFileName);

            // Download with progress tracking
            try (InputStream in = conn.getInputStream();
                 OutputStream out = Files.newOutputStream(targetPath)) {

                byte[] buffer = new byte[8192];
                long totalRead = 0;
                int lastProgress = 0;
                int read;

                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                    totalRead += read;

                    // Report progress
                    if (progressCallback != null && contentLength > 0) {
                        int progress = (int) ((totalRead * 100) / contentLength);
                        if (progress > lastProgress) {
                            lastProgress = progress;
                            progressCallback.accept(progress);
                        }
                    }
                }
            }

            // Verify download
            long actualSize = Files.size(targetPath);
            if (actualSize == 0) {
                Files.deleteIfExists(targetPath);
                return null;
            }

            // Create evidence record
            String originalFileName = "medal_" + clipId + ".mp4";
            Evidence evidence = new Evidence(
                    uploaderUuid, uploaderName, originalFileName,
                    uniqueFileName, Evidence.FileType.VIDEO_MP4, actualSize, "video/mp4"
            );

            // Save to database and cache
            saveEvidence(evidence);
            cache.put(evidence.getId(), evidence);

            plugin.logDebug("[Medal] Downloaded: " + evidence.getId() + " (" + evidence.getFormattedSize() + ")");
            return evidence;

        } finally {
            conn.disconnect();
        }
    }

    /**
     * Result class for Medal import operations.
     */
    public static class MedalImportResult {
        private final boolean success;
        private final Evidence evidence;
        private final String error;

        private MedalImportResult(boolean success, Evidence evidence, String error) {
            this.success = success;
            this.evidence = evidence;
            this.error = error;
        }

        public static MedalImportResult success(Evidence evidence) {
            return new MedalImportResult(true, evidence, null);
        }

        public static MedalImportResult error(String message) {
            return new MedalImportResult(false, null, message);
        }

        public boolean isSuccess() { return success; }
        public Evidence getEvidence() { return evidence; }
        public String getError() { return error; }
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
