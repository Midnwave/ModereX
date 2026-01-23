package com.blockforge.moderex.automod;

import com.blockforge.moderex.punishment.PunishmentType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents an automod rule with comprehensive configuration options.
 * Supports spam protection, caps filter, word filter, anticheat integration,
 * AFK detection, and nickname moderation.
 */
public class AutomodRule {

    private static final Gson GSON = new Gson();

    private String id;
    private String name;
    private String description;
    private RuleType type;
    private boolean enabled = true;
    private boolean builtIn = false;
    private int priority = 0;

    // === Spam Protection Settings ===
    private int spamMessageCount = 3;           // Messages before flagging
    private int spamTimeWindowSeconds = 5;      // Time window in seconds
    private boolean spamDetectSimilar = true;   // Detect similar messages
    private double spamSimilarityThreshold = 0.8; // 80% similarity threshold

    // === Caps Filter Settings ===
    private int capsMaxPercentage = 70;         // Max % of caps allowed
    private int capsMinLength = 10;             // Min message length to check

    // === Word Filter Settings ===
    private List<String> blacklistedPhrases = new ArrayList<>();
    private List<String> exclusionPhrases = new ArrayList<>();
    private FilterMode filterMode = FilterMode.CONTAINS_PHRASE;
    private boolean applyToNicknames = false;   // Apply filter to nicknames
    private boolean nicknameOnly = false;       // ONLY apply to nicknames, not chat

    // === Anticheat Settings ===
    private String anticheatName;               // e.g., "grim", "vulcan"
    private String checkName;                   // e.g., "speed", "fly"
    private int anticheatAlertThreshold = 5;    // Alerts before flagging
    private int anticheatTimeWindowSeconds = 60;

    // === AFK Settings ===
    private int afkTimeoutMinutes = 15;         // Minutes before kick
    private boolean afkKickEnabled = false;

    // === Replay Recording Settings ===
    private boolean recordReplay = false;       // Whether to record a replay when rule triggers
    private int replayDurationSeconds = 60;     // Duration to record (10-300 seconds)

    // === Auto Punishment Settings ===
    private AutoPunishment autoPunishment;
    private String punishmentTemplateId;        // Use preset template

    // === Action on Flag ===
    private FlagAction flagAction = FlagAction.BLOCK;
    private String flagMessage;                 // Custom message to show

    // Violation tracking
    private final Map<UUID, List<Long>> violationTimestamps = new ConcurrentHashMap<>();
    private final Map<UUID, String> lastMessages = new ConcurrentHashMap<>();

    public enum RuleType {
        SPAM_PROTECTION,    // Built-in spam detection
        CAPS_FILTER,        // Built-in caps filter
        WORD_FILTER,        // Custom word/phrase filter
        LINK_FILTER,        // Built-in link detection
        ANTICHEAT,          // Per-check anticheat rules
        AFK_KICK,           // Auto AFK kick
        NICKNAME            // Nickname moderation
    }

    public enum FilterMode {
        CONTAINS_PHRASE,    // Message contains the phrase anywhere
        EXACT_MESSAGE,      // Entire message matches exactly
        REGEX               // Regular expression match
    }

    public enum FlagAction {
        BLOCK,              // Block the message
        WARN,               // Allow but warn
        MODIFY,             // Modify the message (e.g., censor)
        LOG_ONLY            // Just log, don't take action
    }

    public AutomodRule() {
    }

    /**
     * Create a built-in rule with preset configuration.
     */
    public static AutomodRule createBuiltIn(String id, String name, RuleType type) {
        AutomodRule rule = new AutomodRule();
        rule.setId(id);
        rule.setName(name);
        rule.setType(type);
        rule.setBuiltIn(true);
        rule.setEnabled(true);
        return rule;
    }

    // === Violation Tracking ===

    /**
     * Record a violation and return the count within the time window.
     */
    public int recordViolation(UUID uuid) {
        long now = System.currentTimeMillis();
        long windowMs = getTimeWindowMs();

        List<Long> timestamps = violationTimestamps.computeIfAbsent(uuid, k -> new ArrayList<>());

        // Remove old violations outside window
        timestamps.removeIf(t -> now - t > windowMs);

        // Add new violation
        timestamps.add(now);

        return timestamps.size();
    }

    /**
     * Get violation count within the time window.
     */
    public int getViolationCount(UUID uuid) {
        long now = System.currentTimeMillis();
        long windowMs = getTimeWindowMs();

        List<Long> timestamps = violationTimestamps.get(uuid);
        if (timestamps == null) return 0;

        // Count only violations within window
        return (int) timestamps.stream().filter(t -> now - t <= windowMs).count();
    }

    /**
     * Reset violations for a player.
     */
    public void resetViolations(UUID uuid) {
        violationTimestamps.remove(uuid);
        lastMessages.remove(uuid);
    }

    /**
     * Check if a message is similar to the last message.
     */
    public boolean isSimilarToLast(UUID uuid, String message) {
        String normalized = normalizeMessage(message);
        String last = lastMessages.get(uuid);

        if (last == null) {
            lastMessages.put(uuid, normalized);
            return false;
        }

        double similarity = calculateSimilarity(last, normalized);
        lastMessages.put(uuid, normalized);

        return similarity >= spamSimilarityThreshold;
    }

    /**
     * Normalize a message for comparison.
     */
    private String normalizeMessage(String message) {
        return message.toLowerCase()
                .replaceAll("\\s+", " ")
                .replaceAll("[^a-z0-9 ]", "")
                .trim();
    }

    /**
     * Calculate similarity between two strings using Levenshtein distance.
     */
    private double calculateSimilarity(String s1, String s2) {
        if (s1.equals(s2)) return 1.0;
        if (s1.isEmpty() || s2.isEmpty()) return 0.0;

        int maxLen = Math.max(s1.length(), s2.length());
        int distance = levenshteinDistance(s1, s2);

        return 1.0 - ((double) distance / maxLen);
    }

    /**
     * Calculate Levenshtein distance between two strings.
     */
    private int levenshteinDistance(String s1, String s2) {
        int[] prev = new int[s2.length() + 1];
        int[] curr = new int[s2.length() + 1];

        for (int j = 0; j <= s2.length(); j++) {
            prev[j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            curr[0] = i;
            for (int j = 1; j <= s2.length(); j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                curr[j] = Math.min(Math.min(curr[j - 1] + 1, prev[j] + 1), prev[j - 1] + cost);
            }
            int[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return prev[s2.length()];
    }

    /**
     * Get the time window in milliseconds based on rule type.
     */
    private long getTimeWindowMs() {
        return switch (type) {
            case SPAM_PROTECTION -> spamTimeWindowSeconds * 1000L;
            case ANTICHEAT -> anticheatTimeWindowSeconds * 1000L;
            default -> autoPunishment != null ? autoPunishment.getTimeWindow() : 60000L;
        };
    }

    // === Serialization ===

    /**
     * Serialize rule configuration to JSON.
     */
    public String toConfigJson() {
        JsonObject json = new JsonObject();

        json.addProperty("type", type != null ? type.name() : null);
        json.addProperty("description", description);
        json.addProperty("priority", priority);

        // Spam settings
        json.addProperty("spamMessageCount", spamMessageCount);
        json.addProperty("spamTimeWindowSeconds", spamTimeWindowSeconds);
        json.addProperty("spamDetectSimilar", spamDetectSimilar);
        json.addProperty("spamSimilarityThreshold", spamSimilarityThreshold);

        // Caps settings
        json.addProperty("capsMaxPercentage", capsMaxPercentage);
        json.addProperty("capsMinLength", capsMinLength);

        // Word filter settings
        json.add("blacklistedPhrases", GSON.toJsonTree(blacklistedPhrases));
        json.add("exclusionPhrases", GSON.toJsonTree(exclusionPhrases));
        json.addProperty("filterMode", filterMode != null ? filterMode.name() : null);
        json.addProperty("applyToNicknames", applyToNicknames);
        json.addProperty("nicknameOnly", nicknameOnly);

        // Anticheat settings
        json.addProperty("anticheatName", anticheatName);
        json.addProperty("checkName", checkName);
        json.addProperty("anticheatAlertThreshold", anticheatAlertThreshold);
        json.addProperty("anticheatTimeWindowSeconds", anticheatTimeWindowSeconds);

        // AFK settings
        json.addProperty("afkTimeoutMinutes", afkTimeoutMinutes);
        json.addProperty("afkKickEnabled", afkKickEnabled);

        // Replay recording settings
        json.addProperty("recordReplay", recordReplay);
        json.addProperty("replayDurationSeconds", replayDurationSeconds);

        // Flag action
        json.addProperty("flagAction", flagAction != null ? flagAction.name() : null);
        json.addProperty("flagMessage", flagMessage);
        json.addProperty("punishmentTemplateId", punishmentTemplateId);

        // Auto punishment
        if (autoPunishment != null) {
            JsonObject punishment = new JsonObject();
            punishment.addProperty("enabled", autoPunishment.enabled);
            punishment.addProperty("type", autoPunishment.type != null ? autoPunishment.type.name() : null);
            punishment.addProperty("duration", autoPunishment.duration);
            punishment.addProperty("reason", autoPunishment.reason);
            punishment.addProperty("triggerCount", autoPunishment.triggerCount);
            punishment.addProperty("timeWindow", autoPunishment.timeWindow);
            json.add("autoPunishment", punishment);
        }

        return GSON.toJson(json);
    }

    /**
     * Load rule configuration from JSON.
     */
    public void loadConfigJson(String configJson) {
        if (configJson == null || configJson.isEmpty()) return;

        try {
            JsonObject json = GSON.fromJson(configJson, JsonObject.class);

            if (json.has("type") && !json.get("type").isJsonNull()) {
                type = RuleType.valueOf(json.get("type").getAsString());
            }
            if (json.has("description")) description = json.get("description").getAsString();
            if (json.has("priority")) priority = json.get("priority").getAsInt();

            // Spam settings
            if (json.has("spamMessageCount")) spamMessageCount = json.get("spamMessageCount").getAsInt();
            if (json.has("spamTimeWindowSeconds")) spamTimeWindowSeconds = json.get("spamTimeWindowSeconds").getAsInt();
            if (json.has("spamDetectSimilar")) spamDetectSimilar = json.get("spamDetectSimilar").getAsBoolean();
            if (json.has("spamSimilarityThreshold")) spamSimilarityThreshold = json.get("spamSimilarityThreshold").getAsDouble();

            // Caps settings
            if (json.has("capsMaxPercentage")) capsMaxPercentage = json.get("capsMaxPercentage").getAsInt();
            if (json.has("capsMinLength")) capsMinLength = json.get("capsMinLength").getAsInt();

            // Word filter settings
            if (json.has("blacklistedPhrases")) {
                blacklistedPhrases = new ArrayList<>();
                json.getAsJsonArray("blacklistedPhrases").forEach(e -> blacklistedPhrases.add(e.getAsString()));
            }
            if (json.has("exclusionPhrases")) {
                exclusionPhrases = new ArrayList<>();
                json.getAsJsonArray("exclusionPhrases").forEach(e -> exclusionPhrases.add(e.getAsString()));
            }
            if (json.has("filterMode") && !json.get("filterMode").isJsonNull()) {
                filterMode = FilterMode.valueOf(json.get("filterMode").getAsString());
            }
            if (json.has("applyToNicknames")) applyToNicknames = json.get("applyToNicknames").getAsBoolean();
            if (json.has("nicknameOnly")) nicknameOnly = json.get("nicknameOnly").getAsBoolean();

            // Anticheat settings
            if (json.has("anticheatName")) anticheatName = json.get("anticheatName").getAsString();
            if (json.has("checkName")) checkName = json.get("checkName").getAsString();
            if (json.has("anticheatAlertThreshold")) anticheatAlertThreshold = json.get("anticheatAlertThreshold").getAsInt();
            if (json.has("anticheatTimeWindowSeconds")) anticheatTimeWindowSeconds = json.get("anticheatTimeWindowSeconds").getAsInt();

            // AFK settings
            if (json.has("afkTimeoutMinutes")) afkTimeoutMinutes = json.get("afkTimeoutMinutes").getAsInt();
            if (json.has("afkKickEnabled")) afkKickEnabled = json.get("afkKickEnabled").getAsBoolean();

            // Replay recording settings
            if (json.has("recordReplay")) recordReplay = json.get("recordReplay").getAsBoolean();
            if (json.has("replayDurationSeconds")) replayDurationSeconds = json.get("replayDurationSeconds").getAsInt();

            // Flag action
            if (json.has("flagAction") && !json.get("flagAction").isJsonNull()) {
                flagAction = FlagAction.valueOf(json.get("flagAction").getAsString());
            }
            if (json.has("flagMessage")) flagMessage = json.get("flagMessage").getAsString();
            if (json.has("punishmentTemplateId")) punishmentTemplateId = json.get("punishmentTemplateId").getAsString();

            // Auto punishment
            if (json.has("autoPunishment") && !json.get("autoPunishment").isJsonNull()) {
                JsonObject p = json.getAsJsonObject("autoPunishment");
                autoPunishment = new AutoPunishment();
                if (p.has("enabled")) autoPunishment.enabled = p.get("enabled").getAsBoolean();
                if (p.has("type") && !p.get("type").isJsonNull()) {
                    autoPunishment.type = PunishmentType.valueOf(p.get("type").getAsString());
                }
                if (p.has("duration")) autoPunishment.duration = p.get("duration").getAsLong();
                if (p.has("reason")) autoPunishment.reason = p.get("reason").getAsString();
                if (p.has("triggerCount")) autoPunishment.triggerCount = p.get("triggerCount").getAsInt();
                if (p.has("timeWindow")) autoPunishment.timeWindow = p.get("timeWindow").getAsLong();
            }
        } catch (Exception e) {
            // Invalid config, use defaults
        }
    }

    // Legacy compatibility
    public String toConfigString() {
        return toConfigJson();
    }

    public void loadConfig(String configJson) {
        loadConfigJson(configJson);
    }

    // === Getters and Setters ===

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public RuleType getType() { return type; }
    public void setType(RuleType type) { this.type = type; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isBuiltIn() { return builtIn; }
    public void setBuiltIn(boolean builtIn) { this.builtIn = builtIn; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    // Spam settings
    public int getSpamMessageCount() { return spamMessageCount; }
    public void setSpamMessageCount(int spamMessageCount) { this.spamMessageCount = spamMessageCount; }

    public int getSpamTimeWindowSeconds() { return spamTimeWindowSeconds; }
    public void setSpamTimeWindowSeconds(int spamTimeWindowSeconds) { this.spamTimeWindowSeconds = spamTimeWindowSeconds; }

    public boolean isSpamDetectSimilar() { return spamDetectSimilar; }
    public void setSpamDetectSimilar(boolean spamDetectSimilar) { this.spamDetectSimilar = spamDetectSimilar; }

    public double getSpamSimilarityThreshold() { return spamSimilarityThreshold; }
    public void setSpamSimilarityThreshold(double spamSimilarityThreshold) { this.spamSimilarityThreshold = spamSimilarityThreshold; }

    // Caps settings
    public int getCapsMaxPercentage() { return capsMaxPercentage; }
    public void setCapsMaxPercentage(int capsMaxPercentage) { this.capsMaxPercentage = capsMaxPercentage; }

    public int getCapsMinLength() { return capsMinLength; }
    public void setCapsMinLength(int capsMinLength) { this.capsMinLength = capsMinLength; }

    // Word filter settings
    public List<String> getBlacklistedPhrases() { return blacklistedPhrases; }
    public void setBlacklistedPhrases(List<String> blacklistedPhrases) { this.blacklistedPhrases = blacklistedPhrases; }

    public List<String> getExclusionPhrases() { return exclusionPhrases; }
    public void setExclusionPhrases(List<String> exclusionPhrases) { this.exclusionPhrases = exclusionPhrases; }

    public FilterMode getFilterMode() { return filterMode; }
    public void setFilterMode(FilterMode filterMode) { this.filterMode = filterMode; }

    public boolean isApplyToNicknames() { return applyToNicknames; }
    public void setApplyToNicknames(boolean applyToNicknames) { this.applyToNicknames = applyToNicknames; }

    public boolean isNicknameOnly() { return nicknameOnly; }
    public void setNicknameOnly(boolean nicknameOnly) { this.nicknameOnly = nicknameOnly; }

    // Legacy compatibility
    public List<String> getBlacklistedWords() { return blacklistedPhrases; }
    public void setBlacklistedWords(List<String> words) { this.blacklistedPhrases = words; }
    public List<String> getExclusionWords() { return exclusionPhrases; }
    public void setExclusionWords(List<String> words) { this.exclusionPhrases = words; }
    public boolean isExactMatch() { return filterMode == FilterMode.EXACT_MESSAGE; }
    public void setExactMatch(boolean exact) { this.filterMode = exact ? FilterMode.EXACT_MESSAGE : FilterMode.CONTAINS_PHRASE; }

    // Anticheat settings
    public String getAnticheatName() { return anticheatName; }
    public void setAnticheatName(String anticheatName) { this.anticheatName = anticheatName; }

    public String getCheckName() { return checkName; }
    public void setCheckName(String checkName) { this.checkName = checkName; }

    public int getAnticheatAlertThreshold() { return anticheatAlertThreshold; }
    public void setAnticheatAlertThreshold(int threshold) { this.anticheatAlertThreshold = threshold; }

    public int getAnticheatTimeWindowSeconds() { return anticheatTimeWindowSeconds; }
    public void setAnticheatTimeWindowSeconds(int seconds) { this.anticheatTimeWindowSeconds = seconds; }

    // AFK settings
    public int getAfkTimeoutMinutes() { return afkTimeoutMinutes; }
    public void setAfkTimeoutMinutes(int minutes) { this.afkTimeoutMinutes = minutes; }

    public boolean isAfkKickEnabled() { return afkKickEnabled; }
    public void setAfkKickEnabled(boolean enabled) { this.afkKickEnabled = enabled; }

    // Replay recording settings
    public boolean isRecordReplay() { return recordReplay; }
    public void setRecordReplay(boolean recordReplay) { this.recordReplay = recordReplay; }

    public int getReplayDurationSeconds() { return replayDurationSeconds; }
    public void setReplayDurationSeconds(int seconds) { this.replayDurationSeconds = Math.max(10, Math.min(300, seconds)); }

    // Punishment settings
    public AutoPunishment getAutoPunishment() { return autoPunishment; }
    public void setAutoPunishment(AutoPunishment autoPunishment) { this.autoPunishment = autoPunishment; }

    public String getPunishmentTemplateId() { return punishmentTemplateId; }
    public void setPunishmentTemplateId(String templateId) { this.punishmentTemplateId = templateId; }

    public FlagAction getFlagAction() { return flagAction; }
    public void setFlagAction(FlagAction flagAction) { this.flagAction = flagAction; }

    public String getFlagMessage() { return flagMessage; }
    public void setFlagMessage(String flagMessage) { this.flagMessage = flagMessage; }

    // Legacy compatibility
    public int incrementViolation(UUID uuid) { return recordViolation(uuid); }

    /**
     * Auto punishment configuration.
     */
    public static class AutoPunishment {
        private boolean enabled;
        private PunishmentType type;
        private long duration;          // -1 for permanent
        private String reason;
        private int triggerCount;       // Violations before punishing
        private long timeWindow;        // Time window in ms

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public PunishmentType getType() { return type; }
        public void setType(PunishmentType type) { this.type = type; }

        public long getDuration() { return duration; }
        public void setDuration(long duration) { this.duration = duration; }

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }

        public int getTriggerCount() { return triggerCount; }
        public void setTriggerCount(int triggerCount) { this.triggerCount = triggerCount; }

        public long getTimeWindow() { return timeWindow; }
        public void setTimeWindow(long timeWindow) { this.timeWindow = timeWindow; }
    }
}
