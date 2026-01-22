/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util;
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
/*    */ public final class MathUtil
/*    */ {
/*    */   public static int clamp(int value, int min, int max) {
/* 27 */     return (value < min) ? min : Math.min(value, max);
/*    */   }
/*    */   
/*    */   public static double clamp(double value, double min, double max) {
/* 31 */     return (value < min) ? min : Math.min(value, max);
/*    */   }
/*    */   
/*    */   public static float clamp(float value, float min, float max) {
/* 35 */     return (value < min) ? min : Math.min(value, max);
/*    */   }
/*    */   
/*    */   public static int floor(double value) {
/* 39 */     int temp = (int)value;
/* 40 */     return (value < temp) ? (temp - 1) : temp;
/*    */   }
/*    */   
/*    */   public static int floor(float value) {
/* 44 */     int temp = (int)value;
/* 45 */     return (value < temp) ? (temp - 1) : temp;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\MathUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */