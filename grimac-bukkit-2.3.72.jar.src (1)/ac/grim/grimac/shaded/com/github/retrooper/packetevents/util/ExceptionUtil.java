/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util;
/*    */ 
/*    */ import java.util.Set;
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
/*    */ public class ExceptionUtil
/*    */ {
/*    */   public static boolean isException(Throwable t, Class<?> clazz) {
/* 25 */     while (t != null) {
/* 26 */       if (clazz.isAssignableFrom(t.getClass())) {
/* 27 */         return true;
/*    */       }
/*    */       
/* 30 */       t = t.getCause();
/*    */     } 
/* 32 */     return false;
/*    */   }
/*    */   
/*    */   public static boolean isExceptionContainedIn(Throwable t, Set<Class<? extends Throwable>> exceptions) {
/* 36 */     return exceptions.contains(t.getClass());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\ExceptionUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */