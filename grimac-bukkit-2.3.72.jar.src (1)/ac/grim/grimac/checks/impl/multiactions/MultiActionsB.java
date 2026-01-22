/*    */ package ac.grim.grimac.checks.impl.multiactions;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.BlockBreakCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.InteractionHand;
/*    */ import ac.grim.grimac.utils.anticheat.update.BlockBreak;
/*    */ 
/*    */ @CheckData(name = "MultiActionsB", description = "Breaking blocks while using an item", experimental = true)
/*    */ public class MultiActionsB extends Check implements BlockBreakCheck {
/*    */   public MultiActionsB(GrimPlayer player) {
/* 14 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onBlockBreak(BlockBreak blockBreak) {
/* 19 */     if (this.player.packetStateData.isSlowedByUsingItem() && (this.player.packetStateData.lastSlotSelected == this.player.packetStateData.getSlowedByUsingItemSlot() || this.player.packetStateData.itemInUseHand == InteractionHand.OFF_HAND)) {
/*    */       
/* 21 */       if (this.player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_7_10)) {
/*    */         return;
/*    */       }
/*    */       
/* 25 */       if (flagAndAlert() && shouldModifyPackets())
/* 26 */         blockBreak.cancel(); 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\multiactions\MultiActionsB.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */