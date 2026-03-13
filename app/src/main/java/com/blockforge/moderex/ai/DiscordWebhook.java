package com.blockforge.moderex.ai;

import com.blockforge.moderex.ModereX;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

/**
 * Sends AI moderation alerts to Discord via webhook.
 * Supports configurable minimum severity and rich embed formatting.
 */
public class DiscordWebhook {

    private static final Gson GSON = new Gson();

    private final ModereX plugin;
    private final HttpClient httpClient;

    private String webhookUrl = "";
    private boolean enabled = false;
    private ModerationAction minimumSeverity = ModerationAction.BLOCK;

    public DiscordWebhook(ModereX plugin) {
        this.plugin = plugin;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public void setWebhookUrl(String url) {
        this.webhookUrl = url != null ? url : "";
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setMinimumSeverity(ModerationAction severity) {
        this.minimumSeverity = severity;
    }

    public ModerationAction getMinimumSeverity() {
        return minimumSeverity;
    }

    /**
     * Send a moderation alert to Discord.
     * Only sends if enabled, webhook URL is configured, and the result meets the minimum severity.
     */
    public CompletableFuture<Void> sendAlert(String playerName, String playerUuid,
                                              ModerationResult result, String content,
                                              String contentType) {
        if (!enabled || webhookUrl.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        // Check minimum severity
        if (result.getAction().ordinal() < minimumSeverity.ordinal()) {
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.runAsync(() -> {
            try {
                JsonObject payload = buildEmbed(playerName, playerUuid, result, content, contentType);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(webhookUrl))
                        .header("Content-Type", "application/json")
                        .timeout(Duration.ofSeconds(10))
                        .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(payload)))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 429) {
                    plugin.getLogger().warning("Discord webhook rate limited");
                } else if (response.statusCode() != 204 && response.statusCode() != 200) {
                    plugin.getLogger().warning("Discord webhook returned " + response.statusCode());
                }
            } catch (Exception e) {
                plugin.logError("Failed to send Discord webhook", e);
            }
        });
    }

    private JsonObject buildEmbed(String playerName, String playerUuid,
                                   ModerationResult result, String content, String contentType) {
        JsonObject payload = new JsonObject();
        payload.addProperty("username", "ModereX AI");

        JsonArray embeds = new JsonArray();
        JsonObject embed = new JsonObject();

        // Color based on action
        int color = switch (result.getAction()) {
            case BLOCK -> 0xED4245;       // Red
            case WARN -> 0xFEE75C;        // Yellow
            case FLAG_FOR_REVIEW -> 0xF0B232; // Orange
            case ALLOW -> 0x57F287;       // Green
        };
        embed.addProperty("color", color);

        embed.addProperty("title", "\uD83D\uDEE1\uFE0F AI Moderation Alert \u2014 " + result.getAction().name());

        // Fields
        JsonArray fields = new JsonArray();

        JsonObject playerField = new JsonObject();
        playerField.addProperty("name", "Player");
        playerField.addProperty("value", playerName);
        playerField.addProperty("inline", true);
        fields.add(playerField);

        JsonObject actionField = new JsonObject();
        actionField.addProperty("name", "Action");
        actionField.addProperty("value", result.getAction().name());
        actionField.addProperty("inline", true);
        fields.add(actionField);

        JsonObject confField = new JsonObject();
        confField.addProperty("name", "Confidence");
        confField.addProperty("value", String.format("%.0f%%", result.getConfidence() * 100));
        confField.addProperty("inline", true);
        fields.add(confField);

        JsonObject catField = new JsonObject();
        catField.addProperty("name", "Category");
        catField.addProperty("value", result.getCategory());
        catField.addProperty("inline", true);
        fields.add(catField);

        JsonObject typeField = new JsonObject();
        typeField.addProperty("name", "Content Type");
        typeField.addProperty("value", contentType);
        typeField.addProperty("inline", true);
        fields.add(typeField);

        JsonObject contentField = new JsonObject();
        contentField.addProperty("name", "Content");
        String truncated = content.length() > 1024 ? content.substring(0, 1021) + "..." : content;
        contentField.addProperty("value", "```" + truncated + "```");
        contentField.addProperty("inline", false);
        fields.add(contentField);

        if (result.getReason() != null && !result.getReason().isEmpty()) {
            JsonObject reasonField = new JsonObject();
            reasonField.addProperty("name", "Reason");
            reasonField.addProperty("value", result.getReason());
            reasonField.addProperty("inline", false);
            fields.add(reasonField);
        }

        embed.add("fields", fields);

        // Timestamp
        embed.addProperty("timestamp", Instant.now().toString());

        // Footer
        JsonObject footer = new JsonObject();
        footer.addProperty("text", "ModereX AI Moderation");
        embed.add("footer", footer);

        embeds.add(embed);
        payload.add("embeds", embeds);

        return payload;
    }

    /**
     * Serialize webhook configuration to JSON (hides the actual URL).
     */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("enabled", enabled);
        json.addProperty("webhookUrl", webhookUrl.isEmpty() ? "" : "***configured***");
        json.addProperty("minimumSeverity", minimumSeverity.name());
        return json;
    }

    /**
     * Update webhook configuration from a JSON object.
     * Does not overwrite the URL if the value is the masked placeholder.
     */
    public void updateFromJson(JsonObject json) {
        if (json.has("enabled")) {
            setEnabled(json.get("enabled").getAsBoolean());
        }
        if (json.has("webhookUrl")) {
            String url = json.get("webhookUrl").getAsString();
            if (!"***configured***".equals(url)) {
                setWebhookUrl(url);
            }
        }
        if (json.has("minimumSeverity")) {
            try {
                setMinimumSeverity(ModerationAction.valueOf(json.get("minimumSeverity").getAsString()));
            } catch (IllegalArgumentException ignored) {}
        }
    }
}
