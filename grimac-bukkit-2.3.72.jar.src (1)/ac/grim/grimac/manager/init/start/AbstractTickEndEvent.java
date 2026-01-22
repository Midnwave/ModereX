/*    */ package ac.grim.grimac.manager.init.start;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class AbstractTickEndEvent
/*    */   implements StartableInitable
/*    */ {
/*    */   public void start() {}
/*    */   
/*    */   protected void onEndOfTick(GrimPlayer player) {
/* 15 */     player.checkManager.getEntityReplication().onEndOfTickEvent();
/*    */   }
/*    */   
/*    */   protected boolean shouldInjectEndTick() {
/* 19 */     return GrimAPI.INSTANCE.getConfigManager().getConfig().getBooleanElse("Reach.enable-post-packet", false);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\init\start\AbstractTickEndEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */