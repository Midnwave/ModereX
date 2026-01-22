/*    */ package ac.grim.grimac.shaded.maps.weak;
/*    */ 
/*    */ import ac.grim.grimac.shaded.maps.LiteJoiner;
/*    */ import java.util.Collection;
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Objects;
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
/*    */ interface DynamicChild
/*    */   extends Dynamic
/*    */ {
/*    */   public static final String ARROW = "->";
/*    */   
/*    */   static DynamicChild from(Dynamic parent, Object key, Object val) {
/* 28 */     Objects.requireNonNull(parent);
/* 29 */     Objects.requireNonNull(key);
/* 30 */     Objects.requireNonNull(val);
/*    */     
/* 32 */     if (val instanceof Map) return new DynamicMap.Child(parent, key, (Map)val); 
/* 33 */     if (val instanceof List) return new DynamicList.Child(parent, key, (List)val); 
/* 34 */     if (val instanceof Collection) return new DynamicCollection.Child(parent, key, (Collection)val); 
/* 35 */     return new DynamicSomething.Child(parent, key, val);
/*    */   }
/*    */   
/*    */   static DynamicChild key(Dynamic parent, Object key) {
/* 39 */     return from(parent, key, key);
/*    */   }
/*    */ 
/*    */   
/*    */   Dynamic parent();
/*    */   
/*    */   default <T> T as(Class<T> type) {
/*    */     try {
/* 47 */       return type.cast(asObject());
/* 48 */     } catch (ClassCastException ex) {
/* 49 */       LinkedList<Object> ascendingKeyChain = DynamicChildLogic.using(this).getAscendingKeyChainWithRoot();
/* 50 */       Object thisKey = ascendingKeyChain.pollLast();
/* 51 */       ascendingKeyChain.add(String.format("*%s*", new Object[] { thisKey }));
/* 52 */       throw new ClassCastException(String.format("'%s' miscast in path %s: %s. Avoid by checking `if (aDynamic.is(%s.class)) ...` or using `aDynamic.maybe().as(%<s.class)`", new Object[] { thisKey, 
/*    */               
/* 54 */               LiteJoiner.on("->").join(ascendingKeyChain), ex.getMessage(), type.getSimpleName() }));
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\maps\weak\DynamicChild.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */