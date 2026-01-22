/*    */ package ac.grim.grimac.utils.reflection;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import java.lang.reflect.Method;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public final class ReflectionUtils {
/*    */   @Generated
/*    */   private ReflectionUtils() {
/* 10 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*    */   }
/*    */   public static boolean hasClass(String className) {
/* 13 */     return (getClass(className) != null);
/*    */   }
/*    */   
/*    */   public static boolean hasMethod(@NotNull Class<?> clazz, String methodName, Class<?>... parameterTypes) {
/* 17 */     return (getMethod(clazz, methodName, parameterTypes) != null);
/*    */   }
/*    */   @Nullable
/*    */   public static Method getMethod(@NotNull Class<?> clazz, @NotNull String methodName, Class<?>... parameterTypes) {
/*    */     try {
/* 22 */       return clazz.getMethod(methodName, parameterTypes);
/* 23 */     } catch (NoSuchMethodException e) {
/* 24 */       while (clazz != null) {
/*    */         try {
/* 26 */           return clazz.getDeclaredMethod(methodName, parameterTypes);
/* 27 */         } catch (NoSuchMethodException ignored) {
/* 28 */           clazz = clazz.getSuperclass();
/*    */         } 
/*    */       } 
/*    */ 
/*    */       
/* 33 */       return null;
/*    */     } 
/*    */   } @Nullable
/*    */   public static Class<?> getClass(String className) {
/*    */     try {
/* 38 */       return Class.forName(className);
/* 39 */     } catch (ClassNotFoundException e) {
/* 40 */       return null;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\reflection\ReflectionUtils.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */