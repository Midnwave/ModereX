/*    */ package ac.grim.grimac.checks.impl.packetorder;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.DiggingAction;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClientStatus;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ import java.util.ArrayDeque;
/*    */ 
/*    */ @CheckData(name = "PacketOrderG", experimental = true)
/*    */ public class PacketOrderG
/*    */   extends Check implements PostPredictionCheck {
/*    */   public PacketOrderG(GrimPlayer player) {
/* 19 */     super(player);
/*    */ 
/*    */     
/* 22 */     this.flags = new ArrayDeque<>();
/*    */   }
/*    */   private final ArrayDeque<String> flags;
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 26 */     if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING || (event.getPacketType() == PacketType.Play.Client.CLIENT_STATUS && (new WrapperPlayClientClientStatus(event))
/* 27 */       .getAction() == WrapperPlayClientClientStatus.Action.OPEN_INVENTORY_ACHIEVEMENT)) {
/* 28 */       DiggingAction action = null;
/* 29 */       if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
/* 30 */         action = (new WrapperPlayClientPlayerDigging(event)).getAction();
/* 31 */         if (action == DiggingAction.RELEASE_USE_ITEM || action == DiggingAction.START_DIGGING || action == DiggingAction.CANCELLED_DIGGING || action == DiggingAction.FINISHED_DIGGING) {
/*    */           return;
/*    */         }
/*    */       } 
/*    */ 
/*    */ 
/*    */       
/* 38 */       if (this.player.packetOrderProcessor.isAttacking() || this.player.packetOrderProcessor
/* 39 */         .isReleasing() || this.player.packetOrderProcessor
/* 40 */         .isRightClicking() || this.player.packetOrderProcessor
/* 41 */         .isPicking() || this.player.packetOrderProcessor
/* 42 */         .isDigging()) {
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */         
/* 49 */         String verbose = "action=" + ((action == null) ? "openInventory" : ((action == DiggingAction.SWAP_ITEM_WITH_OFFHAND) ? "swap" : "drop")) + ", attacking=" + this.player.packetOrderProcessor.isAttacking() + ", releasing=" + this.player.packetOrderProcessor.isReleasing() + ", rightClicking=" + this.player.packetOrderProcessor.isRightClicking() + ", picking=" + this.player.packetOrderProcessor.isPicking() + ", digging=" + this.player.packetOrderProcessor.isDigging();
/* 50 */         if (!this.player.canSkipTicks()) {
/* 51 */           if (flagAndAlert(verbose) && shouldModifyPackets()) {
/* 52 */             event.setCancelled(true);
/* 53 */             this.player.onPacketCancel();
/*    */           } 
/*    */         } else {
/* 56 */           this.flags.add(verbose);
/*    */         } 
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 64 */     if (!this.player.canSkipTicks())
/*    */       return; 
/* 66 */     if (this.player.isTickingReliablyFor(3)) {
/* 67 */       for (String verbose : this.flags) {
/* 68 */         flagAndAlert(verbose);
/*    */       }
/*    */     }
/*    */     
/* 72 */     this.flags.clear();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\packetorder\PacketOrderG.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */