/*    */ package ac.grim.grimac.predictionengine.predictions.rideable;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.predictionengine.predictions.PredictionEngine;
/*    */ import ac.grim.grimac.predictionengine.predictions.PredictionEngineNormal;
/*    */ import ac.grim.grimac.utils.data.VectorData;
/*    */ import ac.grim.grimac.utils.math.Vector3dm;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class PredictionEngineHappyGhast extends PredictionEngineNormal {
/*    */   @Generated
/*    */   public PredictionEngineHappyGhast(Vector3dm movementVector, double multiplier) {
/* 12 */     this.movementVector = movementVector; this.multiplier = multiplier;
/*    */   }
/*    */   
/*    */   private final Vector3dm movementVector;
/*    */   private final double multiplier;
/*    */   
/*    */   public void endOfTick(GrimPlayer player, double delta) {
/* 19 */     for (VectorData vector : player.getPossibleVelocitiesMinusKnockback()) {
/* 20 */       vector.vector.setX(vector.vector.getX() * this.multiplier);
/* 21 */       vector.vector.setY(vector.vector.getY() * this.multiplier);
/* 22 */       vector.vector.setZ(vector.vector.getZ() * this.multiplier);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public List<VectorData> applyInputsToVelocityPossibilities(GrimPlayer player, Set<VectorData> possibleVectors, float speed) {
/* 28 */     return PredictionEngineRideableUtils.applyInputsToVelocityPossibilities((PredictionEngine)this, this.movementVector, player, possibleVectors, speed);
/*    */   }
/*    */ 
/*    */   
/*    */   public Vector3dm getMovementResultFromInput(GrimPlayer player, Vector3dm inputVector, float flyingSpeed, float yRot) {
/* 33 */     float sin = player.trigHandler.sin(yRot * 0.017453292F);
/* 34 */     float cos = player.trigHandler.cos(yRot * 0.017453292F);
/*    */     
/* 36 */     double xResult = inputVector.getX() * cos - inputVector.getZ() * sin;
/* 37 */     double zResult = inputVector.getZ() * cos + inputVector.getX() * sin;
/*    */     
/* 39 */     return new Vector3dm(xResult * flyingSpeed, inputVector.getY() * flyingSpeed, zResult * flyingSpeed);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\predictionengine\predictions\rideable\PredictionEngineHappyGhast.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */