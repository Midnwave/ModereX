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
/*    */ public interface IntRange
/*    */   extends Range<Integer>
/*    */ {
/*    */   default Integer min() {
/* 50 */     return Integer.valueOf(minInt());
/*    */   }
/*    */ 
/*    */   
/*    */   default Integer max() {
/* 55 */     return Integer.valueOf(maxInt());
/*    */   }
/*    */   
/*    */   int minInt();
/*    */   
/*    */   int maxInt();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\type\range\IntRange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */