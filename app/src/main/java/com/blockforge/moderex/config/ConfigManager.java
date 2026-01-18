package com.blockforge.moderex.config;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.util.TimeUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class ConfigManager {

    private static final int CURRENT_CONFIG_VERSION = 7;

    private final ModereX plugin;
    private final Settings settings;

    private File configFile;
    private FileConfiguration config;

    public ConfigManager(ModereX plugin) {
        this.plugin = plugin;
        this.settings = new Settings();
    }

    public boolean load() {
        try {
            // Create plugin data folder if it doesn't exist
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            // Load config.yml
            loadConfig();

            // Apply timezone setting
            TimeUtil.setTimezone(settings.getTimezone());

            return true;
        } catch (Exception e) {
            plugin.logError("Failed to load configuration", e);
            return false;
        }
    }

    private void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "config.yml");

        // Check if config exists
        if (!configFile.exists()) {
            // Save default config
            plugin.saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        // Check config version and migrate if needed
        int version = config.getInt("config-version", 0);

        // If no config version detected (version 0), reset the entire plugin folder
        if (version == 0 && configFile.exists()) {
            plugin.getLogger().warning("No config version detected - resetting plugin folder...");
            resetPluginFolder();
            // Reload config after reset
            config = YamlConfiguration.loadConfiguration(configFile);
            version = config.getInt("config-version", CURRENT_CONFIG_VERSION);
        }

        if (version < CURRENT_CONFIG_VERSION) {
            migrateConfig(version);
        }

        // Load settings from config
        loadSettings();
    }

    private void resetPluginFolder() {
        File dataFolder = plugin.getDataFolder();
        if (dataFolder.exists()) {
            deleteFolder(dataFolder);
        }
        dataFolder.mkdirs();
        plugin.saveResource("config.yml", false);
        plugin.getLogger().info("Plugin folder has been reset with fresh configuration");
    }

    private void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFolder(file);
                } else {
                    file.delete();
                }
            }
        }
        folder.delete();
    }

    private void loadSettings() {
        // General
        settings.setLanguage(config.getString("general.language", "en_US"));
        settings.setTimezone(config.getString("general.timezone", "America/Chicago"));
        settings.setDebugMode(config.getBoolean("general.debug", false));
        settings.setDebugWebhookUrl(config.getString("general.debug-webhook", ""));
        settings.setDialogsEnabled(config.getBoolean("general.dialogs-enabled", true));

        // Database
        settings.setDatabaseType(config.getString("database.type", "sqlite"));
        settings.setMysqlHost(config.getString("database.mysql.host", "localhost"));
        settings.setMysqlPort(config.getInt("database.mysql.port", 3306));
        settings.setMysqlDatabase(config.getString("database.mysql.database", "moderex"));
        settings.setMysqlUsername(config.getString("database.mysql.username", "root"));
        settings.setMysqlPassword(config.getString("database.mysql.password", ""));
        settings.setMysqlPoolSize(config.getInt("database.mysql.pool-size", 10));

        // Web panel
        settings.setWebPanelEnabled(config.getBoolean("webpanel.enabled", false));
        settings.setWebPanelPort(config.getInt("webpanel.port", 8080));
        settings.setWebPanelHost(config.getString("webpanel.host", ""));
        settings.setWebPanelServerName(config.getString("webpanel.server-name", "My Server"));

        // Generate auth token if not set
        String authToken = config.getString("webpanel.auth-token", "");
        if (authToken.isEmpty() && settings.isWebPanelEnabled()) {
            authToken = UUID.randomUUID().toString().replace("-", "");
            config.set("webpanel.auth-token", authToken);
            saveConfig();
        }
        settings.setWebPanelAuthToken(authToken);

        // Same-port mode (added in v4)
        settings.setWebPanelSamePort(config.getBoolean("webpanel.same-port", false));

        // AI Assistant settings (added in v5)
        settings.setAiEnabled(config.getBoolean("webpanel.ai.enabled", true));
        settings.setAiEndpoint(config.getString("webpanel.ai.endpoint", "http://localhost:11434/api/chat"));
        settings.setAiModel(config.getString("webpanel.ai.model", "devstral-2-123b-cloud"));
        settings.setAiApiKey(config.getString("webpanel.ai.api-key", ""));

        // Proxy
        settings.setProxyEnabled(config.getBoolean("proxy.enabled", false));
        settings.setProxyType(config.getString("proxy.type", "bungeecord"));

        // Update checker
        settings.setUpdateCheckerEnabled(config.getBoolean("update-checker.enabled", true));

        // Anticheat
        settings.setAnticheatAutoDetect(config.getBoolean("anticheat.auto-detect", true));
        settings.setAnticheatProvider(config.getString("anticheat.provider", "auto"));
        settings.setAnticheatRebrandAlerts(config.getBoolean("anticheat.rebrand-alerts", true));

        // Staff
        settings.setStaffChatSoundEnabled(config.getBoolean("staff.chat-sound-enabled", true));
        settings.setStaffChatSound(config.getString("staff.chat-sound", "ENTITY_ITEM_PICKUP"));

        // Chat
        settings.setDefaultSlowmodeSeconds(config.getInt("chat.default-slowmode", 0));
        settings.setChatEnabled(config.getBoolean("chat.enabled", true));

        // Mute
        settings.setMutedPlayersCanUseCommands(config.getBoolean("mute.allow-commands", false));
        settings.setMutedPlayersCanWriteSigns(config.getBoolean("mute.allow-signs", false));

        // Vanish
        settings.setVanishHideFromTablist(config.getBoolean("vanish.hide-from-tablist", true));
        settings.setVanishSilentContainers(config.getBoolean("vanish.silent-containers", true));
        settings.setVanishNoFootsteps(config.getBoolean("vanish.no-footsteps", true));
        settings.setVanishHideRealJoinLeave(config.getBoolean("vanish.hide-real-join-leave", true));
        settings.setVanishSaveVanishState(config.getBoolean("vanish.save-vanish-state", true));

        // Replay (added in v2)
        settings.setReplayEnabled(config.getBoolean("replay.enabled", true));
        settings.setReplayRecordOnAnticheat(config.getBoolean("replay.record-on-anticheat", true));
        settings.setReplayRecordWatchlist(config.getBoolean("replay.record-watchlist", true));
        settings.setReplayNearbyRadius(config.getInt("replay.nearby-radius", 20));
        settings.setReplayMaxDurationSeconds(config.getInt("replay.max-duration-seconds", 300));
        settings.setReplayMaxStored(config.getInt("replay.max-stored", 1000));

        // Anticheat alerts (added in v2)
        settings.setAnticheatAlertsEnabled(config.getBoolean("anticheat.alerts-enabled", true));

        settings.setConfigVersion(CURRENT_CONFIG_VERSION);
    }

    private void migrateConfig(int oldVersion) {
        plugin.getLogger().info("Migrating config from version " + oldVersion + " to " + CURRENT_CONFIG_VERSION);

        try {
            ConfigMerger merger = new ConfigMerger(plugin);
            merger.backupOldConfig(configFile);

            FileConfiguration oldConfig = YamlConfiguration.loadConfiguration(configFile);

            InputStream defaultConfigStream = plugin.getResource("config.yml");
            if (defaultConfigStream == null) {
                plugin.logError("Failed to load default config from resources", null);
                return;
            }

            FileConfiguration newConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultConfigStream)
            );

            ConfigMigration migration = ConfigMigrations.getMigration(CURRENT_CONFIG_VERSION);

            FileConfiguration mergedConfig = merger.merge(oldConfig, newConfig, migration);

            mergedConfig.set("config-version", CURRENT_CONFIG_VERSION);

            mergedConfig.save(configFile);

            config = YamlConfiguration.loadConfiguration(configFile);

            plugin.getLogger().info("Config migration completed successfully");

        } catch (Exception e) {
            plugin.logError("Failed to migrate config", e);
        }
    }


    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.logError("Failed to save config.yml", e);
        }
    }

    public void reload() {
        load();
    }

    public Settings getSettings() {
        return settings;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getCustomConfig(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            if (plugin.getResource(fileName) != null) {
                plugin.saveResource(fileName, false);
            } else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    plugin.logError("Failed to create " + fileName, e);
                }
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public void saveCustomConfig(FileConfiguration config, String fileName) {
        try {
            config.save(new File(plugin.getDataFolder(), fileName));
        } catch (IOException e) {
            plugin.logError("Failed to save " + fileName, e);
        }
    }
}
