/*    */ package ac.grim.grimac.shaded.kyori.adventure.util;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.CheckReturnValue;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.builder.AbstractBuilder;
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
/*    */ public interface InheritanceAwareMap<C, V>
/*    */ {
/*    */   @NotNull
/*    */   static <K, E> InheritanceAwareMap<K, E> empty() {
/* 56 */     return InheritanceAwareMapImpl.EMPTY;
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
/*    */   static <K, E> Builder<K, E> builder() {
/* 68 */     return new InheritanceAwareMapImpl.BuilderImpl<>();
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
/*    */   
/*    */   static <K, E> Builder<K, E> builder(InheritanceAwareMap<? extends K, ? extends E> existing) {
/* 81 */     return (new InheritanceAwareMapImpl.BuilderImpl<>())
/* 82 */       .putAll(existing);
/*    */   }
/*    */   
/*    */   boolean containsKey(@NotNull Class<? extends C> paramClass);
/*    */   
/*    */   @Nullable
/*    */   V get(@NotNull Class<? extends C> paramClass);
/*    */   
/*    */   @CheckReturnValue
/*    */   @NotNull
/*    */   InheritanceAwareMap<C, V> with(@NotNull Class<? extends C> paramClass, @NotNull V paramV);
/*    */   
/*    */   @CheckReturnValue
/*    */   @NotNull
/*    */   InheritanceAwareMap<C, V> without(@NotNull Class<? extends C> paramClass);
/*    */   
/*    */   public static interface Builder<C, V> extends AbstractBuilder<InheritanceAwareMap<C, V>> {
/*    */     @NotNull
/*    */     Builder<C, V> strict(boolean param1Boolean);
/*    */     
/*    */     @NotNull
/*    */     Builder<C, V> put(@NotNull Class<? extends C> param1Class, @NotNull V param1V);
/*    */     
/*    */     @NotNull
/*    */     Builder<C, V> remove(@NotNull Class<? extends C> param1Class);
/*    */     
/*    */     @NotNull
/*    */     Builder<C, V> putAll(@NotNull InheritanceAwareMap<? extends C, ? extends V> param1InheritanceAwareMap);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventur\\util\InheritanceAwareMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */