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
/*    */ public class NBTEnd
/*    */   extends NBT
/*    */ {
/* 23 */   public static final NBTEnd INSTANCE = new NBTEnd();
/*    */ 
/*    */   
/*    */   public NBTType<NBTEnd> getType() {
/* 27 */     return NBTType.END;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 32 */     if (this == obj) {
/* 33 */       return true;
/*    */     }
/* 35 */     if (obj == null) {
/* 36 */       return false;
/*    */     }
/* 38 */     return (getClass() == obj.getClass());
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 43 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public NBTEnd copy() {
/* 48 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 53 */     return "END";
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\nbt\NBTEnd.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */