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

    // Join/Leave messages visibility (all, staff only, off) - for seeing join/leave messages
    private JoinLeaveLevel joinLeaveMessages = JoinLeaveLevel.ALL;

    // Join/Leave alerts (for staff alerts when players join/leave) - IN-GAME ONLY
    private AlertLevel joinLeaveAlerts = AlertLevel.EVERYONE;

    // Moderation action notifications
    private boolean moderationActionsEnabled = true;

    // Punishment notifications - Each type has Everyone, Watchlist Only, Off
    private AlertLevel punishmentAlerts = AlertLevel.EVERYONE;
    private AlertLevel warnAlerts = AlertLevel.EVERYONE;
    private AlertLevel banAlerts = AlertLevel.EVERYONE;
    private AlertLevel muteAlerts = AlertLevel.EVERYONE;
    private AlertLevel kickAlerts = AlertLevel.EVERYONE;
    private AlertLevel pardonAlerts = AlertLevel.EVERYONE;  // Unbans, unmutes, unwarns

    // Automod notifications
    private AlertLevel automodAlerts = AlertLevel.EVERYONE;
    private AlertLevel spamAlerts = AlertLevel.EVERYONE;
    private AlertLevel filterAlerts = AlertLevel.EVERYONE;

    // Nickname alerts - for inappropriate nickname detection
    private AlertLevel nicknameAlerts = AlertLevel.EVERYONE;

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

    // Command monitoring - with Blacklisted Only option
    private CommandAlertLevel commandAlerts = CommandAlertLevel.BLACKLISTED_ONLY;
    private boolean showBlacklistedCommands = true;

    // Private message monitoring
    private AlertLevel privateMessageAlerts = AlertLevel.OFF;

    // Lag/Server Status alerts - On/Off
    private boolean lagAlerts = true;

    // ========== Web Panel Notification Settings ==========

    private WebNotifyMode webNotifyPunishments = WebNotifyMode.TOAST;
    private WebNotifyMode webNotifyAutomod = WebNotifyMode.TOAST;
    private WebNotifyMode webNotifyAnticheat = WebNotifyMode.TOAST;
    private WebNotifyMode webNotifyWatchlist = WebNotifyMode.TOAST;
    private WebNotifyMode webNotifyStaffChat = WebNotifyMode.TOAST;
    private WebNotifyMode webNotifyCommands = WebNotifyMode.TOAST;
    private WebNotifyMode webNotifyNickname = WebNotifyMode.TOAST;
    private WebNotifyMode webNotifyLag = WebNotifyMode.TOAST;

    // Web panel toast position (top-right default)
    private ToastPosition webToastPosition = ToastPosition.TOP_RIGHT;

    // Web panel alert duration in seconds (default 10)
    private int webAlertDurationSeconds = 10;

    // Web panel sound settings per alert type
    private boolean webSoundPunishments = true;
    private boolean webSoundAutomod = true;
    private boolean webSoundAnticheat = true;
    private boolean webSoundWatchlist = true;
    private boolean webSoundStaffChat = true;
    private boolean webSoundCommands = true;
    private boolean webSoundNickname = true;
    private boolean webSoundLag = true;

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

    // ========== Web Notification Mode Enum ==========

    public enum WebNotifyMode {
        TOAST("Toast", "Show in-panel toast notifications"),
        BROWSER("Browser", "Show browser/system notifications"),
        OFF("Off", "No notifications");

        private final String displayName;
        private final String description;

        WebNotifyMode(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }

        public static WebNotifyMode fromString(String s) {
            if (s == null) return TOAST;
            return switch (s.toLowerCase()) {
                case "browser" -> BROWSER;
                case "off" -> OFF;
                default -> TOAST;
            };
        }
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

    // ========== Command Alert Level Enum ==========

    public enum CommandAlertLevel {
        EVERYONE("Everyone", "See all player commands"),
        WATCHLIST_ONLY("Watchlist Only", "Only see commands from watchlist players"),
        BLACKLISTED_ONLY("Blacklisted Only", "Only see blacklisted command attempts"),
        OFF("Off", "No command alerts");

        private final String displayName;
        private final String description;

        CommandAlertLevel(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }

        public CommandAlertLevel next() {
            CommandAlertLevel[] values = values();
            return values[(ordinal() + 1) % values.length];
        }

        public String getColor() {
            return switch (this) {
                case EVERYONE -> "<green>";
                case WATCHLIST_ONLY -> "<yellow>";
                case BLACKLISTED_ONLY -> "<gold>";
                case OFF -> "<red>";
            };
        }
    }

    // ========== Toast Position Enum ==========

    public enum ToastPosition {
        TOP_RIGHT("Top Right", "top-right"),
        BOTTOM_RIGHT("Bottom Right", "bottom-right"),
        TOP_LEFT("Top Left", "top-left"),
        BOTTOM_LEFT("Bottom Left", "bottom-left");

        private final String displayName;
        private final String cssClass;

        ToastPosition(String displayName, String cssClass) {
            this.displayName = displayName;
            this.cssClass = cssClass;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getCssClass() {
            return cssClass;
        }

        public ToastPosition next() {
            ToastPosition[] values = values();
            return values[(ordinal() + 1) % values.length];
        }

        public static ToastPosition fromString(String s) {
            if (s == null) return TOP_RIGHT;
            return switch (s.toLowerCase().replace("-", "_").replace(" ", "_")) {
                case "bottom_right" -> BOTTOM_RIGHT;
                case "top_left" -> TOP_LEFT;
                case "bottom_left" -> BOTTOM_LEFT;
                default -> TOP_RIGHT;
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
        private AlertLevel alertLevel = AlertLevel.EVERYONE; // Default ON for everyone
        private int thresholdCount = 1; // Send alert immediately (every alert)
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
            // Always returns true now since default is EVERYONE
            // This means all alerts show by default until staff customizes them
            return true;
        }
    }

    // Per-check alert preferences map (checkKey -> preference)
    private Map<String, CheckAlertPreference> checkAlertPreferences = new HashMap<>();

    // Custom anticheat presets per staff
    private Map<String, AnticheatPreset> customPresets = new HashMap<>();

    public static class AnticheatPreset {
        private String name;
        private AlertLevel alertLevel;
        private int thresholdCount;
        private int timeWindowSeconds;
        private long createdAt;

        public AnticheatPreset() {}

        public AnticheatPreset(String name, AlertLevel alertLevel, int thresholdCount, int timeWindowSeconds) {
            this.name = name;
            this.alertLevel = alertLevel;
            this.thresholdCount = thresholdCount;
            this.timeWindowSeconds = timeWindowSeconds;
            this.createdAt = System.currentTimeMillis();
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public AlertLevel getAlertLevel() { return alertLevel; }
        public void setAlertLevel(AlertLevel alertLevel) { this.alertLevel = alertLevel; }
        public int getThresholdCount() { return thresholdCount; }
        public void setThresholdCount(int thresholdCount) { this.thresholdCount = thresholdCount; }
        public int getTimeWindowSeconds() { return timeWindowSeconds; }
        public void setTimeWindowSeconds(int timeWindowSeconds) { this.timeWindowSeconds = timeWindowSeconds; }
        public long getCreatedAt() { return createdAt; }
        public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    }

    public Map<String, AnticheatPreset> getCustomPresets() {
        return customPresets;
    }

    public void saveCustomPreset(String name, AlertLevel level, int threshold, int window) {
        customPresets.put(name.toLowerCase(), new AnticheatPreset(name, level, threshold, window));
    }

    public AnticheatPreset getCustomPreset(String name) {
        return customPresets.get(name.toLowerCase());
    }

    public void deleteCustomPreset(String name) {
        customPresets.remove(name.toLowerCase());
    }

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

    // Join/Leave messages visibility
    public JoinLeaveLevel getJoinLeaveMessages() {
        return joinLeaveMessages;
    }

    public void setJoinLeaveMessages(JoinLeaveLevel joinLeaveMessages) {
        this.joinLeaveMessages = joinLeaveMessages;
    }

    // Join/Leave alerts (in-game only)
    public AlertLevel getJoinLeaveAlerts() {
        return joinLeaveAlerts;
    }

    public void setJoinLeaveAlerts(AlertLevel joinLeaveAlerts) {
        this.joinLeaveAlerts = joinLeaveAlerts;
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

    // Nickname alerts
    public AlertLevel getNicknameAlerts() {
        return nicknameAlerts;
    }

    public void setNicknameAlerts(AlertLevel nicknameAlerts) {
        this.nicknameAlerts = nicknameAlerts;
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
    public CommandAlertLevel getCommandAlerts() {
        return commandAlerts;
    }

    public void setCommandAlerts(CommandAlertLevel commandAlerts) {
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

    // Lag/Server status alerts
    public boolean isLagAlerts() {
        return lagAlerts;
    }

    public void setLagAlerts(boolean lagAlerts) {
        this.lagAlerts = lagAlerts;
    }

    // Web panel notification modes
    public WebNotifyMode getWebNotifyPunishments() {
        return webNotifyPunishments;
    }

    public void setWebNotifyPunishments(WebNotifyMode mode) {
        this.webNotifyPunishments = mode;
    }

    public WebNotifyMode getWebNotifyAutomod() {
        return webNotifyAutomod;
    }

    public void setWebNotifyAutomod(WebNotifyMode mode) {
        this.webNotifyAutomod = mode;
    }

    public WebNotifyMode getWebNotifyAnticheat() {
        return webNotifyAnticheat;
    }

    public void setWebNotifyAnticheat(WebNotifyMode mode) {
        this.webNotifyAnticheat = mode;
    }

    public WebNotifyMode getWebNotifyWatchlist() {
        return webNotifyWatchlist;
    }

    public void setWebNotifyWatchlist(WebNotifyMode mode) {
        this.webNotifyWatchlist = mode;
    }

    public WebNotifyMode getWebNotifyStaffChat() {
        return webNotifyStaffChat;
    }

    public void setWebNotifyStaffChat(WebNotifyMode mode) {
        this.webNotifyStaffChat = mode;
    }

    public WebNotifyMode getWebNotifyCommands() {
        return webNotifyCommands;
    }

    public void setWebNotifyCommands(WebNotifyMode mode) {
        this.webNotifyCommands = mode;
    }

    public WebNotifyMode getWebNotifyNickname() {
        return webNotifyNickname;
    }

    public void setWebNotifyNickname(WebNotifyMode mode) {
        this.webNotifyNickname = mode;
    }

    public WebNotifyMode getWebNotifyLag() {
        return webNotifyLag;
    }

    public void setWebNotifyLag(WebNotifyMode mode) {
        this.webNotifyLag = mode;
    }

    // Web toast position
    public ToastPosition getWebToastPosition() {
        return webToastPosition;
    }

    public void setWebToastPosition(ToastPosition position) {
        this.webToastPosition = position;
    }

    // Web alert duration
    public int getWebAlertDurationSeconds() {
        return webAlertDurationSeconds;
    }

    public void setWebAlertDurationSeconds(int seconds) {
        this.webAlertDurationSeconds = Math.max(1, Math.min(60, seconds));
    }

    // Web sound settings
    public boolean isWebSoundPunishments() {
        return webSoundPunishments;
    }

    public void setWebSoundPunishments(boolean enabled) {
        this.webSoundPunishments = enabled;
    }

    public boolean isWebSoundAutomod() {
        return webSoundAutomod;
    }

    public void setWebSoundAutomod(boolean enabled) {
        this.webSoundAutomod = enabled;
    }

    public boolean isWebSoundAnticheat() {
        return webSoundAnticheat;
    }

    public void setWebSoundAnticheat(boolean enabled) {
        this.webSoundAnticheat = enabled;
    }

    public boolean isWebSoundWatchlist() {
        return webSoundWatchlist;
    }

    public void setWebSoundWatchlist(boolean enabled) {
        this.webSoundWatchlist = enabled;
    }

    public boolean isWebSoundStaffChat() {
        return webSoundStaffChat;
    }

    public void setWebSoundStaffChat(boolean enabled) {
        this.webSoundStaffChat = enabled;
    }

    public boolean isWebSoundCommands() {
        return webSoundCommands;
    }

    public void setWebSoundCommands(boolean enabled) {
        this.webSoundCommands = enabled;
    }

    public boolean isWebSoundNickname() {
        return webSoundNickname;
    }

    public void setWebSoundNickname(boolean enabled) {
        this.webSoundNickname = enabled;
    }

    public boolean isWebSoundLag() {
        return webSoundLag;
    }

    public void setWebSoundLag(boolean enabled) {
        this.webSoundLag = enabled;
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

    public boolean shouldShowCommandAlert(CommandAlertLevel level, boolean isOnWatchlist, boolean isBlacklisted) {
        return switch (level) {
            case EVERYONE -> true;
            case WATCHLIST_ONLY -> isOnWatchlist;
            case BLACKLISTED_ONLY -> isBlacklisted;
            case OFF -> false;
        };
    }
}
