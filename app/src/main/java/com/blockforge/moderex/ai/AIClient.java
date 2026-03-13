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
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AI client that routes through the ModereX gateway.
 * MC servers never talk to the AI provider directly — all requests go through
 * the gateway's /api/ai/chat endpoint, which handles model selection and auth.
 */
public class AIClient {

    private static final Gson GSON = new Gson();

    private final ModereX plugin;
    private final HttpClient httpClient;

    // Rate limiting
    private final AtomicInteger descriptionCallsThisMinute = new AtomicInteger(0);
    private final AtomicInteger moderationCallsThisMinute = new AtomicInteger(0);
    private static final int DESCRIPTION_RATE_LIMIT = 10; // 10 calls/min
    private static final int MODERATION_RATE_LIMIT = 30; // 30 calls/min

    // Response cache (key -> cached response)
    private final Map<String, CachedResponse> responseCache = new ConcurrentHashMap<>();
    private static final long CACHE_TTL_MS = 30 * 60 * 1000; // 30 minutes

    private ScheduledExecutorService rateLimitResetScheduler;

    public AIClient(ModereX plugin) {
        this.plugin = plugin;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public void start() {
        rateLimitResetScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ModereX-AIRateReset");
            t.setDaemon(true);
            return t;
        });
        rateLimitResetScheduler.scheduleAtFixedRate(() -> {
            descriptionCallsThisMinute.set(0);
            moderationCallsThisMinute.set(0);
        }, 60, 60, TimeUnit.SECONDS);
    }

    public void shutdown() {
        if (rateLimitResetScheduler != null) {
            rateLimitResetScheduler.shutdownNow();
        }
        responseCache.clear();
    }

    /**
     * Generate an AI description for a server rule.
     */
    public CompletableFuture<String> generateRuleDescription(String ruleTitle, String ruleDescription) {
        if (!canMakeDescriptionCall()) {
            return CompletableFuture.completedFuture(null);
        }

        String cacheKey = "desc:" + ruleTitle + ":" + ruleDescription;
        CachedResponse cached = responseCache.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            return CompletableFuture.completedFuture(cached.response);
        }

        String systemPrompt = "You are a Minecraft server rule description writer. " +
                "Given a rule title and its brief description, write a clear, detailed, " +
                "player-friendly explanation of the rule in 2-3 sentences. " +
                "Keep it professional and easy to understand. Do not use markdown formatting.";

        String userPrompt = "Rule: " + ruleTitle + "\nDescription: " + ruleDescription +
                "\n\nWrite a detailed, player-friendly explanation of this rule.";

        return callAI(systemPrompt, userPrompt).thenApply(response -> {
            if (response != null) {
                responseCache.put(cacheKey, new CachedResponse(response));
                descriptionCallsThisMinute.incrementAndGet();
            }
            return response;
        });
    }

    /**
     * Translate a rule description to a target language.
     */
    public CompletableFuture<String> translateRule(String text, String targetLanguage) {
        if (!canMakeDescriptionCall()) {
            return CompletableFuture.completedFuture(null);
        }

        String cacheKey = "translate:" + targetLanguage + ":" + text;
        CachedResponse cached = responseCache.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            return CompletableFuture.completedFuture(cached.response);
        }

        String systemPrompt = "You are a translator. Translate the following Minecraft server rule " +
                "text to " + targetLanguage + ". Keep the same meaning and tone. " +
                "Only output the translated text, nothing else.";

        return callAI(systemPrompt, text).thenApply(response -> {
            if (response != null) {
                responseCache.put(cacheKey, new CachedResponse(response));
                descriptionCallsThisMinute.incrementAndGet();
            }
            return response;
        });
    }

    /**
     * Moderate content using AI. Returns a ModerationResult.
     */
    public CompletableFuture<ModerationResult> moderateContent(String content, String contentType,
                                                                String systemPrompt) {
        if (!canMakeModerationCall()) {
            return CompletableFuture.completedFuture(ModerationResult.ALLOW_FALLBACK);
        }

        String userPrompt = "Content type: " + contentType + "\nContent: " + content +
                "\n\nAnalyze this content and respond with ONLY a JSON object: " +
                "{\"action\": \"ALLOW|WARN|BLOCK|FLAG_FOR_REVIEW\", " +
                "\"confidence\": 0.0-1.0, \"reason\": \"brief reason\", " +
                "\"category\": \"category of violation or NONE\"}";

        return callAI(systemPrompt, userPrompt).thenApply(response -> {
            if (response == null) {
                return ModerationResult.ALLOW_FALLBACK;
            }
            return parseModerationResult(response);
        }).exceptionally(e -> {
            plugin.logError("AI moderation failed", (Exception) e);
            return ModerationResult.ALLOW_FALLBACK;
        });
    }

    /**
     * Moderate a batch of content items.
     */
    public CompletableFuture<java.util.List<ModerationResult>> moderateBatch(
            java.util.List<String> contents, java.util.List<String> contentTypes, String systemPrompt) {
        java.util.List<CompletableFuture<ModerationResult>> futures = new java.util.ArrayList<>();
        for (int i = 0; i < contents.size(); i++) {
            futures.add(moderateContent(contents.get(i), contentTypes.get(i), systemPrompt));
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList());
    }

    /**
     * Test AI moderation with a sample input. Always bypasses rate limit.
     */
    public CompletableFuture<ModerationResult> testModeration(String content, String systemPrompt) {
        String userPrompt = "Content type: CHAT_MESSAGE\nContent: " + content +
                "\n\nAnalyze this content and respond with ONLY a JSON object: " +
                "{\"action\": \"ALLOW|WARN|BLOCK|FLAG_FOR_REVIEW\", " +
                "\"confidence\": 0.0-1.0, \"reason\": \"brief reason\", " +
                "\"category\": \"category of violation or NONE\"}";

        return callAI(systemPrompt, userPrompt).thenApply(response -> {
            if (response == null) {
                return ModerationResult.ALLOW_FALLBACK;
            }
            return parseModerationResult(response);
        });
    }

    /**
     * Call AI through the gateway's /api/ai/chat proxy endpoint.
     * The gateway handles model selection and provider authentication.
     */
    private CompletableFuture<String> callAI(String systemPrompt, String userPrompt) {
        var settings = plugin.getConfigManager().getSettings();

        // Build the gateway AI endpoint URL from the gateway WebSocket URL
        // wss://gateway.moderex.net/server -> https://gateway.moderex.net/api/ai/chat
        String gatewayHttpUrl = deriveGatewayHttpUrl(settings.getGatewayUrl());
        if (gatewayHttpUrl == null) {
            plugin.getLogger().warning("Cannot derive gateway HTTP URL from: " + settings.getGatewayUrl());
            return CompletableFuture.completedFuture(null);
        }
        String aiEndpoint = gatewayHttpUrl + "/api/ai/chat";

        // Build server auth: "Server serverId:secret"
        String serverId = plugin.getServerIdentity().getServerId();
        String secret = settings.getGatewaySecret();
        if (serverId == null || serverId.isEmpty() || secret == null || secret.isEmpty()) {
            plugin.getLogger().warning("AI unavailable: server ID or gateway secret not configured");
            return CompletableFuture.completedFuture(null);
        }

        JsonArray messages = new JsonArray();

        JsonObject systemMsg = new JsonObject();
        systemMsg.addProperty("role", "system");
        systemMsg.addProperty("content", systemPrompt);
        messages.add(systemMsg);

        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", userPrompt);
        messages.add(userMsg);

        JsonObject requestBody = new JsonObject();
        requestBody.add("messages", messages);

        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(aiEndpoint))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Server " + serverId + ":" + secret)
                        .timeout(Duration.ofSeconds(30))
                        .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(requestBody)))
                        .build();

                HttpResponse<String> response = httpClient.send(
                        request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    plugin.getLogger().warning("AI gateway returned status " + response.statusCode());
                    return null;
                }

                JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();

                // Handle chat API response format (gateway forwards as-is)
                if (responseJson.has("message")) {
                    return responseJson.getAsJsonObject("message").get("content").getAsString().trim();
                }
                // Handle OpenAI-compatible format
                if (responseJson.has("choices")) {
                    return responseJson.getAsJsonArray("choices")
                            .get(0).getAsJsonObject()
                            .getAsJsonObject("message")
                            .get("content").getAsString().trim();
                }

                return null;
            } catch (Exception e) {
                plugin.logError("AI gateway call failed", e);
                return null;
            }
        });
    }

    /**
     * Derive the HTTP(S) base URL from the gateway WebSocket URL.
     * wss://host/path -> https://host
     * ws://host/path -> http://host
     */
    private String deriveGatewayHttpUrl(String gatewayWsUrl) {
        if (gatewayWsUrl == null || gatewayWsUrl.isEmpty()) return null;
        try {
            URI uri = URI.create(gatewayWsUrl);
            String scheme = "wss".equalsIgnoreCase(uri.getScheme()) ? "https" : "http";
            int port = uri.getPort();
            if (port == -1) {
                return scheme + "://" + uri.getHost();
            }
            return scheme + "://" + uri.getHost() + ":" + port;
        } catch (Exception e) {
            return null;
        }
    }

    private ModerationResult parseModerationResult(String response) {
        try {
            // Extract JSON from response (may contain text around it)
            int jsonStart = response.indexOf('{');
            int jsonEnd = response.lastIndexOf('}');
            if (jsonStart == -1 || jsonEnd == -1) {
                return ModerationResult.ALLOW_FALLBACK;
            }

            String jsonStr = response.substring(jsonStart, jsonEnd + 1);
            JsonObject json = JsonParser.parseString(jsonStr).getAsJsonObject();

            ModerationResult result = new ModerationResult();
            result.setAction(ModerationAction.valueOf(json.get("action").getAsString()));
            result.setConfidence(json.get("confidence").getAsDouble());
            result.setReason(json.has("reason") ? json.get("reason").getAsString() : "");
            result.setCategory(json.has("category") ? json.get("category").getAsString() : "NONE");

            return result;
        } catch (Exception e) {
            plugin.logError("Failed to parse moderation result: " + response, e);
            return ModerationResult.ALLOW_FALLBACK;
        }
    }

    private boolean canMakeDescriptionCall() {
        if (!plugin.getConfigManager().getSettings().isAiEnabled()) {
            return false;
        }
        return descriptionCallsThisMinute.get() < DESCRIPTION_RATE_LIMIT;
    }

    private boolean canMakeModerationCall() {
        if (!plugin.getConfigManager().getSettings().isAiEnabled()) {
            return false;
        }
        return moderationCallsThisMinute.get() < MODERATION_RATE_LIMIT;
    }

    public boolean isAvailable() {
        var settings = plugin.getConfigManager().getSettings();
        String serverId = plugin.getServerIdentity().getServerId();
        return settings.isAiEnabled() &&
                settings.getGatewayUrl() != null &&
                !settings.getGatewayUrl().isEmpty() &&
                serverId != null && !serverId.isEmpty() &&
                settings.getGatewaySecret() != null &&
                !settings.getGatewaySecret().isEmpty();
    }

    public int getDescriptionCallsRemaining() {
        return Math.max(0, DESCRIPTION_RATE_LIMIT - descriptionCallsThisMinute.get());
    }

    public int getModerationCallsRemaining() {
        return Math.max(0, MODERATION_RATE_LIMIT - moderationCallsThisMinute.get());
    }

    private static class CachedResponse {
        final String response;
        final long timestamp;

        CachedResponse(String response) {
            this.response = response;
            this.timestamp = System.currentTimeMillis();
        }

        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_TTL_MS;
        }
    }
}
