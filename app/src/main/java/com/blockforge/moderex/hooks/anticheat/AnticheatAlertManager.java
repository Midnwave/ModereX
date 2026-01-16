package com.blockforge.moderex.hooks.anticheat;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.automod.AutomodRule;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.staff.StaffSettings;
import com.blockforge.moderex.util.DurationParser;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AnticheatAlertManager {

    private final ModereX plugin;

    // Per-check rules: anticheat:checkName -> AnticheatCheckRule
    private final Map<String, AnticheatCheckRule> checkRules = new ConcurrentHashMap<>();

    // Alert tracking: playerUUID:anticheat:checkName -> list of timestamps
    private final Map<String, List<Long>> alertHistory = new ConcurrentHashMap<>();

    // Cooldown tracking: playerUUID:anticheat:checkName -> last alert time
    private final Map<String, Long> lastAlertTime = new ConcurrentHashMap<>();

    // Default threshold settings
    private int defaultThresholdCount = 3;
    private long defaultThresholdDuration = 60000; // 60s
    private int cooldownSeconds = 5;
    private int defaultMinVL = 5;

    public AnticheatAlertManager(ModereX plugin) {
        this.plugin = plugin;
    }

    public void load() {
        loadConfig();
        loadCheckRules();
    }

    private void loadConfig() {
        ConfigurationSection ac = plugin.getConfig().getConfigurationSection("anticheat.alerts");
        if (ac != null) {
            defaultMinVL = ac.getInt("min-vl", 5);
            cooldownSeconds = ac.getInt("cooldown", 5);

            ConfigurationSection threshold = ac.getConfigurationSection("default-threshold");
            if (threshold != null) {
                defaultThresholdCount = threshold.getInt("count", 3);
                String duration = threshold.getString("duration", "60s");
                defaultThresholdDuration = DurationParser.parse(duration);
            }
        }
    }

    private void loadCheckRules() {
        checkRules.clear();
        try {
            plugin.getDatabaseManager().query("""
                SELECT * FROM moderex_anticheat_rules
                """,
                rs -> {
                    while (rs.next()) {
                        AnticheatCheckRule rule = new AnticheatCheckRule();
                        rule.setId(rs.getInt("id"));
                        rule.setAnticheat(rs.getString("anticheat"));
                        rule.setCheckName(rs.getString("check_name"));
                        rule.setEnabled(rs.getBoolean("enabled"));
                        rule.setMinVL(rs.getInt("min_vl"));
                        rule.setThresholdCount(rs.getInt("threshold_count"));
                        rule.setThresholdDuration(rs.getLong("threshold_duration"));
                        rule.loadAutoPunishment(rs.getString("auto_punishment"));
                        checkRules.put(rule.getKey(), rule);
                    }
                    return null;
                }
            );
        } catch (SQLException e) {
            plugin.logError("Failed to load anticheat rules", e);
        }
    }

    public boolean shouldShowAlert(Player target, String anticheat, String checkName, int vl) {
        String key = target.getUniqueId() + ":" + anticheat.toLowerCase() + ":" + checkName.toLowerCase();
        AnticheatCheckRule rule = getOrCreateRule(anticheat, checkName);

        if (!rule.isEnabled()) {
            return false;
        }

        // Check minimum VL
        int minVL = rule.getMinVL() > 0 ? rule.getMinVL() : getAnticheatMinVL(anticheat);
        if (vl < minVL) {
            return false;
        }

        // Check cooldown
        Long lastTime = lastAlertTime.get(key);
        long now = System.currentTimeMillis();
        if (lastTime != null && (now - lastTime) < cooldownSeconds * 1000L) {
            return false;
        }

        // Check threshold
        int thresholdCount = rule.getThresholdCount() > 0 ? rule.getThresholdCount() : defaultThresholdCount;
        long thresholdDuration = rule.getThresholdDuration() > 0 ? rule.getThresholdDuration() : defaultThresholdDuration;

        List<Long> history = alertHistory.computeIfAbsent(key, k -> new ArrayList<>());

        // Remove old entries
        history.removeIf(t -> now - t > thresholdDuration);

        // Add current
        history.add(now);

        // Check if threshold met
        if (history.size() >= thresholdCount) {
            lastAlertTime.put(key, now);
            return true;
        }

        return false;
    }

    public boolean shouldShowAlertToStaff(Player staff, Player target, String anticheat, String checkName, int vl) {
        StaffSettings settings = plugin.getStaffSettingsManager().getSettings(staff);

        // Check global anticheat alert level
        boolean isWatched = plugin.getWatchlistManager().isWatched(target.getUniqueId());
        if (!settings.shouldShowAlert(settings.getAnticheatAlerts(), isWatched)) {
            return false;
        }

        // Check per-anticheat preferences
        return settings.isAnticheatCheckEnabled(anticheat, checkName, vl);
    }

    public void processAlert(AnticheatHook.AnticheatAlert alert) {
        Player player = alert.getPlayer();
        String anticheat = alert.getAnticheat();
        String checkName = alert.getCheckName();
        int vl = (int) alert.getVlLevel();

        AnticheatCheckRule rule = getOrCreateRule(anticheat, checkName);

        // Track for auto-punishment
        if (rule.getAutoPunishment() != null && rule.getAutoPunishment().isEnabled()) {
            int violations = rule.incrementViolation(player.getUniqueId());

            if (violations >= rule.getAutoPunishment().getTriggerCount()) {
                executeAutoPunishment(player, rule, checkName, vl);
                rule.resetViolations(player.getUniqueId());
            }
        }
    }

    private void executeAutoPunishment(Player player, AnticheatCheckRule rule, String checkName, int vl) {
        AutomodRule.AutoPunishment punishment = rule.getAutoPunishment();
        String reason = "Anticheat: " + checkName + " (VL: " + vl + ")";
        String duration = punishment.getDuration() == -1 ? "permanent" :
                DurationParser.format(punishment.getDuration());

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

    private int getAnticheatMinVL(String anticheat) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("anticheat." + anticheat.toLowerCase());
        if (section != null) {
            return section.getInt("min-vl", defaultMinVL);
        }
        return defaultMinVL;
    }

    public AnticheatCheckRule getOrCreateRule(String anticheat, String checkName) {
        String key = anticheat.toLowerCase() + ":" + checkName.toLowerCase();
        return checkRules.computeIfAbsent(key, k -> {
            AnticheatCheckRule rule = new AnticheatCheckRule();
            rule.setAnticheat(anticheat);
            rule.setCheckName(checkName);
            rule.setEnabled(true);
            rule.setMinVL(0);
            rule.setThresholdCount(0);
            rule.setThresholdDuration(0);
            saveRule(rule);
            return rule;
        });
    }

    public void saveRule(AnticheatCheckRule rule) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                if (rule.getId() == 0) {
                    plugin.getDatabaseManager().update("""
                        INSERT INTO moderex_anticheat_rules
                        (anticheat, check_name, enabled, min_vl, threshold_count, threshold_duration, auto_punishment)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                        """,
                        rule.getAnticheat(), rule.getCheckName(), rule.isEnabled(),
                        rule.getMinVL(), rule.getThresholdCount(), rule.getThresholdDuration(),
                        rule.toAutoPunishmentJson()
                    );

                    Integer id = plugin.getDatabaseManager().query(
                        "SELECT last_insert_rowid() as id",
                        rs -> rs.next() ? rs.getInt("id") : null
                    );
                    if (id != null) {
                        rule.setId(id);
                    }
                } else {
                    plugin.getDatabaseManager().update("""
                        UPDATE moderex_anticheat_rules
                        SET enabled = ?, min_vl = ?, threshold_count = ?, threshold_duration = ?, auto_punishment = ?
                        WHERE id = ?
                        """,
                        rule.isEnabled(), rule.getMinVL(), rule.getThresholdCount(),
                        rule.getThresholdDuration(), rule.toAutoPunishmentJson(), rule.getId()
                    );
                }
            } catch (SQLException e) {
                plugin.logError("Failed to save anticheat rule", e);
            }
        });
    }

    public Collection<AnticheatCheckRule> getRules() {
        return checkRules.values();
    }

    public Collection<AnticheatCheckRule> getRulesForAnticheat(String anticheat) {
        List<AnticheatCheckRule> rules = new ArrayList<>();
        String prefix = anticheat.toLowerCase() + ":";
        for (Map.Entry<String, AnticheatCheckRule> entry : checkRules.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                rules.add(entry.getValue());
            }
        }
        return rules;
    }

    /**
     * Get all known checks for an anticheat (from registry + detected)
     * This includes predefined checks that haven't been detected yet
     */
    public List<CheckWithInfo> getAllChecksForAnticheat(String anticheat) {
        List<CheckWithInfo> result = new ArrayList<>();
        Set<String> addedChecks = new HashSet<>();

        // First add all predefined checks from the registry
        for (AnticheatChecks.CheckInfo info : AnticheatChecks.getChecks(anticheat)) {
            AnticheatCheckRule rule = checkRules.get(anticheat.toLowerCase() + ":" + info.getName());
            result.add(new CheckWithInfo(info, rule));
            addedChecks.add(info.getName());
        }

        // Then add any detected checks that aren't in the registry
        String prefix = anticheat.toLowerCase() + ":";
        for (Map.Entry<String, AnticheatCheckRule> entry : checkRules.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                String checkName = entry.getKey().substring(prefix.length());
                if (!addedChecks.contains(checkName)) {
                    // Create a dynamic CheckInfo for this detected check
                    AnticheatChecks.Category category = AnticheatChecks.getCategory(anticheat, checkName);
                    AnticheatChecks.CheckInfo dynamicInfo = new AnticheatChecks.CheckInfo(
                        checkName, checkName, category, "Detected check"
                    );
                    result.add(new CheckWithInfo(dynamicInfo, entry.getValue()));
                }
            }
        }

        // Sort by category then name
        result.sort((a, b) -> {
            int catCompare = a.getInfo().getCategory().ordinal() - b.getInfo().getCategory().ordinal();
            if (catCompare != 0) return catCompare;
            return a.getInfo().getDisplayName().compareToIgnoreCase(b.getInfo().getDisplayName());
        });

        return result;
    }

    /**
     * Get all checks grouped by category
     */
    public Map<AnticheatChecks.Category, List<CheckWithInfo>> getChecksByCategory(String anticheat) {
        Map<AnticheatChecks.Category, List<CheckWithInfo>> result = new LinkedHashMap<>();
        for (AnticheatChecks.Category cat : AnticheatChecks.Category.values()) {
            result.put(cat, new ArrayList<>());
        }

        for (CheckWithInfo check : getAllChecksForAnticheat(anticheat)) {
            result.get(check.getInfo().getCategory()).add(check);
        }

        // Remove empty categories
        result.entrySet().removeIf(e -> e.getValue().isEmpty());

        return result;
    }

    /**
     * Wrapper class combining check info with its rule (if any)
     */
    public static class CheckWithInfo {
        private final AnticheatChecks.CheckInfo info;
        private final AnticheatCheckRule rule;

        public CheckWithInfo(AnticheatChecks.CheckInfo info, AnticheatCheckRule rule) {
            this.info = info;
            this.rule = rule;
        }

        public AnticheatChecks.CheckInfo getInfo() {
            return info;
        }

        public AnticheatCheckRule getRule() {
            return rule;
        }

        public boolean hasRule() {
            return rule != null;
        }

        public boolean isEnabled() {
            return rule == null || rule.isEnabled();
        }

        public boolean wasDetected() {
            return rule != null;
        }
    }

    public void clearPlayerData(UUID uuid) {
        String prefix = uuid.toString() + ":";
        alertHistory.keySet().removeIf(k -> k.startsWith(prefix));
        lastAlertTime.keySet().removeIf(k -> k.startsWith(prefix));
    }

    public static class AnticheatCheckRule {
        private int id;
        private String anticheat;
        private String checkName;
        private boolean enabled = true;
        private int minVL = 0;
        private int thresholdCount = 0;
        private long thresholdDuration = 0;
        private AutomodRule.AutoPunishment autoPunishment;

        private final Map<UUID, Integer> violations = new ConcurrentHashMap<>();
        private final Map<UUID, Long> violationTimes = new ConcurrentHashMap<>();

        public String getKey() {
            return anticheat.toLowerCase() + ":" + checkName.toLowerCase();
        }

        public int incrementViolation(UUID uuid) {
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

        public String toAutoPunishmentJson() {
            if (autoPunishment == null) return null;
            return String.format(
                "{\"enabled\":%b,\"type\":\"%s\",\"duration\":%d,\"triggerCount\":%d,\"timeWindow\":%d}",
                autoPunishment.isEnabled(), autoPunishment.getType().name(),
                autoPunishment.getDuration(), autoPunishment.getTriggerCount(),
                autoPunishment.getTimeWindow()
            );
        }

        public void loadAutoPunishment(String json) {
            if (json == null || json.isEmpty()) return;
            try {
                com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
                autoPunishment = new AutomodRule.AutoPunishment();
                autoPunishment.setEnabled(obj.get("enabled").getAsBoolean());
                autoPunishment.setType(PunishmentType.valueOf(obj.get("type").getAsString()));
                autoPunishment.setDuration(obj.get("duration").getAsLong());
                autoPunishment.setTriggerCount(obj.get("triggerCount").getAsInt());
                autoPunishment.setTimeWindow(obj.get("timeWindow").getAsLong());
            } catch (Exception ignored) {}
        }

        // Getters and setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getAnticheat() { return anticheat; }
        public void setAnticheat(String anticheat) { this.anticheat = anticheat; }
        public String getCheckName() { return checkName; }
        public void setCheckName(String checkName) { this.checkName = checkName; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public int getMinVL() { return minVL; }
        public void setMinVL(int minVL) { this.minVL = minVL; }
        public int getThresholdCount() { return thresholdCount; }
        public void setThresholdCount(int thresholdCount) { this.thresholdCount = thresholdCount; }
        public long getThresholdDuration() { return thresholdDuration; }
        public void setThresholdDuration(long thresholdDuration) { this.thresholdDuration = thresholdDuration; }
        public AutomodRule.AutoPunishment getAutoPunishment() { return autoPunishment; }
        public void setAutoPunishment(AutomodRule.AutoPunishment autoPunishment) { this.autoPunishment = autoPunishment; }
    }
}
