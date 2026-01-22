/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt;
/*    */ 
/*    */ import java.util.Objects;
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
/*    */ public class NBTType<T extends NBT>
/*    */ {
/* 25 */   public static final NBTType<NBTEnd> END = new NBTType((Class)NBTEnd.class);
/* 26 */   public static final NBTType<NBTByte> BYTE = new NBTType((Class)NBTByte.class);
/* 27 */   public static final NBTType<NBTShort> SHORT = new NBTType((Class)NBTShort.class);
/* 28 */   public static final NBTType<NBTInt> INT = new NBTType((Class)NBTInt.class);
/* 29 */   public static final NBTType<NBTLong> LONG = new NBTType((Class)NBTLong.class);
/* 30 */   public static final NBTType<NBTFloat> FLOAT = new NBTType((Class)NBTFloat.class);
/* 31 */   public static final NBTType<NBTDouble> DOUBLE = new NBTType((Class)NBTDouble.class);
/* 32 */   public static final NBTType<NBTByteArray> BYTE_ARRAY = new NBTType((Class)NBTByteArray.class);
/* 33 */   public static final NBTType<NBTString> STRING = new NBTType((Class)NBTString.class);
/*    */   
/* 35 */   public static final NBTType<NBTList> LIST = new NBTType((Class)NBTList.class);
/* 36 */   public static final NBTType<NBTCompound> COMPOUND = new NBTType((Class)NBTCompound.class);
/* 37 */   public static final NBTType<NBTIntArray> INT_ARRAY = new NBTType((Class)NBTIntArray.class);
/* 38 */   public static final NBTType<NBTLongArray> LONG_ARRAY = new NBTType((Class)NBTLongArray.class);
/*    */   
/*    */   protected final Class<T> clazz;
/*    */   
/*    */   protected NBTType(Class<T> clazz) {
/* 43 */     this.clazz = clazz;
/*    */   }
/*    */   
/*    */   public Class<T> getNBTClass() {
/* 47 */     return this.clazz;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 52 */     return this.clazz.getSimpleName();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 58 */     if (this == obj) {
/* 59 */       return true;
/*    */     }
/* 61 */     if (obj == null) {
/* 62 */       return false;
/*    */     }
/* 64 */     if (getClass() != obj.getClass()) {
/* 65 */       return false;
/*    */     }
/* 67 */     NBTType<T> other = (NBTType<T>)obj;
/* 68 */     return Objects.equals(this.clazz, other.clazz);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 73 */     return this.clazz.hashCode();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\nbt\NBTType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */