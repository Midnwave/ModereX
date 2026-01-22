/*     */ package ac.grim.grimac.shaded.kyori.adventure.pointer;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.builder.AbstractBuilder;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Buildable;
/*     */ import java.util.Optional;
/*     */ import java.util.function.Supplier;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface Pointers
/*     */   extends Buildable<Pointers, Pointers.Builder>
/*     */ {
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   static Pointers empty() {
/*  49 */     return PointersImpl.EMPTY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   static Builder builder() {
/*  61 */     return new PointersImpl.BuilderImpl();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   <T> Optional<T> get(@NotNull Pointer<T> paramPointer);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract("_, null -> _; _, !null -> !null")
/*     */   @Nullable
/*     */   default <T> T getOrDefault(@NotNull Pointer<T> pointer, @Nullable T defaultValue) {
/*  88 */     return get(pointer).orElse(defaultValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default <T> T getOrDefaultFrom(@NotNull Pointer<T> pointer, @NotNull Supplier<? extends T> defaultValue) {
/* 104 */     return get(pointer).orElseGet(defaultValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   <T> boolean supports(@NotNull Pointer<T> paramPointer);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface Builder
/*     */     extends AbstractBuilder<Pointers>, Buildable.Builder<Pointers>
/*     */   {
/*     */     @Contract("_, _ -> this")
/*     */     @NotNull
/*     */     default <T> Builder withStatic(@NotNull Pointer<T> pointer, @Nullable T value) {
/* 137 */       return withDynamic(pointer, () -> value);
/*     */     }
/*     */     
/*     */     @Contract("_, _ -> this")
/*     */     @NotNull
/*     */     <T> Builder withDynamic(@NotNull Pointer<T> param1Pointer, @NotNull Supplier<T> param1Supplier);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\pointer\Pointers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */