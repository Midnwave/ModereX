/*    */ package ac.grim.grimac.shaded.maps.weak;
/*    */ 
/*    */ import java.util.LinkedList;
/*    */ import java.util.function.Predicate;
/*    */ import java.util.function.Supplier;
/*    */ import java.util.stream.Collectors;
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
/*    */ class DynamicChildLogic
/*    */ {
/*    */   private final DynamicChild child;
/*    */   
/*    */   static DynamicChildLogic using(DynamicChild child) {
/* 26 */     return new DynamicChildLogic(child);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private DynamicChildLogic(DynamicChild child) {
/* 32 */     this.child = child;
/*    */   }
/*    */   
/*    */   public LinkedList<Object> getAscendingKeyChainWithRoot() {
/* 36 */     LinkedList<DynamicChild> ascending = getAscendingChainAllWith(dc -> true);
/* 37 */     return (LinkedList<Object>)Stream.concat(Stream.of(((DynamicChild)ascending.getFirst()).parent()), ascending.stream())
/* 38 */       .map(child -> child.key().asObject())
/* 39 */       .collect(Collectors.toCollection(LinkedList::new));
/*    */   }
/*    */   
/*    */   public LinkedList<DynamicChild> getAscendingChainAllWith(Predicate<DynamicChild> pd) {
/* 43 */     LinkedList<DynamicChild> chain = new LinkedList<>();
/* 44 */     if (!pd.test(this.child)) return chain;
/*    */     
/* 46 */     chain.add(this.child);
/*    */     
/* 48 */     Dynamic nextParent = this.child.parent();
/* 49 */     while (nextParent instanceof DynamicChild && pd.test((DynamicChild)nextParent)) {
/* 50 */       chain.addFirst((DynamicChild)nextParent);
/* 51 */       nextParent = ((DynamicChild)nextParent).parent();
/*    */     } 
/* 53 */     return chain;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\maps\weak\DynamicChildLogic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */