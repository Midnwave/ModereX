/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntitySet;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import java.util.Objects;
/*    */ import java.util.function.BiFunction;
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
/*    */ public class ItemRepairable
/*    */ {
/*    */   private MappedEntitySet<ItemType> items;
/*    */   
/*    */   public ItemRepairable(MappedEntitySet<ItemType> items) {
/* 33 */     this.items = items;
/*    */   }
/*    */   
/*    */   public static ItemRepairable read(PacketWrapper<?> wrapper) {
/* 37 */     MappedEntitySet<ItemType> items = MappedEntitySet.read(wrapper, (BiFunction)ItemTypes.getRegistry());
/* 38 */     return new ItemRepairable(items);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ItemRepairable repairable) {
/* 42 */     MappedEntitySet.write(wrapper, repairable.items);
/*    */   }
/*    */   
/*    */   public MappedEntitySet<ItemType> getItems() {
/* 46 */     return this.items;
/*    */   }
/*    */   
/*    */   public void setItems(MappedEntitySet<ItemType> items) {
/* 50 */     this.items = items;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 55 */     if (this == obj) return true; 
/* 56 */     if (!(obj instanceof ItemRepairable)) return false; 
/* 57 */     ItemRepairable that = (ItemRepairable)obj;
/* 58 */     return this.items.equals(that.items);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 63 */     return Objects.hashCode(this.items);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 68 */     return "ItemRepairable{items=" + this.items + '}';
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemRepairable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */