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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class NBT
/*    */ {
/*    */   public abstract NBTType<?> getType();
/*    */   
/*    */   public abstract boolean equals(Object paramObject);
/*    */   
/*    */   public abstract int hashCode();
/*    */   
/*    */   public String toString() {
/* 33 */     return "nbt";
/*    */   }
/*    */   
/*    */   public abstract NBT copy();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\nbt\NBT.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */