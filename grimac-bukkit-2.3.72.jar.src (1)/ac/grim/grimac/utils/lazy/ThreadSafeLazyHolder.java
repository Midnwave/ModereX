/*    */ package ac.grim.grimac.utils.lazy;
/*    */ 
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
/*    */ final class ThreadSafeLazyHolder<T>
/*    */   implements LazyHolder<T>
/*    */ {
/*    */   private final Supplier<T> supplier;
/*    */   private volatile T value;
/*    */   
/*    */   ThreadSafeLazyHolder(Supplier<T> supplier) {
/* 27 */     this.supplier = supplier;
/*    */   }
/*    */ 
/*    */   
/*    */   public T get() {
/* 32 */     T result = this.value;
/* 33 */     if (result == null) {
/* 34 */       synchronized (this) {
/* 35 */         result = this.value;
/* 36 */         if (result == null) {
/* 37 */           this.value = result = this.supplier.get();
/*    */         }
/*    */       } 
/*    */     }
/* 41 */     return result;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\lazy\ThreadSafeLazyHolder.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */