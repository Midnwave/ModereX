/*     */ package ac.grim.grimac.predictionengine.predictions;
/*     */ 
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.utils.data.VectorData;
/*     */ import ac.grim.grimac.utils.enums.FluidTag;
/*     */ import ac.grim.grimac.utils.math.GrimMath;
/*     */ import ac.grim.grimac.utils.math.Vector3dm;
/*     */ import ac.grim.grimac.utils.nmsutil.Collisions;
/*     */ import ac.grim.grimac.utils.nmsutil.FluidFallingAdjustedMovement;
/*     */ import ac.grim.grimac.utils.nmsutil.ReachUtils;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class PredictionEngineWater
/*     */   extends PredictionEngine
/*     */ {
/*     */   private boolean isFalling;
/*     */   private double playerGravity;
/*     */   private float swimmingFriction;
/*     */   
/*     */   public static void staticVectorEndOfTick(GrimPlayer player, Vector3dm vector, float swimmingFriction, double playerGravity, boolean isFalling) {
/*  23 */     vector.multiply(new Vector3dm(swimmingFriction, 0.8F, swimmingFriction));
/*  24 */     Vector3dm fluidVector = FluidFallingAdjustedMovement.getFluidFallingAdjustedMovement(player, playerGravity, isFalling, vector);
/*  25 */     vector.setX(fluidVector.getX());
/*  26 */     vector.setY(fluidVector.getY());
/*  27 */     vector.setZ(fluidVector.getZ());
/*     */   }
/*     */   
/*     */   public static Set<VectorData> transformSwimmingVectors(GrimPlayer player, Set<VectorData> base) {
/*  31 */     Set<VectorData> swimmingVelocities = new HashSet<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  42 */     if ((player.wasEyeInWater || player.fluidOnEyes == FluidTag.WATER || player.isSwimming || player.wasSwimming) && !player.inVehicle()) {
/*  43 */       for (VectorData vector : base) {
/*  44 */         double lookYAmount = ReachUtils.getLook(player, player.xRot, player.yRot).getY();
/*  45 */         double scalar = (lookYAmount < -0.2D) ? 0.085D : 0.06D;
/*     */ 
/*     */         
/*  48 */         swimmingVelocities.add(vector.returnNewModified(new Vector3dm(vector.vector.getX(), vector.vector.getY() + (lookYAmount - vector.vector.getY()) * scalar, vector.vector.getZ()), VectorData.VectorType.SwimmingSpace));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  57 */         swimmingVelocities.add(vector.returnNewModified(vector.vector, VectorData.VectorType.SurfaceSwimming));
/*     */       } 
/*     */       
/*  60 */       return swimmingVelocities;
/*     */     } 
/*  62 */     return base;
/*     */   }
/*     */   
/*     */   public void guessBestMovement(float swimmingSpeed, GrimPlayer player, boolean isFalling, double playerGravity, float swimmingFriction) {
/*  66 */     this.isFalling = isFalling;
/*  67 */     this.playerGravity = playerGravity;
/*  68 */     this.swimmingFriction = swimmingFriction;
/*  69 */     guessBestMovement(swimmingSpeed, player);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addJumpsToPossibilities(GrimPlayer player, Set<VectorData> existingVelocities) {
/*  74 */     for (VectorData vector : new HashSet(existingVelocities)) {
/*  75 */       if (player.couldSkipTick && vector.isZeroPointZeroThree()) {
/*  76 */         double extraVelFromVertTickSkipUpwards = GrimMath.clamp(player.actualMovement.getY(), vector.vector.clone().getY(), vector.vector.clone().getY() + 0.05000000074505806D);
/*  77 */         existingVelocities.add(new VectorData(vector.vector.clone().setY(extraVelFromVertTickSkipUpwards), vector, VectorData.VectorType.Jump));
/*     */       } else {
/*  79 */         existingVelocities.add(new VectorData(vector.vector.clone().add(new Vector3dm(0.0F, 0.04F, 0.0F)), vector, VectorData.VectorType.Jump));
/*     */       } 
/*     */       
/*  82 */       if (player.slightlyTouchingWater && player.lastOnGround && !player.onGround) {
/*  83 */         Vector3dm withJump = vector.vector.clone();
/*  84 */         doJump(player, withJump);
/*  85 */         existingVelocities.add(new VectorData(withJump, vector, VectorData.VectorType.Jump));
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void endOfTick(GrimPlayer player, double playerGravity) {
/*  92 */     super.endOfTick(player, playerGravity);
/*     */     
/*  94 */     for (VectorData vector : player.getPossibleVelocitiesMinusKnockback()) {
/*  95 */       staticVectorEndOfTick(player, vector.vector, this.swimmingFriction, playerGravity, this.isFalling);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<VectorData> fetchPossibleStartTickVectors(GrimPlayer player) {
/* 102 */     if (player.lastWasClimbing == 0.0D && player.pointThreeEstimator.isNearClimbable()) { if (!player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_14)) { if (!Collisions.isEmpty(player, player.boundingBox.copy().expand(player.clientVelocity
/* 103 */               .getX(), 0.0D, player.clientVelocity.getZ()).expand(0.5D, -1.0E-7D, 0.5D)))
/* 104 */         { player.lastWasClimbing = FluidFallingAdjustedMovement.getFluidFallingAdjustedMovement(player, this.playerGravity, this.isFalling, player.clientVelocity.clone().setY(0.1600000023841858D)).getY();
/*     */ 
/*     */           
/* 107 */           Set<VectorData> baseVelocities = super.fetchPossibleStartTickVectors(player);
/*     */           
/* 109 */           return transformSwimmingVectors(player, baseVelocities); }  Set<VectorData> set1 = super.fetchPossibleStartTickVectors(player); return transformSwimmingVectors(player, set1); }  } else { Set<VectorData> set1 = super.fetchPossibleStartTickVectors(player); return transformSwimmingVectors(player, set1); }  player.lastWasClimbing = FluidFallingAdjustedMovement.getFluidFallingAdjustedMovement(player, this.playerGravity, this.isFalling, player.clientVelocity.clone().setY(0.1600000023841858D)).getY(); Set<VectorData> set = super.fetchPossibleStartTickVectors(player); return transformSwimmingVectors(player, set);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\predictionengine\predictions\PredictionEngineWater.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */