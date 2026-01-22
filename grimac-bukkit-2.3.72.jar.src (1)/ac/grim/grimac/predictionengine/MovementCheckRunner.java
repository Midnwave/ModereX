/*     */ package ac.grim.grimac.predictionengine;
/*     */ 
/*     */ import ac.grim.grimac.api.config.ConfigManager;
/*     */ import ac.grim.grimac.checks.Check;
/*     */ import ac.grim.grimac.checks.impl.prediction.Phase;
/*     */ import ac.grim.grimac.checks.impl.vehicle.VehicleC;
/*     */ import ac.grim.grimac.checks.type.PositionCheck;
/*     */ import ac.grim.grimac.manager.SetbackTeleportUtil;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.predictionengine.movementtick.MovementTickerCamel;
/*     */ import ac.grim.grimac.predictionengine.movementtick.MovementTickerHappyGhast;
/*     */ import ac.grim.grimac.predictionengine.movementtick.MovementTickerHorse;
/*     */ import ac.grim.grimac.predictionengine.movementtick.MovementTickerPig;
/*     */ import ac.grim.grimac.predictionengine.movementtick.MovementTickerPlayer;
/*     */ import ac.grim.grimac.predictionengine.movementtick.MovementTickerStrider;
/*     */ import ac.grim.grimac.predictionengine.predictions.PredictionEngineNormal;
/*     */ import ac.grim.grimac.predictionengine.predictions.rideable.PredictionEngineBoat;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*     */ import ac.grim.grimac.utils.anticheat.update.PositionUpdate;
/*     */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*     */ import ac.grim.grimac.utils.data.Pair;
/*     */ import ac.grim.grimac.utils.data.VectorData;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntity;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntityTrackXRot;
/*     */ import ac.grim.grimac.utils.enums.Pose;
/*     */ import ac.grim.grimac.utils.math.GrimMath;
/*     */ import ac.grim.grimac.utils.math.Vector3dm;
/*     */ import ac.grim.grimac.utils.math.VectorUtils;
/*     */ import ac.grim.grimac.utils.nmsutil.BoundingBoxSize;
/*     */ import ac.grim.grimac.utils.nmsutil.Collisions;
/*     */ import ac.grim.grimac.utils.nmsutil.GetBoundingBox;
/*     */ import ac.grim.grimac.utils.nmsutil.Riptide;
/*     */ 
/*     */ 
/*     */ public class MovementCheckRunner
/*     */   extends Check
/*     */   implements PositionCheck
/*     */ {
/*  51 */   public static double predictionNanos = 300000.0D;
/*     */   
/*  53 */   public static double longPredictionNanos = 300000.0D;
/*     */   private boolean allowSprintJumpingWithElytra = true;
/*     */   
/*     */   public MovementCheckRunner(GrimPlayer player) {
/*  57 */     super(player);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void processAndCheckMovementPacket(PositionUpdate data) {
/*  65 */     if (this.player.getSetbackTeleportUtil().insideUnloadedChunk()) {
/*     */ 
/*     */ 
/*     */       
/*  69 */       boolean invalidVehicle = (this.player.inVehicle() && (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_9) || this.player.getClientVersion().isOlderThan(ClientVersion.V_1_9)));
/*     */       
/*  71 */       if (!invalidVehicle && !data.isTeleport())
/*     */       {
/*  73 */         this.player.getSetbackTeleportUtil().executeForceResync();
/*     */       }
/*     */     } 
/*     */     
/*  77 */     long start = System.nanoTime();
/*  78 */     check(data);
/*  79 */     long length = System.nanoTime() - start;
/*     */     
/*  81 */     if (!this.player.disableGrim) {
/*  82 */       predictionNanos = predictionNanos * 499.0D / 500.0D + length / 500.0D;
/*  83 */       longPredictionNanos = longPredictionNanos * 19999.0D / 20000.0D + length / 20000.0D;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void handleTeleport(PositionUpdate update) {
/*  88 */     this.player.lastX = this.player.x;
/*  89 */     this.player.lastY = this.player.y;
/*  90 */     this.player.lastZ = this.player.z;
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
/* 101 */     if (!this.player.inVehicle()) {
/* 102 */       if (update.getTeleportData() == null) {
/* 103 */         this.player.clientVelocity.setX(0);
/* 104 */         this.player.clientVelocity.setY(0);
/* 105 */         this.player.clientVelocity.setZ(0);
/* 106 */         this.player.lastWasClimbing = 0.0D;
/* 107 */         this.player.canSwimHop = false;
/*     */       } else {
/* 109 */         update.getTeleportData().modifyVector(this.player, this.player.clientVelocity);
/*     */       } 
/*     */     }
/*     */     
/* 113 */     this.player.uncertaintyHandler.lastTeleportTicks.reset();
/*     */ 
/*     */     
/* 116 */     this.player.checkManager.getExplosionHandler().forceExempt();
/* 117 */     this.player.checkManager.getKnockbackHandler().forceExempt();
/*     */     
/* 119 */     this.player.boundingBox = GetBoundingBox.getCollisionBoxForPlayer(this.player, this.player.x, this.player.y, this.player.z);
/*     */ 
/*     */     
/* 122 */     PredictionComplete predictionComplete = new PredictionComplete(0.0D, update, true);
/* 123 */     this.player.getSetbackTeleportUtil().onPredictionComplete(predictionComplete);
/* 124 */     ((Phase)this.player.checkManager.getPostPredictionCheck(Phase.class)).onPredictionComplete(predictionComplete);
/*     */     
/* 126 */     this.player.uncertaintyHandler.lastHorizontalOffset = 0.0D;
/* 127 */     this.player.uncertaintyHandler.lastVerticalOffset = 0.0D;
/*     */   }
/*     */   
/*     */   private void check(PositionUpdate update) {
/* 131 */     if (update.isTeleport()) {
/* 132 */       handleTeleport(update);
/*     */       
/*     */       return;
/*     */     } 
/* 136 */     this.player.movementPackets++;
/*     */     
/* 138 */     this.player.onGround = update.isOnGround();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 143 */     if (!this.player.isFlying && this.player.isSneaking && Collisions.isAboveGround(this.player)) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 148 */       double posX = Math.max(0.05D, GrimMath.clamp(this.player.actualMovement.getX(), -16.0D, 16.0D) + 0.05D);
/* 149 */       double posZ = Math.max(0.05D, GrimMath.clamp(this.player.actualMovement.getZ(), -16.0D, 16.0D) + 0.05D);
/* 150 */       double negX = Math.min(-0.05D, GrimMath.clamp(this.player.actualMovement.getX(), -16.0D, 16.0D) - 0.05D);
/* 151 */       double negZ = Math.min(-0.05D, GrimMath.clamp(this.player.actualMovement.getZ(), -16.0D, 16.0D) - 0.05D);
/*     */       
/* 153 */       Vector3dm NE = Collisions.maybeBackOffFromEdge(new Vector3dm(posX, 0.0D, negZ), this.player, true);
/* 154 */       Vector3dm NW = Collisions.maybeBackOffFromEdge(new Vector3dm(negX, 0.0D, negZ), this.player, true);
/* 155 */       Vector3dm SE = Collisions.maybeBackOffFromEdge(new Vector3dm(posX, 0.0D, posZ), this.player, true);
/* 156 */       Vector3dm SW = Collisions.maybeBackOffFromEdge(new Vector3dm(negX, 0.0D, posZ), this.player, true);
/*     */       
/* 158 */       boolean isEast = (NE.getX() != posX || SE.getX() != posX);
/* 159 */       boolean isWest = (NW.getX() != negX || SW.getX() != negX);
/* 160 */       boolean isNorth = (NE.getZ() != negZ || NW.getZ() != negZ);
/* 161 */       boolean isSouth = (SE.getZ() != posZ || SW.getZ() != posZ);
/*     */       
/* 163 */       if (isEast) this.player.uncertaintyHandler.lastStuckEast.reset(); 
/* 164 */       if (isWest) this.player.uncertaintyHandler.lastStuckWest.reset(); 
/* 165 */       if (isNorth) this.player.uncertaintyHandler.lastStuckNorth.reset(); 
/* 166 */       if (isSouth) this.player.uncertaintyHandler.lastStuckSouth.reset();
/*     */       
/* 168 */       if (isEast || isWest || isSouth || isNorth) {
/* 169 */         this.player.uncertaintyHandler.stuckOnEdge.reset();
/*     */       }
/*     */     } 
/*     */     
/* 173 */     this.player.compensatedWorld.tickPlayerInPistonPushingArea();
/* 174 */     this.player.compensatedEntities.tick();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 183 */     if (this.player.vehicleData.wasVehicleSwitch || this.player.vehicleData.lastDummy) {
/* 184 */       this.player.uncertaintyHandler.lastVehicleSwitch.reset();
/*     */     }
/*     */     
/* 187 */     if (this.player.vehicleData.lastDummy) {
/* 188 */       this.player.clientVelocity.multiply(0.98D);
/*     */     }
/*     */     
/* 191 */     PacketEntity riding = this.player.compensatedEntities.self.getRiding();
/* 192 */     if (this.player.vehicleData.wasVehicleSwitch || this.player.vehicleData.lastDummy) {
/* 193 */       update.setTeleport(true);
/*     */       
/* 195 */       this.player.vehicleData.lastDummy = false;
/* 196 */       this.player.vehicleData.wasVehicleSwitch = false;
/*     */       
/* 198 */       if (riding != null) {
/* 199 */         Vector3dm pos = new Vector3dm(this.player.x, this.player.y, this.player.z);
/* 200 */         SimpleCollisionBox interTruePositions = riding.getPossibleCollisionBoxes();
/*     */ 
/*     */         
/* 203 */         float scale = (float)riding.getAttributeValue(Attributes.SCALE);
/* 204 */         float width = BoundingBoxSize.getWidth(this.player, riding) * scale;
/* 205 */         float height = BoundingBoxSize.getHeight(this.player, riding) * scale;
/* 206 */         interTruePositions.expand(-width, 0.0D, -width);
/* 207 */         interTruePositions.expandMax(0.0D, -height, 0.0D);
/*     */         
/* 209 */         Vector3dm cutTo = VectorUtils.cutBoxToVector(pos, interTruePositions);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 219 */         this.player.lastX = cutTo.getX();
/* 220 */         this.player.lastY = cutTo.getY();
/* 221 */         this.player.lastZ = cutTo.getZ();
/*     */         
/* 223 */         this.player.boundingBox = GetBoundingBox.getCollisionBoxForPlayer(this.player, this.player.lastX, this.player.lastY, this.player.lastZ);
/*     */       }
/*     */       else {
/*     */         
/* 227 */         if ((new Vector3dm(this.player.lastX, this.player.lastY, this.player.lastZ)).distance(new Vector3dm(this.player.x, this.player.y, this.player.z)) > 3.0D) {
/* 228 */           this.player.getSetbackTeleportUtil().executeForceResync();
/*     */         }
/*     */         
/* 231 */         handleTeleport(update);
/*     */         
/* 233 */         if (this.player.isClimbing) {
/* 234 */           Vector3dm ladder = this.player.clientVelocity.clone().setY(0.2D);
/* 235 */           PredictionEngineNormal.staticVectorEndOfTick(this.player, ladder);
/* 236 */           this.player.lastWasClimbing = ladder.getY();
/*     */         } 
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/* 242 */     if (this.player.isInBed != this.player.lastInBed) {
/* 243 */       update.setTeleport(true);
/*     */     }
/* 245 */     this.player.lastInBed = this.player.isInBed;
/*     */ 
/*     */     
/* 248 */     if (this.player.isInBed)
/*     */       return; 
/* 250 */     if (!this.player.inVehicle()) {
/* 251 */       this.player.speed = this.player.compensatedEntities.self.getAttributeValue(Attributes.MOVEMENT_SPEED);
/* 252 */       if (this.player.hasGravity != this.player.playerEntityHasGravity) {
/* 253 */         this.player.pointThreeEstimator.updatePlayerGravity();
/*     */       }
/* 255 */       this.player.hasGravity = this.player.playerEntityHasGravity;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 266 */     if (this.player.inVehicle()) {
/*     */       
/* 268 */       this.player.checkManager.getExplosionHandler().forceExempt();
/*     */ 
/*     */       
/* 271 */       riding.setPositionRaw(this.player, new SimpleCollisionBox(this.player.x, this.player.y, this.player.z, this.player.x, this.player.y, this.player.z));
/*     */       
/* 273 */       if (riding instanceof PacketEntityTrackXRot) { PacketEntityTrackXRot boat = (PacketEntityTrackXRot)riding;
/* 274 */         boat.packetYaw = this.player.xRot;
/* 275 */         boat.interpYaw = this.player.xRot;
/* 276 */         boat.steps = 0; }
/*     */ 
/*     */       
/* 279 */       if (this.player.hasGravity != riding.hasGravity) {
/* 280 */         this.player.pointThreeEstimator.updatePlayerGravity();
/*     */       }
/* 282 */       this.player.hasGravity = riding.hasGravity;
/*     */ 
/*     */       
/* 285 */       if (riding instanceof ac.grim.grimac.utils.data.packetentity.PacketEntityRideable) {
/* 286 */         VehicleC vehicleC = (VehicleC)this.player.checkManager.getCheck(VehicleC.class);
/*     */         
/* 288 */         ItemType requiredItem = (riding.type == EntityTypes.PIG) ? ItemTypes.CARROT_ON_A_STICK : ItemTypes.WARPED_FUNGUS_ON_A_STICK;
/* 289 */         ItemStack mainHand = this.player.inventory.getHeldItem();
/* 290 */         ItemStack offHand = this.player.inventory.getOffHand();
/*     */         
/* 292 */         boolean correctMainHand = (mainHand.getType() == requiredItem);
/* 293 */         boolean correctOffhand = (offHand.getType() == requiredItem);
/*     */         
/* 295 */         if (!correctMainHand && !correctOffhand) {
/*     */           
/* 297 */           vehicleC.flagAndAlert();
/*     */         } else {
/* 299 */           vehicleC.reward();
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 304 */     if (this.player.isFlying) {
/* 305 */       this.player.fallDistance = 0.0D;
/* 306 */       this.player.uncertaintyHandler.lastFlyingTicks.reset();
/*     */     } 
/*     */     
/* 309 */     this.player.isClimbing = Collisions.onClimbable(this.player, this.player.lastX, this.player.lastY, this.player.lastZ);
/*     */     
/* 311 */     this.player.clientControlledVerticalCollision = (Math.abs(this.player.y % 0.015625D) < 1.0E-5D);
/*     */ 
/*     */     
/* 314 */     this.player.actualMovement = new Vector3dm(this.player.x - this.player.lastX, this.player.y - this.player.lastY, this.player.z - this.player.lastZ);
/*     */     
/* 316 */     if (this.player.isSprinting != this.player.lastSprinting) {
/* 317 */       this.player.compensatedEntities.hasSprintingAttributeEnabled = this.player.isSprinting;
/*     */     }
/*     */     
/* 320 */     this.player.lastJumping = this.player.isJumping;
/* 321 */     this.player.isJumping = this.player.packetStateData.knownInput.jump();
/*     */     
/* 323 */     boolean oldFlying = this.player.isFlying;
/* 324 */     boolean oldGliding = this.player.isGliding;
/* 325 */     boolean oldSprinting = this.player.isSprinting;
/* 326 */     boolean oldSneaking = this.player.isSneaking;
/*     */ 
/*     */ 
/*     */     
/* 330 */     if (this.player.inVehicle()) {
/*     */ 
/*     */       
/* 333 */       this.player.isFlying = false;
/* 334 */       this.player.isGliding = false;
/* 335 */       this.player.isSprinting &= riding instanceof ac.grim.grimac.utils.data.packetentity.PacketEntityCamel;
/* 336 */       this.player.isSneaking = false;
/*     */       
/* 338 */       if (riding.type != EntityTypes.PIG && riding.type != EntityTypes.STRIDER) {
/* 339 */         this.player.isClimbing = false;
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 349 */     if (!this.player.inVehicle()) {
/* 350 */       this.player.speed += this.player.compensatedEntities.hasSprintingAttributeEnabled ? (this.player.speed * 0.30000001192092896D) : 0.0D;
/*     */     }
/*     */     
/* 353 */     boolean clientClaimsRiptide = this.player.packetStateData.tryingToRiptide;
/* 354 */     if (this.player.packetStateData.tryingToRiptide) {
/* 355 */       long currentTime = System.currentTimeMillis();
/* 356 */       boolean isInWater = this.player.isInWaterOrRain();
/*     */       
/* 358 */       if (currentTime - this.player.packetStateData.lastRiptide < 450L || !isInWater) {
/* 359 */         this.player.packetStateData.tryingToRiptide = false;
/*     */       }
/*     */       
/* 362 */       this.player.packetStateData.lastRiptide = currentTime;
/*     */     } 
/*     */     
/* 365 */     SimpleCollisionBox steppingOnBB = GetBoundingBox.getCollisionBoxForPlayer(this.player, this.player.x, this.player.y, this.player.z).expand(this.player.getMovementThreshold()).offset(0.0D, -1.0D, 0.0D);
/* 366 */     Collisions.hasMaterial(this.player, steppingOnBB, pair -> {
/*     */           WrappedBlockState data = (WrappedBlockState)pair.first();
/*     */           
/*     */           if (data.getType() == StateTypes.SLIME_BLOCK && this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_8)) {
/*     */             this.player.uncertaintyHandler.isSteppingOnSlime = true;
/*     */             
/*     */             this.player.uncertaintyHandler.isSteppingOnBouncyBlock = true;
/*     */           } 
/*     */           if (data.getType() == StateTypes.HONEY_BLOCK) {
/*     */             if (this.player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_14) && this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_8)) {
/*     */               this.player.uncertaintyHandler.isSteppingOnBouncyBlock = true;
/*     */             }
/*     */             this.player.uncertaintyHandler.isSteppingOnHoney = true;
/*     */           } 
/*     */           if (BlockTags.BEDS.contains(data.getType()) && this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_12)) {
/*     */             this.player.uncertaintyHandler.isSteppingOnBouncyBlock = true;
/*     */           }
/*     */           if (BlockTags.ICE.contains(data.getType())) {
/*     */             this.player.uncertaintyHandler.isSteppingOnIce = true;
/*     */           }
/*     */           if (data.getType() == StateTypes.BUBBLE_COLUMN && this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_13)) {
/*     */             this.player.uncertaintyHandler.isSteppingNearBubbleColumn = true;
/*     */           }
/*     */           if (data.getType() == StateTypes.SCAFFOLDING) {
/*     */             this.player.uncertaintyHandler.isSteppingNearScaffolding = true;
/*     */           }
/*     */           return false;
/*     */         });
/* 394 */     this.player.uncertaintyHandler.thisTickSlimeBlockUncertainty = this.player.uncertaintyHandler.nextTickSlimeBlockUncertainty;
/* 395 */     this.player.uncertaintyHandler.nextTickSlimeBlockUncertainty = 0.0D;
/*     */     
/* 397 */     SimpleCollisionBox expandedBB = GetBoundingBox.getBoundingBoxFromPosAndSize(this.player, this.player.lastX, this.player.lastY, this.player.lastZ, 0.001F, 0.001F);
/*     */ 
/*     */     
/* 400 */     if (this.player.actualMovement.lengthSquared() < 2500.0D) {
/* 401 */       expandedBB.expandToAbsoluteCoordinates(this.player.x, this.player.y, this.player.z);
/*     */     }
/* 403 */     expandedBB.expand((Pose.STANDING.width / 2.0F), 0.0D, (Pose.STANDING.width / 2.0F));
/* 404 */     expandedBB.expandMax(0.0D, Pose.STANDING.height, 0.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 409 */     boolean isGlitchy = this.player.uncertaintyHandler.isNearGlitchyBlock;
/*     */     
/* 411 */     this.player.uncertaintyHandler
/* 412 */       .isNearGlitchyBlock = (this.player.getClientVersion().isOlderThan(ClientVersion.V_1_9) && Collisions.hasMaterial(this.player, expandedBB.copy().expand(0.2D), checkData -> 
/* 413 */         (BlockTags.ANVIL.contains(((WrappedBlockState)checkData.first()).getType()) || ((WrappedBlockState)checkData.first()).getType() == StateTypes.CHEST || ((WrappedBlockState)checkData.first()).getType() == StateTypes.TRAPPED_CHEST)));
/*     */ 
/*     */     
/* 416 */     this.player.uncertaintyHandler.isOrWasNearGlitchyBlock = (isGlitchy || this.player.uncertaintyHandler.isNearGlitchyBlock);
/* 417 */     this.player.uncertaintyHandler.checkForHardCollision();
/*     */     
/* 419 */     if (this.player.isFlying != this.player.wasFlying) {
/* 420 */       this.player.uncertaintyHandler.lastFlyingStatusChange.reset();
/*     */     }
/* 422 */     if (!this.player.inVehicle() && (Math.abs(this.player.x) == 2.9999999E7D || Math.abs(this.player.z) == 2.9999999E7D)) {
/* 423 */       this.player.uncertaintyHandler.lastThirtyMillionHardBorder.reset();
/*     */     }
/*     */     
/* 426 */     if (this.player.isFlying && this.player.getClientVersion().isOlderThan(ClientVersion.V_1_13) && this.player.compensatedWorld.containsLiquid(this.player.boundingBox)) {
/* 427 */       this.player.uncertaintyHandler.lastUnderwaterFlyingHack.reset();
/*     */     }
/*     */     
/* 430 */     boolean couldBeStuckSpeed = Collisions.checkStuckSpeed(this.player, this.player.getMovementThreshold());
/* 431 */     boolean couldLeaveStuckSpeed = (this.player.isPointThree() && Collisions.checkStuckSpeed(this.player, -this.player.getMovementThreshold()));
/* 432 */     this.player.uncertaintyHandler.claimingLeftStuckSpeed = (!this.player.inVehicle() && this.player.stuckSpeedMultiplier.getX() < 1.0D && !couldLeaveStuckSpeed);
/*     */     
/* 434 */     if (couldBeStuckSpeed) {
/* 435 */       this.player.uncertaintyHandler.lastStuckSpeedMultiplier.reset();
/*     */     }
/*     */     
/* 438 */     this.player.startTickClientVel = this.player.clientVelocity;
/*     */     
/* 440 */     boolean wasChecked = false;
/*     */ 
/*     */     
/* 443 */     if (this.player.compensatedEntities.self.isDead || (riding != null && riding.isDead)) {
/*     */       
/* 445 */       this.player.predictedVelocity = new VectorData(new Vector3dm(), VectorData.VectorType.Dead);
/* 446 */       this.player.clientVelocity = new Vector3dm();
/* 447 */     } else if (this.player.disableGrim || (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_8) && this.player.gamemode == GameMode.SPECTATOR) || this.player.isFlying || (this.player.isExemptElytra() && this.player.isGliding)) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 452 */       this.player.predictedVelocity = new VectorData(this.player.actualMovement, VectorData.VectorType.Spectator);
/* 453 */       this.player.clientVelocity = this.player.actualMovement.clone();
/* 454 */       this.player.gravity = 0.0D;
/* 455 */       this.player.friction = 0.91F;
/* 456 */       PredictionEngineNormal.staticVectorEndOfTick(this.player, this.player.clientVelocity);
/* 457 */     } else if (riding == null) {
/* 458 */       wasChecked = true;
/*     */       
/* 460 */       this.player.depthStriderLevel = (float)this.player.compensatedEntities.self.getAttributeValue(Attributes.WATER_MOVEMENT_EFFICIENCY);
/* 461 */       this.player.sneakingSpeedMultiplier = (float)this.player.compensatedEntities.self.getAttributeValue(Attributes.SNEAKING_SPEED);
/*     */ 
/*     */       
/* 464 */       this.player.verticalCollision = false;
/*     */ 
/*     */ 
/*     */       
/* 468 */       if (this.player.lastOnGround && this.player.packetStateData.tryingToRiptide && !this.player.inVehicle()) {
/* 469 */         Vector3dm pushingMovement = Collisions.collide(this.player, 0.0D, 1.1999999284744263D, 0.0D);
/* 470 */         this.player.verticalCollision = (pushingMovement.getY() != 1.1999999284744263D);
/* 471 */         double currentY = this.player.clientVelocity.getY();
/*     */         
/* 473 */         if (likelyGroundRiptide(pushingMovement)) {
/* 474 */           this.player.uncertaintyHandler.thisTickSlimeBlockUncertainty = Math.abs(Riptide.getRiptideVelocity(this.player).getY()) + ((currentY > 0.0D) ? currentY : 0.0D);
/* 475 */           this.player.uncertaintyHandler.nextTickSlimeBlockUncertainty = Math.abs(Riptide.getRiptideVelocity(this.player).getY()) + ((currentY > 0.0D) ? currentY : 0.0D);
/*     */           
/* 477 */           this.player.lastOnGround = false;
/* 478 */           this.player.lastY += pushingMovement.getY();
/* 479 */           PlayerBaseTick.updatePlayerPose(this.player);
/* 480 */           this.player.boundingBox = GetBoundingBox.getPlayerBoundingBox(this.player, this.player.lastX, this.player.lastY, this.player.lastZ);
/* 481 */           this.player.actualMovement = new Vector3dm(this.player.x - this.player.lastX, this.player.y - this.player.lastY, this.player.z - this.player.lastZ);
/*     */           
/* 483 */           this.player.couldSkipTick = true;
/*     */           
/* 485 */           Collisions.handleInsideBlocks(this.player);
/*     */         } 
/*     */       } 
/*     */       
/* 489 */       PlayerBaseTick.doBaseTick(this.player);
/* 490 */       (new MovementTickerPlayer(this.player)).livingEntityAIStep();
/* 491 */       PlayerBaseTick.updatePowderSnow(this.player);
/* 492 */       PlayerBaseTick.updatePlayerPose(this.player);
/* 493 */     } else if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_9) && this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
/* 494 */       wasChecked = true;
/*     */ 
/*     */ 
/*     */       
/* 498 */       if (riding.isBoat) {
/* 499 */         PlayerBaseTick.doBaseTick(this.player);
/*     */         
/* 501 */         (new PredictionEngineBoat(this.player)).guessBestMovement(0.1F, this.player);
/* 502 */       } else if (riding instanceof ac.grim.grimac.utils.data.packetentity.PacketEntityCamel) {
/* 503 */         PlayerBaseTick.doBaseTick(this.player);
/* 504 */         (new MovementTickerCamel(this.player)).livingEntityAIStep();
/* 505 */       } else if (riding instanceof ac.grim.grimac.utils.data.packetentity.PacketEntityHappyGhast) {
/* 506 */         PlayerBaseTick.doBaseTick(this.player);
/* 507 */         (new MovementTickerHappyGhast(this.player)).livingEntityAIStep();
/* 508 */       } else if (riding instanceof ac.grim.grimac.utils.data.packetentity.PacketEntityHorse) {
/* 509 */         PlayerBaseTick.doBaseTick(this.player);
/* 510 */         (new MovementTickerHorse(this.player)).livingEntityAIStep();
/* 511 */       } else if (riding.type == EntityTypes.PIG) {
/* 512 */         PlayerBaseTick.doBaseTick(this.player);
/* 513 */         (new MovementTickerPig(this.player)).livingEntityAIStep();
/* 514 */       } else if (riding.type == EntityTypes.STRIDER) {
/* 515 */         PlayerBaseTick.doBaseTick(this.player);
/* 516 */         (new MovementTickerStrider(this.player)).livingEntityAIStep();
/* 517 */         MovementTickerStrider.floatStrider(this.player);
/* 518 */         Collisions.handleInsideBlocks(this.player);
/*     */       } else {
/* 520 */         wasChecked = false;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 525 */     double offset = this.player.predictedVelocity.vector.distance(this.player.actualMovement);
/* 526 */     offset = this.player.uncertaintyHandler.reduceOffset(offset);
/*     */     
/* 528 */     if (this.player.packetStateData.tryingToRiptide != clientClaimsRiptide) {
/* 529 */       this.player.getSetbackTeleportUtil().executeForceResync();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 537 */     if (this.player.getSetbackTeleportUtil().getRequiredSetBack() != null && this.player.getSetbackTeleportUtil().getRequiredSetBack().getTicksComplete() == 1) {
/* 538 */       Vector3dm setbackVel = this.player.getSetbackTeleportUtil().getRequiredSetBack().getVelocity();
/*     */ 
/*     */ 
/*     */       
/* 542 */       if (this.player.predictedVelocity.isJump() && !this.player.wasTouchingLava && !this.player.wasTouchingWater && ((setbackVel != null && setbackVel
/*     */         
/* 544 */         .getY() >= 0.0D) || !Collisions.slowCouldPointThreeHitGround(this.player, this.player.lastX, this.player.lastY, this.player.lastZ))) {
/* 545 */         this.player.getSetbackTeleportUtil().executeForceResync();
/*     */       }
/*     */       
/* 548 */       boolean lavaBugFix = (this.player.wasTouchingLava && this.player.predictedVelocity.isJump() && this.player.predictedVelocity.vector.getY() < 0.06D && this.player.predictedVelocity.vector.getY() > -0.02D);
/*     */       
/* 550 */       if (!this.player.predictedVelocity.isKnockback() && !lavaBugFix && this.player.getSetbackTeleportUtil().getRequiredSetBack().getVelocity() != null)
/*     */       {
/* 552 */         this.player.getSetbackTeleportUtil().executeForceResync();
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 557 */     if ((this.player.getSetbackTeleportUtil()).blockOffsets) offset = 0.0D;
/*     */     
/* 559 */     if (this.player.skippedTickInActualMovement || !wasChecked) {
/* 560 */       this.player.uncertaintyHandler.lastPointThree.reset();
/*     */     }
/*     */     
/* 563 */     this.player.checkManager.onPredictionFinish(new PredictionComplete(offset, update, wasChecked));
/*     */     
/* 565 */     this.player.wasLastPredictionCompleteChecked = wasChecked;
/*     */ 
/*     */     
/* 568 */     if (this.player.platformPlayer != null && this.player.isGliding && this.player.predictedVelocity.isJump() && this.player.isSprinting && !this.allowSprintJumpingWithElytra) {
/* 569 */       SetbackTeleportUtil.SetbackPosWithVector lastKnownGoodPosition = (this.player.getSetbackTeleportUtil()).lastKnownGoodPosition;
/* 570 */       lastKnownGoodPosition.setVector(lastKnownGoodPosition.getVector().multiply(new Vector3dm(0.546D, 1.0D, 0.546D)));
/* 571 */       this.player.getSetbackTeleportUtil().executeNonSimulatingSetback();
/*     */     } 
/*     */     
/* 574 */     if (!wasChecked) {
/*     */       
/* 576 */       this.player.checkManager.getExplosionHandler().forceExempt();
/* 577 */       this.player.checkManager.getKnockbackHandler().forceExempt();
/*     */     } 
/*     */     
/* 580 */     this.player.lastOnGround = this.player.onGround;
/* 581 */     this.player.lastSprinting = this.player.isSprinting;
/* 582 */     this.player.lastSprintingForSpeed = this.player.isSprinting;
/* 583 */     this.player.wasFlying = this.player.isFlying;
/* 584 */     this.player.wasGliding = this.player.isGliding;
/* 585 */     this.player.wasSwimming = this.player.isSwimming;
/* 586 */     this.player.wasSneaking = this.player.isSneaking;
/* 587 */     this.player.packetStateData.tryingToRiptide = false;
/*     */ 
/*     */     
/* 590 */     if (this.player.inVehicle()) {
/* 591 */       this.player.isFlying = oldFlying;
/* 592 */       this.player.isGliding = oldGliding;
/* 593 */       this.player.isSprinting = oldSprinting;
/* 594 */       this.player.isSneaking = oldSneaking;
/*     */     } 
/*     */     
/* 597 */     this.player.riptideSpinAttackTicks--;
/* 598 */     if (this.player.predictedVelocity.isTrident()) {
/* 599 */       this.player.riptideSpinAttackTicks = 20;
/*     */     }
/* 601 */     this.player.uncertaintyHandler.lastMovementWasZeroPointZeroThree = (!this.player.inVehicle() && this.player.skippedTickInActualMovement);
/* 602 */     this.player.uncertaintyHandler.lastMovementWasUnknown003VectorReset = (!this.player.inVehicle() && this.player.couldSkipTick && this.player.predictedVelocity.isKnockback());
/* 603 */     this.player.couldSkipTick = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 609 */     this.player.uncertaintyHandler
/*     */       
/* 611 */       .wasZeroPointThreeVertically = (!this.player.inVehicle() && ((this.player.uncertaintyHandler.lastMovementWasZeroPointZeroThree && this.player.pointThreeEstimator.controlsVerticalMovement()) || !this.player.pointThreeEstimator.canPredictNextVerticalMovement() || !this.player.pointThreeEstimator.isWasAlwaysCertain()));
/*     */     
/* 613 */     this.player.uncertaintyHandler.lastPacketWasGroundPacket = this.player.uncertaintyHandler.onGroundUncertain;
/* 614 */     this.player.uncertaintyHandler.onGroundUncertain = false;
/*     */     
/* 616 */     this.player.vehicleData.vehicleForward = (float)Math.min(0.98D, Math.max(-0.98D, this.player.vehicleData.nextVehicleForward));
/* 617 */     this.player.vehicleData.vehicleHorizontal = (float)Math.min(0.98D, Math.max(-0.98D, this.player.vehicleData.nextVehicleHorizontal));
/* 618 */     if (this.player.onGround) {
/* 619 */       this.player.vehicleData.horseJump = this.player.vehicleData.nextHorseJump;
/* 620 */       this.player.vehicleData.nextHorseJump = 0.0F;
/*     */     } 
/*     */     
/* 623 */     this.player.vehicleData.camelDashCooldown = Math.max(0, this.player.vehicleData.camelDashCooldown - 1);
/*     */     
/* 625 */     this.player.minAttackSlow = 0;
/* 626 */     this.player.maxAttackSlow = 0;
/*     */     
/* 628 */     this.player.likelyKB = null;
/* 629 */     this.player.firstBreadKB = null;
/* 630 */     this.player.firstBreadExplosion = null;
/* 631 */     this.player.likelyExplosions = null;
/*     */     
/* 633 */     this.player.trigHandler.setOffset(offset);
/* 634 */     this.player.pointThreeEstimator.endOfTickTick();
/*     */   }
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
/*     */   private boolean likelyGroundRiptide(Vector3dm pushingMovement) {
/* 652 */     double riptideYResult = Riptide.getRiptideVelocity(this.player).getY();
/*     */     
/* 654 */     double riptideDiffToBase = Math.abs(this.player.actualMovement.getY() - riptideYResult);
/* 655 */     double riptideDiffToGround = Math.abs(this.player.actualMovement.getY() - riptideYResult - pushingMovement.getY());
/*     */ 
/*     */ 
/*     */     
/* 659 */     return (riptideDiffToGround < riptideDiffToBase);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onReload(ConfigManager config) {
/* 664 */     this.allowSprintJumpingWithElytra = config.getBooleanElse("exploit.allow-sprint-jumping-when-using-elytra", true);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\predictionengine\MovementCheckRunner.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */