package com.blockforge.moderex.util;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.config.lang.MessageKey;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

/**
 * Automatic plugin updater that checks GitHub releases and downloads new versions.
 * Tracks by asset upload date so rebuilds of the same version are detected.
 * On server restart, the new JAR will be loaded automatically.
 */
public class GitHubAutoUpdater {

    private static final String GITHUB_API = "https://api.github.com/repos/%s/%s/releases/latest";
    private static final String USER_AGENT = "ModereX-AutoUpdater";

    private final ModereX plugin;
    private final String owner;
    private final String repo;
    private final Path lastUpdateFile;

    private String latestVersion;
    private String latestAssetDate;
    private String downloadUrl;
    private boolean updateAvailable;
    private boolean updateDownloaded;

    public GitHubAutoUpdater(ModereX plugin) {
        this.plugin = plugin;
        this.lastUpdateFile = plugin.getDataFolder().toPath().resolve(".data").resolve("last_github_update");

        // Get GitHub repo info from config
        String githubRepo = plugin.getConfigManager().getSettings().getGithubRepo();
        String[] parts = githubRepo.split("/");
        if (parts.length == 2) {
            this.owner = parts[0];
            this.repo = parts[1];
        } else {
            // Default
            this.owner = "crenshawcodes";
            this.repo = "ModereX";
        }
    }

