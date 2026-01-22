/*    */ package ac.grim.grimac.shaded.kyori.adventure.util;
/*    */ 
/*    */ import java.util.ServiceLoader;
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
/*    */ final class Services0
/*    */ {
/*    */   static <S> ServiceLoader<S> loader(Class<S> type) {
/* 33 */     return ServiceLoader.load(type, type.getClassLoader());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventur\\util\Services0.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */