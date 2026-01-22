/*     */ package ac.grim.grimac.utils.anticheat;
/*     */ 
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.api.GrimUser;
/*     */ import ac.grim.grimac.api.event.GrimEvent;
/*     */ import ac.grim.grimac.api.event.events.GrimJoinEvent;
/*     */ import ac.grim.grimac.api.event.events.GrimQuitEvent;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.channel.ChannelHelper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*     */ import ac.grim.grimac.utils.reflection.GeyserUtil;
/*     */ import java.util.Collection;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ 
/*     */ public class PlayerDataManager
/*     */ {
/*  19 */   public final Collection<User> exemptUsers = ConcurrentHashMap.newKeySet();
/*  20 */   private final ConcurrentHashMap<User, GrimPlayer> playerDataMap = new ConcurrentHashMap<>();
/*     */ 
/*     */ 
/*     */   
/*     */   public GrimPlayer getPlayer(UUID uuid) {
/*  25 */     Object channel = PacketEvents.getAPI().getProtocolManager().getChannel(uuid);
/*  26 */     User user = PacketEvents.getAPI().getProtocolManager().getUser(channel);
/*  27 */     return getPlayer(user);
/*     */   }
/*     */ 
/*     */   
/*     */   public GrimPlayer getPlayer(User user) {
/*  32 */     GrimPlayer player = this.playerDataMap.get(user);
/*  33 */     if (player != null && player.platformPlayer != null && player.platformPlayer.isExternalPlayer())
/*  34 */       return null; 
/*  35 */     return player;
/*     */   }
/*     */   
/*     */   public boolean shouldCheck(User user) {
/*  39 */     if (this.exemptUsers.contains(user)) return false; 
/*  40 */     if (!ChannelHelper.isOpen(user.getChannel())) return false;
/*     */     
/*  42 */     if (user.getUUID() != null) {
/*     */       
/*  44 */       if (GeyserUtil.isBedrockPlayer(user.getUUID())) {
/*  45 */         this.exemptUsers.add(user);
/*  46 */         return false;
/*     */       } 
/*     */ 
/*     */       
/*  50 */       GrimPlayer grimPlayer = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(user);
/*  51 */       if (grimPlayer != null && grimPlayer.hasPermission("grim.exempt")) {
/*  52 */         this.exemptUsers.add(user);
/*  53 */         return false;
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/*  58 */       if (user.getUUID().toString().startsWith("00000000-0000-0000-0009")) {
/*  59 */         this.exemptUsers.add(user);
/*  60 */         return false;
/*     */       } 
/*     */     } 
/*     */     
/*  64 */     return true;
/*     */   }
/*     */   
/*     */   public void addUser(User user) {
/*  68 */     if (shouldCheck(user)) {
/*  69 */       GrimPlayer player = new GrimPlayer(user);
/*  70 */       this.playerDataMap.put(user, player);
/*  71 */       GrimAPI.INSTANCE.getEventBus().post((GrimEvent)new GrimJoinEvent((GrimUser)player));
/*     */     } 
/*     */   }
/*     */   
/*     */   public GrimPlayer remove(User user) {
/*  76 */     return this.playerDataMap.remove(user);
/*     */   }
/*     */   
/*     */   public void onDisconnect(User user) {
/*  80 */     GrimPlayer grimPlayer = remove(user);
/*  81 */     if (grimPlayer != null) GrimAPI.INSTANCE.getEventBus().post((GrimEvent)new GrimQuitEvent((GrimUser)grimPlayer)); 
/*  82 */     this.exemptUsers.remove(user);
/*     */     
/*  84 */     UUID uuid = user.getProfile().getUUID();
/*     */ 
/*     */     
/*  87 */     if (uuid == null) {
/*     */       return;
/*     */     }
/*  90 */     GrimAPI.INSTANCE.getAlertManager().handlePlayerQuit(GrimAPI.INSTANCE
/*  91 */         .getPlatformPlayerFactory().getFromUUID(uuid));
/*     */ 
/*     */     
/*  94 */     GrimAPI.INSTANCE.getSpectateManager().onQuit(uuid);
/*     */ 
/*     */     
/*  97 */     GrimAPI.INSTANCE.getPlatformPlayerFactory().invalidatePlayer(uuid);
/*     */   }
/*     */   
/*     */   public Collection<GrimPlayer> getEntries() {
/* 101 */     return this.playerDataMap.values();
/*     */   }
/*     */   
/*     */   public int size() {
/* 105 */     return this.playerDataMap.size();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\anticheat\PlayerDataManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */