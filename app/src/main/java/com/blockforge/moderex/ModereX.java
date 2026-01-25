package com.blockforge.moderex;

import com.blockforge.moderex.automod.AutomodManager;
import com.blockforge.moderex.automod.api.AutomodAPI;
import com.blockforge.moderex.commands.CommandManager;
import com.blockforge.moderex.config.ConfigManager;
import com.blockforge.moderex.config.lang.LanguageManager;
import com.blockforge.moderex.database.DatabaseManager;
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
import com.blockforge.moderex.util.UpdateChecker;
import com.blockforge.moderex.util.VersionUtil;
import com.blockforge.moderex.watchlist.WatchlistManager;
import com.blockforge.moderex.webpanel.HybridPanelServer;
import com.blockforge.moderex.webpanel.debug.WebPanelDebugger;
import com.blockforge.moderex.webpanel.netty.NettyInjector;
import com.blockforge.moderex.webpanel.netty.SamePortPanelHandler;
import com.blockforge.moderex.webpanel.netty.ServerVersionUtil;
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
    private NettyInjector nettyInjector;
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
    private StaffSettingsManager staffSettingsManager;
    private com.blockforge.moderex.resourcepack.ResourcePackManager resourcePackManager;
    private com.blockforge.moderex.rules.RulesManager rulesManager;
    private com.blockforge.moderex.automod.AfkManager afkManager;
    private DebugWebhook debugWebhook;
    private WebPanelDebugger webPanelDebugger;

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
        logStartup("Detected Minecraft version: " + VersionUtil.getServerVersion());

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

        logStartup("Initializing GeoIP system...");
        this.geoIPManager = new com.blockforge.moderex.geoip.GeoIPManager(this);
        geoIPManager.initialize();

        logStartup("Initializing watchlist...");
        this.watchlistManager = new WatchlistManager(this);

        logStartup("Initializing rules system...");
        this.rulesManager = new com.blockforge.moderex.rules.RulesManager(this);
        rulesManager.initialize();

        logStartup("Initializing replay system...");
        this.blockLogManager = new BlockLogManager(this);
        this.fakeBlockManager = new FakeBlockManager(this);
        this.entityLogManager = new EntityLogManager(this);
        this.replayManager = new ReplayManager(this);
        replayManager.start();

        logStartup("Initializing activity log system...");
        this.activityLogManager = new ActivityLogManager(this);
        activityLogManager.initialize();

        logStartup("Initializing server status monitor...");
        this.serverStatusManager = new com.blockforge.moderex.monitor.ServerStatusManager(this);
        if (configManager.getSettings().isServerStatusEnabled()) {
            serverStatusManager.start();
        }

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
            if (configManager.getSettings().isWebPanelSamePort()) {
                // EXPERIMENTAL: Use Netty injection to host panel on Minecraft's port
                logStartup("Starting web panel on same port as Minecraft (experimental)...");
                NettyInjector injector = ServerVersionUtil.getInjector();
                if (injector != null) {
                    SamePortPanelHandler handler = new SamePortPanelHandler(this);
                    if (injector.inject(this, handler)) {
                        this.nettyInjector = injector;
                        logStartup("Web panel same-port mode enabled using " + injector.getName());
                    } else {
                        getLogger().warning("Failed to inject Netty handler for same-port mode!");
                        getLogger().warning("Falling back to dedicated port mode...");
                        startDedicatedPanelServer();
                    }
                } else {
                    getLogger().warning("Netty injection not available for this server version!");
                    getLogger().warning("Falling back to dedicated port mode...");
                    startDedicatedPanelServer();
                }
            } else {
                startDedicatedPanelServer();
            }

            // Initialize web panel debugger after panel server is created
            if (hybridPanelServer != null) {
                logStartup("Initializing web panel debugger...");
                this.webPanelDebugger = new WebPanelDebugger(this);
            }
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
        if (configManager.getSettings().isGithubAutoUpdateEnabled()) {
            logStartup("Checking GitHub for updates...");
            new com.blockforge.moderex.util.GitHubAutoUpdater(this).checkAsync();
        }

        long endTime = System.currentTimeMillis();
        logStartup("ModereX enabled successfully in " + (endTime - startTime) + "ms!");
    }

    @Override
    public void onDisable() {
        logStartup("Disabling ModereX...");

        // Stop web panel debugger
        if (webPanelDebugger != null) {
            webPanelDebugger.shutdown();
        }

        // Stop web panel server
        if (hybridPanelServer != null) {
            hybridPanelServer.stop();
        }

        // Remove Netty injection if active
        if (nettyInjector != null && nettyInjector.isInjected()) {
            nettyInjector.remove();
            nettyInjector = null;
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

        // Stop punishment scheduler
        if (punishmentScheduler != null) {
            punishmentScheduler.stop();
        }

        // Stop AFK manager
        if (afkManager != null) {
            afkManager.stop();
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

        instance = null;
        getLogger().info("ModereX disabled.");
    }

    public void reload() {
        getLogger().info("Reloading ModereX...");

        // Reload configuration
        configManager.load();
        languageManager.load();
        automodManager.load();

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
        if (nettyInjector != null && nettyInjector.isInjected()) {
            nettyInjector.remove();
            nettyInjector = null;
        }

        // Restart web panel if enabled
        if (configManager.getSettings().isWebPanelEnabled()) {
            if (configManager.getSettings().isWebPanelSamePort()) {
                // Same-port mode
                NettyInjector injector = ServerVersionUtil.getInjector();
                if (injector != null) {
                    SamePortPanelHandler handler = new SamePortPanelHandler(this);
                    if (injector.inject(this, handler)) {
                        this.nettyInjector = injector;
                        getLogger().info("Web panel same-port mode enabled using " + injector.getName());
                    } else {
                        getLogger().warning("Failed to inject Netty handler, falling back to dedicated port");
                        startDedicatedPanelServer();
                    }
                } else {
                    getLogger().warning("Netty injection not available, falling back to dedicated port");
                    startDedicatedPanelServer();
                }
            } else {
                startDedicatedPanelServer();
            }

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

    public HookManager getHookManager() {
        return hookManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
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

    public com.blockforge.moderex.resourcepack.ResourcePackManager getResourcePackManager() {
        return resourcePackManager;
    }

    public com.blockforge.moderex.monitor.ServerStatusManager getServerStatusManager() {
        return serverStatusManager;
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
                player.sendMessage(message);
            }
        }
        // Also send to console
        getServer().getConsoleSender().sendMessage(message);
    }
}
