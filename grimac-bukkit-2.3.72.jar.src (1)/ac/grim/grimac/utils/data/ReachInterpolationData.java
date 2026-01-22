/*     */ package ac.grim.grimac.utils.data;
/*     */ 
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.CollisionBox;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.NoCollisionBox;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntity;
/*     */ import ac.grim.grimac.utils.nmsutil.GetBoundingBox;
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
/*     */ public class ReachInterpolationData
/*     */ {
/*     */   private final SimpleCollisionBox targetLocation;
/*     */   private final GrimPlayer player;
/*     */   private final PacketEntity entity;
/*     */   public SimpleCollisionBox startingLocation;
/*  36 */   private int interpolationStepsLowBound = 0;
/*  37 */   private int interpolationStepsHighBound = 0;
/*  38 */   private int interpolationSteps = 1;
/*     */   private boolean expandNonRelative = false;
/*  40 */   private int cancelledLerpInterpolationStepsLowBound = Integer.MAX_VALUE;
/*     */   
/*     */   public ReachInterpolationData(GrimPlayer player, SimpleCollisionBox startingLocation, TrackedPosition position, PacketEntity entity) {
/*  43 */     boolean isPointNine = (!player.inVehicle() && player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9));
/*     */     
/*  45 */     this.startingLocation = startingLocation;
/*  46 */     Vector3d pos = position.getPos();
/*  47 */     this.targetLocation = new SimpleCollisionBox(pos.x, pos.y, pos.z, pos.x, pos.y, pos.z, false);
/*  48 */     this.player = player;
/*  49 */     this.entity = entity;
/*     */ 
/*     */ 
/*     */     
/*  53 */     if (!isPointNine && PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_9)) {
/*  54 */       this.targetLocation.expand(0.03125D);
/*     */     }
/*     */     
/*  57 */     if (entity.isBoat) {
/*  58 */       this.interpolationSteps = 10;
/*  59 */     } else if (entity.isMinecart) {
/*  60 */       this.interpolationSteps = 5;
/*  61 */     } else if (entity.type == EntityTypes.SHULKER) {
/*  62 */       this.interpolationSteps = 1;
/*  63 */     } else if (entity.isLivingEntity) {
/*  64 */       this.interpolationSteps = 3;
/*     */     } else {
/*  66 */       this.interpolationSteps = 1;
/*     */     } 
/*     */     
/*  69 */     if (isPointNine) this.interpolationStepsHighBound = getInterpolationSteps();
/*     */   
/*     */   }
/*     */   
/*     */   public ReachInterpolationData(GrimPlayer player, SimpleCollisionBox finishedLoc, PacketEntity entity) {
/*  74 */     this.startingLocation = finishedLoc;
/*  75 */     this.targetLocation = finishedLoc;
/*  76 */     this.entity = entity;
/*  77 */     this.player = player;
/*     */   }
/*     */   
/*     */   public static SimpleCollisionBox combineCollisionBox(SimpleCollisionBox one, SimpleCollisionBox two) {
/*  81 */     double minX = Math.min(one.minX, two.minX);
/*  82 */     double maxX = Math.max(one.maxX, two.maxX);
/*  83 */     double minY = Math.min(one.minY, two.minY);
/*  84 */     double maxY = Math.max(one.maxY, two.maxY);
/*  85 */     double minZ = Math.min(one.minZ, two.minZ);
/*  86 */     double maxZ = Math.max(one.maxZ, two.maxZ);
/*     */     
/*  88 */     return new SimpleCollisionBox(minX, minY, minZ, maxX, maxY, maxZ);
/*     */   }
/*     */   
/*     */   public static CollisionBox getOverlapHitbox(CollisionBox b1, CollisionBox b2) {
/*  92 */     if (b1 == NoCollisionBox.INSTANCE || b2 == NoCollisionBox.INSTANCE)
/*  93 */       return (CollisionBox)NoCollisionBox.INSTANCE; 
/*  94 */     if (!(b1 instanceof SimpleCollisionBox) || !(b2 instanceof SimpleCollisionBox)) {
/*  95 */       throw new IllegalArgumentException("Both b1 and b2 must be SimpleCollisionBox instances");
/*     */     }
/*     */     
/*  98 */     SimpleCollisionBox box1 = (SimpleCollisionBox)b1;
/*  99 */     SimpleCollisionBox box2 = (SimpleCollisionBox)b2;
/*     */ 
/*     */     
/* 102 */     double overlapMinX = Math.max(box1.minX, box2.minX);
/* 103 */     double overlapMaxX = Math.min(box1.maxX, box2.maxX);
/* 104 */     double overlapMinY = Math.max(box1.minY, box2.minY);
/* 105 */     double overlapMaxY = Math.min(box1.maxY, box2.maxY);
/* 106 */     double overlapMinZ = Math.max(box1.minZ, box2.minZ);
/* 107 */     double overlapMaxZ = Math.min(box1.maxZ, box2.maxZ);
/*     */ 
/*     */     
/* 110 */     if (overlapMinX > overlapMaxX || overlapMinY > overlapMaxY || overlapMinZ > overlapMaxZ) {
/* 111 */       return (CollisionBox)NoCollisionBox.INSTANCE;
/*     */     }
/*     */ 
/*     */     
/* 115 */     return (CollisionBox)new SimpleCollisionBox(overlapMinX, overlapMinY, overlapMinZ, overlapMaxX, overlapMaxY, overlapMaxZ);
/*     */   }
/*     */   
/*     */   private int getInterpolationSteps() {
/* 119 */     return this.interpolationSteps;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleCollisionBox getPossibleLocationCombined() {
/* 141 */     int interpSteps = getInterpolationSteps();
/*     */     
/* 143 */     int interpolationStepsLowBound = Math.min(this.interpolationStepsLowBound, this.cancelledLerpInterpolationStepsLowBound);
/*     */ 
/*     */     
/* 146 */     double stepMinX = (this.targetLocation.minX - this.startingLocation.minX) / interpSteps;
/* 147 */     double stepMaxX = (this.targetLocation.maxX - this.startingLocation.maxX) / interpSteps;
/* 148 */     double stepMinY = (this.targetLocation.minY - this.startingLocation.minY) / interpSteps;
/* 149 */     double stepMaxY = (this.targetLocation.maxY - this.startingLocation.maxY) / interpSteps;
/* 150 */     double stepMinZ = (this.targetLocation.minZ - this.startingLocation.minZ) / interpSteps;
/* 151 */     double stepMaxZ = (this.targetLocation.maxZ - this.startingLocation.maxZ) / interpSteps;
/*     */     
/* 153 */     SimpleCollisionBox minimumInterpLocation = new SimpleCollisionBox(this.startingLocation.minX + interpolationStepsLowBound * stepMinX, this.startingLocation.minY + interpolationStepsLowBound * stepMinY, this.startingLocation.minZ + interpolationStepsLowBound * stepMinZ, this.startingLocation.maxX + interpolationStepsLowBound * stepMaxX, this.startingLocation.maxY + interpolationStepsLowBound * stepMaxY, this.startingLocation.maxZ + interpolationStepsLowBound * stepMaxZ);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 161 */     for (int step = interpolationStepsLowBound + 1; step <= this.interpolationStepsHighBound; step++) {
/* 162 */       minimumInterpLocation = combineCollisionBox(minimumInterpLocation, new SimpleCollisionBox(this.startingLocation.minX + step * stepMinX, this.startingLocation.minY + step * stepMinY, this.startingLocation.minZ + step * stepMinZ, this.startingLocation.maxX + step * stepMaxX, this.startingLocation.maxY + step * stepMaxY, this.startingLocation.maxZ + step * stepMaxZ));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 171 */     return minimumInterpLocation;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleCollisionBox getPossibleHitboxCombined() {
/* 193 */     SimpleCollisionBox minimumInterpLocation = getPossibleLocationCombined();
/*     */     
/* 195 */     if (this.expandNonRelative) {
/* 196 */       minimumInterpLocation.expand(0.03125D, 0.015625D, 0.03125D);
/*     */     }
/* 198 */     GetBoundingBox.expandBoundingBoxByEntityDimensions(minimumInterpLocation, this.player, this.entity);
/*     */     
/* 200 */     return minimumInterpLocation;
/*     */   }
/*     */ 
/*     */   
/*     */   public void updatePossibleStartingLocation(SimpleCollisionBox possibleLocationCombined) {
/* 205 */     this.startingLocation = combineCollisionBox(this.startingLocation, possibleLocationCombined);
/*     */   }
/*     */ 
/*     */   
/*     */   public void tickMovement(boolean incrementLowBound, boolean tickingReliably) {
/* 210 */     if (!tickingReliably) this.interpolationStepsHighBound = getInterpolationSteps(); 
/* 211 */     if (incrementLowBound)
/* 212 */       this.interpolationStepsLowBound = Math.min(this.interpolationStepsLowBound + 1, getInterpolationSteps()); 
/* 213 */     this.interpolationStepsHighBound = Math.min(this.interpolationStepsHighBound + 1, getInterpolationSteps());
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 218 */     return "ReachInterpolationData{targetLocation=" + String.valueOf(this.targetLocation) + ", startingLocation=" + String.valueOf(this.startingLocation) + ", interpolationStepsLowBound=" + this.interpolationStepsLowBound + ", interpolationStepsHighBound=" + this.interpolationStepsHighBound + "}";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void expandNonRelative() {
/* 227 */     this.expandNonRelative = true;
/*     */   }
/*     */   
/*     */   public void cancelLerp() {
/* 231 */     this.cancelledLerpInterpolationStepsLowBound = this.interpolationStepsLowBound;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\ReachInterpolationData.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */