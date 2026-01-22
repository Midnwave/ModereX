/*     */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.manager.player;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.player.PlayerManager;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.protocol.ProtocolManager;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.channel.ChannelHelper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.PlayerPingAccessorModern;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.SpigotReflectionUtil;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.protocolsupport.ProtocolSupportUtil;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.viaversion.ViaVersionUtil;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.Map;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.bukkit.entity.Player;
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
/*     */ public class PlayerManagerImpl
/*     */   implements PlayerManager
/*     */ {
/*     */   @Internal
/*  52 */   public final Map<UUID, WeakReference<Player>> joiningPlayers = new ConcurrentHashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPing(@NotNull Object player) {
/*  58 */     if (SpigotReflectionUtil.V_1_17_OR_HIGHER) {
/*  59 */       return PlayerPingAccessorModern.getPing((Player)player);
/*     */     }
/*  61 */     return SpigotReflectionUtil.getPlayerPingLegacy((Player)player);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public ClientVersion getClientVersion(@NotNull Object p) {
/*  67 */     Player player = (Player)p;
/*  68 */     User user = getUser(player);
/*  69 */     if (user == null) return ClientVersion.UNKNOWN; 
/*  70 */     if (user.getClientVersion() == null) {
/*     */       int protocolVersion;
/*  72 */       if (ProtocolSupportUtil.isAvailable()) {
/*  73 */         protocolVersion = ProtocolSupportUtil.getProtocolVersion(user.getAddress());
/*  74 */         PacketEvents.getAPI().getLogManager().debug("Requested ProtocolSupport for user " + user.getName() + "'s protocol version. Protocol version: " + protocolVersion);
/*  75 */       } else if (ViaVersionUtil.isAvailable()) {
/*  76 */         protocolVersion = ViaVersionUtil.getProtocolVersion(player);
/*  77 */         PacketEvents.getAPI().getLogManager().debug("Requested ViaVersion for " + player.getName() + "'s protocol version. Protocol version: " + protocolVersion);
/*     */       }
/*     */       else {
/*     */         
/*  81 */         protocolVersion = PacketEvents.getAPI().getServerManager().getVersion().getProtocolVersion();
/*  82 */         PacketEvents.getAPI().getLogManager().debug("No protocol translation plugins are available. We will assume " + user.getName() + "'s protocol version is the same as the server's protocol version. Protocol version: " + protocolVersion);
/*     */       } 
/*  84 */       ClientVersion version = ClientVersion.getById(protocolVersion);
/*  85 */       user.setClientVersion(version);
/*     */     } 
/*  87 */     return user.getClientVersion();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getChannel(@NotNull Object player) {
/*  92 */     UUID uuid = ((Player)player).getUniqueId();
/*  93 */     ProtocolManager protocolManager = PacketEvents.getAPI().getProtocolManager();
/*  94 */     Object channel = protocolManager.getChannel(uuid);
/*  95 */     if (channel == null) {
/*  96 */       channel = SpigotReflectionUtil.getChannel((Player)player);
/*     */ 
/*     */       
/*  99 */       if (channel != null) {
/* 100 */         synchronized (channel) {
/* 101 */           if (ChannelHelper.isOpen(channel)) {
/* 102 */             protocolManager.setChannel(uuid, channel);
/*     */           }
/*     */         } 
/*     */       }
/*     */     } 
/* 107 */     return channel;
/*     */   }
/*     */ 
/*     */   
/*     */   public User getUser(@NotNull Object player) {
/* 112 */     Player p = (Player)player;
/* 113 */     Object channel = getChannel(p);
/*     */     
/* 115 */     if (channel == null) return null; 
/* 116 */     return PacketEvents.getAPI().getProtocolManager().getUser(channel);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\manager\player\PlayerManagerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */