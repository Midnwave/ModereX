package com.blockforge.moderex.util;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.license.LicenseManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Automatic plugin updater that checks GitHub releases folder and downloads new versions.
 * Supports both public repos (raw URL) and private repos (API with token).
 */
public class GitHubAutoUpdater {

    // For public repos
    private static final String RAW_URL = "https://raw.githubusercontent.com/%s/%s/main/releases/";
    // For private repos (requires token)
    private static final String API_URL = "https://api.github.com/repos/%s/%s/contents/releases/";

    private static final String BUILD_INFO_FILE = "build-info.txt";
    private static final String USER_AGENT = "ModereX-AutoUpdater";

    private final ModereX plugin;
    private final String owner;
    private final String repo;
    private final String token;
    private final Path lastUpdateFile;
    private final boolean isPrivate;

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
            this.owner = "Midnwave";
            this.repo = "ModereX";
        }

        // Get token (empty string if not set)
        this.token = plugin.getConfigManager().getSettings().getGithubToken();
        this.isPrivate = token != null && !token.isEmpty();

        // Download URL will be set dynamically based on version from build-info.txt
        this.downloadUrl = null;
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
            String buildInfoContent = fetchBuildInfo();
            if (buildInfoContent == null) {
                return false;
            }

            // Parse build info
            String buildDate = null;
            String version = null;
            for (String line : buildInfoContent.split("\n")) {
                line = line.trim();
                if (line.matches("\\d{4}-\\d{2}-\\d{2}T.*")) {
                    buildDate = line;
                } else if (line.startsWith("Version:")) {
                    version = line.substring("Version:".length()).trim();
                }
            }

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
                updateAvailable = buildDate.compareTo(lastKnownDate) > 0;

                if (updateAvailable) {
                    plugin.getLogger().info("New build available on GitHub: " + latestVersion +
                            " (built: " + buildDate + ")");
                } else {
                    plugin.logDebug("Running latest build from GitHub.");
                }
            }

            if (updateAvailable) {
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
     * Fetch build-info.txt content from GitHub.
     */
    private String fetchBuildInfo() {
        try {
            // Add cache-busting parameter to always get fresh build info
            String cacheBuster = "?t=" + System.currentTimeMillis();
            String url;
            if (isPrivate) {
                url = String.format(API_URL, owner, repo) + BUILD_INFO_FILE;
            } else {
                url = String.format(RAW_URL, owner, repo) + BUILD_INFO_FILE + cacheBuster;
            }

            HttpURLConnection connection = createConnection(url);
            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                plugin.logDebug("GitHub build-info.txt returned " + responseCode);
                return null;
            }

            if (isPrivate) {
                // API returns JSON with base64-encoded content
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
                String content = json.get("content").getAsString();
                // Remove newlines from base64 and decode
                content = content.replace("\n", "").replace("\r", "");
                return new String(Base64.getDecoder().decode(content));
            } else {
                // Raw URL returns plain text
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                reader.close();
                return content.toString();
            }
        } catch (Exception e) {
            plugin.logDebug("Failed to fetch build info: " + e.getMessage());
            return null;
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
            String buildInfoContent = fetchBuildInfo();
            if (buildInfoContent == null) {
                return "GitHub returned error - no build found in releases folder." +
                       (isPrivate ? " Check that your token is valid." : "");
            }

            // Parse build info
            String buildDate = null;
            String version = null;
            for (String line : buildInfoContent.split("\n")) {
                line = line.trim();
                if (line.matches("\\d{4}-\\d{2}-\\d{2}T.*")) {
                    buildDate = line;
                } else if (line.startsWith("Version:")) {
                    version = line.substring("Version:".length()).trim();
                }
            }

            if (buildDate == null) {
                return "No build info found in releases folder.";
            }

            latestBuildDate = buildDate;
            latestVersion = version != null ? version : "unknown";

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
            if (latestVersion == null || latestVersion.isEmpty() || latestVersion.equals("unknown")) {
                plugin.getLogger().warning("Cannot download update: version not detected from build-info.txt");
                return false;
            }

            // Construct download URL for versioned JAR (e.g., ModereX-2.0dev-236.jar)
            // Using versioned filename avoids caching issues with ModereX-latest.jar
            String jarFileName = "ModereX-" + latestVersion + ".jar";
            // Add cache-busting parameter to avoid GitHub/CDN caching
            String cacheBuster = "?t=" + System.currentTimeMillis();

            // CI builds unlicensed JARs in releases/ - licensed builds are done via admin panel
            if (isPrivate) {
                this.downloadUrl = String.format(API_URL, owner, repo) + jarFileName;
            } else {
                this.downloadUrl = String.format(RAW_URL, owner, repo) + jarFileName + cacheBuster;
            }

            plugin.getLogger().info("Downloading update from GitHub: " + jarFileName);

            File updateFolder = new File(plugin.getDataFolder().getParentFile(), "update");
            if (!updateFolder.exists()) {
                updateFolder.mkdirs();
            }

            Path tempFile = Files.createTempFile("moderex-update-", ".jar");

            if (isPrivate) {
                // Use API for private repos
                HttpURLConnection connection = createConnection(downloadUrl);
                int responseCode = connection.getResponseCode();

                if (responseCode != 200) {
                    plugin.getLogger().warning("Download failed with HTTP " + responseCode);
                    return false;
                }

                // API returns JSON with base64-encoded content
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
                String content = json.get("content").getAsString();
                content = content.replace("\n", "").replace("\r", "");
                byte[] jarBytes = Base64.getDecoder().decode(content);

                plugin.getLogger().info("Downloading " + formatSize(jarBytes.length) + "...");
                Files.write(tempFile, jarBytes);
            } else {
                // Use raw URL for public repos
                HttpURLConnection connection = createConnection(downloadUrl);
                int responseCode = connection.getResponseCode();

                if (responseCode != 200) {
                    plugin.getLogger().warning("Download failed with HTTP " + responseCode);
                    return false;
                }

                long contentLength = connection.getContentLengthLong();
                plugin.getLogger().info("Downloading " + formatSize(contentLength) + "...");

                try (InputStream in = connection.getInputStream();
                     OutputStream out = Files.newOutputStream(tempFile)) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
            }

            // Move to update folder
            String jarName = "ModereX-" + (latestVersion != null ? latestVersion : "latest") + ".jar";
            Path targetPath = updateFolder.toPath().resolve(jarName);
            Files.move(tempFile, targetPath, StandardCopyOption.REPLACE_EXISTING);

            // If this is a licensed build, patch the license token into the downloaded JAR
            LicenseManager lm = plugin.getLicenseManager();
            if (lm != null && lm.isLicensed() && lm.getLicenseToken() != null) {
                patchLicenseToken(targetPath, lm.getLicenseToken());
            }

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

    /**
     * Patch license token into a downloaded JAR file.
     * Opens the JAR as a ZIP filesystem and replaces license-token.properties.
     */
    private void patchLicenseToken(Path jarPath, String licenseToken) {
        try {
            URI jarUri = URI.create("jar:" + jarPath.toUri());
            try (FileSystem zipFs = FileSystems.newFileSystem(jarUri, Map.of())) {
                Path propsPath = zipFs.getPath("license-token.properties");
                String content = "# ModereX License Token - Patched by auto-updater\n" +
                        "token=" + licenseToken + "\n" +
                        "buildTimestamp=" + System.currentTimeMillis() + "\n";
                Files.writeString(propsPath, content);
            }
            plugin.getLogger().info("[AutoUpdate] License token patched into updated JAR");
        } catch (Exception e) {
            plugin.getLogger().warning("[AutoUpdate] Failed to patch license token: " + e.getMessage());
        }
    }

    /**
     * Create HTTP connection with proper headers.
     */
    private HttpURLConnection createConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(60000);
        connection.setInstanceFollowRedirects(true);

        // Add authorization header for private repos
        if (isPrivate && token != null && !token.isEmpty()) {
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
        }

        return connection;
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
                    Msg.send(player, plugin.getLanguageManager().getPrefixed(MessageKey.GITHUB_UPDATE_AVAILABLE,
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
                    Msg.send(player, plugin.getLanguageManager().getPrefixed(MessageKey.GITHUB_UPDATE_DOWNLOADED,
                            "version", latestVersion != null ? latestVersion : "new build"));
                }
            }
        });
    }

    public void notifyPlayer(Player player) {
        if (!player.hasPermission("moderex.admin")) return;

        if (updateDownloaded) {
            Msg.send(player, plugin.getLanguageManager().getPrefixed(MessageKey.GITHUB_UPDATE_PENDING,
                    "version", latestVersion != null ? latestVersion : "new build"));
        } else if (updateAvailable) {
            Msg.send(player, plugin.getLanguageManager().getPrefixed(MessageKey.GITHUB_UPDATE_AVAILABLE,
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
        long interval = 20L * 60 * 60 * 24; // 24 hours

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
