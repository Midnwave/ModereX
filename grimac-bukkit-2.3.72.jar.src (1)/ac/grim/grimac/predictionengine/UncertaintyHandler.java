/*     */ package ac.grim.grimac.predictionengine;
/*     */ 
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*     */ import ac.grim.grimac.shaded.fastutil.objects.ObjectIterator;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*     */ import ac.grim.grimac.utils.data.LastInstance;
/*     */ import ac.grim.grimac.utils.data.VectorData;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntity;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntityRideable;
/*     */ import ac.grim.grimac.utils.lists.EvictingQueue;
/*     */ import ac.grim.grimac.utils.math.Vector3dm;
/*     */ import ac.grim.grimac.utils.nmsutil.BoundingBoxSize;
/*     */ import ac.grim.grimac.utils.nmsutil.ReachUtils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ public class UncertaintyHandler
/*     */ {
/*     */   private final GrimPlayer player;
/*  26 */   public EvictingQueue<Double> pistonX = new EvictingQueue(5);
/*  27 */   public EvictingQueue<Double> pistonY = new EvictingQueue(5);
/*  28 */   public EvictingQueue<Double> pistonZ = new EvictingQueue(5);
/*     */ 
/*     */   
/*     */   public boolean isStepMovement;
/*     */ 
/*     */   
/*     */   public HashSet<BlockFace> slimePistonBounces;
/*     */ 
/*     */   
/*  37 */   public double xNegativeUncertainty = 0.0D;
/*  38 */   public double xPositiveUncertainty = 0.0D;
/*  39 */   public double zNegativeUncertainty = 0.0D;
/*  40 */   public double zPositiveUncertainty = 0.0D;
/*  41 */   public double yNegativeUncertainty = 0.0D;
/*  42 */   public double yPositiveUncertainty = 0.0D;
/*     */   
/*  44 */   public double thisTickSlimeBlockUncertainty = 0.0D;
/*  45 */   public double nextTickSlimeBlockUncertainty = 0.0D;
/*     */   
/*     */   public boolean onGroundUncertain = false;
/*     */   
/*     */   public boolean lastPacketWasGroundPacket = false;
/*     */   
/*     */   public boolean isSteppingOnSlime = false;
/*     */   
/*     */   public boolean isSteppingOnIce = false;
/*     */   
/*     */   public boolean isSteppingOnHoney = false;
/*     */   
/*     */   public boolean wasSteppingOnBouncyBlock = false;
/*     */   
/*     */   public boolean isSteppingOnBouncyBlock = false;
/*     */   
/*     */   public boolean isSteppingNearBubbleColumn = false;
/*     */   public boolean isSteppingNearScaffolding = false;
/*     */   public boolean isSteppingNearShulker = false;
/*     */   public boolean isNearGlitchyBlock = false;
/*     */   public boolean isOrWasNearGlitchyBlock = false;
/*     */   public boolean claimingLeftStuckSpeed = false;
/*     */   public boolean lastMovementWasZeroPointZeroThree = false;
/*     */   public boolean lastMovementWasUnknown003VectorReset = false;
/*     */   public boolean wasZeroPointThreeVertically = false;
/*  70 */   public EvictingQueue<Integer> collidingEntities = new EvictingQueue(3);
/*     */   
/*  72 */   public EvictingQueue<Integer> riptideEntities = new EvictingQueue(3);
/*     */   
/*  74 */   public List<Integer> fishingRodPulls = new ArrayList<>();
/*  75 */   public SimpleCollisionBox fireworksBox = null;
/*  76 */   public SimpleCollisionBox fishingRodPullBox = null;
/*     */   
/*     */   public LastInstance lastFlyingTicks;
/*     */   public LastInstance lastFlyingStatusChange;
/*     */   public LastInstance lastUnderwaterFlyingHack;
/*     */   public LastInstance lastStuckSpeedMultiplier;
/*     */   public LastInstance lastHardCollidingLerpingEntity;
/*     */   public LastInstance lastThirtyMillionHardBorder;
/*     */   public LastInstance lastTeleportTicks;
/*     */   public LastInstance lastPointThree;
/*     */   public LastInstance stuckOnEdge;
/*     */   public LastInstance lastStuckNorth;
/*     */   public LastInstance lastStuckSouth;
/*     */   public LastInstance lastStuckWest;
/*     */   public LastInstance lastStuckEast;
/*     */   public LastInstance lastVehicleSwitch;
/*  92 */   public double lastHorizontalOffset = 0.0D;
/*  93 */   public double lastVerticalOffset = 0.0D;
/*     */   
/*     */   public UncertaintyHandler(GrimPlayer player) {
/*  96 */     this.player = player;
/*  97 */     this.lastFlyingTicks = new LastInstance(player);
/*  98 */     this.lastFlyingStatusChange = new LastInstance(player);
/*  99 */     this.lastUnderwaterFlyingHack = new LastInstance(player);
/* 100 */     this.lastStuckSpeedMultiplier = new LastInstance(player);
/* 101 */     this.lastHardCollidingLerpingEntity = new LastInstance(player);
/* 102 */     this.lastThirtyMillionHardBorder = new LastInstance(player);
/* 103 */     this.lastTeleportTicks = new LastInstance(player);
/* 104 */     this.lastPointThree = new LastInstance(player);
/* 105 */     this.stuckOnEdge = new LastInstance(player);
/* 106 */     this.lastStuckNorth = new LastInstance(player);
/* 107 */     this.lastStuckSouth = new LastInstance(player);
/* 108 */     this.lastStuckWest = new LastInstance(player);
/* 109 */     this.lastStuckEast = new LastInstance(player);
/* 110 */     this.lastVehicleSwitch = new LastInstance(player);
/* 111 */     tick();
/*     */     
/* 113 */     this.riptideEntities.add(Integer.valueOf(0));
/* 114 */     this.collidingEntities.add(Integer.valueOf(0));
/*     */   }
/*     */   
/*     */   public void tick() {
/* 118 */     this.pistonX.add(Double.valueOf(0.0D));
/* 119 */     this.pistonY.add(Double.valueOf(0.0D));
/* 120 */     this.pistonZ.add(Double.valueOf(0.0D));
/* 121 */     this.isStepMovement = false;
/*     */     
/* 123 */     this.isSteppingNearShulker = false;
/* 124 */     this.wasSteppingOnBouncyBlock = this.isSteppingOnBouncyBlock;
/* 125 */     this.isSteppingOnSlime = false;
/* 126 */     this.isSteppingOnBouncyBlock = false;
/* 127 */     this.isSteppingOnIce = false;
/* 128 */     this.isSteppingOnHoney = false;
/* 129 */     this.isSteppingNearBubbleColumn = false;
/* 130 */     this.isSteppingNearScaffolding = false;
/*     */     
/* 132 */     this.slimePistonBounces = new HashSet<>();
/* 133 */     tickFireworksBox();
/*     */   }
/*     */   
/*     */   public boolean wasAffectedByStuckSpeed() {
/* 137 */     return this.lastStuckSpeedMultiplier.hasOccurredSince(5);
/*     */   }
/*     */   
/*     */   public void tickFireworksBox() {
/* 141 */     this.fishingRodPullBox = this.fishingRodPulls.isEmpty() ? null : new SimpleCollisionBox();
/* 142 */     this.fireworksBox = null;
/*     */     
/* 144 */     for (Iterator<Integer> iterator = this.fishingRodPulls.iterator(); iterator.hasNext(); ) { int owner = ((Integer)iterator.next()).intValue();
/* 145 */       PacketEntity entity = this.player.compensatedEntities.getEntity(owner);
/* 146 */       if (entity == null)
/*     */         continue; 
/* 148 */       SimpleCollisionBox entityBox = entity.getPossibleCollisionBoxes();
/* 149 */       float scale = (float)entity.getAttributeValue(Attributes.SCALE);
/* 150 */       float width = BoundingBoxSize.getWidth(this.player, entity) * scale;
/* 151 */       float height = BoundingBoxSize.getHeight(this.player, entity) * scale;
/*     */ 
/*     */       
/* 154 */       entityBox.maxY -= height;
/* 155 */       entityBox.expand((-width / 2.0F), 0.0D, (-width / 2.0F));
/*     */       
/* 157 */       Vector3dm maxLocation = new Vector3dm(entityBox.maxX, entityBox.maxY, entityBox.maxZ);
/* 158 */       Vector3dm minLocation = new Vector3dm(entityBox.minX, entityBox.minY, entityBox.minZ);
/*     */       
/* 160 */       Vector3dm diff = minLocation.subtract(new Vector3dm(this.player.lastX, this.player.lastY + 1.4400000000000002D, this.player.lastZ)).multiply(0.1D);
/* 161 */       this.fishingRodPullBox.minX = Math.min(0.0D, diff.getX());
/* 162 */       this.fishingRodPullBox.minY = Math.min(0.0D, diff.getY());
/* 163 */       this.fishingRodPullBox.minZ = Math.min(0.0D, diff.getZ());
/*     */       
/* 165 */       diff = maxLocation.subtract(new Vector3dm(this.player.lastX, this.player.lastY + 1.4400000000000002D, this.player.lastZ)).multiply(0.1D);
/* 166 */       this.fishingRodPullBox.maxX = Math.max(0.0D, diff.getX());
/* 167 */       this.fishingRodPullBox.maxY = Math.max(0.0D, diff.getY());
/* 168 */       this.fishingRodPullBox.maxZ = Math.max(0.0D, diff.getZ()); }
/*     */ 
/*     */     
/* 171 */     this.fishingRodPulls.clear();
/*     */     
/* 173 */     int maxFireworks = this.player.fireworks.getMaxFireworksAppliedPossible() * 2;
/* 174 */     if (maxFireworks <= 0 || (!this.player.isGliding && !this.player.wasGliding)) {
/*     */       return;
/*     */     }
/*     */     
/* 178 */     this.fireworksBox = new SimpleCollisionBox();
/*     */     
/* 180 */     Vector3dm currentLook = ReachUtils.getLook(this.player, this.player.xRot, this.player.yRot);
/* 181 */     Vector3dm lastLook = ReachUtils.getLook(this.player, this.player.lastXRot, this.player.lastYRot);
/*     */     
/* 183 */     double antiTickSkipping = this.player.isPointThree() ? 0.0D : 0.05D;
/*     */     
/* 185 */     double minX = Math.min(-antiTickSkipping, currentLook.getX()) + Math.min(-antiTickSkipping, lastLook.getX());
/* 186 */     double minY = Math.min(-antiTickSkipping, currentLook.getY()) + Math.min(-antiTickSkipping, lastLook.getY());
/* 187 */     double minZ = Math.min(-antiTickSkipping, currentLook.getZ()) + Math.min(-antiTickSkipping, lastLook.getZ());
/* 188 */     double maxX = Math.max(antiTickSkipping, currentLook.getX()) + Math.max(antiTickSkipping, lastLook.getX());
/* 189 */     double maxY = Math.max(antiTickSkipping, currentLook.getY()) + Math.max(antiTickSkipping, lastLook.getY());
/* 190 */     double maxZ = Math.max(antiTickSkipping, currentLook.getZ()) + Math.max(antiTickSkipping, lastLook.getZ());
/*     */     
/* 192 */     minX *= 1.7D;
/* 193 */     minY *= 1.7D;
/* 194 */     minZ *= 1.7D;
/* 195 */     maxX *= 1.7D;
/* 196 */     maxY *= 1.7D;
/* 197 */     maxZ *= 1.7D;
/*     */     
/* 199 */     minX = Math.max(-1.7D, minX);
/* 200 */     minY = Math.max(-1.7D, minY);
/* 201 */     minZ = Math.max(-1.7D, minZ);
/* 202 */     maxX = Math.min(1.7D, maxX);
/* 203 */     maxY = Math.min(1.7D, maxY);
/* 204 */     maxZ = Math.min(1.7D, maxZ);
/*     */ 
/*     */ 
/*     */     
/* 208 */     this.fireworksBox = new SimpleCollisionBox(minX, minY, minZ, maxX, maxY, maxZ);
/*     */   }
/*     */   
/*     */   public double getOffsetHorizontal(VectorData data) {
/* 212 */     double threshold = this.player.getMovementThreshold();
/*     */     
/* 214 */     boolean newVectorPointThree = (this.player.couldSkipTick && data.isKnockback());
/* 215 */     boolean explicit003 = (data.isZeroPointZeroThree() || this.lastMovementWasZeroPointZeroThree);
/* 216 */     boolean either003 = (newVectorPointThree || explicit003);
/*     */     
/* 218 */     double pointThree = (newVectorPointThree || this.lastMovementWasUnknown003VectorReset) ? threshold : 0.0D;
/*     */ 
/*     */     
/* 221 */     if (explicit003) {
/* 222 */       pointThree = 0.546D * threshold * 2.0D + threshold;
/*     */     }
/*     */ 
/*     */     
/* 226 */     if (either003 && (influencedByBouncyBlock() || this.isSteppingOnHoney)) {
/* 227 */       pointThree = 0.7280000000000001D * threshold * 2.0D + threshold;
/*     */     }
/*     */     
/* 230 */     if (either003 && this.isSteppingOnIce) {
/* 231 */       pointThree = 0.8999900000000001D * threshold * 2.0D + threshold;
/*     */     }
/*     */     
/* 234 */     if (pointThree > threshold) {
/* 235 */       pointThree *= 0.8999900000000001D;
/*     */     }
/*     */     
/* 238 */     if (either003 && (this.player.lastOnGround || this.player.isFlying)) {
/* 239 */       pointThree = 0.91D * threshold * 2.0D + threshold;
/*     */     }
/*     */     
/* 242 */     if (either003 && (this.player.isGliding || this.player.wasGliding)) {
/* 243 */       pointThree = 0.99D * threshold * 2.0D + threshold;
/*     */     }
/*     */     
/* 246 */     if (this.player.uncertaintyHandler.claimingLeftStuckSpeed) {
/* 247 */       pointThree = 0.15D;
/*     */     }
/*     */     
/* 250 */     return pointThree;
/*     */   }
/*     */   
/*     */   public boolean influencedByBouncyBlock() {
/* 254 */     return (this.isSteppingOnBouncyBlock || this.wasSteppingOnBouncyBlock);
/*     */   }
/*     */ 
/*     */   
/*     */   public double getVerticalOffset(VectorData data) {
/* 259 */     if (this.player.uncertaintyHandler.claimingLeftStuckSpeed) {
/* 260 */       return 0.06D;
/*     */     }
/*     */     
/* 263 */     if (this.player.uncertaintyHandler.wasSteppingOnBouncyBlock && (this.player.wasTouchingWater || this.player.wasTouchingLava)) {
/* 264 */       return 0.06D;
/*     */     }
/*     */     
/* 267 */     if (this.lastFlyingTicks.hasOccurredSince(5) && Math.abs(data.vector.getY()) < 4.5D * this.player.flySpeed - 0.25D) {
/* 268 */       return 0.06D;
/*     */     }
/* 270 */     double pointThree = this.player.getMovementThreshold();
/*     */     
/* 272 */     if (data.isTrident()) {
/* 273 */       return pointThree * 2.0D;
/*     */     }
/*     */     
/* 276 */     if (this.player.couldSkipTick && (data.isKnockback() || this.player.isClimbing) && !data.isZeroPointZeroThree()) {
/* 277 */       return pointThree;
/*     */     }
/* 279 */     if (this.player.pointThreeEstimator.controlsVerticalMovement())
/*     */     {
/* 281 */       if (data.isZeroPointZeroThree() || this.lastMovementWasZeroPointZeroThree) {
/* 282 */         return pointThree * 2.0D;
/*     */       }
/*     */     }
/*     */     
/* 286 */     if (this.wasZeroPointThreeVertically || this.player.uncertaintyHandler.onGroundUncertain || this.player.uncertaintyHandler.lastPacketWasGroundPacket) {
/* 287 */       return pointThree;
/*     */     }
/* 289 */     return 0.0D;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double reduceOffset(double offset) {
/* 296 */     if (this.player.uncertaintyHandler.lastHardCollidingLerpingEntity.hasOccurredSince(3)) {
/* 297 */       offset -= 1.2D;
/*     */     }
/*     */     
/* 300 */     if (this.player.uncertaintyHandler.isOrWasNearGlitchyBlock) {
/* 301 */       offset -= 0.25D;
/*     */     }
/*     */ 
/*     */     
/* 305 */     if (this.player.uncertaintyHandler.wasAffectedByStuckSpeed() && (!this.player.isPointThree() || this.player.inVehicle())) {
/* 306 */       offset -= 0.01D;
/*     */     }
/*     */     
/* 309 */     if (this.player.uncertaintyHandler.influencedByBouncyBlock() && (!this.player.isPointThree() || this.player.inVehicle())) {
/* 310 */       offset -= 0.03D;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 315 */     PacketEntity packetEntity = this.player.compensatedEntities.self.getRiding(); if (packetEntity instanceof PacketEntityRideable) { PacketEntityRideable vehicle = (PacketEntityRideable)packetEntity;
/* 316 */       if (vehicle.currentBoostTime < vehicle.boostTimeMax + 20) {
/* 317 */         offset -= 0.01D;
/*     */       } }
/*     */     
/* 320 */     return Math.max(0.0D, offset);
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkForHardCollision() {
/* 325 */     if (hasHardCollision()) this.player.uncertaintyHandler.lastHardCollidingLerpingEntity.reset();
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean hasHardCollision() {
/* 331 */     SimpleCollisionBox expandedBB = this.player.boundingBox.copy().expand(1.0D);
/* 332 */     return (this.isSteppingNearShulker || regularHardCollision(expandedBB) || striderCollision(expandedBB) || boatCollision(expandedBB));
/*     */   }
/*     */   
/*     */   private boolean regularHardCollision(SimpleCollisionBox expandedBB) {
/* 336 */     PacketEntity riding = this.player.compensatedEntities.self.getRiding();
/* 337 */     for (ObjectIterator<PacketEntity> objectIterator = this.player.compensatedEntities.entityMap.values().iterator(); objectIterator.hasNext(); ) { PacketEntity entity = objectIterator.next();
/* 338 */       if ((entity.isBoat || entity.type == EntityTypes.SHULKER || entity.isHappyGhast) && entity != riding && entity
/* 339 */         .getPossibleCollisionBoxes().isIntersected(expandedBB)) {
/* 340 */         return true;
/*     */       } }
/*     */ 
/*     */     
/* 344 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean striderCollision(SimpleCollisionBox expandedBB) {
/* 349 */     if (this.player.compensatedEntities.self.getRiding() instanceof ac.grim.grimac.utils.data.packetentity.PacketEntityStrider) {
/* 350 */       for (ObjectIterator<PacketEntity> objectIterator = this.player.compensatedEntities.entityMap.values().iterator(); objectIterator.hasNext(); ) { PacketEntity entity = objectIterator.next();
/* 351 */         if (entity.type == EntityTypes.STRIDER && entity != this.player.compensatedEntities.self.getRiding() && 
/* 352 */           !entity.hasPassenger(entity) && entity.getPossibleCollisionBoxes().isIntersected(expandedBB)) {
/* 353 */           return true;
/*     */         } }
/*     */     
/*     */     }
/*     */     
/* 358 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean boatCollision(SimpleCollisionBox expandedBB) {
/* 363 */     PacketEntity riding = this.player.compensatedEntities.self.getRiding();
/* 364 */     if (riding == null || !riding.isBoat) return false;
/*     */     
/* 366 */     for (ObjectIterator<PacketEntity> objectIterator = this.player.compensatedEntities.entityMap.values().iterator(); objectIterator.hasNext(); ) { PacketEntity entity = objectIterator.next();
/* 367 */       if (entity != riding && entity.isPushable() && !riding.hasPassenger(entity) && entity
/* 368 */         .getPossibleCollisionBoxes().isIntersected(expandedBB)) {
/* 369 */         return true;
/*     */       } }
/*     */     
/* 372 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\predictionengine\UncertaintyHandler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */