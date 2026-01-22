/*    */ package ac.grim.grimac.checks.impl.packetorder;
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ import java.util.ArrayDeque;
/*    */ 
/*    */ @CheckData(name = "PacketOrderE", experimental = true)
/*    */ public class PacketOrderE extends Check implements PostPredictionCheck {
/*    */   private final ArrayDeque<String> flags;
/*    */   
/*    */   public PacketOrderE(GrimPlayer player) {
/* 16 */     super(player);
/*    */ 
/*    */     
/* 19 */     this.flags = new ArrayDeque<>();
/*    */   }
/*    */   private boolean setback;
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 24 */     if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE && (
/* 25 */       this.player.packetOrderProcessor.isAttacking() || this.player.packetOrderProcessor
/* 26 */       .isRightClicking() || this.player.packetOrderProcessor
/* 27 */       .isOpeningInventory() || this.player.packetOrderProcessor
/* 28 */       .isReleasing() || this.player.packetOrderProcessor
/* 29 */       .isSneaking() || this.player.packetOrderProcessor
/* 30 */       .isSprinting() || this.player.packetOrderProcessor
/* 31 */       .isLeavingBed() || this.player.packetOrderProcessor
/* 32 */       .isStartingToGlide() || this.player.packetOrderProcessor
/* 33 */       .isJumpingWithMount())) {
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 44 */       String verbose = "attacking=" + this.player.packetOrderProcessor.isAttacking() + ", rightClicking=" + this.player.packetOrderProcessor.isRightClicking() + ", openingInventory=" + this.player.packetOrderProcessor.isOpeningInventory() + ", releasing=" + this.player.packetOrderProcessor.isReleasing() + ", sneaking=" + this.player.packetOrderProcessor.isSneaking() + ", sprinting=" + this.player.packetOrderProcessor.isSprinting() + ", bed=" + this.player.packetOrderProcessor.isLeavingBed() + ", sprinting=" + this.player.packetOrderProcessor.isSprinting() + ", gliding=" + this.player.packetOrderProcessor.isStartingToGlide() + ", mountJumping=" + this.player.packetOrderProcessor.isJumpingWithMount();
/* 45 */       if (((this.player.canSkipTicks() && this.flags.add(verbose)) || flagAndAlert(verbose)) && 
/* 46 */         this.player.packetOrderProcessor.isUsing()) {
/* 47 */         this.setback = true;
/*    */       }
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 56 */     if (!this.player.canSkipTicks()) {
/* 57 */       if (this.setback) {
/* 58 */         this.setback = false;
/* 59 */         setbackIfAboveSetbackVL();
/*    */       } 
/*    */       
/*    */       return;
/*    */     } 
/* 64 */     if (this.player.isTickingReliablyFor(3)) {
/* 65 */       for (String verbose : this.flags) {
/* 66 */         if (flagAndAlert(verbose) && this.setback) {
/* 67 */           this.setback = false;
/* 68 */           setbackIfAboveSetbackVL();
/*    */         } 
/*    */       } 
/*    */     }
/*    */     
/* 73 */     this.setback = false;
/* 74 */     this.flags.clear();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\packetorder\PacketOrderE.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */