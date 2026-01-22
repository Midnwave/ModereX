/*    */ package ac.grim.grimac.manager.tick.impl;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.manager.tick.Tickable;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ 
/*    */ public class TickInventory
/*    */   implements Tickable {
/*    */   public void tick() {
/* 10 */     for (GrimPlayer player : GrimAPI.INSTANCE.getPlayerDataManager().getEntries())
/* 11 */       player.inventory.inventory.getInventoryStorage().tickWithBukkit(); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\tick\impl\TickInventory.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */