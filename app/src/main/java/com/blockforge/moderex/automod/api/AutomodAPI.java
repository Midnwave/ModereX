package com.blockforge.moderex.automod.api;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.automod.AutomodManager;
import com.blockforge.moderex.automod.AutomodRule;
import com.blockforge.moderex.punishment.PunishmentType;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Public API for other plugins to interact with ModereX's automod system.
 * This API allows external plugins to:
 * - Check and modify chat filter rules
 * - Configure spam protection settings
 * - Configure caps filter settings
 * - Create and manage custom rules
 * - Process messages through filters
 * - Listen to automod events
 *
 * @since 1.0.0
 */
public class AutomodAPI {

    private final ModereX plugin;

    public AutomodAPI(ModereX plugin) {
        this.plugin = plugin;
    }

    // ==================== SPAM PROTECTION ====================

    /**
     * Check if spam protection is enabled.
     *
     * @return true if spam protection is enabled
     */
    public boolean isSpamProtectionEnabled() {
        return plugin.getAutomodManager().isSpamPreventionEnabled();
    }

    /**
     * Enable or disable spam protection.
     *
     * @param enabled true to enable, false to disable
     */
    public void setSpamProtectionEnabled(boolean enabled) {
        plugin.getAutomodManager().setSpamPreventionEnabled(enabled);
    }

    /**
     * Get the spam protection configuration.
     *
     * @return SpamConfig with current settings
     */
    public SpamConfig getSpamConfig() {
        AutomodRule rule = plugin.getAutomodManager().getRule("spam_protection");
        if (rule == null) return new SpamConfig(3, 5, true, 0.8);

        return new SpamConfig(
            rule.getSpamMessageCount(),
            rule.getSpamTimeWindowSeconds(),
            rule.isSpamDetectSimilar(),
            rule.getSpamSimilarityThreshold()
        );
    }

    /**
     * Configure spam protection settings.
     *
     * @param config The spam configuration to apply
     */
    public void setSpamConfig(SpamConfig config) {
        AutomodRule rule = plugin.getAutomodManager().getRule("spam_protection");
        if (rule == null) return;

        rule.setSpamMessageCount(config.messageCount());
        rule.setSpamTimeWindowSeconds(config.timeWindowSeconds());
        rule.setSpamDetectSimilar(config.detectSimilar());
        rule.setSpamSimilarityThreshold(config.similarityThreshold());
        plugin.getAutomodManager().saveRule(rule);
    }

    /**
     * Spam protection configuration record.
     *
     * @param messageCount Number of messages before triggering (default: 3)
     * @param timeWindowSeconds Time window in seconds (default: 5)
     * @param detectSimilar Whether to detect similar/duplicate messages (default: true)
     * @param similarityThreshold Similarity threshold 0.0-1.0 (default: 0.8 = 80%)
     */
    public record SpamConfig(int messageCount, int timeWindowSeconds, boolean detectSimilar, double similarityThreshold) {
        public SpamConfig {
            if (messageCount < 1) messageCount = 1;
            if (timeWindowSeconds < 1) timeWindowSeconds = 1;
            if (similarityThreshold < 0) similarityThreshold = 0;
            if (similarityThreshold > 1) similarityThreshold = 1;
        }
    }

    // ==================== CAPS FILTER ====================

    /**
     * Check if caps filter is enabled.
     *
     * @return true if caps filter is enabled
     */
    public boolean isCapsFilterEnabled() {
        return plugin.getAutomodManager().isCapsFilterEnabled();
    }

    /**
     * Enable or disable caps filter.
     *
     * @param enabled true to enable, false to disable
     */
    public void setCapsFilterEnabled(boolean enabled) {
        plugin.getAutomodManager().setCapsFilterEnabled(enabled);
    }

    /**
     * Get the caps filter configuration.
     *
     * @return CapsConfig with current settings
     */
    public CapsConfig getCapsConfig() {
        AutomodRule rule = plugin.getAutomodManager().getRule("caps_filter");
        if (rule == null) return new CapsConfig(70, 10);

        return new CapsConfig(
            rule.getCapsMaxPercentage(),
            rule.getCapsMinLength()
        );
    }

    /**
     * Configure caps filter settings.
     *
     * @param config The caps configuration to apply
     */
    public void setCapsConfig(CapsConfig config) {
        AutomodRule rule = plugin.getAutomodManager().getRule("caps_filter");
        if (rule == null) return;

        rule.setCapsMaxPercentage(config.maxPercentage());
        rule.setCapsMinLength(config.minLength());
        plugin.getAutomodManager().saveRule(rule);
    }

    /**
     * Caps filter configuration record.
     *
     * @param maxPercentage Maximum percentage of caps allowed (default: 70)
     * @param minLength Minimum message length to check (default: 10)
     */
    public record CapsConfig(int maxPercentage, int minLength) {
        public CapsConfig {
            if (maxPercentage < 0) maxPercentage = 0;
            if (maxPercentage > 100) maxPercentage = 100;
            if (minLength < 1) minLength = 1;
        }
    }

    // ==================== LINK FILTER ====================

    /**
     * Check if link filter is enabled.
     *
     * @return true if link filter is enabled
     */
    public boolean isLinkFilterEnabled() {
        AutomodRule rule = plugin.getAutomodManager().getRule("link_filter");
        return rule != null && rule.isEnabled();
    }

    /**
     * Enable or disable link filter.
     *
     * @param enabled true to enable, false to disable
     */
    public void setLinkFilterEnabled(boolean enabled) {
        AutomodRule rule = plugin.getAutomodManager().getRule("link_filter");
        if (rule != null) {
            rule.setEnabled(enabled);
            plugin.getAutomodManager().saveRule(rule);
        }
    }

    // ==================== RULE CREATION ====================

    /**
     * Create a new rule builder for flexible rule configuration.
     * Use this to create custom word filter or nickname rules with full control.
     *
     * @param name The rule name
     * @return A RuleBuilder for configuring the rule
     */
    public RuleBuilder createRule(String name) {
        return new RuleBuilder(plugin, name);
    }

    /**
     * Create a new word filter rule.
     *
     * @param name The rule name
     * @return The created rule's ID, or null if creation failed
     */
    public String createWordFilterRule(String name) {
        AutomodRule rule = plugin.getAutomodManager().createRule(name, AutomodRule.RuleType.WORD_FILTER);
        return rule != null ? rule.getId() : null;
    }

    /**
     * Create a word filter rule with specific phrases.
     *
     * @param name The rule name
     * @param phrases List of phrases to filter
     * @param exactMatch true for exact message matching, false for contains
     * @return The created rule's ID, or null if creation failed
     */
    public String createWordFilterRule(String name, List<String> phrases, boolean exactMatch) {
        AutomodRule rule = plugin.getAutomodManager().createRule(name, AutomodRule.RuleType.WORD_FILTER);
        if (rule == null) return null;

        rule.setBlacklistedPhrases(new ArrayList<>(phrases));
        rule.setExactMatch(exactMatch);
        plugin.getAutomodManager().saveRule(rule);

        return rule.getId();
    }

    /**
     * Create a new nickname filter rule.
     *
     * @param name The rule name
     * @return The created rule's ID, or null if creation failed
     */
    public String createNicknameRule(String name) {
        AutomodRule rule = plugin.getAutomodManager().createRule(name, AutomodRule.RuleType.NICKNAME);
        return rule != null ? rule.getId() : null;
    }

    /**
     * Create a nickname filter rule with specific phrases.
     *
     * @param name The rule name
     * @param phrases List of phrases to filter in nicknames
     * @return The created rule's ID, or null if creation failed
     */
    public String createNicknameRule(String name, List<String> phrases) {
        AutomodRule rule = plugin.getAutomodManager().createRule(name, AutomodRule.RuleType.NICKNAME);
        if (rule == null) return null;

        rule.setBlacklistedPhrases(new ArrayList<>(phrases));
        rule.setNicknameOnly(true);
        plugin.getAutomodManager().saveRule(rule);

        return rule.getId();
    }

    /**
     * Update a rule's name.
     *
     * @param ruleId The rule ID
     * @param newName The new name
     * @return true if the rule was found and updated
     */
    public boolean setRuleName(String ruleId, String newName) {
        AutomodRule rule = plugin.getAutomodManager().getRule(ruleId);
        if (rule == null || rule.isBuiltIn()) return false;

        rule.setName(newName);
        plugin.getAutomodManager().saveRule(rule);
        return true;
    }

    /**
     * Update a rule's description.
     *
     * @param ruleId The rule ID
     * @param description The new description
     * @return true if the rule was found and updated
     */
    public boolean setRuleDescription(String ruleId, String description) {
        AutomodRule rule = plugin.getAutomodManager().getRule(ruleId);
        if (rule == null) return false;

        rule.setDescription(description);
        plugin.getAutomodManager().saveRule(rule);
        return true;
    }

    /**
     * Set the filter mode for a word filter rule.
     *
     * @param ruleId The rule ID
     * @param mode The filter mode (CONTAINS_PHRASE, EXACT_MESSAGE, or REGEX)
     * @return true if the rule was found and updated
     */
    public boolean setRuleFilterMode(String ruleId, FilterMode mode) {
        AutomodRule rule = plugin.getAutomodManager().getRule(ruleId);
        if (rule == null) return false;

        rule.setFilterMode(AutomodRule.FilterMode.valueOf(mode.name()));
        plugin.getAutomodManager().saveRule(rule);
        return true;
    }

    /**
     * Set whether a rule should also apply to nicknames.
     *
     * @param ruleId The rule ID
     * @param applyToNicknames true to apply to nicknames
     * @return true if the rule was found and updated
     */
    public boolean setRuleApplyToNicknames(String ruleId, boolean applyToNicknames) {
        AutomodRule rule = plugin.getAutomodManager().getRule(ruleId);
        if (rule == null) return false;

        rule.setApplyToNicknames(applyToNicknames);
        plugin.getAutomodManager().saveRule(rule);
        return true;
    }

    /**
     * Set the action taken when a rule is triggered.
     *
     * @param ruleId The rule ID
     * @param action The flag action
     * @return true if the rule was found and updated
     */
    public boolean setRuleFlagAction(String ruleId, FlagAction action) {
        AutomodRule rule = plugin.getAutomodManager().getRule(ruleId);
        if (rule == null) return false;

        rule.setFlagAction(AutomodRule.FlagAction.valueOf(action.name()));
        plugin.getAutomodManager().saveRule(rule);
        return true;
    }

    // ==================== REPLAY RECORDING ====================

    /**
     * Set whether a rule should trigger replay recording when activated.
     * When enabled, the plugin will automatically start recording player
     * activity when the rule is triggered.
     *
     * @param ruleId The rule ID
     * @param enabled true to enable replay recording
     * @return true if the rule was found and updated
     */
    public boolean setRuleRecordReplay(String ruleId, boolean enabled) {
        AutomodRule rule = plugin.getAutomodManager().getRule(ruleId);
        if (rule == null) return false;

        rule.setRecordReplay(enabled);
        plugin.getAutomodManager().saveRule(rule);
        return true;
    }

    /**
     * Set the duration for replay recording when a rule triggers.
     *
     * @param ruleId The rule ID
     * @param seconds Duration in seconds (10-300)
     * @return true if the rule was found and updated
     */
    public boolean setRuleReplayDuration(String ruleId, int seconds) {
        AutomodRule rule = plugin.getAutomodManager().getRule(ruleId);
        if (rule == null) return false;

        rule.setReplayDurationSeconds(seconds);
        plugin.getAutomodManager().saveRule(rule);
        return true;
    }

    /**
     * Get the replay recording configuration for a rule.
     *
     * @param ruleId The rule ID
     * @return ReplayConfig or null if rule not found
     */
    public ReplayConfig getRuleReplayConfig(String ruleId) {
        AutomodRule rule = plugin.getAutomodManager().getRule(ruleId);
        if (rule == null) return null;

        return new ReplayConfig(rule.isRecordReplay(), rule.getReplayDurationSeconds());
    }

    /**
     * Replay recording configuration record.
     *
     * @param enabled Whether replay recording is enabled
     * @param durationSeconds Recording duration in seconds (10-300)
     */
    public record ReplayConfig(boolean enabled, int durationSeconds) {
        public ReplayConfig {
            if (durationSeconds < 10) durationSeconds = 10;
            if (durationSeconds > 300) durationSeconds = 300;
        }
    }

    /**
     * Get a rule by its ID.
     *
     * @param ruleId The rule ID
     * @return RuleInfo or null if not found
     */
    public RuleInfo getRule(String ruleId) {
        AutomodRule rule = plugin.getAutomodManager().getRule(ruleId);
        if (rule == null) return null;
        return toRuleInfo(rule);
    }

    /**
     * Get all automod rules.
     *
     * @return List of all rules
     */
    public List<RuleInfo> getAllRules() {
        List<RuleInfo> result = new ArrayList<>();
        for (AutomodRule rule : plugin.getAutomodManager().getRules()) {
            result.add(toRuleInfo(rule));
        }
        return result;
    }

    /**
     * Get rules by type.
     *
     * @param type The rule type
     * @return List of matching rules
     */
    public List<RuleInfo> getRulesByType(RuleType type) {
        List<RuleInfo> result = new ArrayList<>();
        for (AutomodRule rule : plugin.getAutomodManager().getRules()) {
            if (rule.getType().name().equals(type.name())) {
                result.add(toRuleInfo(rule));
            }
        }
        return result;
    }

    /**
     * Enable or disable a rule.
     *
     * @param ruleId The rule ID
     * @param enabled true to enable, false to disable
     * @return true if the rule was found and updated
     */
    public boolean setRuleEnabled(String ruleId, boolean enabled) {
        AutomodRule rule = plugin.getAutomodManager().getRule(ruleId);
        if (rule == null) return false;

        rule.setEnabled(enabled);
        plugin.getAutomodManager().saveRule(rule);
        return true;
    }

    /**
     * Update the blacklisted phrases for a rule.
     *
     * @param ruleId The rule ID
     * @param phrases The new list of phrases
     * @return true if the rule was found and updated
     */
    public boolean setRulePhrases(String ruleId, List<String> phrases) {
        AutomodRule rule = plugin.getAutomodManager().getRule(ruleId);
        if (rule == null) return false;

        rule.setBlacklistedPhrases(new ArrayList<>(phrases));
        plugin.getAutomodManager().saveRule(rule);
        return true;
    }

    /**
     * Add a phrase to a rule's blacklist.
     *
     * @param ruleId The rule ID
     * @param phrase The phrase to add
     * @return true if the rule was found and updated
     */
    public boolean addPhrase(String ruleId, String phrase) {
        AutomodRule rule = plugin.getAutomodManager().getRule(ruleId);
        if (rule == null) return false;

        List<String> phrases = rule.getBlacklistedPhrases();
        if (!phrases.contains(phrase)) {
            phrases.add(phrase);
            plugin.getAutomodManager().saveRule(rule);
        }
        return true;
    }

    /**
     * Remove a phrase from a rule's blacklist.
     *
     * @param ruleId The rule ID
     * @param phrase The phrase to remove
     * @return true if the phrase was found and removed
     */
    public boolean removePhrase(String ruleId, String phrase) {
        AutomodRule rule = plugin.getAutomodManager().getRule(ruleId);
        if (rule == null) return false;

        List<String> phrases = rule.getBlacklistedPhrases();
        if (phrases.remove(phrase)) {
            plugin.getAutomodManager().saveRule(rule);
            return true;
        }
        return false;
    }

    /**
     * Set exclusion phrases for a rule.
     *
     * @param ruleId The rule ID
     * @param exclusions The exclusion phrases
     * @return true if the rule was found and updated
     */
    public boolean setRuleExclusions(String ruleId, List<String> exclusions) {
        AutomodRule rule = plugin.getAutomodManager().getRule(ruleId);
        if (rule == null) return false;

        rule.setExclusionPhrases(new ArrayList<>(exclusions));
        plugin.getAutomodManager().saveRule(rule);
        return true;
    }

    /**
     * Delete a rule.
     *
     * @param ruleId The rule ID
     * @return true if the rule was deleted (built-in rules cannot be deleted)
     */
    public boolean deleteRule(String ruleId) {
        AutomodRule rule = plugin.getAutomodManager().getRule(ruleId);
        if (rule == null || rule.isBuiltIn()) return false;

        plugin.getAutomodManager().deleteRule(ruleId);
        return true;
    }

    // ==================== AUTO PUNISHMENT ====================

    /**
     * Configure auto-punishment for a rule.
     *
     * @param ruleId The rule ID
     * @param config The punishment configuration (null to disable)
     * @return true if the rule was found and updated
     */
    public boolean setAutoPunishment(String ruleId, PunishmentConfig config) {
        AutomodRule rule = plugin.getAutomodManager().getRule(ruleId);
        if (rule == null) return false;

        if (config == null) {
            AutomodRule.AutoPunishment punishment = rule.getAutoPunishment();
            if (punishment != null) {
                punishment.setEnabled(false);
            }
        } else {
            AutomodRule.AutoPunishment punishment = rule.getAutoPunishment();
            if (punishment == null) {
                punishment = new AutomodRule.AutoPunishment();
                rule.setAutoPunishment(punishment);
            }

            punishment.setEnabled(true);
            punishment.setType(switch (config.type()) {
                case WARN -> PunishmentType.WARN;
                case MUTE -> PunishmentType.MUTE;
                case KICK -> PunishmentType.KICK;
                case BAN -> PunishmentType.BAN;
            });
            punishment.setDuration(config.durationMs());
            punishment.setTriggerCount(config.triggerCount());
            punishment.setTimeWindow(config.timeWindowMs());
            punishment.setReason(config.reason());
        }

        plugin.getAutomodManager().saveRule(rule);
        return true;
    }

    /**
     * Get auto-punishment configuration for a rule.
     *
     * @param ruleId The rule ID
     * @return PunishmentConfig or null if not configured
     */
    public PunishmentConfig getAutoPunishment(String ruleId) {
        AutomodRule rule = plugin.getAutomodManager().getRule(ruleId);
        if (rule == null) return null;

        AutomodRule.AutoPunishment punishment = rule.getAutoPunishment();
        if (punishment == null || !punishment.isEnabled()) return null;

        return new PunishmentConfig(
            switch (punishment.getType()) {
                case WARN -> AutoPunishmentType.WARN;
                case MUTE, IPMUTE -> AutoPunishmentType.MUTE;
                case KICK -> AutoPunishmentType.KICK;
                case BAN, IPBAN -> AutoPunishmentType.BAN;
            },
            punishment.getDuration(),
            punishment.getTriggerCount(),
            punishment.getTimeWindow(),
            punishment.getReason()
        );
    }

    /**
     * Punishment configuration record.
     *
     * @param type The punishment type
     * @param durationMs Duration in milliseconds (-1 for permanent)
     * @param triggerCount Violations before punishment is applied
     * @param timeWindowMs Time window for counting violations in milliseconds
     * @param reason Custom reason (null for default)
     */
    public record PunishmentConfig(AutoPunishmentType type, long durationMs, int triggerCount, long timeWindowMs, String reason) {
        public PunishmentConfig {
            if (triggerCount < 1) triggerCount = 1;
            if (timeWindowMs < 1000) timeWindowMs = 60000; // 1 minute minimum
        }

        public PunishmentConfig(AutoPunishmentType type, long durationMs, int triggerCount, long timeWindowMs) {
            this(type, durationMs, triggerCount, timeWindowMs, null);
        }
    }

    // ==================== MESSAGE PROCESSING ====================

    /**
     * Process a message through automod filters.
     * This does NOT send the message, only checks if it would be filtered.
     *
     * @param player The player sending the message
     * @param message The message content
     * @return FilterResult indicating what would happen
     */
    public FilterResult checkMessage(Player player, String message) {
        AutomodManager.FilterResult result = plugin.getAutomodManager().processMessage(player, message);
        return new FilterResult(
            result.isBlocked(),
            result.isModified(),
            result.getModifiedMessage(),
            result.getReason()
        );
    }

    /**
     * Check if a nickname would be filtered.
     *
     * @param player The player setting the nickname
     * @param nickname The nickname
     * @return FilterResult indicating what would happen
     */
    public FilterResult checkNickname(Player player, String nickname) {
        AutomodManager.FilterResult result = plugin.getAutomodManager().processNickname(player, nickname);
        return new FilterResult(
            result.isBlocked(),
            result.isModified(),
            result.getModifiedMessage(),
            result.getReason()
        );
    }

    /**
     * Filter result record.
     *
     * @param blocked Whether the message would be blocked
     * @param modified Whether the message would be modified
     * @param modifiedMessage The modified message (if modified)
     * @param reason The reason for blocking/modifying
     */
    public record FilterResult(boolean blocked, boolean modified, String modifiedMessage, String reason) {
        public boolean allowed() {
            return !blocked && !modified;
        }
    }

    // ==================== ANTICHEAT RULES ====================

    /**
     * Get all anticheat rules.
     *
     * @return List of anticheat rules
     */
    public List<RuleInfo> getAnticheatRules() {
        return getRulesByType(RuleType.ANTICHEAT);
    }

    /**
     * Get anticheat rules for a specific anticheat.
     *
     * @param anticheatName The anticheat name (e.g., "grim", "vulcan")
     * @return List of matching rules
     */
    public List<RuleInfo> getAnticheatRules(String anticheatName) {
        List<RuleInfo> result = new ArrayList<>();
        String prefix = "ac_" + anticheatName.toLowerCase() + "_";

        for (AutomodRule rule : plugin.getAutomodManager().getRules()) {
            if (rule.getId().startsWith(prefix)) {
                result.add(toRuleInfo(rule));
            }
        }
        return result;
    }

    /**
     * Configure an anticheat rule's threshold settings.
     *
     * @param ruleId The rule ID (format: ac_anticheat_checkname)
     * @param alertThreshold Number of alerts before triggering
     * @param timeWindowSeconds Time window in seconds
     * @return true if the rule was found and updated
     */
    public boolean setAnticheatThreshold(String ruleId, int alertThreshold, int timeWindowSeconds) {
        AutomodRule rule = plugin.getAutomodManager().getRule(ruleId);
        if (rule == null || rule.getType() != AutomodRule.RuleType.ANTICHEAT) return false;

        rule.setAnticheatAlertThreshold(alertThreshold);
        rule.setAnticheatTimeWindowSeconds(timeWindowSeconds);
        plugin.getAutomodManager().saveRule(rule);
        return true;
    }

    // ==================== UTILITY ====================

    /**
     * Reload automod rules from database.
     */
    public void reload() {
        plugin.getAutomodManager().load();
    }

    /**
     * Clear a player's violation history.
     *
     * @param player The player
     */
    public void clearPlayerViolations(Player player) {
        plugin.getAutomodManager().clearPlayerData(player.getUniqueId());
    }

    /**
     * Clear a player's violation history by UUID.
     *
     * @param uuid The player UUID
     */
    public void clearPlayerViolations(UUID uuid) {
        plugin.getAutomodManager().clearPlayerData(uuid);
    }

    // ==================== INTERNAL ====================

    private RuleInfo toRuleInfo(AutomodRule rule) {
        return new RuleInfo(
            rule.getId(),
            rule.getName(),
            rule.getDescription(),
            RuleType.valueOf(rule.getType().name()),
            rule.isEnabled(),
            rule.isBuiltIn(),
            rule.getBlacklistedPhrases(),
            rule.getExclusionPhrases(),
            rule.isExactMatch(),
            rule.isRecordReplay(),
            rule.getReplayDurationSeconds()
        );
    }

    // ==================== ENUMS & RECORDS ====================

    /**
     * Rule type enum matching internal types.
     */
    public enum RuleType {
        SPAM_PROTECTION,
        CAPS_FILTER,
        WORD_FILTER,
        LINK_FILTER,
        ANTICHEAT,
        AFK_KICK,
        NICKNAME
    }

    /**
     * Filter mode for word filters.
     */
    public enum FilterMode {
        /** Match if the message contains the phrase anywhere */
        CONTAINS_PHRASE,
        /** Match only if the entire message matches exactly */
        EXACT_MESSAGE,
        /** Use regex pattern matching */
        REGEX
    }

    /**
     * Action to take when a rule is triggered.
     */
    public enum FlagAction {
        /** Block the message entirely */
        BLOCK,
        /** Allow but warn the player */
        WARN,
        /** Modify the message (e.g., censor words) */
        MODIFY,
        /** Log only, don't take action */
        LOG_ONLY
    }

    /**
     * Auto-punishment type enum.
     */
    public enum AutoPunishmentType {
        WARN,
        MUTE,
        KICK,
        BAN
    }

    /**
     * Rule information record.
     *
     * @param id The rule ID
     * @param name The rule name
     * @param description The rule description
     * @param type The rule type
     * @param enabled Whether the rule is enabled
     * @param builtIn Whether this is a built-in rule
     * @param phrases Blacklisted phrases
     * @param exclusions Exclusion phrases
     * @param exactMatch Whether to use exact matching
     * @param recordReplay Whether to record replays when triggered
     * @param replayDurationSeconds Replay recording duration
     */
    public record RuleInfo(
        String id,
        String name,
        String description,
        RuleType type,
        boolean enabled,
        boolean builtIn,
        List<String> phrases,
        List<String> exclusions,
        boolean exactMatch,
        boolean recordReplay,
        int replayDurationSeconds
    ) {}

    // ==================== RULE BUILDER ====================

    /**
     * Builder for creating custom automod rules with full configuration.
     * Example usage:
     * <pre>
     * String ruleId = api.createRule("Bad Words Filter")
     *     .type(RuleType.WORD_FILTER)
     *     .description("Filters inappropriate language")
     *     .phrases(Arrays.asList("badword1", "badword2"))
     *     .filterMode(FilterMode.CONTAINS_PHRASE)
     *     .flagAction(FlagAction.BLOCK)
     *     .enabled(true)
     *     .autoPunishment(new PunishmentConfig(AutoPunishmentType.WARN, 0, 3, 300000))
     *     .build();
     * </pre>
     */
    public static class RuleBuilder {
        private final ModereX plugin;
        private final String name;
        private RuleType type = RuleType.WORD_FILTER;
        private String description;
        private List<String> phrases = new ArrayList<>();
        private List<String> exclusions = new ArrayList<>();
        private FilterMode filterMode = FilterMode.CONTAINS_PHRASE;
        private FlagAction flagAction = FlagAction.BLOCK;
        private boolean enabled = true;
        private boolean exactMatch = false;
        private boolean applyToNicknames = false;
        private boolean nicknameOnly = false;
        private boolean recordReplay = false;
        private int replayDurationSeconds = 60;
        private PunishmentConfig punishment;

        public RuleBuilder(ModereX plugin, String name) {
            this.plugin = plugin;
            this.name = name;
        }

        /**
         * Set the rule type. Defaults to WORD_FILTER.
         * Only WORD_FILTER and NICKNAME are allowed for custom rules.
         */
        public RuleBuilder type(RuleType type) {
            if (type != RuleType.WORD_FILTER && type != RuleType.NICKNAME) {
                throw new IllegalArgumentException("Custom rules can only be WORD_FILTER or NICKNAME type");
            }
            this.type = type;
            return this;
        }

        /** Set the rule description. */
        public RuleBuilder description(String description) {
            this.description = description;
            return this;
        }

        /** Set the phrases to filter. */
        public RuleBuilder phrases(List<String> phrases) {
            this.phrases = new ArrayList<>(phrases);
            return this;
        }

        /** Add a single phrase to filter. */
        public RuleBuilder addPhrase(String phrase) {
            this.phrases.add(phrase);
            return this;
        }

        /** Set exclusion phrases (words that bypass the filter). */
        public RuleBuilder exclusions(List<String> exclusions) {
            this.exclusions = new ArrayList<>(exclusions);
            return this;
        }

        /** Add a single exclusion phrase. */
        public RuleBuilder addExclusion(String exclusion) {
            this.exclusions.add(exclusion);
            return this;
        }

        /** Set the filter mode. Defaults to CONTAINS_PHRASE. */
        public RuleBuilder filterMode(FilterMode mode) {
            this.filterMode = mode;
            this.exactMatch = (mode == FilterMode.EXACT_MESSAGE);
            return this;
        }

        /** Set the action to take when triggered. Defaults to BLOCK. */
        public RuleBuilder flagAction(FlagAction action) {
            this.flagAction = action;
            return this;
        }

        /** Set whether the rule is enabled. Defaults to true. */
        public RuleBuilder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        /** Set whether to use exact message matching. */
        public RuleBuilder exactMatch(boolean exactMatch) {
            this.exactMatch = exactMatch;
            return this;
        }

        /** Set whether the rule also applies to nicknames. */
        public RuleBuilder applyToNicknames(boolean apply) {
            this.applyToNicknames = apply;
            return this;
        }

        /** Set whether the rule ONLY applies to nicknames. */
        public RuleBuilder nicknameOnly(boolean only) {
            this.nicknameOnly = only;
            if (only) this.type = RuleType.NICKNAME;
            return this;
        }

        /**
         * Enable replay recording when this rule is triggered.
         * When enabled, the plugin will automatically record player activity.
         */
        public RuleBuilder recordReplay(boolean enabled) {
            this.recordReplay = enabled;
            return this;
        }

        /**
         * Set the replay recording duration in seconds.
         * Only applicable if recordReplay is enabled.
         *
         * @param seconds Duration in seconds (10-300)
         */
        public RuleBuilder replayDuration(int seconds) {
            this.replayDurationSeconds = Math.max(10, Math.min(300, seconds));
            return this;
        }

        /** Configure auto-punishment for this rule. */
        public RuleBuilder autoPunishment(PunishmentConfig config) {
            this.punishment = config;
            return this;
        }

        /**
         * Build and save the rule.
         * @return The rule ID, or null if creation failed
         */
        public String build() {
            AutomodRule.RuleType internalType = AutomodRule.RuleType.valueOf(type.name());
            AutomodRule rule = plugin.getAutomodManager().createRule(name, internalType);
            if (rule == null) return null;

            // Apply configuration
            if (description != null) rule.setDescription(description);
            rule.setBlacklistedPhrases(phrases);
            rule.setExclusionPhrases(exclusions);
            rule.setFilterMode(AutomodRule.FilterMode.valueOf(filterMode.name()));
            rule.setFlagAction(AutomodRule.FlagAction.valueOf(flagAction.name()));
            rule.setEnabled(enabled);
            rule.setExactMatch(exactMatch);
            rule.setApplyToNicknames(applyToNicknames);
            rule.setNicknameOnly(nicknameOnly);

            // Apply replay recording settings
            rule.setRecordReplay(recordReplay);
            rule.setReplayDurationSeconds(replayDurationSeconds);

            // Apply punishment if configured
            if (punishment != null) {
                AutomodRule.AutoPunishment autoPunishment = new AutomodRule.AutoPunishment();
                autoPunishment.setEnabled(true);
                autoPunishment.setType(switch (punishment.type()) {
                    case WARN -> PunishmentType.WARN;
                    case MUTE -> PunishmentType.MUTE;
                    case KICK -> PunishmentType.KICK;
                    case BAN -> PunishmentType.BAN;
                });
                autoPunishment.setDuration(punishment.durationMs());
                autoPunishment.setTriggerCount(punishment.triggerCount());
                autoPunishment.setTimeWindow(punishment.timeWindowMs());
                if (punishment.reason() != null) {
                    autoPunishment.setReason(punishment.reason());
                }
                rule.setAutoPunishment(autoPunishment);
            }

            plugin.getAutomodManager().saveRule(rule);
            return rule.getId();
        }
    }
}