    /**
     * Check for updates asynchronously.
     */
    public CompletableFuture<Boolean> checkAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return check();
            } catch (Exception e) {
                plugin.logDebug("GitHub update check failed: " + e.getMessage());
                return false;
            }
        });
    }

    /**
     * Check GitHub releases for a newer version or newer build.
     */
    public boolean check() {
        try {
            String apiUrl = String.format(GITHUB_API, owner, repo);
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                plugin.logDebug("GitHub API returned " + responseCode);
                return false;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JsonObject release = JsonParser.parseString(response.toString()).getAsJsonObject();
            String tagName = release.get("tag_name").getAsString();
            latestVersion = tagName.startsWith("v") ? tagName.substring(1) : tagName;

            // Find the JAR asset and get its upload date
            JsonArray assets = release.getAsJsonArray("assets");
            String assetUpdatedAt = null;

            for (int i = 0; i < assets.size(); i++) {
                JsonObject asset = assets.get(i).getAsJsonObject();
                String name = asset.get("name").getAsString();
                if (name.endsWith(".jar") && name.toLowerCase().contains("moderex")) {
                    downloadUrl = asset.get("browser_download_url").getAsString();
                    assetUpdatedAt = asset.get("updated_at").getAsString();
                    break;
                }
            }

            if (downloadUrl == null || assetUpdatedAt == null) {
                plugin.logDebug("No JAR asset found in GitHub release");
                return false;
            }

            latestAssetDate = assetUpdatedAt;

            // Check if this is a newer build by comparing asset dates
            String lastKnownDate = loadLastUpdateDate();

            // Update is available if:
            // 1. We've never downloaded before (lastKnownDate is null)
            // 2. The asset date is newer than our last download
            if (lastKnownDate == null) {
                updateAvailable = true;
                plugin.getLogger().info("GitHub release found: " + latestVersion + " (first check)");
            } else {
                try {
                    Instant lastInstant = Instant.parse(lastKnownDate);
                    Instant assetInstant = Instant.parse(assetUpdatedAt);
                    updateAvailable = assetInstant.isAfter(lastInstant);

                    if (updateAvailable) {
                        plugin.getLogger().info("New build available on GitHub: " + latestVersion +
                                " (uploaded: " + assetUpdatedAt + ")");
                    } else {
                        plugin.logDebug("Running latest build from GitHub.");
                    }
                } catch (Exception e) {
                    // If date parsing fails, fall back to assuming update available
                    updateAvailable = true;
                }
            }

            if (updateAvailable) {
                // Auto-download if enabled
                if (plugin.getConfigManager().getSettings().isGithubAutoDownload()) {
                    downloadUpdate();
                }
                notifyStaff();
            }

            return updateAvailable;
        } catch (Exception e) {
            plugin.logDebug("GitHub update check failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Force check and download update (for manual command).
     * Returns a message about what happened.
     */
    public CompletableFuture<String> forceUpdateAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return forceUpdate();
            } catch (Exception e) {
                return "Update check failed: " + e.getMessage();
            }
        });
    }

    /**
     * Force check and download, ignoring saved date.
     */
    public String forceUpdate() {
        try {
            String apiUrl = String.format(GITHUB_API, owner, repo);
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                return "GitHub API returned HTTP " + responseCode;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JsonObject release = JsonParser.parseString(response.toString()).getAsJsonObject();
            String tagName = release.get("tag_name").getAsString();
            latestVersion = tagName.startsWith("v") ? tagName.substring(1) : tagName;

            // Find the JAR asset
            JsonArray assets = release.getAsJsonArray("assets");
            downloadUrl = null;

            for (int i = 0; i < assets.size(); i++) {
                JsonObject asset = assets.get(i).getAsJsonObject();
                String name = asset.get("name").getAsString();
                if (name.endsWith(".jar") && name.toLowerCase().contains("moderex")) {
                    downloadUrl = asset.get("browser_download_url").getAsString();
                    latestAssetDate = asset.get("updated_at").getAsString();
                    break;
                }
            }

            if (downloadUrl == null) {
                return "No JAR asset found in the latest GitHub release.";
            }

            // Download the update
            if (downloadUpdate()) {
                return "Update downloaded successfully! Version: " + latestVersion +
                       "\nRestart the server to apply the update.";
            } else {
                return "Failed to download the update.";
            }
        } catch (Exception e) {
            return "Update check failed: " + e.getMessage();
        }
    }

    /**
     * Download the update JAR to the update folder.
     */
    public CompletableFuture<Boolean> downloadUpdateAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return downloadUpdate();
            } catch (Exception e) {
                plugin.logDebug("Update download failed: " + e.getMessage());
                return false;
            }
        });
    }

    /**
     * Download the update JAR file.
     */
    public boolean downloadUpdate() {
        if (downloadUrl == null) {
            plugin.getLogger().warning("No download URL available for update.");
            return false;
        }

        try {
            plugin.getLogger().info("Downloading update from GitHub...");

            // Create update folder if it doesn't exist
            File updateFolder = new File(plugin.getDataFolder().getParentFile(), "update");
            if (!updateFolder.exists()) {
                updateFolder.mkdirs();
            }

            // Download to temp file first
            URL url = new URL(downloadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(60000);

            // Follow redirects for GitHub
            connection.setInstanceFollowRedirects(true);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                plugin.getLogger().warning("Download failed with HTTP " + responseCode);
                return false;
            }

            long contentLength = connection.getContentLengthLong();
            plugin.getLogger().info("Downloading " + formatSize(contentLength) + "...");

            // Download to temp file
            Path tempFile = Files.createTempFile("moderex-update-", ".jar");
            try (InputStream in = connection.getInputStream();
                 OutputStream out = Files.newOutputStream(tempFile)) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                long totalRead = 0;
                int lastPercent = 0;

                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;

                    if (contentLength > 0) {
                        int percent = (int) ((totalRead * 100) / contentLength);
                        if (percent >= lastPercent + 10) {
                            plugin.logDebug("Download progress: " + percent + "%");
                            lastPercent = percent;
                        }
                    }
                }
            }

            // Move to update folder
            String jarName = "ModereX-" + latestVersion + ".jar";
            Path targetPath = updateFolder.toPath().resolve(jarName);
            Files.move(tempFile, targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Save the asset date so we know we have this version
            saveLastUpdateDate(latestAssetDate);

            updateDownloaded = true;
            plugin.getLogger().info("Update downloaded successfully: " + jarName);
            plugin.getLogger().info("The update will be applied on server restart.");

            // Notify online staff
            notifyDownloadComplete();

            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to download update: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Load the last update date from file.
     */
    private String loadLastUpdateDate() {
        try {
            if (Files.exists(lastUpdateFile)) {
                return Files.readString(lastUpdateFile).trim();
            }
        } catch (Exception e) {
            plugin.logDebug("Failed to load last update date: " + e.getMessage());
        }
        return null;
    }

    /**
     * Save the last update date to file.
     */
    private void saveLastUpdateDate(String date) {
        try {
            Files.createDirectories(lastUpdateFile.getParent());
            Files.writeString(lastUpdateFile, date);
        } catch (Exception e) {
            plugin.logDebug("Failed to save last update date: " + e.getMessage());
        }
    }

    /**
     * Compare version strings.
     */
    private boolean isNewerVersion(String newVersion, String currentVersion) {
        try {
            String[] newParts = newVersion.replaceAll("[^0-9.]", "").split("\\.");
            String[] currentParts = currentVersion.replaceAll("[^0-9.]", "").split("\\.");

            int length = Math.max(newParts.length, currentParts.length);
            for (int i = 0; i < length; i++) {
                int newPart = i < newParts.length && !newParts[i].isEmpty() ? Integer.parseInt(newParts[i]) : 0;
                int currentPart = i < currentParts.length && !currentParts[i].isEmpty() ? Integer.parseInt(currentParts[i]) : 0;

                if (newPart > currentPart) {
                    return true;
                } else if (newPart < currentPart) {
                    return false;
                }
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Notify staff about available update.
     */
    private void notifyStaff() {
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("moderex.admin")) {
                    player.sendMessage(plugin.getLanguageManager().getPrefixed(MessageKey.GITHUB_UPDATE_AVAILABLE,
                            "version", latestVersion,
                            "current", plugin.getPluginMeta().getVersion()));
                }
            }
        });
    }

    /**
     * Notify staff that download is complete.
     */
    private void notifyDownloadComplete() {
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("moderex.admin")) {
                    player.sendMessage(plugin.getLanguageManager().getPrefixed(MessageKey.GITHUB_UPDATE_DOWNLOADED,
                            "version", latestVersion));
                }
            }
        });
    }

    /**
     * Notify a specific player about available updates.
     */
    public void notifyPlayer(Player player) {
        if (!player.hasPermission("moderex.admin")) return;

        if (updateDownloaded) {
            player.sendMessage(plugin.getLanguageManager().getPrefixed(MessageKey.GITHUB_UPDATE_PENDING,
                    "version", latestVersion));
        } else if (updateAvailable) {
            player.sendMessage(plugin.getLanguageManager().getPrefixed(MessageKey.GITHUB_UPDATE_AVAILABLE,
                    "version", latestVersion,
                    "current", plugin.getPluginMeta().getVersion()));
        }
    }

    /**
     * Format file size for display.
     */
    private String formatSize(long bytes) {
        if (bytes < 0) return "unknown size";
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }

    /**
     * Schedule periodic update checks (every 24 hours).
     * Call this after the initial check to keep checking while server runs.
     */
    public void schedulePeriodicCheck() {
        // 24 hours in ticks (20 ticks/second * 60 * 60 * 24)
        long interval = 20L * 60 * 60 * 24;

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            try {
                // Only check if we haven't already downloaded an update
                if (!updateDownloaded) {
                    check();
                }
            } catch (Exception ignored) {
            }
        }, interval, interval);

        plugin.logDebug("GitHub auto-update periodic check scheduled (every 24 hours)");
    }

    // Getters
    public String getLatestVersion() {
        return latestVersion;
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public boolean isUpdateDownloaded() {
        return updateDownloaded;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
}
