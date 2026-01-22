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
/*    */ public class NBTDouble
/*    */   extends NBTNumber
/*    */ {
/*    */   protected final double value;
/*    */   
/*    */   public NBTDouble(double value) {
/* 26 */     this.value = value;
/*    */   }
/*    */ 
/*    */   
/*    */   public NBTType<NBTDouble> getType() {
/* 31 */     return NBTType.DOUBLE;
/*    */   }
/*    */ 
/*    */   
/*    */   public Number getAsNumber() {
/* 36 */     return Double.valueOf(this.value);
/*    */   }
/*    */ 
/*    */   
/*    */   public byte getAsByte() {
/* 41 */     return (byte)(int)this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public short getAsShort() {
/* 46 */     return (short)(int)this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getAsInt() {
/* 51 */     return (int)this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getAsLong() {
/* 56 */     return (long)this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public float getAsFloat() {
/* 61 */     return (float)this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public double getAsDouble() {
/* 66 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 71 */     return Double.hashCode(this.value);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 76 */     if (this == obj) {
/* 77 */       return true;
/*    */     }
/* 79 */     if (obj == null) {
/* 80 */       return false;
/*    */     }
/* 82 */     if (getClass() != obj.getClass()) {
/* 83 */       return false;
/*    */     }
/* 85 */     NBTDouble other = (NBTDouble)obj;
/* 86 */     return (Double.doubleToLongBits(this.value) == Double.doubleToLongBits(other.value));
/*    */   }
/*    */ 
/*    */   
/*    */   public NBTDouble copy() {
/* 91 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 96 */     return "Double(" + this.value + ")";
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\nbt\NBTDouble.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */