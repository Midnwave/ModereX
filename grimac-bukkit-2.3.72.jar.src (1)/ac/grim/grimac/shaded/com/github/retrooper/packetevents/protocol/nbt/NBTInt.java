/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt;
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
/*    */ public class NBTInt
/*    */   extends NBTNumber
/*    */ {
/*    */   protected final int value;
/*    */   
/*    */   public NBTInt(int value) {
/* 26 */     this.value = value;
/*    */   }
/*    */ 
/*    */   
/*    */   public NBTType<NBTInt> getType() {
/* 31 */     return NBTType.INT;
/*    */   }
/*    */ 
/*    */   
/*    */   public Number getAsNumber() {
/* 36 */     return Integer.valueOf(this.value);
/*    */   }
/*    */ 
/*    */   
/*    */   public byte getAsByte() {
/* 41 */     return (byte)this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public short getAsShort() {
/* 46 */     return (short)this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getAsInt() {
/* 51 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getAsLong() {
/* 56 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public float getAsFloat() {
/* 61 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public double getAsDouble() {
/* 66 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 71 */     if (this == obj) {
/* 72 */       return true;
/*    */     }
/* 74 */     if (obj == null) {
/* 75 */       return false;
/*    */     }
/* 77 */     if (getClass() != obj.getClass()) {
/* 78 */       return false;
/*    */     }
/* 80 */     NBTInt other = (NBTInt)obj;
/* 81 */     return (this.value == other.value);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 86 */     return Integer.hashCode(this.value);
/*    */   }
/*    */ 
/*    */   
/*    */   public NBTInt copy() {
/* 91 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 96 */     return "Int(" + this.value + ")";
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\nbt\NBTInt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */