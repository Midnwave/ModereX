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
/*    */ public class NBTFloat
/*    */   extends NBTNumber
/*    */ {
/*    */   protected final float value;
/*    */   
/*    */   public NBTFloat(float value) {
/* 26 */     this.value = value;
/*    */   }
/*    */ 
/*    */   
/*    */   public NBTType<NBTFloat> getType() {
/* 31 */     return NBTType.FLOAT;
/*    */   }
/*    */ 
/*    */   
/*    */   public Number getAsNumber() {
/* 36 */     return Float.valueOf(this.value);
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
/* 80 */     NBTFloat other = (NBTFloat)obj;
/* 81 */     return (Float.floatToIntBits(this.value) == Float.floatToIntBits(other.value));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 86 */     return Float.hashCode(this.value);
/*    */   }
/*    */ 
/*    */   
/*    */   public NBTFloat copy() {
/* 91 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 96 */     return "Float(" + this.value + ")";
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\nbt\NBTFloat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */