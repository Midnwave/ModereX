package com.blockforge.moderex;

import com.blockforge.moderex.automod.AutomodManager;
import com.blockforge.moderex.automod.api.AutomodAPI;
import com.blockforge.moderex.commands.CommandManager;
import com.blockforge.moderex.config.ConfigManager;
import com.blockforge.moderex.config.lang.LanguageManager;
import com.blockforge.moderex.database.DatabaseManager;
import com.blockforge.moderex.license.LicenseManager;
import com.blockforge.moderex.gui.GuiManager;
import com.blockforge.moderex.hooks.HookManager;
import com.blockforge.moderex.listeners.ListenerManager;
import com.blockforge.moderex.proxy.ProxyManager;
import com.blockforge.moderex.punishment.PunishmentManager;
import com.blockforge.moderex.punishment.PunishmentScheduler;
import com.blockforge.moderex.punishment.TemplateManager;
import com.blockforge.moderex.replay.ReplayManager;
import com.blockforge.moderex.replay.block.BlockLogManager;
import com.blockforge.moderex.replay.block.FakeBlockManager;
import com.blockforge.moderex.replay.entity.EntityLogManager;
import com.blockforge.moderex.log.ActivityLogManager;
import com.blockforge.moderex.staff.StaffChatManager;
import com.blockforge.moderex.staff.StaffSettingsManager;
import com.blockforge.moderex.staff.VanishManager;
import com.blockforge.moderex.vanish.api.VanishAPI;
import com.blockforge.moderex.vanish.api.VanishEventFilter;
import com.blockforge.moderex.util.DebugWebhook;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.UpdateChecker;
import com.blockforge.moderex.util.VersionUtil;
import com.blockforge.moderex.watchlist.WatchlistManager;
import com.blockforge.moderex.webpanel.HybridPanelServer;
import com.blockforge.moderex.webpanel.debug.WebPanelDebugger;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class ModereX extends JavaPlugin {

    private static ModereX instance;

    private ConfigManager configManager;
    private LanguageManager languageManager;
    private DatabaseManager databaseManager;
    private PunishmentManager punishmentManager;
    private PunishmentScheduler punishmentScheduler;
    private AutomodManager automodManager;
    private com.blockforge.moderex.hooks.anticheat.AnticheatManager anticheatManager;
    private com.blockforge.moderex.hooks.moderation.ModerationHookManager moderationHookManager;
    private GuiManager guiManager;
    private StaffChatManager staffChatManager;
    private VanishManager vanishManager;
    private VanishAPI vanishAPI;
    private AutomodAPI automodAPI;
    private com.blockforge.moderex.disguise.DisguiseManager disguiseManager;
    private com.blockforge.moderex.staff.StaffModeManager staffModeManager;
    private com.blockforge.moderex.geoip.GeoIPManager geoIPManager;
    private WatchlistManager watchlistManager;
    private com.blockforge.moderex.player.PlayerProfileManager playerProfileManager;
    private ProxyManager proxyManager;
    private com.blockforge.moderex.web.WebAuthManager webAuthManager;
    private HybridPanelServer hybridPanelServer;
    private HookManager hookManager;
    private CommandManager commandManager;
    private ListenerManager listenerManager;
    private ReplayManager replayManager;
    private BlockLogManager blockLogManager;
    private FakeBlockManager fakeBlockManager;
    private EntityLogManager entityLogManager;
    private ActivityLogManager activityLogManager;
    private TemplateManager templateManager;
    private com.blockforge.moderex.monitor.ServerStatusManager serverStatusManager;
    private com.blockforge.moderex.monitor.PerformanceSettingsManager performanceSettingsManager;
    private com.blockforge.moderex.monitor.TpsThrottleManager tpsThrottleManager;
    private com.blockforge.moderex.monitor.MemoryManager memoryManager;
    private StaffSettingsManager staffSettingsManager;
    private com.blockforge.moderex.resourcepack.ResourcePackManager resourcePackManager;
    private com.blockforge.moderex.rules.RulesManager rulesManager;
    private com.blockforge.moderex.rules.RuleAcceptanceManager ruleAcceptanceManager;
    private com.blockforge.moderex.rules.CodeOfConductManager codeOfConductManager;
    private com.blockforge.moderex.ai.OllamaClient ollamaClient;
    private com.blockforge.moderex.ai.AIModerationManager aiModerationManager;
    private com.blockforge.moderex.security.RaidProtectionManager raidProtectionManager;
    private com.blockforge.moderex.automod.AfkManager afkManager;
    private DebugWebhook debugWebhook;
    private WebPanelDebugger webPanelDebugger;
    private com.blockforge.moderex.util.GitHubAutoUpdater githubAutoUpdater;
    private com.blockforge.moderex.alert.AlertManager alertManager;
    private com.blockforge.moderex.evidence.EvidenceManager evidenceManager;
    private com.blockforge.moderex.evidence.EvidenceSelectionManager evidenceSelectionManager;
    private com.blockforge.moderex.portal.AuthSessionManager authSessionManager;
    private com.blockforge.moderex.identity.ServerIdentity serverIdentity;
    private com.blockforge.moderex.gateway.GatewayClient gatewayClient;
    private com.blockforge.moderex.permissions.PermissionManager permissionManager;
    private LicenseManager licenseManager;

    // Lockdown state
    private boolean globalLockdown = false;
    private boolean localLockdown = false;

    @Override
    public void onEnable() {
        instance = this;
        long startTime = System.currentTimeMillis();

        // Print ASCII art banner
        printBanner();

        logStartup("Initializing ModereX v" + getDescription().getVersion());
        logStartup("Running on " + VersionUtil.getFormattedVersion());

        // Initialize cross-platform message utility (must be before any managers)
        Msg.init(this);

        // Initialize configuration
        logStartup("Loading configuration...");
        this.configManager = new ConfigManager(this);
        if (!configManager.load()) {
            getLogger().severe("Failed to load configuration! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize debug webhook (after config is loaded)
        this.debugWebhook = new DebugWebhook(this);
        debugWebhook.setWebhookUrl(configManager.getSettings().getDebugWebhookUrl());

        // Initialize language manager
        logStartup("Loading language files...");
        this.languageManager = new LanguageManager(this);
        languageManager.load();

        // Initialize database
        logStartup("Connecting to database...");
        this.databaseManager = new DatabaseManager(this);
        if (!databaseManager.initialize()) {
            getLogger().severe("Failed to initialize database! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize server identity (unique persistent ID for this server)
        logStartup("Initializing server identity...");
        this.serverIdentity = new com.blockforge.moderex.identity.ServerIdentity(this);
        serverIdentity.initialize();

        // Initialize license system (for dev builds)
        logStartup("Initializing license system...");
        this.licenseManager = new LicenseManager(this);
        boolean licenseValid = licenseManager.init().join(); // Block until validation completes
        if (!licenseValid) {
            // License validation failed - plugin will be disabled by LicenseManager
            return;
        }

        // Initialize hook manager (connects to other plugins)
        logStartup("Initializing plugin hooks...");
        this.hookManager = new HookManager(this);
        hookManager.initialize();

        // Register PlaceholderAPI expansion if available
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            logStartup("Registering PlaceholderAPI expansion...");
            try {
                new com.blockforge.moderex.integrations.ModereXPlaceholders(this).register();
                logStartup("PlaceholderAPI expansion registered successfully!");
            } catch (Exception e) {
                getLogger().warning("Failed to register PlaceholderAPI expansion: " + e.getMessage());
            }
        }

        // Initialize built-in permission system
        logStartup("Initializing permission system...");
        this.permissionManager = new com.blockforge.moderex.permissions.PermissionManager(this);
        permissionManager.initialize();

        // Initialize core managers
        logStartup("Initializing punishment system...");
        this.punishmentManager = new PunishmentManager(this);
        this.punishmentScheduler = new PunishmentScheduler(this);
        punishmentScheduler.start();

        logStartup("Initializing automod system...");
        this.automodManager = new AutomodManager(this);
        automodManager.load();
        this.automodAPI = new AutomodAPI(this);

        // Register anticheat automod rules (now that AutomodManager is ready)
        if (hookManager != null && hookManager.getAnticheatManager() != null) {
            for (String acName : hookManager.getAnticheatManager().getEnabledAnticheats()) {
                var hook = hookManager.getAnticheatManager().getHook(acName);
                if (hook != null) {
                    automodManager.registerAnticheatRules(hook.getName(), hook.getVersion());
                }
            }
        }

        logStartup("Initializing AFK manager...");
        this.afkManager = new com.blockforge.moderex.automod.AfkManager(this);
        getServer().getPluginManager().registerEvents(afkManager, this);
        afkManager.start();

        // Note: AnticheatManager is already initialized in HookManager.initialize()
        // Use hookManager.getAnticheatManager() instead of creating a duplicate
        this.anticheatManager = hookManager.getAnticheatManager();

        logStartup("Initializing moderation plugin integrations...");
        this.moderationHookManager = new com.blockforge.moderex.hooks.moderation.ModerationHookManager(this);
        moderationHookManager.initialize();

        logStartup("Initializing GUI system...");
        this.guiManager = new GuiManager(this);

        logStartup("Initializing resource pack...");
        this.resourcePackManager = new com.blockforge.moderex.resourcepack.ResourcePackManager(this);
        resourcePackManager.initialize();

        logStartup("Initializing staff features...");
        this.staffChatManager = new StaffChatManager(this);
        this.vanishManager = new VanishManager(this);
        this.vanishAPI = new VanishAPI(this);
        getServer().getPluginManager().registerEvents(new VanishEventFilter(this, vanishAPI), this);
        this.disguiseManager = new com.blockforge.moderex.disguise.DisguiseManager(this);
        this.staffModeManager = new com.blockforge.moderex.staff.StaffModeManager(this);
        this.staffSettingsManager = new StaffSettingsManager(this);
        this.alertManager = new com.blockforge.moderex.alert.AlertManager(this);

        logStartup("Initializing GeoIP system...");
        this.geoIPManager = new com.blockforge.moderex.geoip.GeoIPManager(this);
        geoIPManager.initialize();

        logStartup("Initializing watchlist...");
        this.watchlistManager = new WatchlistManager(this);

        logStartup("Initializing rules system...");
        this.rulesManager = new com.blockforge.moderex.rules.RulesManager(this);
        rulesManager.initialize();

        logStartup("Initializing rule acceptance tracking...");
        this.ruleAcceptanceManager = new com.blockforge.moderex.rules.RuleAcceptanceManager(this);
        ruleAcceptanceManager.initialize();

        logStartup("Initializing Code of Conduct...");
        this.codeOfConductManager = new com.blockforge.moderex.rules.CodeOfConductManager(this);
        codeOfConductManager.initialize();

        logStartup("Initializing AI client...");
        this.ollamaClient = new com.blockforge.moderex.ai.OllamaClient(this);
        ollamaClient.start();

        logStartup("Initializing AI moderation...");
        this.aiModerationManager = new com.blockforge.moderex.ai.AIModerationManager(this);
        aiModerationManager.initialize();
        aiModerationManager.start();

        logStartup("Initializing raid protection...");
        this.raidProtectionManager = new com.blockforge.moderex.security.RaidProtectionManager(this);
        raidProtectionManager.initialize();
        raidProtectionManager.start();

        logStartup("Initializing replay system...");
        this.blockLogManager = new BlockLogManager(this);
        this.fakeBlockManager = new FakeBlockManager(this);
        this.entityLogManager = new EntityLogManager(this);
        this.replayManager = new ReplayManager(this);
        replayManager.start();

        logStartup("Initializing activity log system...");
        this.activityLogManager = new ActivityLogManager(this);
        activityLogManager.initialize();

        logStartup("Initializing evidence system...");
        // Apply evidence max file size from config
        com.blockforge.moderex.evidence.Evidence.setMaxFileSizeMB(configManager.getSettings().getEvidenceMaxFileSizeMb());
        this.evidenceManager = new com.blockforge.moderex.evidence.EvidenceManager(this);
        evidenceManager.initialize();
        this.evidenceSelectionManager = new com.blockforge.moderex.evidence.EvidenceSelectionManager(this);
        evidenceSelectionManager.initialize();

        logStartup("Initializing player auth session manager...");
        this.authSessionManager = new com.blockforge.moderex.portal.AuthSessionManager(this);
        authSessionManager.setSessionExpiryHours(configManager.getSettings().getPlayerPortalSessionExpiryHours());
        authSessionManager.initialize();

        logStartup("Initializing server status monitor...");
        this.serverStatusManager = new com.blockforge.moderex.monitor.ServerStatusManager(this);
        if (configManager.getSettings().isServerStatusEnabled()) {
            serverStatusManager.start();
        }

        logStartup("Initializing performance settings manager...");
        this.performanceSettingsManager = new com.blockforge.moderex.monitor.PerformanceSettingsManager(this);
        performanceSettingsManager.initialize();

        logStartup("Initializing TPS throttle system...");
        this.tpsThrottleManager = new com.blockforge.moderex.monitor.TpsThrottleManager(this);
        tpsThrottleManager.createTable();
        tpsThrottleManager.start();

        logStartup("Initializing memory management system...");
        this.memoryManager = new com.blockforge.moderex.monitor.MemoryManager(this);
        memoryManager.start();

        logStartup("Initializing template system...");
        this.templateManager = new TemplateManager(this);
        templateManager.load();

        logStartup("Initializing player profiles...");
        this.playerProfileManager = new com.blockforge.moderex.player.PlayerProfileManager(this);
        playerProfileManager.initialize();

        // Initialize proxy support if enabled
        if (configManager.getSettings().isProxyEnabled()) {
            logStartup("Initializing proxy support...");
            this.proxyManager = new ProxyManager(this);
            proxyManager.initialize();
        }

        // Initialize web authentication manager
        logStartup("Initializing web authentication...");
        this.webAuthManager = new com.blockforge.moderex.web.WebAuthManager(this);
        webAuthManager.initialize();

        // Initialize web panel server if enabled (single port for HTTP + WebSocket)
        if (configManager.getSettings().isWebPanelEnabled()) {
            startDedicatedPanelServer();

            // Initialize web panel debugger after panel server is created
            if (hybridPanelServer != null) {
                logStartup("Initializing web panel debugger...");
                this.webPanelDebugger = new WebPanelDebugger(this);
            }
        }

        // Initialize gateway client if enabled (connects to gateway.moderex.net)
        if (configManager.getSettings().isGatewayEnabled()) {
            logStartup("Initializing gateway client...");
            this.gatewayClient = new com.blockforge.moderex.gateway.GatewayClient(this);
            // Connect gateway to panel server for handling requests
            if (hybridPanelServer == null) {
                logStartup("Creating panel server for gateway message handling...");
                // Create HybridPanelServer on port 0 (won't actually bind - just for gateway message handling)
                this.hybridPanelServer = new HybridPanelServer(this, 0);
                // Don't call start() - we just need the message handler functionality
            }
            gatewayClient.setMessageHandler(hybridPanelServer);
            gatewayClient.start();
        } else {
            logStartup("Gateway disabled (opt-out mode) - panel only accessible via direct IP:port");
        }

        // Register commands
        logStartup("Registering commands...");
        this.commandManager = new CommandManager(this);
        commandManager.registerAll();

        // Register listeners
        logStartup("Registering event listeners...");
        this.listenerManager = new ListenerManager(this);
        listenerManager.registerAll();

        // Check for updates (Modrinth)
        if (configManager.getSettings().isUpdateCheckerEnabled()) {
            new UpdateChecker(this).checkAsync();
        }

        // Check for GitHub updates and auto-download
        this.githubAutoUpdater = new com.blockforge.moderex.util.GitHubAutoUpdater(this);
        if (configManager.getSettings().isGithubAutoUpdateEnabled()) {
            logStartup("Checking GitHub for updates...");
            githubAutoUpdater.checkAsync();
            githubAutoUpdater.schedulePeriodicCheck(); // Check every 24 hours
        }

        long endTime = System.currentTimeMillis();
        logStartup("ModereX enabled successfully in " + (endTime - startTime) + "ms!");

        // Dev Build Tester advertisement to Discord webhook
        if (debugWebhook != null && debugWebhook.isEnabled()) {
            debugWebhook.success("★ Dev Build Tester Applications are now open! Apply at: https://discord.com/channels/1131588549892907089/1309211930665156668 | Discord: https://discord.gg/jQGMhKA5m6");
        }
    }

    @Override
    public void onDisable() {
        logStartup("Disabling ModereX...");

        // Stop license manager
        if (licenseManager != null) {
            licenseManager.shutdown();
        }

        // Stop gateway client
        if (gatewayClient != null) {
            gatewayClient.stop();
        }

        // Stop web panel debugger
        if (webPanelDebugger != null) {
            webPanelDebugger.shutdown();
        }

        // Stop web panel server
        if (hybridPanelServer != null) {
            hybridPanelServer.stop();
        }

        // Shutdown web authentication
        if (webAuthManager != null) {
            webAuthManager.shutdown();
        }

        // Stop replay system
        if (replayManager != null) {
            replayManager.stop();
        }

        // Stop activity log system
        if (activityLogManager != null) {
            activityLogManager.shutdown();
        }

        // Stop evidence system
        if (evidenceManager != null) {
            evidenceManager.shutdown();
        }

        // Stop evidence selection manager
        if (evidenceSelectionManager != null) {
            evidenceSelectionManager.shutdown();
        }

        // Stop auth session manager
        if (authSessionManager != null) {
            authSessionManager.shutdown();
        }

        // Stop punishment scheduler
        if (punishmentScheduler != null) {
            punishmentScheduler.stop();
        }

        // Stop AFK manager
        if (afkManager != null) {
            afkManager.stop();
        }

        // Stop raid protection
        if (raidProtectionManager != null) {
            raidProtectionManager.stop();
        }

        // Stop AI moderation
        if (aiModerationManager != null) {
            aiModerationManager.stop();
        }

        // Stop AI client
        if (ollamaClient != null) {
            ollamaClient.shutdown();
        }

        // Stop performance managers
        if (memoryManager != null) {
            memoryManager.stop();
        }
        if (tpsThrottleManager != null) {
            tpsThrottleManager.stop();
        }
        if (performanceSettingsManager != null) {
            performanceSettingsManager.stop();
        }
        if (serverStatusManager != null) {
            serverStatusManager.stop();
        }

        // Close database connections
        if (databaseManager != null) {
            databaseManager.shutdown();
        }

        // Unhook from other plugins
        if (hookManager != null) {
            hookManager.shutdown();
        }

        // Note: AnticheatManager shutdown is handled by HookManager.shutdown()

        // Cleanup disguise manager
        if (disguiseManager != null) {
            disguiseManager.cleanup();
        }

        // Cleanup staff mode manager
        if (staffModeManager != null) {
            staffModeManager.cleanup();
        }

        // Shutdown GeoIP manager
        if (geoIPManager != null) {
            geoIPManager.shutdown();
        }

        // Shutdown debug webhook
        if (debugWebhook != null) {
            debugWebhook.success("[ModereX] Plugin disabled.");
            debugWebhook.shutdown();
        }

        // Close cross-platform message utility
        Msg.close();

        instance = null;
        getLogger().info("ModereX disabled.");
    }

    public void reload() {
        getLogger().info("Reloading ModereX...");

        // Reload configuration
        configManager.load();
        languageManager.load();
        automodManager.load();

        // Handle gateway client based on new config
        if (configManager.getSettings().isGatewayEnabled()) {
            if (gatewayClient == null) {
                // Gateway was disabled, now enabled - create new client
                this.gatewayClient = new com.blockforge.moderex.gateway.GatewayClient(this);
                // Connect gateway to panel server for handling requests
                if (hybridPanelServer != null) {
                    gatewayClient.setMessageHandler(hybridPanelServer);
                }
                gatewayClient.start();
            } else {
                // Gateway was already enabled - reconnect to apply any config changes
                gatewayClient.reconnect();
            }
        } else {
            // Gateway disabled - stop client if running
            if (gatewayClient != null) {
                gatewayClient.stop();
                gatewayClient = null;
            }
        }

        // Re-register anticheat automod rules (cleared during automodManager.load())
        if (hookManager != null && hookManager.getAnticheatManager() != null) {
            for (String acName : hookManager.getAnticheatManager().getEnabledAnticheats()) {
                var hook = hookManager.getAnticheatManager().getHook(acName);
                if (hook != null) {
                    automodManager.registerAnticheatRules(hook.getName(), hook.getVersion());
                }
            }
        }

        // Reload vanish API whitelist
        if (vanishAPI != null) {
            vanishAPI.reload();
        }

        // Update debug webhook URL
        if (debugWebhook != null) {
            debugWebhook.setWebhookUrl(configManager.getSettings().getDebugWebhookUrl());
        }

        // Stop existing web panel services
        if (webPanelDebugger != null) {
            webPanelDebugger.shutdown();
            webPanelDebugger = null;
        }
        if (hybridPanelServer != null) {
            hybridPanelServer.stop();
            hybridPanelServer = null;
        }

        // Restart web panel if enabled
        if (configManager.getSettings().isWebPanelEnabled()) {
            startDedicatedPanelServer();

            // Reinitialize web panel debugger
            if (hybridPanelServer != null) {
                this.webPanelDebugger = new WebPanelDebugger(this);
            }
        }

        getLogger().info("ModereX reloaded successfully!");
    }

    private void startDedicatedPanelServer() {
        int port = configManager.getSettings().getWebPanelPort();
        logStartup("Starting web panel server on port " + port + "...");
        this.hybridPanelServer = new HybridPanelServer(this, port);
        hybridPanelServer.start();
    }

    private void printBanner() {
        // ANSI color codes for console
        String DARK_BLUE = "\u001B[34m";
        String LIGHT_BLUE = "\u001B[36m";
        String GRAY = "\u001B[90m";
        String WHITE = "\u001B[97m";
        String RESET = "\u001B[0m";

        // Slanted ASCII art for "ModereX"
        String[] banner = {
            "",
            DARK_BLUE + "    __  ___          __              " + LIGHT_BLUE + "_ __",
            DARK_BLUE + "   /  |/  /___  ____/ /__  ________ " + LIGHT_BLUE + "| |/ /",
            DARK_BLUE + "  / /|_/ / __ \\/ __  / _ \\/ ___/ _ \\" + LIGHT_BLUE + "|   / ",
            DARK_BLUE + " / /  / / /_/ / /_/ /  __/ /  /  __/" + LIGHT_BLUE + "/   |  ",
            DARK_BLUE + "/_/  /_/\\____/\\__,_/\\___/_/   \\___/" + LIGHT_BLUE + "/_/|_|  ",
            "",
            RESET + "           Advanced Moderation for Minecraft",
            "",
            GRAY + "   © 2026 BlockForge Studios & ADF Industries",
            GRAY + "   Version: " + WHITE + getDescription().getVersion(),
            "",
            GRAY + "   Discord:",
            GRAY + "   - BlockForge Studios: " + LIGHT_BLUE + "https://discord.gg/jQGMhKA5m6",
            GRAY + "   - ADF Industries: " + LIGHT_BLUE + "https://discord.gg/qWpcRmDW2P",
            "",
            WHITE + "   ★ Dev Build Tester Applications Now Open!",
            GRAY + "   Apply: " + LIGHT_BLUE + "https://discord.com/channels/1131588549892907089/1309211930665156668",
            GRAY + "   Discord: " + LIGHT_BLUE + "https://discord.gg/jQGMhKA5m6",
            ""
        };

        for (String line : banner) {
            getServer().getConsoleSender().sendMessage(line + RESET);
        }
    }

    private void logStartup(String message) {
        getLogger().info(message);
        // Send success messages to webhook (plugin startup messages are success)
        if (debugWebhook != null && debugWebhook.isEnabled()) {
            debugWebhook.success("[ModereX] " + message);
        }
    }

    public void logDebug(String message) {
        if (configManager != null && configManager.getSettings().isDebugMode()) {
            getLogger().info("[DEBUG] " + message);
            // Send debug messages to webhook
            if (debugWebhook != null && debugWebhook.isEnabled()) {
                debugWebhook.debug("[DEBUG] " + message);
            }
        }
    }

    public void logError(String message, Throwable throwable) {
        getLogger().log(Level.SEVERE, message, throwable);
        // Send error messages to webhook
        if (debugWebhook != null && debugWebhook.isEnabled()) {
            String errorMsg = message;
            if (throwable != null) {
                errorMsg += ": " + throwable.getMessage();
            }
            debugWebhook.error("[ERROR] " + errorMsg);
        }
    }

    // Getters for all managers
    public static ModereX getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public PunishmentManager getPunishmentManager() {
        return punishmentManager;
    }

    public PunishmentScheduler getPunishmentScheduler() {
        return punishmentScheduler;
    }

    public AutomodManager getAutomodManager() {
        return automodManager;
    }

    public com.blockforge.moderex.automod.AfkManager getAfkManager() {
        return afkManager;
    }

    public com.blockforge.moderex.hooks.anticheat.AnticheatManager getAnticheatManager() {
        return anticheatManager;
    }

    public com.blockforge.moderex.hooks.moderation.ModerationHookManager getModerationHookManager() {
        return moderationHookManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public StaffChatManager getStaffChatManager() {
        return staffChatManager;
    }

    public VanishManager getVanishManager() {
        return vanishManager;
    }

    public VanishAPI getVanishAPI() {
        return vanishAPI;
    }

    public AutomodAPI getAutomodAPI() {
        return automodAPI;
    }

    public com.blockforge.moderex.disguise.DisguiseManager getDisguiseManager() {
        return disguiseManager;
    }

    public com.blockforge.moderex.staff.StaffModeManager getStaffModeManager() {
        return staffModeManager;
    }

    public com.blockforge.moderex.geoip.GeoIPManager getGeoIPManager() {
        return geoIPManager;
    }

    public WatchlistManager getWatchlistManager() {
        return watchlistManager;
    }

    public com.blockforge.moderex.rules.RulesManager getRulesManager() {
        return rulesManager;
    }

    public com.blockforge.moderex.rules.RuleAcceptanceManager getRuleAcceptanceManager() {
        return ruleAcceptanceManager;
    }

    public com.blockforge.moderex.rules.CodeOfConductManager getCodeOfConductManager() {
        return codeOfConductManager;
    }

    public com.blockforge.moderex.ai.OllamaClient getOllamaClient() {
        return ollamaClient;
    }

    public com.blockforge.moderex.ai.AIModerationManager getAiModerationManager() {
        return aiModerationManager;
    }

    public com.blockforge.moderex.security.RaidProtectionManager getRaidProtectionManager() {
        return raidProtectionManager;
    }

    public com.blockforge.moderex.player.PlayerProfileManager getPlayerProfileManager() {
        return playerProfileManager;
    }

    public ProxyManager getProxyManager() {
        return proxyManager;
    }

    public com.blockforge.moderex.web.WebAuthManager getWebAuthManager() {
        return webAuthManager;
    }

    public HybridPanelServer getWebPanelServer() {
        return hybridPanelServer;
    }

    public WebPanelDebugger getWebPanelDebugger() {
        return webPanelDebugger;
    }

    public com.blockforge.moderex.util.GitHubAutoUpdater getGitHubAutoUpdater() {
        return githubAutoUpdater;
    }

    public HookManager getHookManager() {
        return hookManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public com.blockforge.moderex.commands.admin.MxCommand getMxCommand() {
        return commandManager != null ? commandManager.getMxCommand() : null;
    }

    public ListenerManager getListenerManager() {
        return listenerManager;
    }

    public ReplayManager getReplayManager() {
        return replayManager;
    }

    public ActivityLogManager getActivityLogManager() {
        return activityLogManager;
    }

    public com.blockforge.moderex.evidence.EvidenceManager getEvidenceManager() {
        return evidenceManager;
    }

    public com.blockforge.moderex.evidence.EvidenceSelectionManager getEvidenceSelectionManager() {
        return evidenceSelectionManager;
    }

    public com.blockforge.moderex.portal.AuthSessionManager getAuthSessionManager() {
        return authSessionManager;
    }

    public com.blockforge.moderex.identity.ServerIdentity getServerIdentity() {
        return serverIdentity;
    }

    public com.blockforge.moderex.gateway.GatewayClient getGatewayClient() {
        return gatewayClient;
    }

    public com.blockforge.moderex.permissions.PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public BlockLogManager getBlockLogManager() {
        return blockLogManager;
    }

    public FakeBlockManager getFakeBlockManager() {
        return fakeBlockManager;
    }

    public EntityLogManager getEntityLogManager() {
        return entityLogManager;
    }

    public TemplateManager getTemplateManager() {
        return templateManager;
    }

    public StaffSettingsManager getStaffSettingsManager() {
        return staffSettingsManager;
    }

    public com.blockforge.moderex.alert.AlertManager getAlertManager() {
        return alertManager;
    }

    public com.blockforge.moderex.resourcepack.ResourcePackManager getResourcePackManager() {
        return resourcePackManager;
    }

    public com.blockforge.moderex.monitor.ServerStatusManager getServerStatusManager() {
        return serverStatusManager;
    }

    public com.blockforge.moderex.monitor.PerformanceSettingsManager getPerformanceSettingsManager() {
        return performanceSettingsManager;
    }

    public com.blockforge.moderex.monitor.TpsThrottleManager getTpsThrottleManager() {
        return tpsThrottleManager;
    }

    public com.blockforge.moderex.monitor.MemoryManager getMemoryManager() {
        return memoryManager;
    }

    public LicenseManager getLicenseManager() {
        return licenseManager;
    }

    public Component getPrefix() {
        return languageManager.getPrefix();
    }

    // Lockdown methods
    public boolean isGlobalLockdown() {
        return globalLockdown;
    }

    public void setGlobalLockdown(boolean lockdown) {
        this.globalLockdown = lockdown;
    }

    public boolean isLocalLockdown() {
        return localLockdown;
    }

    public void setLocalLockdown(boolean lockdown) {
        this.localLockdown = lockdown;
    }

    /**
     * Broadcast a message to all players with a specific permission
     */
    public void broadcastToPermission(com.blockforge.moderex.config.lang.MessageKey messageKey, String permission, String... placeholders) {
        Component message = languageManager.get(messageKey, placeholders);
        for (org.bukkit.entity.Player player : getServer().getOnlinePlayers()) {
            if (player.hasPermission(permission)) {
                Msg.send(player, message);
            }
        }
        // Also send to console
        Msg.send(getServer().getConsoleSender(), message);
    }
}
