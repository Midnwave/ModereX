/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt;
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
/*    */ public class NBTLongArray
/*    */   extends NBT
/*    */ {
/*    */   protected final long[] array;
/*    */   
/*    */   public NBTLongArray(long[] array) {
/* 28 */     this.array = array;
/*    */   }
/*    */ 
/*    */   
/*    */   public NBTType<NBTLongArray> getType() {
/* 33 */     return NBTType.LONG_ARRAY;
/*    */   }
/*    */   
/*    */   public long[] getValue() {
/* 37 */     return this.array;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 42 */     if (this == obj) {
/* 43 */       return true;
/*    */     }
/* 45 */     if (obj == null) {
/* 46 */       return false;
/*    */     }
/* 48 */     if (getClass() != obj.getClass()) {
/* 49 */       return false;
/*    */     }
/* 51 */     NBTLongArray other = (NBTLongArray)obj;
/* 52 */     return Arrays.equals(this.array, other.array);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 57 */     return Arrays.hashCode(this.array);
/*    */   }
/*    */ 
/*    */   
/*    */   public NBTLongArray copy() {
/* 62 */     long[] along = new long[this.array.length];
/* 63 */     System.arraycopy(this.array, 0, along, 0, this.array.length);
/* 64 */     return new NBTLongArray(along);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 69 */     return "LongArray(" + Arrays.toString(this.array) + ")";
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\nbt\NBTLongArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */