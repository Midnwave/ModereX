package com.blockforge.moderex.config;

import com.blockforge.moderex.ModereX;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Handles merging old configuration values into new configuration files.
 * Preserves user settings when updating configs while adding new keys.
 */
public class ConfigMerger {

    private final ModereX plugin;
    private final Set<String> warnings = new HashSet<>();

    public ConfigMerger(ModereX plugin) {
        this.plugin = plugin;
    }

    /**
     * Merge old config values into new config.
     *
     * @param oldConfig Old configuration
     * @param newConfig New configuration (from resources)
     * @param migrations Migrations to apply (renames, deletions)
     * @return Merged configuration
     */
    public FileConfiguration merge(FileConfiguration oldConfig, FileConfiguration newConfig, ConfigMigration migrations) {
        warnings.clear();

        // Apply migrations first (renames, deletions)
        if (migrations != null) {
            applyMigrations(oldConfig, migrations);
        }

        // Copy all old values that still exist in new config
        mergeSection(oldConfig, newConfig, newConfig, "");

        // Log any warnings
        if (!warnings.isEmpty()) {
            plugin.getLogger().warning("=".repeat(60));
            plugin.getLogger().warning("CONFIG MIGRATION WARNINGS:");
            for (String warning : warnings) {
                plugin.getLogger().warning("  - " + warning);
            }
            plugin.getLogger().warning("Your old config has been saved to config.yml.old");
            plugin.getLogger().warning("=".repeat(60));
        }

        return newConfig;
    }

    /**
     * Recursively merge configuration sections.
     */
    private void mergeSection(ConfigurationSection oldSection, ConfigurationSection newSection,
                               FileConfiguration targetConfig, String path) {
        for (String key : oldSection.getKeys(false)) {
            String fullPath = path.isEmpty() ? key : path + "." + key;

            // Check if key exists in new config
            if (!newSection.contains(key)) {
                // Key no longer exists - warn user
                warnings.add("Removed key: " + fullPath + " = " + oldSection.get(key));
                continue;
            }

            Object oldValue = oldSection.get(key);
            Object newValue = newSection.get(key);

            // Handle sections recursively
            if (oldValue instanceof ConfigurationSection && newValue instanceof ConfigurationSection) {
                mergeSection((ConfigurationSection) oldValue, (ConfigurationSection) newValue, targetConfig, fullPath);
            }
            // Handle value type mismatches
            else if (oldValue != null && newValue != null &&
                     !oldValue.getClass().equals(newValue.getClass())) {
                warnings.add("Type mismatch for " + fullPath + ": old=" + oldValue.getClass().getSimpleName() +
                           ", new=" + newValue.getClass().getSimpleName() + " (using new default)");
            }
            // Copy old value
            else {
                targetConfig.set(fullPath, oldValue);
            }
        }
    }

    /**
     * Apply configuration migrations (renames, deletions, transformations).
     */
    private void applyMigrations(FileConfiguration config, ConfigMigration migrations) {
        // Apply renames
        for (Map.Entry<String, String> rename : migrations.getRenames().entrySet()) {
            String oldPath = rename.getKey();
            String newPath = rename.getValue();

            if (config.contains(oldPath)) {
                Object value = config.get(oldPath);
                config.set(newPath, value);
                config.set(oldPath, null);
                plugin.logDebug("[ConfigMerger] Renamed: " + oldPath + " -> " + newPath);
            }
        }

        // Apply deletions
        for (String deletePath : migrations.getDeletions()) {
            if (config.contains(deletePath)) {
                config.set(deletePath, null);
                plugin.logDebug("[ConfigMerger] Deleted: " + deletePath);
            }
        }

        // Apply custom transformations
        for (ConfigMigration.Transformation transformation : migrations.getTransformations()) {
            try {
                transformation.apply(config);
            } catch (Exception e) {
                plugin.logError("Failed to apply transformation", e);
            }
        }
    }

    /**
     * Save backup of old configuration.
     */
    public void backupOldConfig(File configFile) throws IOException {
        File backupFile = new File(configFile.getParent(), "config.yml.old");

        // Read old config
        FileConfiguration oldConfig = YamlConfiguration.loadConfiguration(configFile);

        // Save to backup
        oldConfig.save(backupFile);

        plugin.getLogger().info("Old configuration backed up to config.yml.old");
    }

    /**
     * Get warnings generated during merge.
     */
    public Set<String> getWarnings() {
        return new HashSet<>(warnings);
    }
}
