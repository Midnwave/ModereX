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
/*    */ public interface ByteRange
/*    */   extends Range<Byte>
/*    */ {
/*    */   default Byte min() {
/* 50 */     return Byte.valueOf(minByte());
/*    */   }
/*    */ 
/*    */   
/*    */   default Byte max() {
/* 55 */     return Byte.valueOf(maxByte());
/*    */   }
/*    */   
/*    */   byte minByte();
/*    */   
/*    */   byte maxByte();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\type\range\ByteRange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */