package com.blockforge.moderex.identity;

import com.blockforge.moderex.ModereX;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Properties;

/**
 * Manages the persistent unique server identity.
 *
 * The server ID is a unique identifier in UUID-style format (XXXXX-XXXXX-XXXXX-XXXXX-XXXXX) that:
 * - Is generated once on first plugin startup
 * - Persists across plugin updates, JAR replacements, and config resets
 * - Is stored in multiple locations for redundancy
 * - Is used for gateway routing and server identification
 * - Uses safe characters only (no 0/O, 1/l/I confusion)
 */
public class ServerIdentity {

    private static final String ID_FILE_NAME = ".moderex-server-id";
    private static final String ID_TABLE = "moderex_server_identity";

    // UUID-style format: 5 groups of 5 characters
    private static final int GROUP_SIZE = 5;
    private static final int GROUP_COUNT = 5;
    private static final int TOTAL_LENGTH = GROUP_SIZE * GROUP_COUNT + (GROUP_COUNT - 1); // 25 chars + 4 dashes = 29

    // Safe characters: A-Z (no O, I) + 2-9 (no 0, 1) = 32 characters
    private static final String ID_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final ModereX plugin;
    private String serverId;
    private String serverName;
    private long createdAt;

    // The minimum URL prefix (number of groups) needed for uniqueness
    // This is determined by the gateway and starts at 1
    private int urlPrefixGroups = 1;

    // Premium status (validated by gateway)
    private boolean premium = false;
    private long premiumExpiresAt = 0;
    private long premiumCachedUntil = 0;

    // Premium cache duration: 24 hours
    private static final long PREMIUM_CACHE_DURATION = 24 * 60 * 60 * 1000;

    public ServerIdentity(ModereX plugin) {
        this.plugin = plugin;
    }

    /**
     * Initialize the server identity.
     * Loads from file/database or generates a new one if none exists.
     */
    public void initialize() {
        // Try to load from multiple sources in order of priority
        serverId = loadFromFile();

        if (serverId == null) {
            serverId = loadFromDatabase();
        }

        if (serverId == null) {
            // Generate new ID
            serverId = generateNewId();
            createdAt = System.currentTimeMillis();
            plugin.getLogger().info("[Identity] Generated new server ID: " + serverId);
        }

        // Ensure ID is saved to all locations for redundancy
        saveToFile();
        saveToDatabase();

        // Load or set server name
        serverName = plugin.getConfigManager().getSettings().getServerName();
        if (serverName == null || serverName.isEmpty()) {
            serverName = "Server-" + getFirstGroup();
        }

        plugin.getLogger().info("[Identity] Server ID: " + serverId);
    }

    /**
     * Load server ID from the hidden file in plugin data folder.
     */
    private String loadFromFile() {
        // Primary location: plugin data folder
        Path primaryPath = plugin.getDataFolder().toPath().resolve(ID_FILE_NAME);
        String id = readIdFromFile(primaryPath);
        if (id != null) {
            plugin.logDebug("[Identity] Loaded server ID from data folder: " + id);
            return id;
        }

        // Fallback: parent of plugin folder (survives plugin folder deletion)
        Path parent = plugin.getDataFolder().toPath().getParent();
        if (parent != null) {
            Path fallbackPath = parent.resolve(ID_FILE_NAME);
            id = readIdFromFile(fallbackPath);
            if (id != null) {
                plugin.logDebug("[Identity] Loaded server ID from plugins folder: " + id);
                return id;
            }

            // Secondary fallback: server root folder
            Path grandparent = parent.getParent();
            if (grandparent != null) {
                Path serverRootPath = grandparent.resolve(ID_FILE_NAME);
                id = readIdFromFile(serverRootPath);
                if (id != null) {
                    plugin.logDebug("[Identity] Loaded server ID from server root: " + id);
                    return id;
                }
            }
        }

        return null;
    }

    /**
     * Read ID from a specific file path.
     */
    private String readIdFromFile(Path path) {
        if (!Files.exists(path)) {
            return null;
        }

        try {
            Properties props = new Properties();
            try (InputStream in = Files.newInputStream(path)) {
                props.load(in);
            }

            String id = props.getProperty("server_id");
            if (id != null && isValidId(id)) {
                String created = props.getProperty("created_at");
                if (created != null) {
                    try {
                        createdAt = Long.parseLong(created);
                    } catch (NumberFormatException ignored) {}
                }

                // Load URL prefix groups if saved
                String prefix = props.getProperty("url_prefix_groups");
                if (prefix != null) {
                    try {
                        urlPrefixGroups = Integer.parseInt(prefix);
                    } catch (NumberFormatException ignored) {}
                }

                // Load premium status if saved
                String premiumStr = props.getProperty("premium");
                if (premiumStr != null) {
                    premium = Boolean.parseBoolean(premiumStr);
                }
                String expiresStr = props.getProperty("premium_expires_at");
                if (expiresStr != null) {
                    try {
                        premiumExpiresAt = Long.parseLong(expiresStr);
                    } catch (NumberFormatException ignored) {}
                }
                String cachedStr = props.getProperty("premium_cached_until");
                if (cachedStr != null) {
                    try {
                        premiumCachedUntil = Long.parseLong(cachedStr);
                    } catch (NumberFormatException ignored) {}
                }

                return id;
            }
        } catch (IOException e) {
            plugin.logDebug("[Identity] Failed to read ID from " + path + ": " + e.getMessage());
        }

        return null;
    }

    /**
     * Load server ID from database.
     */
    private String loadFromDatabase() {
        try {
            // Ensure table exists
            plugin.getDatabaseManager().update("""
                CREATE TABLE IF NOT EXISTS %s (
                    key VARCHAR(64) PRIMARY KEY,
                    value TEXT NOT NULL,
                    updated_at BIGINT NOT NULL
                )
                """.formatted(ID_TABLE));

            return plugin.getDatabaseManager().query(
                "SELECT value FROM " + ID_TABLE + " WHERE key = 'server_id'",
                rs -> {
                    if (rs.next()) {
                        String id = rs.getString("value");
                        if (isValidId(id)) {
                            plugin.logDebug("[Identity] Loaded server ID from database: " + id);
                            return id;
                        }
                    }
                    return null;
                }
            );
        } catch (Exception e) {
            plugin.logDebug("[Identity] Failed to load ID from database: " + e.getMessage());
            return null;
        }
    }

    /**
     * Save server ID to files (multiple locations for redundancy).
     */
    private void saveToFile() {
        // Primary location: plugins/ModereX/.moderex-server-id
        Path primaryPath = plugin.getDataFolder().toPath().resolve(ID_FILE_NAME);
        writeIdToFile(primaryPath);

        // Backup 1: plugins/.moderex-server-id (survives ModereX folder deletion)
        Path parent = plugin.getDataFolder().toPath().getParent();
        if (parent != null) {
            Path backupPath = parent.resolve(ID_FILE_NAME);
            writeIdToFile(backupPath);

            // Backup 2: server root/.moderex-server-id (survives plugins folder deletion)
            Path grandparent = parent.getParent();
            if (grandparent != null) {
                Path serverRootPath = grandparent.resolve(ID_FILE_NAME);
                writeIdToFile(serverRootPath);
            }
        }
    }

    /**
     * Write ID to a specific file path.
     */
    private void writeIdToFile(Path path) {
        try {
            // Ensure parent directory exists
            Files.createDirectories(path.getParent());

            Properties props = new Properties();
            props.setProperty("server_id", serverId);
            props.setProperty("created_at", String.valueOf(createdAt));
            props.setProperty("plugin_version", plugin.getDescription().getVersion());
            props.setProperty("url_prefix_groups", String.valueOf(urlPrefixGroups));
            props.setProperty("premium", String.valueOf(premium));
            props.setProperty("premium_expires_at", String.valueOf(premiumExpiresAt));
            props.setProperty("premium_cached_until", String.valueOf(premiumCachedUntil));

            try (OutputStream out = Files.newOutputStream(path)) {
                props.store(out, "ModereX Server Identity - DO NOT DELETE OR MODIFY");
            }

            // Try to hide the file on Windows
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                try {
                    Files.setAttribute(path, "dos:hidden", true);
                } catch (Exception ignored) {}
            }

            plugin.logDebug("[Identity] Saved server ID to: " + path);
        } catch (IOException e) {
            plugin.logDebug("[Identity] Failed to save ID to " + path + ": " + e.getMessage());
        }
    }

    /**
     * Save server ID to database.
     */
    private void saveToDatabase() {
        try {
            long now = System.currentTimeMillis();

            plugin.getDatabaseManager().update("""
                INSERT OR REPLACE INTO %s (key, value, updated_at)
                VALUES ('server_id', ?, ?)
                """.formatted(ID_TABLE), serverId, now);

            plugin.getDatabaseManager().update("""
                INSERT OR REPLACE INTO %s (key, value, updated_at)
                VALUES ('created_at', ?, ?)
                """.formatted(ID_TABLE), String.valueOf(createdAt), now);

            plugin.getDatabaseManager().update("""
                INSERT OR REPLACE INTO %s (key, value, updated_at)
                VALUES ('url_prefix_groups', ?, ?)
                """.formatted(ID_TABLE), String.valueOf(urlPrefixGroups), now);

            plugin.logDebug("[Identity] Saved server ID to database");
        } catch (Exception e) {
            plugin.logDebug("[Identity] Failed to save ID to database: " + e.getMessage());
        }
    }

    /**
     * Generate a new unique server ID in UUID-style format.
     * Format: XXXXX-XXXXX-XXXXX-XXXXX-XXXXX (5 groups of 5 characters)
     */
    private String generateNewId() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(TOTAL_LENGTH);

        for (int group = 0; group < GROUP_COUNT; group++) {
            if (group > 0) {
                sb.append('-');
            }
            for (int i = 0; i < GROUP_SIZE; i++) {
                sb.append(ID_CHARS.charAt(random.nextInt(ID_CHARS.length())));
            }
        }

        return sb.toString();
    }

    /**
     * Validate that an ID is in the correct format.
     * Must be: XXXXX-XXXXX-XXXXX-XXXXX-XXXXX (5 groups of 5 safe characters)
     */
    private boolean isValidId(String id) {
        if (id == null || id.length() != TOTAL_LENGTH) {
            return false;
        }

        String[] groups = id.split("-");
        if (groups.length != GROUP_COUNT) {
            return false;
        }

        for (String group : groups) {
            if (group.length() != GROUP_SIZE) {
                return false;
            }
            for (char c : group.toCharArray()) {
                if (ID_CHARS.indexOf(c) == -1) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Get the unique server ID.
     * Format: XXXXX-XXXXX-XXXXX-XXXXX-XXXXX
     */
    public String getServerId() {
        return serverId;
    }

    /**
     * Get the first group of the server ID (first 5 chars before first dash).
     */
    public String getFirstGroup() {
        if (serverId == null) return "?????";
        int dashIdx = serverId.indexOf('-');
        return dashIdx > 0 ? serverId.substring(0, dashIdx) : serverId.substring(0, Math.min(5, serverId.length()));
    }

    /**
     * Get the URL prefix for this server (used in panel.moderex.net/{prefix}/).
     * Starts with just first group, expands if needed for uniqueness.
     * Always lowercase for URL consistency.
     */
    public String getUrlPrefix() {
        if (serverId == null) return "?????";

        String[] groups = serverId.split("-");
        StringBuilder prefix = new StringBuilder();

        for (int i = 0; i < Math.min(urlPrefixGroups, groups.length); i++) {
            if (i > 0) prefix.append("-");
            prefix.append(groups[i].toLowerCase());
        }

        return prefix.toString();
    }

    /**
     * Set the number of groups needed for unique URL prefix.
     * Called by gateway when it determines uniqueness.
     */
    public void setUrlPrefixGroups(int groups) {
        if (groups >= 1 && groups <= GROUP_COUNT) {
            this.urlPrefixGroups = groups;
            // Persist the change
            saveToFile();
            saveToDatabase();
        }
    }

    /**
     * Get the number of groups in the URL prefix.
     */
    public int getUrlPrefixGroups() {
        return urlPrefixGroups;
    }

    /**
     * Get the server display name.
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Get when this server ID was first created.
     */
    public long getCreatedAt() {
        return createdAt;
    }

    /**
     * Get the panel URL for this server.
     * Uses the progressive prefix (starts short, expands if needed).
     *
     * @param baseDomain Base domain like "moderex.net" or "moderex.pages.dev"
     */
    public String getPanelUrl(String baseDomain) {
        return "https://panel." + baseDomain + "/" + getUrlPrefix() + "/";
    }

    /**
     * Get the full panel URL using the complete server ID (for fallback/direct reference).
     */
    public String getFullPanelUrl(String baseDomain) {
        return "https://panel." + baseDomain + "/" + serverId.toLowerCase() + "/";
    }

    /**
     * Check if a given URL prefix matches this server.
     * Handles both short prefixes and full IDs.
     */
    public boolean matchesPrefix(String prefix) {
        if (prefix == null || serverId == null) return false;

        // Normalize to lowercase
        String normalizedPrefix = prefix.toLowerCase().replace("/", "");
        String normalizedId = serverId.toLowerCase();

        // Check if the prefix matches the beginning of our ID
        return normalizedId.startsWith(normalizedPrefix);
    }

    // ==================== Premium Status ====================

    /**
     * Check if this server is on a premium plan.
     * Premium status is validated by the gateway and cached for 24 hours
     * to allow offline operation if the gateway becomes unavailable.
     *
     * @return true if the server is premium and the status hasn't expired
     */
    public boolean isPremium() {
        if (!premium) {
            return false;
        }

        long now = System.currentTimeMillis();

        // Check if premium subscription expired
        if (premiumExpiresAt > 0 && now > premiumExpiresAt) {
            plugin.logDebug("[Identity] Premium subscription expired");
            premium = false;
            return false;
        }

        // Check if cache is still valid (if gateway went offline)
        if (premiumCachedUntil > 0 && now > premiumCachedUntil) {
            plugin.logDebug("[Identity] Premium cache expired (gateway offline?)");
            premium = false;
            return false;
        }

        return true;
    }

    /**
     * Set premium status. Called by GatewayClient when receiving registration response.
     *
     * @param premium Whether the server has premium status
     * @param expiresAt Timestamp when premium expires (0 = never, -1 = lifetime)
     */
    public void setPremium(boolean premium, long expiresAt) {
        this.premium = premium;
        this.premiumExpiresAt = (expiresAt == -1) ? 0 : expiresAt; // -1 means lifetime (never expires)
        this.premiumCachedUntil = System.currentTimeMillis() + PREMIUM_CACHE_DURATION;

        if (premium) {
            if (expiresAt == -1 || expiresAt == 0) {
                plugin.getLogger().info("[Identity] Premium status: LIFETIME");
            } else {
                long daysLeft = (expiresAt - System.currentTimeMillis()) / (24 * 60 * 60 * 1000);
                plugin.getLogger().info("[Identity] Premium status: ACTIVE (" + daysLeft + " days remaining)");
            }
        }

        saveToFile();
    }

    /**
     * Get when premium subscription expires.
     * @return Timestamp, 0 for lifetime, -1 if not premium
     */
    public long getPremiumExpiresAt() {
        if (!premium) return -1;
        return premiumExpiresAt;
    }
}
