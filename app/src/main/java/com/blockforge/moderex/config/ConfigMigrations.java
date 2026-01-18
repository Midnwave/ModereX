package com.blockforge.moderex.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry of all configuration migrations.
 * Add new migrations here when making breaking config changes.
 */
public class ConfigMigrations {

    private static final Map<Integer, ConfigMigration> migrations = new HashMap<>();

    static {
        // Example migrations:

        // Version 1 -> 2: No breaking changes
        // (Migrations are only needed for breaking changes)

        // Version 6 -> 7 ( no pun intended ): Example migration
        // migrations.put(7, new ConfigMigration(6, 7)
        //     .rename("old.key.path", "new.key.path")
        //     .delete("deprecated.setting")
        //     .transform(config -> {
        //         if (config.contains("some.value")) {
        //             String oldValue = config.getString("some.value");
        //             config.set("some.value", oldValue.toUpperCase());
        //         }
        //     })
        // );
    }

    /**
     * Get migration for a specific target version.
     *
     * @param toVersion Target version
     * @return Migration, or null if no migration needed
     */
    public static ConfigMigration getMigration(int toVersion) {
        return migrations.get(toVersion);
    }

    /**
     * Check if a migration exists for the target version.
     *
     * @param toVersion Target version
     * @return True if migration exists
     */
    public static boolean hasMigration(int toVersion) {
        return migrations.containsKey(toVersion);
    }

    /**
     * Register a new migration.
     *
     * @param migration Migration to register
     */
    public static void registerMigration(ConfigMigration migration) {
        migrations.put(migration.getToVersion(), migration);
    }
}
