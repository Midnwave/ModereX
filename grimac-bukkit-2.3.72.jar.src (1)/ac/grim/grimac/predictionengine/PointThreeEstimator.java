/*     */ package ac.grim.grimac.predictionengine;
/*     */ 
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.predictionengine.predictions.PredictionEngine;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion.PotionType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion.PotionTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.utils.collisions.CollisionData;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.CollisionBox;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*     */ import ac.grim.grimac.utils.data.Pair;
/*     */ import ac.grim.grimac.utils.data.VectorData;
/*     */ import ac.grim.grimac.utils.data.VelocityData;
/*     */ import ac.grim.grimac.utils.data.tags.SyncedTags;
/*     */ import ac.grim.grimac.utils.math.Vector3dm;
/*     */ import ac.grim.grimac.utils.nmsutil.Collisions;
/*     */ import ac.grim.grimac.utils.nmsutil.FluidTypeFlowing;
/*     */ import ac.grim.grimac.utils.nmsutil.GetBoundingBox;
/*     */ import ac.grim.grimac.utils.nmsutil.Materials;
/*     */ import java.util.OptionalInt;
/*     */ import java.util.Set;
/*     */ import lombok.Generated;
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
/*     */ 
/*     */ 
/*     */ public class PointThreeEstimator
/*     */ {
/*     */   private final GrimPlayer player;
/*     */   public boolean isNearFluid = false;
/*     */   private boolean headHitter = false;
/*     */   private boolean isNearClimbable = false;
/*     */   
/*     */   @Generated
/*     */   public boolean isNearClimbable() {
/* 100 */     return this.isNearClimbable;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isGliding = false;
/*     */   
/*     */   private boolean gravityChanged = false;
/*     */   
/*     */   private boolean isNearHorizontalFlowingLiquid = false;
/*     */   private boolean isNearVerticalFlowingLiquid = false;
/*     */   private boolean isNearBubbleColumn = false;
/* 111 */   private int maxPositiveLevitation = Integer.MIN_VALUE;
/* 112 */   private int minNegativeLevitation = Integer.MAX_VALUE;
/*     */   private boolean isPushing = false; @Generated
/* 114 */   public void setPushing(boolean isPushing) { this.isPushing = isPushing; } @Generated
/* 115 */   public boolean isPushing() { return this.isPushing; }
/*     */    private boolean wasAlwaysCertain = true; @Generated
/*     */   public boolean isWasAlwaysCertain() {
/* 118 */     return this.wasAlwaysCertain;
/*     */   }
/*     */   
/*     */   public PointThreeEstimator(GrimPlayer player) {
/* 122 */     this.player = player;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleChangeBlock(int x, int y, int z, WrappedBlockState state) {
/* 127 */     StateType stateType = state.getType();
/* 128 */     CollisionBox data = CollisionData.getData(stateType).getMovementCollisionBox(this.player, this.player.getClientVersion(), state, x, y, z);
/* 129 */     SimpleCollisionBox normalBox = GetBoundingBox.getBoundingBoxFromPosAndSize(this.player, this.player.x, this.player.y, this.player.z, 0.6F, 1.8F);
/*     */ 
/*     */ 
/*     */     
/* 133 */     double movementThreshold = this.player.getMovementThreshold();
/* 134 */     SimpleCollisionBox slightlyExpanded = normalBox.copy().expand(movementThreshold, 0.0D, movementThreshold);
/* 135 */     if (!slightlyExpanded.isIntersected(data) && slightlyExpanded.offset(0.0D, movementThreshold, 0.0D).isIntersected(data)) {
/* 136 */       this.headHitter = true;
/*     */     }
/*     */     
/* 139 */     float collisionBoxThreshold = (float)(movementThreshold * 2.0D);
/* 140 */     SimpleCollisionBox pointThreeBox = GetBoundingBox.getBoundingBoxFromPosAndSize(this.player, this.player.x, this.player.y - movementThreshold, this.player.z, 0.6F + collisionBoxThreshold, 1.8F + collisionBoxThreshold);
/* 141 */     if ((Materials.isWater(this.player.getClientVersion(), state) || stateType == StateTypes.LAVA) && pointThreeBox
/* 142 */       .isIntersected(new SimpleCollisionBox(x, y, z))) {
/*     */       
/* 144 */       if (stateType == StateTypes.BUBBLE_COLUMN && this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_13)) {
/* 145 */         this.isNearBubbleColumn = true;
/*     */       }
/*     */       
/* 148 */       Vector3dm fluidVector = FluidTypeFlowing.getFlow(this.player, x, y, z);
/* 149 */       if (fluidVector.getX() != 0.0D || fluidVector.getZ() != 0.0D) {
/* 150 */         this.isNearHorizontalFlowingLiquid = true;
/*     */       }
/* 152 */       if (fluidVector.getY() != 0.0D) {
/* 153 */         this.isNearVerticalFlowingLiquid = true;
/*     */       }
/*     */       
/* 156 */       this.isNearFluid = true;
/*     */     } 
/*     */     
/* 159 */     if (pointThreeBox.isIntersected(new SimpleCollisionBox(x, y, z))) {
/*     */       
/* 161 */       int controllingEntityId = this.player.inVehicle() ? this.player.getRidingVehicleId() : this.player.entityID;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 166 */       VelocityData oldFirstBreadKB = this.player.firstBreadKB;
/* 167 */       VelocityData oldLikelyKB = this.player.likelyKB;
/* 168 */       this.player.firstBreadKB = this.player.checkManager.getKnockbackHandler().calculateFirstBreadKnockback(controllingEntityId, this.player.lastTransactionReceived.get());
/* 169 */       this.player.likelyKB = this.player.checkManager.getKnockbackHandler().calculateRequiredKB(controllingEntityId, this.player.lastTransactionReceived.get(), true);
/*     */       
/* 171 */       VelocityData oldFirstBreadEx = this.player.firstBreadExplosion;
/* 172 */       VelocityData oldLikelyEx = this.player.likelyExplosions;
/* 173 */       this.player.firstBreadExplosion = this.player.checkManager.getExplosionHandler().getFirstBreadAddedExplosion(this.player.lastTransactionReceived.get());
/* 174 */       this.player.likelyExplosions = this.player.checkManager.getExplosionHandler().getPossibleExplosions(this.player.lastTransactionReceived.get(), true);
/*     */       
/* 176 */       this.player.updateVelocityMovementSkipping();
/*     */       
/* 178 */       if (this.player.couldSkipTick) {
/* 179 */         this.player.uncertaintyHandler.lastPointThree.reset();
/*     */       } else {
/*     */         
/* 182 */         this.player.firstBreadKB = oldFirstBreadKB;
/* 183 */         this.player.likelyKB = oldLikelyKB;
/* 184 */         this.player.firstBreadExplosion = oldFirstBreadEx;
/* 185 */         this.player.likelyExplosions = oldLikelyEx;
/*     */       } 
/*     */     } 
/*     */     
/* 189 */     if (!this.player.inVehicle() && ((stateType == StateTypes.POWDER_SNOW && this.player.inventory.getBoots().getType() == ItemTypes.LEATHER_BOOTS) || this.player.tagManager
/* 190 */       .block(SyncedTags.CLIMBABLE).contains(stateType)) && pointThreeBox.isIntersected(new SimpleCollisionBox(x, y, z))) {
/* 191 */       this.isNearClimbable = true;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canPredictNextVerticalMovement() {
/* 200 */     return (!this.gravityChanged && this.maxPositiveLevitation == Integer.MIN_VALUE && this.minNegativeLevitation == Integer.MAX_VALUE);
/*     */   }
/*     */   
/*     */   public double positiveLevitation(double y) {
/* 204 */     if (this.maxPositiveLevitation == Integer.MIN_VALUE) return y; 
/* 205 */     return 0.05D * (this.maxPositiveLevitation + 1) - y * 0.2D;
/*     */   }
/*     */   
/*     */   public double negativeLevitation(double y) {
/* 209 */     if (this.minNegativeLevitation == Integer.MAX_VALUE) return y; 
/* 210 */     return 0.05D * (this.minNegativeLevitation + 1) - y * 0.2D;
/*     */   }
/*     */   
/*     */   public boolean controlsVerticalMovement() {
/* 214 */     return (this.isNearFluid || this.isNearClimbable || this.isNearHorizontalFlowingLiquid || this.isNearVerticalFlowingLiquid || this.isNearBubbleColumn || this.isGliding || this.player.uncertaintyHandler.influencedByBouncyBlock() || this.player.checkManager
/* 215 */       .getKnockbackHandler().isKnockbackPointThree() || this.player.checkManager.getExplosionHandler().isExplosionPointThree());
/*     */   }
/*     */   
/*     */   public void updatePlayerPotions(PotionType potion, Integer level) {
/* 219 */     if (potion == PotionTypes.LEVITATION) {
/* 220 */       this.maxPositiveLevitation = Math.max((level == null) ? Integer.MIN_VALUE : level.intValue(), this.maxPositiveLevitation);
/* 221 */       this.minNegativeLevitation = Math.min((level == null) ? Integer.MAX_VALUE : level.intValue(), this.minNegativeLevitation);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void updatePlayerGliding() {
/* 226 */     this.isGliding = true;
/*     */   }
/*     */   
/*     */   public void updatePlayerGravity() {
/* 230 */     this.gravityChanged = true;
/*     */   }
/*     */   
/*     */   public void endOfTickTick() {
/* 234 */     double movementThreshold = this.player.getMovementThreshold();
/* 235 */     float collisionBoxThreshold = this.player.isPointThree() ? 0.06F : 4.0E-4F;
/* 236 */     SimpleCollisionBox pointThreeBox = GetBoundingBox.getBoundingBoxFromPosAndSize(this.player, this.player.x, this.player.y - movementThreshold, this.player.z, 0.6F + collisionBoxThreshold, 1.8F + collisionBoxThreshold);
/*     */ 
/*     */     
/* 239 */     SimpleCollisionBox oldBB = this.player.boundingBox;
/*     */     
/* 241 */     this.headHitter = false;
/*     */     
/* 243 */     (new float[3])[0] = 0.6F; (new float[3])[1] = 1.5F; (new float[3])[2] = 1.8F; (new float[1])[0] = this.player.pose.height; for (float sizes : this.player.skippedTickInActualMovement ? new float[3] : new float[1]) {
/*     */       
/* 245 */       this.player.boundingBox = GetBoundingBox.getBoundingBoxFromPosAndSize(this.player, this.player.x, this.player.y + (sizes - 0.01F), this.player.z, 0.6F, 0.01F);
/* 246 */       this.headHitter = (this.headHitter || Collisions.collide(this.player, 0.0D, movementThreshold, 0.0D).getY() != movementThreshold);
/*     */     } 
/*     */     
/* 249 */     this.player.boundingBox = oldBB;
/*     */     
/* 251 */     checkNearbyBlocks(pointThreeBox);
/*     */     
/* 253 */     this.maxPositiveLevitation = Integer.MIN_VALUE;
/* 254 */     this.minNegativeLevitation = Integer.MAX_VALUE;
/*     */     
/* 256 */     this.isGliding = this.player.isGliding;
/* 257 */     this.gravityChanged = false;
/* 258 */     this.wasAlwaysCertain = true;
/* 259 */     this.isPushing = false;
/*     */   }
/*     */ 
/*     */   
/*     */   private void checkNearbyBlocks(SimpleCollisionBox pointThreeBox) {
/* 264 */     this.isNearHorizontalFlowingLiquid = false;
/* 265 */     this.isNearVerticalFlowingLiquid = false;
/* 266 */     this.isNearClimbable = false;
/* 267 */     this.isNearBubbleColumn = false;
/* 268 */     this.isNearFluid = false;
/*     */ 
/*     */     
/* 271 */     Collisions.hasMaterial(this.player, pointThreeBox, pair -> {
/*     */           WrappedBlockState state = (WrappedBlockState)pair.first();
/*     */           
/*     */           StateType stateType = state.getType();
/*     */           if (this.player.tagManager.block(SyncedTags.CLIMBABLE).contains(stateType) || (stateType == StateTypes.POWDER_SNOW && !this.player.inVehicle() && this.player.inventory.getBoots().getType() == ItemTypes.LEATHER_BOOTS)) {
/*     */             this.isNearClimbable = true;
/*     */           }
/*     */           if (BlockTags.TRAPDOORS.contains(stateType)) {
/* 279 */             this.isNearClimbable = (this.isNearClimbable || Collisions.trapdoorUsableAsLadder(this.player, ((Vector3d)pair.second()).getX(), ((Vector3d)pair.second()).getY(), ((Vector3d)pair.second()).getZ(), state));
/*     */           }
/*     */           if (stateType == StateTypes.BUBBLE_COLUMN && this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_13)) {
/*     */             this.isNearBubbleColumn = true;
/*     */           }
/*     */           if (Materials.isWater(this.player.getClientVersion(), (WrappedBlockState)pair.first()) || ((WrappedBlockState)pair.first()).getType() == StateTypes.LAVA) {
/*     */             this.isNearFluid = true;
/*     */           }
/*     */           return false;
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean closeEnoughToGroundToStepWithPointThree(VectorData data, double originalY) {
/* 295 */     if (this.player.inVehicle()) return false; 
/* 296 */     if (!this.player.isPointThree()) return false;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 315 */     if (this.player.clientControlledVerticalCollision && data != null && data.isZeroPointZeroThree()) {
/* 316 */       return checkForGround(originalY);
/*     */     }
/*     */     
/* 319 */     return false;
/*     */   }
/*     */   
/*     */   private boolean checkForGround(double y) {
/* 323 */     SimpleCollisionBox playerBox = this.player.boundingBox;
/* 324 */     double threshold = this.player.getMovementThreshold();
/* 325 */     this.player.boundingBox = this.player.boundingBox.copy().expand(threshold, 0.0D, threshold).offset(0.0D, threshold, 0.0D);
/*     */     
/* 327 */     double searchDistance = -0.2D + Math.min(0.0D, y);
/* 328 */     Vector3dm collisionResult = Collisions.collide(this.player, 0.0D, searchDistance, 0.0D);
/* 329 */     this.player.boundingBox = playerBox;
/* 330 */     return (collisionResult.getY() != searchDistance);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean determineCanSkipTick(float speed, Set<VectorData> init) {
/* 337 */     if (!this.player.canSkipTicks() && this.player.packetStateData.didLastMovementIncludePosition && !this.player.uncertaintyHandler.isSteppingOnSlime) {
/* 338 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 342 */     double minimum = Double.MAX_VALUE;
/*     */     
/* 344 */     if ((this.player.isGliding || this.player.wasGliding) && !this.player.packetStateData.didLastMovementIncludePosition) {
/* 345 */       return true;
/*     */     }
/*     */ 
/*     */     
/* 349 */     if (this.player.inVehicle()) {
/* 350 */       return false;
/*     */     }
/*     */     
/* 353 */     if (isNearClimbable() || this.isPushing || this.player.uncertaintyHandler.wasAffectedByStuckSpeed() || this.player.fireworks.getMaxFireworksAppliedPossible() > 0) {
/* 354 */       return true;
/*     */     }
/*     */     
/* 357 */     boolean couldStep = (this.player.isPointThree() && checkForGround(this.player.clientVelocity.getY()));
/*     */ 
/*     */     
/* 360 */     for (VectorData data : init) {
/*     */       
/* 362 */       Vector3dm toZeroVec = (new PredictionEngine()).handleStartingVelocityUncertainty(this.player, data, new Vector3dm());
/*     */       
/* 364 */       Vector3dm collisionResult = Collisions.collide(this.player, toZeroVec.getX(), toZeroVec.getY(), toZeroVec.getZ(), -2.147483648E9D, null);
/*     */ 
/*     */ 
/*     */       
/* 368 */       boolean likelyStepSkip = (this.player.isPointThree() && data.vector.getY() > -0.08D && data.vector.getY() < 0.06D && couldStep);
/*     */ 
/*     */ 
/*     */       
/* 372 */       double minHorizLength = Math.max(0.0D, Math.hypot(collisionResult.getX(), collisionResult.getZ()) - speed);
/*     */ 
/*     */       
/* 375 */       boolean forcedNo003 = (data.isExplosion() || data.isKnockback());
/*     */ 
/*     */       
/* 378 */       double length = Math.hypot(((!forcedNo003 && this.player.lastOnGround) || likelyStepSkip || controlsVerticalMovement()) ? 0.0D : Math.abs(collisionResult.getY()), minHorizLength);
/*     */       
/* 380 */       minimum = Math.min(minimum, length);
/*     */       
/* 382 */       if (minimum < this.player.getMovementThreshold()) {
/*     */         break;
/*     */       }
/*     */     } 
/* 386 */     return (minimum < this.player.getMovementThreshold());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public double getHorizontalFluidPushingUncertainty(VectorData vector) {
/* 392 */     return (this.isNearHorizontalFlowingLiquid && vector.isZeroPointZeroThree()) ? 0.028D : 0.0D;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public double getVerticalFluidPushingUncertainty(VectorData vector) {
/* 398 */     return ((this.isNearBubbleColumn || this.isNearVerticalFlowingLiquid) && vector.isZeroPointZeroThree()) ? 0.028D : 0.0D;
/*     */   }
/*     */   
/*     */   public double getVerticalBubbleUncertainty(VectorData vectorData) {
/* 402 */     return (this.isNearBubbleColumn && vectorData.isZeroPointZeroThree()) ? 0.35D : 0.0D;
/*     */   }
/*     */   
/*     */   public double getAdditionalVerticalUncertainty(VectorData vector) {
/* 406 */     double fluidAddition = vector.isZeroPointZeroThree() ? 0.014D : 0.0D;
/*     */     
/* 408 */     if (this.player.inVehicle()) return 0.0D;
/*     */     
/* 410 */     if (this.headHitter) {
/* 411 */       this.wasAlwaysCertain = false;
/*     */ 
/*     */       
/* 414 */       return -Math.max(0.0D, vector.vector.getY()) - 0.1D - fluidAddition;
/* 415 */     }  if (this.player.uncertaintyHandler.wasAffectedByStuckSpeed()) {
/* 416 */       this.wasAlwaysCertain = false;
/*     */ 
/*     */       
/* 419 */       return -0.1D - fluidAddition;
/*     */     } 
/*     */ 
/*     */     
/* 423 */     if (!vector.isZeroPointZeroThree()) return 0.0D;
/*     */     
/* 425 */     double minMovement = this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 0.003D : 0.005D;
/*     */ 
/*     */     
/* 428 */     double yVel = vector.vector.getY();
/* 429 */     double maxYTraveled = 0.0D;
/* 430 */     boolean first = true;
/*     */     
/*     */     do {
/* 433 */       if (Math.abs(yVel) < minMovement) yVel = 0.0D;
/*     */ 
/*     */       
/* 436 */       if (!first) {
/* 437 */         maxYTraveled += yVel;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 444 */       if (!first && yVel == 0.0D) {
/*     */         break;
/*     */       }
/*     */       
/* 448 */       first = false;
/*     */ 
/*     */       
/* 451 */       yVel = iterateGravity(this.player, yVel);
/*     */     
/*     */     }
/* 454 */     while (yVel != 0.0D && 
/* 455 */       Math.abs(maxYTraveled + vector.vector.getY()) < this.player.getMovementThreshold());
/*     */     
/* 457 */     if (maxYTraveled != 0.0D) {
/* 458 */       this.wasAlwaysCertain = false;
/*     */     }
/*     */ 
/*     */     
/* 462 */     return maxYTraveled;
/*     */   }
/*     */   
/*     */   private double iterateGravity(GrimPlayer player, double y) {
/* 466 */     OptionalInt levitation = player.compensatedEntities.getPotionLevelForPlayer(PotionTypes.LEVITATION);
/* 467 */     if (levitation.isPresent()) {
/*     */       
/* 469 */       y += 0.05D * (levitation.getAsInt() + 1) - y * 0.2D;
/* 470 */     } else if (player.hasGravity) {
/*     */       
/* 472 */       y -= player.gravity;
/*     */     } 
/*     */ 
/*     */     
/* 476 */     return y * 0.98D;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\predictionengine\PointThreeEstimator.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */