/*    */ package ac.grim.grimac.shaded.fastutil.doubles;
/*    */ 
/*    */ import ac.grim.grimac.shaded.fastutil.objects.ObjectBidirectionalIterator;
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
/*    */ public interface DoubleBidirectionalIterator
/*    */   extends DoubleIterator, ObjectBidirectionalIterator<Double>
/*    */ {
/*    */   @Deprecated
/*    */   default Double previous() {
/* 44 */     return Double.valueOf(previousDouble());
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
/*    */   
/*    */   default int back(int n) {
/* 60 */     int i = n;
/* 61 */     for (; i-- != 0 && hasPrevious(); previousDouble());
/* 62 */     return n - i - 1;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   default int skip(int n) {
/* 68 */     return super.skip(n);
/*    */   }
/*    */   
/*    */   double previousDouble();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\fastutil\doubles\DoubleBidirectionalIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */