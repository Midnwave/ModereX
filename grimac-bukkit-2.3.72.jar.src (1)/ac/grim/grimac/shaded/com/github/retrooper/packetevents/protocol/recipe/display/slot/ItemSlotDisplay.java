/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.display.slot;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
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
/*    */ public class ItemSlotDisplay
/*    */   extends SlotDisplay<ItemSlotDisplay>
/*    */ {
/*    */   private ItemType item;
/*    */   
/*    */   public ItemSlotDisplay(ItemType item) {
/* 32 */     super(SlotDisplayTypes.ITEM);
/* 33 */     this.item = item;
/*    */   }
/*    */   
/*    */   public static ItemSlotDisplay read(PacketWrapper<?> wrapper) {
/* 37 */     ItemType item = (ItemType)wrapper.readMappedEntity((IRegistry)ItemTypes.getRegistry());
/* 38 */     return new ItemSlotDisplay(item);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ItemSlotDisplay display) {
/* 42 */     wrapper.writeMappedEntity((MappedEntity)display.getItem());
/*    */   }
/*    */   
/*    */   public ItemType getItem() {
/* 46 */     return this.item;
/*    */   }
/*    */   
/*    */   public void setItem(ItemType item) {
/* 50 */     this.item = item;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 55 */     if (this == obj) return true; 
/* 56 */     if (!(obj instanceof ItemSlotDisplay)) return false; 
/* 57 */     ItemSlotDisplay that = (ItemSlotDisplay)obj;
/* 58 */     return this.item.equals(that.item);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 63 */     return Objects.hashCode(this.item);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 68 */     return "ItemSlotDisplay{item=" + this.item + '}';
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\display\slot\ItemSlotDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */