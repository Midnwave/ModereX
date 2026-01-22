/*     */ package ac.grim.grimac.utils.nmsutil;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*     */ import ac.grim.grimac.utils.data.Pair;
/*     */ import ac.grim.grimac.utils.math.GrimMath;
/*     */ import ac.grim.grimac.utils.math.Vector3dm;
/*     */ import ac.grim.grimac.utils.math.VectorUtils;
/*     */ import lombok.Generated;
/*     */ 
/*     */ public final class ReachUtils {
/*     */   @Generated
/*     */   private ReachUtils() {
/*  15 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*     */   }
/*     */   public static Pair<Vector3dm, BlockFace> calculateIntercept(SimpleCollisionBox self, Vector3dm origin, Vector3dm end) {
/*  18 */     Vector3dm minX = getIntermediateWithXValue(origin, end, self.minX);
/*  19 */     Vector3dm maxX = getIntermediateWithXValue(origin, end, self.maxX);
/*  20 */     Vector3dm minY = getIntermediateWithYValue(origin, end, self.minY);
/*  21 */     Vector3dm maxY = getIntermediateWithYValue(origin, end, self.maxY);
/*  22 */     Vector3dm minZ = getIntermediateWithZValue(origin, end, self.minZ);
/*  23 */     Vector3dm maxZ = getIntermediateWithZValue(origin, end, self.maxZ);
/*     */     
/*  25 */     BlockFace bestFace = null;
/*     */     
/*  27 */     if (!isVecInYZ(self, minX)) minX = null; 
/*  28 */     if (!isVecInYZ(self, maxX)) maxX = null; 
/*  29 */     if (!isVecInXZ(self, minY)) minY = null; 
/*  30 */     if (!isVecInXZ(self, maxY)) maxY = null; 
/*  31 */     if (!isVecInXY(self, minZ)) minZ = null; 
/*  32 */     if (!isVecInXY(self, maxZ)) maxZ = null;
/*     */     
/*  34 */     Vector3dm best = null;
/*     */     
/*  36 */     if (minX != null) {
/*  37 */       best = minX;
/*  38 */       bestFace = BlockFace.WEST;
/*     */     } 
/*     */     
/*  41 */     if (maxX != null && (best == null || origin.distanceSquared(maxX) < origin.distanceSquared(best))) {
/*  42 */       best = maxX;
/*  43 */       bestFace = BlockFace.EAST;
/*     */     } 
/*     */     
/*  46 */     if (minY != null && (best == null || origin.distanceSquared(minY) < origin.distanceSquared(best))) {
/*  47 */       best = minY;
/*  48 */       bestFace = BlockFace.DOWN;
/*     */     } 
/*     */     
/*  51 */     if (maxY != null && (best == null || origin.distanceSquared(maxY) < origin.distanceSquared(best))) {
/*  52 */       best = maxY;
/*  53 */       bestFace = BlockFace.UP;
/*     */     } 
/*     */     
/*  56 */     if (minZ != null && (best == null || origin.distanceSquared(minZ) < origin.distanceSquared(best))) {
/*  57 */       best = minZ;
/*  58 */       bestFace = BlockFace.NORTH;
/*     */     } 
/*     */     
/*  61 */     if (maxZ != null && (best == null || origin.distanceSquared(maxZ) < origin.distanceSquared(best))) {
/*  62 */       best = maxZ;
/*  63 */       bestFace = BlockFace.SOUTH;
/*     */     } 
/*     */     
/*  66 */     return new Pair(best, bestFace);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Vector3dm getIntermediateWithXValue(Vector3dm self, Vector3dm other, double x) {
/*  74 */     double deltaX = other.getX() - self.getX();
/*  75 */     double deltaY = other.getY() - self.getY();
/*  76 */     double deltaZ = other.getZ() - self.getZ();
/*     */     
/*  78 */     if (deltaX * deltaX < 1.0000000116860974E-7D) {
/*  79 */       return null;
/*     */     }
/*  81 */     double d3 = (x - self.getX()) / deltaX;
/*  82 */     return (d3 >= 0.0D && d3 <= 1.0D) ? new Vector3dm(self.getX() + deltaX * d3, self.getY() + deltaY * d3, self.getZ() + deltaZ * d3) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Vector3dm getIntermediateWithYValue(Vector3dm self, Vector3dm other, double y) {
/*  91 */     double deltaX = other.getX() - self.getX();
/*  92 */     double deltaY = other.getY() - self.getY();
/*  93 */     double deltaZ = other.getZ() - self.getZ();
/*     */     
/*  95 */     if (deltaY * deltaY < 1.0000000116860974E-7D) {
/*  96 */       return null;
/*     */     }
/*  98 */     double d3 = (y - self.getY()) / deltaY;
/*  99 */     return (d3 >= 0.0D && d3 <= 1.0D) ? new Vector3dm(self.getX() + deltaX * d3, self.getY() + deltaY * d3, self.getZ() + deltaZ * d3) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Vector3dm getIntermediateWithZValue(Vector3dm self, Vector3dm other, double z) {
/* 108 */     double deltaX = other.getX() - self.getX();
/* 109 */     double deltaY = other.getY() - self.getY();
/* 110 */     double deltaZ = other.getZ() - self.getZ();
/*     */     
/* 112 */     if (deltaZ * deltaZ < 1.0000000116860974E-7D) {
/* 113 */       return null;
/*     */     }
/* 115 */     double d3 = (z - self.getZ()) / deltaZ;
/* 116 */     return (d3 >= 0.0D && d3 <= 1.0D) ? new Vector3dm(self.getX() + deltaX * d3, self.getY() + deltaY * d3, self.getZ() + deltaZ * d3) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isVecInYZ(SimpleCollisionBox self, Vector3dm vec) {
/* 124 */     return (vec != null && vec.getY() >= self.minY && vec.getY() <= self.maxY && vec.getZ() >= self.minZ && vec.getZ() <= self.maxZ);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isVecInXZ(SimpleCollisionBox self, Vector3dm vec) {
/* 131 */     return (vec != null && vec.getX() >= self.minX && vec.getX() <= self.maxX && vec.getZ() >= self.minZ && vec.getZ() <= self.maxZ);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isVecInXY(SimpleCollisionBox self, Vector3dm vec) {
/* 138 */     return (vec != null && vec.getX() >= self.minX && vec.getX() <= self.maxX && vec.getY() >= self.minY && vec.getY() <= self.maxY);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Vector3dm getLook(GrimPlayer player, float yaw, float pitch) {
/* 143 */     if (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_12_2)) {
/* 144 */       float f6 = player.trigHandler.cos(GrimMath.radians(-yaw) - 3.1415927F);
/* 145 */       float f7 = player.trigHandler.sin(GrimMath.radians(-yaw) - 3.1415927F);
/* 146 */       float f8 = -player.trigHandler.cos(GrimMath.radians(-pitch));
/* 147 */       float f9 = player.trigHandler.sin(GrimMath.radians(-pitch));
/* 148 */       return new Vector3dm(f7 * f8, f9, f6 * f8);
/*     */     } 
/* 150 */     float f = GrimMath.radians(pitch);
/* 151 */     float f1 = GrimMath.radians(-yaw);
/* 152 */     float f2 = player.trigHandler.cos(f1);
/* 153 */     float f3 = player.trigHandler.sin(f1);
/* 154 */     float f4 = player.trigHandler.cos(f);
/* 155 */     float f5 = player.trigHandler.sin(f);
/* 156 */     return new Vector3dm((f3 * f4), -f5, (f2 * f4));
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isVecInside(SimpleCollisionBox self, Vector3dm vec) {
/* 161 */     return (vec.getX() > self.minX && vec.getX() < self.maxX && vec.getY() > self.minY && vec.getY() < self.maxY && vec.getZ() > self.minZ && vec.getZ() < self.maxZ);
/*     */   }
/*     */   
/*     */   public static double getMinReachToBox(GrimPlayer player, SimpleCollisionBox targetBox) {
/* 165 */     boolean giveMovementThresholdLenience = (!player.packetStateData.didLastMovementIncludePosition || player.canSkipTicks());
/* 166 */     if (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8)) {
/* 167 */       targetBox.expand(0.1D);
/*     */     }
/* 169 */     double lowest = Double.MAX_VALUE;
/*     */     
/* 171 */     if (giveMovementThresholdLenience) targetBox.expand(player.getMovementThreshold()); 
/* 172 */     double[] possibleEyeHeights = player.getPossibleEyeHeights();
/* 173 */     for (double eyes : possibleEyeHeights) {
/* 174 */       Vector3dm from = new Vector3dm(player.x, player.y + eyes, player.z);
/* 175 */       Vector3dm closestPoint = VectorUtils.cutBoxToVector(from, targetBox);
/* 176 */       lowest = Math.min(lowest, closestPoint.distance(from));
/*     */     } 
/*     */     
/* 179 */     return lowest;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\nmsutil\ReachUtils.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */