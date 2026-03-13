package com.blockforge.moderex.ai;

import com.blockforge.moderex.ModereX;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages AI-powered content moderation.
 * Processes chat, signs, books, nicknames, anvil renames, and commands.
 */
public class AIModerationManager {

    private static final Gson GSON = new Gson();

    private final ModereX plugin;
    private ModerationPreset activePreset = ModerationPreset.CUSTOM;
    private String customSystemPrompt = "";
    private double confidenceThreshold = 0.7;
    private final Set<ContentType> enabledContentTypes = EnumSet.noneOf(ContentType.class);

    // Batching: queue messages, process in batches
    private final ConcurrentLinkedQueue<PendingModeration> moderationQueue = new ConcurrentLinkedQueue<>();
    private ScheduledExecutorService batchProcessor;
    private static final int BATCH_SIZE = 5;
    private static final long BATCH_INTERVAL_MS = 500;

    // Statistics
    private final AtomicInteger totalChecked = new AtomicInteger(0);
    private final AtomicInteger totalBlocked = new AtomicInteger(0);
    private final AtomicInteger totalWarned = new AtomicInteger(0);
    private final AtomicInteger totalFlagged = new AtomicInteger(0);

    // Analytics and Discord integration
    private final ModerationAnalytics analytics;
    private final DiscordWebhook discordWebhook;

    // Escalation and context
    private final EscalationManager escalationManager;
    private final MessageContextWindow contextWindow;
    private boolean contextEnabled = true;
    private int contextMessageCount = 20; // last 20 messages sent to AI

    // Review queue and sandbox/dry-run mode
    private final ModerationReviewQueue reviewQueue;
    private boolean dryRunMode = false;

    // Skin scanning
    private final SkinScanner skinScanner;

    public AIModerationManager(ModereX plugin) {
        this.plugin = plugin;
        this.analytics = new ModerationAnalytics(plugin);
        this.discordWebhook = new DiscordWebhook(plugin);
        this.escalationManager = new EscalationManager(plugin);
        this.contextWindow = new MessageContextWindow();
        this.reviewQueue = new ModerationReviewQueue(plugin);
        this.skinScanner = new SkinScanner(plugin);
    }

    public void initialize() {
        createTable();
        loadSettings();
        reviewQueue.initialize();
    }

    public void start() {
        batchProcessor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ModereX-AIModBatch");
            t.setDaemon(true);
            return t;
        });
        batchProcessor.scheduleAtFixedRate(this::processBatch, BATCH_INTERVAL_MS, BATCH_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if (batchProcessor != null) {
            batchProcessor.shutdownNow();
        }
    }

    private void createTable() {
        try {
            String sql;
            if (plugin.getDatabaseManager().isMySQL()) {
                sql = """
                    CREATE TABLE IF NOT EXISTS moderex_ai_moderation_log (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        player_uuid VARCHAR(36) NOT NULL,
                        player_name VARCHAR(16) NOT NULL,
                        content_type VARCHAR(32) NOT NULL,
                        content TEXT NOT NULL,
                        action VARCHAR(20) NOT NULL,
                        confidence DOUBLE NOT NULL,
                        reason TEXT,
                        category VARCHAR(64),
                        created_at BIGINT NOT NULL,
                        INDEX idx_player (player_uuid),
                        INDEX idx_time (created_at)
                    )
                """;
            } else {
                sql = """
                    CREATE TABLE IF NOT EXISTS moderex_ai_moderation_log (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        player_uuid VARCHAR(36) NOT NULL,
                        player_name VARCHAR(16) NOT NULL,
                        content_type VARCHAR(32) NOT NULL,
                        content TEXT NOT NULL,
                        action VARCHAR(20) NOT NULL,
                        confidence DOUBLE NOT NULL,
                        reason TEXT,
                        category VARCHAR(64),
                        created_at BIGINT NOT NULL
                    )
                """;
            }
            plugin.getDatabaseManager().update(sql);

            if (!plugin.getDatabaseManager().isMySQL()) {
                plugin.getDatabaseManager().update(
                        "CREATE INDEX IF NOT EXISTS idx_aimod_player ON moderex_ai_moderation_log(player_uuid)");
                plugin.getDatabaseManager().update(
                        "CREATE INDEX IF NOT EXISTS idx_aimod_time ON moderex_ai_moderation_log(created_at)");
            }
        } catch (SQLException e) {
            plugin.logError("Failed to create ai_moderation_log table", e);
        }
    }

    private void loadSettings() {
        // Settings loaded from config, defaults are fine for now
    }

    /**
     * Check if a content type is enabled for AI moderation.
     */
    public boolean isContentTypeEnabled(ContentType type) {
        return enabledContentTypes.contains(type);
    }

    /**
     * Submit content for AI moderation. Returns a future with the result.
     */
    public CompletableFuture<ModerationResult> checkContent(Player player, String content, ContentType contentType) {
        if (!isContentTypeEnabled(contentType)) {
            return CompletableFuture.completedFuture(ModerationResult.ALLOW_FALLBACK);
        }

        var aiClient = plugin.getAIClient();
        if (aiClient == null || !aiClient.isAvailable()) {
            return CompletableFuture.completedFuture(ModerationResult.ALLOW_FALLBACK);
        }

        totalChecked.incrementAndGet();

        // Add message to context window
        contextWindow.addMessage(player.getUniqueId(), content, contentType.getDisplayName());

        // Build system prompt with optional context
        String systemPrompt = getEffectiveSystemPrompt();
        if (contextEnabled) {
            String context = contextWindow.getContextString(player.getUniqueId(), contextMessageCount);
            if (!context.isEmpty()) {
                systemPrompt = systemPrompt + "\n\n" + context;
            }
        }

        return aiClient.moderateContent(content, contentType.getDisplayName(), systemPrompt)
                .thenApply(result -> {
                    // Apply confidence threshold
                    if (result.getConfidence() < confidenceThreshold &&
                            result.getAction() != ModerationAction.ALLOW) {
                        // Below threshold - downgrade to allow
                        result = ModerationResult.ALLOW_FALLBACK;
                    }

                    // Parse violation category from the category string
                    ViolationCategory violationCat = ViolationCategory.fromString(result.getCategory());
                    result.setViolationCategory(violationCat);

                    // Run escalation logic
                    EscalationManager.EscalatedAction escalated = escalationManager.recordAndEscalate(
                            player.getUniqueId(), violationCat, result.getAction());
                    result.setAction(escalated.action());

                    // Track statistics
                    switch (result.getAction()) {
                        case BLOCK -> totalBlocked.incrementAndGet();
                        case WARN -> totalWarned.incrementAndGet();
                        case FLAG_FOR_REVIEW -> totalFlagged.incrementAndGet();
                    }

                    // Add FLAG_FOR_REVIEW items to review queue
                    if (result.getAction() == ModerationAction.FLAG_FOR_REVIEW) {
                        reviewQueue.addToQueue(player.getUniqueId().toString(), player.getName(),
                                contentType, content, result);
                    }

                    // Dry-run mode: log but don't actually block
                    if (dryRunMode && result.getAction() != ModerationAction.ALLOW) {
                        ModerationResult dryResult = new ModerationResult(
                                ModerationAction.ALLOW, result.getConfidence(),
                                "[DRY RUN] " + result.getReason(), result.getCategory());
                        // Log the original decision, then return the dry-run override
                        logModerationDecision(player, contentType, content, result);

                        // Send Discord webhook alert
                        discordWebhook.sendAlert(player.getName(), player.getUniqueId().toString(),
                                result, content, contentType.getDisplayName());

                        return dryResult;
                    }

                    // Log to database
                    logModerationDecision(player, contentType, content, result);

                    // Send Discord webhook alert
                    discordWebhook.sendAlert(player.getName(), player.getUniqueId().toString(),
                            result, content, contentType.getDisplayName());

                    return result;
                });
    }

    /**
     * Submit content for batch processing (fire-and-forget with callback).
     */
    public void submitForBatchModeration(Player player, String content, ContentType contentType,
                                          java.util.function.Consumer<ModerationResult> callback) {
        moderationQueue.add(new PendingModeration(player, content, contentType, callback));
    }

    private void processBatch() {
        if (moderationQueue.isEmpty()) return;

        List<PendingModeration> batch = new ArrayList<>();
        for (int i = 0; i < BATCH_SIZE && !moderationQueue.isEmpty(); i++) {
            PendingModeration item = moderationQueue.poll();
            if (item != null) batch.add(item);
        }

        if (batch.isEmpty()) return;

        for (PendingModeration item : batch) {
            checkContent(item.player, item.content, item.contentType)
                    .thenAccept(result -> {
                        if (item.callback != null) {
                            // Run callback on main thread
                            plugin.getServer().getScheduler().runTask(plugin, () ->
                                    item.callback.accept(result));
                        }
                    });
        }
    }

    private void logModerationDecision(Player player, ContentType contentType, String content, ModerationResult result) {
        CompletableFuture.runAsync(() -> {
            try {
                plugin.getDatabaseManager().update("""
                    INSERT INTO moderex_ai_moderation_log
                    (player_uuid, player_name, content_type, content, action, confidence, reason, category, created_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                        player.getUniqueId().toString(),
                        player.getName(),
                        contentType.name(),
                        content,
                        result.getAction().name(),
                        result.getConfidence(),
                        result.getReason(),
                        result.getCategory(),
                        System.currentTimeMillis()
                );
            } catch (SQLException e) {
                plugin.logError("Failed to log AI moderation decision", e);
            }
        });
    }

    private String getEffectiveSystemPrompt() {
        if (activePreset == ModerationPreset.CUSTOM && !customSystemPrompt.isEmpty()) {
            return customSystemPrompt;
        }
        return activePreset.getSystemPrompt();
    }

    // === Configuration Methods ===

    public void setPreset(ModerationPreset preset) {
        this.activePreset = preset;
        if (preset != ModerationPreset.CUSTOM) {
            this.confidenceThreshold = preset.getDefaultConfidenceThreshold();
            this.enabledContentTypes.clear();
            this.enabledContentTypes.addAll(preset.getDefaultContentTypes());
        }
    }

    public void setCustomSystemPrompt(String prompt) {
        this.customSystemPrompt = prompt;
    }

    public void setConfidenceThreshold(double threshold) {
        this.confidenceThreshold = Math.max(0.0, Math.min(1.0, threshold));
    }

    public void setContentTypeEnabled(ContentType type, boolean enabled) {
        if (enabled) {
            enabledContentTypes.add(type);
        } else {
            enabledContentTypes.remove(type);
        }
    }

    public EscalationManager getEscalationManager() { return escalationManager; }
    public MessageContextWindow getContextWindow() { return contextWindow; }
    public void setContextEnabled(boolean enabled) { this.contextEnabled = enabled; }
    public boolean isContextEnabled() { return contextEnabled; }
    public void setContextMessageCount(int count) { this.contextMessageCount = Math.max(1, Math.min(50, count)); }
    public int getContextMessageCount() { return contextMessageCount; }
    public ModerationReviewQueue getReviewQueue() { return reviewQueue; }
    public boolean isDryRunMode() { return dryRunMode; }
    public void setDryRunMode(boolean dryRunMode) { this.dryRunMode = dryRunMode; }
    public ModerationAnalytics getAnalytics() { return analytics; }
    public DiscordWebhook getDiscordWebhook() { return discordWebhook; }
    public SkinScanner getSkinScanner() { return skinScanner; }

    public void updateFromJson(JsonObject json) {
        if (json.has("preset")) {
            try {
                setPreset(ModerationPreset.valueOf(json.get("preset").getAsString()));
            } catch (IllegalArgumentException ignored) {}
        }
        if (json.has("confidenceThreshold")) {
            setConfidenceThreshold(json.get("confidenceThreshold").getAsDouble());
        }
        if (json.has("customSystemPrompt")) {
            setCustomSystemPrompt(json.get("customSystemPrompt").getAsString());
        }
        if (json.has("contentTypes")) {
            JsonObject types = json.getAsJsonObject("contentTypes");
            for (ContentType ct : ContentType.values()) {
                if (types.has(ct.name())) {
                    setContentTypeEnabled(ct, types.get(ct.name()).getAsBoolean());
                }
            }
        }
        if (json.has("contextEnabled")) {
            setContextEnabled(json.get("contextEnabled").getAsBoolean());
        }
        if (json.has("contextMessageCount")) {
            setContextMessageCount(json.get("contextMessageCount").getAsInt());
        }
        if (json.has("dryRunMode")) {
            setDryRunMode(json.get("dryRunMode").getAsBoolean());
        }
        if (json.has("escalationTiers")) {
            JsonArray tiersArr = json.getAsJsonArray("escalationTiers");
            List<EscalationManager.EscalationTier> newTiers = new ArrayList<>();
            for (var element : tiersArr) {
                JsonObject t = element.getAsJsonObject();
                newTiers.add(new EscalationManager.EscalationTier(
                        t.get("name").getAsString(),
                        t.get("threshold").getAsInt(),
                        ModerationAction.valueOf(t.get("action").getAsString()),
                        t.has("punishmentType") && !t.get("punishmentType").getAsString().isEmpty()
                                ? t.get("punishmentType").getAsString() : null,
                        t.has("durationSeconds") ? t.get("durationSeconds").getAsLong() : 0
                ));
            }
            if (!newTiers.isEmpty()) {
                escalationManager.updateTiers(newTiers);
            }
        }
        if (json.has("discord")) {
            discordWebhook.updateFromJson(json.getAsJsonObject("discord"));
        }
        if (json.has("skinScanner")) {
            skinScanner.updateFromJson(json.getAsJsonObject("skinScanner"));
        }
    }

    public JsonObject toStatusJson() {
        JsonObject status = new JsonObject();
        status.addProperty("preset", activePreset.name());
        status.addProperty("presetName", activePreset.getDisplayName());
        status.addProperty("confidenceThreshold", confidenceThreshold);
        status.addProperty("customSystemPrompt", customSystemPrompt);

        JsonObject types = new JsonObject();
        for (ContentType ct : ContentType.values()) {
            types.addProperty(ct.name(), enabledContentTypes.contains(ct));
        }
        status.add("contentTypes", types);

        // Available presets
        JsonArray presets = new JsonArray();
        for (ModerationPreset preset : ModerationPreset.values()) {
            JsonObject p = new JsonObject();
            p.addProperty("id", preset.name());
            p.addProperty("name", preset.getDisplayName());
            p.addProperty("description", preset.getDescription());
            p.addProperty("defaultThreshold", preset.getDefaultConfidenceThreshold());
            presets.add(p);
        }
        status.add("presets", presets);

        // Available content types
        JsonArray contentTypeList = new JsonArray();
        for (ContentType ct : ContentType.values()) {
            JsonObject c = new JsonObject();
            c.addProperty("id", ct.name());
            c.addProperty("name", ct.getDisplayName());
            c.addProperty("description", ct.getDescription());
            c.addProperty("enabled", enabledContentTypes.contains(ct));
            contentTypeList.add(c);
        }
        status.add("availableContentTypes", contentTypeList);

        // Escalation tiers
        status.add("escalation", escalationManager.toJson());

        // Context window settings
        JsonObject contextSettings = new JsonObject();
        contextSettings.addProperty("enabled", contextEnabled);
        contextSettings.addProperty("messageCount", contextMessageCount);
        status.add("contextWindow", contextSettings);

        // Dry-run mode and review queue
        status.addProperty("dryRunMode", dryRunMode);
        status.add("reviewQueue", reviewQueue.toJson());

        // Violation categories
        JsonArray categories = new JsonArray();
        for (ViolationCategory cat : ViolationCategory.values()) {
            if (cat == ViolationCategory.NONE) continue;
            JsonObject c = new JsonObject();
            c.addProperty("id", cat.name());
            c.addProperty("name", cat.getDisplayName());
            c.addProperty("description", cat.getDescription());
            c.addProperty("defaultSeverity", cat.getDefaultSeverity().name());
            c.addProperty("severityLevel", cat.getDefaultSeverity().getLevel());
            categories.add(c);
        }
        status.add("violationCategories", categories);

        // Statistics
        JsonObject stats = new JsonObject();
        stats.addProperty("totalChecked", totalChecked.get());
        stats.addProperty("totalBlocked", totalBlocked.get());
        stats.addProperty("totalWarned", totalWarned.get());
        stats.addProperty("totalFlagged", totalFlagged.get());

        var aiClient = plugin.getAIClient();
        stats.addProperty("aiAvailable", aiClient != null && aiClient.isAvailable());
        stats.addProperty("model", "ModereX AI");
        stats.addProperty("moderationCallsRemaining",
                aiClient != null ? aiClient.getModerationCallsRemaining() : 0);
        stats.addProperty("moderationCallsUsed",
                aiClient != null ? 30 - aiClient.getModerationCallsRemaining() : 0);
        stats.addProperty("moderationCallsMax", 30);
        status.add("stats", stats);

        // Discord webhook status
        status.add("discord", discordWebhook.toJson());

        // Skin scanner status
        status.add("skinScanner", skinScanner.toStatusJson());

        return status;
    }

    /**
     * Get recent moderation log entries for the panel.
     */
    public CompletableFuture<JsonObject> getLogEntries(int page, int pageSize) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                int offset = page * pageSize;
                JsonObject result = new JsonObject();

                // Get total count
                Integer total = plugin.getDatabaseManager().query(
                        "SELECT COUNT(*) as cnt FROM moderex_ai_moderation_log",
                        rs -> rs.next() ? rs.getInt("cnt") : 0);
                result.addProperty("total", total != null ? total : 0);

                // Get entries
                JsonArray entries = new JsonArray();
                plugin.getDatabaseManager().query("""
                    SELECT * FROM moderex_ai_moderation_log
                    ORDER BY created_at DESC LIMIT ? OFFSET ?
                """, rs -> {
                    while (rs.next()) {
                        JsonObject entry = new JsonObject();
                        entry.addProperty("id", rs.getInt("id"));
                        entry.addProperty("playerUuid", rs.getString("player_uuid"));
                        entry.addProperty("playerName", rs.getString("player_name"));
                        entry.addProperty("contentType", rs.getString("content_type"));
                        entry.addProperty("content", rs.getString("content"));
                        entry.addProperty("action", rs.getString("action"));
                        entry.addProperty("confidence", rs.getDouble("confidence"));
                        entry.addProperty("reason", rs.getString("reason"));
                        entry.addProperty("category", rs.getString("category"));
                        entry.addProperty("createdAt", rs.getLong("created_at"));
                        entries.add(entry);
                    }
                    return null;
                }, pageSize, offset);

                result.add("entries", entries);
                return result;
            } catch (SQLException e) {
                plugin.logError("Failed to get AI moderation log", e);
                JsonObject empty = new JsonObject();
                empty.add("entries", new JsonArray());
                empty.addProperty("total", 0);
                return empty;
            }
        });
    }

    private record PendingModeration(Player player, String content, ContentType contentType,
                                     java.util.function.Consumer<ModerationResult> callback) {
    }
}
