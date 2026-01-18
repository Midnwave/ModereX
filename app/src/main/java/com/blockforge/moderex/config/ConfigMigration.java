package com.blockforge.moderex.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

/**
 * Defines configuration migrations for a specific version upgrade.
 * Migrations handle renames, deletions, and custom transformations.
 */
public class ConfigMigration {

    private final int fromVersion;
    private final int toVersion;
    private final Map<String, String> renames = new HashMap<>();
    private final Set<String> deletions = new HashSet<>();
    private final List<Transformation> transformations = new ArrayList<>();

    public ConfigMigration(int fromVersion, int toVersion) {
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
    }

    /**
     * Add a key rename migration.
     *
     * @param oldPath Old config path
     * @param newPath New config path
     * @return This migration for chaining
     */
    public ConfigMigration rename(String oldPath, String newPath) {
        renames.put(oldPath, newPath);
        return this;
    }

    /**
     * Add a key deletion migration.
     *
     * @param path Config path to delete
     * @return This migration for chaining
     */
    public ConfigMigration delete(String path) {
        deletions.add(path);
        return this;
    }

    /**
     * Add a custom transformation.
     *
     * @param transformation Custom transformation logic
     * @return This migration for chaining
     */
    public ConfigMigration transform(Transformation transformation) {
        transformations.add(transformation);
        return this;
    }

    public int getFromVersion() {
        return fromVersion;
    }

    public int getToVersion() {
        return toVersion;
    }

    public Map<String, String> getRenames() {
        return renames;
    }

    public Set<String> getDeletions() {
        return deletions;
    }

    public List<Transformation> getTransformations() {
        return transformations;
    }

    /**
     * Interface for custom configuration transformations.
     */
    @FunctionalInterface
    public interface Transformation {
        /**
         * Apply transformation to config.
         *
         * @param config Configuration to transform
         */
        void apply(FileConfiguration config);
    }
}
