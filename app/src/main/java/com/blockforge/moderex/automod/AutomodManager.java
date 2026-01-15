package com.blockforge.moderex.automod;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.punishment.PunishmentType;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class AutomodManager {

    private final ModereX plugin;
    private final Map<String, AutomodRule> rules = new ConcurrentHashMap<>();
    private final Map<UUID, List<Long>> messageTimestamps = new ConcurrentHashMap<>();
    private final Map<UUID, String> lastMessages = new ConcurrentHashMap<>();

    // Spam prevention settings
    private boolean spamPreventionEnabled = true;
    private int maxMessagesPerSecond = 3;
    private int duplicateMessageThreshold = 2;
    private boolean capsFilterEnabled = true;
    private double maxCapsPercentage = 0.7;
    private int minCapsLength = 10;

    public AutomodManager(ModereX plugin) {
        this.plugin = plugin;
    }

    public void load() {
        rules.clear();

        // Load default rules
        loadDefaultRules();

        // Load custom rules from database
        loadCustomRules();

        plugin.getLogger().info("Loaded " + rules.size() + " automod rules.");
    }

    private void loadDefaultRules() {
        // Spam prevention rule (built-in, cannot be deleted)
        AutomodRule spamRule = new AutomodRule();
        spamRule.setId("spam_prevention");
        spamRule.setName("Spam Prevention");
        spamRule.setType(AutomodRule.RuleType.SPAM);
        spamRule.setBuiltIn(true);
        spamRule.setEnabled(spamPreventionEnabled);
        rules.put(spamRule.getId(), spamRule);

        // Caps filter rule (built-in)
        AutomodRule capsRule = new AutomodRule();
        capsRule.setId("caps_filter");
        capsRule.setName("Caps Filter");
        capsRule.setType(AutomodRule.RuleType.CAPS);
        capsRule.setBuiltIn(true);
        capsRule.setEnabled(capsFilterEnabled);
        rules.put(capsRule.getId(), capsRule);
    }

    private void loadCustomRules() {
        try {
            plugin.getDatabaseManager().query("""
                    SELECT * FROM moderex_automod_rules WHERE type = 'WORD_FILTER'
                    """,
                    rs -> {
                        while (rs.next()) {
                            AutomodRule rule = new AutomodRule();
                            rule.setId(String.valueOf(rs.getInt("id")));
                            rule.setName(rs.getString("name"));
                            rule.setType(AutomodRule.RuleType.WORD_FILTER);
                            rule.setEnabled(rs.getBoolean("enabled"));
                            rule.loadConfig(rs.getString("config"));
                            rules.put(rule.getId(), rule);
                        }
                        return null;
                    }
            );
        } catch (SQLException e) {
            plugin.logError("Failed to load automod rules", e);
        }
    }

    public FilterResult processMessage(Player player, String message) {
        // Bypass permission check
        if (player.hasPermission("moderex.bypass.automod")) {
            return FilterResult.allow();
        }

        UUID uuid = player.getUniqueId();

        // Check spam prevention
        if (spamPreventionEnabled) {
            FilterResult spamResult = checkSpam(uuid, message);
            if (spamResult.isBlocked()) {
                alertStaff(player, "Spam Prevention", message);
                return spamResult;
            }
        }

        // Check caps filter
        if (capsFilterEnabled) {
            FilterResult capsResult = checkCaps(message);
            if (capsResult.isModified()) {
                return capsResult;
            }
        }

        // Check word filter rules
        for (AutomodRule rule : rules.values()) {
            if (!rule.isEnabled() || rule.getType() != AutomodRule.RuleType.WORD_FILTER) {
                continue;
            }

            FilterResult result = checkWordFilter(rule, message);
            if (result.isBlocked()) {
                alertStaff(player, rule.getName(), message);
                handleAutoPunishment(player, rule);
                return result;
            }
        }

        // Store message for duplicate detection
        lastMessages.put(uuid, message.toLowerCase().replaceAll("\\s+", ""));

        return FilterResult.allow();
    }

    private FilterResult checkSpam(UUID uuid, String message) {
        long now = System.currentTimeMillis();

        // Check message rate
        List<Long> timestamps = messageTimestamps.computeIfAbsent(uuid, k -> new ArrayList<>());

        // Remove old timestamps (older than 1 second)
        timestamps.removeIf(t -> now - t > 1000);

        if (timestamps.size() >= maxMessagesPerSecond) {
            return FilterResult.block("spam");
        }

        timestamps.add(now);

        // Check duplicate messages
        String lastMessage = lastMessages.get(uuid);
        if (lastMessage != null) {
            String normalized = message.toLowerCase().replaceAll("\\s+", "");
            if (isSimilar(lastMessage, normalized)) {
                return FilterResult.block("duplicate");
            }
        }

        return FilterResult.allow();
    }

    private boolean isSimilar(String msg1, String msg2) {
        // Simple similarity check - can be enhanced with Levenshtein distance
        if (msg1.equals(msg2)) return true;

        // Check if one contains the other
        if (msg1.contains(msg2) || msg2.contains(msg1)) return true;

        // Check similarity ratio
        int maxLen = Math.max(msg1.length(), msg2.length());
        if (maxLen == 0) return true;

        int commonChars = countCommonChars(msg1, msg2);
        return (double) commonChars / maxLen > 0.8;
    }

    private int countCommonChars(String s1, String s2) {
        int count = 0;
        Map<Character, Integer> charCount = new HashMap<>();

        for (char c : s1.toCharArray()) {
            charCount.merge(c, 1, Integer::sum);
        }

        for (char c : s2.toCharArray()) {
            if (charCount.getOrDefault(c, 0) > 0) {
                count++;
                charCount.merge(c, -1, Integer::sum);
            }
        }

        return count;
    }

    private FilterResult checkCaps(String message) {
        if (message.length() < minCapsLength) {
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

        if (letterCount > 0 && (double) capsCount / letterCount > maxCapsPercentage) {
            // Convert to lowercase instead of blocking
            return FilterResult.modify(message.toLowerCase());
        }

        return FilterResult.allow();
    }

    private FilterResult checkWordFilter(AutomodRule rule, String message) {
        List<String> blacklistedWords = rule.getBlacklistedWords();
        List<String> exclusions = rule.getExclusionWords();
        boolean exactMatch = rule.isExactMatch();

        String lowerMessage = message.toLowerCase();

        for (String word : blacklistedWords) {
            String lowerWord = word.toLowerCase();

            if (exactMatch) {
                // Check for exact word match
                Pattern pattern = Pattern.compile("\\b" + Pattern.quote(lowerWord) + "\\b",
                        Pattern.CASE_INSENSITIVE);
                if (pattern.matcher(message).find()) {
                    // Check exclusions
                    boolean excluded = false;
                    for (String exclusion : exclusions) {
                        if (lowerMessage.contains(exclusion.toLowerCase())) {
                            excluded = true;
                            break;
                        }
                    }
                    if (!excluded) {
                        return FilterResult.block(rule.getName());
                    }
                }
            } else {
                // Check if message contains the word/phrase
                if (lowerMessage.contains(lowerWord)) {
                    // Check exclusions
                    boolean excluded = false;
                    for (String exclusion : exclusions) {
                        if (lowerMessage.contains(exclusion.toLowerCase())) {
                            excluded = true;
                            break;
                        }
                    }
                    if (!excluded) {
                        return FilterResult.block(rule.getName());
                    }
                }
            }
        }

        return FilterResult.allow();
    }

    private void handleAutoPunishment(Player player, AutomodRule rule) {
        AutomodRule.AutoPunishment autoPunish = rule.getAutoPunishment();
        if (autoPunish == null || !autoPunish.isEnabled()) {
            return;
        }

        // Track violations
        int violations = rule.incrementViolation(player.getUniqueId());

        if (violations >= autoPunish.getTriggerCount()) {
            // Reset violation count
            rule.resetViolations(player.getUniqueId());

            // Execute punishment
            String reason = "Automod: " + rule.getName();
            switch (autoPunish.getType()) {
                case MUTE -> plugin.getPunishmentManager().mute(
                        player.getUniqueId(), player.getName(), null, "Automod",
                        autoPunish.getDuration(), reason
                );
                case KICK -> plugin.getPunishmentManager().kick(
                        player.getUniqueId(), player.getName(), null, "Automod", reason
                );
                case BAN -> plugin.getPunishmentManager().ban(
                        player.getUniqueId(), player.getName(), null, "Automod",
                        autoPunish.getDuration(), reason
                );
                case WARN -> plugin.getPunishmentManager().warn(
                        player.getUniqueId(), player.getName(), null, "Automod",
                        autoPunish.getDuration(), reason
                );
            }
        }
    }

    private void alertStaff(Player player, String ruleName, String message) {
        Component alert = plugin.getLanguageManager().get(MessageKey.AUTOMOD_ALERT,
                "player", player.getName(),
                "rule", ruleName
        );

        for (Player staff : plugin.getServer().getOnlinePlayers()) {
            if (staff.hasPermission("moderex.notify.automod")) {
                staff.sendMessage(alert);
            }
        }
    }

    public AutomodRule createRule(String name) {
        AutomodRule rule = new AutomodRule();
        rule.setName(name);
        rule.setType(AutomodRule.RuleType.WORD_FILTER);
        rule.setEnabled(true);
        rule.setBuiltIn(false);

        try {
            plugin.getDatabaseManager().update("""
                    INSERT INTO moderex_automod_rules (name, type, enabled, config, created_at, updated_at)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """,
                    name, "WORD_FILTER", true, rule.toConfigString(),
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
            }
        } catch (SQLException e) {
            plugin.logError("Failed to create automod rule", e);
        }

        return rule;
    }

    public void saveRule(AutomodRule rule) {
        if (rule.isBuiltIn()) return;

        try {
            plugin.getDatabaseManager().update("""
                    UPDATE moderex_automod_rules
                    SET name = ?, enabled = ?, config = ?, updated_at = ?
                    WHERE id = ?
                    """,
                    rule.getName(), rule.isEnabled(), rule.toConfigString(),
                    System.currentTimeMillis(), Integer.parseInt(rule.getId())
            );
        } catch (SQLException e) {
            plugin.logError("Failed to save automod rule", e);
        }
    }

    public void deleteRule(String ruleId) {
        AutomodRule rule = rules.get(ruleId);
        if (rule == null || rule.isBuiltIn()) return;

        try {
            plugin.getDatabaseManager().update(
                    "DELETE FROM moderex_automod_rules WHERE id = ?",
                    Integer.parseInt(ruleId)
            );
            rules.remove(ruleId);
        } catch (SQLException e) {
            plugin.logError("Failed to delete automod rule", e);
        }
    }

    public Collection<AutomodRule> getRules() {
        return rules.values();
    }

    public AutomodRule getRule(String id) {
        return rules.get(id);
    }

    public void clearPlayerData(UUID uuid) {
        messageTimestamps.remove(uuid);
        lastMessages.remove(uuid);
    }

    // Settings methods
    public boolean isSpamPreventionEnabled() {
        return spamPreventionEnabled;
    }

    public void setSpamPreventionEnabled(boolean enabled) {
        this.spamPreventionEnabled = enabled;
        AutomodRule spamRule = rules.get("spam_prevention");
        if (spamRule != null) {
            spamRule.setEnabled(enabled);
        }
    }

    public boolean isCapsFilterEnabled() {
        return capsFilterEnabled;
    }

    public void setCapsFilterEnabled(boolean enabled) {
        this.capsFilterEnabled = enabled;
        AutomodRule capsRule = rules.get("caps_filter");
        if (capsRule != null) {
            capsRule.setEnabled(enabled);
        }
    }

    public void handleAnticheatAlert(Player player, String anticheat, String checkName,
                                     String checkType, int violations, double vlLevel) {
        // Check for anticheat-based automod rules
        for (AutomodRule rule : rules.values()) {
            if (!rule.isEnabled()) continue;
            if (rule.getType() != AutomodRule.RuleType.ANTICHEAT) continue;

            // Increment violation count
            int currentViolations = rule.incrementViolation(player.getUniqueId());

            // Check if auto-punishment should trigger
            AutomodRule.AutoPunishment punishment = rule.getAutoPunishment();
            if (punishment != null && punishment.isEnabled()) {
                if (currentViolations >= punishment.getTriggerCount()) {
                    executeAutoPunishment(player, punishment, checkName, vlLevel);
                    rule.resetViolations(player.getUniqueId());
                }
            }
        }
    }

    private void executeAutoPunishment(Player player, AutomodRule.AutoPunishment punishment,
                                       String checkName, double vlLevel) {
        String reason = "Automod: " + checkName + " (VL: " + (int) vlLevel + ")";
        String duration = punishment.getDuration() == -1 ? "permanent" :
                com.blockforge.moderex.util.DurationParser.format(punishment.getDuration());

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            switch (punishment.getType()) {
                case WARN -> plugin.getServer().dispatchCommand(
                        plugin.getServer().getConsoleSender(),
                        "warn " + player.getName() + " " + reason
                );
                case MUTE -> plugin.getServer().dispatchCommand(
                        plugin.getServer().getConsoleSender(),
                        "mute " + player.getName() + " " + duration + " " + reason
                );
                case KICK -> plugin.getServer().dispatchCommand(
                        plugin.getServer().getConsoleSender(),
                        "kick " + player.getName() + " " + reason
                );
                case BAN -> plugin.getServer().dispatchCommand(
                        plugin.getServer().getConsoleSender(),
                        "ban " + player.getName() + " " + duration + " " + reason
                );
                default -> {}
            }
        });
    }

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

        public boolean isBlocked() {
            return blocked;
        }

        public boolean isModified() {
            return modified;
        }

        public String getModifiedMessage() {
            return modifiedMessage;
        }

        public String getReason() {
            return reason;
        }
    }
}
