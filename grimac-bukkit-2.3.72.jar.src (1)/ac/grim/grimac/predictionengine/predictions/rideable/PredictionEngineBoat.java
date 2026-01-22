/*     */ package ac.grim.grimac.predictionengine.predictions.rideable;
/*     */ 
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.predictionengine.predictions.PredictionEngine;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.utils.collisions.CollisionData;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*     */ import ac.grim.grimac.utils.data.VectorData;
/*     */ import ac.grim.grimac.utils.enums.BoatEntityStatus;
/*     */ import ac.grim.grimac.utils.math.GrimMath;
/*     */ import ac.grim.grimac.utils.math.Vector3dm;
/*     */ import ac.grim.grimac.utils.nmsutil.BlockProperties;
/*     */ import ac.grim.grimac.utils.nmsutil.Collisions;
/*     */ import ac.grim.grimac.utils.nmsutil.GetBoundingBox;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class PredictionEngineBoat
/*     */   extends PredictionEngine {
/*     */   public PredictionEngineBoat(GrimPlayer player) {
/*  26 */     player.uncertaintyHandler.collidingEntities.add(Integer.valueOf(0));
/*  27 */     player.vehicleData.midTickY = 0.0D;
/*     */ 
/*     */     
/*  30 */     player.vehicleData.oldStatus = player.vehicleData.status;
/*  31 */     player.vehicleData.status = getStatus(player);
/*     */   }
/*     */   
/*     */   private static BoatEntityStatus getStatus(GrimPlayer player) {
/*  35 */     BoatEntityStatus status = isUnderwater(player);
/*  36 */     if (status != null) {
/*  37 */       player.vehicleData.waterLevel = player.boundingBox.maxY;
/*  38 */       return status;
/*  39 */     }  if (checkInWater(player)) {
/*  40 */       return BoatEntityStatus.IN_WATER;
/*     */     }
/*  42 */     float friction = getGroundFriction(player);
/*  43 */     if (friction > 0.0F) {
/*  44 */       player.vehicleData.landFriction = friction;
/*  45 */       return BoatEntityStatus.ON_LAND;
/*     */     } 
/*  47 */     return BoatEntityStatus.IN_AIR;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private static BoatEntityStatus isUnderwater(@NotNull GrimPlayer player) {
/*  53 */     SimpleCollisionBox axisalignedbb = player.boundingBox;
/*  54 */     double d0 = axisalignedbb.maxY + 0.001D;
/*  55 */     int i = GrimMath.floor(axisalignedbb.minX);
/*  56 */     int j = GrimMath.ceil(axisalignedbb.maxX);
/*  57 */     int k = GrimMath.floor(axisalignedbb.maxY);
/*  58 */     int l = GrimMath.ceil(d0);
/*  59 */     int i1 = GrimMath.floor(axisalignedbb.minZ);
/*  60 */     int j1 = GrimMath.ceil(axisalignedbb.maxZ);
/*  61 */     boolean flag = false;
/*     */     
/*  63 */     for (int k1 = i; k1 < j; k1++) {
/*  64 */       for (int l1 = k; l1 < l; l1++) {
/*  65 */         for (int i2 = i1; i2 < j1; i2++) {
/*  66 */           double level = player.compensatedWorld.getWaterFluidLevelAt(k1, l1, i2);
/*  67 */           if (d0 < l1 + level) {
/*  68 */             if (!player.compensatedWorld.isWaterSourceBlock(k1, l1, i2)) {
/*  69 */               return BoatEntityStatus.UNDER_FLOWING_WATER;
/*     */             }
/*     */             
/*  72 */             flag = true;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/*  78 */     return flag ? BoatEntityStatus.UNDER_WATER : null;
/*     */   }
/*     */   private static boolean checkInWater(GrimPlayer grimPlayer) {
/*     */     int m;
/*  82 */     SimpleCollisionBox axisalignedbb = grimPlayer.boundingBox;
/*  83 */     int i = GrimMath.floor(axisalignedbb.minX);
/*  84 */     int j = GrimMath.ceil(axisalignedbb.maxX);
/*  85 */     int k = GrimMath.floor(axisalignedbb.minY);
/*  86 */     int l = GrimMath.ceil(axisalignedbb.minY + 0.001D);
/*  87 */     int i1 = GrimMath.floor(axisalignedbb.minZ);
/*  88 */     int j1 = GrimMath.ceil(axisalignedbb.maxZ);
/*  89 */     boolean flag = false;
/*  90 */     grimPlayer.vehicleData.waterLevel = -1.7976931348623157E308D;
/*     */     
/*  92 */     for (int k1 = i; k1 < j; k1++) {
/*  93 */       for (int l1 = k; l1 < l; l1++) {
/*  94 */         for (int i2 = i1; i2 < j1; i2++) {
/*  95 */           double level = grimPlayer.compensatedWorld.getWaterFluidLevelAt(k1, l1, i2);
/*  96 */           if (level > 0.0D) {
/*  97 */             float f = (float)(l1 + level);
/*  98 */             grimPlayer.vehicleData.waterLevel = Math.max(f, grimPlayer.vehicleData.waterLevel);
/*  99 */             m = flag | ((axisalignedbb.minY < f) ? 1 : 0);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 105 */     return m;
/*     */   }
/*     */   
/*     */   public static float getGroundFriction(GrimPlayer player) {
/* 109 */     SimpleCollisionBox axisalignedbb = player.boundingBox;
/* 110 */     SimpleCollisionBox axisalignedbb1 = new SimpleCollisionBox(axisalignedbb.minX, axisalignedbb.minY - 0.001D, axisalignedbb.minZ, axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ, false);
/* 111 */     int i = (int)(Math.floor(axisalignedbb1.minX) - 1.0D);
/* 112 */     int j = (int)(Math.ceil(axisalignedbb1.maxX) + 1.0D);
/* 113 */     int k = (int)(Math.floor(axisalignedbb1.minY) - 1.0D);
/* 114 */     int l = (int)(Math.ceil(axisalignedbb1.maxY) + 1.0D);
/* 115 */     int i1 = (int)(Math.floor(axisalignedbb1.minZ) - 1.0D);
/* 116 */     int j1 = (int)(Math.ceil(axisalignedbb1.maxZ) + 1.0D);
/*     */     
/* 118 */     float f = 0.0F;
/* 119 */     int k1 = 0;
/*     */     
/* 121 */     for (int l1 = i; l1 < j; l1++) {
/* 122 */       for (int i2 = i1; i2 < j1; i2++) {
/* 123 */         int j2 = ((l1 != i && l1 != j - 1) ? 0 : 1) + ((i2 != i1 && i2 != j1 - 1) ? 0 : 1);
/* 124 */         if (j2 != 2) {
/* 125 */           for (int k2 = k; k2 < l; k2++) {
/* 126 */             if (j2 <= 0 || (k2 != k && k2 != l - 1)) {
/* 127 */               WrappedBlockState blockData = player.compensatedWorld.getBlock(l1, k2, i2);
/* 128 */               StateType blockMaterial = blockData.getType();
/*     */               
/* 130 */               if (blockMaterial != StateTypes.LILY_PAD && CollisionData.getData(blockMaterial).getMovementCollisionBox(player, player.getClientVersion(), blockData, l1, k2, i2).isIntersected(axisalignedbb1)) {
/* 131 */                 f += BlockProperties.getMaterialFriction(player, blockMaterial);
/* 132 */                 k1++;
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 140 */     return f / k1;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<VectorData> applyInputsToVelocityPossibilities(GrimPlayer player, Set<VectorData> possibleVectors, float speed) {
/* 145 */     List<VectorData> vectors = new ArrayList<>();
/*     */     
/* 147 */     for (VectorData data : possibleVectors) {
/*     */       
/* 149 */       data.input = new Vector3dm(player.vehicleData.vehicleForward, 0.0F, player.vehicleData.vehicleHorizontal);
/*     */       
/* 151 */       for (int applyStuckSpeed = 1; applyStuckSpeed >= 0 && (
/* 152 */         applyStuckSpeed != 0 || !player.isForceStuckSpeed()); applyStuckSpeed--) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 157 */         if (player.vehicleData.vehicleForward == 0.0F) {
/* 158 */           Vector3dm vector = data.vector.clone();
/* 159 */           controlBoat(player, vector, true);
/* 160 */           if (applyStuckSpeed != 0) vector.multiply(player.stuckSpeedMultiplier); 
/* 161 */           vectors.add(data.returnNewModified(vector, VectorData.VectorType.InputResult));
/*     */         } 
/*     */         
/* 164 */         controlBoat(player, data.vector, false);
/* 165 */         if (applyStuckSpeed != 0) data.vector.multiply(player.stuckSpeedMultiplier); 
/* 166 */         vectors.add(data);
/*     */       } 
/*     */     } 
/*     */     
/* 170 */     return vectors;
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<VectorData> fetchPossibleStartTickVectors(GrimPlayer player) {
/* 175 */     Set<VectorData> vectors = player.getPossibleVelocities();
/* 176 */     addFluidPushingToStartingVectors(player, vectors);
/*     */     
/* 178 */     for (VectorData data : vectors) {
/* 179 */       floatBoat(player, data.vector);
/*     */     }
/*     */     
/* 182 */     return vectors;
/*     */   }
/*     */ 
/*     */   
/*     */   public void endOfTick(GrimPlayer player, double d) {
/* 187 */     super.endOfTick(player, d);
/* 188 */     Collisions.handleInsideBlocks(player);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canSwimHop(GrimPlayer player) {
/* 193 */     return false;
/*     */   }
/*     */   
/*     */   private void floatBoat(GrimPlayer player, Vector3dm vector) {
/* 197 */     double d1 = player.hasGravity ? -0.03999999910593033D : 0.0D;
/* 198 */     double d2 = 0.0D;
/* 199 */     float invFriction = 0.05F;
/*     */     
/* 201 */     if (player.vehicleData.oldStatus == BoatEntityStatus.IN_AIR && player.vehicleData.status != BoatEntityStatus.IN_AIR && player.vehicleData.status != BoatEntityStatus.ON_LAND) {
/* 202 */       player.vehicleData.waterLevel = player.lastY + player.boundingBox.maxY - player.boundingBox.minY;
/*     */       
/* 204 */       player.lastY = (getWaterLevelAbove(player) - 0.5625F) + 0.101D;
/* 205 */       player.boundingBox = GetBoundingBox.getCollisionBoxForPlayer(player, player.lastX, player.lastY, player.lastZ);
/* 206 */       player.actualMovement = new Vector3dm(player.x - player.lastX, player.y - player.lastY, player.z - player.lastZ);
/* 207 */       vector.setY(0);
/*     */       
/* 209 */       player.vehicleData.lastYd = 0.0D;
/* 210 */       player.vehicleData.status = BoatEntityStatus.IN_WATER;
/*     */     } else {
/* 212 */       if (player.vehicleData.status == BoatEntityStatus.IN_WATER) {
/* 213 */         d2 = (player.vehicleData.waterLevel - player.lastY) / (player.boundingBox.maxY - player.boundingBox.minY);
/* 214 */         invFriction = 0.9F;
/* 215 */       } else if (player.vehicleData.status == BoatEntityStatus.UNDER_FLOWING_WATER) {
/* 216 */         d1 = -7.0E-4D;
/* 217 */         invFriction = 0.9F;
/* 218 */       } else if (player.vehicleData.status == BoatEntityStatus.UNDER_WATER) {
/* 219 */         d2 = 0.009999999776482582D;
/* 220 */         invFriction = 0.45F;
/* 221 */       } else if (player.vehicleData.status == BoatEntityStatus.IN_AIR) {
/* 222 */         invFriction = 0.9F;
/* 223 */       } else if (player.vehicleData.status == BoatEntityStatus.ON_LAND) {
/* 224 */         invFriction = player.vehicleData.landFriction;
/* 225 */         player.vehicleData.landFriction /= 2.0F;
/*     */       } 
/*     */       
/* 228 */       vector.setX(vector.getX() * invFriction);
/* 229 */       vector.setY(vector.getY() + d1);
/* 230 */       vector.setZ(vector.getZ() * invFriction);
/*     */       
/* 232 */       if (d2 > 0.0D) {
/* 233 */         double yVel = vector.getY();
/* 234 */         vector.setY((yVel + d2 * 0.06153846016296973D) * 0.75D);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public float getWaterLevelAbove(GrimPlayer player) {
/* 240 */     SimpleCollisionBox axisalignedbb = player.boundingBox;
/* 241 */     int i = (int)Math.floor(axisalignedbb.minX);
/* 242 */     int j = (int)Math.ceil(axisalignedbb.maxX);
/* 243 */     int k = (int)Math.floor(axisalignedbb.maxY);
/* 244 */     int l = (int)Math.ceil(axisalignedbb.maxY - player.vehicleData.lastYd);
/* 245 */     int i1 = (int)Math.floor(axisalignedbb.minZ);
/* 246 */     int j1 = (int)Math.ceil(axisalignedbb.maxZ);
/*     */     
/*     */     int k1;
/* 249 */     label22: for (k1 = k; k1 < l; k1++) {
/* 250 */       float f = 0.0F;
/*     */       
/* 252 */       for (int l1 = i; l1 < j; l1++) {
/* 253 */         for (int i2 = i1; i2 < j1; i2++) {
/* 254 */           double level = player.compensatedWorld.getWaterFluidLevelAt(l1, k1, i2);
/*     */           
/* 256 */           f = (float)Math.max(f, level);
/*     */           
/* 258 */           if (f >= 1.0F) {
/*     */             continue label22;
/*     */           }
/*     */         } 
/*     */       } 
/*     */       
/* 264 */       if (f < 1.0F) {
/* 265 */         return k1 + f;
/*     */       }
/*     */     } 
/*     */     
/* 269 */     return (l + 1);
/*     */   }
/*     */   
/*     */   private void controlBoat(GrimPlayer player, Vector3dm vector, boolean intermediate) {
/* 273 */     float f = 0.0F;
/* 274 */     if (player.vehicleData.vehicleHorizontal != 0.0F && !intermediate && player.vehicleData.vehicleForward == 0.0F) {
/* 275 */       f += 0.005F;
/*     */     }
/*     */ 
/*     */     
/* 279 */     if (intermediate || player.vehicleData.vehicleForward > 0.1D) {
/* 280 */       f += 0.04F;
/*     */     }
/*     */     
/* 283 */     if (intermediate || player.vehicleData.vehicleForward < -0.01D) {
/* 284 */       f -= 0.005F;
/*     */     }
/*     */     
/* 287 */     vector.add(new Vector3dm((player.trigHandler.sin(-player.xRot * 0.017453292F) * f), 0.0D, (player.trigHandler.cos(player.xRot * 0.017453292F) * f)));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\predictionengine\predictions\rideable\PredictionEngineBoat.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */