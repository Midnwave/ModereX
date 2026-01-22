/*    */ package ac.grim.grimac.checks.impl.sprint;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ import ac.grim.grimac.utils.enums.Pose;
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
/*    */ 
/*    */ @CheckData(name = "SprintB", description = "Sprinting while sneaking or crawling", setback = 5.0D, experimental = true)
/*    */ public class SprintB extends Check implements PostPredictionCheck {
/*    */   public SprintB(GrimPlayer player) {
/* 16 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 21 */     if (this.player.isSlowMovement && this.player.sneakingSpeedMultiplier < 0.8F && predictionComplete.isChecked()) {
/* 22 */       ClientVersion version = this.player.getClientVersion();
/*    */ 
/*    */       
/* 25 */       if (version.isNewerThanOrEquals(ClientVersion.V_1_14_2) && version != ClientVersion.V_1_21_4) {
/*    */         return;
/*    */       }
/*    */ 
/*    */       
/* 30 */       if (version.isNewerThanOrEquals(ClientVersion.V_1_14) && this.player.wasFlying && this.player.lastPose == Pose.FALL_FLYING && !this.player.isGliding) {
/*    */         return;
/*    */       }
/*    */ 
/*    */       
/* 35 */       if (version == ClientVersion.V_1_21_4 && (((Double)Collections.<Double>max((Collection<? extends Double>)this.player.uncertaintyHandler.pistonX)).doubleValue() != 0.0D || (
/* 36 */         (Double)Collections.<Double>max((Collection<? extends Double>)this.player.uncertaintyHandler.pistonY)).doubleValue() != 0.0D || (
/* 37 */         (Double)Collections.<Double>max((Collection<? extends Double>)this.player.uncertaintyHandler.pistonZ)).doubleValue() != 0.0D)) {
/*    */         return;
/*    */       }
/*    */       
/* 41 */       if (this.player.isSprinting && (!this.player.wasTouchingWater || version.isOlderThan(ClientVersion.V_1_13)))
/* 42 */       { flagAndAlertWithSetback(); }
/* 43 */       else { reward(); }
/*    */     
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\sprint\SprintB.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */