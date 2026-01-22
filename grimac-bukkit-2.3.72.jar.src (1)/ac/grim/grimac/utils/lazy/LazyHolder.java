/*    */ package ac.grim.grimac.utils.lazy;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import java.util.function.Supplier;
/*    */ 
/*    */ public interface LazyHolder<T> {
/*    */   @Contract(value = "_ -> new", pure = true)
/*    */   @NotNull
/*    */   static <T> LazyHolder<T> threadSafe(Supplier<T> supplier) {
/* 11 */     return new ThreadSafeLazyHolder<>(supplier);
/*    */   }
/*    */   @Contract(value = "_ -> new", pure = true)
/*    */   @NotNull
/*    */   static <T> LazyHolder<T> simple(Supplier<T> supplier) {
/* 16 */     return new SimpleLazyHolder<>(supplier);
/*    */   }
/*    */   
/*    */   T get();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\lazy\LazyHolder.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */