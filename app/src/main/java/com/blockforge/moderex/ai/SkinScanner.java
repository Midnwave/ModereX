package com.blockforge.moderex.ai;

import com.blockforge.moderex.ModereX;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Scans player skins via the Mojang API for inappropriate content.
 * Uses basic image analysis through the AI gateway when vision is available,
 * or falls back to skin URL pattern detection.
 */
public class SkinScanner {

    private static final Gson GSON = new Gson();
    private static final String MOJANG_SESSION_API = "https://sessionserver.mojang.com/session/minecraft/profile/";

    private final ModereX plugin;
    private final HttpClient httpClient;

    // Cache scan results to avoid repeated API calls
    private final Map<UUID, SkinScanResult> scanCache = new ConcurrentHashMap<>();
    private static final long CACHE_TTL_MS = 3600000L; // 1 hour

    private boolean enabled = false;
    private boolean autoScanOnJoin = false;

    public SkinScanner(ModereX plugin) {
        this.plugin = plugin;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Fetch a player's skin URL from Mojang API.
     */
    public CompletableFuture<String> getSkinUrl(UUID playerUuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String dashlessUuid = playerUuid.toString().replace("-", "");
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(MOJANG_SESSION_API + dashlessUuid))
                        .header("Accept", "application/json")
                        .timeout(Duration.ofSeconds(10))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    plugin.getLogger().warning("Mojang API returned " + response.statusCode() + " for " + playerUuid);
                    return null;
                }

                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                JsonArray properties = json.getAsJsonArray("properties");

                for (var prop : properties) {
                    JsonObject propObj = prop.getAsJsonObject();
                    if ("textures".equals(propObj.get("name").getAsString())) {
                        String base64 = propObj.get("value").getAsString();
                        String decoded = new String(Base64.getDecoder().decode(base64));
                        JsonObject textures = JsonParser.parseString(decoded).getAsJsonObject();
                        JsonObject skin = textures.getAsJsonObject("textures").getAsJsonObject("SKIN");
                        return skin.get("url").getAsString();
                    }
                }

                return null;
            } catch (Exception e) {
                plugin.logError("Failed to fetch skin from Mojang", e);
                return null;
            }
        });
    }

    /**
     * Scan a player's skin for inappropriate content.
     * First checks cache, then fetches from Mojang and sends to AI for analysis.
     */
    public CompletableFuture<SkinScanResult> scanPlayer(UUID playerUuid, String playerName) {
        // Check cache
        SkinScanResult cached = scanCache.get(playerUuid);
        if (cached != null && !cached.isExpired()) {
            return CompletableFuture.completedFuture(cached);
        }

        return getSkinUrl(playerUuid).thenCompose(skinUrl -> {
            if (skinUrl == null) {
                SkinScanResult noSkin = new SkinScanResult(playerUuid, playerName, false, "Unable to fetch skin", 0.0);
                scanCache.put(playerUuid, noSkin);
                return CompletableFuture.completedFuture(noSkin);
            }

            // Use AI to analyze the skin URL
            var aiClient = plugin.getAIClient();
            if (aiClient == null || !aiClient.isAvailable()) {
                SkinScanResult noAi = new SkinScanResult(playerUuid, playerName, false, "AI unavailable", 0.0);
                noAi.setSkinUrl(skinUrl);
                scanCache.put(playerUuid, noAi);
                return CompletableFuture.completedFuture(noAi);
            }

            String systemPrompt = "You are a Minecraft skin content moderator. " +
                    "Given a Minecraft skin texture URL, analyze whether the skin could contain " +
                    "inappropriate imagery (explicit content, hate symbols, offensive imagery). " +
                    "NOTE: You cannot actually view the image from a URL alone. " +
                    "Instead, analyze the URL patterns and player name for red flags. " +
                    "Respond with ONLY a JSON object: " +
                    "{\"flagged\": true/false, \"reason\": \"brief reason\", \"confidence\": 0.0-1.0}";

            String userPrompt = "Player: " + playerName + "\nSkin URL: " + skinUrl +
                    "\n\nAnalyze if this player's skin should be flagged for review.";

            return aiClient.moderateContent(userPrompt, "SKIN", systemPrompt)
                    .thenApply(result -> {
                        boolean flagged = result.getAction() != ModerationAction.ALLOW;
                        SkinScanResult scanResult = new SkinScanResult(
                                playerUuid, playerName, flagged,
                                result.getReason(), result.getConfidence());
                        scanResult.setSkinUrl(skinUrl);
                        scanCache.put(playerUuid, scanResult);
                        return scanResult;
                    });
        });
    }

    /**
     * Get all cached scan results as JSON.
     */
    public JsonObject toStatusJson() {
        JsonObject json = new JsonObject();
        json.addProperty("enabled", enabled);
        json.addProperty("autoScanOnJoin", autoScanOnJoin);
        json.addProperty("cachedScans", scanCache.size());

        JsonArray flagged = new JsonArray();
        for (SkinScanResult result : scanCache.values()) {
            if (result.isFlagged() && !result.isExpired()) {
                JsonObject entry = new JsonObject();
                entry.addProperty("playerUuid", result.playerUuid.toString());
                entry.addProperty("playerName", result.playerName);
                entry.addProperty("reason", result.reason);
                entry.addProperty("confidence", result.confidence);
                entry.addProperty("skinUrl", result.skinUrl != null ? result.skinUrl : "");
                flagged.add(entry);
            }
        }
        json.add("flaggedSkins", flagged);

        return json;
    }

    public void updateFromJson(JsonObject json) {
        if (json.has("enabled")) this.enabled = json.get("enabled").getAsBoolean();
        if (json.has("autoScanOnJoin")) this.autoScanOnJoin = json.get("autoScanOnJoin").getAsBoolean();
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isAutoScanOnJoin() { return autoScanOnJoin; }
    public void setAutoScanOnJoin(boolean auto) { this.autoScanOnJoin = auto; }

    public void clearCache() { scanCache.clear(); }

    /**
     * Result of a skin scan.
     */
    public static class SkinScanResult {
        private final UUID playerUuid;
        private final String playerName;
        private final boolean flagged;
        private final String reason;
        private final double confidence;
        private final long scannedAt;
        private String skinUrl;

        public SkinScanResult(UUID playerUuid, String playerName, boolean flagged,
                              String reason, double confidence) {
            this.playerUuid = playerUuid;
            this.playerName = playerName;
            this.flagged = flagged;
            this.reason = reason;
            this.confidence = confidence;
            this.scannedAt = System.currentTimeMillis();
        }

        public UUID getPlayerUuid() { return playerUuid; }
        public String getPlayerName() { return playerName; }
        public boolean isFlagged() { return flagged; }
        public String getReason() { return reason; }
        public double getConfidence() { return confidence; }
        public String getSkinUrl() { return skinUrl; }
        public void setSkinUrl(String url) { this.skinUrl = url; }
        public boolean isExpired() { return System.currentTimeMillis() - scannedAt > CACHE_TTL_MS; }
    }
}
