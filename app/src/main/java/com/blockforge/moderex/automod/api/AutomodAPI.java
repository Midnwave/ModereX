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

    // ==================== WORD FILTER RULES ====================

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
            rule.isExactMatch()
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
        boolean exactMatch
    ) {}
}
