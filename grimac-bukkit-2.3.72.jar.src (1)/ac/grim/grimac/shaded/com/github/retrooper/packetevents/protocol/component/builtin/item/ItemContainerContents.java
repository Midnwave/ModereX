/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import java.util.List;
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
/*    */ public class ItemContainerContents
/*    */ {
/*    */   private List<ItemStack> items;
/*    */   
/*    */   public ItemContainerContents(List<ItemStack> items) {
/* 32 */     this.items = items;
/*    */   }
/*    */   
/*    */   public static ItemContainerContents read(PacketWrapper<?> wrapper) {
/* 36 */     List<ItemStack> items = wrapper.readList(PacketWrapper::readItemStack);
/* 37 */     return new ItemContainerContents(items);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ItemContainerContents contents) {
/* 41 */     wrapper.writeList(contents.items, PacketWrapper::writeItemStack);
/*    */   }
/*    */   
/*    */   public void addItem(ItemStack itemStack) {
/* 45 */     this.items.add(itemStack);
/*    */   }
/*    */   
/*    */   public List<ItemStack> getItems() {
/* 49 */     return this.items;
/*    */   }
/*    */   
/*    */   public void setItems(List<ItemStack> items) {
/* 53 */     this.items = items;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 58 */     if (this == obj) return true; 
/* 59 */     if (!(obj instanceof ItemContainerContents)) return false; 
/* 60 */     ItemContainerContents that = (ItemContainerContents)obj;
/* 61 */     return this.items.equals(that.items);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 66 */     return Objects.hashCode(this.items);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemContainerContents.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */