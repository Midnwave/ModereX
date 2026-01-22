/*    */ package ac.grim.grimac.checks.impl.packetorder;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClientStatus;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ import java.util.ArrayDeque;
/*    */ 
/*    */ @CheckData(name = "PacketOrderK", experimental = true)
/*    */ public class PacketOrderK
/*    */   extends Check implements PostPredictionCheck {
/*    */   public PacketOrderK(GrimPlayer player) {
/* 17 */     super(player);
/*    */ 
/*    */     
/* 20 */     this.flags = new ArrayDeque<>();
/*    */   }
/*    */   private final ArrayDeque<String> flags;
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 24 */     if (event.getPacketType() == PacketType.Play.Client.CLIENT_STATUS && (
/* 25 */       new WrapperPlayClientClientStatus(event)).getAction() == WrapperPlayClientClientStatus.Action.OPEN_INVENTORY_ACHIEVEMENT && (
/* 26 */       this.player.packetOrderProcessor.isClickingInInventory() || this.player.packetOrderProcessor.isClosingInventory())) {
/* 27 */       String verbose = "open, clicking=" + this.player.packetOrderProcessor.isClickingInInventory() + ", closing=" + this.player.packetOrderProcessor.isClosingInventory();
/* 28 */       if (!this.player.canSkipTicks()) {
/* 29 */         flagAndAlert(verbose);
/*    */       } else {
/* 31 */         this.flags.add(verbose);
/*    */       } 
/*    */     } 
/*    */ 
/*    */ 
/*    */     
/* 37 */     if ((event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW || event.getPacketType() == PacketType.Play.Client.CLOSE_WINDOW) && 
/* 38 */       this.player.packetOrderProcessor.isOpeningInventory()) {
/* 39 */       String verbose = (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) ? "click" : "close";
/* 40 */       if (!this.player.canSkipTicks()) {
/* 41 */         if (flagAndAlert(verbose) && shouldModifyPackets()) {
/* 42 */           event.setCancelled(true);
/* 43 */           this.player.onPacketCancel();
/*    */         } 
/*    */       } else {
/* 46 */         this.flags.add(verbose);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 54 */     if (!this.player.canSkipTicks())
/*    */       return; 
/* 56 */     if (this.player.isTickingReliablyFor(3)) {
/* 57 */       for (String verbose : this.flags) {
/* 58 */         flagAndAlert(verbose);
/*    */       }
/*    */     }
/*    */     
/* 62 */     this.flags.clear();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\packetorder\PacketOrderK.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */