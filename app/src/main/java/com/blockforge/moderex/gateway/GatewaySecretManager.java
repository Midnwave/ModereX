package com.blockforge.moderex.gateway;

import com.blockforge.moderex.ModereX;

import java.io.*;
import java.nio.file.*;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Properties;

/**
 * Manages the gateway authentication secret in a dedicated file with backups.
 *
 * Storage layout:
 *   plugins/ModereX/.gateway-secret          (primary)
 *   plugins/ModereX/data/.gateway-secret.bak  (backup 1)
 *   plugins/ModereX/data/.gateway-secret.bak2 (backup 2)
 *
 * Recovery logic:
 *   1. If primary exists → use it, sync backups if needed
 *   2. If primary missing but backup exists → restore from backup
 *   3. If ALL copies missing → generate fresh secret (intentional reset)
 */
public class GatewaySecretManager {

    private static final String SECRET_FILE = ".gateway-secret";
    private static final String BACKUP_FILE = ".gateway-secret.bak";
    private static final String BACKUP2_FILE = ".gateway-secret.bak2";
    private static final int SECRET_LENGTH = 64;
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private final ModereX plugin;
    private final Path primaryPath;
    private final Path backup1Path;
    private final Path backup2Path;
    private String secret;

    public GatewaySecretManager(ModereX plugin) {
        this.plugin = plugin;
        Path pluginDir = plugin.getDataFolder().toPath();
        Path dataDir = pluginDir.resolve("data");

        this.primaryPath = pluginDir.resolve(SECRET_FILE);
        this.backup1Path = dataDir.resolve(BACKUP_FILE);
        this.backup2Path = dataDir.resolve(BACKUP2_FILE);
    }

    /**
     * Load or generate the gateway secret.
     * Call this during plugin startup.
     */
    public void initialize() {
        // Ensure data directory exists
        try {
            Files.createDirectories(backup1Path.getParent());
        } catch (IOException e) {
            plugin.getLogger().warning("[Gateway] Failed to create data directory: " + e.getMessage());
        }

        // Try to load from primary
        secret = readSecret(primaryPath);

        if (secret != null) {
            // Primary exists, sync backups
            syncBackups();
            plugin.getLogger().info("[Gateway] Secret loaded from primary file");
            return;
        }

        // Primary missing - try backup 1
        secret = readSecret(backup1Path);
        if (secret != null) {
            plugin.getLogger().info("[Gateway] Secret restored from backup 1");
            writeSecret(primaryPath, secret);
            syncBackups();
            return;
        }

        // Backup 1 missing - try backup 2
        secret = readSecret(backup2Path);
        if (secret != null) {
            plugin.getLogger().info("[Gateway] Secret restored from backup 2");
            writeSecret(primaryPath, secret);
            syncBackups();
            return;
        }

        // ALL copies missing - generate fresh secret
        secret = generateSecret();
        writeSecret(primaryPath, secret);
        syncBackups();
        plugin.getLogger().info("[Gateway] Generated new gateway secret (all copies were missing)");

        // Also migrate: remove old secret from config.yml if present
        migrateFromConfig();
    }

    /**
     * Get the current gateway secret.
     */
    public String getSecret() {
        return secret;
    }

    /**
     * Read a secret from a properties file.
     */
    private String readSecret(Path path) {
        if (!Files.exists(path)) return null;
        try {
            Properties props = new Properties();
            try (InputStream in = Files.newInputStream(path)) {
                props.load(in);
            }
            String value = props.getProperty("secret");
            if (value != null && !value.isEmpty()) {
                return value;
            }
        } catch (IOException e) {
            plugin.getLogger().warning("[Gateway] Failed to read secret from " + path.getFileName() + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Write a secret to a properties file.
     */
    private void writeSecret(Path path, String secretValue) {
        try {
            Files.createDirectories(path.getParent());
            Properties props = new Properties();
            props.setProperty("secret", secretValue);
            props.setProperty("generated", Instant.now().toString());
            try (OutputStream out = Files.newOutputStream(path)) {
                props.store(out, "ModereX Gateway Secret - DO NOT DELETE\nThis file authenticates your server with the ModereX gateway.\nDeleting this file will require re-registration.");
            }
        } catch (IOException e) {
            plugin.getLogger().warning("[Gateway] Failed to write secret to " + path.getFileName() + ": " + e.getMessage());
        }
    }

    /**
     * Sync backups with the current secret.
     */
    private void syncBackups() {
        if (secret == null) return;

        // Check and update backup 1
        String bak1 = readSecret(backup1Path);
        if (!secret.equals(bak1)) {
            writeSecret(backup1Path, secret);
        }

        // Check and update backup 2
        String bak2 = readSecret(backup2Path);
        if (!secret.equals(bak2)) {
            writeSecret(backup2Path, secret);
        }
    }

    /**
     * Generate a cryptographically secure random secret.
     */
    private String generateSecret() {
        SecureRandom sr = new SecureRandom();
        StringBuilder sb = new StringBuilder(SECRET_LENGTH);
        for (int i = 0; i < SECRET_LENGTH; i++) {
            sb.append(CHARS.charAt(sr.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * Migrate old secret from config.yml to the file system.
     * Removes the gateway.secret key from config.yml after migration.
     */
    private void migrateFromConfig() {
        try {
            String configSecret = plugin.getConfig().getString("gateway.secret", "");
            if (configSecret != null && !configSecret.isEmpty()) {
                // Use the config secret instead of the newly generated one
                secret = configSecret;
                writeSecret(primaryPath, secret);
                syncBackups();

                // Remove from config.yml
                plugin.getConfig().set("gateway.secret", null);
                plugin.saveConfig();
                plugin.getLogger().info("[Gateway] Migrated secret from config.yml to .gateway-secret file");
            }
        } catch (Exception e) {
            plugin.getLogger().warning("[Gateway] Failed to migrate secret from config: " + e.getMessage());
        }
    }
}
