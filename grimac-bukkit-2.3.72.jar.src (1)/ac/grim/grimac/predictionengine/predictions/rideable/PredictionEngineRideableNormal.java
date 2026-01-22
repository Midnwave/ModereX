/*    */ package ac.grim.grimac.predictionengine.predictions.rideable;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.predictionengine.predictions.PredictionEngineNormal;
/*    */ import ac.grim.grimac.utils.data.VectorData;
/*    */ import ac.grim.grimac.utils.math.Vector3dm;
/*    */ import java.util.Set;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public class PredictionEngineRideableNormal extends PredictionEngineNormal {
/*    */   @Generated
/*    */   public PredictionEngineRideableNormal(Vector3dm movementVector) {
/* 12 */     this.movementVector = movementVector;
/*    */   }
/*    */   
/*    */   private final Vector3dm movementVector;
/*    */   
/*    */   public void addJumpsToPossibilities(GrimPlayer player, Set<VectorData> existingVelocities) {
/* 18 */     PredictionEngineRideableUtils.handleJumps(player, existingVelocities);
/*    */   }
/*    */ 
/*    */   
/*    */   public List<VectorData> applyInputsToVelocityPossibilities(GrimPlayer player, Set<VectorData> possibleVectors, float speed) {
/* 23 */     return PredictionEngineRideableUtils.applyInputsToVelocityPossibilities(this.movementVector, player, possibleVectors, speed);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\predictionengine\predictions\rideable\PredictionEngineRideableNormal.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */