package com.blockforge.moderex;

import com.blockforge.moderex.automod.AutomodManager;
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
import com.blockforge.moderex.staff.StaffChatManager;
import com.blockforge.moderex.staff.StaffSettingsManager;
import com.blockforge.moderex.staff.VanishManager;
import com.blockforge.moderex.util.UpdateChecker;
import com.blockforge.moderex.util.VersionUtil;
import com.blockforge.moderex.watchlist.WatchlistManager;
import com.blockforge.moderex.webpanel.HybridPanelServer;
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
    private TemplateManager templateManager;
    private com.blockforge.moderex.monitor.ServerStatusManager serverStatusManager;
    private StaffSettingsManager staffSettingsManager;

    @Override
    public void onEnable() {
        instance = this;
        long startTime = System.currentTimeMillis();

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

        // Initialize core managers
        logStartup("Initializing punishment system...");
        this.punishmentManager = new PunishmentManager(this);
        this.punishmentScheduler = new PunishmentScheduler(this);
        punishmentScheduler.start();

        logStartup("Initializing automod system...");
        this.automodManager = new AutomodManager(this);
        automodManager.load();

        logStartup("Initializing anticheat integrations...");
        this.anticheatManager = new com.blockforge.moderex.hooks.anticheat.AnticheatManager(this);
        anticheatManager.initialize();

        logStartup("Initializing moderation plugin integrations...");
        this.moderationHookManager = new com.blockforge.moderex.hooks.moderation.ModerationHookManager(this);
        moderationHookManager.initialize();

        logStartup("Initializing GUI system...");
        this.guiManager = new GuiManager(this);

        logStartup("Initializing staff features...");
        this.staffChatManager = new StaffChatManager(this);
        this.vanishManager = new VanishManager(this);
        this.staffSettingsManager = new StaffSettingsManager(this);

        logStartup("Initializing watchlist...");
        this.watchlistManager = new WatchlistManager(this);

        logStartup("Initializing replay system...");
        this.replayManager = new ReplayManager(this);
        replayManager.start();

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
        }

        // Register commands
        logStartup("Registering commands...");
        this.commandManager = new CommandManager(this);
        commandManager.registerAll();

        // Register listeners
        logStartup("Registering event listeners...");
        this.listenerManager = new ListenerManager(this);
        listenerManager.registerAll();

        // Check for updates
        if (configManager.getSettings().isUpdateCheckerEnabled()) {
            new UpdateChecker(this).checkAsync();
        }

        long endTime = System.currentTimeMillis();
        logStartup("ModereX enabled successfully in " + (endTime - startTime) + "ms!");
    }

    @Override
    public void onDisable() {
        logStartup("Disabling ModereX...");

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

        // Stop punishment scheduler
        if (punishmentScheduler != null) {
            punishmentScheduler.stop();
        }

        // Close database connections
        if (databaseManager != null) {
            databaseManager.shutdown();
        }

        // Unhook from other plugins
        if (hookManager != null) {
            hookManager.shutdown();
        }

        // Shutdown anticheat integrations
        if (anticheatManager != null) {
            anticheatManager.shutdown();
        }

        instance = null;
        logStartup("ModereX disabled.");
    }

    public void reload() {
        getLogger().info("Reloading ModereX...");

        // Reload configuration
        configManager.load();
        languageManager.load();
        automodManager.load();

        // Stop existing web panel services
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
        }

        getLogger().info("ModereX reloaded successfully!");
    }

    private void startDedicatedPanelServer() {
        int port = configManager.getSettings().getWebPanelPort();
        logStartup("Starting web panel server on port " + port + "...");
        this.hybridPanelServer = new HybridPanelServer(this, port);
        hybridPanelServer.start();
    }

    private void logStartup(String message) {
        getLogger().info(message);
    }

    public void logDebug(String message) {
        if (configManager != null && configManager.getSettings().isDebugMode()) {
            getLogger().info("[DEBUG] " + message);
        }
    }

    public void logError(String message, Throwable throwable) {
        getLogger().log(Level.SEVERE, message, throwable);
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

    public WatchlistManager getWatchlistManager() {
        return watchlistManager;
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

    public TemplateManager getTemplateManager() {
        return templateManager;
    }

    public StaffSettingsManager getStaffSettingsManager() {
        return staffSettingsManager;
    }

    public com.blockforge.moderex.monitor.ServerStatusManager getServerStatusManager() {
        return serverStatusManager;
    }

    public Component getPrefix() {
        return languageManager.getPrefix();
    }
}
