/*     */ package ac.grim.grimac.predictionengine;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAttributes;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*     */ import ac.grim.grimac.utils.data.attribute.ValuedAttribute;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntity;
/*     */ import ac.grim.grimac.utils.enums.FluidTag;
/*     */ import ac.grim.grimac.utils.enums.Pose;
/*     */ import ac.grim.grimac.utils.latency.CompensatedEntities;
/*     */ import ac.grim.grimac.utils.math.GrimMath;
/*     */ import ac.grim.grimac.utils.math.Vector3dm;
/*     */ import ac.grim.grimac.utils.nmsutil.BlockProperties;
/*     */ import ac.grim.grimac.utils.nmsutil.CheckIfChunksLoaded;
/*     */ import ac.grim.grimac.utils.nmsutil.Collisions;
/*     */ import ac.grim.grimac.utils.nmsutil.FluidTypeFlowing;
/*     */ import ac.grim.grimac.utils.nmsutil.GetBoundingBox;
/*     */ import java.util.Optional;
/*     */ import lombok.Generated;
/*     */ 
/*     */ public final class PlayerBaseTick {
/*     */   @Generated
/*     */   private PlayerBaseTick() {
/*  28 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*     */   }
/*     */   public static boolean canEnterPose(GrimPlayer player, Pose pose, double x, double y, double z) {
/*  31 */     return Collisions.isEmpty(player, getBoundingBoxForPose(player, pose, x, y, z).expand(-1.0E-7D));
/*     */   }
/*     */   
/*     */   private static SimpleCollisionBox getBoundingBoxForPose(GrimPlayer player, Pose pose, double x, double y, double z) {
/*  35 */     float scale = (float)player.compensatedEntities.self.getAttributeValue(Attributes.SCALE);
/*  36 */     float width = pose.width * scale;
/*  37 */     float height = pose.height * scale;
/*  38 */     float radius = width / 2.0F;
/*  39 */     return new SimpleCollisionBox(x - radius, y, z - radius, x + radius, y + height, z + radius, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public static void doBaseTick(GrimPlayer player) {
/*  44 */     player.baseTickAddition = new Vector3dm();
/*  45 */     player.baseTickWaterPushing = new Vector3dm();
/*     */     
/*  47 */     if (player.isFlying && player.isSneaking && !player.inVehicle()) {
/*  48 */       Vector3dm flyingShift = new Vector3dm(0.0F, player.flySpeed * -3.0F, 0.0F);
/*  49 */       player.baseTickAddVector(flyingShift);
/*  50 */       player.trackBaseTickAddition(flyingShift);
/*     */     } 
/*     */     
/*  53 */     updateInWaterStateAndDoFluidPushing(player);
/*  54 */     updateFluidOnEyes(player);
/*  55 */     updateSwimming(player);
/*     */ 
/*     */     
/*  58 */     if (player.wasTouchingLava) {
/*  59 */       player.fallDistance *= 0.5D;
/*     */     }
/*     */ 
/*     */     
/*  63 */     if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_13) && player.wasTouchingWater && player.isSneaking && !player.isFlying && !player.inVehicle()) {
/*  64 */       Vector3dm waterPushVector = new Vector3dm(0.0F, -0.04F, 0.0F);
/*  65 */       player.baseTickAddVector(waterPushVector);
/*  66 */       player.trackBaseTickAddition(waterPushVector);
/*     */     } 
/*     */     
/*  69 */     player.lastPose = player.pose;
/*     */     
/*  71 */     if (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_13_2)) {
/*     */       
/*  73 */       player.isSlowMovement = player.isSneaking;
/*     */     } else {
/*  75 */       player
/*     */         
/*  77 */         .isSlowMovement = ((!player.wasFlying && !player.isSwimming && canEnterPose(player, Pose.CROUCHING, player.lastX, player.lastY, player.lastZ) && (player.wasSneaking || (!player.isInBed && !canEnterPose(player, Pose.STANDING, player.lastX, player.lastY, player.lastZ)))) || ((player.pose == Pose.SWIMMING || (!player.isGliding && player.pose == Pose.FALL_FLYING)) && !player.wasTouchingWater));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  84 */       if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_14) && player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_14_4)) {
/*  85 */         player.isSlowMovement |= player.isSneaking;
/*     */       }
/*     */     } 
/*     */     
/*  89 */     if (player.inVehicle()) player.isSlowMovement = false;
/*     */ 
/*     */     
/*  92 */     if (!player.inVehicle()) {
/*  93 */       moveTowardsClosestSpace(player, player.lastX - (player.boundingBox.maxX - player.boundingBox.minX) * 0.35D, player.lastZ + (player.boundingBox.maxZ - player.boundingBox.minZ) * 0.35D);
/*  94 */       moveTowardsClosestSpace(player, player.lastX - (player.boundingBox.maxX - player.boundingBox.minX) * 0.35D, player.lastZ - (player.boundingBox.maxZ - player.boundingBox.minZ) * 0.35D);
/*  95 */       moveTowardsClosestSpace(player, player.lastX + (player.boundingBox.maxX - player.boundingBox.minX) * 0.35D, player.lastZ - (player.boundingBox.maxZ - player.boundingBox.minZ) * 0.35D);
/*  96 */       moveTowardsClosestSpace(player, player.lastX + (player.boundingBox.maxX - player.boundingBox.minX) * 0.35D, player.lastZ + (player.boundingBox.maxZ - player.boundingBox.minZ) * 0.35D);
/*     */     } 
/*     */     
/*  99 */     if (player.getClientVersion().isOlderThan(ClientVersion.V_1_14)) {
/* 100 */       updatePlayerSize(player);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void updateFluidOnEyes(GrimPlayer player) {
/* 107 */     player.wasEyeInWater = (player.fluidOnEyes == FluidTag.WATER);
/* 108 */     player.fluidOnEyes = null;
/*     */     
/* 110 */     double d0 = player.lastY + player.getEyeHeight() - 0.1111111119389534D;
/*     */     
/* 112 */     PacketEntity riding = player.compensatedEntities.self.getRiding();
/* 113 */     if (riding != null && riding.isBoat && !player.vehicleData.boatUnderwater && player.boundingBox.maxY >= d0 && player.boundingBox.minY <= d0) {
/*     */       return;
/*     */     }
/*     */     
/* 117 */     double d1 = (float)Math.floor(d0) + player.compensatedWorld.getWaterFluidLevelAt(player.lastX, d0, player.lastZ);
/* 118 */     if (d1 > d0) {
/* 119 */       player.fluidOnEyes = FluidTag.WATER;
/* 120 */       if (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_15_2)) {
/* 121 */         player.wasEyeInWater = true;
/*     */       }
/*     */       return;
/*     */     } 
/* 125 */     if (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_15_2)) {
/* 126 */       player.wasEyeInWater = false;
/*     */     }
/* 128 */     d1 = (float)Math.floor(d0) + player.compensatedWorld.getWaterFluidLevelAt(player.lastX, d0, player.lastZ);
/* 129 */     if (d1 > d0) {
/* 130 */       player.fluidOnEyes = FluidTag.LAVA;
/*     */     }
/*     */   }
/*     */   
/*     */   private static void updateInWaterStateAndDoFluidPushing(GrimPlayer player) {
/* 135 */     updateInWaterStateAndDoWaterCurrentPushing(player);
/* 136 */     double multiplier = player.dimensionType.isUltraWarm() ? 0.007D : 0.0023333333333333335D;
/*     */     
/* 138 */     if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_16)) {
/* 139 */       player.wasTouchingLava = updateFluidHeightAndDoFluidPushing(player, FluidTag.LAVA, multiplier);
/*     */     }
/* 141 */     else if (player.getClientVersion().isOlderThan(ClientVersion.V_1_14)) {
/* 142 */       SimpleCollisionBox playerBox = player.boundingBox.copy().expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D);
/* 143 */       player.wasTouchingLava = player.compensatedWorld.containsLava(playerBox);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static void updatePowderSnow(GrimPlayer player) {
/* 149 */     if (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_16_4))
/*     */       return; 
/* 151 */     ValuedAttribute playerSpeed = player.compensatedEntities.self.getAttribute(Attributes.MOVEMENT_SPEED).orElseThrow();
/*     */ 
/*     */     
/* 154 */     Optional<WrapperPlayServerUpdateAttributes.Property> property = playerSpeed.property();
/* 155 */     if (property.isEmpty()) {
/*     */       return;
/*     */     }
/* 158 */     ((WrapperPlayServerUpdateAttributes.Property)property.get()).getModifiers().removeIf(modifier -> (modifier.getUUID().equals(CompensatedEntities.SNOW_MODIFIER_UUID) || modifier.getName().getKey().equals("powder_snow")));
/* 159 */     playerSpeed.recalculate();
/*     */ 
/*     */     
/* 162 */     StateType type = BlockProperties.getOnPos(player, player.mainSupportingBlockData, new Vector3d(player.x, player.y, player.z));
/*     */     
/* 164 */     if (!type.isAir()) {
/* 165 */       int i = player.powderSnowFrozenTicks;
/* 166 */       if (i > 0) {
/* 167 */         int ticksToFreeze = 140;
/*     */         
/* 169 */         float percentFrozen = Math.min(i, ticksToFreeze) / ticksToFreeze;
/* 170 */         float percentFrozenReducedToSpeed = -0.05F * percentFrozen;
/*     */         
/* 172 */         ((WrapperPlayServerUpdateAttributes.Property)property.get()).getModifiers().add(new WrapperPlayServerUpdateAttributes.PropertyModifier(CompensatedEntities.SNOW_MODIFIER_UUID, percentFrozenReducedToSpeed, WrapperPlayServerUpdateAttributes.PropertyModifier.Operation.ADDITION));
/* 173 */         playerSpeed.recalculate();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static void updatePlayerPose(GrimPlayer player) {
/* 180 */     if (canEnterPose(player, Pose.SWIMMING, player.x, player.y, player.z)) {
/*     */       Pose pose;
/* 182 */       if (player.isGliding) {
/* 183 */         pose = Pose.FALL_FLYING;
/* 184 */       } else if (player.isInBed) {
/* 185 */         pose = Pose.SLEEPING;
/* 186 */       } else if (player.isSwimming) {
/* 187 */         pose = Pose.SWIMMING;
/* 188 */       } else if (player.isRiptidePose) {
/* 189 */         pose = Pose.SPIN_ATTACK;
/* 190 */       } else if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) && player.getClientVersion().isOlderThan(ClientVersion.V_1_14) && player.isSneaking) {
/* 191 */         pose = Pose.NINE_CROUCHING;
/* 192 */       } else if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_14) && player.isSneaking && !player.isFlying) {
/* 193 */         pose = Pose.CROUCHING;
/*     */       } else {
/* 195 */         pose = Pose.STANDING;
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 200 */       if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_14) && 
/* 201 */         !player.inVehicle() && !canEnterPose(player, pose, player.x, player.y, player.z)) {
/* 202 */         if (canEnterPose(player, Pose.CROUCHING, player.x, player.y, player.z)) {
/* 203 */           pose = Pose.CROUCHING;
/*     */         } else {
/* 205 */           pose = Pose.SWIMMING;
/*     */         } 
/*     */       }
/*     */       
/* 209 */       player.pose = pose;
/* 210 */       player.boundingBox = getBoundingBoxForPose(player, player.pose, player.x, player.y, player.z);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static void updatePlayerSize(GrimPlayer player) {
/*     */     Pose pose;
/* 217 */     if (player.isGliding) {
/* 218 */       pose = Pose.FALL_FLYING;
/* 219 */     } else if (player.isInBed) {
/* 220 */       pose = Pose.SLEEPING;
/* 221 */     } else if (!player.isSwimming && !player.isRiptidePose) {
/* 222 */       if (player.isSneaking && player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
/* 223 */         pose = Pose.NINE_CROUCHING;
/*     */       } else {
/* 225 */         pose = Pose.STANDING;
/*     */       } 
/*     */     } else {
/* 228 */       pose = Pose.SWIMMING;
/*     */     } 
/*     */ 
/*     */     
/* 232 */     if (pose != player.pose) {
/* 233 */       Pose oldPose = player.pose;
/* 234 */       player.pose = pose;
/*     */       
/* 236 */       SimpleCollisionBox box = GetBoundingBox.getCollisionBoxForPlayer(player, player.lastX, player.lastY, player.lastZ);
/* 237 */       boolean collides = !Collisions.isEmpty(player, box);
/*     */       
/* 239 */       if (collides) {
/*     */         
/* 241 */         player.pose = oldPose;
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/* 246 */     player.boundingBox = GetBoundingBox.getCollisionBoxForPlayer(player, player.lastX, player.lastY, player.lastZ);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void updateSwimming(GrimPlayer player) {
/* 251 */     if (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_12_2)) {
/* 252 */       player.isSwimming = false;
/* 253 */     } else if (player.isFlying) {
/* 254 */       player.isSwimming = false;
/*     */     }
/* 256 */     else if (player.inVehicle()) {
/* 257 */       player.isSwimming = false;
/* 258 */     } else if (player.isSwimming) {
/* 259 */       player.isSwimming = (player.lastSprinting && player.wasTouchingWater);
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 264 */       boolean feetInWater = (player.getClientVersion().isOlderThan(ClientVersion.V_1_17) || player.compensatedWorld.getWaterFluidLevelAt(player.lastX, player.lastY, player.lastZ) > 0.0D);
/* 265 */       player.isSwimming = (player.lastSprinting && player.wasEyeInWater && player.wasTouchingWater && feetInWater);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static void moveTowardsClosestSpace(GrimPlayer player, double xPosition, double zPosition) {
/* 271 */     double movementThreshold = player.getMovementThreshold();
/* 272 */     player.boundingBox = player.boundingBox.expand(movementThreshold, 0.0D, movementThreshold);
/* 273 */     if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_14)) {
/* 274 */       moveTowardsClosestSpaceModern(player, xPosition, zPosition);
/*     */     } else {
/* 276 */       moveTowardsClosestSpaceLegacy(player, xPosition, zPosition);
/*     */     } 
/* 278 */     player.boundingBox = player.boundingBox.expand(-movementThreshold, 0.0D, -movementThreshold);
/*     */   }
/*     */   
/*     */   private static void moveTowardsClosestSpaceLegacy(GrimPlayer player, double x, double z) {
/*     */     boolean suffocates;
/* 283 */     int floorX = GrimMath.floor(x);
/* 284 */     int floorZ = GrimMath.floor(z);
/* 285 */     int floorY = GrimMath.floor(player.lastY + 0.5D);
/*     */     
/* 287 */     double d0 = x - floorX;
/* 288 */     double d1 = z - floorZ;
/*     */ 
/*     */ 
/*     */     
/* 292 */     if (player.isSwimming) {
/* 293 */       SimpleCollisionBox blockPos = (new SimpleCollisionBox(floorX, floorY, floorZ, floorX + 1.0D, (floorY + 1), floorZ + 1.0D, false)).expand(-1.0E-7D);
/* 294 */       suffocates = Collisions.suffocatesAt(player, blockPos);
/*     */     } else {
/* 296 */       suffocates = !clearAbove(player, floorX, floorY, floorZ);
/*     */     } 
/*     */     
/* 299 */     if (suffocates) {
/* 300 */       int i = -1;
/* 301 */       double d2 = 9999.0D;
/* 302 */       if (clearAbove(player, floorX - 1, floorY, floorZ) && d0 < d2) {
/* 303 */         d2 = d0;
/* 304 */         i = 0;
/*     */       } 
/*     */       
/* 307 */       if (clearAbove(player, floorX + 1, floorY, floorZ) && 1.0D - d0 < d2) {
/* 308 */         d2 = 1.0D - d0;
/* 309 */         i = 1;
/*     */       } 
/*     */       
/* 312 */       if (clearAbove(player, floorX, floorY, floorZ - 1) && d1 < d2) {
/* 313 */         d2 = d1;
/* 314 */         i = 4;
/*     */       } 
/*     */       
/* 317 */       if (clearAbove(player, floorX, floorY, floorZ + 1) && 1.0D - d1 < d2) {
/* 318 */         i = 5;
/*     */       }
/*     */       
/* 321 */       if (i == 0) {
/* 322 */         player.uncertaintyHandler.xNegativeUncertainty -= 0.1D;
/* 323 */         player.uncertaintyHandler.xPositiveUncertainty += 0.1D;
/* 324 */         player.pointThreeEstimator.setPushing(true);
/*     */       } 
/*     */       
/* 327 */       if (i == 1) {
/* 328 */         player.uncertaintyHandler.xNegativeUncertainty -= 0.1D;
/* 329 */         player.uncertaintyHandler.xPositiveUncertainty += 0.1D;
/* 330 */         player.pointThreeEstimator.setPushing(true);
/*     */       } 
/*     */       
/* 333 */       if (i == 4) {
/* 334 */         player.uncertaintyHandler.zNegativeUncertainty -= 0.1D;
/* 335 */         player.uncertaintyHandler.zPositiveUncertainty += 0.1D;
/* 336 */         player.pointThreeEstimator.setPushing(true);
/*     */       } 
/*     */       
/* 339 */       if (i == 5) {
/* 340 */         player.uncertaintyHandler.zNegativeUncertainty -= 0.1D;
/* 341 */         player.uncertaintyHandler.zPositiveUncertainty += 0.1D;
/* 342 */         player.pointThreeEstimator.setPushing(true);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static void moveTowardsClosestSpaceModern(GrimPlayer player, double xPosition, double zPosition) {
/* 349 */     int blockX = (int)Math.floor(xPosition);
/* 350 */     int blockZ = (int)Math.floor(zPosition);
/*     */     
/* 352 */     if (!suffocatesAt(player, blockX, blockZ)) {
/*     */       return;
/*     */     }
/*     */     
/* 356 */     double relativeXMovement = xPosition - blockX;
/* 357 */     double relativeZMovement = zPosition - blockZ;
/* 358 */     BlockFace direction = null;
/* 359 */     double lowestValue = Double.MAX_VALUE;
/* 360 */     for (BlockFace direction2 : new BlockFace[] { BlockFace.WEST, BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH }) {
/*     */       
/* 362 */       double d7 = (direction2 == BlockFace.WEST || direction2 == BlockFace.EAST) ? relativeXMovement : relativeZMovement;
/* 363 */       double d6 = (direction2 == BlockFace.EAST || direction2 == BlockFace.SOUTH) ? (1.0D - d7) : d7;
/*     */       
/* 365 */       switch (direction2) { case EAST: 
/*     */         case WEST: 
/*     */         case NORTH: 
/*     */         default:
/* 369 */           break; }  boolean doesSuffocate = suffocatesAt(player, blockX, blockZ + 1);
/*     */ 
/*     */       
/* 372 */       if (d6 < lowestValue && !doesSuffocate) {
/* 373 */         lowestValue = d6;
/* 374 */         direction = direction2;
/*     */       } 
/* 376 */     }  if (direction != null) {
/* 377 */       if (direction == BlockFace.WEST || direction == BlockFace.EAST) {
/* 378 */         player.uncertaintyHandler.xPositiveUncertainty += 0.15D;
/* 379 */         player.uncertaintyHandler.xNegativeUncertainty -= 0.15D;
/* 380 */         player.pointThreeEstimator.setPushing(true);
/*     */       } else {
/* 382 */         player.uncertaintyHandler.zPositiveUncertainty += 0.15D;
/* 383 */         player.uncertaintyHandler.zNegativeUncertainty -= 0.15D;
/* 384 */         player.pointThreeEstimator.setPushing(true);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public static void updateInWaterStateAndDoWaterCurrentPushing(GrimPlayer player) {
/* 390 */     PacketEntity riding = player.compensatedEntities.self.getRiding();
/* 391 */     player.wasWasTouchingWater = player.wasTouchingWater;
/* 392 */     player.wasTouchingWater = (updateFluidHeightAndDoFluidPushing(player, FluidTag.WATER, 0.014D) && (riding == null || !riding.isBoat));
/* 393 */     if (player.wasTouchingWater)
/* 394 */       player.fallDistance = 0.0D; 
/*     */   }
/*     */   
/*     */   private static boolean updateFluidHeightAndDoFluidPushing(GrimPlayer player, FluidTag tag, double multiplier) {
/* 398 */     if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_13)) {
/* 399 */       return updateFluidHeightAndDoFluidPushingModern(player, tag, multiplier);
/*     */     }
/*     */     
/* 402 */     return updateFluidHeightAndDoFluidPushingLegacy(player, tag, multiplier);
/*     */   }
/*     */   
/*     */   private static boolean updateFluidHeightAndDoFluidPushingLegacy(GrimPlayer player, FluidTag tag, double multiplier) {
/* 406 */     SimpleCollisionBox aABB = player.boundingBox.copy().expand(0.0D, -0.4D, 0.0D).expand(-0.001D);
/*     */     
/* 408 */     int floorX = GrimMath.floor(aABB.minX);
/* 409 */     int ceilX = GrimMath.ceil(aABB.maxX);
/* 410 */     int floorY = GrimMath.floor(aABB.minY);
/* 411 */     int ceilY = GrimMath.ceil(aABB.maxY);
/* 412 */     int floorZ = GrimMath.floor(aABB.minZ);
/* 413 */     int ceilZ = GrimMath.ceil(aABB.maxZ);
/* 414 */     if (CheckIfChunksLoaded.isChunksUnloadedAt(player, floorX, floorY, floorZ, ceilX, ceilY, ceilZ)) {
/* 415 */       return false;
/*     */     }
/*     */     
/* 418 */     boolean hasPushed = false;
/* 419 */     Vector3dm vec3 = new Vector3dm();
/*     */     
/* 421 */     for (int x = floorX; x < ceilX; x++) {
/* 422 */       for (int y = floorY; y < ceilY; y++) {
/* 423 */         for (int z = floorZ; z < ceilZ; z++) {
/*     */           double fluidHeight;
/* 425 */           if (tag == FluidTag.WATER) {
/* 426 */             fluidHeight = player.compensatedWorld.getWaterFluidLevelAt(x, y, z);
/*     */           } else {
/* 428 */             fluidHeight = player.compensatedWorld.getLavaFluidLevelAt(x, y, z);
/*     */           } 
/*     */           
/* 431 */           if (fluidHeight != 0.0D) {
/*     */ 
/*     */             
/* 434 */             double d0 = (y + 1) - fluidHeight;
/*     */             
/* 436 */             if (!player.isFlying && ceilY >= d0) {
/* 437 */               hasPushed = true;
/* 438 */               vec3.add(FluidTypeFlowing.getFlow(player, x, y, z));
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 445 */     if (tag == FluidTag.WATER && vec3.lengthSquared() > 0.0D) {
/* 446 */       vec3.normalize();
/* 447 */       vec3.multiply(multiplier);
/* 448 */       player.baseTickAddWaterPushing(vec3);
/* 449 */       player.baseTickAddVector(vec3);
/*     */     } 
/*     */     
/* 452 */     return hasPushed;
/*     */   }
/*     */   
/*     */   private static boolean updateFluidHeightAndDoFluidPushingModern(GrimPlayer player, FluidTag tag, double multiplier) {
/* 456 */     SimpleCollisionBox aABB = player.boundingBox.copy().expand(-0.001D);
/*     */     
/* 458 */     int floorX = GrimMath.floor(aABB.minX);
/* 459 */     int ceilX = GrimMath.ceil(aABB.maxX);
/* 460 */     int floorY = GrimMath.floor(aABB.minY);
/* 461 */     int ceilY = GrimMath.ceil(aABB.maxY);
/* 462 */     int floorZ = GrimMath.floor(aABB.minZ);
/* 463 */     int ceilZ = GrimMath.ceil(aABB.maxZ);
/* 464 */     if (CheckIfChunksLoaded.isChunksUnloadedAt(player, floorX, floorY, floorZ, ceilX, ceilY, ceilZ)) {
/* 465 */       return false;
/*     */     }
/* 467 */     double d2 = 0.0D;
/* 468 */     boolean hasTouched = false;
/* 469 */     Vector3dm vec3 = new Vector3dm();
/* 470 */     int n7 = 0;
/*     */     
/* 472 */     for (int x = floorX; x < ceilX; x++) {
/* 473 */       for (int y = floorY; y < ceilY; y++) {
/* 474 */         for (int z = floorZ; z < ceilZ; z++) {
/*     */           double fluidHeight;
/*     */ 
/*     */           
/* 478 */           if (tag == FluidTag.WATER) {
/* 479 */             fluidHeight = player.compensatedWorld.getWaterFluidLevelAt(x, y, z);
/*     */           } else {
/* 481 */             fluidHeight = player.compensatedWorld.getLavaFluidLevelAt(x, y, z);
/*     */           } 
/*     */           
/* 484 */           if (player.getClientVersion().isOlderThan(ClientVersion.V_1_14))
/* 485 */             fluidHeight = Math.min(fluidHeight, 0.8888888888888888D); 
/*     */           double fluidHeightToWorld;
/* 487 */           if (fluidHeight != 0.0D && (fluidHeightToWorld = y + fluidHeight) >= aABB.minY) {
/*     */ 
/*     */             
/* 490 */             hasTouched = true;
/* 491 */             d2 = Math.max(fluidHeightToWorld - aABB.minY, d2);
/*     */             
/* 493 */             if (!player.isFlying) {
/* 494 */               Vector3dm vec32 = FluidTypeFlowing.getFlow(player, x, y, z);
/* 495 */               if (d2 < 0.4D) {
/* 496 */                 vec32 = vec32.multiply(d2);
/*     */               }
/* 498 */               vec3 = vec3.add(vec32);
/* 499 */               n7++;
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 505 */     if (vec3.lengthSquared() > 0.0D) {
/* 506 */       if (n7 > 0) {
/* 507 */         vec3 = vec3.multiply(1.0D / n7);
/*     */       }
/*     */       
/* 510 */       if (player.inVehicle())
/*     */       {
/* 512 */         vec3 = vec3.normalize();
/*     */       }
/*     */ 
/*     */       
/* 516 */       if (tag != FluidTag.LAVA || player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_16)) {
/* 517 */         vec3 = vec3.multiply(multiplier);
/*     */ 
/*     */         
/* 520 */         player.baseTickAddWaterPushing(vec3);
/* 521 */         if (Math.abs(player.clientVelocity.getX()) < 0.003D && Math.abs(player.clientVelocity.getZ()) < 0.003D && vec3.length() < 0.0045000000000000005D) {
/* 522 */           vec3 = vec3.normalize().multiply(0.0045000000000000005D);
/*     */         }
/*     */         
/* 525 */         player.baseTickAddVector(vec3);
/*     */       } 
/*     */     } 
/*     */     
/* 529 */     if (tag == FluidTag.LAVA) {
/* 530 */       player.slightlyTouchingLava = (hasTouched && d2 <= 0.4D);
/*     */     }
/*     */     
/* 533 */     if (tag == FluidTag.WATER) {
/* 534 */       player.slightlyTouchingWater = (hasTouched && d2 <= 0.4D);
/*     */     }
/*     */     
/* 537 */     return hasTouched;
/*     */   }
/*     */   
/*     */   private static boolean suffocatesAt(GrimPlayer player, int x, int z) {
/* 541 */     SimpleCollisionBox axisAlignedBB = (new SimpleCollisionBox(x, player.boundingBox.minY, z, x + 1.0D, player.boundingBox.maxY, z + 1.0D, false)).expand(-1.0E-7D);
/* 542 */     return Collisions.suffocatesAt(player, axisAlignedBB);
/*     */   }
/*     */   
/*     */   private static boolean clearAbove(GrimPlayer player, int x, int y, int z) {
/* 546 */     return (!Collisions.doesBlockSuffocate(player, x, y, z) && !Collisions.doesBlockSuffocate(player, x, y + 1, z));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\predictionengine\PlayerBaseTick.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */