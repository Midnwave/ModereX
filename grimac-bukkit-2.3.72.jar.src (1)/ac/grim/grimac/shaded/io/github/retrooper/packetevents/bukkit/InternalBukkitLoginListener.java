/*    */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.bukkit;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEventsAPI;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.FakeChannelUtil;
/*    */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.injector.SpigotChannelInjector;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.EventPriority;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.player.PlayerLoginEvent;
/*    */ import org.jspecify.annotations.NullMarked;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @NullMarked
/*    */ @Internal
/*    */ public class InternalBukkitLoginListener
/*    */   implements Listener
/*    */ {
/*    */   @EventHandler(priority = EventPriority.LOWEST)
/*    */   public void onLogin(PlayerLoginEvent event) {
/* 44 */     PacketEventsAPI<?> api = PacketEvents.getAPI();
/* 45 */     User user = api.getPlayerManager().getUser(event.getPlayer());
/* 46 */     if (user != null) {
/*    */       
/* 48 */       SpigotChannelInjector injector = (SpigotChannelInjector)api.getInjector();
/* 49 */       injector.updatePlayer(user, event.getPlayer());
/*    */       return;
/*    */     } 
/* 52 */     Object channel = api.getPlayerManager().getChannel(event.getPlayer());
/* 53 */     if ((channel != null && FakeChannelUtil.isFakeChannel(channel)) || (api
/* 54 */       .isTerminated() && !api.getSettings().isKickIfTerminated())) {
/*    */       return;
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 60 */     event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "PacketEvents failed to inject into a channel");
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\bukkit\InternalBukkitLoginListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */