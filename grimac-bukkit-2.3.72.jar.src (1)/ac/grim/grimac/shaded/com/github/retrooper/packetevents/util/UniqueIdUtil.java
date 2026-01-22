/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util;
/*    */ 
/*    */ import java.util.UUID;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class UniqueIdUtil
/*    */ {
/*    */   public static UUID fromIntArray(int[] array) {
/* 29 */     if (array.length != 4) {
/* 30 */       throw new IllegalStateException("Invalid encoded uuid length: " + array.length + " != 4");
/*    */     }
/* 32 */     return new UUID(array[0] << 32L | array[1] & 0xFFFFFFFFL, array[2] << 32L | array[3] & 0xFFFFFFFFL);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static int[] toIntArray(UUID uniqueId) {
/* 39 */     return new int[] {
/* 40 */         (int)(uniqueId.getMostSignificantBits() >> 32L), 
/* 41 */         (int)uniqueId.getMostSignificantBits(), 
/* 42 */         (int)(uniqueId.getLeastSignificantBits() >> 32L), 
/* 43 */         (int)uniqueId.getLeastSignificantBits()
/*    */       };
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\UniqueIdUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */