/*    */ package ac.grim.grimac.utils.nmsutil;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ 
/*    */ public final class CheckIfChunksLoaded {
/*    */   @Generated
/*    */   private CheckIfChunksLoaded() {
/*  7 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*    */   } public static boolean isChunksUnloadedAt(GrimPlayer player, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
/*  9 */     if (maxY < player.compensatedWorld.getMinHeight() || minY >= player.compensatedWorld.getMaxHeight()) {
/* 10 */       return true;
/*    */     }
/*    */     
/* 13 */     minX >>= 4;
/* 14 */     minZ >>= 4;
/* 15 */     maxX >>= 4;
/* 16 */     maxZ >>= 4;
/*    */     
/* 18 */     for (int i = minX; i <= maxX; i++) {
/* 19 */       for (int j = minZ; j <= maxZ; j++) {
/* 20 */         if (player.compensatedWorld.getChunk(i, j) == null) {
/* 21 */           return true;
/*    */         }
/*    */       } 
/*    */     } 
/*    */     
/* 26 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\nmsutil\CheckIfChunksLoaded.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */