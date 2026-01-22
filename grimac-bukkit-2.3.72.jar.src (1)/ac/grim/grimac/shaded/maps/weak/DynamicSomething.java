/*    */ package ac.grim.grimac.shaded.maps.weak;
/*    */ 
/*    */ import ac.grim.grimac.shaded.maps.LiteJoiner;
/*    */ import java.util.stream.Stream;
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
/*    */ class DynamicSomething
/*    */   extends AbstractDynamic<Object>
/*    */   implements Dynamic, Describer
/*    */ {
/*    */   public DynamicSomething(Object inner) {
/* 25 */     super(inner);
/*    */   }
/*    */ 
/*    */   
/*    */   public Dynamic get(Object key) {
/* 30 */     return new ParentAbsence.Barren<>(this, key);
/*    */   }
/*    */ 
/*    */   
/*    */   public String describe() {
/* 35 */     return this.inner.getClass().getSimpleName();
/*    */   }
/*    */ 
/*    */   
/*    */   protected Object keyLiteral() {
/* 40 */     return "root";
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 45 */     return keyLiteral() + ":" + describe();
/*    */   }
/*    */ 
/*    */   
/*    */   public Stream<Dynamic> children() {
/* 50 */     return Stream.empty();
/*    */   }
/*    */   
/*    */   static class Child
/*    */     extends DynamicSomething implements DynamicChild {
/*    */     private final Dynamic parent;
/*    */     private final Object key;
/*    */     
/*    */     Child(Dynamic parent, Object key, Object inner) {
/* 59 */       super(inner);
/* 60 */       this.parent = parent;
/* 61 */       this.key = key;
/*    */     }
/*    */ 
/*    */     
/*    */     public Dynamic parent() {
/* 66 */       return this.parent;
/*    */     }
/*    */ 
/*    */     
/*    */     public Object keyLiteral() {
/* 71 */       return this.key;
/*    */     }
/*    */ 
/*    */     
/*    */     public String toString() {
/* 76 */       return LiteJoiner.on("->").join(DynamicChildLogic.using(this).getAscendingKeyChainWithRoot()) + ":" + 
/* 77 */         describe();
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\maps\weak\DynamicSomething.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */