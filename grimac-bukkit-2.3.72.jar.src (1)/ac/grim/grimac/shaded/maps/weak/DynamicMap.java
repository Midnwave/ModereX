/*    */ package ac.grim.grimac.shaded.maps.weak;
/*    */ 
/*    */ import ac.grim.grimac.shaded.maps.LiteJoiner;
/*    */ import java.util.Map;
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
/*    */ class DynamicMap
/*    */   extends AbstractDynamic<Map<?, ?>>
/*    */   implements Dynamic, Describer
/*    */ {
/*    */   public DynamicMap(Map<?, ?> inner) {
/* 26 */     super(inner);
/*    */   }
/*    */ 
/*    */   
/*    */   public Dynamic get(Object childKey) {
/* 31 */     if (this.inner.isEmpty()) return new ParentAbsence.Empty<>(this, childKey); 
/* 32 */     if (!this.inner.containsKey(childKey)) {
/* 33 */       if (childKey instanceof String) {
/* 34 */         for (Map.Entry<?, ?> entry : this.inner.entrySet()) {
/* 35 */           if (childKey.equals(entry.getKey().toString())) {
/* 36 */             return (entry.getValue() != null) ? DynamicChild.from(this, childKey, entry.getValue()) : new ChildAbsence.Null(this, childKey);
/*    */           }
/*    */         } 
/*    */       }
/*    */       
/* 41 */       String keyString = childKey.toString();
/* 42 */       if (this.inner.containsKey(keyString)) {
/* 43 */         Object object = this.inner.get(keyString);
/* 44 */         return (object != null) ? DynamicChild.from(this, keyString, object) : new ChildAbsence.Null(this, keyString);
/*    */       } 
/*    */       
/* 47 */       return new ChildAbsence.Missing<>(this, childKey);
/*    */     } 
/* 49 */     Object val = this.inner.get(childKey);
/* 50 */     return (val != null) ? DynamicChild.from(this, childKey, val) : new ChildAbsence.Null(this, childKey);
/*    */   }
/*    */ 
/*    */   
/*    */   public Stream<Dynamic> children() {
/* 55 */     return this.inner.keySet().stream().map(this::get);
/*    */   }
/*    */ 
/*    */   
/*    */   public String describe() {
/* 60 */     if (this.inner.isEmpty()) return "Empty-Map"; 
/* 61 */     return "Map" + this.inner.keySet().toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public Object keyLiteral() {
/* 66 */     return "root";
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 71 */     return keyLiteral() + ":" + describe();
/*    */   }
/*    */   
/*    */   static class Child
/*    */     extends DynamicMap implements DynamicChild {
/*    */     private final Dynamic parent;
/*    */     private final Object key;
/*    */     
/*    */     Child(Dynamic parent, Object key, Map<?, ?> inner) {
/* 80 */       super(inner);
/* 81 */       this.parent = parent;
/* 82 */       this.key = key;
/*    */     }
/*    */ 
/*    */     
/*    */     public Dynamic parent() {
/* 87 */       return this.parent;
/*    */     }
/*    */ 
/*    */     
/*    */     public Object keyLiteral() {
/* 92 */       return this.key;
/*    */     }
/*    */ 
/*    */     
/*    */     public String toString() {
/* 97 */       return LiteJoiner.on("->").join(DynamicChildLogic.using(this).getAscendingKeyChainWithRoot()) + ":" + describe();
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\maps\weak\DynamicMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */