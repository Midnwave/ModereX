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
/*    */ public interface ShortRange
/*    */   extends Range<Short>
/*    */ {
/*    */   default Short min() {
/* 50 */     return Short.valueOf(minShort());
/*    */   }
/*    */ 
/*    */   
/*    */   default Short max() {
/* 55 */     return Short.valueOf(maxShort());
/*    */   }
/*    */   
/*    */   short minShort();
/*    */   
/*    */   short maxShort();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\type\range\ShortRange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */