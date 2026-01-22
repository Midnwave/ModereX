/*    */ package ac.grim.grimac.shaded.fastutil.objects;
/*    */ 
/*    */ import ac.grim.grimac.shaded.fastutil.BigListIterator;
/*    */ import ac.grim.grimac.shaded.fastutil.SafeMath;
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
/*    */ public interface ObjectBigListIterator<K>
/*    */   extends ObjectBidirectionalIterator<K>, BigListIterator<K>
/*    */ {
/*    */   default void set(K k) {
/* 35 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default void add(K k) {
/* 45 */     throw new UnsupportedOperationException();
/*    */   }
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
/*    */   default long skip(long n) {
/* 60 */     long i = n;
/* 61 */     for (; i-- != 0L && hasNext(); next());
/* 62 */     return n - i - 1L;
/*    */   }
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
/*    */   default long back(long n) {
/* 77 */     long i = n;
/* 78 */     for (; i-- != 0L && hasPrevious(); previous());
/* 79 */     return n - i - 1L;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default int skip(int n) {
/* 87 */     return SafeMath.safeLongToInt(skip(n));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\fastutil\objects\ObjectBigListIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */