/*    */ package ac.grim.grimac.utils.math;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*    */ 
/*    */ public final class VanillaMath {
/*    */   @Generated
/*    */   private VanillaMath() {
/*  7 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*  8 */   } private static final float[] SIN = new float[65536];
/*    */   
/*    */   static {
/* 11 */     for (int i = 0; i < SIN.length; i++) {
/* 12 */       SIN[i] = (float)StrictMath.sin(i * Math.PI * 2.0D / 65536.0D);
/*    */     }
/*    */   }
/*    */   
/*    */   @Contract(pure = true)
/*    */   public static float sin(float value) {
/* 18 */     return SIN[(int)(value * 10430.378F) & 0xFFFF];
/*    */   }
/*    */   
/*    */   @Contract(pure = true)
/*    */   public static float cos(float value) {
/* 23 */     return SIN[(int)(value * 10430.378F + 16384.0F) & 0xFFFF];
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\math\VanillaMath.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */