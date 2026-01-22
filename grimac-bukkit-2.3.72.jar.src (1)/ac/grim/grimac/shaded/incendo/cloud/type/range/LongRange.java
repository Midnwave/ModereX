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
/*    */ public interface LongRange
/*    */   extends Range<Long>
/*    */ {
/*    */   default Long min() {
/* 50 */     return Long.valueOf(minLong());
/*    */   }
/*    */ 
/*    */   
/*    */   default Long max() {
/* 55 */     return Long.valueOf(maxLong());
/*    */   }
/*    */   
/*    */   long minLong();
/*    */   
/*    */   long maxLong();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\type\range\LongRange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */