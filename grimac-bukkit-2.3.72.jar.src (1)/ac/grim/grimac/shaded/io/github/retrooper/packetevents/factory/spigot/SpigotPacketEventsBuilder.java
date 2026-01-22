/*     */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.factory.spigot;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEventsAPI;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerCommon;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.injector.ChannelInjector;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.player.PlayerManager;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.protocol.ProtocolManager;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerManager;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.NettyManager;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.settings.PacketEventsSettings;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.LogManager;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.PEVersion;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.bstats.bukkit.Metrics;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.bstats.charts.CustomChart;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.bstats.charts.SimplePie;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.bukkit.InternalBukkitListener;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.bukkit.InternalBukkitLoginListener;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.bukkit.InternalGlobalBukkitListener;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.bukkit.InternalPaperListener;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.injector.SpigotChannelInjector;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.injector.connection.ServerConnectionInitializer;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.manager.InternalBukkitPacketListener;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.manager.player.PlayerManagerImpl;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.manager.protocol.ProtocolManagerImpl;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.manager.server.ServerManagerImpl;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.netty.NettyManagerImpl;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.BukkitLogManager;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.SpigotReflectionUtil;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.folia.FoliaScheduler;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.protocolsupport.ProtocolSupportUtil;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.viaversion.CustomPipelineUtil;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.viaversion.ViaVersionUtil;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SpigotPacketEventsBuilder
/*     */ {
/*     */   private static PacketEventsAPI<Plugin> API_INSTANCE;
/*     */   
/*     */   public static void clearBuildCache() {
/*  62 */     API_INSTANCE = null;
/*     */   }
/*     */   
/*     */   public static PacketEventsAPI<Plugin> build(Plugin plugin) {
/*  66 */     if (API_INSTANCE == null) {
/*  67 */       API_INSTANCE = buildNoCache(plugin);
/*     */     }
/*  69 */     return API_INSTANCE;
/*     */   }
/*     */   
/*     */   public static PacketEventsAPI<Plugin> build(Plugin plugin, PacketEventsSettings settings) {
/*  73 */     if (API_INSTANCE == null) {
/*  74 */       API_INSTANCE = buildNoCache(plugin, settings);
/*     */     }
/*  76 */     return API_INSTANCE;
/*     */   }
/*     */   
/*     */   public static PacketEventsAPI<Plugin> buildNoCache(Plugin plugin) {
/*  80 */     return buildNoCache(plugin, new PacketEventsSettings());
/*     */   }
/*     */   
/*     */   public static PacketEventsAPI<Plugin> buildNoCache(final Plugin plugin, final PacketEventsSettings inSettings) {
/*  84 */     return new PacketEventsAPI<Plugin>() {
/*  85 */         private final PacketEventsSettings settings = inSettings;
/*  86 */         private final ProtocolManager protocolManager = (ProtocolManager)new ProtocolManagerImpl();
/*  87 */         private final ServerManager serverManager = (ServerManager)new ServerManagerImpl();
/*  88 */         private final PlayerManager playerManager = (PlayerManager)new PlayerManagerImpl();
/*  89 */         private final NettyManager nettyManager = (NettyManager)new NettyManagerImpl();
/*  90 */         private final SpigotChannelInjector injector = new SpigotChannelInjector();
/*  91 */         private final LogManager logManager = (LogManager)new BukkitLogManager();
/*     */         
/*     */         private boolean loaded;
/*     */         private boolean initialized;
/*     */         private boolean lateBind = false;
/*     */         private boolean terminated = false;
/*     */         
/*     */         public void load() {
/*  99 */           if (!this.loaded) {
/*     */             
/* 101 */             String id = plugin.getName().toLowerCase();
/* 102 */             PacketEvents.IDENTIFIER = "pe-" + id;
/* 103 */             PacketEvents.ENCODER_NAME = "pe-encoder-" + id;
/* 104 */             PacketEvents.DECODER_NAME = "pe-decoder-" + id;
/* 105 */             PacketEvents.CONNECTION_HANDLER_NAME = "pe-connection-handler-" + id;
/* 106 */             PacketEvents.SERVER_CHANNEL_HANDLER_NAME = "pe-connection-initializer-" + id;
/* 107 */             PacketEvents.TIMEOUT_HANDLER_NAME = "pe-timeout-handler-" + id;
/*     */             try {
/* 109 */               SpigotReflectionUtil.init();
/* 110 */               CustomPipelineUtil.init();
/* 111 */               WrappedBlockState.ensureLoad();
/* 112 */             } catch (Exception ex) {
/* 113 */               throw new IllegalStateException(ex);
/*     */             } 
/*     */             
/* 116 */             if (!PacketType.isPrepared()) {
/* 117 */               PacketType.prepare();
/*     */             }
/*     */ 
/*     */             
/* 121 */             this.lateBind = !this.injector.isServerBound();
/*     */             
/* 123 */             if (!this.lateBind) {
/* 124 */               this.injector.inject();
/*     */             }
/*     */             
/* 127 */             this.loaded = true;
/*     */ 
/*     */ 
/*     */             
/* 131 */             getEventManager().registerListener((PacketListenerCommon)new InternalBukkitPacketListener());
/*     */           } 
/*     */         }
/*     */ 
/*     */         
/*     */         public boolean isLoaded() {
/* 137 */           return this.loaded;
/*     */         }
/*     */ 
/*     */ 
/*     */         
/*     */         public void init() {
/* 143 */           load();
/* 144 */           if (!this.initialized) {
/* 145 */             Plugin plugin = (Plugin)PacketEvents.getAPI().getPlugin();
/* 146 */             String bukkitVersion = Bukkit.getBukkitVersion();
/*     */             
/* 148 */             if (bukkitVersion.contains("Unknown")) {
/* 149 */               ServerVersion fallbackVersion = ServerVersion.V_1_8_8;
/* 150 */               String failureToDetectVersionMsg = "Your server software is preventing us from checking the Minecraft Server version. This is what we found: " + bukkitVersion + ". We will assume the Server version is " + fallbackVersion.name() + "...\n If you need assistance, join our Discord server: https://discord.gg/DVHxPPxHZc";
/* 151 */               plugin.getLogger().warning(failureToDetectVersionMsg);
/*     */             } else {
/*     */               
/* 154 */               PEVersion bukkitServerVersion = PEVersion.fromString(bukkitVersion.substring(0, bukkitVersion.indexOf("-")));
/* 155 */               PEVersion latestSupportedVersion = PEVersion.fromString(ServerVersion.getLatest().getReleaseName());
/* 156 */               if (bukkitServerVersion.isNewerThan(latestSupportedVersion)) {
/*     */                 
/* 158 */                 plugin.getLogger().warning("Your build of PacketEvents does not support the Minecraft version " + bukkitServerVersion + "! The latest Minecraft version supported by your build of PacketEvents is " + latestSupportedVersion + ". Please test the development builds, as they may already have support for your Minecraft version (hint: select the build that contains 'spigot'): https://ci.codemc.io/job/retrooper/job/packetevents/ If you're in need of any help, join our Discord server: https://discord.gg/DVHxPPxHZc");
/*     */ 
/*     */                 
/* 161 */                 Bukkit.getPluginManager().disablePlugin(plugin);
/*     */                 return;
/*     */               } 
/*     */             } 
/* 165 */             if (this.settings.shouldCheckForUpdates()) {
/* 166 */               getUpdateChecker().handleUpdateCheck();
/*     */             }
/*     */             
/* 169 */             Metrics metrics = new Metrics(plugin, 11327);
/*     */             
/* 171 */             metrics.addCustomChart((CustomChart)new SimplePie("packetevents_version", () -> getVersion().toStringWithoutSnapshot()));
/*     */             
/* 173 */             Bukkit.getPluginManager().registerEvents((Listener)new InternalGlobalBukkitListener(), plugin);
/*     */ 
/*     */             
/*     */             try {
/* 177 */               Class.forName("io.papermc.paper.connection.PlayerConnection");
/* 178 */               Bukkit.getPluginManager().registerEvents((Listener)new InternalPaperListener(plugin), plugin);
/* 179 */             } catch (ClassNotFoundException ignored) {
/* 180 */               if (this.serverManager.getVersion().isNewerThanOrEquals(ServerVersion.V_1_20_5)) {
/*     */                 
/* 182 */                 Bukkit.getPluginManager().registerEvents((Listener)new InternalBukkitLoginListener(), plugin);
/*     */               } else {
/* 184 */                 Bukkit.getPluginManager().registerEvents((Listener)new InternalBukkitListener(plugin), plugin);
/*     */               } 
/*     */             } 
/*     */             
/* 188 */             if (this.lateBind) {
/*     */               
/* 190 */               Runnable lateBindTask = () -> {
/*     */                   if (this.injector.isServerBound()) {
/*     */                     this.injector.inject();
/*     */                   }
/*     */                 };
/* 195 */               FoliaScheduler.runTaskOnInit(plugin, lateBindTask);
/*     */             } 
/*     */ 
/*     */             
/* 199 */             if (!"true".equalsIgnoreCase(System.getenv("PE_IGNORE_INCOMPATIBILITY"))) {
/* 200 */               checkCompatibility();
/*     */             }
/*     */ 
/*     */             
/* 204 */             for (Player player : Bukkit.getOnlinePlayers()) {
/* 205 */               User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
/* 206 */               SpigotChannelInjector injector = (SpigotChannelInjector)PacketEvents.getAPI().getInjector();
/* 207 */               injector.updatePlayer(user, player);
/*     */             } 
/*     */             
/* 210 */             this.initialized = true;
/*     */           } 
/*     */         }
/*     */ 
/*     */         
/*     */         private void checkCompatibility() {
/* 216 */           ViaVersionUtil.checkIfViaIsPresent();
/* 217 */           ProtocolSupportUtil.checkIfProtocolSupportIsPresent();
/*     */           
/* 219 */           Plugin viaPlugin = Bukkit.getPluginManager().getPlugin("ViaVersion");
/* 220 */           if (viaPlugin != null) {
/* 221 */             String[] ver = viaPlugin.getDescription().getVersion().split("\\.", 3);
/* 222 */             int major = Integer.parseInt(ver[0]);
/* 223 */             int minor = Integer.parseInt(ver[1]);
/* 224 */             if (major < 4 || (major == 4 && minor < 5)) {
/* 225 */               PacketEvents.getAPI().getLogManager().severe("You are attempting to combine 2.0 PacketEvents with a ViaVersion older than 4.5.0, please update your ViaVersion!");
/*     */               
/* 227 */               Plugin ourPlugin = getPlugin();
/* 228 */               Bukkit.getPluginManager().disablePlugin(ourPlugin);
/* 229 */               throw new IllegalStateException("ViaVersion incompatibility! Update to v4.5.0 or newer!");
/*     */             } 
/*     */           } 
/*     */           
/* 233 */           Plugin protocolLibPlugin = Bukkit.getPluginManager().getPlugin("ProtocolLib");
/* 234 */           if (protocolLibPlugin != null) {
/* 235 */             int majorVersion = Integer.parseInt(protocolLibPlugin.getDescription().getVersion().split("\\.", 2)[0]);
/* 236 */             if (majorVersion < 5) {
/* 237 */               PacketEvents.getAPI().getLogManager().severe("You are attempting to combine 2.0 PacketEvents with a ProtocolLib version older than v5.0.0. This is no longer works, please update to their dev builds. https://ci.dmulloy2.net/job/ProtocolLib/lastBuild/");
/*     */ 
/*     */ 
/*     */               
/* 241 */               Plugin ourPlugin = getPlugin();
/* 242 */               Bukkit.getPluginManager().disablePlugin(ourPlugin);
/* 243 */               throw new IllegalStateException("ProtocolLib incompatibility! Update to v5.0.0 or newer!");
/*     */             } 
/*     */           } 
/*     */         }
/*     */ 
/*     */         
/*     */         public boolean isInitialized() {
/* 250 */           return this.initialized;
/*     */         }
/*     */ 
/*     */         
/*     */         public void terminate() {
/* 255 */           if (this.initialized) {
/*     */             
/* 257 */             this.injector.uninject();
/* 258 */             for (User user : this.protocolManager.getUsers()) {
/* 259 */               ServerConnectionInitializer.destroyHandlers(user.getChannel());
/*     */             }
/*     */             
/* 262 */             getEventManager().unregisterAllListeners();
/* 263 */             this.initialized = false;
/* 264 */             this.terminated = true;
/*     */           } 
/*     */         }
/*     */ 
/*     */         
/*     */         public boolean isTerminated() {
/* 270 */           return this.terminated;
/*     */         }
/*     */ 
/*     */         
/*     */         public Plugin getPlugin() {
/* 275 */           return plugin;
/*     */         }
/*     */ 
/*     */         
/*     */         public ProtocolManager getProtocolManager() {
/* 280 */           return this.protocolManager;
/*     */         }
/*     */ 
/*     */         
/*     */         public ServerManager getServerManager() {
/* 285 */           return this.serverManager;
/*     */         }
/*     */ 
/*     */         
/*     */         public PlayerManager getPlayerManager() {
/* 290 */           return this.playerManager;
/*     */         }
/*     */ 
/*     */         
/*     */         public PacketEventsSettings getSettings() {
/* 295 */           return this.settings;
/*     */         }
/*     */ 
/*     */         
/*     */         public NettyManager getNettyManager() {
/* 300 */           return this.nettyManager;
/*     */         }
/*     */ 
/*     */         
/*     */         public ChannelInjector getInjector() {
/* 305 */           return (ChannelInjector)this.injector;
/*     */         }
/*     */ 
/*     */         
/*     */         public LogManager getLogManager() {
/* 310 */           return this.logManager;
/*     */         }
/*     */       };
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\factory\spigot\SpigotPacketEventsBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */