/*    */ package ac.grim.grimac.predictionengine.predictions;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.utils.data.VectorData;
/*    */ import ac.grim.grimac.utils.math.Vector3dm;
/*    */ import ac.grim.grimac.utils.nmsutil.ReachUtils;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class PredictionEngineElytra
/*    */   extends PredictionEngine {
/*    */   public static Vector3dm getElytraMovement(GrimPlayer player, Vector3dm vector, Vector3dm lookVector) {
/* 16 */     float yRotRadians = player.yRot * 0.017453292F;
/* 17 */     double horizontalSqrt = Math.sqrt(lookVector.getX() * lookVector.getX() + lookVector.getZ() * lookVector.getZ());
/* 18 */     double horizontalLength = vector.clone().setY(0).length();
/* 19 */     double length = lookVector.length();
/*    */ 
/*    */     
/* 22 */     double vertCosRotation = player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_18_2) ? Math.cos(yRotRadians) : player.trigHandler.cos(yRotRadians);
/* 23 */     vertCosRotation = (float)(vertCosRotation * vertCosRotation * Math.min(1.0D, length / 0.4D));
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 28 */     double recalculatedGravity = player.compensatedEntities.self.getAttributeValue(Attributes.GRAVITY);
/* 29 */     if (player.clientVelocity.getY() <= 0.0D && player.compensatedEntities.getSlowFallingAmplifier().isPresent()) {
/* 30 */       recalculatedGravity = player.getClientVersion().isOlderThan(ClientVersion.V_1_20_5) ? 0.01D : Math.min(recalculatedGravity, 0.01D);
/*    */     }
/*    */     
/* 33 */     vector.add(new Vector3dm(0.0D, recalculatedGravity * (-1.0D + vertCosRotation * 0.75D), 0.0D));
/*    */ 
/*    */ 
/*    */     
/* 37 */     if (vector.getY() < 0.0D && horizontalSqrt > 0.0D) {
/* 38 */       double d5 = vector.getY() * -0.1D * vertCosRotation;
/* 39 */       vector.add(new Vector3dm(lookVector.getX() * d5 / horizontalSqrt, d5, lookVector.getZ() * d5 / horizontalSqrt));
/*    */     } 
/*    */ 
/*    */     
/* 43 */     if (yRotRadians < 0.0F && horizontalSqrt > 0.0D) {
/* 44 */       double d5 = horizontalLength * -player.trigHandler.sin(yRotRadians) * 0.04D;
/* 45 */       vector.add(new Vector3dm(-lookVector.getX() * d5 / horizontalSqrt, d5 * 3.2D, -lookVector.getZ() * d5 / horizontalSqrt));
/*    */     } 
/*    */ 
/*    */     
/* 49 */     if (horizontalSqrt > 0.0D) {
/* 50 */       vector.add(new Vector3dm((lookVector.getX() / horizontalSqrt * horizontalLength - vector.getX()) * 0.1D, 0.0D, (lookVector.getZ() / horizontalSqrt * horizontalLength - vector.getZ()) * 0.1D));
/*    */     }
/*    */     
/* 53 */     return vector;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public List<VectorData> applyInputsToVelocityPossibilities(GrimPlayer player, Set<VectorData> possibleVectors, float speed) {
/* 59 */     List<VectorData> results = new ArrayList<>();
/*    */ 
/*    */     
/* 62 */     for (int shitmath = 0; shitmath <= 1; shitmath++, player.trigHandler.toggleShitMath()) {
/* 63 */       Vector3dm currentLook = ReachUtils.getLook(player, player.xRot, player.yRot);
/* 64 */       for (int applyStuckSpeed = 1; applyStuckSpeed >= 0 && (
/* 65 */         applyStuckSpeed != 0 || !player.isForceStuckSpeed()); applyStuckSpeed--) {
/* 66 */         for (VectorData data : possibleVectors) {
/* 67 */           Vector3dm elytraResult = getElytraMovement(player, data.vector.clone(), currentLook);
/* 68 */           if (applyStuckSpeed != 0) elytraResult.multiply(player.stuckSpeedMultiplier); 
/* 69 */           elytraResult.multiply(new Vector3dm(0.99F, 0.98F, 0.99F));
/* 70 */           VectorData modified = data.returnNewModified(elytraResult, VectorData.VectorType.InputResult);
/* 71 */           modified.input = new Vector3dm(0, 0, 0);
/* 72 */           results.add(modified);
/*    */         } 
/*    */       } 
/*    */     } 
/*    */     
/* 77 */     return results;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void addJumpsToPossibilities(GrimPlayer player, Set<VectorData> existingVelocities) {
/* 83 */     (new PredictionEngineNormal()).addJumpsToPossibilities(player, existingVelocities);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\predictionengine\predictions\PredictionEngineElytra.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */