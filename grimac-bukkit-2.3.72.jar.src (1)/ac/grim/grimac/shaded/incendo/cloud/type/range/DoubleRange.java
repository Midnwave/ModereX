/*    */ package ac.grim.grimac.shaded.incendo.cloud.type.range;
/*    */ 
/*    */ import org.immutables.value.Value.Immutable;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Immutable
/*    */ public interface DoubleRange
/*    */   extends Range<Double>
/*    */ {
/*    */   default Double min() {
/* 50 */     return Double.valueOf(minDouble());
/*    */   }
/*    */ 
/*    */   
/*    */   default Double max() {
/* 55 */     return Double.valueOf(maxDouble());
/*    */   }
/*    */   
/*    */   double minDouble();
/*    */   
/*    */   double maxDouble();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\type\range\DoubleRange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */