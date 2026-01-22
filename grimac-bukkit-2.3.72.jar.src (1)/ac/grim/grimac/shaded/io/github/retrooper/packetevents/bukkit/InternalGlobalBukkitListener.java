/*    */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.bukkit;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEventsAPI;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.UserLoginEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.player.PlayerJoinEvent;
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
/*    */ public class InternalGlobalBukkitListener
/*    */   implements Listener
/*    */ {
/*    */   @EventHandler
/*    */   public void onPlayerJoin(PlayerJoinEvent event) {
/* 32 */     PacketEventsAPI<?> api = PacketEvents.getAPI();
/* 33 */     User user = api.getPlayerManager().getUser(event.getPlayer());
/* 34 */     PacketEvents.getAPI().getEventManager().callEvent((PacketEvent)new UserLoginEvent(user, event.getPlayer()));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\bukkit\InternalGlobalBukkitListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */