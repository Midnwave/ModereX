/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.flattener;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
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
/*    */ @FunctionalInterface
/*    */ public interface FlattenerListener
/*    */ {
/*    */   default void pushStyle(@NotNull Style style) {}
/*    */   
/*    */   void component(@NotNull String paramString);
/*    */   
/*    */   default boolean shouldContinue() {
/* 60 */     return true;
/*    */   }
/*    */   
/*    */   default void popStyle(@NotNull Style style) {}
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\flattener\FlattenerListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */