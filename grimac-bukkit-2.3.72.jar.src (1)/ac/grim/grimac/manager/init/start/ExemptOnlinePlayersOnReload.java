/*    */ package ac.grim.grimac.manager.init.start;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.platform.api.player.PlatformPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ExemptOnlinePlayersOnReload
/*    */   implements StartableInitable
/*    */ {
/*    */   public void start() {
/* 14 */     for (PlatformPlayer player : GrimAPI.INSTANCE.getPlatformPlayerFactory().getOnlinePlayers()) {
/* 15 */       User user = PacketEvents.getAPI().getPlayerManager().getUser(player.getNative());
/* 16 */       (GrimAPI.INSTANCE.getPlayerDataManager()).exemptUsers.add(user);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\init\start\ExemptOnlinePlayersOnReload.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */