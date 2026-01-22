/*    */ package ac.grim.grimac.utils.math;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*    */ import lombok.Generated;
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
/*    */ public final class OptifineFastMath
/*    */ {
/*    */   @Generated
/*    */   private OptifineFastMath() {
/* 43 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/* 44 */   } private static final float[] SIN_TABLE_FAST = new float[4096];
/* 45 */   private static final float radToIndex = roundToFloat(651.8986469044033D);
/*    */   
/*    */   static {
/* 48 */     for (int j = 0; j < SIN_TABLE_FAST.length; j++) {
/* 49 */       SIN_TABLE_FAST[j] = roundToFloat(StrictMath.sin(j * Math.PI * 2.0D / 4096.0D));
/*    */     }
/*    */   }
/*    */   
/*    */   @Contract(pure = true)
/*    */   public static float sin(float value) {
/* 55 */     return SIN_TABLE_FAST[(int)(value * radToIndex) & 0xFFF];
/*    */   }
/*    */   
/*    */   @Contract(pure = true)
/*    */   public static float cos(float value) {
/* 60 */     return SIN_TABLE_FAST[(int)(value * radToIndex + 1024.0F) & 0xFFF];
/*    */   }
/*    */   
/*    */   @Contract(pure = true)
/*    */   public static float roundToFloat(double value) {
/* 65 */     return (float)(Math.round(value * 1.0E8D) / 1.0E8D);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\math\OptifineFastMath.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */