/*   */ package ac.grim.grimac.manager.init.start;
/*   */ 
/*   */ import ac.grim.grimac.GrimAPI;
/*   */ import ac.grim.grimac.player.GrimPlayer;
/*   */ 
/*   */ public class PacketLimiter
/*   */   implements StartableInitable {
/*   */   public void start() {
/* 9 */     GrimAPI.INSTANCE.getScheduler().getAsyncScheduler().runAtFixedRate(GrimAPI.INSTANCE.getGrimPlugin(), () -> { for (GrimPlayer player : GrimAPI.INSTANCE.getPlayerDataManager().getEntries()) player.cancelledPackets.set(0);  }1L, 20L);
/*   */   }
/*   */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\init\start\PacketLimiter.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */