package com.blockforge.moderex.config;

public class Settings {

    // General settings
    private String language = "en_US";
    private String timezone = "America/Chicago";
    private boolean debugMode = false;
    private boolean dialogsEnabled = true;

    // Database settings
    private String databaseType = "sqlite"; // sqlite or mysql
    private String mysqlHost = "localhost";
    private int mysqlPort = 3306;
    private String mysqlDatabase = "moderex";
    private String mysqlUsername = "root";
    private String mysqlPassword = "";
    private int mysqlPoolSize = 10;

    // Web panel settings
    private boolean webPanelEnabled = false;
    private int webPanelPort = 8080;
    private String webPanelHost = ""; // Public host for clients (auto-detect if empty)
    private String webPanelServerName = "My Server"; // Server name shown in panel
    private String webPanelAuthToken = "";
    private boolean webPanelSamePort = false; // Use same port as Minecraft (not recommended)

    // AI Assistant settings
    private boolean aiEnabled = true;
    private String aiEndpoint = "http://localhost:11434/api/chat";
    private String aiModel = "devstral-2-123b-cloud";
    private String aiApiKey = "";

    // Proxy settings
    private boolean proxyEnabled = false;
    private String proxyType = "bungeecord"; // bungeecord or velocity

    // Update checker
    private boolean updateCheckerEnabled = true;

    // Anticheat settings
    private boolean anticheatAutoDetect = true;
    private String anticheatProvider = "auto"; // auto, grim, vulcan, matrix, etc.
    private boolean anticheatRebrandAlerts = true;
    private boolean anticheatBlockOriginalMessages = true;
    private boolean anticheatAlertsEnabled = true;

    // Staff settings
    private boolean staffChatSoundEnabled = true;
    private String staffChatSound = "ENTITY_ITEM_PICKUP";

    // Chat settings
    private int defaultSlowmodeSeconds = 0;
    private boolean chatEnabled = true;

    // Mute settings
    private boolean mutedPlayersCanUseCommands = false;
    private boolean mutedPlayersCanWriteSigns = false;

    // Vanish settings
    private boolean vanishHideFromTablist = true;
    private boolean vanishSilentContainers = true;
    private boolean vanishNoFootsteps = true;

    // Replay settings
    private boolean replayEnabled = true;
    private boolean replayRecordOnAnticheat = true;
    private boolean replayRecordWatchlist = true;
    private int replayNearbyRadius = 20;
    private int replayMaxDurationSeconds = 300;
    private int replayMaxStored = 1000;

    // Server status settings
    private boolean serverStatusEnabled = true;
    private boolean serverStatusAlertsEnabled = true;
    private double serverStatusTpsThreshold = 18.0;
    private int serverStatusChunkEntityThreshold = 100;
    private int serverStatusChunkTileThreshold = 50;

    // Config version for migration
    private int configVersion = 2;

    // Getters and setters
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public boolean isDialogsEnabled() {
        return dialogsEnabled;
    }

    public void setDialogsEnabled(boolean dialogsEnabled) {
        this.dialogsEnabled = dialogsEnabled;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public String getMysqlHost() {
        return mysqlHost;
    }

    public void setMysqlHost(String mysqlHost) {
        this.mysqlHost = mysqlHost;
    }

    public int getMysqlPort() {
        return mysqlPort;
    }

    public void setMysqlPort(int mysqlPort) {
        this.mysqlPort = mysqlPort;
    }

    public String getMysqlDatabase() {
        return mysqlDatabase;
    }

    public void setMysqlDatabase(String mysqlDatabase) {
        this.mysqlDatabase = mysqlDatabase;
    }

    public String getMysqlUsername() {
        return mysqlUsername;
    }

    public void setMysqlUsername(String mysqlUsername) {
        this.mysqlUsername = mysqlUsername;
    }

    public String getMysqlPassword() {
        return mysqlPassword;
    }

    public void setMysqlPassword(String mysqlPassword) {
        this.mysqlPassword = mysqlPassword;
    }

    public int getMysqlPoolSize() {
        return mysqlPoolSize;
    }

    public void setMysqlPoolSize(int mysqlPoolSize) {
        this.mysqlPoolSize = mysqlPoolSize;
    }

    public boolean isWebPanelEnabled() {
        return webPanelEnabled;
    }

    public void setWebPanelEnabled(boolean webPanelEnabled) {
        this.webPanelEnabled = webPanelEnabled;
    }

    public int getWebPanelPort() {
        return webPanelPort;
    }

    public void setWebPanelPort(int webPanelPort) {
        this.webPanelPort = webPanelPort;
    }

    public String getWebPanelHost() {
        return webPanelHost;
    }

    public void setWebPanelHost(String webPanelHost) {
        this.webPanelHost = webPanelHost;
    }

    public String getWebPanelServerName() {
        return webPanelServerName;
    }

    public void setWebPanelServerName(String webPanelServerName) {
        this.webPanelServerName = webPanelServerName;
    }

    public String getWebPanelAuthToken() {
        return webPanelAuthToken;
    }

    public void setWebPanelAuthToken(String webPanelAuthToken) {
        this.webPanelAuthToken = webPanelAuthToken;
    }

    public boolean isWebPanelSamePort() {
        return webPanelSamePort;
    }

    public void setWebPanelSamePort(boolean webPanelSamePort) {
        this.webPanelSamePort = webPanelSamePort;
    }

    public boolean isAiEnabled() {
        return aiEnabled;
    }

    public void setAiEnabled(boolean aiEnabled) {
        this.aiEnabled = aiEnabled;
    }

    public String getAiEndpoint() {
        return aiEndpoint;
    }

    public void setAiEndpoint(String aiEndpoint) {
        this.aiEndpoint = aiEndpoint;
    }

    public String getAiModel() {
        return aiModel;
    }

    public void setAiModel(String aiModel) {
        this.aiModel = aiModel;
    }

    public String getAiApiKey() {
        return aiApiKey;
    }

    public void setAiApiKey(String aiApiKey) {
        this.aiApiKey = aiApiKey;
    }

    public boolean isProxyEnabled() {
        return proxyEnabled;
    }

    public void setProxyEnabled(boolean proxyEnabled) {
        this.proxyEnabled = proxyEnabled;
    }

    public String getProxyType() {
        return proxyType;
    }

    public void setProxyType(String proxyType) {
        this.proxyType = proxyType;
    }

    public boolean isUpdateCheckerEnabled() {
        return updateCheckerEnabled;
    }

    public void setUpdateCheckerEnabled(boolean updateCheckerEnabled) {
        this.updateCheckerEnabled = updateCheckerEnabled;
    }

    public boolean isAnticheatAutoDetect() {
        return anticheatAutoDetect;
    }

    public void setAnticheatAutoDetect(boolean anticheatAutoDetect) {
        this.anticheatAutoDetect = anticheatAutoDetect;
    }

    public String getAnticheatProvider() {
        return anticheatProvider;
    }

    public void setAnticheatProvider(String anticheatProvider) {
        this.anticheatProvider = anticheatProvider;
    }

    public boolean isAnticheatRebrandAlerts() {
        return anticheatRebrandAlerts;
    }

    public void setAnticheatRebrandAlerts(boolean anticheatRebrandAlerts) {
        this.anticheatRebrandAlerts = anticheatRebrandAlerts;
    }

    public boolean isAnticheatBlockOriginalMessages() {
        return anticheatBlockOriginalMessages;
    }

    public void setAnticheatBlockOriginalMessages(boolean anticheatBlockOriginalMessages) {
        this.anticheatBlockOriginalMessages = anticheatBlockOriginalMessages;
    }

    public boolean isAnticheatAlertsEnabled() {
        return anticheatAlertsEnabled;
    }

    public void setAnticheatAlertsEnabled(boolean anticheatAlertsEnabled) {
        this.anticheatAlertsEnabled = anticheatAlertsEnabled;
    }

    public boolean isStaffChatSoundEnabled() {
        return staffChatSoundEnabled;
    }

    public void setStaffChatSoundEnabled(boolean staffChatSoundEnabled) {
        this.staffChatSoundEnabled = staffChatSoundEnabled;
    }

    public String getStaffChatSound() {
        return staffChatSound;
    }

    public void setStaffChatSound(String staffChatSound) {
        this.staffChatSound = staffChatSound;
    }

    public int getDefaultSlowmodeSeconds() {
        return defaultSlowmodeSeconds;
    }

    public void setDefaultSlowmodeSeconds(int defaultSlowmodeSeconds) {
        this.defaultSlowmodeSeconds = defaultSlowmodeSeconds;
    }

    public boolean isChatEnabled() {
        return chatEnabled;
    }

    public void setChatEnabled(boolean chatEnabled) {
        this.chatEnabled = chatEnabled;
    }

    public boolean isMutedPlayersCanUseCommands() {
        return mutedPlayersCanUseCommands;
    }

    public void setMutedPlayersCanUseCommands(boolean mutedPlayersCanUseCommands) {
        this.mutedPlayersCanUseCommands = mutedPlayersCanUseCommands;
    }

    public boolean isMutedPlayersCanWriteSigns() {
        return mutedPlayersCanWriteSigns;
    }

    public void setMutedPlayersCanWriteSigns(boolean mutedPlayersCanWriteSigns) {
        this.mutedPlayersCanWriteSigns = mutedPlayersCanWriteSigns;
    }

    public boolean isVanishHideFromTablist() {
        return vanishHideFromTablist;
    }

    public void setVanishHideFromTablist(boolean vanishHideFromTablist) {
        this.vanishHideFromTablist = vanishHideFromTablist;
    }

    public boolean isVanishSilentContainers() {
        return vanishSilentContainers;
    }

    public void setVanishSilentContainers(boolean vanishSilentContainers) {
        this.vanishSilentContainers = vanishSilentContainers;
    }

    public boolean isVanishNoFootsteps() {
        return vanishNoFootsteps;
    }

    public void setVanishNoFootsteps(boolean vanishNoFootsteps) {
        this.vanishNoFootsteps = vanishNoFootsteps;
    }

    public int getConfigVersion() {
        return configVersion;
    }

    public void setConfigVersion(int configVersion) {
        this.configVersion = configVersion;
    }

    public boolean isReplayEnabled() {
        return replayEnabled;
    }

    public void setReplayEnabled(boolean replayEnabled) {
        this.replayEnabled = replayEnabled;
    }

    public boolean isReplayRecordOnAnticheat() {
        return replayRecordOnAnticheat;
    }

    public void setReplayRecordOnAnticheat(boolean replayRecordOnAnticheat) {
        this.replayRecordOnAnticheat = replayRecordOnAnticheat;
    }

    public boolean isReplayRecordWatchlist() {
        return replayRecordWatchlist;
    }

    public void setReplayRecordWatchlist(boolean replayRecordWatchlist) {
        this.replayRecordWatchlist = replayRecordWatchlist;
    }

    public int getReplayNearbyRadius() {
        return replayNearbyRadius;
    }

    public void setReplayNearbyRadius(int replayNearbyRadius) {
        this.replayNearbyRadius = replayNearbyRadius;
    }

    public int getReplayMaxDurationSeconds() {
        return replayMaxDurationSeconds;
    }

    public void setReplayMaxDurationSeconds(int replayMaxDurationSeconds) {
        this.replayMaxDurationSeconds = replayMaxDurationSeconds;
    }

    public int getReplayMaxStored() {
        return replayMaxStored;
    }

    public void setReplayMaxStored(int replayMaxStored) {
        this.replayMaxStored = replayMaxStored;
    }

    public boolean isServerStatusEnabled() {
        return serverStatusEnabled;
    }

    public void setServerStatusEnabled(boolean serverStatusEnabled) {
        this.serverStatusEnabled = serverStatusEnabled;
    }

    public boolean isServerStatusAlertsEnabled() {
        return serverStatusAlertsEnabled;
    }

    public void setServerStatusAlertsEnabled(boolean serverStatusAlertsEnabled) {
        this.serverStatusAlertsEnabled = serverStatusAlertsEnabled;
    }

    public double getServerStatusTpsThreshold() {
        return serverStatusTpsThreshold;
    }

    public void setServerStatusTpsThreshold(double serverStatusTpsThreshold) {
        this.serverStatusTpsThreshold = serverStatusTpsThreshold;
    }

    public int getServerStatusChunkEntityThreshold() {
        return serverStatusChunkEntityThreshold;
    }

    public void setServerStatusChunkEntityThreshold(int serverStatusChunkEntityThreshold) {
        this.serverStatusChunkEntityThreshold = serverStatusChunkEntityThreshold;
    }

    public int getServerStatusChunkTileThreshold() {
        return serverStatusChunkTileThreshold;
    }

    public void setServerStatusChunkTileThreshold(int serverStatusChunkTileThreshold) {
        this.serverStatusChunkTileThreshold = serverStatusChunkTileThreshold;
    }
}
