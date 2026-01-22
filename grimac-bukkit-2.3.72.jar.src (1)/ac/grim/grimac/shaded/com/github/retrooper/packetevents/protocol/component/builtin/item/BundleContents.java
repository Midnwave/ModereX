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
/*    */ public class BundleContents
/*    */ {
/*    */   private List<ItemStack> items;
/*    */   
/*    */   public BundleContents(List<ItemStack> items) {
/* 32 */     this.items = items;
/*    */   }
/*    */   
/*    */   public static BundleContents read(PacketWrapper<?> wrapper) {
/* 36 */     List<ItemStack> items = wrapper.readList(PacketWrapper::readPresentItemStack);
/* 37 */     return new BundleContents(items);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, BundleContents projectiles) {
/* 41 */     wrapper.writeList(projectiles.items, PacketWrapper::writePresentItemStack);
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
/* 59 */     if (!(obj instanceof BundleContents)) return false; 
/* 60 */     BundleContents that = (BundleContents)obj;
/* 61 */     return this.items.equals(that.items);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 66 */     return Objects.hashCode(this.items);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\BundleContents.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */