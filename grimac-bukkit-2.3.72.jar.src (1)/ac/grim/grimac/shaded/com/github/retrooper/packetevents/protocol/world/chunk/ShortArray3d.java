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
/*    */ 
/*    */ public class ShortArray3d
/*    */ {
/*    */   private final short[] data;
/*    */   
/*    */   public ShortArray3d(int size) {
/* 28 */     this.data = new short[size];
/*    */   }
/*    */   
/*    */   public ShortArray3d(short[] array) {
/* 32 */     this.data = array;
/*    */   }
/*    */   
/*    */   public short[] getData() {
/* 36 */     return this.data;
/*    */   }
/*    */   
/*    */   public int get(int x, int y, int z) {
/* 40 */     return this.data[y << 8 | z << 4 | x] & 0xFFFF;
/*    */   }
/*    */   
/*    */   public void set(int x, int y, int z, int val) {
/* 44 */     this.data[y << 8 | z << 4 | x] = (short)val;
/*    */   }
/*    */   
/*    */   public int getBlock(int x, int y, int z) {
/* 48 */     return get(x, y, z) >> 4;
/*    */   }
/*    */   
/*    */   public void setBlock(int x, int y, int z, int block) {
/* 52 */     set(x, y, z, block << 4 | getData(x, y, z));
/*    */   }
/*    */   
/*    */   public int getData(int x, int y, int z) {
/* 56 */     return get(x, y, z) & 0xF;
/*    */   }
/*    */   
/*    */   public void setData(int x, int y, int z, int data) {
/* 60 */     set(x, y, z, getBlock(x, y, z) << 4 | data);
/*    */   }
/*    */   
/*    */   public void setBlockAndData(int x, int y, int z, int block, int data) {
/* 64 */     set(x, y, z, block << 4 | data);
/*    */   }
/*    */   
/*    */   public void fill(int val) {
/* 68 */     Arrays.fill(this.data, (short)val);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 73 */     if (this == o) return true; 
/* 74 */     if (o == null || getClass() != o.getClass()) return false;
/*    */     
/* 76 */     ShortArray3d that = (ShortArray3d)o;
/*    */     
/* 78 */     if (!Arrays.equals(this.data, that.data)) return false;
/*    */     
/* 80 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 85 */     return Arrays.hashCode(this.data);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\chunk\ShortArray3d.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */