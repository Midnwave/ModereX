package com.blockforge.moderex.config;

public class Settings {

    // General settings
    private String language = "en_US";
    private String timezone = "America/Chicago";
    private boolean debugMode = false;
    private String debugWebhookUrl = "";
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

    // GitHub auto-update settings
    private boolean githubAutoUpdateEnabled = true;
    private boolean githubAutoDownload = true;
    private String githubRepo = "Midnwave/ModereX"; // owner/repo format
    private String githubToken = ""; // PAT for private repos

    // Anticheat settings
    private boolean anticheatAutoDetect = true;
    private String anticheatProvider = "auto"; // auto, grim, vulcan, matrix, etc.
    private boolean anticheatRebrandAlerts = true;
    private boolean anticheatBlockOriginalMessages = true;
    private boolean anticheatAlertsEnabled = true;

    // Staff settings
    private boolean staffChatSoundEnabled = true;
    private String staffChatSound = "ENTITY_ITEM_PICKUP";

    // Join/Leave message settings
    private String joinLeaveVisibility = "ALL"; // ALL, MODERATORS_ONLY, OFF
    private boolean joinLeaveSuppressVanilla = true;
    private String joinLeaveJoinFormat = "<gray><player_prefix><yellow><player></yellow> joined the server";
    private String joinLeaveLeaveFormat = "<gray><player_prefix><yellow><player></yellow> left the server";

    // Private messages visibility (staff setting)
    private String privateMessagesVisibility = "ENABLED"; // ENABLED, WATCHLIST_ONLY, OFF

    // Chat settings
    private int defaultSlowmodeSeconds = 0;
    private boolean chatEnabled = true;

    // Automod settings
    private boolean spamPreventionEnabled = true;
    private boolean capsFilterEnabled = true;
    private boolean linkFilterEnabled = true;

    // Mute settings - what muted players cannot do
    private boolean mutedPlayersCanUseCommands = false;
    private boolean mutedPlayersCanWriteSigns = false;
    private boolean muteBlocksChat = true;
    private boolean muteBlocksMsg = true;
    private boolean muteBlocksSigns = true;
    private boolean muteBlocksBooks = true;
    private boolean muteBlocksBroadcast = false;
    private boolean muteBlocksVoice = true;
    private boolean muteBlocksVoiceJoin = true;

    // Warn settings
    private boolean warnNotifyStaff = true;
    private boolean warnAutoEscalate = false;

    // Command blacklist settings
    private boolean cmdBlacklistAllowModerexCommands = false;
    private java.util.List<String> cmdBlacklistProtectedCommands = java.util.List.of(
        "stop", "reload", "op", "deop", "whitelist"
    );

    // Vanish settings
    private boolean vanishHideFromTablist = true;
    private boolean vanishSilentContainers = true;
    private boolean vanishNoFootsteps = true;
    private boolean vanishHideRealJoinLeave = true;
    private boolean vanishSaveVanishState = true;
    private boolean vanishUsePacketLevel = true;
    private boolean vanishDebugPacketFiltering = false;
    private java.util.List<String> vanishApiWhitelist = new java.util.ArrayList<>();

    // Fake messages settings
    private boolean vanishFakeMessagesEnabled = false;
    private String vanishFakeJoinMessage = "&e{player} joined the game";
    private String vanishFakeLeaveMessage = "&e{player} left the game";

    // Plugin hooks settings
    private boolean vanishHookEssentialsXEnabled = false;
    private boolean vanishHookEssentialsXHideMessages = true;
    private boolean vanishHookCustomJoinMessagesEnabled = false;
    private boolean vanishHookCustomJoinMessagesHideMessages = true;
    private boolean vanishHookAlonsoJoinEnabled = false;
    private boolean vanishHookAlonsoJoinHideMessages = true;

    // Level-based visibility settings
    private boolean vanishLevelsEnabled = true;
    private int vanishLevelsDefaultLevel = 1;
    private int vanishLevelsOpLevel = 100;
    private boolean vanishLevelsStaffCanSeeLowerLevels = true;

    // LuckPerms integration settings
    private boolean vanishLuckPermsEnabled = false;
    private String vanishLuckPermsVanishPrefix = "&7[V] ";
    private String vanishLuckPermsVanishSuffix = "";
    private boolean vanishLuckPermsModifyTab = true;

    // Vanish list settings
    private boolean vanishListRespectVisibility = true;
    private boolean vanishListShowLevels = true;
    private String vanishListFormat = "&7- &f{player} &8(Level {level})";

    // Replay settings
    private boolean replayEnabled = true;
    private boolean replayRecordOnAnticheat = true;
    private boolean replayRecordWatchlist = true;
    private int replayNearbyRadius = 20;
    private int replayMaxDurationSeconds = 300;
    private int replayMaxStored = 1000;
    private boolean replayBlockLoggingEnabled = true;
    private boolean replayFakeBlocksEnabled = true;
    private boolean replayUsePhysicalBlocks = true; // When true, physically place/break blocks instead of fake packets
    private boolean replaySoundsEnabled = true;
    private boolean replayAnimationsEnabled = true;
    private int replayBlockLogMaxPerSession = 10000;
    private int replayPlayerSafetyRadius = 30; // Radius to scan for players to teleport away from replay area

    // Replay logging types (all enabled by default)
    private boolean replayLogMovement = true;
    private boolean replayLogBlocks = true;
    private boolean replayLogChat = true;
    private boolean replayLogCommands = true;
    private boolean replayLogCombat = true;
    private boolean replayLogInventory = true;
    private boolean replayLogItems = true; // pickup, drop, use, consume
    private boolean replayLogEquipment = true; // armor, held items
    private boolean replayLogEntities = true; // entity interactions
    private boolean replayLogTeleports = true;
    private boolean replayLogDeaths = true;

    // Entity tracking
    private boolean replayEntityTrackingEnabled = true;
    private int replayEntityTrackingRadius = 30;
    private boolean replayEntityDropUninvolved = false; // Drop entities not involved in actions (saves space)
    private int replayEntityLogMaxPerSession = 5000;

    // Storage optimization
    private boolean replayCompressEquipment = true; // Only store equipment when it changes
    private boolean replayCompressMovement = true; // Skip duplicate positions
    private int replaySnapshotInterval = 2; // Ticks between snapshots (lower = more detailed but larger files)

    // Server status settings
    private boolean serverStatusEnabled = true;
    private boolean serverStatusAlertsEnabled = true;
    private double serverStatusTpsThreshold = 18.0;
    private int serverStatusChunkEntityThreshold = 100;
    private int serverStatusChunkTileThreshold = 50;

    // Activity log settings
    private boolean activityLogEnabled = true;
    private String activityLogStorageType = "database"; // database, h2, yml, json, text
    private int activityLogEntriesPerPage = 10;
    private long activityLogRetentionDays = 30; // Days to keep logs, 0 for unlimited (legacy)
    private int maxChatLogs = 50; // Max chat logs to show in command output
    private int maxCommandLogs = 100; // Max commands to show in /cmdhistory output
    private boolean activityLogChat = true;
    private boolean activityLogCommands = true;
    private boolean activityLogSigns = true;
    private boolean activityLogItems = true;
    private boolean activityLogAnvils = true;
    private boolean activityLogSessions = true;
    private boolean activityLogUsernames = true;

    // Individual retention periods (in days, -1 = forever, 0 = disable)
    private long retentionChat = 30;
    private long retentionCommands = 30;
    private long retentionSigns = 30;
    private long retentionSessions = 30;
    private long retentionItems = 30;
    private long retentionAnvils = 30;
    private long retentionUsernames = -1; // Keep forever
    private long retentionIpChanges = -1; // Keep forever
    private long retentionPunishments = -1; // Keep forever
    private long retentionPardons = -1; // Keep forever
    private long retentionAutomod = 30;
    private long retentionAnticheat = 30;
    private long retentionStaffActions = 90;
    private long retentionWatchlist = 90;

    // Evidence system settings
    private int evidenceMaxFileSizeMb = 250;
    private boolean evidenceRequireEvidence = false;
    private int evidenceMaxActivityLogEntries = 5;

    // Player portal settings
    private boolean playerPortalEnabled = true;
    private int playerPortalSessionExpiryHours = 12;

    // Gateway settings
    private boolean gatewayEnabled = true;
    private String gatewayUrl = "wss://gateway.moderex.net/server";
    private int gatewayReconnectDelaySeconds = 5;
    private int gatewayMaxReconnectAttempts = -1; // -1 = infinite
    private boolean gatewayDebugLogging = false;

    // Config version for migration
    private int configVersion = 2;

    // Rank colors for web panel (LuckPerms group name -> hex color)
    private java.util.Map<String, String> rankColors = new java.util.HashMap<>();

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

    public String getDebugWebhookUrl() {
        return debugWebhookUrl;
    }

    public void setDebugWebhookUrl(String debugWebhookUrl) {
        this.debugWebhookUrl = debugWebhookUrl;
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

    public boolean isGithubAutoUpdateEnabled() {
        return githubAutoUpdateEnabled;
    }

    public void setGithubAutoUpdateEnabled(boolean githubAutoUpdateEnabled) {
        this.githubAutoUpdateEnabled = githubAutoUpdateEnabled;
    }

    public boolean isGithubAutoDownload() {
        return githubAutoDownload;
    }

    public void setGithubAutoDownload(boolean githubAutoDownload) {
        this.githubAutoDownload = githubAutoDownload;
    }

    public String getGithubRepo() {
        return githubRepo;
    }

    public void setGithubRepo(String githubRepo) {
        this.githubRepo = githubRepo;
    }

    public String getGithubToken() {
        return githubToken;
    }

    public void setGithubToken(String githubToken) {
        this.githubToken = githubToken;
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

    // Mute blocks getters and setters
    public boolean isMuteBlocksChat() {
        return muteBlocksChat;
    }

    public void setMuteBlocksChat(boolean muteBlocksChat) {
        this.muteBlocksChat = muteBlocksChat;
    }

    public boolean isMuteBlocksMsg() {
        return muteBlocksMsg;
    }

    public void setMuteBlocksMsg(boolean muteBlocksMsg) {
        this.muteBlocksMsg = muteBlocksMsg;
    }

    public boolean isMuteBlocksSigns() {
        return muteBlocksSigns;
    }

    public void setMuteBlocksSigns(boolean muteBlocksSigns) {
        this.muteBlocksSigns = muteBlocksSigns;
    }

    public boolean isMuteBlocksBooks() {
        return muteBlocksBooks;
    }

    public void setMuteBlocksBooks(boolean muteBlocksBooks) {
        this.muteBlocksBooks = muteBlocksBooks;
    }

    public boolean isMuteBlocksBroadcast() {
        return muteBlocksBroadcast;
    }

    public void setMuteBlocksBroadcast(boolean muteBlocksBroadcast) {
        this.muteBlocksBroadcast = muteBlocksBroadcast;
    }

    public boolean isMuteBlocksVoice() {
        return muteBlocksVoice;
    }

    public void setMuteBlocksVoice(boolean muteBlocksVoice) {
        this.muteBlocksVoice = muteBlocksVoice;
    }

    public boolean isMuteBlocksVoiceJoin() {
        return muteBlocksVoiceJoin;
    }

    public void setMuteBlocksVoiceJoin(boolean muteBlocksVoiceJoin) {
        this.muteBlocksVoiceJoin = muteBlocksVoiceJoin;
    }

    // Warn settings getters and setters
    public boolean isWarnNotifyStaff() {
        return warnNotifyStaff;
    }

    public void setWarnNotifyStaff(boolean warnNotifyStaff) {
        this.warnNotifyStaff = warnNotifyStaff;
    }

    public boolean isWarnAutoEscalate() {
        return warnAutoEscalate;
    }

    public void setWarnAutoEscalate(boolean warnAutoEscalate) {
        this.warnAutoEscalate = warnAutoEscalate;
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

    public boolean isVanishHideRealJoinLeave() {
        return vanishHideRealJoinLeave;
    }

    public void setVanishHideRealJoinLeave(boolean vanishHideRealJoinLeave) {
        this.vanishHideRealJoinLeave = vanishHideRealJoinLeave;
    }

    public boolean isVanishSaveVanishState() {
        return vanishSaveVanishState;
    }

    public void setVanishSaveVanishState(boolean vanishSaveVanishState) {
        this.vanishSaveVanishState = vanishSaveVanishState;
    }

    public boolean isVanishUsePacketLevel() {
        return vanishUsePacketLevel;
    }

    public void setVanishUsePacketLevel(boolean vanishUsePacketLevel) {
        this.vanishUsePacketLevel = vanishUsePacketLevel;
    }

    public boolean isVanishDebugPacketFiltering() {
        return vanishDebugPacketFiltering;
    }

    public void setVanishDebugPacketFiltering(boolean vanishDebugPacketFiltering) {
        this.vanishDebugPacketFiltering = vanishDebugPacketFiltering;
    }

    public java.util.List<String> getVanishApiWhitelist() {
        return vanishApiWhitelist;
    }

    public void setVanishApiWhitelist(java.util.List<String> vanishApiWhitelist) {
        this.vanishApiWhitelist = vanishApiWhitelist;
    }

    public int getConfigVersion() {
        return configVersion;
    }

    public void setConfigVersion(int configVersion) {
        this.configVersion = configVersion;
    }

    public java.util.Map<String, String> getRankColors() {
        return rankColors;
    }

    public void setRankColors(java.util.Map<String, String> rankColors) {
        this.rankColors = rankColors;
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

    public boolean isReplayBlockLoggingEnabled() {
        return replayBlockLoggingEnabled;
    }

    public void setReplayBlockLoggingEnabled(boolean replayBlockLoggingEnabled) {
        this.replayBlockLoggingEnabled = replayBlockLoggingEnabled;
    }

    public boolean isReplayFakeBlocksEnabled() {
        return replayFakeBlocksEnabled;
    }

    public void setReplayFakeBlocksEnabled(boolean replayFakeBlocksEnabled) {
        this.replayFakeBlocksEnabled = replayFakeBlocksEnabled;
    }

    public boolean isReplayUsePhysicalBlocks() {
        return replayUsePhysicalBlocks;
    }

    public void setReplayUsePhysicalBlocks(boolean replayUsePhysicalBlocks) {
        this.replayUsePhysicalBlocks = replayUsePhysicalBlocks;
    }

    public int getReplayPlayerSafetyRadius() {
        return replayPlayerSafetyRadius;
    }

    public void setReplayPlayerSafetyRadius(int replayPlayerSafetyRadius) {
        this.replayPlayerSafetyRadius = replayPlayerSafetyRadius;
    }

    // Logging type getters and setters
    public boolean isReplayLogMovement() { return replayLogMovement; }
    public void setReplayLogMovement(boolean v) { this.replayLogMovement = v; }

    public boolean isReplayLogBlocks() { return replayLogBlocks; }
    public void setReplayLogBlocks(boolean v) { this.replayLogBlocks = v; }

    public boolean isReplayLogChat() { return replayLogChat; }
    public void setReplayLogChat(boolean v) { this.replayLogChat = v; }

    public boolean isReplayLogCommands() { return replayLogCommands; }
    public void setReplayLogCommands(boolean v) { this.replayLogCommands = v; }

    public boolean isReplayLogCombat() { return replayLogCombat; }
    public void setReplayLogCombat(boolean v) { this.replayLogCombat = v; }

    public boolean isReplayLogInventory() { return replayLogInventory; }
    public void setReplayLogInventory(boolean v) { this.replayLogInventory = v; }

    public boolean isReplayLogItems() { return replayLogItems; }
    public void setReplayLogItems(boolean v) { this.replayLogItems = v; }

    public boolean isReplayLogEquipment() { return replayLogEquipment; }
    public void setReplayLogEquipment(boolean v) { this.replayLogEquipment = v; }

    public boolean isReplayLogEntities() { return replayLogEntities; }
    public void setReplayLogEntities(boolean v) { this.replayLogEntities = v; }

    public boolean isReplayLogTeleports() { return replayLogTeleports; }
    public void setReplayLogTeleports(boolean v) { this.replayLogTeleports = v; }

    public boolean isReplayLogDeaths() { return replayLogDeaths; }
    public void setReplayLogDeaths(boolean v) { this.replayLogDeaths = v; }

    // Entity tracking getters and setters
    public boolean isReplayEntityTrackingEnabled() { return replayEntityTrackingEnabled; }
    public void setReplayEntityTrackingEnabled(boolean v) { this.replayEntityTrackingEnabled = v; }

    public int getReplayEntityTrackingRadius() { return replayEntityTrackingRadius; }
    public void setReplayEntityTrackingRadius(int v) { this.replayEntityTrackingRadius = v; }

    public boolean isReplayEntityDropUninvolved() { return replayEntityDropUninvolved; }
    public void setReplayEntityDropUninvolved(boolean v) { this.replayEntityDropUninvolved = v; }

    public int getReplayEntityLogMaxPerSession() { return replayEntityLogMaxPerSession; }
    public void setReplayEntityLogMaxPerSession(int v) { this.replayEntityLogMaxPerSession = v; }

    // Storage optimization getters and setters
    public boolean isReplayCompressEquipment() { return replayCompressEquipment; }
    public void setReplayCompressEquipment(boolean v) { this.replayCompressEquipment = v; }

    public boolean isReplayCompressMovement() { return replayCompressMovement; }
    public void setReplayCompressMovement(boolean v) { this.replayCompressMovement = v; }

    public int getReplaySnapshotInterval() { return replaySnapshotInterval; }
    public void setReplaySnapshotInterval(int v) { this.replaySnapshotInterval = v; }

    public boolean isReplaySoundsEnabled() {
        return replaySoundsEnabled;
    }

    public void setReplaySoundsEnabled(boolean replaySoundsEnabled) {
        this.replaySoundsEnabled = replaySoundsEnabled;
    }

    public boolean isReplayAnimationsEnabled() {
        return replayAnimationsEnabled;
    }

    public void setReplayAnimationsEnabled(boolean replayAnimationsEnabled) {
        this.replayAnimationsEnabled = replayAnimationsEnabled;
    }

    public int getReplayBlockLogMaxPerSession() {
        return replayBlockLogMaxPerSession;
    }

    public void setReplayBlockLogMaxPerSession(int replayBlockLogMaxPerSession) {
        this.replayBlockLogMaxPerSession = replayBlockLogMaxPerSession;
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

    // Activity log getters and setters
    public boolean isActivityLogEnabled() { return activityLogEnabled; }
    public void setActivityLogEnabled(boolean v) { this.activityLogEnabled = v; }

    public String getActivityLogStorageType() { return activityLogStorageType; }
    public void setActivityLogStorageType(String v) { this.activityLogStorageType = v; }

    public int getActivityLogEntriesPerPage() { return activityLogEntriesPerPage; }
    public void setActivityLogEntriesPerPage(int v) { this.activityLogEntriesPerPage = v; }

    public long getActivityLogRetentionDays() { return activityLogRetentionDays; }
    public void setActivityLogRetentionDays(long v) { this.activityLogRetentionDays = v; }

    public int getMaxChatLogs() { return maxChatLogs; }
    public void setMaxChatLogs(int v) { this.maxChatLogs = v; }

    public int getMaxCommandLogs() { return maxCommandLogs; }
    public void setMaxCommandLogs(int v) { this.maxCommandLogs = v; }

    public boolean isActivityLogChat() { return activityLogChat; }
    public void setActivityLogChat(boolean v) { this.activityLogChat = v; }

    public boolean isActivityLogCommands() { return activityLogCommands; }
    public void setActivityLogCommands(boolean v) { this.activityLogCommands = v; }

    public boolean isActivityLogSigns() { return activityLogSigns; }
    public void setActivityLogSigns(boolean v) { this.activityLogSigns = v; }

    public boolean isActivityLogItems() { return activityLogItems; }
    public void setActivityLogItems(boolean v) { this.activityLogItems = v; }

    public boolean isActivityLogSessions() { return activityLogSessions; }
    public void setActivityLogSessions(boolean v) { this.activityLogSessions = v; }

    public boolean isActivityLogAnvils() { return activityLogAnvils; }
    public void setActivityLogAnvils(boolean v) { this.activityLogAnvils = v; }

    public boolean isActivityLogUsernames() { return activityLogUsernames; }
    public void setActivityLogUsernames(boolean v) { this.activityLogUsernames = v; }

    // Individual retention period getters/setters (-1 = forever, 0 = disable)
    public long getRetentionChat() { return retentionChat; }
    public void setRetentionChat(long v) { this.retentionChat = v; }

    public long getRetentionCommands() { return retentionCommands; }
    public void setRetentionCommands(long v) { this.retentionCommands = v; }

    public long getRetentionSigns() { return retentionSigns; }
    public void setRetentionSigns(long v) { this.retentionSigns = v; }

    public long getRetentionSessions() { return retentionSessions; }
    public void setRetentionSessions(long v) { this.retentionSessions = v; }

    public long getRetentionItems() { return retentionItems; }
    public void setRetentionItems(long v) { this.retentionItems = v; }

    public long getRetentionAnvils() { return retentionAnvils; }
    public void setRetentionAnvils(long v) { this.retentionAnvils = v; }

    public long getRetentionUsernames() { return retentionUsernames; }
    public void setRetentionUsernames(long v) { this.retentionUsernames = v; }

    public long getRetentionIpChanges() { return retentionIpChanges; }
    public void setRetentionIpChanges(long v) { this.retentionIpChanges = v; }

    public long getRetentionPunishments() { return retentionPunishments; }
    public void setRetentionPunishments(long v) { this.retentionPunishments = v; }

    public long getRetentionPardons() { return retentionPardons; }
    public void setRetentionPardons(long v) { this.retentionPardons = v; }

    public long getRetentionAutomod() { return retentionAutomod; }
    public void setRetentionAutomod(long v) { this.retentionAutomod = v; }

    public long getRetentionAnticheat() { return retentionAnticheat; }
    public void setRetentionAnticheat(long v) { this.retentionAnticheat = v; }

    public long getRetentionStaffActions() { return retentionStaffActions; }
    public void setRetentionStaffActions(long v) { this.retentionStaffActions = v; }

    public long getRetentionWatchlist() { return retentionWatchlist; }
    public void setRetentionWatchlist(long v) { this.retentionWatchlist = v; }

    // Evidence system getters/setters
    public int getEvidenceMaxFileSizeMb() { return evidenceMaxFileSizeMb; }
    public void setEvidenceMaxFileSizeMb(int v) { this.evidenceMaxFileSizeMb = v; }

    public boolean isEvidenceRequireEvidence() { return evidenceRequireEvidence; }
    public void setEvidenceRequireEvidence(boolean v) { this.evidenceRequireEvidence = v; }

    public int getEvidenceMaxActivityLogEntries() { return evidenceMaxActivityLogEntries; }
    public void setEvidenceMaxActivityLogEntries(int v) { this.evidenceMaxActivityLogEntries = v; }

    // Player portal getters/setters
    public boolean isPlayerPortalEnabled() { return playerPortalEnabled; }
    public void setPlayerPortalEnabled(boolean v) { this.playerPortalEnabled = v; }

    public int getPlayerPortalSessionExpiryHours() { return playerPortalSessionExpiryHours; }
    public void setPlayerPortalSessionExpiryHours(int v) { this.playerPortalSessionExpiryHours = v; }

    // Gateway getters/setters
    public boolean isGatewayEnabled() { return gatewayEnabled; }
    public void setGatewayEnabled(boolean v) { this.gatewayEnabled = v; }

    public String getGatewayUrl() { return gatewayUrl; }
    public void setGatewayUrl(String v) { this.gatewayUrl = v; }

    public int getGatewayReconnectDelaySeconds() { return gatewayReconnectDelaySeconds; }
    public void setGatewayReconnectDelaySeconds(int v) { this.gatewayReconnectDelaySeconds = v; }

    /** Alias for GatewayClient compatibility */
    public int getGatewayReconnectDelay() { return gatewayReconnectDelaySeconds; }

    public int getGatewayMaxReconnectAttempts() { return gatewayMaxReconnectAttempts; }
    public void setGatewayMaxReconnectAttempts(int v) { this.gatewayMaxReconnectAttempts = v; }

    public boolean isGatewayDebugLogging() { return gatewayDebugLogging; }
    public void setGatewayDebugLogging(boolean v) { this.gatewayDebugLogging = v; }

    /**
     * Get the server name (uses web panel server name).
     */
    public String getServerName() { return webPanelServerName; }

    // Fake messages getters and setters
    public boolean isVanishFakeMessagesEnabled() {
        return vanishFakeMessagesEnabled;
    }

    public void setVanishFakeMessagesEnabled(boolean vanishFakeMessagesEnabled) {
        this.vanishFakeMessagesEnabled = vanishFakeMessagesEnabled;
    }

    public String getVanishFakeJoinMessage() {
        return vanishFakeJoinMessage;
    }

    public void setVanishFakeJoinMessage(String vanishFakeJoinMessage) {
        this.vanishFakeJoinMessage = vanishFakeJoinMessage;
    }

    public String getVanishFakeLeaveMessage() {
        return vanishFakeLeaveMessage;
    }

    public void setVanishFakeLeaveMessage(String vanishFakeLeaveMessage) {
        this.vanishFakeLeaveMessage = vanishFakeLeaveMessage;
    }

    // Plugin hooks getters and setters
    public boolean isVanishHookEssentialsXEnabled() {
        return vanishHookEssentialsXEnabled;
    }

    public void setVanishHookEssentialsXEnabled(boolean vanishHookEssentialsXEnabled) {
        this.vanishHookEssentialsXEnabled = vanishHookEssentialsXEnabled;
    }

    public boolean isVanishHookEssentialsXHideMessages() {
        return vanishHookEssentialsXHideMessages;
    }

    public void setVanishHookEssentialsXHideMessages(boolean vanishHookEssentialsXHideMessages) {
        this.vanishHookEssentialsXHideMessages = vanishHookEssentialsXHideMessages;
    }

    public boolean isVanishHookCustomJoinMessagesEnabled() {
        return vanishHookCustomJoinMessagesEnabled;
    }

    public void setVanishHookCustomJoinMessagesEnabled(boolean vanishHookCustomJoinMessagesEnabled) {
        this.vanishHookCustomJoinMessagesEnabled = vanishHookCustomJoinMessagesEnabled;
    }

    public boolean isVanishHookCustomJoinMessagesHideMessages() {
        return vanishHookCustomJoinMessagesHideMessages;
    }

    public void setVanishHookCustomJoinMessagesHideMessages(boolean vanishHookCustomJoinMessagesHideMessages) {
        this.vanishHookCustomJoinMessagesHideMessages = vanishHookCustomJoinMessagesHideMessages;
    }

    public boolean isVanishHookAlonsoJoinEnabled() {
        return vanishHookAlonsoJoinEnabled;
    }

    public void setVanishHookAlonsoJoinEnabled(boolean vanishHookAlonsoJoinEnabled) {
        this.vanishHookAlonsoJoinEnabled = vanishHookAlonsoJoinEnabled;
    }

    public boolean isVanishHookAlonsoJoinHideMessages() {
        return vanishHookAlonsoJoinHideMessages;
    }

    public void setVanishHookAlonsoJoinHideMessages(boolean vanishHookAlonsoJoinHideMessages) {
        this.vanishHookAlonsoJoinHideMessages = vanishHookAlonsoJoinHideMessages;
    }

    // Level-based visibility getters and setters
    public boolean isVanishLevelsEnabled() {
        return vanishLevelsEnabled;
    }

    public void setVanishLevelsEnabled(boolean vanishLevelsEnabled) {
        this.vanishLevelsEnabled = vanishLevelsEnabled;
    }

    public int getVanishLevelsDefaultLevel() {
        return vanishLevelsDefaultLevel;
    }

    public void setVanishLevelsDefaultLevel(int vanishLevelsDefaultLevel) {
        this.vanishLevelsDefaultLevel = vanishLevelsDefaultLevel;
    }

    public int getVanishLevelsOpLevel() {
        return vanishLevelsOpLevel;
    }

    public void setVanishLevelsOpLevel(int vanishLevelsOpLevel) {
        this.vanishLevelsOpLevel = vanishLevelsOpLevel;
    }

    public boolean isVanishLevelsStaffCanSeeLowerLevels() {
        return vanishLevelsStaffCanSeeLowerLevels;
    }

    public void setVanishLevelsStaffCanSeeLowerLevels(boolean vanishLevelsStaffCanSeeLowerLevels) {
        this.vanishLevelsStaffCanSeeLowerLevels = vanishLevelsStaffCanSeeLowerLevels;
    }

    // LuckPerms integration getters and setters
    public boolean isVanishLuckPermsEnabled() {
        return vanishLuckPermsEnabled;
    }

    public void setVanishLuckPermsEnabled(boolean vanishLuckPermsEnabled) {
        this.vanishLuckPermsEnabled = vanishLuckPermsEnabled;
    }

    public String getVanishLuckPermsVanishPrefix() {
        return vanishLuckPermsVanishPrefix;
    }

    public void setVanishLuckPermsVanishPrefix(String vanishLuckPermsVanishPrefix) {
        this.vanishLuckPermsVanishPrefix = vanishLuckPermsVanishPrefix;
    }

    public String getVanishLuckPermsVanishSuffix() {
        return vanishLuckPermsVanishSuffix;
    }

    public void setVanishLuckPermsVanishSuffix(String vanishLuckPermsVanishSuffix) {
        this.vanishLuckPermsVanishSuffix = vanishLuckPermsVanishSuffix;
    }

    public boolean isVanishLuckPermsModifyTab() {
        return vanishLuckPermsModifyTab;
    }

    public void setVanishLuckPermsModifyTab(boolean vanishLuckPermsModifyTab) {
        this.vanishLuckPermsModifyTab = vanishLuckPermsModifyTab;
    }

    // Vanish list getters and setters
    public boolean isVanishListRespectVisibility() {
        return vanishListRespectVisibility;
    }

    public void setVanishListRespectVisibility(boolean vanishListRespectVisibility) {
        this.vanishListRespectVisibility = vanishListRespectVisibility;
    }

    public boolean isVanishListShowLevels() {
        return vanishListShowLevels;
    }

    public void setVanishListShowLevels(boolean vanishListShowLevels) {
        this.vanishListShowLevels = vanishListShowLevels;
    }

    public String getVanishListFormat() {
        return vanishListFormat;
    }

    public void setVanishListFormat(String vanishListFormat) {
        this.vanishListFormat = vanishListFormat;
    }

    // Automod getters and setters
    public boolean isSpamPreventionEnabled() {
        return spamPreventionEnabled;
    }

    public void setSpamPreventionEnabled(boolean spamPreventionEnabled) {
        this.spamPreventionEnabled = spamPreventionEnabled;
    }

    public boolean isCapsFilterEnabled() {
        return capsFilterEnabled;
    }

    public void setCapsFilterEnabled(boolean capsFilterEnabled) {
        this.capsFilterEnabled = capsFilterEnabled;
    }

    public boolean isLinkFilterEnabled() {
        return linkFilterEnabled;
    }

    public void setLinkFilterEnabled(boolean linkFilterEnabled) {
        this.linkFilterEnabled = linkFilterEnabled;
    }

    // Join/Leave message getters and setters
    public String getJoinLeaveVisibility() {
        return joinLeaveVisibility;
    }

    public void setJoinLeaveVisibility(String joinLeaveVisibility) {
        this.joinLeaveVisibility = joinLeaveVisibility;
    }

    public boolean isJoinLeaveSuppressVanilla() {
        return joinLeaveSuppressVanilla;
    }

    public void setJoinLeaveSuppressVanilla(boolean joinLeaveSuppressVanilla) {
        this.joinLeaveSuppressVanilla = joinLeaveSuppressVanilla;
    }

    public String getJoinLeaveJoinFormat() {
        return joinLeaveJoinFormat;
    }

    public void setJoinLeaveJoinFormat(String joinLeaveJoinFormat) {
        this.joinLeaveJoinFormat = joinLeaveJoinFormat;
    }

    public String getJoinLeaveLeaveFormat() {
        return joinLeaveLeaveFormat;
    }

    public void setJoinLeaveLeaveFormat(String joinLeaveLeaveFormat) {
        this.joinLeaveLeaveFormat = joinLeaveLeaveFormat;
    }

    // Private messages visibility getters and setters
    public String getPrivateMessagesVisibility() {
        return privateMessagesVisibility;
    }

    public void setPrivateMessagesVisibility(String privateMessagesVisibility) {
        this.privateMessagesVisibility = privateMessagesVisibility;
    }

    // Command blacklist getters and setters
    public boolean isCmdBlacklistAllowModerexCommands() {
        return cmdBlacklistAllowModerexCommands;
    }

    public void setCmdBlacklistAllowModerexCommands(boolean cmdBlacklistAllowModerexCommands) {
        this.cmdBlacklistAllowModerexCommands = cmdBlacklistAllowModerexCommands;
    }

    public java.util.List<String> getCmdBlacklistProtectedCommands() {
        return cmdBlacklistProtectedCommands;
    }

    public void setCmdBlacklistProtectedCommands(java.util.List<String> cmdBlacklistProtectedCommands) {
        this.cmdBlacklistProtectedCommands = cmdBlacklistProtectedCommands;
    }
}
