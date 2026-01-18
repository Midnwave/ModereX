package com.blockforge.moderex.staff;

import com.blockforge.moderex.ModereX;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VanishLevel {

    private final ModereX plugin;
    private final Map<UUID, Integer> playerLevels = new HashMap<>();
    private final Map<UUID, Long> vanishStartTimes = new HashMap<>();
    private final Map<UUID, Integer> fileLevels = new HashMap<>();
    private final Map<String, Integer> fileNameLevels = new HashMap<>();

    private boolean luckPermsAvailable = false;
    private FileConfiguration levelsConfig;
    private int defaultLevel = 1;
    private int opLevel = 100;

    public VanishLevel(ModereX plugin) {
        this.plugin = plugin;
        checkLuckPerms();
        loadLevelsConfig();
    }

    /**
     * Check if LuckPerms is available.
     */
    private void checkLuckPerms() {
        luckPermsAvailable = Bukkit.getPluginManager().getPlugin("LuckPerms") != null;
        if (!luckPermsAvailable) {
            plugin.getLogger().info("LuckPerms not found - using file-based vanish levels from vanish/levels.yml");
        }
    }

    /**
     * Load vanish levels from vanish/levels.yml (when LuckPerms is not available).
     */
    private void loadLevelsConfig() {
        if (luckPermsAvailable) {
            return; // LuckPerms handles levels via permissions
        }

        File vanishFolder = new File(plugin.getDataFolder(), "vanish");
        if (!vanishFolder.exists()) {
            vanishFolder.mkdirs();
        }

        File levelsFile = new File(vanishFolder, "levels.yml");
        if (!levelsFile.exists()) {
            plugin.saveResource("vanish/levels.yml", false);
        }

        levelsConfig = YamlConfiguration.loadConfiguration(levelsFile);

        // Load default settings
        defaultLevel = levelsConfig.getInt("default-level", 1);
        opLevel = levelsConfig.getInt("op-level", 100);

        // Load player-specific levels
        if (levelsConfig.contains("players")) {
            for (String key : levelsConfig.getConfigurationSection("players").getKeys(false)) {
                int level = levelsConfig.getInt("players." + key);

                // Try to parse as UUID
                try {
                    UUID uuid = UUID.fromString(key);
                    fileLevels.put(uuid, level);
                } catch (IllegalArgumentException e) {
                    // Not a UUID, treat as username
                    fileNameLevels.put(key.toLowerCase(), level);
                }
            }
        }

        plugin.logDebug("[VanishLevel] Loaded " + fileLevels.size() + " UUID-based levels and " +
                       fileNameLevels.size() + " name-based levels from file");
    }

    /**
     * Get the vanish level for a player.
     *
     * With LuckPerms:
     * 1. Opped players get op-level from config
     * 2. Players with moderex.vanish.level.X get level X
     * 3. Players get default-level from config
     *
     * Without LuckPerms:
     * 1. Opped players get op-level from levels.yml
     * 2. Players with UUID in levels.yml get configured level
     * 3. Players with username in levels.yml get configured level
     * 4. Players get default-level from levels.yml
     */
    public int getVanishLevel(Player player) {
        if (luckPermsAvailable) {
            // LuckPerms mode: use permissions
            if (player.isOp()) {
                return plugin.getConfigManager().getSettings().getVanishLevelsOpLevel();
            }

            for (int level = 100; level >= 1; level--) {
                if (player.hasPermission("moderex.vanish.level." + level)) {
                    return level;
                }
            }

            return plugin.getConfigManager().getSettings().getVanishLevelsDefaultLevel();
        } else {
            // File mode: use levels.yml
            if (player.isOp()) {
                return opLevel;
            }

            // Check UUID
            if (fileLevels.containsKey(player.getUniqueId())) {
                return fileLevels.get(player.getUniqueId());
            }

            // Check username
            if (fileNameLevels.containsKey(player.getName().toLowerCase())) {
                return fileNameLevels.get(player.getName().toLowerCase());
            }

            return defaultLevel;
        }
    }

    /**
     * Get the see level for a player.
     * This determines what level of vanished players they can see.
     * Checks permissions in this order:
     * 1. Players with moderex.vanish.see.level.X can see up to level X
     * 2. Default: can see up to (their vanish level - 1)
     */
    public int getSeeLevel(Player player) {
        for (int level = 100; level >= 1; level--) {
            if (player.hasPermission("moderex.vanish.see.level." + level)) {
                return level;
            }
        }

        int vanishLevel = getVanishLevel(player);
        return Math.max(0, vanishLevel - 1);
    }

    /**
     * Check if observer can see target player (who is vanished)
     * Returns true if:
     * - Level system is disabled
     * - Observer is oppped
     * - Observer has permission to see vanished players
     * - Observers see level >= targets vanish level
     * - Staff can see lower levels is enabled and observers vanish level > targets vanish level
     */
    public boolean canSee(Player observer, Player target) {
        if (!plugin.getConfigManager().getSettings().isVanishLevelsEnabled()) {
            return observer.hasPermission("moderex.command.vanish");
        }

        if (observer.isOp()) {
            return true;
        }

        if (!observer.hasPermission("moderex.command.vanish")) {
            return false;
        }

        int targetLevel = getStoredLevel(target.getUniqueId());
        int observerSeeLevel = getSeeLevel(observer);

        if (observerSeeLevel >= targetLevel) {
            return true;
        }

        if (plugin.getConfigManager().getSettings().isVanishLevelsStaffCanSeeLowerLevels()) {
            int observerVanishLevel = getVanishLevel(observer);
            return observerVanishLevel > targetLevel;
        }

        return false;
    }

    /**
     * Store a player's level when they vanish.
     */
    public void storeLevel(UUID uuid, int level) {
        playerLevels.put(uuid, level);
        vanishStartTimes.put(uuid, System.currentTimeMillis());
    }

    /**
     * Get a stored level for a vanished player.
     */
    public int getStoredLevel(UUID uuid) {
        return playerLevels.getOrDefault(uuid, plugin.getConfigManager().getSettings().getVanishLevelsDefaultLevel());
    }

    /**
     * Remove stored level when player unvanishes.
     */
    public void removeLevel(UUID uuid) {
        playerLevels.remove(uuid);
        vanishStartTimes.remove(uuid);
    }

    /**
     * Get how long a player has been vanished (in milliseconds).
     */
    public long getVanishDuration(UUID uuid) {
        Long startTime = vanishStartTimes.get(uuid);
        if (startTime == null) {
            return 0;
        }
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Format vanish duration as a human-readable string.
     */
    public String formatDuration(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + "d " + (hours % 24) + "h";
        } else if (hours > 0) {
            return hours + "h " + (minutes % 60) + "m";
        } else if (minutes > 0) {
            return minutes + "m " + (seconds % 60) + "s";
        } else {
            return seconds + "s";
        }
    }

    /**
     * Clear all stored levels.
     */
    public void clearAll() {
        playerLevels.clear();
        vanishStartTimes.clear();
    }

    /**
     * Check if LuckPerms is available for permissions-based levels.
     */
    public boolean isLuckPermsAvailable() {
        return luckPermsAvailable;
    }

    /**
     * Reload levels config from file.
     */
    public void reloadLevelsConfig() {
        fileLevels.clear();
        fileNameLevels.clear();
        checkLuckPerms();
        loadLevelsConfig();
    }
}
