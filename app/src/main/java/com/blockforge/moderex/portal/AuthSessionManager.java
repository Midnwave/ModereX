package com.blockforge.moderex.portal;

import com.blockforge.moderex.ModereX;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages authentication sessions for the player punishment portal.
 * Sessions are 10-character alphanumeric strings that expire after a configured time.
 */
public class AuthSessionManager {

    private final ModereX plugin;
    private final Map<String, PlayerAuthSession> sessionCache = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    // Characters used for session ID generation (62 chars: A-Z, a-z, 0-9)
    private static final String SESSION_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SESSION_ID_LENGTH = 10;

    // Default session expiry: 12 hours
    private long sessionExpiryMs = 12 * 60 * 60 * 1000;

    public AuthSessionManager(ModereX plugin) {
        this.plugin = plugin;
    }

    /**
     * Initialize the auth session manager.
     */
    public void initialize() {
        loadActiveSessions();
        startExpiryCleanupTask();
        plugin.getLogger().info("Player auth session manager initialized");
    }

    /**
     * Set session expiry time from config.
     */
    public void setSessionExpiryHours(int hours) {
        this.sessionExpiryMs = hours * 60L * 60L * 1000L;
    }

    /**
     * Generate a new auth session for a player.
     *
     * @param playerUuid The player's UUID
     * @param caseId Optional case ID to link to (can be null)
     * @return The generated session ID
     */
    public CompletableFuture<String> createSession(UUID playerUuid, String caseId) {
        return CompletableFuture.supplyAsync(() -> {
            String sessionId = generateUniqueSessionId();
            long now = System.currentTimeMillis();
            long expiresAt = now + sessionExpiryMs;

            PlayerAuthSession session = new PlayerAuthSession(
                    sessionId, playerUuid, caseId, now, expiresAt, null, true
            );

            // Save to database
            saveSession(session);

            // Add to cache
            sessionCache.put(sessionId, session);

            plugin.logDebug("[AuthSession] Created session " + sessionId + " for player " + playerUuid);
            return sessionId;
        });
    }

    /**
     * Generate a unique 10-character session ID.
     */
    private String generateUniqueSessionId() {
        String sessionId;
        int attempts = 0;
        do {
            sessionId = generateSessionId();
            attempts++;
            if (attempts > 100) {
                throw new RuntimeException("Failed to generate unique session ID after 100 attempts");
            }
        } while (sessionCache.containsKey(sessionId) || sessionExistsInDatabase(sessionId));
        return sessionId;
    }

    /**
     * Generate a random 10-character session ID.
     */
    private String generateSessionId() {
        StringBuilder sb = new StringBuilder(SESSION_ID_LENGTH);
        for (int i = 0; i < SESSION_ID_LENGTH; i++) {
            sb.append(SESSION_CHARS.charAt(random.nextInt(SESSION_CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * Check if a session ID exists in the database.
     */
    private boolean sessionExistsInDatabase(String sessionId) {
        try {
            return plugin.getDatabaseManager().query(
                    "SELECT 1 FROM moderex_player_auth_sessions WHERE id = ?",
                    rs -> rs.next(),
                    sessionId
            );
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Validate a session and return the session data if valid.
     *
     * @param sessionId The session ID to validate
     * @return The session if valid, null otherwise
     */
    public PlayerAuthSession validateSession(String sessionId) {
        if (sessionId == null || sessionId.length() != SESSION_ID_LENGTH) {
            return null;
        }

        // Check cache first
        PlayerAuthSession session = sessionCache.get(sessionId);
        if (session == null) {
            // Try loading from database
            session = loadSession(sessionId);
            if (session != null) {
                sessionCache.put(sessionId, session);
            }
        }

        if (session == null) {
            return null;
        }

        // Check if session is active and not expired
        if (!session.isActive() || session.isExpired()) {
            return null;
        }

        return session;
    }

    /**
     * Get a session by player UUID (most recent active session).
     */
    public PlayerAuthSession getSessionByPlayer(UUID playerUuid) {
        return sessionCache.values().stream()
                .filter(s -> s.getPlayerUuid().equals(playerUuid) && s.isActive() && !s.isExpired())
                .findFirst()
                .orElse(null);
    }

    /**
     * Invalidate (logout) a session.
     */
    public CompletableFuture<Void> invalidateSession(String sessionId) {
        return CompletableFuture.runAsync(() -> {
            PlayerAuthSession session = sessionCache.get(sessionId);
            if (session != null) {
                session.setActive(false);
                updateSession(session);
            }

            // Also update in database
            try {
                plugin.getDatabaseManager().update(
                        "UPDATE moderex_player_auth_sessions SET active = FALSE WHERE id = ?",
                        sessionId
                );
            } catch (SQLException e) {
                plugin.logError("Failed to invalidate session", e);
            }

            sessionCache.remove(sessionId);
            plugin.logDebug("[AuthSession] Invalidated session " + sessionId);
        });
    }

    /**
     * Set device hash for trusted device functionality.
     */
    public CompletableFuture<Void> setDeviceHash(String sessionId, String deviceHash) {
        return CompletableFuture.runAsync(() -> {
            PlayerAuthSession session = sessionCache.get(sessionId);
            if (session != null) {
                session.setDeviceHash(deviceHash);
                updateSession(session);
            }

            try {
                plugin.getDatabaseManager().update(
                        "UPDATE moderex_player_auth_sessions SET device_hash = ? WHERE id = ?",
                        deviceHash, sessionId
                );
            } catch (SQLException e) {
                plugin.logError("Failed to set device hash", e);
            }
        });
    }

    /**
     * Check if a device is trusted for a player.
     */
    public boolean isDeviceTrusted(UUID playerUuid, String deviceHash) {
        if (deviceHash == null || deviceHash.isEmpty()) {
            return false;
        }

        try {
            return plugin.getDatabaseManager().query(
                    "SELECT 1 FROM moderex_player_settings WHERE player_uuid = ? AND trusted_devices LIKE ?",
                    rs -> rs.next(),
                    playerUuid.toString(), "%" + deviceHash + "%"
            );
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Save session to database.
     */
    private void saveSession(PlayerAuthSession session) {
        String sql = """
            INSERT INTO moderex_player_auth_sessions
            (id, player_uuid, case_id, created_at, expires_at, device_hash, active)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try {
            plugin.getDatabaseManager().update(sql,
                    session.getId(),
                    session.getPlayerUuid().toString(),
                    session.getCaseId(),
                    session.getCreatedAt(),
                    session.getExpiresAt(),
                    session.getDeviceHash(),
                    session.isActive()
            );
        } catch (SQLException e) {
            plugin.logError("Failed to save auth session", e);
        }
    }

    /**
     * Update session in database.
     */
    private void updateSession(PlayerAuthSession session) {
        String sql = """
            UPDATE moderex_player_auth_sessions
            SET device_hash = ?, active = ?
            WHERE id = ?
            """;

        try {
            plugin.getDatabaseManager().update(sql,
                    session.getDeviceHash(),
                    session.isActive(),
                    session.getId()
            );
        } catch (SQLException e) {
            plugin.logError("Failed to update auth session", e);
        }
    }

    /**
     * Load session from database.
     */
    private PlayerAuthSession loadSession(String sessionId) {
        try {
            return plugin.getDatabaseManager().query(
                    "SELECT * FROM moderex_player_auth_sessions WHERE id = ?",
                    rs -> {
                        if (rs.next()) {
                            return new PlayerAuthSession(
                                    rs.getString("id"),
                                    UUID.fromString(rs.getString("player_uuid")),
                                    rs.getString("case_id"),
                                    rs.getLong("created_at"),
                                    rs.getLong("expires_at"),
                                    rs.getString("device_hash"),
                                    rs.getBoolean("active")
                            );
                        }
                        return null;
                    },
                    sessionId
            );
        } catch (SQLException e) {
            plugin.logError("Failed to load auth session", e);
            return null;
        }
    }

    /**
     * Load all active sessions into cache.
     */
    private void loadActiveSessions() {
        CompletableFuture.runAsync(() -> {
            try {
                long now = System.currentTimeMillis();
                plugin.getDatabaseManager().query(
                        "SELECT * FROM moderex_player_auth_sessions WHERE active = TRUE AND expires_at > ?",
                        rs -> {
                            int count = 0;
                            while (rs.next()) {
                                PlayerAuthSession session = new PlayerAuthSession(
                                        rs.getString("id"),
                                        UUID.fromString(rs.getString("player_uuid")),
                                        rs.getString("case_id"),
                                        rs.getLong("created_at"),
                                        rs.getLong("expires_at"),
                                        rs.getString("device_hash"),
                                        rs.getBoolean("active")
                                );
                                sessionCache.put(session.getId(), session);
                                count++;
                            }
                            plugin.logDebug("[AuthSession] Loaded " + count + " active sessions");
                            return null;
                        },
                        now
                );
            } catch (SQLException e) {
                plugin.logError("Failed to load active sessions", e);
            }
        });
    }

    /**
     * Start background task to clean up expired sessions.
     */
    private void startExpiryCleanupTask() {
        // Run every hour
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            long now = System.currentTimeMillis();

            // Remove from cache
            sessionCache.entrySet().removeIf(entry ->
                    entry.getValue().isExpired() || !entry.getValue().isActive()
            );

            // Mark expired sessions as inactive in database
            try {
                plugin.getDatabaseManager().update(
                        "UPDATE moderex_player_auth_sessions SET active = FALSE WHERE expires_at < ?",
                        now
                );
            } catch (SQLException e) {
                plugin.logError("Failed to cleanup expired sessions", e);
            }

            plugin.logDebug("[AuthSession] Cleanup completed, " + sessionCache.size() + " active sessions");
        }, 20L * 60 * 60, 20L * 60 * 60); // Every hour
    }

    /**
     * Shutdown and cleanup.
     */
    public void shutdown() {
        sessionCache.clear();
    }

    /**
     * Get session count.
     */
    public int getSessionCount() {
        return sessionCache.size();
    }
}
