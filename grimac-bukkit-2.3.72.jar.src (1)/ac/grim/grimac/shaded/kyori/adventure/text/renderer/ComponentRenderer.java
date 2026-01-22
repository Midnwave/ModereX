/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.renderer;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import java.util.function.Function;
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
/*    */ public interface ComponentRenderer<C>
/*    */ {
/*    */   @NotNull
/*    */   Component render(@NotNull Component paramComponent, @NotNull C paramC);
/*    */   
/*    */   default <T> ComponentRenderer<T> mapContext(Function<T, C> transformer) {
/* 56 */     return (component, ctx) -> render(component, transformer.apply(ctx));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\renderer\ComponentRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */