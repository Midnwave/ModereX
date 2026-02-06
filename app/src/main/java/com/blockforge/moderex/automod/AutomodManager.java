package com.blockforge.moderex.automod;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.hooks.anticheat.AnticheatChecks;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.replay.ReplaySession;
import com.blockforge.moderex.replay.ReplaySnapshot;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AutomodManager handles automatic chat moderation, spam prevention, content filtering,
 * nickname moderation, and command message detection.
 */
public class AutomodManager {

    private final ModereX plugin;
    private final Map<String, AutomodRule> rules = new ConcurrentHashMap<>();
    private final Map<UUID, List<Long>> messageTimestamps = new ConcurrentHashMap<>();

    // URL/Link detection pattern
    private static final Pattern URL_PATTERN = Pattern.compile(
            "(?i)\\b(?:https?://|www\\.|[a-z0-9][a-z0-9-]*\\.[a-z]{2,}(?:/|\\b))",
            Pattern.CASE_INSENSITIVE
    );

    // IP address pattern
    private static final Pattern IP_PATTERN = Pattern.compile(
            "\\b(?:\\d{1,3}\\.){3}\\d{1,3}(?::\\d{1,5})?\\b"
    );

    // Message commands that should be filtered (base names, will match any plugin prefix)
    private static final Set<String> MESSAGE_COMMANDS = Set.of(
            "msg", "message", "tell", "whisper", "w", "m", "pm", "dm",
            "r", "reply", "er", "emsg", "etell", "ewhisper", "ew",
            "mail", "helpop", "ac", "adminchat", "socialspy"
    );

    // Color code patterns for nickname filtering
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile(
            "(?i)(&[0-9a-fk-or]|&#[0-9a-f]{6}|<#[0-9a-f]{6}>|\\{#[0-9a-f]{6}\\}|" +
            "§[0-9a-fk-or]|<[a-z_]+>|\\[[a-z_]+\\])"
    );

    // Special/unicode characters pattern - includes resource pack characters and filter bypass characters
    // Private Use Area (U+E000-U+F8FF), Symbols, Box Drawing, Arrows, Math, etc.
    private static final Pattern SPECIAL_CHARS_PATTERN = Pattern.compile(
            "[\\uE000-\\uF8FF" +  // Private Use Area (resource packs)
            "\\u2500-\\u257F" +   // Box Drawing
            "\\u2580-\\u259F" +   // Block Elements
            "\\u25A0-\\u25FF" +   // Geometric Shapes
            "\\u2600-\\u26FF" +   // Miscellaneous Symbols
            "\\u2700-\\u27BF" +   // Dingbats
            "\\u2800-\\u28FF" +   // Braille Patterns
            "\\u2B00-\\u2BFF" +   // Misc Symbols and Arrows
            "\\uFE00-\\uFE0F" +   // Variation Selectors
            "\\uFFF0-\\uFFFF" +   // Specials
            "\\u0300-\\u036F" +   // Combining Diacritical Marks (zalgo)
            "\\u1AB0-\\u1AFF" +   // Combining Diacritical Extended
            "\\u1DC0-\\u1DFF" +   // Combining Diacritical Supplement
            "\\u20D0-\\u20FF" +   // Combining Diacritical for Symbols
            "\\uFE20-\\uFE2F" +   // Combining Half Marks
            "\\u0483-\\u0489" +   // Combining Cyrillic
            "\\u0591-\\u05BD" +   // Hebrew diacritics
            "\\u0610-\\u061A" +   // Arabic diacritics
            "\\u064B-\\u065F" +   // Arabic fathatan
            "\\u0670" +           // Arabic superscript alef
            "\\u06D6-\\u06DC" +   // Arabic small
            "\\u06DF-\\u06E4" +   // Arabic small
            "\\u06E7-\\u06E8" +   // Arabic small
            "\\u06EA-\\u06ED" +   // Arabic small
            "\\u0711" +           // Syriac
            "\\u0730-\\u074A" +   // Syriac combining
            "\\u07A6-\\u07B0" +   // Thaana combining
            "\\u07EB-\\u07F3" +   // NKo combining
            "\\u0816-\\u0819" +   // Samaritan
            "\\u081B-\\u0823" +   // Samaritan
            "\\u0825-\\u0827" +   // Samaritan
            "\\u0829-\\u082D" +   // Samaritan
            "\\u0859-\\u085B" +   // Mandaic
            "\\u08D4-\\u08E1" +   // Arabic Extended
            "\\u08E3-\\u0903" +   // Arabic Extended
            "\\u093A-\\u093C" +   // Devanagari
            "\\u093E-\\u094F" +   // Devanagari
            "\\u0951-\\u0957" +   // Devanagari stress
            "\\u0962-\\u0963" +   // Devanagari vowel
            "]"
    );

    public AutomodManager(ModereX plugin) {
        this.plugin = plugin;
    }

    /**
     * Load all automod rules from database and create built-in rules.
     */
    public void load() {
        rules.clear();
        loadBuiltInRules();
        loadCustomRules();
        plugin.getLogger().info("Loaded " + rules.size() + " automod rules.");
    }

    /**
     * Create built-in rules that cannot be deleted.
     */
    private void loadBuiltInRules() {
        // Spam Protection (built-in)
        AutomodRule spamRule = AutomodRule.createBuiltIn(
                "spam_protection", "Spam Protection", AutomodRule.RuleType.SPAM_PROTECTION
        );
        spamRule.setDescription("Blocks rapid or repetitive messages");
        spamRule.setSpamMessageCount(3);
        spamRule.setSpamTimeWindowSeconds(5);
        spamRule.setSpamDetectSimilar(true);
        spamRule.setEnabled(plugin.getConfigManager().getSettings().isSpamPreventionEnabled());
        rules.put(spamRule.getId(), spamRule);

        // Caps Filter (built-in)
        AutomodRule capsRule = AutomodRule.createBuiltIn(
                "caps_filter", "Caps Lock Filter", AutomodRule.RuleType.CAPS_FILTER
        );
        capsRule.setDescription("Converts excessive caps to lowercase");
        capsRule.setCapsMaxPercentage(70);
        capsRule.setCapsMinLength(10);
        capsRule.setFlagAction(AutomodRule.FlagAction.MODIFY);
        capsRule.setEnabled(plugin.getConfigManager().getSettings().isCapsFilterEnabled());
        rules.put(capsRule.getId(), capsRule);

        // Link Filter (built-in)
        AutomodRule linkRule = AutomodRule.createBuiltIn(
                "link_filter", "Link Filter", AutomodRule.RuleType.LINK_FILTER
        );
        linkRule.setDescription("Blocks URLs and IP addresses");
        linkRule.setEnabled(plugin.getConfigManager().getSettings().isLinkFilterEnabled());
        rules.put(linkRule.getId(), linkRule);

        // AFK Kick (built-in)
        AutomodRule afkRule = AutomodRule.createBuiltIn(
                "afk_kick", "AFK Auto-Kick", AutomodRule.RuleType.AFK_KICK
        );
        afkRule.setDescription("Kicks players after inactivity");
        afkRule.setAfkTimeoutMinutes(15);
        afkRule.setAfkKickEnabled(false);
        afkRule.setEnabled(false);
        rules.put(afkRule.getId(), afkRule);

        // Special Characters Filter (built-in)
        AutomodRule specialCharsRule = AutomodRule.createBuiltIn(
                "special_chars", "Special Characters Filter", AutomodRule.RuleType.SPECIAL_CHARS
        );
        specialCharsRule.setDescription("Blocks special unicode characters often used in resource packs");
        specialCharsRule.setApplyToNicknames(true);
        specialCharsRule.setEnabled(false); // Disabled by default
        rules.put(specialCharsRule.getId(), specialCharsRule);

        // Load built-in rule configs from database (to restore user settings)
        loadBuiltInRuleConfigs();
    }

    /**
     * Load built-in rule configurations from database.
     */
    /**
     * Map from built-in rule type to its programmatic ID.
     * This fixes the name-to-ID fallback when rule_id is NULL in the database.
     */
    private static final java.util.Map<String, String> BUILTIN_TYPE_TO_ID = java.util.Map.of(
            "SPAM_PROTECTION", "spam_protection",
            "CAPS_FILTER", "caps_filter",
            "LINK_FILTER", "link_filter",
            "AFK_KICK", "afk_kick",
            "SPECIAL_CHARS", "special_chars"
    );

    private void loadBuiltInRuleConfigs() {
        try {
            plugin.logDebug("[Automod] Loading built-in rule configs from database...");
            plugin.getDatabaseManager().query("""
                    SELECT * FROM moderex_automod_rules WHERE type IN ('SPAM_PROTECTION', 'CAPS_FILTER', 'LINK_FILTER', 'AFK_KICK', 'SPECIAL_CHARS')
                    """,
                    rs -> {
                        int count = 0;
                        while (rs.next()) {
                            count++;
                            String id = rs.getString("rule_id");
                            String name = rs.getString("name");
                            String type = rs.getString("type");
                            String config = rs.getString("config");
                            boolean enabled = rs.getBoolean("enabled");
                            int rowId = rs.getInt("id");

                            plugin.logDebug("[Automod] Found DB row: rule_id=" + id + ", name=" + name + ", type=" + type + ", enabled=" + enabled);

                            // Resolve the in-memory rule ID from the DB row
                            String resolvedId = id;
                            if (resolvedId == null || resolvedId.isEmpty()) {
                                // Use type-to-ID mapping (most reliable), fallback to name conversion
                                resolvedId = BUILTIN_TYPE_TO_ID.getOrDefault(type, name.toLowerCase().replace(" ", "_"));
                                plugin.logDebug("[Automod] rule_id was NULL, resolved to: " + resolvedId + " via type=" + type);

                                // Fix the database row to have the correct rule_id for future lookups
                                try {
                                    plugin.getDatabaseManager().update(
                                            "UPDATE moderex_automod_rules SET rule_id = ? WHERE id = ?",
                                            resolvedId, rowId
                                    );
                                    plugin.logDebug("[Automod] Fixed NULL rule_id in DB row " + rowId + " → " + resolvedId);
                                } catch (SQLException fixEx) {
                                    plugin.logDebug("[Automod] Could not fix rule_id: " + fixEx.getMessage());
                                }
                            }

                            AutomodRule existing = rules.get(resolvedId);
                            if (existing != null) {
                                plugin.logDebug("[Automod] Applying config to in-memory rule: " + resolvedId);
                                existing.setEnabled(enabled);
                                existing.loadConfigJson(config);
                                plugin.logDebug("[Automod] After load - rule " + resolvedId + " enabled=" + existing.isEnabled());
                            } else {
                                plugin.logDebug("[Automod] WARNING: No in-memory rule found for resolved id: " + resolvedId);
                            }
                        }
                        plugin.logDebug("[Automod] Loaded " + count + " built-in rule configs from database");
                        return null;
                    }
            );
        } catch (SQLException e) {
            // Table might not have rule_id column yet, that's fine
            plugin.logDebug("Could not load built-in rule configs: " + e.getMessage());
        }
    }

    /**
     * Load custom rules from database.
     */
    private void loadCustomRules() {
        try {
            plugin.getDatabaseManager().query("""
                    SELECT * FROM moderex_automod_rules WHERE type = 'WORD_FILTER' OR type = 'NICKNAME'
                    """,
                    rs -> {
                        while (rs.next()) {
                            AutomodRule rule = new AutomodRule();
                            rule.setId(String.valueOf(rs.getInt("id")));
                            rule.setName(rs.getString("name"));
                            rule.setEnabled(rs.getBoolean("enabled"));
                            rule.loadConfigJson(rs.getString("config"));

                            // Set type from database or default to WORD_FILTER
                            String typeStr = rs.getString("type");
                            try {
                                rule.setType(AutomodRule.RuleType.valueOf(typeStr));
                            } catch (Exception e) {
                                rule.setType(AutomodRule.RuleType.WORD_FILTER);
                            }

                            rules.put(rule.getId(), rule);
                        }
                        return null;
                    }
            );
        } catch (SQLException e) {
            plugin.logError("Failed to load automod rules", e);
        }
    }

    /**
     * Process a chat message through all enabled automod rules.
     */
    public FilterResult processMessage(Player player, String message) {
        if (player.hasPermission("moderex.bypass.automod")) {
            return FilterResult.allow();
        }

        UUID uuid = player.getUniqueId();

        // Check each rule in priority order
        List<AutomodRule> sortedRules = new ArrayList<>(rules.values());
        sortedRules.sort(Comparator.comparingInt(AutomodRule::getPriority).reversed());

        for (AutomodRule rule : sortedRules) {
            if (!rule.isEnabled()) continue;
            if (rule.isNicknameOnly()) continue; // Skip nickname-only rules for chat

            FilterResult result = processRule(player, uuid, message, rule);
            if (result.isBlocked() || result.isModified()) {
                return result;
            }
        }

        return FilterResult.allow();
    }

    /**
     * Process a command message (e.g., /msg, /tell) through automod.
     */
    public FilterResult processCommandMessage(Player player, String command, String message) {
        if (player.hasPermission("moderex.bypass.automod")) {
            return FilterResult.allow();
        }

        // Check if this is a message command
        String baseCmd = command.toLowerCase().split(":")[0]; // Handle plugin:command format
        if (baseCmd.startsWith("/")) baseCmd = baseCmd.substring(1);

        // Check against known message commands
        String finalBaseCmd = baseCmd;
        boolean isMessageCommand = MESSAGE_COMMANDS.stream()
                .anyMatch(cmd -> finalBaseCmd.equals(cmd) || finalBaseCmd.endsWith(":" + cmd));

        if (!isMessageCommand) {
            return FilterResult.allow();
        }

        // Process through chat filters
        return processMessage(player, message);
    }

    /**
     * Process a nickname through applicable automod rules.
     */
    public FilterResult processNickname(Player player, String nickname) {
        if (player.hasPermission("moderex.bypass.automod.nickname")) {
            return FilterResult.allow();
        }

        // Strip color codes for filtering
        String strippedNick = stripColorCodes(nickname);

        for (AutomodRule rule : rules.values()) {
            if (!rule.isEnabled()) continue;
            if (rule.getType() != AutomodRule.RuleType.WORD_FILTER &&
                rule.getType() != AutomodRule.RuleType.NICKNAME) continue;
            if (!rule.isApplyToNicknames() && rule.getType() != AutomodRule.RuleType.NICKNAME) continue;

            FilterResult result = checkWordFilter(rule, strippedNick);
            if (result.isBlocked()) {
                alertStaff(player, rule.getName(), "Nickname: " + nickname);
                handleAutoPunishment(player, rule);
                return result;
            }
        }

        return FilterResult.allow();
    }

    /**
     * Strip color codes from a string for filtering.
     */
    public String stripColorCodes(String text) {
        return COLOR_CODE_PATTERN.matcher(text).replaceAll("");
    }

    /**
     * Process a single rule against a message.
     */
    private FilterResult processRule(Player player, UUID uuid, String message, AutomodRule rule) {
        return switch (rule.getType()) {
            case SPAM_PROTECTION -> checkSpam(player, uuid, message, rule);
            case CAPS_FILTER -> checkCaps(player, message, rule);
            case LINK_FILTER -> checkLinks(player, message, rule);
            case WORD_FILTER, NICKNAME -> {
                FilterResult result = checkWordFilter(rule, message);
                if (result.isBlocked()) {
                    alertStaff(player, rule.getName(), message);
                    handleAutoPunishment(player, rule);
                }
                yield result;
            }
            case SPECIAL_CHARS -> checkSpecialChars(player, message, rule);
            case ANTICHEAT -> FilterResult.allow(); // Handled separately
            case AFK_KICK -> FilterResult.allow(); // Handled by AfkManager
        };
    }

    /**
     * Check spam protection rule.
     */
    private FilterResult checkSpam(Player player, UUID uuid, String message, AutomodRule rule) {
        long now = System.currentTimeMillis();
        long windowMs = rule.getSpamTimeWindowSeconds() * 1000L;

        // Get/create timestamp list
        List<Long> timestamps = messageTimestamps.computeIfAbsent(uuid, k -> new ArrayList<>());

        // Remove old timestamps
        timestamps.removeIf(t -> now - t > windowMs);

        // Check message count
        if (timestamps.size() >= rule.getSpamMessageCount()) {
            alertStaff(player, rule.getName(), "Rapid messages: " + message);
            handleAutoPunishment(player, rule);
            return FilterResult.block("spam");
        }

        // Check similar messages
        if (rule.isSpamDetectSimilar() && rule.isSimilarToLast(uuid, message)) {
            int violations = rule.recordViolation(uuid);
            if (violations >= 2) { // Allow one similar message
                alertStaff(player, rule.getName(), "Similar message: " + message);
                // Pass false since violation was already recorded above
                handleAutoPunishment(player, rule, false);
                return FilterResult.block("duplicate");
            }
        }

        timestamps.add(now);
        return FilterResult.allow();
    }

    /**
     * Check caps filter rule.
     */
    private FilterResult checkCaps(Player player, String message, AutomodRule rule) {
        if (message.length() < rule.getCapsMinLength()) {
            return FilterResult.allow();
        }

        int capsCount = 0;
        int letterCount = 0;

        for (char c : message.toCharArray()) {
            if (Character.isLetter(c)) {
                letterCount++;
                if (Character.isUpperCase(c)) {
                    capsCount++;
                }
            }
        }

        if (letterCount > 0) {
            double capsPercent = (double) capsCount / letterCount * 100;
            if (capsPercent > rule.getCapsMaxPercentage()) {
                if (rule.getFlagAction() == AutomodRule.FlagAction.MODIFY) {
                    alertStaff(player, rule.getName(), message, "modified");
                    handleAutoPunishment(player, rule);
                    return FilterResult.modify(message.toLowerCase());
                } else {
                    alertStaff(player, rule.getName(), message, "blocked");
                    handleAutoPunishment(player, rule);
                    return FilterResult.block("caps");
                }
            }
        }

        return FilterResult.allow();
    }

    /**
     * Check link filter rule.
     */
    private FilterResult checkLinks(Player player, String message, AutomodRule rule) {
        if (URL_PATTERN.matcher(message).find() || IP_PATTERN.matcher(message).find()) {
            alertStaff(player, rule.getName(), message);
            handleAutoPunishment(player, rule);
            return FilterResult.block("link");
        }
        return FilterResult.allow();
    }

    /**
     * Check special characters filter rule.
     * Detects unicode characters commonly used in resource packs or to bypass filters.
     */
    private FilterResult checkSpecialChars(Player player, String message, AutomodRule rule) {
        if (SPECIAL_CHARS_PATTERN.matcher(message).find()) {
            alertStaff(player, rule.getName(), "Special characters in: " + message);
            handleAutoPunishment(player, rule);
            return FilterResult.block("special_chars");
        }
        return FilterResult.allow();
    }

    /**
     * Check word filter rule.
     */
    private FilterResult checkWordFilter(AutomodRule rule, String message) {
        List<String> phrases = rule.getBlacklistedPhrases();
        List<String> exclusions = rule.getExclusionPhrases();
        AutomodRule.FilterMode mode = rule.getFilterMode();

        if (phrases == null || phrases.isEmpty()) {
            return FilterResult.allow();
        }

        String lowerMessage = message.toLowerCase();

        for (String phrase : phrases) {
            if (phrase == null || phrase.isEmpty()) continue;
            String lowerPhrase = phrase.toLowerCase();

            boolean matches = switch (mode) {
                case EXACT_MESSAGE -> lowerMessage.trim().equals(lowerPhrase.trim());
                case REGEX -> {
                    try {
                        yield Pattern.compile(phrase, Pattern.CASE_INSENSITIVE).matcher(message).find();
                    } catch (Exception e) {
                        yield false;
                    }
                }
                case CONTAINS_PHRASE -> {
                    // Word boundary check for better accuracy
                    Pattern pattern = Pattern.compile(
                            "\\b" + Pattern.quote(lowerPhrase) + "\\b",
                            Pattern.CASE_INSENSITIVE
                    );
                    yield pattern.matcher(message).find() || lowerMessage.contains(lowerPhrase);
                }
            };

            if (matches) {
                // Check exclusions - exact word/phrase match with word boundaries
                boolean excluded = false;
                if (exclusions != null) {
                    for (String exclusion : exclusions) {
                        if (exclusion != null && !exclusion.isEmpty()) {
                            // Use word boundary matching for exact word/phrase exclusion
                            Pattern exclusionPattern = Pattern.compile(
                                    "\\b" + Pattern.quote(exclusion.toLowerCase()) + "\\b",
                                    Pattern.CASE_INSENSITIVE
                            );
                            if (exclusionPattern.matcher(message).find()) {
                                excluded = true;
                                break;
                            }
                        }
                    }
                }

                if (!excluded) {
                    return FilterResult.block(rule.getName());
                }
            }
        }

        return FilterResult.allow();
    }

    /**
     * Handle auto punishment for a rule violation.
     * Records a new violation and checks if threshold is reached.
     */
    private void handleAutoPunishment(Player player, AutomodRule rule) {
        handleAutoPunishment(player, rule, true);
    }

    /**
     * Handle auto punishment for a rule violation.
     * @param recordViolation If true, records a new violation. If false, uses existing count
     *                        (use false when violation was already recorded by the calling code).
     */
    private void handleAutoPunishment(Player player, AutomodRule rule, boolean recordViolation) {
        AutomodRule.AutoPunishment punishment = rule.getAutoPunishment();
        if (punishment == null || !punishment.isEnabled()) {
            plugin.logDebug("[Automod] Auto-punishment not enabled for rule: " + rule.getName());
            return;
        }

        // Record violation or get existing count
        int violations;
        if (recordViolation) {
            violations = rule.recordViolation(player.getUniqueId());
        } else {
            violations = rule.getViolationCount(player.getUniqueId());
        }
        int triggerCount = punishment.getTriggerCount();

        plugin.logDebug("[Automod] Auto-punishment check for " + player.getName() +
                " on rule '" + rule.getName() + "': violations=" + violations +
                ", trigger=" + triggerCount);

        if (violations < triggerCount) {
            return;
        }

        plugin.logDebug("[Automod] Threshold reached! Executing auto-punishment: " +
                punishment.getType() + " for " + player.getName());

        // Reset violations
        rule.resetViolations(player.getUniqueId());

        // Execute punishment
        String reason = punishment.getReason() != null ? punishment.getReason() :
                "Automod: " + rule.getName();

        // Log auto-punishment action to activity log
        String punishAction = "auto-" + punishment.getType().name().toLowerCase();
        plugin.getActivityLogManager().logAutomodTrigger(player, rule.getName(),
                reason + " (violations: " + violations + "/" + triggerCount + ")", punishAction);

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            switch (punishment.getType()) {
                case MUTE, IPMUTE -> plugin.getPunishmentManager().mute(
                        player.getUniqueId(), player.getName(), null, "Automod",
                        punishment.getDuration(), reason
                );
                case KICK -> plugin.getPunishmentManager().kick(
                        player.getUniqueId(), player.getName(), null, "Automod", reason
                );
                case BAN, IPBAN -> plugin.getPunishmentManager().ban(
                        player.getUniqueId(), player.getName(), null, "Automod",
                        punishment.getDuration(), reason
                );
                case WARN -> plugin.getPunishmentManager().warn(
                        player.getUniqueId(), player.getName(), null, "Automod",
                        punishment.getDuration(), reason
                );
            }
        });
    }

    /**
     * Alert staff about an automod violation.
     */
    /**
     * Alert staff about an automod violation (backwards compat - defaults to "blocked" action).
     */
    private void alertStaff(Player player, String ruleName, String message) {
        alertStaff(player, ruleName, message, "blocked");
    }

    /**
     * Alert staff about an automod violation with specific action taken.
     */
    private void alertStaff(Player player, String ruleName, String message, String action) {
        Component alert = plugin.getLanguageManager().get(MessageKey.AUTOMOD_ALERT,
                "player", player.getName(),
                "rule", ruleName,
                "message", message
        );

        for (Player staff : plugin.getServer().getOnlinePlayers()) {
            if (staff.hasPermission("moderex.notify.automod")) {
                staff.sendMessage(alert);
            }
        }

        // Broadcast to web panel
        if (plugin.getWebPanelServer() != null) {
            plugin.getWebPanelServer().broadcastAutomodAlert(
                    player.getName(), player.getUniqueId(), ruleName, message
            );
        }

        // Log flag entry to activity log
        plugin.getActivityLogManager().logAutomodTrigger(player, ruleName, message, "flagged");

        // Log action entry to activity log
        plugin.getActivityLogManager().logAutomodTrigger(player, ruleName, message, action);

        // Trigger replay recording if enabled for this rule
        AutomodRule rule = findRuleByName(ruleName);
        if (rule != null) {
            triggerReplayRecording(player, rule);
        }
    }

    /**
     * Find a rule by its display name.
     */
    private AutomodRule findRuleByName(String ruleName) {
        for (AutomodRule rule : rules.values()) {
            if (rule.getName().equals(ruleName)) {
                return rule;
            }
        }
        return null;
    }

    /**
     * Trigger replay recording if the rule has it enabled.
     */
    private void triggerReplayRecording(Player player, AutomodRule rule) {
        if (!rule.isRecordReplay()) {
            return;
        }

        // Check if replay manager exists and is enabled
        if (plugin.getReplayManager() == null) {
            plugin.logDebug("[Automod] Replay recording requested but ReplayManager is not available");
            return;
        }

        // Start recording
        plugin.logDebug("[Automod] Starting replay recording for " + player.getName() +
                " (rule: " + rule.getName() + ", duration: " + rule.getReplayDurationSeconds() + "s)");

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            try {
                // Start recording with automod trigger reason
                var session = plugin.getReplayManager().startRecording(
                        player,
                        ReplaySession.RecordingReason.AUTOMOD_TRIGGER
                );

                if (session != null) {
                    // Capture the triggering rule info as an action
                    session.captureAction(player, ReplaySnapshot.ActionType.NONE,
                            "Automod triggered: " + rule.getName());

                    // Schedule stop after configured duration
                    int durationTicks = rule.getReplayDurationSeconds() * 20; // 20 ticks per second
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        plugin.getReplayManager().stopRecording(player.getUniqueId());
                        plugin.logDebug("[Automod] Stopped replay recording for " + player.getName());
                    }, durationTicks);
                }
            } catch (Exception e) {
                plugin.logError("Failed to start automod replay recording for " + player.getName(), e);
            }
        });
    }

    /**
     * Handle an anticheat alert and process through anticheat rules.
     */
    public void handleAnticheatAlert(Player player, String anticheat, String checkName,
                                     String checkType, int violations, double vlLevel) {
        // Find or create anticheat rule for this check
        String ruleId = "ac_" + anticheat.toLowerCase() + "_" + checkName.toLowerCase().replace(" ", "_");
        AutomodRule rule = rules.get(ruleId);

        if (rule == null) {
            // Create new anticheat rule (disabled by default)
            rule = new AutomodRule();
            rule.setId(ruleId);
            rule.setName(anticheat + " - " + checkName);
            rule.setType(AutomodRule.RuleType.ANTICHEAT);
            rule.setBuiltIn(true);
            rule.setEnabled(false);
            rule.setAnticheatName(anticheat);
            rule.setCheckName(checkName);
            rule.setDescription("Auto-generated rule for " + anticheat + " " + checkName + " alerts");
            rules.put(ruleId, rule);

            // Save to database
            saveRule(rule);
        }

        if (!rule.isEnabled()) return;

        // Record violation
        int count = rule.recordViolation(player.getUniqueId());

        // Check threshold
        if (count >= rule.getAnticheatAlertThreshold()) {
            alertStaff(player, rule.getName(), checkName + " (VL: " + (int) vlLevel + ")");
            // Pass false since violation was already recorded above
            handleAutoPunishment(player, rule, false);
            rule.resetViolations(player.getUniqueId());
        }
    }

    // === CRUD Operations ===

    /**
     * Create a new custom rule.
     */
    public AutomodRule createRule(String name, AutomodRule.RuleType type) {
        AutomodRule rule = new AutomodRule();
        rule.setName(name);
        rule.setType(type);
        rule.setEnabled(true);
        rule.setBuiltIn(false);

        try {
            plugin.getDatabaseManager().update("""
                    INSERT INTO moderex_automod_rules (name, type, enabled, config, created_at, updated_at)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """,
                    name, type.name(), true, rule.toConfigJson(),
                    System.currentTimeMillis(), System.currentTimeMillis()
            );

            // Get the inserted ID
            Integer id = plugin.getDatabaseManager().query(
                    "SELECT last_insert_rowid() as id",
                    rs -> rs.next() ? rs.getInt("id") : null
            );

            if (id != null) {
                rule.setId(String.valueOf(id));
                rules.put(rule.getId(), rule);
                // Don't broadcast here - let saveRule handle it after all properties are set
            }
        } catch (SQLException e) {
            plugin.logError("Failed to create automod rule", e);
        }

        return rule;
    }

    /**
     * Save a rule to the database.
     */
    public void saveRule(AutomodRule rule) throws RuntimeException {
        plugin.logDebug("[Automod] saveRule called for: " + rule.getId() + " (type=" + rule.getType() +
                ", builtIn=" + rule.isBuiltIn() + ")");

        // Handle built-in rules
        if (rule.isBuiltIn()) {
            plugin.logDebug("[Automod] Routing to saveBuiltInRule for: " + rule.getId());
            saveBuiltInRule(rule);
            // No broadcast here - let caller handle it
            return;
        }

        // Handle anticheat rules (they have string IDs like ac_grim_airliquidplace)
        if (rule.getType() == AutomodRule.RuleType.ANTICHEAT) {
            plugin.logDebug("[Automod] Routing to saveAnticheatRule for: " + rule.getId());
            saveAnticheatRule(rule);
            // No broadcast here - let caller handle it
            return;
        }

        try {
            String configJson = rule.toConfigJson();
            plugin.logDebug("[Automod] Saving custom rule to database: " + rule.getId());
            plugin.logDebug("[Automod] Custom rule config: " + configJson);

            int updated = plugin.getDatabaseManager().update("""
                    UPDATE moderex_automod_rules
                    SET name = ?, enabled = ?, config = ?, updated_at = ?
                    WHERE id = ?
                    """,
                    rule.getName(), rule.isEnabled(), configJson,
                    System.currentTimeMillis(), Integer.parseInt(rule.getId())
            );

            // No broadcast here - let caller handle it
            plugin.logDebug("[Automod] Successfully saved rule: " + rule.getId() + " (rows updated: " + updated + ")");
        } catch (SQLException e) {
            plugin.logError("Failed to save automod rule", e);
            throw new RuntimeException("Database error saving automod rule: " + e.getMessage(), e);
        } catch (NumberFormatException e) {
            plugin.logError("Failed to parse rule ID as integer: " + rule.getId() +
                    " (type=" + rule.getType() + ")", e);
            throw new RuntimeException("Invalid rule ID format: " + rule.getId(), e);
        }
    }

    /**
     * Save a built-in rule's settings.
     */
    private void saveBuiltInRule(AutomodRule rule) throws RuntimeException {
        // Update settings
        switch (rule.getId()) {
            case "spam_protection" -> {
                plugin.getConfigManager().getSettings().setSpamPreventionEnabled(rule.isEnabled());
            }
            case "caps_filter" -> {
                plugin.getConfigManager().getSettings().setCapsFilterEnabled(rule.isEnabled());
            }
            case "link_filter" -> {
                plugin.getConfigManager().getSettings().setLinkFilterEnabled(rule.isEnabled());
            }
        }

        // Save to database for full config
        try {
            String configJson = rule.toConfigJson();
            plugin.logDebug("[Automod] Saving built-in rule: " + rule.getId() + ", enabled=" + rule.isEnabled());
            plugin.logDebug("[Automod] Config JSON: " + configJson);

            // Try to update by rule_id first (most reliable for built-in rules)
            int updated = plugin.getDatabaseManager().update("""
                    UPDATE moderex_automod_rules
                    SET enabled = ?, config = ?, updated_at = ?, rule_id = ?
                    WHERE rule_id = ?
                    """,
                    rule.isEnabled(), configJson, System.currentTimeMillis(),
                    rule.getId(), rule.getId()
            );

            // If no match by rule_id, try matching by type (handles old rows with NULL rule_id)
            if (updated == 0) {
                updated = plugin.getDatabaseManager().update("""
                        UPDATE moderex_automod_rules
                        SET enabled = ?, config = ?, updated_at = ?, rule_id = ?
                        WHERE type = ? AND rule_id IS NULL
                        """,
                        rule.isEnabled(), configJson, System.currentTimeMillis(),
                        rule.getId(), rule.getType().name()
                );
                if (updated > 0) {
                    plugin.logDebug("[Automod] Fixed old row with NULL rule_id for type: " + rule.getType().name());
                }
            }

            plugin.logDebug("[Automod] UPDATE affected " + updated + " rows for rule: " + rule.getId());

            if (updated == 0) {
                // Insert new row with rule_id always set
                plugin.getDatabaseManager().update("""
                        INSERT INTO moderex_automod_rules (rule_id, name, type, enabled, config, created_at, updated_at)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                        """,
                        rule.getId(), rule.getName(), rule.getType().name(),
                        rule.isEnabled(), configJson,
                        System.currentTimeMillis(), System.currentTimeMillis()
                );
                plugin.logDebug("[Automod] INSERT new row for rule: " + rule.getId());
            }
            plugin.logDebug("[Automod] Successfully saved built-in rule: " + rule.getId());
        } catch (SQLException e) {
            plugin.logError("Failed to save built-in rule: " + rule.getId(), e);
            throw new RuntimeException("Database error saving built-in rule: " + e.getMessage(), e);
        }

        plugin.saveConfig();
    }

    /**
     * Delete a rule.
     */
    public void deleteRule(String ruleId) {
        AutomodRule rule = rules.get(ruleId);
        if (rule == null || rule.isBuiltIn()) return;

        plugin.logDebug("[Automod] deleteRule called for: " + ruleId + " (type=" + rule.getType() + ")");

        try {
            // Anticheat rules use string IDs stored in rule_id column
            if (rule.getType() == AutomodRule.RuleType.ANTICHEAT) {
                plugin.getDatabaseManager().update(
                        "DELETE FROM moderex_automod_rules WHERE rule_id = ?",
                        ruleId
                );
            } else {
                // Custom rules use numeric IDs stored in id column
                plugin.getDatabaseManager().update(
                        "DELETE FROM moderex_automod_rules WHERE id = ?",
                        Integer.parseInt(ruleId)
                );
            }
            rules.remove(ruleId);
            // No broadcast here - let caller handle it
            plugin.logDebug("[Automod] Successfully deleted rule: " + ruleId);
        } catch (SQLException e) {
            plugin.logError("Failed to delete automod rule", e);
        } catch (NumberFormatException e) {
            plugin.logError("Failed to parse rule ID as integer: " + ruleId, e);
        }
    }

    /**
     * Register anticheat rules for each check from a detected anticheat.
     * Each check becomes its own rule (disabled by default).
     *
     * @param anticheatName The anticheat name (e.g., "Grim")
     * @param version The anticheat version (e.g., "2.3.72")
     */
    public void registerAnticheatRules(String anticheatName, String version) {
        List<AnticheatChecks.CheckInfo> checks = AnticheatChecks.getChecks(anticheatName);
        if (checks.isEmpty()) {
            plugin.logDebug("[Automod] No known checks for anticheat: " + anticheatName);
            return;
        }

        plugin.getLogger().info("[Automod] Registering " + checks.size() + " anticheat rules for " +
                anticheatName + " " + version);

        int registered = 0;
        for (AnticheatChecks.CheckInfo check : checks) {
            String ruleId = "ac_" + anticheatName.toLowerCase() + "_" + check.getName().toLowerCase();

            // Skip if rule already exists
            if (rules.containsKey(ruleId)) {
                continue;
            }

            // Create the rule
            AutomodRule rule = new AutomodRule();
            rule.setId(ruleId);
            rule.setName(anticheatName + " - " + check.getDisplayName());
            rule.setDescription(check.getDescription() + " (" + anticheatName + " " + version + ")");
            rule.setType(AutomodRule.RuleType.ANTICHEAT);
            rule.setBuiltIn(false);  // Not built-in, but persistent
            rule.setEnabled(false);  // Disabled by default
            rule.setPriority(1000 + registered);  // Low priority (shown at end)
            rule.setAnticheatName(anticheatName);
            rule.setCheckName(check.getName());
            rule.setAnticheatAlertThreshold(10);  // Default threshold
            rule.setAnticheatTimeWindowSeconds(60);  // Default time window

            // Add to rules map
            rules.put(ruleId, rule);

            // Save to database
            saveAnticheatRule(rule);

            registered++;
        }

        if (registered > 0) {
            plugin.getLogger().info("[Automod] Registered " + registered + " new anticheat rules for " +
                    anticheatName + " " + version);
            // No broadcast here - rules are loaded on panel connect
        }
    }

    /**
     * Save an anticheat rule to the database.
     */
    private void saveAnticheatRule(AutomodRule rule) throws RuntimeException {
        try {
            // Check if rule already exists in database
            boolean exists = plugin.getDatabaseManager().query("""
                    SELECT 1 FROM moderex_automod_rules WHERE rule_id = ? LIMIT 1
                    """,
                    rs -> rs.next(),
                    rule.getId()
            );

            if (exists) {
                // Update existing
                plugin.getDatabaseManager().update("""
                        UPDATE moderex_automod_rules
                        SET name = ?, enabled = ?, config = ?, updated_at = ?
                        WHERE rule_id = ?
                        """,
                        rule.getName(), rule.isEnabled(), rule.toConfigJson(),
                        System.currentTimeMillis(), rule.getId()
                );
            } else {
                // Insert new
                plugin.getDatabaseManager().update("""
                        INSERT INTO moderex_automod_rules (rule_id, name, type, enabled, config, created_at, updated_at)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                        """,
                        rule.getId(), rule.getName(), rule.getType().name(),
                        rule.isEnabled(), rule.toConfigJson(),
                        System.currentTimeMillis(), System.currentTimeMillis()
                );
            }
            plugin.logDebug("[Automod] Successfully saved anticheat rule: " + rule.getId());
        } catch (SQLException e) {
            plugin.logError("Failed to save anticheat rule: " + rule.getId(), e);
            throw new RuntimeException("Database error saving anticheat rule: " + e.getMessage(), e);
        }
    }

    /**
     * Load anticheat rules from the database.
     */
    public void loadAnticheatRulesFromDatabase() {
        try {
            plugin.getDatabaseManager().query("""
                    SELECT rule_id, name, type, enabled, config FROM moderex_automod_rules
                    WHERE type = 'ANTICHEAT' AND rule_id IS NOT NULL
                    """,
                    rs -> {
                        while (rs.next()) {
                            String ruleId = rs.getString("rule_id");
                            if (ruleId == null || rules.containsKey(ruleId)) continue;

                            AutomodRule rule = new AutomodRule();
                            rule.setId(ruleId);
                            rule.setName(rs.getString("name"));
                            rule.setType(AutomodRule.RuleType.ANTICHEAT);
                            rule.setEnabled(rs.getBoolean("enabled"));

                            String config = rs.getString("config");
                            if (config != null) {
                                rule.loadConfigJson(config);
                            }

                            rules.put(ruleId, rule);
                        }
                        return null;
                    }
            );
        } catch (SQLException e) {
            plugin.logError("Failed to load anticheat rules from database", e);
        }
    }

    /**
     * Broadcast automod rules update to web panel clients (full refresh - use sparingly).
     */
    public void broadcastAutomodRulesUpdate() {
        if (plugin.getWebPanelServer() != null) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                plugin.getWebPanelServer().broadcastAutomodRules();
            });
        }
    }

    /**
     * Broadcast a single rule update to web panel clients (more efficient than full refresh).
     */
    public void broadcastSingleRuleUpdate(AutomodRule rule) {
        broadcastSingleRuleUpdate(rule, "System");
    }

    /**
     * Broadcast a single rule update to web panel clients (more efficient than full refresh).
     * @param rule The updated rule
     * @param by The name of who made the change (for toast exclusion)
     */
    public void broadcastSingleRuleUpdate(AutomodRule rule, String by) {
        if (plugin.getWebPanelServer() != null && rule != null) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                plugin.getWebPanelServer().broadcastSingleRuleUpdate(rule, by);
            });
        }
    }

    /**
     * Broadcast a rule deletion event to web panel clients.
     */
    public void broadcastRuleDeleted(String ruleId) {
        broadcastRuleDeleted(ruleId, "System");
    }

    /**
     * Broadcast a rule deletion event to web panel clients.
     * @param ruleId The ID of the deleted rule
     * @param by The name of who deleted it (for toast exclusion)
     */
    public void broadcastRuleDeleted(String ruleId, String by) {
        if (plugin.getWebPanelServer() != null && ruleId != null) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                plugin.getWebPanelServer().broadcastRuleDeleted(ruleId, by);
            });
        }
    }

    // === Getters ===

    public Collection<AutomodRule> getRules() {
        return rules.values();
    }

    public AutomodRule getRule(String id) {
        return rules.get(id);
    }

    public void clearPlayerData(UUID uuid) {
        messageTimestamps.remove(uuid);
        for (AutomodRule rule : rules.values()) {
            rule.resetViolations(uuid);
        }
    }

    // Legacy compatibility
    public boolean isSpamPreventionEnabled() {
        AutomodRule rule = rules.get("spam_protection");
        return rule != null && rule.isEnabled();
    }

    public void setSpamPreventionEnabled(boolean enabled) {
        AutomodRule rule = rules.get("spam_protection");
        if (rule != null) {
            rule.setEnabled(enabled);
            saveRule(rule);
        }
    }

    public boolean isCapsFilterEnabled() {
        AutomodRule rule = rules.get("caps_filter");
        return rule != null && rule.isEnabled();
    }

    public void setCapsFilterEnabled(boolean enabled) {
        AutomodRule rule = rules.get("caps_filter");
        if (rule != null) {
            rule.setEnabled(enabled);
            saveRule(rule);
        }
    }

    // Legacy createRule method
    public AutomodRule createRule(String name) {
        return createRule(name, AutomodRule.RuleType.WORD_FILTER);
    }

    /**
     * Filter result from processing a message.
     */
    public static class FilterResult {
        private final boolean blocked;
        private final boolean modified;
        private final String modifiedMessage;
        private final String reason;

        private FilterResult(boolean blocked, boolean modified, String modifiedMessage, String reason) {
            this.blocked = blocked;
            this.modified = modified;
            this.modifiedMessage = modifiedMessage;
            this.reason = reason;
        }

        public static FilterResult allow() {
            return new FilterResult(false, false, null, null);
        }

        public static FilterResult block(String reason) {
            return new FilterResult(true, false, null, reason);
        }

        public static FilterResult modify(String newMessage) {
            return new FilterResult(false, true, newMessage, null);
        }

        public boolean isBlocked() { return blocked; }
        public boolean isModified() { return modified; }
        public String getModifiedMessage() { return modifiedMessage; }
        public String getReason() { return reason; }
    }
}
