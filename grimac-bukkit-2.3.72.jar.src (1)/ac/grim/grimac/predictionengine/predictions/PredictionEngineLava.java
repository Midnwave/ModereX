/*    */ package ac.grim.grimac.predictionengine.predictions;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.utils.data.VectorData;
/*    */ import ac.grim.grimac.utils.math.GrimMath;
/*    */ import ac.grim.grimac.utils.math.Vector3dm;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class PredictionEngineLava
/*    */   extends PredictionEngine
/*    */ {
/*    */   public void addJumpsToPossibilities(GrimPlayer player, Set<VectorData> existingVelocities) {
/* 14 */     for (VectorData vector : new HashSet(existingVelocities)) {
/* 15 */       if (player.couldSkipTick && vector.isZeroPointZeroThree()) {
/* 16 */         double extraVelFromVertTickSkipUpwards = GrimMath.clamp(player.actualMovement.getY(), vector.vector.clone().getY(), vector.vector.clone().getY() + 0.05000000074505806D);
/* 17 */         existingVelocities.add(new VectorData(vector.vector.clone().setY(extraVelFromVertTickSkipUpwards), vector, VectorData.VectorType.Jump));
/*    */       } else {
/* 19 */         existingVelocities.add(new VectorData(vector.vector.clone().add(new Vector3dm(0.0F, 0.04F, 0.0F)), vector, VectorData.VectorType.Jump));
/*    */       } 
/*    */       
/* 22 */       if (player.slightlyTouchingLava && player.lastOnGround && !player.onGround) {
/* 23 */         Vector3dm withJump = vector.vector.clone();
/* 24 */         doJump(player, withJump);
/* 25 */         existingVelocities.add(new VectorData(withJump, vector, VectorData.VectorType.Jump));
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\predictionengine\predictions\PredictionEngineLava.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */