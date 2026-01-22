/*    */ package ac.grim.grimac.checks.impl.multiactions;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ 
/*    */ @CheckData(name = "MultiActionsD", description = "Closed inventory while moving", experimental = true)
/*    */ public class MultiActionsD extends Check implements PacketCheck {
/*    */   public MultiActionsD(GrimPlayer player) {
/* 13 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 18 */     if (event.getPacketType() == PacketType.Play.Client.CLOSE_WINDOW) {
/* 19 */       String verbose = MultiActionsC.getVerbose(this.player);
/* 20 */       if (!verbose.isEmpty() && flagAndAlert(verbose) && shouldModifyPackets()) {
/* 21 */         event.setCancelled(true);
/* 22 */         this.player.onPacketCancel();
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\multiactions\MultiActionsD.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */