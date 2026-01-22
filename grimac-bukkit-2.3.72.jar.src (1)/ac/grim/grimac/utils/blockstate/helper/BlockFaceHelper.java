/*    */ package ac.grim.grimac.utils.blockstate.helper;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ 
/*    */ public final class BlockFaceHelper {
/*    */   @Generated
/*    */   private BlockFaceHelper() {
/* 10 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*    */   } @Contract(pure = true)
/*    */   public static boolean isFaceVertical(@Nullable BlockFace face) {
/* 13 */     return (face == BlockFace.UP || face == BlockFace.DOWN);
/*    */   }
/*    */   
/*    */   @Contract(pure = true)
/*    */   public static boolean isFaceHorizontal(@Nullable BlockFace face) {
/* 18 */     return (face == BlockFace.NORTH || face == BlockFace.EAST || face == BlockFace.SOUTH || face == BlockFace.WEST);
/*    */   }
/*    */   
/*    */   @Contract(pure = true)
/*    */   public static BlockFace getClockWise(@NotNull BlockFace face) {
/* 23 */     switch (face) { case NORTH: case SOUTH: case WEST:  }  return 
/*    */ 
/*    */ 
/*    */       
/* 27 */       BlockFace.SOUTH;
/*    */   }
/*    */ 
/*    */   
/*    */   @Contract(pure = true)
/*    */   public static BlockFace getPEClockWise(@NotNull BlockFace face) {
/* 33 */     switch (face) { case NORTH: case SOUTH: case WEST:  }  return 
/*    */ 
/*    */ 
/*    */       
/* 37 */       BlockFace.SOUTH;
/*    */   }
/*    */ 
/*    */   
/*    */   @Contract(pure = true)
/*    */   public static BlockFace getCounterClockwise(@NotNull BlockFace face) {
/* 43 */     switch (face) { case NORTH: case SOUTH: case WEST:  }  return 
/*    */ 
/*    */ 
/*    */       
/* 47 */       BlockFace.NORTH;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\blockstate\helper\BlockFaceHelper.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */