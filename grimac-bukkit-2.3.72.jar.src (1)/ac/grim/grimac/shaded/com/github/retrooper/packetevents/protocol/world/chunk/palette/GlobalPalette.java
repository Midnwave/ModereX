/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.palette;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class GlobalPalette
/*    */   implements Palette
/*    */ {
/*    */   public static final int BITS_PER_ENTRY = 15;
/* 36 */   public static final GlobalPalette INSTANCE = new GlobalPalette();
/*    */ 
/*    */   
/*    */   public int size() {
/* 40 */     return Integer.MAX_VALUE;
/*    */   }
/*    */ 
/*    */   
/*    */   public int stateToId(int state) {
/* 45 */     return state;
/*    */   }
/*    */ 
/*    */   
/*    */   public int idToState(int id) {
/* 50 */     return id;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getBits() {
/* 55 */     return 15;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\chunk\palette\GlobalPalette.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */