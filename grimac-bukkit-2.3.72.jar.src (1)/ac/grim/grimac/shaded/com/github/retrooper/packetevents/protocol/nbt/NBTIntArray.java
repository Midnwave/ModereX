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
/*    */ public class NBTIntArray
/*    */   extends NBT
/*    */ {
/*    */   protected final int[] array;
/*    */   
/*    */   public NBTIntArray(int[] array) {
/* 28 */     this.array = array;
/*    */   }
/*    */ 
/*    */   
/*    */   public NBTType<NBTIntArray> getType() {
/* 33 */     return NBTType.INT_ARRAY;
/*    */   }
/*    */   
/*    */   public int[] getValue() {
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
/* 51 */     NBTIntArray other = (NBTIntArray)obj;
/* 52 */     return Arrays.equals(this.array, other.array);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 57 */     return Arrays.hashCode(this.array);
/*    */   }
/*    */ 
/*    */   
/*    */   public NBTIntArray copy() {
/* 62 */     int[] aint = new int[this.array.length];
/* 63 */     System.arraycopy(this.array, 0, aint, 0, this.array.length);
/* 64 */     return new NBTIntArray(aint);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 69 */     return "IntArray(" + Arrays.toString(this.array) + ")";
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\nbt\NBTIntArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */