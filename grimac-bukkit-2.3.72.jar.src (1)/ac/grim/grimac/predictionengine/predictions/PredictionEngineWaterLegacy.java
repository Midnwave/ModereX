/*    */ package ac.grim.grimac.predictionengine.predictions;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.utils.data.VectorData;
/*    */ import ac.grim.grimac.utils.math.Vector3dm;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class PredictionEngineWaterLegacy
/*    */   extends PredictionEngine {
/*    */   private float swimmingSpeed;
/*    */   private float swimmingFriction;
/*    */   
/*    */   public void guessBestMovement(float swimmingSpeed, GrimPlayer player, float swimmingFriction) {
/* 15 */     this.swimmingSpeed = swimmingSpeed;
/* 16 */     this.swimmingFriction = swimmingFriction;
/* 17 */     guessBestMovement(swimmingSpeed, player);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Vector3dm getMovementResultFromInput(GrimPlayer player, Vector3dm inputVector, float f, float f2) {
/* 23 */     float lengthSquared = (float)inputVector.lengthSquared();
/*    */     
/* 25 */     if (lengthSquared >= 1.0E-4F) {
/* 26 */       lengthSquared = (float)Math.sqrt(lengthSquared);
/*    */       
/* 28 */       if (lengthSquared < 1.0F) {
/* 29 */         lengthSquared = 1.0F;
/*    */       }
/*    */       
/* 32 */       lengthSquared = this.swimmingSpeed / lengthSquared;
/* 33 */       inputVector.multiply(lengthSquared);
/* 34 */       float sinResult = player.trigHandler.sin(player.xRot * 0.017453292F);
/* 35 */       float cosResult = player.trigHandler.cos(player.xRot * 0.017453292F);
/*    */       
/* 37 */       return new Vector3dm(inputVector.getX() * cosResult - inputVector.getZ() * sinResult, inputVector
/* 38 */           .getY(), inputVector.getZ() * cosResult + inputVector.getX() * sinResult);
/*    */     } 
/*    */     
/* 41 */     return new Vector3dm();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void addJumpsToPossibilities(GrimPlayer player, Set<VectorData> existingVelocities) {
/* 47 */     for (VectorData vector : new HashSet(existingVelocities)) {
/* 48 */       existingVelocities.add(new VectorData(vector.vector.clone().add(new Vector3dm(0.0F, 0.04F, 0.0F)), vector, VectorData.VectorType.Jump));
/*    */       
/* 50 */       if (player.skippedTickInActualMovement) {
/* 51 */         existingVelocities.add(new VectorData(vector.vector.clone().add(new Vector3dm(0.0F, 0.02F, 0.0F)), vector, VectorData.VectorType.Jump));
/*    */       }
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void endOfTick(GrimPlayer player, double playerGravity) {
/* 58 */     super.endOfTick(player, playerGravity);
/*    */     
/* 60 */     for (VectorData vector : player.getPossibleVelocitiesMinusKnockback()) {
/* 61 */       vector.vector.multiply(new Vector3dm(this.swimmingFriction, 0.8F, this.swimmingFriction));
/*    */ 
/*    */       
/* 64 */       vector.vector.setY(vector.vector.getY() - 0.02D);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\predictionengine\predictions\PredictionEngineWaterLegacy.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */