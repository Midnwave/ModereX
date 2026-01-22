/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*    */ 
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
/*    */ 
/*    */ public class ItemEnchantable
/*    */ {
/*    */   private int value;
/*    */   
/*    */   public ItemEnchantable(int value) {
/* 30 */     this.value = value;
/*    */   }
/*    */   
/*    */   public static ItemEnchantable read(PacketWrapper<?> wrapper) {
/* 34 */     int value = wrapper.readVarInt();
/* 35 */     return new ItemEnchantable(value);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ItemEnchantable enchantable) {
/* 39 */     wrapper.writeVarInt(enchantable.value);
/*    */   }
/*    */   
/*    */   public int getValue() {
/* 43 */     return this.value;
/*    */   }
/*    */   
/*    */   public void setValue(int value) {
/* 47 */     this.value = value;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 52 */     if (this == obj) return true; 
/* 53 */     if (!(obj instanceof ItemEnchantable)) return false; 
/* 54 */     ItemEnchantable that = (ItemEnchantable)obj;
/* 55 */     return (this.value == that.value);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 60 */     return Objects.hashCode(Integer.valueOf(this.value));
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 65 */     return "ItemEnchantable{value=" + this.value + '}';
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemEnchantable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */