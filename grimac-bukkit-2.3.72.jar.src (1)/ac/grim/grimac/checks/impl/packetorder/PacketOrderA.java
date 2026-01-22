/*    */ package ac.grim.grimac.checks.impl.packetorder;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ 
/*    */ @CheckData(name = "PacketOrderA", experimental = true)
/*    */ public class PacketOrderA
/*    */   extends Check implements PostPredictionCheck {
/*    */   public PacketOrderA(GrimPlayer player) {
/* 16 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   private int invalid;
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 23 */     if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
/* 24 */       WrapperPlayClientClickWindow.WindowClickType clickType = (new WrapperPlayClientClickWindow(event)).getWindowClickType();
/*    */       
/* 26 */       if (((clickType == WrapperPlayClientClickWindow.WindowClickType.PICKUP || clickType == WrapperPlayClientClickWindow.WindowClickType.PICKUP_ALL) && this.player.packetOrderProcessor.isQuickMoveClicking()) || (clickType == WrapperPlayClientClickWindow.WindowClickType.QUICK_MOVE && this.player.packetOrderProcessor
/* 27 */         .isPickUpClicking())) {
/* 28 */         if (!this.player.canSkipTicks()) {
/* 29 */           if (flagAndAlert() && shouldModifyPackets()) {
/* 30 */             event.setCancelled(true);
/* 31 */             this.player.onPacketCancel();
/*    */           } 
/*    */         } else {
/* 34 */           this.invalid++;
/*    */         } 
/*    */       }
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 42 */     if (!this.player.canSkipTicks())
/*    */       return; 
/* 44 */     if (this.player.isTickingReliablyFor(3)) {
/* 45 */       for (; this.invalid >= 1; this.invalid--) {
/* 46 */         flagAndAlert();
/*    */       }
/*    */     }
/*    */     
/* 50 */     this.invalid = 0;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\packetorder\PacketOrderA.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */