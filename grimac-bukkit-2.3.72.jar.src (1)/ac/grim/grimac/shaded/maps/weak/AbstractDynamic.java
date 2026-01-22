/*    */ package ac.grim.grimac.shaded.maps.weak;
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
/*    */ public abstract class AbstractDynamic<T>
/*    */   implements Dynamic
/*    */ {
/*    */   protected final T inner;
/*    */   
/*    */   public AbstractDynamic(T inner) {
/* 23 */     this.inner = inner;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isPresent() {
/* 28 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object asObject() {
/* 33 */     return this.inner;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 38 */     return this.inner.hashCode();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Dynamic key() {
/* 45 */     return DynamicChild.key(this, keyLiteral());
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 50 */     if (this == o) return true; 
/* 51 */     if (o == null || getClass() != o.getClass()) return false; 
/* 52 */     AbstractDynamic other = (AbstractDynamic)o;
/* 53 */     return this.inner.equals(other.inner);
/*    */   }
/*    */   
/*    */   protected abstract Object keyLiteral();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\maps\weak\AbstractDynamic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */