package com.blockforge.moderex.web;

import com.blockforge.moderex.ModereX;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WebAuthManager {

    private final ModereX plugin;

    // Temporary sessions (URL auth tokens)
    private final Map<String, TempSession> tempSessions = new ConcurrentHashMap<>();

    // Permanent tokens: SHA256(token) -> playerUUID
    private final Map<String, String> permanentTokenHashes = new ConcurrentHashMap<>();

    // Active authenticated sessions: sessionId -> AuthenticatedSession
    private final Map<String, AuthenticatedSession> activeSessions = new ConcurrentHashMap<>();

    // Rate limiting: IP -> AttemptTracker
    private final Map<String, AttemptTracker> loginAttempts = new ConcurrentHashMap<>();

    // Trusted devices: SHA256(fingerprint) -> TrustedDevice
    private final Map<String, TrustedDevice> trustedDevices = new ConcurrentHashMap<>();

    // Token file path
    private final Path tokensFile;

    // Trusted devices file path
    private final Path trustedDevicesFile;

    // Constants
    private static final int PERMANENT_TOKEN_LENGTH = 50;
    private static final int TEMP_TOKEN_LENGTH = 32;
    private static final int SESSION_TOKEN_LENGTH = 64;
    private static final String TOKEN_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final long TEMP_SESSION_TIMEOUT_MS = 30 * 60 * 1000; // 30 minutes
    private static final long SESSION_TIMEOUT_MS = 30 * 60 * 1000; // 30 minutes inactivity
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MS = 15 * 60 * 1000; // 15 minutes

    private final SecureRandom secureRandom = new SecureRandom();

    public WebAuthManager(ModereX plugin) {
        this.plugin = plugin;
        // Store in hidden .data subfolder
        Path dataDir = plugin.getDataFolder().toPath().resolve(".data");
        this.tokensFile = dataDir.resolve("tokens");
        this.trustedDevicesFile = dataDir.resolve("devices");
    }

    public void initialize() {
        // Create hidden .data directory
        try {
            Path dataDir = tokensFile.getParent();
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            // Set directory as hidden on Windows
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                try {
                    Files.setAttribute(dataDir, "dos:hidden", true);
                } catch (Exception ignored) {}
            }
        } catch (IOException e) {
            plugin.logError("Failed to create auth data directory", e);
        }

        loadPermanentTokens();
        loadTrustedDevices();
        startCleanupTask();
        plugin.getLogger().info("Web authentication manager initialized.");
    }

    public void shutdown() {
        savePermanentTokens();
        saveTrustedDevices();
        tempSessions.clear();
        activeSessions.clear();
        trustedDevices.clear();
    }

    // ==================== PERMANENT TOKENS ====================

    public String generatePermanentToken(UUID playerUuid) {
        // Remove any existing token for this player
        removeExistingPermanentToken(playerUuid);

        // Generate secure token with dashes for readability
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < PERMANENT_TOKEN_LENGTH; i++) {
            if (i > 0 && i % 10 == 0) {
                token.append("-");
            }
            token.append(TOKEN_CHARS.charAt(secureRandom.nextInt(TOKEN_CHARS.length())));
        }

        String tokenStr = token.toString();
        String hash = hashSHA256(tokenStr);

        // Store hash -> uuid mapping
        permanentTokenHashes.put(hash, playerUuid.toString());
        savePermanentTokens();

        plugin.logDebug("Generated permanent token for " + playerUuid);
        return tokenStr;
    }

    public boolean hasPermanentToken(UUID playerUuid) {
        String uuidStr = playerUuid.toString();
        return permanentTokenHashes.containsValue(uuidStr);
    }

    public UUID validatePermanentToken(String token, String clientIp) {
        // Check rate limiting
        if (isRateLimited(clientIp)) {
            return null;
        }

        String hash = hashSHA256(token);
        String uuidStr = permanentTokenHashes.get(hash);

        if (uuidStr != null) {
            // Reset failed attempts on success
            loginAttempts.remove(clientIp);
            return UUID.fromString(uuidStr);
        } else {
            // Record failed attempt
            recordFailedAttempt(clientIp);
            return null;
        }
    }

    public boolean revokePermanentToken(UUID playerUuid) {
        return removeExistingPermanentToken(playerUuid);
    }

    private boolean removeExistingPermanentToken(UUID playerUuid) {
        String uuidStr = playerUuid.toString();
        String hashToRemove = null;

        for (Map.Entry<String, String> entry : permanentTokenHashes.entrySet()) {
            if (entry.getValue().equals(uuidStr)) {
                hashToRemove = entry.getKey();
                break;
            }
        }

        if (hashToRemove != null) {
            permanentTokenHashes.remove(hashToRemove);
            savePermanentTokens();
            return true;
        }
        return false;
    }

    // ==================== TEMPORARY URL TOKENS ====================

    public String generateTempToken(UUID playerUuid, String playerName) {
        // Invalidate any existing temp token for this player
        tempSessions.entrySet().removeIf(e -> e.getValue().playerUuid.equals(playerUuid));

        String token = generateSecureToken(TEMP_TOKEN_LENGTH);

        TempSession session = new TempSession(playerUuid, playerName, System.currentTimeMillis());
        tempSessions.put(token, session);

        plugin.logDebug("Generated temp URL token for " + playerName);
        return token;
    }

    public TempSession validateTempToken(String token) {
        TempSession session = tempSessions.get(token);

        if (session == null) {
            return null;
        }

        // Check if expired
        if (System.currentTimeMillis() - session.lastActivity > TEMP_SESSION_TIMEOUT_MS) {
            tempSessions.remove(token);
            return null;
        }

        // Update last activity
        session.lastActivity = System.currentTimeMillis();
        return session;
    }

    public AuthenticatedSession consumeTempToken(String token) {
        TempSession temp = validateTempToken(token);
        if (temp == null) {
            return null;
        }

        // Remove temp token
        tempSessions.remove(token);

        // Create authenticated session
        return createSession(temp.playerUuid, temp.playerName);
    }

    // ==================== AUTHENTICATED SESSIONS ====================

    public AuthenticatedSession createSession(UUID playerUuid, String playerName) {
        String sessionId = generateSecureToken(SESSION_TOKEN_LENGTH);

        AuthenticatedSession session = new AuthenticatedSession(
                sessionId, playerUuid, playerName, System.currentTimeMillis()
        );

        activeSessions.put(sessionId, session);
        plugin.logDebug("Created web session for " + playerName);
        return session;
    }

    public AuthenticatedSession validateSession(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return null;
        }

        AuthenticatedSession session = activeSessions.get(sessionId);

        if (session == null) {
            return null;
        }

        // Check if expired due to inactivity
        if (System.currentTimeMillis() - session.lastActivity > SESSION_TIMEOUT_MS) {
            activeSessions.remove(sessionId);
            return null;
        }

        // Refresh last activity
        session.lastActivity = System.currentTimeMillis();
        return session;
    }

    public void invalidateSession(String sessionId) {
        activeSessions.remove(sessionId);
    }

    public void invalidateAllSessions(UUID playerUuid) {
        activeSessions.entrySet().removeIf(e -> e.getValue().playerUuid.equals(playerUuid));
    }

    // ==================== RATE LIMITING ====================

    public boolean isRateLimited(String ip) {
        AttemptTracker tracker = loginAttempts.get(ip);
        if (tracker == null) {
            return false;
        }

        // Check if lockout has expired
        if (tracker.lockedUntil > 0 && System.currentTimeMillis() > tracker.lockedUntil) {
            loginAttempts.remove(ip);
            return false;
        }

        return tracker.lockedUntil > 0;
    }

    public long getRemainingLockoutSeconds(String ip) {
        AttemptTracker tracker = loginAttempts.get(ip);
        if (tracker == null || tracker.lockedUntil <= 0) {
            return 0;
        }

        long remaining = tracker.lockedUntil - System.currentTimeMillis();
        return Math.max(0, remaining / 1000);
    }

    public void recordFailedAttempt(String ip) {
        AttemptTracker tracker = loginAttempts.computeIfAbsent(ip, k -> new AttemptTracker());
        tracker.attempts++;
        tracker.lastAttempt = System.currentTimeMillis();

        if (tracker.attempts >= MAX_LOGIN_ATTEMPTS) {
            tracker.lockedUntil = System.currentTimeMillis() + LOCKOUT_DURATION_MS;
            plugin.logDebug("IP " + ip + " locked out due to " + tracker.attempts + " failed login attempts");
        }
    }

    public void clearFailedAttempts(String ip) {
        loginAttempts.remove(ip);
    }

    // ==================== TRUSTED DEVICES ====================

    public boolean registerTrustedDevice(UUID playerUuid, String playerName, String deviceFingerprint) {
        if (deviceFingerprint == null || deviceFingerprint.length() < 16) {
            return false; // Invalid fingerprint
        }

        String hash = hashSHA256(deviceFingerprint);
        TrustedDevice device = new TrustedDevice(playerUuid, playerName, System.currentTimeMillis());
        trustedDevices.put(hash, device);
        saveTrustedDevices();

        plugin.logDebug("Registered trusted device for " + playerName);
        return true;
    }

    public TrustedDevice validateTrustedDevice(String deviceFingerprint) {
        if (deviceFingerprint == null || deviceFingerprint.isEmpty()) {
            return null;
        }

        String hash = hashSHA256(deviceFingerprint);
        return trustedDevices.get(hash);
    }

    public boolean removeTrustedDevice(String deviceFingerprint) {
        if (deviceFingerprint == null || deviceFingerprint.isEmpty()) {
            return false;
        }

        String hash = hashSHA256(deviceFingerprint);
        TrustedDevice removed = trustedDevices.remove(hash);
        if (removed != null) {
            saveTrustedDevices();
            return true;
        }
        return false;
    }

    public int removeAllTrustedDevices(UUID playerUuid) {
        int count = 0;
        Iterator<Map.Entry<String, TrustedDevice>> it = trustedDevices.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, TrustedDevice> entry = it.next();
            if (entry.getValue().playerUuid.equals(playerUuid)) {
                it.remove();
                count++;
            }
        }
        if (count > 0) {
            saveTrustedDevices();
        }
        return count;
    }

    public int getTrustedDeviceCount(UUID playerUuid) {
        int count = 0;
        for (TrustedDevice device : trustedDevices.values()) {
            if (device.playerUuid.equals(playerUuid)) {
                count++;
            }
        }
        return count;
    }

    public boolean hasTrustedDevices(UUID playerUuid) {
        for (TrustedDevice device : trustedDevices.values()) {
            if (device.playerUuid.equals(playerUuid)) {
                return true;
            }
        }
        return false;
    }

    // ==================== UTILITIES ====================

    private String generateSecureToken(int length) {
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < length; i++) {
            token.append(TOKEN_CHARS.charAt(secureRandom.nextInt(TOKEN_CHARS.length())));
        }
        return token.toString();
    }

    private String hashSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // ==================== PERSISTENCE ====================

    private void loadPermanentTokens() {
        if (!Files.exists(tokensFile)) {
            return;
        }

        try {
            List<String> lines = Files.readAllLines(tokensFile, StandardCharsets.UTF_8);

            for (String line : lines) {
                line = line.trim();

                // Skip comments and empty lines
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Format: hash=uuid
                if (line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        permanentTokenHashes.put(parts[0].trim(), parts[1].trim());
                    }
                }
            }

            plugin.logDebug("Loaded " + permanentTokenHashes.size() + " permanent tokens");
        } catch (IOException e) {
            plugin.logError("Failed to load auth tokens", e);
        }
    }

    private void savePermanentTokens() {
        try {
            // Simple format: hash=uuid per line
            List<String> output = new ArrayList<>();
            output.add("# ModereX Auth Tokens - DO NOT SHARE");

            for (Map.Entry<String, String> entry : permanentTokenHashes.entrySet()) {
                output.add(entry.getKey() + "=" + entry.getValue());
            }

            Files.write(tokensFile, output, StandardCharsets.UTF_8);
        } catch (IOException e) {
            plugin.logError("Failed to save auth tokens", e);
        }
    }

    // ==================== TRUSTED DEVICE PERSISTENCE ====================

    private void loadTrustedDevices() {
        if (!Files.exists(trustedDevicesFile)) {
            return;
        }

        try {
            List<String> lines = Files.readAllLines(trustedDevicesFile, StandardCharsets.UTF_8);

            for (String line : lines) {
                line = line.trim();

                // Skip comments and empty lines
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Format: fingerprintHash=uuid|name|createdAt
                if (line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String hash = parts[0].trim();
                        String[] data = parts[1].trim().split("\\|");
                        if (data.length >= 3) {
                            try {
                                UUID uuid = UUID.fromString(data[0]);
                                String name = data[1];
                                long createdAt = Long.parseLong(data[2]);
                                trustedDevices.put(hash, new TrustedDevice(uuid, name, createdAt));
                            } catch (Exception ignored) {}
                        }
                    }
                }
            }

            plugin.logDebug("Loaded " + trustedDevices.size() + " trusted devices");
        } catch (IOException e) {
            plugin.logError("Failed to load trusted devices", e);
        }
    }

    private void saveTrustedDevices() {
        try {
            // Simple format: hash=uuid|name|createdAt per line
            List<String> output = new ArrayList<>();
            output.add("# ModereX Trusted Devices - DO NOT SHARE");

            for (Map.Entry<String, TrustedDevice> entry : trustedDevices.entrySet()) {
                TrustedDevice device = entry.getValue();
                output.add(entry.getKey() + "=" +
                        device.playerUuid.toString() + "|" + device.playerName + "|" + device.createdAt);
            }

            Files.write(trustedDevicesFile, output, StandardCharsets.UTF_8);
        } catch (IOException e) {
            plugin.logError("Failed to save trusted devices", e);
        }
    }

    // ==================== CLEANUP TASK ====================

    private void startCleanupTask() {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            long now = System.currentTimeMillis();

            // Clean expired temp sessions
            tempSessions.entrySet().removeIf(e ->
                    now - e.getValue().lastActivity > TEMP_SESSION_TIMEOUT_MS);

            // Clean expired authenticated sessions
            activeSessions.entrySet().removeIf(e ->
                    now - e.getValue().lastActivity > SESSION_TIMEOUT_MS);

            // Clean expired rate limit entries
            loginAttempts.entrySet().removeIf(e -> {
                AttemptTracker tracker = e.getValue();
                // Remove if lockout expired and no recent attempts
                return (tracker.lockedUntil > 0 && now > tracker.lockedUntil) ||
                       (tracker.lockedUntil <= 0 && now - tracker.lastAttempt > LOCKOUT_DURATION_MS);
            });

        }, 20 * 60, 20 * 60); // Run every minute
    }

    // ==================== INNER CLASSES ====================

    public static class TempSession {
        public final UUID playerUuid;
        public final String playerName;
        public final long createdAt;
        public long lastActivity;

        public TempSession(UUID playerUuid, String playerName, long createdAt) {
            this.playerUuid = playerUuid;
            this.playerName = playerName;
            this.createdAt = createdAt;
            this.lastActivity = createdAt;
        }
    }

    public static class AuthenticatedSession {
        public final String sessionId;
        public final UUID playerUuid;
        public final String playerName;
        public final long createdAt;
        public long lastActivity;

        public AuthenticatedSession(String sessionId, UUID playerUuid, String playerName, long createdAt) {
            this.sessionId = sessionId;
            this.playerUuid = playerUuid;
            this.playerName = playerName;
            this.createdAt = createdAt;
            this.lastActivity = createdAt;
        }
    }

    private static class AttemptTracker {
        int attempts = 0;
        long lastAttempt = 0;
        long lockedUntil = 0;
    }

    public static class TrustedDevice {
        public final UUID playerUuid;
        public final String playerName;
        public final long createdAt;

        public TrustedDevice(UUID playerUuid, String playerName, long createdAt) {
            this.playerUuid = playerUuid;
            this.playerName = playerName;
            this.createdAt = createdAt;
        }
    }
}
