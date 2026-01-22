/*    */ package ac.grim.grimac.shaded.maps.weak;
/*    */ 
/*    */ import ac.grim.grimac.shaded.maps.LiteJoiner;
/*    */ import java.util.List;
/*    */ import java.util.Optional;
/*    */ import java.util.stream.IntStream;
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
/*    */ 
/*    */ class DynamicList
/*    */   extends AbstractDynamic<List>
/*    */   implements Dynamic, Describer
/*    */ {
/*    */   public DynamicList(List inner) {
/* 29 */     super(inner);
/*    */   }
/*    */ 
/*    */   
/*    */   public Dynamic get(Object key) {
/* 34 */     if (this.inner.isEmpty()) return new ParentAbsence.Empty<>(this, key);
/*    */ 
/*    */ 
/*    */     
/* 38 */     Integer index = Optional.<Object>ofNullable(key).flatMap(k -> Converter.convert(k).maybe().intoInteger()).orElse(null);
/* 39 */     if (index == null) return new ChildAbsence.Missing<>(this, key);
/*    */     
/* 41 */     if (index.intValue() < 0 || index.intValue() >= this.inner.size()) return new ChildAbsence.Missing<>(this, index);
/*    */     
/* 43 */     Object val = this.inner.get(index.intValue());
/* 44 */     return (val != null) ? DynamicChild.from(this, index, val) : new ChildAbsence.Null(this, index);
/*    */   }
/*    */ 
/*    */   
/*    */   public Stream<Dynamic> children() {
/* 49 */     return IntStream.range(0, this.inner.size()).mapToObj(this::get);
/*    */   }
/*    */ 
/*    */   
/*    */   public String describe() {
/* 54 */     String type = "List";
/* 55 */     switch (this.inner.size()) { case 0:
/* 56 */         return "Empty-List";
/* 57 */       case 1: return "List[0]";
/* 58 */       case 2: return "List[0, 1]"; }
/* 59 */      return String.format("%s[0..%d]", new Object[] { "List", Integer.valueOf(this.inner.size() - 1) });
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected Object keyLiteral() {
/* 65 */     return "root";
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 70 */     return keyLiteral() + ":" + describe();
/*    */   }
/*    */   
/*    */   static class Child
/*    */     extends DynamicList implements DynamicChild {
/*    */     private final Dynamic parent;
/*    */     private final Object key;
/*    */     
/*    */     Child(Dynamic parent, Object key, List inner) {
/* 79 */       super(inner);
/* 80 */       this.parent = parent;
/* 81 */       this.key = key;
/*    */     }
/*    */ 
/*    */     
/*    */     public Dynamic parent() {
/* 86 */       return this.parent;
/*    */     }
/*    */ 
/*    */     
/*    */     public Object keyLiteral() {
/* 91 */       return this.key;
/*    */     }
/*    */ 
/*    */     
/*    */     public String toString() {
/* 96 */       return LiteJoiner.on("->").join(DynamicChildLogic.using(this).getAscendingKeyChainWithRoot()) + ":" + describe();
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\maps\weak\DynamicList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */