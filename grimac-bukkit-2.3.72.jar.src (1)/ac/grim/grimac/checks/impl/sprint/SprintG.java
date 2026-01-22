/*    */ package ac.grim.grimac.checks.impl.sprint;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ 
/*    */ @CheckData(name = "SprintG", description = "Sprinting while in water", experimental = true)
/*    */ public class SprintG extends Check implements PostPredictionCheck {
/*    */   public SprintG(GrimPlayer player) {
/* 13 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 18 */     if (this.player.wasTouchingWater && (this.player.wasWasTouchingWater || this.player.getClientVersion() == ClientVersion.V_1_21_4) && !this.player.wasEyeInWater && this.player
/* 19 */       .getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_13) && this.player.wasLastPredictionCompleteChecked && predictionComplete
/* 20 */       .isChecked())
/* 21 */       if (this.player.isSprinting && !this.player.isSwimming) {
/* 22 */         flagAndAlertWithSetback();
/*    */       } else {
/* 24 */         reward();
/*    */       }  
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\sprint\SprintG.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */