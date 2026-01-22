/*    */ package ac.grim.grimac.shaded.kyori.adventure.text;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
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
/*    */ public interface TranslationArgumentLike
/*    */   extends ComponentLike
/*    */ {
/*    */   @NotNull
/*    */   TranslationArgument asTranslationArgument();
/*    */   
/*    */   @NotNull
/*    */   default Component asComponent() {
/* 46 */     return asTranslationArgument().asComponent();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\TranslationArgumentLike.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */