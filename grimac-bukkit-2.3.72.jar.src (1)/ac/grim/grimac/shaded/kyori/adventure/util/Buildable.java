/*    */ package ac.grim.grimac.shaded.kyori.adventure.util;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.builder.AbstractBuilder;
/*    */ import java.util.function.Consumer;
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
/*    */ public interface Buildable<R, B extends Buildable.Builder<R>>
/*    */ {
/*    */   @Deprecated
/*    */   @Contract(mutates = "param1")
/*    */   @NotNull
/*    */   static <R extends Buildable<R, B>, B extends Builder<R>> R configureAndBuild(@NotNull B builder, @Nullable Consumer<? super B> consumer) {
/* 54 */     return (R)AbstractBuilder.configureAndBuild((AbstractBuilder)builder, consumer);
/*    */   }
/*    */   
/*    */   @Contract(value = "-> new", pure = true)
/*    */   @NotNull
/*    */   B toBuilder();
/*    */   
/*    */   @Deprecated
/*    */   public static interface Builder<R> extends AbstractBuilder<R> {
/*    */     @Contract(value = "-> new", pure = true)
/*    */     @NotNull
/*    */     R build();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventur\\util\Buildable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */