/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.format;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.ComponentBuilder;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.ComponentBuilderApplicable;
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
/*    */ public interface StyleBuilderApplicable
/*    */   extends ComponentBuilderApplicable
/*    */ {
/*    */   @Contract(mutates = "param")
/*    */   void styleApply(Style.Builder paramBuilder);
/*    */   
/*    */   default void componentBuilderApply(@NotNull ComponentBuilder<?, ?> component) {
/* 50 */     component.style(this::styleApply);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\format\StyleBuilderApplicable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */