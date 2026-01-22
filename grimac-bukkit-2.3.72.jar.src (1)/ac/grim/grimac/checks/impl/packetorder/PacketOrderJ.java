/*    */ package ac.grim.grimac.checks.impl.packetorder;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ 
/*    */ @CheckData(name = "PacketOrderJ", experimental = true)
/*    */ public class PacketOrderJ extends Check implements PostPredictionCheck {
/*    */   public PacketOrderJ(GrimPlayer player) {
/* 14 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   private int invalid;
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 21 */     if ((event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT || event.getPacketType() == PacketType.Play.Client.USE_ITEM) && 
/* 22 */       this.player.packetOrderProcessor.isAttacking() && !this.player.packetOrderProcessor.isInteracting()) {
/* 23 */       if (!this.player.canSkipTicks()) {
/* 24 */         if (flagAndAlert() && shouldModifyPackets()) {
/* 25 */           event.setCancelled(true);
/* 26 */           this.player.onPacketCancel();
/*    */         } 
/*    */       } else {
/* 29 */         this.invalid++;
/*    */       } 
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 37 */     if (!this.player.canSkipTicks())
/*    */       return; 
/* 39 */     if (this.player.isTickingReliablyFor(3)) {
/* 40 */       for (; this.invalid >= 1; this.invalid--) {
/* 41 */         flagAndAlert();
/*    */       }
/*    */     }
/*    */     
/* 45 */     this.invalid = 0;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\packetorder\PacketOrderJ.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */