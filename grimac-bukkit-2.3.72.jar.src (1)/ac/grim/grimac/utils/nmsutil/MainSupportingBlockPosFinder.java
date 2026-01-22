/*    */ package ac.grim.grimac.utils.nmsutil;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*    */ import ac.grim.grimac.utils.data.MainSupportingBlockData;
/*    */ import com.google.common.util.concurrent.AtomicDouble;
/*    */ import java.util.Optional;
/*    */ import java.util.concurrent.atomic.AtomicReference;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public final class MainSupportingBlockPosFinder {
/*    */   @Generated
/*    */   private MainSupportingBlockPosFinder() {
/* 15 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*    */   } public static MainSupportingBlockData findMainSupportingBlockPos(GrimPlayer player, MainSupportingBlockData lastSupportingBlock, Vector3d lastMovement, SimpleCollisionBox maxPose, boolean isOnGround) {
/* 17 */     if (!isOnGround) {
/* 18 */       return new MainSupportingBlockData(null, false);
/*    */     }
/*    */     
/* 21 */     SimpleCollisionBox slightlyBelowPlayer = new SimpleCollisionBox(maxPose.minX, maxPose.minY - 1.0E-6D, maxPose.minZ, maxPose.maxX, maxPose.minY, maxPose.maxZ);
/*    */     
/* 23 */     Optional<Vector3i> supportingBlock = findSupportingBlock(player, slightlyBelowPlayer);
/* 24 */     if (supportingBlock.isEmpty() && !lastSupportingBlock.lastOnGroundAndNoBlock()) {
/* 25 */       if (lastMovement != null) {
/* 26 */         SimpleCollisionBox aabb2 = slightlyBelowPlayer.offset(-lastMovement.x, 0.0D, -lastMovement.z);
/* 27 */         supportingBlock = findSupportingBlock(player, aabb2);
/* 28 */         return new MainSupportingBlockData(supportingBlock.orElse(null), true);
/*    */       } 
/*    */     } else {
/* 31 */       return new MainSupportingBlockData(supportingBlock.orElse(null), true);
/*    */     } 
/*    */     
/* 34 */     return new MainSupportingBlockData(null, true);
/*    */   }
/*    */   
/*    */   private static Optional<Vector3i> findSupportingBlock(GrimPlayer player, SimpleCollisionBox searchBox) {
/* 38 */     Vector3d playerPos = new Vector3d(player.x, player.y, player.z);
/*    */     
/* 40 */     AtomicReference<Vector3i> bestBlockPos = new AtomicReference<>();
/* 41 */     AtomicDouble blockPosDistance = new AtomicDouble(Double.MAX_VALUE);
/*    */     
/* 43 */     Collisions.forEachCollisionBox(player, searchBox, pos -> {
/*    */           Vector3i blockPos = pos.toVector3i();
/*    */           
/*    */           Vector3d blockPosAsVector3d = new Vector3d(blockPos.getX() + 0.5D, blockPos.getY() + 0.5D, blockPos.getZ() + 0.5D);
/*    */           
/*    */           double distance = playerPos.distanceSquared(blockPosAsVector3d);
/*    */           
/*    */           if (distance < blockPosDistance.get() || (distance == blockPosDistance.get() && (bestBlockPos.get() == null || firstHasPriorityOverSecond(blockPos, bestBlockPos.get())))) {
/*    */             bestBlockPos.set(blockPos);
/*    */             blockPosDistance.set(distance);
/*    */           } 
/*    */         });
/* 55 */     return Optional.ofNullable(bestBlockPos.get());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static boolean firstHasPriorityOverSecond(Vector3i first, Vector3i second) {
/* 71 */     if (first.getY() < second.getY()) return true;
/*    */     
/* 73 */     double sumX = (second.getX() - first.getX());
/* 74 */     double sumY = (second.getZ() - first.getZ());
/*    */     
/* 76 */     double horizontalSumTotal = sumX + sumY;
/* 77 */     if (horizontalSumTotal == 0.0D)
/*    */     {
/* 79 */       return (sumX < 0.0D);
/*    */     }
/*    */ 
/*    */     
/* 83 */     return (horizontalSumTotal < 0.0D);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\nmsutil\MainSupportingBlockPosFinder.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */