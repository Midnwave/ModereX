package com.blockforge.moderex.automod;

import com.blockforge.moderex.punishment.PunishmentType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AutomodRule {

    private static final Gson GSON = new Gson();

    private String id;
    private String name;
    private RuleType type;
    private boolean enabled = true;
    private boolean builtIn = false;
    private int priority = 0;

    // Word filter specific
    private List<String> blacklistedWords = new ArrayList<>();
    private List<String> exclusionWords = new ArrayList<>();
    private boolean exactMatch = true;

    // Auto punishment
    private AutoPunishment autoPunishment;

    // Violation tracking
    private final Map<UUID, Integer> violations = new ConcurrentHashMap<>();
    private final Map<UUID, Long> violationTimes = new ConcurrentHashMap<>();

    public enum RuleType {
        WORD_FILTER,
        SPAM,
        CAPS,
        ANTICHEAT
    }

    public AutomodRule() {
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RuleType getType() {
        return type;
    }

    public void setType(RuleType type) {
        this.type = type;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isBuiltIn() {
        return builtIn;
    }

    public void setBuiltIn(boolean builtIn) {
        this.builtIn = builtIn;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public List<String> getBlacklistedWords() {
        return blacklistedWords;
    }

    public void setBlacklistedWords(List<String> blacklistedWords) {
        this.blacklistedWords = blacklistedWords;
    }

    public void addBlacklistedWord(String word) {
        if (!blacklistedWords.contains(word.toLowerCase())) {
            blacklistedWords.add(word.toLowerCase());
        }
    }

    public void removeBlacklistedWord(String word) {
        blacklistedWords.remove(word.toLowerCase());
    }

    public List<String> getExclusionWords() {
        return exclusionWords;
    }

    public void setExclusionWords(List<String> exclusionWords) {
        this.exclusionWords = exclusionWords;
    }

    public void addExclusionWord(String word) {
        if (!exclusionWords.contains(word.toLowerCase())) {
            exclusionWords.add(word.toLowerCase());
        }
    }

    public void removeExclusionWord(String word) {
        exclusionWords.remove(word.toLowerCase());
    }

    public boolean isExactMatch() {
        return exactMatch;
    }

    public void setExactMatch(boolean exactMatch) {
        this.exactMatch = exactMatch;
    }

    public AutoPunishment getAutoPunishment() {
        return autoPunishment;
    }

    public void setAutoPunishment(AutoPunishment autoPunishment) {
        this.autoPunishment = autoPunishment;
    }

    public int incrementViolation(UUID uuid) {
        // Clean old violations (older than 1 hour)
        long now = System.currentTimeMillis();
        if (autoPunishment != null && autoPunishment.getTimeWindow() > 0) {
            Long lastTime = violationTimes.get(uuid);
            if (lastTime != null && now - lastTime > autoPunishment.getTimeWindow()) {
                violations.remove(uuid);
            }
        }

        violationTimes.put(uuid, now);
        return violations.merge(uuid, 1, Integer::sum);
    }

    public void resetViolations(UUID uuid) {
        violations.remove(uuid);
        violationTimes.remove(uuid);
    }

    public int getViolationCount(UUID uuid) {
        return violations.getOrDefault(uuid, 0);
    }

    public String toConfigString() {
        JsonObject json = new JsonObject();
        json.add("blacklistedWords", GSON.toJsonTree(blacklistedWords));
        json.add("exclusionWords", GSON.toJsonTree(exclusionWords));
        json.addProperty("exactMatch", exactMatch);

        if (autoPunishment != null) {
            JsonObject punishment = new JsonObject();
            punishment.addProperty("enabled", autoPunishment.enabled);
            punishment.addProperty("type", autoPunishment.type.name());
            punishment.addProperty("duration", autoPunishment.duration);
            punishment.addProperty("triggerCount", autoPunishment.triggerCount);
            punishment.addProperty("timeWindow", autoPunishment.timeWindow);
            json.add("autoPunishment", punishment);
        }

        return GSON.toJson(json);
    }

    public void loadConfig(String configJson) {
        try {
            JsonObject json = GSON.fromJson(configJson, JsonObject.class);

            if (json.has("blacklistedWords")) {
                blacklistedWords = new ArrayList<>();
                json.getAsJsonArray("blacklistedWords").forEach(e -> blacklistedWords.add(e.getAsString()));
            }

            if (json.has("exclusionWords")) {
                exclusionWords = new ArrayList<>();
                json.getAsJsonArray("exclusionWords").forEach(e -> exclusionWords.add(e.getAsString()));
            }

            if (json.has("exactMatch")) {
                exactMatch = json.get("exactMatch").getAsBoolean();
            }

            if (json.has("autoPunishment")) {
                JsonObject punishment = json.getAsJsonObject("autoPunishment");
                autoPunishment = new AutoPunishment();
                autoPunishment.enabled = punishment.get("enabled").getAsBoolean();
                autoPunishment.type = PunishmentType.valueOf(punishment.get("type").getAsString());
                autoPunishment.duration = punishment.get("duration").getAsLong();
                autoPunishment.triggerCount = punishment.get("triggerCount").getAsInt();
                autoPunishment.timeWindow = punishment.get("timeWindow").getAsLong();
            }
        } catch (Exception e) {
            // Invalid config, use defaults
        }
    }

    public static class AutoPunishment {
        private boolean enabled;
        private PunishmentType type;
        private long duration; // -1 for permanent
        private int triggerCount; // Number of violations before punishing
        private long timeWindow; // Time window for counting violations (ms)

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public PunishmentType getType() {
            return type;
        }

        public void setType(PunishmentType type) {
            this.type = type;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public int getTriggerCount() {
            return triggerCount;
        }

        public void setTriggerCount(int triggerCount) {
            this.triggerCount = triggerCount;
        }

        public long getTimeWindow() {
            return timeWindow;
        }

        public void setTimeWindow(long timeWindow) {
            this.timeWindow = timeWindow;
        }
    }
}
