/*    */ package ac.grim.grimac.utils.collisions;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public final class AxisUtil {
/*    */   @Generated
/*    */   private AxisUtil() {
/* 10 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*    */   }
/*    */   @NotNull
/*    */   public static SimpleCollisionBox combine(@NotNull SimpleCollisionBox base, @NotNull SimpleCollisionBox toMerge) {
/* 14 */     boolean insideX = (toMerge.minX <= base.minX && toMerge.maxX >= base.maxX);
/* 15 */     boolean insideY = (toMerge.minY <= base.minY && toMerge.maxY >= base.maxY);
/* 16 */     boolean insideZ = (toMerge.minZ <= base.minZ && toMerge.maxZ >= base.maxZ);
/*    */     
/* 18 */     if (insideX && insideY && !insideZ)
/* 19 */       return new SimpleCollisionBox(base.minX, base.maxY, Math.min(base.minZ, toMerge.minZ), base.minX, base.maxY, Math.max(base.maxZ, toMerge.maxZ)); 
/* 20 */     if (insideX && !insideY && insideZ)
/* 21 */       return new SimpleCollisionBox(base.minX, Math.min(base.minY, toMerge.minY), base.minZ, base.maxX, Math.max(base.maxY, toMerge.maxY), base.maxZ); 
/* 22 */     if (!insideX && insideY && insideZ) {
/* 23 */       return new SimpleCollisionBox(Math.min(base.minX, toMerge.maxX), base.minY, base.maxZ, Math.max(base.minX, toMerge.minX), base.minY, base.maxZ);
/*    */     }
/*    */     
/* 26 */     return base;
/*    */   }
/*    */   
/*    */   @Contract(pure = true)
/*    */   public static boolean isSameAxis(BlockFace one, BlockFace two) {
/* 31 */     return (one == two || one == two.getOppositeFace());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\collisions\AxisUtil.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */