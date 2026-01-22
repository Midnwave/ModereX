/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.filter;
/*    */ 
/*    */ import java.util.BitSet;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FilterMask
/*    */ {
/* 28 */   public static final FilterMask FULLY_FILTERED = new FilterMask(new BitSet(0), FilterMaskType.FULLY_FILTERED);
/* 29 */   public static final FilterMask PASS_THROUGH = new FilterMask(new BitSet(0), FilterMaskType.PASS_THROUGH);
/*    */   
/*    */   private final BitSet mask;
/*    */   
/*    */   private final FilterMaskType type;
/*    */   
/*    */   private FilterMask(BitSet mask, FilterMaskType type) {
/* 36 */     this.type = type;
/* 37 */     this.mask = mask;
/*    */   }
/*    */   
/*    */   public FilterMask(BitSet mask) {
/* 41 */     this.type = FilterMaskType.PARTIALLY_FILTERED;
/* 42 */     this.mask = mask;
/*    */   }
/*    */   
/*    */   public BitSet getMask() {
/* 46 */     return this.mask;
/*    */   }
/*    */   
/*    */   public FilterMaskType getType() {
/* 50 */     return this.type;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\filter\FilterMask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */