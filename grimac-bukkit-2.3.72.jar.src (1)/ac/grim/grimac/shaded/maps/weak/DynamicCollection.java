/*    */ package ac.grim.grimac.shaded.maps.weak;
/*    */ 
/*    */ import ac.grim.grimac.shaded.maps.LiteJoiner;
/*    */ import java.util.Collection;
/*    */ import java.util.stream.Stream;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class DynamicCollection
/*    */   extends AbstractDynamic<Collection>
/*    */   implements Dynamic, Describer
/*    */ {
/*    */   static final String NO_KEY = "?";
/*    */   
/*    */   DynamicCollection(Collection inner) {
/* 18 */     super(inner);
/*    */   }
/*    */ 
/*    */   
/*    */   protected Object keyLiteral() {
/* 23 */     return "root";
/*    */   }
/*    */ 
/*    */   
/*    */   public Dynamic get(Object childKey) {
/* 28 */     if (this.inner.isEmpty()) return new ParentAbsence.Empty<>(this, childKey); 
/* 29 */     return new ChildAbsence.Missing<>(this, childKey);
/*    */   }
/*    */ 
/*    */   
/*    */   public Stream<Dynamic> children() {
/* 34 */     return this.inner.stream()
/* 35 */       .map(val -> (val == null) ? new ChildAbsence.Null(this, "?") : DynamicChild.from(this, "?", val));
/*    */   }
/*    */ 
/*    */   
/*    */   public String describe() {
/* 40 */     String type = (this.inner instanceof java.util.Set) ? "Set" : "Collection";
/* 41 */     if (this.inner.isEmpty()) return "Empty-" + type; 
/* 42 */     return type + "[size:" + this.inner.size() + "]";
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 47 */     return keyLiteral() + ":" + describe();
/*    */   }
/*    */   
/*    */   static class Child
/*    */     extends DynamicCollection implements DynamicChild {
/*    */     private final Dynamic parent;
/*    */     private final Object key;
/*    */     
/*    */     Child(Dynamic parent, Object key, Collection inner) {
/* 56 */       super(inner);
/* 57 */       this.parent = parent;
/* 58 */       this.key = key;
/*    */     }
/*    */ 
/*    */     
/*    */     public Dynamic parent() {
/* 63 */       return this.parent;
/*    */     }
/*    */ 
/*    */     
/*    */     public Object keyLiteral() {
/* 68 */       return this.key;
/*    */     }
/*    */ 
/*    */     
/*    */     public String toString() {
/* 73 */       return LiteJoiner.on("->").join(DynamicChildLogic.using(this).getAscendingKeyChainWithRoot()) + ":" + 
/* 74 */         describe();
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\maps\weak\DynamicCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */