package com.blockforge.moderex.staff;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class StaffSettings {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private UUID staffUuid;
    private long updatedAt;

    // ========== Notification Settings ==========

    // Join/Leave messages (all, staff only, off)
    private JoinLeaveLevel joinLeaveMessages = JoinLeaveLevel.ALL;

    // Moderation action notifications
    private boolean moderationActionsEnabled = true;

    // Punishment notifications
    private AlertLevel punishmentAlerts = AlertLevel.EVERYONE;
    private AlertLevel warnAlerts = AlertLevel.EVERYONE;
    private AlertLevel banAlerts = AlertLevel.EVERYONE;
    private AlertLevel muteAlerts = AlertLevel.EVERYONE;
    private AlertLevel kickAlerts = AlertLevel.EVERYONE;
    private AlertLevel pardonAlerts = AlertLevel.EVERYONE;

    // Automod notifications
    private AlertLevel automodAlerts = AlertLevel.WATCHLIST_ONLY;
    private AlertLevel spamAlerts = AlertLevel.WATCHLIST_ONLY;
    private AlertLevel filterAlerts = AlertLevel.WATCHLIST_ONLY;

    // Anticheat notifications
    private AlertLevel anticheatAlerts = AlertLevel.EVERYONE;
    private int anticheatMinVL = 10;

    // Per-anticheat check preferences (which checks to show)
    private Map<String, AnticheatPreference> anticheatPreferences = new HashMap<>();

    // Staff chat
    private boolean staffChatEnabled = true;
    private boolean staffChatSound = true;

    // Watchlist notifications
    private boolean watchlistJoinAlerts = true;
    private boolean watchlistQuitAlerts = true;
    private boolean watchlistActivityAlerts = true;

    // Command monitoring
    private AlertLevel commandAlerts = AlertLevel.WATCHLIST_ONLY;
    private boolean showBlacklistedCommands = true;

    // Private message monitoring
    private AlertLevel privateMessageAlerts = AlertLevel.OFF;

    // ========== UI Settings ==========

    private boolean compactMode = false;
    private boolean soundEnabled = true;
    private boolean actionBarAlerts = false;
    private boolean chatAlerts = true;
    private boolean bossBarAlerts = false;

    // ========== Vanish Settings ==========

    private boolean autoVanishOnJoin = false;
    private boolean vanishNightVision = true;
    private boolean vanishShowSelf = true;

    public StaffSettings() {
        this.updatedAt = System.currentTimeMillis();
    }

    public StaffSettings(UUID staffUuid) {
        this.staffUuid = staffUuid;
        this.updatedAt = System.currentTimeMillis();
    }

    // ========== Serialization ==========

    public String toJson() {
        return GSON.toJson(this);
    }

    public static StaffSettings fromJson(String json) {
        return GSON.fromJson(json, StaffSettings.class);
    }

    public static StaffSettings fromJson(JsonObject json) {
        return GSON.fromJson(json, StaffSettings.class);
    }

    // ========== Alert Level Enum ==========

    public enum AlertLevel {
        EVERYONE("Everyone", "Receive alerts for all players"),
        WATCHLIST_ONLY("Watchlist Only", "Only receive alerts for watchlist players"),
        OFF("Off", "Do not receive these alerts");

        private final String displayName;
        private final String description;

        AlertLevel(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }

        public AlertLevel next() {
            AlertLevel[] values = values();
            return values[(ordinal() + 1) % values.length];
        }

        public String getColor() {
            return switch (this) {
                case EVERYONE -> "<green>";
                case WATCHLIST_ONLY -> "<yellow>";
                case OFF -> "<red>";
            };
        }
    }

    // ========== Join/Leave Level Enum ==========

    public enum JoinLeaveLevel {
        ALL("All Players", "See join/leave for everyone"),
        STAFF_ONLY("Staff Only", "Only see staff join/leave"),
        OFF("Off", "Hide all join/leave messages");

        private final String displayName;
        private final String description;

        JoinLeaveLevel(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }

        public JoinLeaveLevel next() {
            JoinLeaveLevel[] values = values();
            return values[(ordinal() + 1) % values.length];
        }

        public String getColor() {
            return switch (this) {
                case ALL -> "<green>";
                case STAFF_ONLY -> "<yellow>";
                case OFF -> "<red>";
            };
        }
    }

    // ========== Anticheat Preference Class ==========

    public static class AnticheatPreference {
        private String anticheat;
        private boolean enabled = true;
        private int minVL = 5;
        private Set<String> disabledChecks = new HashSet<>();

        public AnticheatPreference() {}

        public AnticheatPreference(String anticheat) {
            this.anticheat = anticheat;
        }

        public String getAnticheat() {
            return anticheat;
        }

        public void setAnticheat(String anticheat) {
            this.anticheat = anticheat;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getMinVL() {
            return minVL;
        }

        public void setMinVL(int minVL) {
            this.minVL = minVL;
        }

        public Set<String> getDisabledChecks() {
            return disabledChecks;
        }

        public void setDisabledChecks(Set<String> disabledChecks) {
            this.disabledChecks = disabledChecks;
        }

        public boolean isCheckEnabled(String checkName) {
            return enabled && !disabledChecks.contains(checkName.toLowerCase());
        }

        public void toggleCheck(String checkName) {
            String lower = checkName.toLowerCase();
            if (disabledChecks.contains(lower)) {
                disabledChecks.remove(lower);
            } else {
                disabledChecks.add(lower);
            }
        }
    }

    // ========== Per-Check Alert Preference ==========

    public static class CheckAlertPreference {
        private String checkKey; // format: "anticheat:checkname"
        private AlertLevel alertLevel = AlertLevel.OFF; // Default OFF (unconfigured)
        private int thresholdCount = 5; // Send alert after X triggers
        private int timeWindowSeconds = 60; // Within Y seconds

        public CheckAlertPreference() {}

        public CheckAlertPreference(String checkKey) {
            this.checkKey = checkKey;
        }

        public String getCheckKey() {
            return checkKey;
        }

        public void setCheckKey(String checkKey) {
            this.checkKey = checkKey;
        }

        public AlertLevel getAlertLevel() {
            return alertLevel;
        }

        public void setAlertLevel(AlertLevel alertLevel) {
            this.alertLevel = alertLevel;
        }

        public int getThresholdCount() {
            return thresholdCount;
        }

        public void setThresholdCount(int thresholdCount) {
            this.thresholdCount = Math.max(1, thresholdCount);
        }

        public int getTimeWindowSeconds() {
            return timeWindowSeconds;
        }

        public void setTimeWindowSeconds(int timeWindowSeconds) {
            this.timeWindowSeconds = Math.max(1, timeWindowSeconds);
        }

        public boolean isConfigured() {
            return alertLevel != AlertLevel.OFF;
        }
    }

    // Per-check alert preferences map (checkKey -> preference)
    private Map<String, CheckAlertPreference> checkAlertPreferences = new HashMap<>();

    public Map<String, CheckAlertPreference> getCheckAlertPreferences() {
        return checkAlertPreferences;
    }

    public CheckAlertPreference getCheckAlertPreference(String anticheat, String checkName) {
        String key = anticheat.toLowerCase() + ":" + checkName.toLowerCase();
        return checkAlertPreferences.computeIfAbsent(key, CheckAlertPreference::new);
    }

    public void setCheckAlertPreference(String anticheat, String checkName, CheckAlertPreference pref) {
        String key = anticheat.toLowerCase() + ":" + checkName.toLowerCase();
        pref.setCheckKey(key);
        checkAlertPreferences.put(key, pref);
    }

    // ========== Getters and Setters ==========

    public UUID getStaffUuid() {
        return staffUuid;
    }

    public void setStaffUuid(UUID staffUuid) {
        this.staffUuid = staffUuid;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Join/Leave messages
    public JoinLeaveLevel getJoinLeaveMessages() {
        return joinLeaveMessages;
    }

    public void setJoinLeaveMessages(JoinLeaveLevel joinLeaveMessages) {
        this.joinLeaveMessages = joinLeaveMessages;
    }

    // Moderation actions
    public boolean isModerationActionsEnabled() {
        return moderationActionsEnabled;
    }

    public void setModerationActionsEnabled(boolean moderationActionsEnabled) {
        this.moderationActionsEnabled = moderationActionsEnabled;
    }

    // Punishment alerts
    public AlertLevel getPunishmentAlerts() {
        return punishmentAlerts;
    }

    public void setPunishmentAlerts(AlertLevel punishmentAlerts) {
        this.punishmentAlerts = punishmentAlerts;
    }

    public AlertLevel getWarnAlerts() {
        return warnAlerts;
    }

    public void setWarnAlerts(AlertLevel warnAlerts) {
        this.warnAlerts = warnAlerts;
    }

    public AlertLevel getBanAlerts() {
        return banAlerts;
    }

    public void setBanAlerts(AlertLevel banAlerts) {
        this.banAlerts = banAlerts;
    }

    public AlertLevel getMuteAlerts() {
        return muteAlerts;
    }

    public void setMuteAlerts(AlertLevel muteAlerts) {
        this.muteAlerts = muteAlerts;
    }

    public AlertLevel getKickAlerts() {
        return kickAlerts;
    }

    public void setKickAlerts(AlertLevel kickAlerts) {
        this.kickAlerts = kickAlerts;
    }

    public AlertLevel getPardonAlerts() {
        return pardonAlerts;
    }

    public void setPardonAlerts(AlertLevel pardonAlerts) {
        this.pardonAlerts = pardonAlerts;
    }

    // Automod alerts
    public AlertLevel getAutomodAlerts() {
        return automodAlerts;
    }

    public void setAutomodAlerts(AlertLevel automodAlerts) {
        this.automodAlerts = automodAlerts;
    }

    public AlertLevel getSpamAlerts() {
        return spamAlerts;
    }

    public void setSpamAlerts(AlertLevel spamAlerts) {
        this.spamAlerts = spamAlerts;
    }

    public AlertLevel getFilterAlerts() {
        return filterAlerts;
    }

    public void setFilterAlerts(AlertLevel filterAlerts) {
        this.filterAlerts = filterAlerts;
    }

    // Anticheat alerts
    public AlertLevel getAnticheatAlerts() {
        return anticheatAlerts;
    }

    public void setAnticheatAlerts(AlertLevel anticheatAlerts) {
        this.anticheatAlerts = anticheatAlerts;
    }

    public int getAnticheatMinVL() {
        return anticheatMinVL;
    }

    public void setAnticheatMinVL(int anticheatMinVL) {
        this.anticheatMinVL = anticheatMinVL;
    }

    // Per-anticheat preferences
    public Map<String, AnticheatPreference> getAnticheatPreferences() {
        return anticheatPreferences;
    }

    public AnticheatPreference getAnticheatPreference(String anticheat) {
        return anticheatPreferences.computeIfAbsent(
            anticheat.toLowerCase(),
            k -> new AnticheatPreference(anticheat)
        );
    }

    public boolean isAnticheatCheckEnabled(String anticheat, String checkName, int vl) {
        AnticheatPreference pref = anticheatPreferences.get(anticheat.toLowerCase());
        if (pref == null) {
            return vl >= anticheatMinVL;
        }
        return pref.isCheckEnabled(checkName) && vl >= pref.getMinVL();
    }

    // Staff chat
    public boolean isStaffChatEnabled() {
        return staffChatEnabled;
    }

    public void setStaffChatEnabled(boolean staffChatEnabled) {
        this.staffChatEnabled = staffChatEnabled;
    }

    public boolean isStaffChatSound() {
        return staffChatSound;
    }

    public void setStaffChatSound(boolean staffChatSound) {
        this.staffChatSound = staffChatSound;
    }

    // Watchlist
    public boolean isWatchlistJoinAlerts() {
        return watchlistJoinAlerts;
    }

    public void setWatchlistJoinAlerts(boolean watchlistJoinAlerts) {
        this.watchlistJoinAlerts = watchlistJoinAlerts;
    }

    public boolean isWatchlistQuitAlerts() {
        return watchlistQuitAlerts;
    }

    public void setWatchlistQuitAlerts(boolean watchlistQuitAlerts) {
        this.watchlistQuitAlerts = watchlistQuitAlerts;
    }

    public boolean isWatchlistActivityAlerts() {
        return watchlistActivityAlerts;
    }

    public void setWatchlistActivityAlerts(boolean watchlistActivityAlerts) {
        this.watchlistActivityAlerts = watchlistActivityAlerts;
    }

    // Command monitoring
    public AlertLevel getCommandAlerts() {
        return commandAlerts;
    }

    public void setCommandAlerts(AlertLevel commandAlerts) {
        this.commandAlerts = commandAlerts;
    }

    public boolean isShowBlacklistedCommands() {
        return showBlacklistedCommands;
    }

    public void setShowBlacklistedCommands(boolean showBlacklistedCommands) {
        this.showBlacklistedCommands = showBlacklistedCommands;
    }

    // Private messages
    public AlertLevel getPrivateMessageAlerts() {
        return privateMessageAlerts;
    }

    public void setPrivateMessageAlerts(AlertLevel privateMessageAlerts) {
        this.privateMessageAlerts = privateMessageAlerts;
    }

    // UI settings
    public boolean isCompactMode() {
        return compactMode;
    }

    public void setCompactMode(boolean compactMode) {
        this.compactMode = compactMode;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public boolean isActionBarAlerts() {
        return actionBarAlerts;
    }

    public void setActionBarAlerts(boolean actionBarAlerts) {
        this.actionBarAlerts = actionBarAlerts;
    }

    public boolean isChatAlerts() {
        return chatAlerts;
    }

    public void setChatAlerts(boolean chatAlerts) {
        this.chatAlerts = chatAlerts;
    }

    public boolean isBossBarAlerts() {
        return bossBarAlerts;
    }

    public void setBossBarAlerts(boolean bossBarAlerts) {
        this.bossBarAlerts = bossBarAlerts;
    }

    // Vanish settings
    public boolean isAutoVanishOnJoin() {
        return autoVanishOnJoin;
    }

    public void setAutoVanishOnJoin(boolean autoVanishOnJoin) {
        this.autoVanishOnJoin = autoVanishOnJoin;
    }

    public boolean isVanishNightVision() {
        return vanishNightVision;
    }

    public void setVanishNightVision(boolean vanishNightVision) {
        this.vanishNightVision = vanishNightVision;
    }

    public boolean isVanishShowSelf() {
        return vanishShowSelf;
    }

    public void setVanishShowSelf(boolean vanishShowSelf) {
        this.vanishShowSelf = vanishShowSelf;
    }

    public boolean shouldShowAlert(AlertLevel level, boolean isOnWatchlist) {
        return switch (level) {
            case EVERYONE -> true;
            case WATCHLIST_ONLY -> isOnWatchlist;
            case OFF -> false;
        };
    }
}
