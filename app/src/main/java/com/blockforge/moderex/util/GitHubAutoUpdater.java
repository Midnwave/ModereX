package com.blockforge.moderex.util;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.config.lang.MessageKey;
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
 * Automatic plugin updater that checks GitHub releases folder and downloads new versions.
 * Downloads from: https://raw.githubusercontent.com/{owner}/{repo}/main/releases/ModereX-latest.jar
 * Checks build info: https://raw.githubusercontent.com/{owner}/{repo}/main/releases/build-info.txt
 */
public class GitHubAutoUpdater {

    private static final String RAW_URL = "https://raw.githubusercontent.com/%s/%s/main/releases/";
    private static final String BUILD_INFO_FILE = "build-info.txt";
    private static final String JAR_FILE = "ModereX-latest.jar";
    private static final String USER_AGENT = "ModereX-AutoUpdater";

    private final ModereX plugin;
    private final String owner;
    private final String repo;
    private final Path lastUpdateFile;

    private String latestVersion;
    private String latestBuildDate;
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
            this.owner = "Midnwave";
            this.repo = "ModereX";
        }

        // Set download URL
        this.downloadUrl = String.format(RAW_URL, owner, repo) + JAR_FILE;
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
     * Check GitHub releases folder for a newer build.
     */
    public boolean check() {
        try {
            String buildInfoUrl = String.format(RAW_URL, owner, repo) + BUILD_INFO_FILE;
            URL url = new URL(buildInfoUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                plugin.logDebug("GitHub build-info.txt returned " + responseCode);
                return false;
            }

            // Read build info
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String buildDate = null;
            String version = null;
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.matches("\\d{4}-\\d{2}-\\d{2}T.*")) {
                    buildDate = line;
                } else if (line.startsWith("Version:")) {
                    version = line.substring("Version:".length()).trim();
                }
            }
            reader.close();

            if (buildDate == null) {
                plugin.logDebug("No build date found in build-info.txt");
                return false;
            }

            latestBuildDate = buildDate;
            latestVersion = version != null ? version : "unknown";

            // Check if this is a newer build by comparing dates
            String lastKnownDate = loadLastUpdateDate();

            if (lastKnownDate == null) {
                updateAvailable = true;
                plugin.getLogger().info("GitHub build found: " + latestVersion + " (first check)");
            } else {
                // Simple string comparison works for ISO dates
                updateAvailable = buildDate.compareTo(lastKnownDate) > 0;

                if (updateAvailable) {
                    plugin.getLogger().info("New build available on GitHub: " + latestVersion +
                            " (built: " + buildDate + ")");
                } else {
                    plugin.logDebug("Running latest build from GitHub.");
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
            // Fetch build info
            String buildInfoUrl = String.format(RAW_URL, owner, repo) + BUILD_INFO_FILE;
            URL url = new URL(buildInfoUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                return "GitHub returned HTTP " + responseCode + " - no build found in releases folder.";
            }

            // Read build info
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String buildDate = null;
            String version = null;
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.matches("\\d{4}-\\d{2}-\\d{2}T.*")) {
                    buildDate = line;
                } else if (line.startsWith("Version:")) {
                    version = line.substring("Version:".length()).trim();
                }
            }
            reader.close();

            if (buildDate == null) {
                return "No build info found in releases folder.";
            }

            latestBuildDate = buildDate;
            latestVersion = version != null ? version : "unknown";

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
     * Download the update JAR file.
     */
    public boolean downloadUpdate() {
        try {
            plugin.getLogger().info("Downloading update from GitHub...");

            // Create update folder if it doesn't exist
            File updateFolder = new File(plugin.getDataFolder().getParentFile(), "update");
            if (!updateFolder.exists()) {
                updateFolder.mkdirs();
            }

            // Download from releases folder
            URL url = new URL(downloadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(60000);
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

            // Move to update folder with version name
            String jarName = "ModereX-" + (latestVersion != null ? latestVersion : "latest") + ".jar";
            Path targetPath = updateFolder.toPath().resolve(jarName);
            Files.move(tempFile, targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Save the build date so we know we have this version
            if (latestBuildDate != null) {
                saveLastUpdateDate(latestBuildDate);
            }

            updateDownloaded = true;
            plugin.getLogger().info("Update downloaded successfully: " + jarName);
            plugin.getLogger().info("The update will be applied on server restart.");

            notifyDownloadComplete();
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to download update: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

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

    private void saveLastUpdateDate(String date) {
        try {
            Files.createDirectories(lastUpdateFile.getParent());
            Files.writeString(lastUpdateFile, date);
        } catch (Exception e) {
            plugin.logDebug("Failed to save last update date: " + e.getMessage());
        }
    }

    private void notifyStaff() {
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("moderex.admin")) {
                    player.sendMessage(plugin.getLanguageManager().getPrefixed(MessageKey.GITHUB_UPDATE_AVAILABLE,
                            "version", latestVersion != null ? latestVersion : "new build",
                            "current", plugin.getPluginMeta().getVersion()));
                }
            }
        });
    }

    private void notifyDownloadComplete() {
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("moderex.admin")) {
                    player.sendMessage(plugin.getLanguageManager().getPrefixed(MessageKey.GITHUB_UPDATE_DOWNLOADED,
                            "version", latestVersion != null ? latestVersion : "new build"));
                }
            }
        });
    }

    public void notifyPlayer(Player player) {
        if (!player.hasPermission("moderex.admin")) return;

        if (updateDownloaded) {
            player.sendMessage(plugin.getLanguageManager().getPrefixed(MessageKey.GITHUB_UPDATE_PENDING,
                    "version", latestVersion != null ? latestVersion : "new build"));
        } else if (updateAvailable) {
            player.sendMessage(plugin.getLanguageManager().getPrefixed(MessageKey.GITHUB_UPDATE_AVAILABLE,
                    "version", latestVersion != null ? latestVersion : "new build",
                    "current", plugin.getPluginMeta().getVersion()));
        }
    }

    private String formatSize(long bytes) {
        if (bytes < 0) return "unknown size";
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }

    public void schedulePeriodicCheck() {
        // 24 hours in ticks
        long interval = 20L * 60 * 60 * 24;

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            try {
                if (!updateDownloaded) {
                    check();
                }
            } catch (Exception ignored) {
            }
        }, interval, interval);

        plugin.logDebug("GitHub auto-update periodic check scheduled (every 24 hours)");
    }

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
