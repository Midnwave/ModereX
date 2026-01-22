/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk;
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
/*    */ public class NibbleArray3d
/*    */ {
/*    */   private final byte[] data;
/*    */   
/*    */   public NibbleArray3d(byte[] data) {
/* 35 */     this.data = data;
/*    */   }
/*    */   
/*    */   public NibbleArray3d(int size) {
/* 39 */     this(new byte[size >> 1]);
/*    */   }
/*    */   
/*    */   @Deprecated
/*    */   public NibbleArray3d(NetStreamInput in, int size) {
/* 44 */     this(in.readBytes(size));
/*    */   }
/*    */   
/*    */   public NibbleArray3d(PacketWrapper<?> wrapper, int size) {
/* 48 */     this(wrapper.readBytes(size));
/*    */   }
/*    */   
/*    */   public byte[] getData() {
/* 52 */     return this.data;
/*    */   }
/*    */   
/*    */   public int get(int x, int y, int z) {
/* 56 */     int key = y << 8 | z << 4 | x;
/* 57 */     int index = key >> 1;
/* 58 */     int part = key & 0x1;
/* 59 */     return (part == 0) ? (this.data[index] & 0xF) : (this.data[index] >> 4 & 0xF);
/*    */   }
/*    */   
/*    */   public void set(int x, int y, int z, int val) {
/* 63 */     int key = y << 8 | z << 4 | x;
/* 64 */     int index = key >> 1;
/* 65 */     int part = key & 0x1;
/* 66 */     if (part == 0) {
/* 67 */       this.data[index] = (byte)(this.data[index] & 0xF0 | val & 0xF);
/*    */     } else {
/* 69 */       this.data[index] = (byte)(this.data[index] & 0xF | (val & 0xF) << 4);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\chunk\NibbleArray3d.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */