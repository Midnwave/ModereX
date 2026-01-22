/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk;
/*    */ 
/*    */ import java.util.Arrays;
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
/*    */ public class ByteArray3d
/*    */ {
/*    */   private final byte[] data;
/*    */   
/*    */   public ByteArray3d(int size) {
/* 27 */     this.data = new byte[size];
/*    */   }
/*    */   
/*    */   public ByteArray3d(byte[] array) {
/* 31 */     this.data = array;
/*    */   }
/*    */   
/*    */   public byte[] getData() {
/* 35 */     return this.data;
/*    */   }
/*    */   
/*    */   public int get(int x, int y, int z) {
/* 39 */     return this.data[y << 8 | z << 4 | x] & 0xFF;
/*    */   }
/*    */   
/*    */   public void set(int x, int y, int z, int val) {
/* 43 */     this.data[y << 8 | z << 4 | x] = (byte)val;
/*    */   }
/*    */   
/*    */   public void fill(int val) {
/* 47 */     Arrays.fill(this.data, (byte)val);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\chunk\ByteArray3d.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */