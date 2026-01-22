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
/*    */ @CheckData(name = "PacketOrderL", experimental = true)
/*    */ public class PacketOrderL
/*    */   extends Check implements PostPredictionCheck {
/*    */   public PacketOrderL(GrimPlayer player) {
/* 19 */     super(player);
/*    */ 
/*    */     
/* 22 */     this.flags = new ArrayDeque<>();
/*    */   }
/*    */   private final ArrayDeque<String> flags;
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 26 */     if (event.getPacketType() == PacketType.Play.Client.CLIENT_STATUS && (
/* 27 */       new WrapperPlayClientClientStatus(event)).getAction() == WrapperPlayClientClientStatus.Action.OPEN_INVENTORY_ACHIEVEMENT && 
/* 28 */       this.player.packetOrderProcessor.isDropping()) {
/* 29 */       if (!this.player.canSkipTicks()) {
/* 30 */         if (flagAndAlert("inventory") && shouldModifyPackets()) {
/* 31 */           event.setCancelled(true);
/* 32 */           this.player.onPacketCancel();
/*    */         } 
/*    */       } else {
/* 35 */         this.flags.add("inventory");
/*    */       } 
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 41 */     if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING && (
/* 42 */       new WrapperPlayClientPlayerDigging(event)).getAction() == DiggingAction.SWAP_ITEM_WITH_OFFHAND && 
/* 43 */       this.player.packetOrderProcessor.isDropping()) {
/* 44 */       if (!this.player.canSkipTicks()) {
/* 45 */         if (flagAndAlert("swap") && shouldModifyPackets()) {
/* 46 */           event.setCancelled(true);
/* 47 */           this.player.onPacketCancel();
/*    */         } 
/*    */       } else {
/* 50 */         this.flags.add("swap");
/*    */       } 
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 59 */     if (!this.player.canSkipTicks())
/*    */       return; 
/* 61 */     if (this.player.isTickingReliablyFor(3)) {
/* 62 */       for (String verbose : this.flags) {
/* 63 */         flagAndAlert(verbose);
/*    */       }
/*    */     }
/*    */     
/* 67 */     this.flags.clear();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\packetorder\PacketOrderL.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */