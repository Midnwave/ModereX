package com.blockforge.moderex.util;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.config.lang.MessageKey;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class UpdateChecker {

    private static final String MODRINTH_API = "https://api.modrinth.com/v2/project/moderex/version";
    private static final String USER_AGENT = "ModereX-UpdateChecker";

    private final ModereX plugin;
    private String latestVersion;
    private boolean updateAvailable;

    public UpdateChecker(ModereX plugin) {
        this.plugin = plugin;
    }

    public CompletableFuture<Boolean> checkAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return check();
            } catch (Exception e) {
                plugin.logDebug("Update check failed: " + e.getMessage());
                return false;
            }
        });
    }

    public boolean check() {
        try {
            URL url = new URL(MODRINTH_API);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            if (connection.getResponseCode() != 200) {
                return false;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse response - Modrinth returns an array of versions
            String jsonResponse = response.toString();
            if (jsonResponse.startsWith("[")) {
                // Get first (latest) version
                com.google.gson.JsonArray versions = JsonParser.parseString(jsonResponse).getAsJsonArray();
                if (versions.size() > 0) {
                    JsonObject latest = versions.get(0).getAsJsonObject();
                    latestVersion = latest.get("version_number").getAsString();

                    String currentVersion = plugin.getDescription().getVersion();
                    updateAvailable = !currentVersion.equals(latestVersion) &&
                            isNewerVersion(latestVersion, currentVersion);

                    if (updateAvailable) {
                        plugin.getLogger().info("A new version is available: " + latestVersion +
                                " (current: " + currentVersion + ")");
                        notifyStaff();
                    } else {
                        plugin.getLogger().info("You are running the latest version.");
                    }

                    return updateAvailable;
                }
            }

            return false;
        } catch (Exception e) {
            plugin.logDebug("Update check failed: " + e.getMessage());
            return false;
        }
    }

    private boolean isNewerVersion(String newVersion, String currentVersion) {
        try {
            String[] newParts = newVersion.replaceAll("[^0-9.]", "").split("\\.");
            String[] currentParts = currentVersion.replaceAll("[^0-9.]", "").split("\\.");

            int length = Math.max(newParts.length, currentParts.length);
            for (int i = 0; i < length; i++) {
                int newPart = i < newParts.length ? Integer.parseInt(newParts[i]) : 0;
                int currentPart = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;

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

    private void notifyStaff() {
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("moderex.admin")) {
                    player.sendMessage(plugin.getLanguageManager().getPrefixed(MessageKey.UPDATE_AVAILABLE,
                            "version", latestVersion));
                }
            }
        });
    }

    public void notifyPlayer(Player player) {
        if (updateAvailable && player.hasPermission("moderex.admin")) {
            player.sendMessage(plugin.getLanguageManager().getPrefixed(MessageKey.UPDATE_AVAILABLE,
                    "version", latestVersion));
        }
    }

    public void schedulePeriodicCheck() {
        // 24 hours in ticks (20 ticks/second * 60 * 60 * 24)
        long interval = 20L * 60 * 60 * 24;

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            try {
                check();
            } catch (Exception ignored) {
            }
        }, interval, interval);
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }
}
