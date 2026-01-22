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
/*    */ final class SimpleLazyHolder<T>
/*    */   implements LazyHolder<T>
/*    */ {
/*    */   private T value;
/*    */   private Supplier<T> supplier;
/*    */   
/*    */   SimpleLazyHolder(Supplier<T> supplier) {
/* 50 */     this.supplier = supplier;
/*    */   }
/*    */ 
/*    */   
/*    */   public T get() {
/* 55 */     if (this.supplier != null) {
/* 56 */       this.value = this.supplier.get();
/* 57 */       this.supplier = null;
/*    */     } 
/* 59 */     return this.value;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\lazy\SimpleLazyHolder.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */