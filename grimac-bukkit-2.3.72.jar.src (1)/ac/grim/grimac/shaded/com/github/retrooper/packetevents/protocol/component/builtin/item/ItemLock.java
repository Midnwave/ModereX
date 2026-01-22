/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*    */ public class ItemLock
/*    */ {
/*    */   private String code;
/*    */   
/*    */   public ItemLock(String code) {
/* 31 */     this.code = code;
/*    */   }
/*    */   
/*    */   public static ItemLock read(PacketWrapper<?> wrapper) {
/* 35 */     NBTString codeTag = (NBTString)wrapper.readNBTRaw();
/* 36 */     return new ItemLock(codeTag.getValue());
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ItemLock lock) {
/* 40 */     wrapper.writeNBTRaw((NBT)new NBTString(lock.code));
/*    */   }
/*    */   
/*    */   public String getCode() {
/* 44 */     return this.code;
/*    */   }
/*    */   
/*    */   public void setCode(String code) {
/* 48 */     this.code = code;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 53 */     if (this == obj) return true; 
/* 54 */     if (!(obj instanceof ItemLock)) return false; 
/* 55 */     ItemLock itemLock = (ItemLock)obj;
/* 56 */     return this.code.equals(itemLock.code);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 61 */     return Objects.hashCode(this.code);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemLock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */