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
import java.util.concurrent.CompletableFuture;

/**
 * Automatic plugin updater that checks GitHub releases and downloads new versions.
 * On server restart, the new JAR will be loaded automatically.
 */
public class GitHubAutoUpdater {

    private static final String GITHUB_API = "https://api.github.com/repos/%s/%s/releases/latest";
    private static final String USER_AGENT = "ModereX-AutoUpdater";

    private final ModereX plugin;
    private final String owner;
    private final String repo;

    private String latestVersion;
    private String downloadUrl;
    private boolean updateAvailable;
    private boolean updateDownloaded;

    public GitHubAutoUpdater(ModereX plugin) {
        this.plugin = plugin;
        // Get GitHub repo info from config
        String githubRepo = plugin.getConfigManager().getSettings().getGithubRepo();
        String[] parts = githubRepo.split("/");
        if (parts.length == 2) {
            this.owner = parts[0];
            this.repo = parts[1];
        } else {
            // Default to blockforge/ModereX
            this.owner = "blockforge";
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
     * Check GitHub releases for a newer version.
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

            String currentVersion = plugin.getDescription().getVersion();
            updateAvailable = !currentVersion.equals(latestVersion) &&
                    isNewerVersion(latestVersion, currentVersion);

            if (updateAvailable) {
                // Find the JAR asset
                JsonArray assets = release.getAsJsonArray("assets");
                for (int i = 0; i < assets.size(); i++) {
                    JsonObject asset = assets.get(i).getAsJsonObject();
                    String name = asset.get("name").getAsString();
                    if (name.endsWith(".jar") && name.toLowerCase().contains("moderex")) {
                        downloadUrl = asset.get("browser_download_url").getAsString();
                        break;
                    }
                }

                if (downloadUrl == null) {
                    plugin.logDebug("No JAR asset found in GitHub release");
                    return false;
                }

                plugin.getLogger().info("New version available on GitHub: " + latestVersion +
                        " (current: " + currentVersion + ")");

                // Auto-download if enabled
                if (plugin.getConfigManager().getSettings().isGithubAutoDownload()) {
                    downloadUpdate();
                }

                notifyStaff();
            } else {
                plugin.logDebug("Running latest version from GitHub.");
            }

            return updateAvailable;
        } catch (Exception e) {
            plugin.logDebug("GitHub update check failed: " + e.getMessage());
            return false;
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
                            "current", plugin.getDescription().getVersion()));
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
                    "current", plugin.getDescription().getVersion()));
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
