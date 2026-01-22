/*    */ package ac.grim.grimac.shaded.fastutil.objects;
/*    */ 
/*    */ import java.util.Iterator;
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
/*    */ public interface ObjectIterator<K>
/*    */   extends Iterator<K>
/*    */ {
/*    */   default int skip(int n) {
/* 39 */     if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n); 
/* 40 */     int i = n;
/* 41 */     for (; i-- != 0 && hasNext(); next());
/* 42 */     return n - i - 1;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\fastutil\objects\ObjectIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */