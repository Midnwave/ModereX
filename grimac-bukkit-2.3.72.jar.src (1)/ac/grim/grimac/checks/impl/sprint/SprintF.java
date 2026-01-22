/*    */ package ac.grim.grimac.checks.impl.sprint;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ 
/*    */ @CheckData(name = "SprintF", description = "Sprinting while gliding", experimental = true)
/*    */ public class SprintF extends Check implements PostPredictionCheck {
/*    */   public SprintF(GrimPlayer player) {
/* 13 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 18 */     if (this.player.wasGliding && this.player.isGliding && this.player.getClientVersion() == ClientVersion.V_1_21_4)
/* 19 */       if (this.player.isSprinting) {
/* 20 */         flagAndAlertWithSetback();
/*    */       } else {
/* 22 */         reward();
/*    */       }  
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\sprint\SprintF.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */