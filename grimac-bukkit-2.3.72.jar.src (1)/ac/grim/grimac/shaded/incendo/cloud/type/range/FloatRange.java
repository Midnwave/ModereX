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
/*    */ public interface FloatRange
/*    */   extends Range<Float>
/*    */ {
/*    */   default Float min() {
/* 50 */     return Float.valueOf(minFloat());
/*    */   }
/*    */ 
/*    */   
/*    */   default Float max() {
/* 55 */     return Float.valueOf(maxFloat());
/*    */   }
/*    */   
/*    */   float minFloat();
/*    */   
/*    */   float maxFloat();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\type\range\FloatRange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */