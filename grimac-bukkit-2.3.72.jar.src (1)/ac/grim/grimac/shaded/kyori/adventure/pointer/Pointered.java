/*    */ package ac.grim.grimac.shaded.kyori.adventure.pointer;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import java.util.Optional;
/*    */ import java.util.function.Supplier;
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
/*    */ public interface Pointered
/*    */ {
/*    */   @NotNull
/*    */   default <T> Optional<T> get(@NotNull Pointer<T> pointer) {
/* 48 */     return pointers().get(pointer);
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
/*    */ 
/*    */   
/*    */   @Contract("_, null -> _; _, !null -> !null")
/*    */   @Nullable
/*    */   default <T> T getOrDefault(@NotNull Pointer<T> pointer, @Nullable T defaultValue) {
/* 65 */     return pointers().getOrDefault(pointer, defaultValue);
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
/*    */ 
/*    */ 
/*    */   
/*    */   default <T> T getOrDefaultFrom(@NotNull Pointer<T> pointer, @NotNull Supplier<? extends T> defaultValue) {
/* 81 */     return pointers().getOrDefaultFrom(pointer, defaultValue);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   default Pointers pointers() {
/* 91 */     return Pointers.empty();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\pointer\Pointered.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */