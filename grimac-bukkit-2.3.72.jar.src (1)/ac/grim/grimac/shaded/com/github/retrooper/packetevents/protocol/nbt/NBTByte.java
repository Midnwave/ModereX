/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NBTByte
/*     */   extends NBTNumber
/*     */ {
/*     */   protected final byte value;
/*     */   
/*     */   public NBTByte(byte value) {
/*  26 */     this.value = value;
/*     */   }
/*     */   
/*     */   public NBTByte(boolean value) {
/*  30 */     this((byte)(value ? 1 : 0));
/*     */   }
/*     */   
/*     */   public boolean getAsBool() {
/*  34 */     return (this.value != 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public NBTType<NBTByte> getType() {
/*  39 */     return NBTType.BYTE;
/*     */   }
/*     */ 
/*     */   
/*     */   public Number getAsNumber() {
/*  44 */     return Byte.valueOf(this.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getAsByte() {
/*  49 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public short getAsShort() {
/*  54 */     return (short)this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getAsInt() {
/*  59 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getAsLong() {
/*  64 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getAsFloat() {
/*  69 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getAsDouble() {
/*  74 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  79 */     return Byte.hashCode(this.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*  84 */     if (this == obj) {
/*  85 */       return true;
/*     */     }
/*  87 */     if (obj == null) {
/*  88 */       return false;
/*     */     }
/*  90 */     if (getClass() != obj.getClass()) {
/*  91 */       return false;
/*     */     }
/*  93 */     NBTByte other = (NBTByte)obj;
/*  94 */     return (this.value == other.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public NBTByte copy() {
/*  99 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 104 */     return "Byte(" + this.value + ")";
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\nbt\NBTByte.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */