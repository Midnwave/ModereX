/*    */ package ac.grim.grimac.manager.tick.impl;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.manager.tick.Tickable;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ 
/*    */ public class ClearRecentlyUpdatedBlocks
/*    */   implements Tickable
/*    */ {
/*    */   private static final int maxTickAge = 2;
/*    */   
/*    */   public void tick() {
/* 13 */     for (GrimPlayer player : GrimAPI.INSTANCE.getPlayerDataManager().getEntries())
/* 14 */       player.blockHistory.cleanup((GrimAPI.INSTANCE.getTickManager()).currentTick - 2); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\tick\impl\ClearRecentlyUpdatedBlocks.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */