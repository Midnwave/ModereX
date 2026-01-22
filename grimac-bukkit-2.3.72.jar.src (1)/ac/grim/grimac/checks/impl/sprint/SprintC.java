/*    */ package ac.grim.grimac.checks.impl.sprint;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ 
/*    */ @CheckData(name = "SprintC", description = "Sprinting while using an item", setback = 5.0D, experimental = true)
/*    */ public class SprintC extends Check implements PostPredictionCheck {
/*    */   private boolean flaggedLastTick = false;
/*    */   
/*    */   public SprintC(GrimPlayer player) {
/* 15 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 20 */     if (this.player.packetStateData.isSlowedByUsingItem()) {
/* 21 */       ClientVersion version = this.player.getClientVersion();
/*    */ 
/*    */       
/* 24 */       if (version.isNewerThanOrEquals(ClientVersion.V_1_14_2) && version != ClientVersion.V_1_21_4) {
/*    */         return;
/*    */       }
/*    */       
/* 28 */       if (this.player.isSprinting && (!this.player.wasTouchingWater || version.isOlderThan(ClientVersion.V_1_13))) {
/* 29 */         if (this.flaggedLastTick) flagAndAlertWithSetback(); 
/* 30 */         this.flaggedLastTick = true;
/*    */       } else {
/* 32 */         reward();
/* 33 */         this.flaggedLastTick = false;
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\sprint\SprintC.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */