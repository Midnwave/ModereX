/*    */ package ac.grim.grimac.utils.math;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public final class LegacyFastMath {
/*    */   @Generated
/*    */   private LegacyFastMath() {
/*  8 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*  9 */   } private static final float[] SIN_TABLE_FAST = new float[4096];
/*    */   static {
/*    */     int i;
/* 12 */     for (i = 0; i < 4096; i++) {
/* 13 */       SIN_TABLE_FAST[i] = (float)Math.sin(((i + 0.5F) / 4096.0F * 6.2831855F));
/*    */     }
/*    */     
/* 16 */     for (i = 0; i < 360; i += 90) {
/* 17 */       SIN_TABLE_FAST[(int)(i * 11.377778F) & 0xFFF] = (float)Math.sin(GrimMath.radians(i));
/*    */     }
/*    */   }
/*    */   
/*    */   @Contract(pure = true)
/*    */   public static float sin(float value) {
/* 23 */     return SIN_TABLE_FAST[(int)(value * 651.8986F) & 0xFFF];
/*    */   }
/*    */   
/*    */   @Contract(pure = true)
/*    */   public static float cos(float value) {
/* 28 */     return SIN_TABLE_FAST[(int)((value + 1.5707964F) * 651.8986F) & 0xFFF];
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\math\LegacyFastMath.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */