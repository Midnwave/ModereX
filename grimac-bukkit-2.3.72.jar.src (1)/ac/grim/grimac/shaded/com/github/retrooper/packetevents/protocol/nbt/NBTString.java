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
/*    */ public class NBTString
/*    */   extends NBT
/*    */ {
/*    */   protected final String string;
/*    */   
/*    */   public NBTString(String string) {
/* 28 */     this.string = string;
/*    */   }
/*    */ 
/*    */   
/*    */   public NBTType<NBTString> getType() {
/* 33 */     return NBTType.STRING;
/*    */   }
/*    */   
/*    */   public String getValue() {
/* 37 */     return this.string;
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
/* 51 */     NBTString other = (NBTString)obj;
/* 52 */     return Objects.equals(this.string, other.string);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 57 */     return Objects.hash(new Object[] { this.string });
/*    */   }
/*    */ 
/*    */   
/*    */   public NBTString copy() {
/* 62 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 67 */     return "String(" + this.string + ")";
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\nbt\NBTString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */