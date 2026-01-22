/*    */ package ac.grim.grimac.utils.inventory.slot;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*    */ import ac.grim.grimac.utils.inventory.InventoryStorage;
/*    */ 
/*    */ public class ResultSlot
/*    */   extends Slot {
/*    */   public ResultSlot(InventoryStorage container, int slot) {
/* 10 */     super(container, slot);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean mayPlace(ItemStack itemStack) {
/* 15 */     return false;
/*    */   }
/*    */   
/*    */   public void onTake(GrimPlayer player, ItemStack itemStack) {}
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\inventory\slot\ResultSlot.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */