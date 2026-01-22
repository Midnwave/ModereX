/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.flattener;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.builder.AbstractBuilder;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.util.Buildable;
/*    */ import java.util.function.BiConsumer;
/*    */ import java.util.function.Consumer;
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
/*    */ public interface ComponentFlattener
/*    */   extends Buildable<ComponentFlattener, ComponentFlattener.Builder>
/*    */ {
/*    */   public static final int NO_NESTING_LIMIT = -1;
/*    */   
/*    */   @NotNull
/*    */   static Builder builder() {
/* 56 */     return new ComponentFlattenerImpl.BuilderImpl();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   static ComponentFlattener basic() {
/* 69 */     return ComponentFlattenerImpl.BASIC;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   static ComponentFlattener textOnly() {
/* 81 */     return ComponentFlattenerImpl.TEXT_ONLY;
/*    */   }
/*    */   
/*    */   void flatten(@NotNull Component paramComponent, @NotNull FlattenerListener paramFlattenerListener);
/*    */   
/*    */   public static interface Builder extends AbstractBuilder<ComponentFlattener>, Buildable.Builder<ComponentFlattener> {
/*    */     @NotNull
/*    */     <T extends Component> Builder mapper(@NotNull Class<T> param1Class, @NotNull Function<T, String> param1Function);
/*    */     
/*    */     @NotNull
/*    */     <T extends Component> Builder complexMapper(@NotNull Class<T> param1Class, @NotNull BiConsumer<T, Consumer<Component>> param1BiConsumer);
/*    */     
/*    */     @NotNull
/*    */     Builder unknownMapper(@Nullable Function<Component, String> param1Function);
/*    */     
/*    */     @NotNull
/*    */     Builder nestingLimit(int param1Int);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\flattener\ComponentFlattener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */