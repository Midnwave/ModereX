/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.reflection;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
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
/*    */ public class NestedClassUtil
/*    */ {
/*    */   public static Class<?> getNestedClass(Class<?> cls, String name) {
/* 25 */     if (cls == null) {
/* 26 */       return null;
/*    */     }
/* 28 */     for (Class<?> subClass : cls.getDeclaredClasses()) {
/* 29 */       if (subClass.getSimpleName().equals(name)) {
/* 30 */         return subClass;
/*    */       }
/*    */     } 
/* 33 */     return null;
/*    */   }
/*    */   
/*    */   public static Class<?> getNestedClass(Class<?> cls, int index) {
/* 37 */     if (cls == null) {
/* 38 */       return null;
/*    */     }
/* 40 */     int currentIndex = 0;
/* 41 */     for (Class<?> subClass : cls.getDeclaredClasses()) {
/* 42 */       if (index == currentIndex++) {
/* 43 */         return subClass;
/*    */       }
/*    */     } 
/* 46 */     return null;
/*    */   }
/*    */   
/*    */   public static Class<?> getNestedClass(Class<?> cls, Annotation annotation, int index) {
/* 50 */     int currentIndex = 0;
/* 51 */     for (Class<?> subClass : cls.getDeclaredClasses()) {
/* 52 */       if (subClass.isAnnotationPresent((Class)annotation.getClass()) && 
/* 53 */         index == currentIndex++) {
/* 54 */         return subClass;
/*    */       }
/*    */     } 
/*    */     
/* 58 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\reflection\NestedClassUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */