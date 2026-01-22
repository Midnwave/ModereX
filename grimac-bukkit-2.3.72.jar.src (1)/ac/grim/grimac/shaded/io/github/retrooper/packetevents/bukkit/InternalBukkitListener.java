/*     */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.bukkit;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEventsAPI;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.channel.ChannelHelper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.FakeChannelUtil;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.injector.SpigotChannelInjector;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.manager.player.PlayerManagerImpl;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.folia.FoliaScheduler;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.Map;
/*     */ import java.util.UUID;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.player.PlayerJoinEvent;
/*     */ import org.bukkit.event.player.PlayerLoginEvent;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.jspecify.annotations.NullMarked;
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
/*     */ @NullMarked
/*     */ @Internal
/*     */ public class InternalBukkitListener
/*     */   implements Listener
/*     */ {
/*     */   static final String KICK_MESSAGE = "PacketEvents failed to inject into a channel";
/*     */   private final Plugin plugin;
/*     */   
/*     */   public InternalBukkitListener(Plugin plugin) {
/*  55 */     this.plugin = plugin;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @EventHandler(priority = EventPriority.MONITOR)
/*     */   public void onLogin(PlayerLoginEvent event) {
/*  62 */     if (event.getResult() == PlayerLoginEvent.Result.ALLOWED) {
/*  63 */       onPreJoin(event.getPlayer());
/*     */     }
/*     */   }
/*     */   
/*     */   @EventHandler(priority = EventPriority.LOWEST)
/*     */   public void onJoin(PlayerJoinEvent event) {
/*  69 */     onPostJoin(event.getPlayer());
/*     */   }
/*     */ 
/*     */   
/*     */   void onPreJoin(Player player) {
/*  74 */     PacketEventsAPI<?> api = PacketEvents.getAPI();
/*  75 */     Map<UUID, WeakReference<Player>> map = ((PlayerManagerImpl)api.getPlayerManager()).joiningPlayers;
/*  76 */     map.put(player.getUniqueId(), new WeakReference<>(player));
/*     */   }
/*     */   
/*     */   void onPostJoin(Player player) {
/*  80 */     PacketEventsAPI<?> api = PacketEvents.getAPI();
/*  81 */     User user = api.getPlayerManager().getUser(player);
/*  82 */     if (user != null) {
/*     */       
/*  84 */       SpigotChannelInjector injector = (SpigotChannelInjector)PacketEvents.getAPI().getInjector();
/*  85 */       injector.setPlayer(user.getChannel(), player);
/*     */       
/*  87 */       ((PlayerManagerImpl)api.getPlayerManager()).joiningPlayers.remove(player.getUniqueId());
/*     */ 
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/*  95 */     ((PlayerManagerImpl)api.getPlayerManager()).joiningPlayers.remove(player.getUniqueId());
/*     */     
/*  97 */     Object channel = api.getPlayerManager().getChannel(player);
/*  98 */     if ((channel != null && FakeChannelUtil.isFakeChannel(channel)) || (api
/*  99 */       .isTerminated() && !api.getSettings().isKickIfTerminated())) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 105 */     FoliaScheduler.getEntityScheduler().runDelayed((Entity)player, this.plugin, __ -> { if ((channel != null) ? ChannelHelper.isOpen(channel) : player.isOnline()) player.kickPlayer("PacketEvents failed to inject into a channel");  }null, 0L);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\bukkit\InternalBukkitListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */