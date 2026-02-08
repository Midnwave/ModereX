package com.blockforge.moderex.web;

import com.blockforge.moderex.ModereX;
import com.google.gson.JsonObject;

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
    private static final long TOKEN_EXPIRY_MS = 90L * 24 * 60 * 60 * 1000; // 90 days

    // Challenge constants for CAPTCHA after failed attempts
    private static final int CHALLENGE_THRESHOLD = 3; // Require challenge after this many fails

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

        // Store hash -> uuid:timestamp mapping
        long now = System.currentTimeMillis();
        long expiresAt = now + TOKEN_EXPIRY_MS;
        permanentTokenHashes.put(hash, playerUuid.toString() + ":" + now);
        savePermanentTokens();

        // Sync to gateway if enabled (global token)
        if (plugin.getGatewayClient() != null && plugin.getGatewayClient().isConnected()) {
            String username = org.bukkit.Bukkit.getOfflinePlayer(playerUuid).getName();
            plugin.getGatewayClient().registerGlobalToken(playerUuid, username, hash, expiresAt);
        }

        plugin.logDebug("Generated permanent token for " + playerUuid);
        return tokenStr;
    }

    public boolean hasPermanentToken(UUID playerUuid) {
        String uuidStr = playerUuid.toString();
        for (String value : permanentTokenHashes.values()) {
            // Handle both "uuid" and "uuid:timestamp" formats
            String storedUuid = value.contains(":") ? value.split(":", 2)[0] : value;
            if (storedUuid.equals(uuidStr)) return true;
        }
        return false;
    }

    /**
     * Result of a permanent token validation.
     */
    public enum TokenValidationResult {
        VALID, EXPIRED, INVALID, RATE_LIMITED, CHALLENGE_REQUIRED
    }

    /**
     * Validate a permanent token and return detailed result.
     * Returns the player UUID if valid, null otherwise. Use getLastValidationResult() for details.
     */
    private volatile TokenValidationResult lastValidationResult = TokenValidationResult.INVALID;

    public TokenValidationResult getLastValidationResult() {
        return lastValidationResult;
    }

    public UUID validatePermanentToken(String token, String clientIp) {
        // Check rate limiting
        if (isRateLimited(clientIp)) {
            lastValidationResult = TokenValidationResult.RATE_LIMITED;
            return null;
        }

        // Check if challenge is required
        AttemptTracker tracker = loginAttempts.get(clientIp);
        if (tracker != null && tracker.attempts >= CHALLENGE_THRESHOLD && !tracker.challengeSolved) {
            lastValidationResult = TokenValidationResult.CHALLENGE_REQUIRED;
            return null;
        }

        String hash = hashSHA256(token);
        String value = permanentTokenHashes.get(hash);

        if (value != null) {
            // Parse uuid:timestamp format (backwards compatible with old uuid-only format)
            String uuidStr;
            long createdAt = 0;

            if (value.contains(":")) {
                String[] parts = value.split(":", 2);
                uuidStr = parts[0];
                try {
                    createdAt = Long.parseLong(parts[1]);
                } catch (NumberFormatException e) {
                    createdAt = 0; // Treat as legacy token (no expiry check)
                }
            } else {
                uuidStr = value; // Legacy format - no timestamp
            }

            // Check if token has expired (90 days)
            if (createdAt > 0 && System.currentTimeMillis() - createdAt > TOKEN_EXPIRY_MS) {
                // Token expired - remove it
                permanentTokenHashes.remove(hash);
                savePermanentTokens();
                lastValidationResult = TokenValidationResult.EXPIRED;
                plugin.logDebug("Permanent token expired for " + uuidStr);
                return null;
            }

            // Valid token
            loginAttempts.remove(clientIp);
            lastValidationResult = TokenValidationResult.VALID;
            return UUID.fromString(uuidStr);
        } else {
            // Record failed attempt
            recordFailedAttempt(clientIp);
            lastValidationResult = TokenValidationResult.INVALID;
            return null;
        }
    }

    public boolean revokePermanentToken(UUID playerUuid) {
        boolean removed = removeExistingPermanentToken(playerUuid);

        // Disconnect any active sessions for this player
        if (removed) {
            disconnectPlayerSessions(playerUuid);
        }

        // Sync revocation to gateway if enabled
        if (removed && plugin.getGatewayClient() != null && plugin.getGatewayClient().isConnected()) {
            plugin.getGatewayClient().revokeGlobalToken(playerUuid);
        }

        return removed;
    }

    /**
     * Disconnect all active web panel sessions for a player.
     * Called when token is revoked for security.
     */
    public void disconnectPlayerSessions(UUID playerUuid) {
        // Remove from active sessions
        activeSessions.entrySet().removeIf(entry -> entry.getValue().playerUuid.equals(playerUuid));

        // Disconnect from web panel server
        if (plugin.getWebPanelServer() != null) {
            plugin.getWebPanelServer().disconnectPlayer(playerUuid, "TOKEN_REVOKED", "Your access token has been revoked.");
        }

        plugin.logDebug("[WebAuth] Disconnected all sessions for player: " + playerUuid);
    }

    private boolean removeExistingPermanentToken(UUID playerUuid) {
        String uuidStr = playerUuid.toString();
        String hashToRemove = null;

        for (Map.Entry<String, String> entry : permanentTokenHashes.entrySet()) {
            // Handle both "uuid" and "uuid:timestamp" formats
            String storedUuid = entry.getValue().contains(":") ? entry.getValue().split(":", 2)[0] : entry.getValue();
            if (storedUuid.equals(uuidStr)) {
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

        // Check exponential backoff
        if (tracker.backoffUntil > 0 && System.currentTimeMillis() < tracker.backoffUntil) {
            return true;
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
        tracker.challengeSolved = false;

        if (tracker.attempts >= MAX_LOGIN_ATTEMPTS) {
            // Full lockout
            tracker.lockedUntil = System.currentTimeMillis() + LOCKOUT_DURATION_MS;
            plugin.logDebug("IP " + ip + " locked out due to " + tracker.attempts + " failed login attempts");
        } else if (tracker.attempts >= CHALLENGE_THRESHOLD) {
            // Generate challenge and set exponential backoff
            generateChallenge(tracker);
            // Exponential backoff: 5s after 4th, 15s after 5th
            int backoffSeconds = (int) Math.pow(3, tracker.attempts - CHALLENGE_THRESHOLD) * 5;
            tracker.backoffUntil = System.currentTimeMillis() + (backoffSeconds * 1000L);
            plugin.logDebug("IP " + ip + " requires challenge (attempt " + tracker.attempts + ", backoff " + backoffSeconds + "s)");
        }
    }

    private void generateChallenge(AttemptTracker tracker) {
        int a = secureRandom.nextInt(20) + 1;
        int b = secureRandom.nextInt(20) + 1;
        boolean add = secureRandom.nextBoolean();
        if (add) {
            tracker.challengeQuestion = "What is " + a + " + " + b + "?";
            tracker.challengeAnswer = String.valueOf(a + b);
        } else {
            // Ensure no negative results
            int max = Math.max(a, b);
            int min = Math.min(a, b);
            tracker.challengeQuestion = "What is " + max + " - " + min + "?";
            tracker.challengeAnswer = String.valueOf(max - min);
        }
    }

    /**
     * Get the current challenge question for an IP, or null if no challenge required.
     */
    public String getChallengeQuestion(String ip) {
        AttemptTracker tracker = loginAttempts.get(ip);
        if (tracker != null && tracker.attempts >= CHALLENGE_THRESHOLD && !tracker.challengeSolved) {
            return tracker.challengeQuestion;
        }
        return null;
    }

    /**
     * Validate a challenge answer. Returns true if correct.
     */
    public boolean validateChallenge(String ip, String answer) {
        AttemptTracker tracker = loginAttempts.get(ip);
        if (tracker == null || tracker.challengeAnswer == null) {
            return false;
        }

        // Use constant-time comparison to prevent timing attacks
        if (java.security.MessageDigest.isEqual(
                tracker.challengeAnswer.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                answer.trim().getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
            tracker.challengeSolved = true;
            return true;
        } else {
            // Wrong answer counts as another failed attempt
            recordFailedAttempt(ip);
            return false;
        }
    }

    /**
     * Check if an IP is in backoff period (must wait before next attempt).
     */
    public boolean isInBackoff(String ip) {
        AttemptTracker tracker = loginAttempts.get(ip);
        if (tracker == null || tracker.backoffUntil <= 0) return false;
        return System.currentTimeMillis() < tracker.backoffUntil;
    }

    /**
     * Get remaining backoff seconds for an IP.
     */
    public long getRemainingBackoffSeconds(String ip) {
        AttemptTracker tracker = loginAttempts.get(ip);
        if (tracker == null || tracker.backoffUntil <= 0) return 0;
        long remaining = tracker.backoffUntil - System.currentTimeMillis();
        return Math.max(0, remaining / 1000);
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

        // Sync device fingerprints to gateway
        syncDeviceFingerprintsToGateway(playerUuid);

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
            // Sync updated fingerprints to gateway
            syncDeviceFingerprintsToGateway(removed.playerUuid);
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
            // Sync cleared fingerprints to gateway
            syncDeviceFingerprintsToGateway(playerUuid);
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

    /**
     * Get all trusted device fingerprint hashes for a player.
     * Used for syncing device fingerprints to the gateway.
     */
    public List<String> getTrustedDeviceHashes(UUID playerUuid) {
        List<String> hashes = new ArrayList<>();
        for (Map.Entry<String, TrustedDevice> entry : trustedDevices.entrySet()) {
            if (entry.getValue().playerUuid.equals(playerUuid)) {
                hashes.add(entry.getKey());
            }
        }
        return hashes;
    }

    // ==================== GATEWAY SYNC ====================

    /**
     * Sync a player's trusted device fingerprint hashes to the gateway.
     * Called after registering/removing devices so the gateway can validate device auth.
     */
    private void syncDeviceFingerprintsToGateway(UUID playerUuid) {
        if (plugin.getGatewayClient() != null && plugin.getGatewayClient().isConnected()) {
            List<String> hashes = getTrustedDeviceHashes(playerUuid);
            // Get current color scheme from panel settings
            String colorScheme = "blue";
            if (plugin.getWebPanelServer() != null) {
                JsonObject settingsJson = plugin.getWebPanelServer().getUserSettingsJson(playerUuid);
                if (settingsJson.has("themeColor")) {
                    colorScheme = settingsJson.get("themeColor").getAsString();
                }
            }
            plugin.getGatewayClient().syncUserSettings(playerUuid, colorScheme, hashes);
        }
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

            // Clean expired permanent tokens (90-day TTL)
            boolean tokensChanged = false;
            Iterator<Map.Entry<String, String>> tokenIt = permanentTokenHashes.entrySet().iterator();
            while (tokenIt.hasNext()) {
                Map.Entry<String, String> entry = tokenIt.next();
                String value = entry.getValue();
                if (value.contains(":")) {
                    try {
                        long createdAt = Long.parseLong(value.split(":", 2)[1]);
                        if (now - createdAt > TOKEN_EXPIRY_MS) {
                            tokenIt.remove();
                            tokensChanged = true;
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
            if (tokensChanged) {
                savePermanentTokens();
            }

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
        long backoffUntil = 0;        // Exponential backoff timestamp
        boolean challengeSolved = false; // Whether the CAPTCHA challenge was solved
        String challengeAnswer = null;   // Current challenge answer
        String challengeQuestion = null; // Current challenge question
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
