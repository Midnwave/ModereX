/*     */ package ac.grim.grimac.utils.nmsutil;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*     */ import ac.grim.grimac.utils.collisions.CollisionData;
/*     */ import ac.grim.grimac.utils.collisions.HitboxData;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.CollisionBox;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*     */ import ac.grim.grimac.utils.data.HitData;
/*     */ import ac.grim.grimac.utils.data.Pair;
/*     */ import ac.grim.grimac.utils.math.GrimMath;
/*     */ import ac.grim.grimac.utils.math.Vector3dm;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.function.BiFunction;
/*     */ import lombok.Generated;
/*     */ 
/*     */ public final class WorldRayTrace {
/*     */   @Generated
/*     */   private WorldRayTrace() {
/*  26 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*     */   } public static HitData getNearestBlockHitResult(GrimPlayer player, StateType heldItem, boolean sourcesHaveHitbox, boolean fluidPlacement, boolean itemUsePlacement) {
/*  28 */     Vector3d startingPos = new Vector3d(player.x, player.y + player.getEyeHeight(), player.z);
/*  29 */     Vector3dm startingVec = new Vector3dm(startingPos.getX(), startingPos.getY(), startingPos.getZ());
/*  30 */     Ray trace = new Ray(player, startingPos.getX(), startingPos.getY(), startingPos.getZ(), player.xRot, player.yRot);
/*  31 */     double distance = (itemUsePlacement && player.getClientVersion().isOlderThan(ClientVersion.V_1_20_5)) ? 5.0D : player.compensatedEntities.self.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
/*  32 */     Vector3dm endVec = trace.getPointAtDistance(distance);
/*  33 */     Vector3d endPos = new Vector3d(endVec.getX(), endVec.getY(), endVec.getZ());
/*     */     
/*  35 */     return traverseBlocks(player, startingPos, endPos, (block, vector3i) -> {
/*     */           if (fluidPlacement && player.getClientVersion().isOlderThan(ClientVersion.V_1_13) && CollisionData.getData(block.getType()).getMovementCollisionBox(player, player.getClientVersion(), block, vector3i.getX(), vector3i.getY(), vector3i.getZ()).isNull()) {
/*     */             return null;
/*     */           }
/*     */           CollisionBox data = HitboxData.getBlockHitbox(player, heldItem, player.getClientVersion(), block, false, vector3i.getX(), vector3i.getY(), vector3i.getZ());
/*     */           List<SimpleCollisionBox> boxes = new ArrayList<>();
/*     */           data.downCast(boxes);
/*     */           double bestHitResult = Double.MAX_VALUE;
/*     */           Vector3dm bestHitLoc = null;
/*     */           BlockFace bestFace = null;
/*     */           for (SimpleCollisionBox box : boxes) {
/*     */             Pair<Vector3dm, BlockFace> intercept = ReachUtils.calculateIntercept(box, trace.getOrigin(), trace.getPointAtDistance(distance));
/*     */             if (intercept.first() == null) {
/*     */               continue;
/*     */             }
/*     */             Vector3dm hitLoc = (Vector3dm)intercept.first();
/*     */             if (hitLoc.distanceSquared(startingVec) < bestHitResult) {
/*     */               bestHitResult = hitLoc.distanceSquared(startingVec);
/*     */               bestHitLoc = hitLoc;
/*     */               bestFace = (BlockFace)intercept.second();
/*     */             } 
/*     */           } 
/*     */           if (bestHitLoc != null) {
/*     */             return new HitData(vector3i, bestHitLoc, bestFace, block);
/*     */           }
/*     */           if (sourcesHaveHitbox && (player.compensatedWorld.isWaterSourceBlock(vector3i.getX(), vector3i.getY(), vector3i.getZ()) || player.compensatedWorld.getLavaFluidLevelAt(vector3i.getX(), vector3i.getY(), vector3i.getZ()) == 0.8888888955116272D)) {
/*     */             double waterHeight = player.getClientVersion().isOlderThan(ClientVersion.V_1_13) ? 1.0D : player.compensatedWorld.getFluidLevelAt(vector3i.getX(), vector3i.getY(), vector3i.getZ());
/*     */             SimpleCollisionBox box = new SimpleCollisionBox(vector3i.getX(), vector3i.getY(), vector3i.getZ(), (vector3i.getX() + 1), vector3i.getY() + waterHeight, (vector3i.getZ() + 1));
/*     */             Pair<Vector3dm, BlockFace> intercept = ReachUtils.calculateIntercept(box, trace.getOrigin(), trace.getPointAtDistance(distance));
/*     */             if (intercept.first() != null) {
/*     */               return new HitData(vector3i, (Vector3dm)intercept.first(), (BlockFace)intercept.second(), block);
/*     */             }
/*     */           } 
/*     */           return null;
/*     */         });
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
/*     */   public static HitData traverseBlocks(GrimPlayer player, Vector3d start, Vector3d end, BiFunction<WrappedBlockState, Vector3i, HitData> predicate) {
/*  90 */     double endX = GrimMath.lerp(-1.0E-7D, end.x, start.x);
/*  91 */     double endY = GrimMath.lerp(-1.0E-7D, end.y, start.y);
/*  92 */     double endZ = GrimMath.lerp(-1.0E-7D, end.z, start.z);
/*  93 */     double startX = GrimMath.lerp(-1.0E-7D, start.x, end.x);
/*  94 */     double startY = GrimMath.lerp(-1.0E-7D, start.y, end.y);
/*  95 */     double startZ = GrimMath.lerp(-1.0E-7D, start.z, end.z);
/*  96 */     int floorStartX = GrimMath.floor(startX);
/*  97 */     int floorStartY = GrimMath.floor(startY);
/*  98 */     int floorStartZ = GrimMath.floor(startZ);
/*     */ 
/*     */     
/* 101 */     if (start.equals(end)) return null;
/*     */     
/* 103 */     WrappedBlockState state = player.compensatedWorld.getBlock(floorStartX, floorStartY, floorStartZ);
/* 104 */     HitData apply = predicate.apply(state, new Vector3i(floorStartX, floorStartY, floorStartZ));
/*     */     
/* 106 */     if (apply != null) {
/* 107 */       return apply;
/*     */     }
/*     */     
/* 110 */     double xDiff = endX - startX;
/* 111 */     double yDiff = endY - startY;
/* 112 */     double zDiff = endZ - startZ;
/* 113 */     double xSign = Math.signum(xDiff);
/* 114 */     double ySign = Math.signum(yDiff);
/* 115 */     double zSign = Math.signum(zDiff);
/*     */     
/* 117 */     double posXInverse = (xSign == 0.0D) ? Double.MAX_VALUE : (xSign / xDiff);
/* 118 */     double posYInverse = (ySign == 0.0D) ? Double.MAX_VALUE : (ySign / yDiff);
/* 119 */     double posZInverse = (zSign == 0.0D) ? Double.MAX_VALUE : (zSign / zDiff);
/*     */     
/* 121 */     double d12 = posXInverse * ((xSign > 0.0D) ? (1.0D - GrimMath.frac(startX)) : GrimMath.frac(startX));
/* 122 */     double d13 = posYInverse * ((ySign > 0.0D) ? (1.0D - GrimMath.frac(startY)) : GrimMath.frac(startY));
/* 123 */     double d14 = posZInverse * ((zSign > 0.0D) ? (1.0D - GrimMath.frac(startZ)) : GrimMath.frac(startZ));
/*     */ 
/*     */     
/* 126 */     while (d12 <= 1.0D || d13 <= 1.0D || d14 <= 1.0D) {
/* 127 */       if (d12 < d13) {
/* 128 */         if (d12 < d14) {
/* 129 */           floorStartX = (int)(floorStartX + xSign);
/* 130 */           d12 += posXInverse;
/*     */         } else {
/* 132 */           floorStartZ = (int)(floorStartZ + zSign);
/* 133 */           d14 += posZInverse;
/*     */         } 
/* 135 */       } else if (d13 < d14) {
/* 136 */         floorStartY = (int)(floorStartY + ySign);
/* 137 */         d13 += posYInverse;
/*     */       } else {
/* 139 */         floorStartZ = (int)(floorStartZ + zSign);
/* 140 */         d14 += posZInverse;
/*     */       } 
/*     */       
/* 143 */       state = player.compensatedWorld.getBlock(floorStartX, floorStartY, floorStartZ);
/* 144 */       apply = predicate.apply(state, new Vector3i(floorStartX, floorStartY, floorStartZ));
/*     */       
/* 146 */       if (apply != null) {
/* 147 */         return apply;
/*     */       }
/*     */     } 
/*     */     
/* 151 */     return null;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\nmsutil\WorldRayTrace.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */