/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.palette;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.stream.NetStreamInput;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SingletonPalette
/*    */   implements Palette
/*    */ {
/*    */   private final int state;
/*    */   
/*    */   @Deprecated
/*    */   public SingletonPalette(NetStreamInput in) {
/* 36 */     this(in.readVarInt());
/*    */   }
/*    */   
/*    */   public SingletonPalette(PacketWrapper<?> wrapper) {
/* 40 */     this(wrapper.readVarInt());
/*    */   }
/*    */   
/*    */   public SingletonPalette(int state) {
/* 44 */     this.state = state;
/*    */   }
/*    */ 
/*    */   
/*    */   public int size() {
/* 49 */     return 1;
/*    */   }
/*    */ 
/*    */   
/*    */   public int stateToId(int state) {
/* 54 */     if (this.state == state) {
/* 55 */       return 0;
/*    */     }
/* 57 */     return -1;
/*    */   }
/*    */ 
/*    */   
/*    */   public int idToState(int id) {
/* 62 */     if (id == 0) {
/* 63 */       return this.state;
/*    */     }
/* 65 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getBits() {
/* 70 */     return 0;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\chunk\palette\SingletonPalette.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */