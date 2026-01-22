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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ListPalette
/*    */   implements Palette
/*    */ {
/*    */   private final int bits;
/*    */   private final int[] data;
/* 38 */   private int nextId = 0;
/*    */   
/*    */   public ListPalette(int bitsPerEntry) {
/* 41 */     this.bits = bitsPerEntry;
/* 42 */     this.data = new int[1 << bitsPerEntry];
/*    */   }
/*    */   
/*    */   @Deprecated
/*    */   public ListPalette(int bitsPerEntry, NetStreamInput in) {
/* 47 */     this(bitsPerEntry);
/*    */     
/* 49 */     int paletteLength = in.readVarInt();
/* 50 */     for (int i = 0; i < paletteLength; i++) {
/* 51 */       this.data[i] = in.readVarInt();
/*    */     }
/* 53 */     this.nextId = paletteLength;
/*    */   }
/*    */   
/*    */   public ListPalette(int bitsPerEntry, PacketWrapper<?> wrapper) {
/* 57 */     this(bitsPerEntry);
/*    */     
/* 59 */     int paletteLength = wrapper.readVarInt();
/* 60 */     for (int i = 0; i < paletteLength; i++) {
/* 61 */       this.data[i] = wrapper.readVarInt();
/*    */     }
/* 63 */     this.nextId = paletteLength;
/*    */   }
/*    */ 
/*    */   
/*    */   public int size() {
/* 68 */     return this.nextId;
/*    */   }
/*    */ 
/*    */   
/*    */   public int stateToId(int state) {
/* 73 */     int id = -1;
/* 74 */     for (int i = 0; i < this.nextId; i++) {
/* 75 */       if (this.data[i] == state) {
/* 76 */         id = i;
/*    */         break;
/*    */       } 
/*    */     } 
/* 80 */     if (id == -1 && size() < this.data.length) {
/* 81 */       id = this.nextId++;
/* 82 */       this.data[id] = state;
/*    */     } 
/*    */     
/* 85 */     return id;
/*    */   }
/*    */ 
/*    */   
/*    */   public int idToState(int id) {
/* 90 */     if (id >= 0 && id < size()) {
/* 91 */       return this.data[id];
/*    */     }
/* 93 */     return 0;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getBits() {
/* 99 */     return this.bits;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\chunk\palette\ListPalette.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */