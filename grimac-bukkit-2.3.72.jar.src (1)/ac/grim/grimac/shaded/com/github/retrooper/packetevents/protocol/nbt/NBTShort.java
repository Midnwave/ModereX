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
/*    */ public class NBTShort
/*    */   extends NBTNumber
/*    */ {
/*    */   protected final short value;
/*    */   
/*    */   public NBTShort(short value) {
/* 25 */     this.value = value;
/*    */   }
/*    */ 
/*    */   
/*    */   public NBTType<NBTShort> getType() {
/* 30 */     return NBTType.SHORT;
/*    */   }
/*    */ 
/*    */   
/*    */   public Number getAsNumber() {
/* 35 */     return Short.valueOf(this.value);
/*    */   }
/*    */ 
/*    */   
/*    */   public byte getAsByte() {
/* 40 */     return (byte)this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public short getAsShort() {
/* 45 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getAsInt() {
/* 50 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getAsLong() {
/* 55 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public float getAsFloat() {
/* 60 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public double getAsDouble() {
/* 65 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 70 */     if (this == obj) {
/* 71 */       return true;
/*    */     }
/* 73 */     if (obj == null) {
/* 74 */       return false;
/*    */     }
/* 76 */     if (getClass() != obj.getClass()) {
/* 77 */       return false;
/*    */     }
/* 79 */     NBTShort other = (NBTShort)obj;
/* 80 */     return (this.value == other.value);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 85 */     return Short.hashCode(this.value);
/*    */   }
/*    */ 
/*    */   
/*    */   public NBTShort copy() {
/* 90 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 95 */     return "Short(" + this.value + ")";
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\nbt\NBTShort.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */