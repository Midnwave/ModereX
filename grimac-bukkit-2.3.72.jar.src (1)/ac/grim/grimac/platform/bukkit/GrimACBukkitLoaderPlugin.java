/*     */ package ac.grim.grimac.platform.bukkit;
/*     */ 
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.api.GrimAPIProvider;
/*     */ import ac.grim.grimac.api.GrimAbstractAPI;
/*     */ import ac.grim.grimac.api.plugin.BasicGrimPlugin;
/*     */ import ac.grim.grimac.api.plugin.GrimPlugin;
/*     */ import ac.grim.grimac.manager.init.Initable;
/*     */ import ac.grim.grimac.manager.init.start.ExemptOnlinePlayersOnReload;
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
/*     */ import ac.grim.grimac.platform.bukkit.initables.BukkitBStats;
/*     */ import ac.grim.grimac.platform.bukkit.initables.BukkitEventManager;
/*     */ import ac.grim.grimac.platform.bukkit.initables.BukkitTickEndEvent;
/*     */ import ac.grim.grimac.platform.bukkit.manager.BukkitMessagePlaceHolderManager;
/*     */ import ac.grim.grimac.platform.bukkit.manager.BukkitParserDescriptorFactory;
/*     */ import ac.grim.grimac.platform.bukkit.manager.BukkitPermissionRegistrationManager;
/*     */ import ac.grim.grimac.platform.bukkit.manager.BukkitPlatformPluginManager;
/*     */ import ac.grim.grimac.platform.bukkit.player.BukkitPlatformPlayerFactory;
/*     */ import ac.grim.grimac.platform.bukkit.scheduler.bukkit.BukkitPlatformScheduler;
/*     */ import ac.grim.grimac.platform.bukkit.scheduler.folia.FoliaPlatformScheduler;
/*     */ import ac.grim.grimac.platform.bukkit.sender.BukkitSenderFactory;
/*     */ import ac.grim.grimac.platform.bukkit.utils.placeholder.PlaceholderAPIExpansion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEventsAPI;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CloudCapability;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.SenderMapper;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.BrigadierSetting;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.CloudBrigadierManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.CloudBukkitCapabilities;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.execution.ExecutionCoordinator;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.paper.LegacyPaperCommandManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.setting.Configurable;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.setting.Setting;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.utils.lazy.LazyHolder;
/*     */ import java.util.function.Supplier;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.ServicePriority;
/*     */ import org.bukkit.plugin.java.JavaPlugin;
/*     */ 
/*     */ public final class GrimACBukkitLoaderPlugin extends JavaPlugin implements PlatformLoader {
/*  56 */   private final LazyHolder<PlatformScheduler> scheduler = LazyHolder.simple(this::createScheduler); public static GrimACBukkitLoaderPlugin LOADER;
/*  57 */   private final LazyHolder<PacketEventsAPI<?>> packetEvents = LazyHolder.simple(() -> SpigotPacketEventsBuilder.build((Plugin)this));
/*  58 */   private final LazyHolder<BukkitSenderFactory> senderFactory = LazyHolder.simple(BukkitSenderFactory::new);
/*  59 */   private final LazyHolder<CommandManager<Sender>> commandManager = LazyHolder.simple(this::createCommandManager);
/*  60 */   private final LazyHolder<ItemResetHandler> itemResetHandler = LazyHolder.simple(ac.grim.grimac.platform.bukkit.manager.BukkitItemResetHandler::new);
/*     */   
/*  62 */   private final PlatformPlayerFactory playerFactory = (PlatformPlayerFactory)new BukkitPlatformPlayerFactory();
/*  63 */   private final CommandAdapter parserFactory = (CommandAdapter)new BukkitParserDescriptorFactory();
/*  64 */   private final PlatformPluginManager platformPluginManager = (PlatformPluginManager)new BukkitPlatformPluginManager();
/*     */   private final GrimPlugin plugin;
/*  66 */   private final PlatformServer platformServer = new BukkitPlatformServer();
/*  67 */   private final MessagePlaceHolderManager messagePlaceHolderManager = (MessagePlaceHolderManager)new BukkitMessagePlaceHolderManager();
/*  68 */   private final BukkitPermissionRegistrationManager bukkitPermissionRegistrationManager = new BukkitPermissionRegistrationManager();
/*     */   
/*     */   public GrimACBukkitLoaderPlugin() {
/*  71 */     this
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  76 */       .plugin = (GrimPlugin)new BasicGrimPlugin(getLogger(), getDataFolder(), getDescription().getVersion(), getDescription().getDescription(), getDescription().getAuthors());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onLoad() {
/*  82 */     LOADER = this;
/*  83 */     GrimAPI.INSTANCE.load(this, getBukkitInitTasks());
/*     */   }
/*     */   
/*     */   private Initable[] getBukkitInitTasks() {
/*  87 */     return new Initable[] { (Initable)new ExemptOnlinePlayersOnReload(), (Initable)new BukkitEventManager(), (Initable)new BukkitTickEndEvent(), (Initable)new BukkitBStats(), (Initable)(() -> {
/*     */           if (BukkitMessagePlaceHolderManager.hasPlaceholderAPI) {
/*     */             (new PlaceholderAPIExpansion()).register();
/*     */           }
/*     */         }) };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void onEnable() {
/* 102 */     GrimAPI.INSTANCE.start();
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisable() {
/* 107 */     GrimAPI.INSTANCE.stop();
/*     */   }
/*     */ 
/*     */   
/*     */   public PlatformScheduler getScheduler() {
/* 112 */     return (PlatformScheduler)this.scheduler.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public PlatformPlayerFactory getPlatformPlayerFactory() {
/* 117 */     return this.playerFactory;
/*     */   }
/*     */ 
/*     */   
/*     */   public CommandAdapter getCommandAdapter() {
/* 122 */     return this.parserFactory;
/*     */   }
/*     */ 
/*     */   
/*     */   public PacketEventsAPI<?> getPacketEvents() {
/* 127 */     return (PacketEventsAPI)this.packetEvents.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public CommandManager<Sender> getCommandManager() {
/* 132 */     return (CommandManager<Sender>)this.commandManager.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public ItemResetHandler getItemResetHandler() {
/* 137 */     return (ItemResetHandler)this.itemResetHandler.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public SenderFactory<CommandSender> getSenderFactory() {
/* 142 */     return (SenderFactory<CommandSender>)this.senderFactory.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public GrimPlugin getPlugin() {
/* 147 */     return this.plugin;
/*     */   }
/*     */ 
/*     */   
/*     */   public PlatformPluginManager getPluginManager() {
/* 152 */     return this.platformPluginManager;
/*     */   }
/*     */ 
/*     */   
/*     */   public PlatformServer getPlatformServer() {
/* 157 */     return this.platformServer;
/*     */   }
/*     */ 
/*     */   
/*     */   public void registerAPIService() {
/* 162 */     GrimAPIProvider.init((GrimAbstractAPI)GrimAPI.INSTANCE.getExternalAPI());
/* 163 */     Bukkit.getServicesManager().register(GrimAbstractAPI.class, GrimAPI.INSTANCE.getExternalAPI(), (Plugin)LOADER, ServicePriority.Normal);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public MessagePlaceHolderManager getMessagePlaceHolderManager() {
/* 168 */     return this.messagePlaceHolderManager;
/*     */   }
/*     */ 
/*     */   
/*     */   public PermissionRegistrationManager getPermissionManager() {
/* 173 */     return (PermissionRegistrationManager)this.bukkitPermissionRegistrationManager;
/*     */   }
/*     */   
/*     */   private PlatformScheduler createScheduler() {
/* 177 */     return (GrimAPI.INSTANCE.getPlatform() == Platform.FOLIA) ? (PlatformScheduler)new FoliaPlatformScheduler() : (PlatformScheduler)new BukkitPlatformScheduler();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private CommandManager<Sender> createCommandManager() {
/* 184 */     LegacyPaperCommandManager<Sender> manager = new LegacyPaperCommandManager((Plugin)this, ExecutionCoordinator.simpleCoordinator(), (SenderMapper)this.senderFactory.get());
/*     */     
/* 186 */     if (manager.hasCapability((CloudCapability)CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
/* 187 */       manager.registerBrigadier();
/* 188 */       CloudBrigadierManager<Sender, ?> cbm = manager.brigadierManager();
/* 189 */       Configurable<BrigadierSetting> settings = cbm.settings();
/* 190 */       settings.set((Setting)BrigadierSetting.FORCE_EXECUTABLE, true);
/* 191 */     } else if (manager.hasCapability((CloudCapability)CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
/* 192 */       manager.registerAsynchronousCompletions();
/*     */     } 
/* 194 */     return (CommandManager<Sender>)manager;
/*     */   }
/*     */   
/*     */   public BukkitSenderFactory getBukkitSenderFactory() {
/* 198 */     return (BukkitSenderFactory)LOADER.senderFactory.get();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\GrimACBukkitLoaderPlugin.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */