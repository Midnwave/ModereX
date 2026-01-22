/*     */ package ac.grim.grimac;
/*     */ 
/*     */ import ac.grim.grimac.api.event.EventBus;
/*     */ import ac.grim.grimac.api.event.OptimizedEventBus;
/*     */ import ac.grim.grimac.api.plugin.GrimPlugin;
/*     */ import ac.grim.grimac.manager.AlertManagerImpl;
/*     */ import ac.grim.grimac.manager.DiscordManager;
/*     */ import ac.grim.grimac.manager.InitManager;
/*     */ import ac.grim.grimac.manager.SpectateManager;
/*     */ import ac.grim.grimac.manager.TickManager;
/*     */ import ac.grim.grimac.manager.config.BaseConfigManager;
/*     */ import ac.grim.grimac.manager.init.Initable;
/*     */ import ac.grim.grimac.manager.violationdatabase.ViolationDatabaseManager;
/*     */ import ac.grim.grimac.platform.api.Platform;
/*     */ import ac.grim.grimac.platform.api.PlatformLoader;
/*     */ import ac.grim.grimac.platform.api.PlatformServer;
/*     */ import ac.grim.grimac.platform.api.manager.CommandAdapter;
/*     */ import ac.grim.grimac.platform.api.manager.ItemResetHandler;
/*     */ import ac.grim.grimac.platform.api.manager.MessagePlaceHolderManager;
/*     */ import ac.grim.grimac.platform.api.manager.PermissionRegistrationManager;
/*     */ import ac.grim.grimac.platform.api.manager.PlatformPluginManager;
/*     */ import ac.grim.grimac.platform.api.player.PlatformPlayerFactory;
/*     */ import ac.grim.grimac.platform.api.scheduler.PlatformScheduler;
/*     */ import ac.grim.grimac.platform.api.sender.Sender;
/*     */ import ac.grim.grimac.platform.api.sender.SenderFactory;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.utils.anticheat.PlayerDataManager;
/*     */ import ac.grim.grimac.utils.common.GrimArguments;
/*     */ import ac.grim.grimac.utils.reflection.ReflectionUtils;
/*     */ import java.util.Objects;
/*     */ import lombok.Generated;
/*     */ 
/*     */ public final class GrimAPI
/*     */ {
/*  36 */   public static final GrimAPI INSTANCE = new GrimAPI(); @Generated
/*     */   public Platform getPlatform() {
/*  38 */     return this.platform;
/*  39 */   } private final BaseConfigManager configManager; private final AlertManagerImpl alertManager; private final SpectateManager spectateManager; private final DiscordManager discordManager; private final PlayerDataManager playerDataManager; private final TickManager tickManager; private final Platform platform = detectPlatform(); private final EventBus eventBus; private final GrimExternalAPI externalAPI; private ViolationDatabaseManager violationDatabaseManager; private PlatformLoader loader; private InitManager initManager; @Generated
/*  40 */   public BaseConfigManager getConfigManager() { return this.configManager; } @Generated
/*  41 */   public AlertManagerImpl getAlertManager() { return this.alertManager; } @Generated
/*  42 */   public SpectateManager getSpectateManager() { return this.spectateManager; } @Generated
/*  43 */   public DiscordManager getDiscordManager() { return this.discordManager; } @Generated
/*  44 */   public PlayerDataManager getPlayerDataManager() { return this.playerDataManager; } @Generated
/*  45 */   public TickManager getTickManager() { return this.tickManager; } @Generated
/*  46 */   public EventBus getEventBus() { return this.eventBus; } @Generated
/*  47 */   public GrimExternalAPI getExternalAPI() { return this.externalAPI; } @Generated
/*  48 */   public ViolationDatabaseManager getViolationDatabaseManager() { return this.violationDatabaseManager; } @Generated
/*  49 */   public PlatformLoader getLoader() { return this.loader; } @Generated
/*  50 */   public InitManager getInitManager() { return this.initManager; } private boolean initialized = false; @Generated
/*     */   public boolean isInitialized() {
/*  52 */     return this.initialized;
/*     */   }
/*     */   private GrimAPI() {
/*  55 */     this.configManager = new BaseConfigManager();
/*  56 */     this.alertManager = new AlertManagerImpl();
/*  57 */     this.spectateManager = new SpectateManager();
/*  58 */     this.discordManager = new DiscordManager();
/*  59 */     this.playerDataManager = new PlayerDataManager();
/*  60 */     this.tickManager = new TickManager();
/*  61 */     this.eventBus = (EventBus)new OptimizedEventBus();
/*  62 */     this.externalAPI = new GrimExternalAPI(this);
/*     */   }
/*     */ 
/*     */   
/*     */   private static Platform detectPlatform() {
/*  67 */     Platform override = Platform.getByName(GrimArguments.PLATFORM_OVERRIDE);
/*  68 */     if (override != null) return override; 
/*  69 */     if (ReflectionUtils.hasClass("io.papermc.paper.threadedregions.RegionizedServer")) return Platform.FOLIA; 
/*  70 */     if (ReflectionUtils.hasClass("org.bukkit.Bukkit")) return Platform.BUKKIT; 
/*  71 */     if (ReflectionUtils.hasClass("net.fabricmc.loader.api.FabricLoader")) return Platform.FABRIC; 
/*  72 */     throw new IllegalStateException("Unknown platform!");
/*     */   }
/*     */   
/*     */   public void load(PlatformLoader platformLoader, Initable... platformSpecificInitables) {
/*  76 */     this.loader = platformLoader;
/*  77 */     this.violationDatabaseManager = new ViolationDatabaseManager(getGrimPlugin());
/*  78 */     Objects.requireNonNull(this.loader); this.initManager = new InitManager(this.loader.getPacketEvents(), this.loader::getCommandManager, platformSpecificInitables);
/*  79 */     this.initManager.load();
/*  80 */     this.initialized = true;
/*     */   }
/*     */   
/*     */   public void start() {
/*  84 */     checkInitialized();
/*  85 */     this.initManager.start();
/*     */   }
/*     */   
/*     */   public void stop() {
/*  89 */     checkInitialized();
/*  90 */     this.initManager.stop();
/*     */   }
/*     */   
/*     */   public PlatformScheduler getScheduler() {
/*  94 */     return this.loader.getScheduler();
/*     */   }
/*     */   
/*     */   public PlatformPlayerFactory getPlatformPlayerFactory() {
/*  98 */     return this.loader.getPlatformPlayerFactory();
/*     */   }
/*     */   
/*     */   public CommandAdapter getCommandAdapter() {
/* 102 */     return this.loader.getCommandAdapter();
/*     */   }
/*     */   
/*     */   public GrimPlugin getGrimPlugin() {
/* 106 */     return this.loader.getPlugin();
/*     */   }
/*     */   
/*     */   public SenderFactory<?> getSenderFactory() {
/* 110 */     return this.loader.getSenderFactory();
/*     */   }
/*     */   
/*     */   public ItemResetHandler getItemResetHandler() {
/* 114 */     return this.loader.getItemResetHandler();
/*     */   }
/*     */   
/*     */   public PlatformPluginManager getPluginManager() {
/* 118 */     return this.loader.getPluginManager();
/*     */   }
/*     */   
/*     */   public PlatformServer getPlatformServer() {
/* 122 */     return this.loader.getPlatformServer();
/*     */   }
/*     */   @NotNull
/*     */   public MessagePlaceHolderManager getMessagePlaceHolderManager() {
/* 126 */     return this.loader.getMessagePlaceHolderManager();
/*     */   }
/*     */   
/*     */   public CommandManager<Sender> getCommandManager() {
/* 130 */     return this.loader.getCommandManager();
/*     */   }
/*     */   
/*     */   private void checkInitialized() {
/* 134 */     if (!this.initialized) {
/* 135 */       throw new IllegalStateException("GrimAPI has not been initialized!");
/*     */     }
/*     */   }
/*     */   
/*     */   public PermissionRegistrationManager getPermissionManager() {
/* 140 */     return this.loader.getPermissionManager();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\GrimAPI.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */