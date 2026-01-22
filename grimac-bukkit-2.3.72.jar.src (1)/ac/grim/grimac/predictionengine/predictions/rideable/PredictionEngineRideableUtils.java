/*     */ package ac.grim.grimac.predictionengine.predictions.rideable;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.predictionengine.predictions.PredictionEngine;
/*     */ import ac.grim.grimac.predictionengine.predictions.PredictionEngineNormal;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion.PotionTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.utils.data.VectorData;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntity;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntityCamel;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntityHorse;
/*     */ import ac.grim.grimac.utils.math.Vector3dm;
/*     */ import ac.grim.grimac.utils.nmsutil.JumpPower;
/*     */ import ac.grim.grimac.utils.nmsutil.ReachUtils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.OptionalInt;
/*     */ import java.util.Set;
/*     */ import lombok.Generated;
/*     */ 
/*     */ public final class PredictionEngineRideableUtils {
/*     */   @Generated
/*     */   private PredictionEngineRideableUtils() {
/*  24 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*     */   } public static Set<VectorData> handleJumps(GrimPlayer player, Set<VectorData> possibleVectors) {
/*     */     PacketEntityHorse horse;
/*  27 */     PacketEntity packetEntity = player.compensatedEntities.self.getRiding(); if (packetEntity instanceof PacketEntityHorse) { horse = (PacketEntityHorse)packetEntity; }
/*  28 */     else { return possibleVectors; }
/*     */     
/*  30 */     if (horse instanceof PacketEntityCamel) { PacketEntityCamel camel = (PacketEntityCamel)horse;
/*  31 */       handleCamelDash(player, possibleVectors, camel); }
/*     */     else
/*  33 */     { handleHorseJumping(player, possibleVectors, horse); }
/*     */ 
/*     */ 
/*     */     
/*  37 */     if (player.lastOnGround) {
/*  38 */       player.vehicleData.horseJump = 0.0F;
/*  39 */       player.vehicleData.horseJumping = false;
/*     */     } 
/*     */     
/*  42 */     return possibleVectors;
/*     */   }
/*     */   private static void handleCamelDash(GrimPlayer player, Set<VectorData> possibleVectors, PacketEntityCamel camel) {
/*     */     double jumpYVelocity;
/*  46 */     boolean wantsToJump = (player.vehicleData.horseJump > 0.0F && !player.vehicleData.horseJumping && player.lastOnGround);
/*  47 */     if (!wantsToJump)
/*     */       return; 
/*  49 */     double jumpFactor = camel.getAttributeValue(Attributes.JUMP_STRENGTH) * JumpPower.getPlayerJumpFactor(player);
/*     */ 
/*     */ 
/*     */     
/*  53 */     OptionalInt jumpBoost = player.compensatedEntities.getPotionLevelForPlayer(PotionTypes.JUMP_BOOST);
/*  54 */     if (jumpBoost.isPresent()) {
/*  55 */       jumpYVelocity = jumpFactor + ((jumpBoost.getAsInt() + 1) * 0.1F);
/*     */     } else {
/*  57 */       jumpYVelocity = jumpFactor;
/*     */     } 
/*     */     
/*  60 */     double multiplier = (22.2222F * player.vehicleData.horseJump) * camel.getAttributeValue(Attributes.MOVEMENT_SPEED) * BlockProperties.getBlockSpeedFactor(player, player.mainSupportingBlockData, new Vector3d(player.lastX, player.lastY, player.lastZ));
/*  61 */     Vector3dm jumpVelocity = ReachUtils.getLook(player, player.xRot, player.yRot).multiply(new Vector3dm(1.0D, 0.0D, 1.0D)).normalize().multiply(multiplier).add(new Vector3dm(0.0D, (1.4285F * player.vehicleData.horseJump) * jumpYVelocity, 0.0D));
/*     */     
/*  63 */     for (VectorData vectorData : possibleVectors) {
/*  64 */       vectorData.vector.add(jumpVelocity);
/*     */     }
/*     */     
/*  67 */     player.vehicleData.horseJumping = true;
/*  68 */     player.vehicleData.camelDashCooldown = 55;
/*     */   }
/*     */ 
/*     */   
/*     */   private static void handleHorseJumping(GrimPlayer player, Set<VectorData> possibleVectors, PacketEntityHorse horse) {
/*     */     double jumpVelocity;
/*  74 */     boolean wantsToJump = (player.vehicleData.horseJump > 0.0F && !player.vehicleData.horseJumping && player.lastOnGround);
/*  75 */     if (!wantsToJump)
/*     */       return; 
/*  77 */     float forwardInput = player.vehicleData.vehicleForward;
/*     */     
/*  79 */     if (forwardInput <= 0.0F) {
/*  80 */       forwardInput *= 0.25F;
/*     */     }
/*     */     
/*  83 */     double jumpFactor = ((float)horse.getAttributeValue(Attributes.JUMP_STRENGTH) * player.vehicleData.horseJump * JumpPower.getPlayerJumpFactor(player));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  90 */     OptionalInt jumpBoost = player.compensatedEntities.getPotionLevelForPlayer(PotionTypes.JUMP_BOOST);
/*  91 */     if (jumpBoost.isPresent()) {
/*  92 */       jumpVelocity = jumpFactor + ((jumpBoost.getAsInt() + 1) * 0.1F);
/*     */     } else {
/*  94 */       jumpVelocity = jumpFactor;
/*     */     } 
/*     */     
/*  97 */     player.vehicleData.horseJumping = true;
/*     */     
/*  99 */     float f2 = player.trigHandler.sin(player.xRot * 0.017453292F);
/* 100 */     float f3 = player.trigHandler.cos(player.xRot * 0.017453292F);
/*     */     
/* 102 */     for (VectorData vectorData : possibleVectors) {
/* 103 */       vectorData.vector.setY(jumpVelocity);
/* 104 */       if (forwardInput > 0.0F) {
/* 105 */         vectorData.vector.add(new Vector3dm((-0.4F * f2 * player.vehicleData.horseJump), 0.0D, (0.4F * f3 * player.vehicleData.horseJump)));
/*     */       }
/*     */     } 
/*     */     
/* 109 */     player.vehicleData.horseJump = 0.0F;
/*     */   }
/*     */   
/*     */   public static List<VectorData> applyInputsToVelocityPossibilities(Vector3dm movementVector, GrimPlayer player, Set<VectorData> possibleVectors, float speed) {
/* 113 */     return applyInputsToVelocityPossibilities(new PredictionEngine(), movementVector, player, possibleVectors, speed);
/*     */   }
/*     */   
/*     */   public static List<VectorData> applyInputsToVelocityPossibilities(PredictionEngine predictionEngine, Vector3dm movementVector, GrimPlayer player, Set<VectorData> possibleVectors, float speed) {
/* 117 */     List<VectorData> returnVectors = new ArrayList<>();
/*     */     
/* 119 */     for (VectorData possibleLastTickOutput : possibleVectors) {
/* 120 */       for (int applyStuckSpeed = 1; applyStuckSpeed >= 0 && (
/* 121 */         applyStuckSpeed != 0 || !player.isForceStuckSpeed()); applyStuckSpeed--) {
/*     */         
/* 123 */         VectorData result = new VectorData(possibleLastTickOutput.vector.clone().add(predictionEngine.getMovementResultFromInput(player, movementVector, speed, player.xRot)), possibleLastTickOutput, VectorData.VectorType.InputResult);
/* 124 */         result.input = new Vector3dm(player.vehicleData.vehicleForward, 0.0F, player.vehicleData.vehicleHorizontal);
/* 125 */         Vector3dm vector = result.vector.clone();
/* 126 */         if (applyStuckSpeed != 0) vector.multiply(player.stuckSpeedMultiplier); 
/* 127 */         result = result.returnNewModified(vector, VectorData.VectorType.StuckMultiplier);
/* 128 */         result = result.returnNewModified((new PredictionEngineNormal()).handleOnClimbable(result.vector.clone(), player), VectorData.VectorType.Climbable);
/* 129 */         returnVectors.add(result);
/*     */ 
/*     */ 
/*     */         
/* 133 */         result = new VectorData(possibleLastTickOutput.vector.clone(), possibleLastTickOutput, VectorData.VectorType.InputResult);
/* 134 */         result.input = new Vector3dm(player.vehicleData.vehicleForward, 0.0F, player.vehicleData.vehicleHorizontal);
/* 135 */         vector = result.vector.clone();
/* 136 */         if (applyStuckSpeed != 0) vector.multiply(player.stuckSpeedMultiplier); 
/* 137 */         result = result.returnNewModified(vector, VectorData.VectorType.StuckMultiplier);
/* 138 */         result = result.returnNewModified((new PredictionEngineNormal()).handleOnClimbable(result.vector.clone(), player), VectorData.VectorType.Climbable);
/* 139 */         returnVectors.add(result);
/*     */       } 
/*     */     } 
/*     */     
/* 143 */     return returnVectors;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\predictionengine\predictions\rideable\PredictionEngineRideableUtils.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */