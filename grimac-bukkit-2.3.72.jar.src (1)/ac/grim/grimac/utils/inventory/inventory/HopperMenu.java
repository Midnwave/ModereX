/*    */ package ac.grim.grimac.utils.inventory.inventory;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*    */ import ac.grim.grimac.utils.inventory.Inventory;
/*    */ import ac.grim.grimac.utils.inventory.InventoryStorage;
/*    */ import ac.grim.grimac.utils.inventory.slot.Slot;
/*    */ 
/*    */ public class HopperMenu extends AbstractContainerMenu {
/*    */   public HopperMenu(GrimPlayer player, Inventory playerInventory) {
/* 11 */     super(player, playerInventory);
/*    */     
/* 13 */     InventoryStorage containerStorage = new InventoryStorage(5);
/* 14 */     for (int i = 0; i < 5; i++) {
/* 15 */       addSlot(new Slot(containerStorage, i));
/*    */     }
/*    */     
/* 18 */     addFourRowPlayerInventory();
/*    */   }
/*    */ 
/*    */   
/*    */   public ItemStack quickMoveStack(int slotID) {
/* 23 */     ItemStack itemstack = ItemStack.EMPTY;
/* 24 */     Slot slot = this.slots.get(slotID);
/* 25 */     if (slot != null && slot.hasItem()) {
/* 26 */       ItemStack itemstack1 = slot.getItem();
/* 27 */       itemstack = itemstack1.copy();
/* 28 */       if (slotID < 5) {
/* 29 */         if (!moveItemStackTo(itemstack1, 5, this.slots.size(), true)) {
/* 30 */           return ItemStack.EMPTY;
/*    */         }
/* 32 */       } else if (!moveItemStackTo(itemstack1, 0, 5, false)) {
/* 33 */         return ItemStack.EMPTY;
/*    */       } 
/*    */       
/* 36 */       if (itemstack1.isEmpty()) {
/* 37 */         slot.set(ItemStack.EMPTY);
/*    */       }
/*    */     } 
/*    */     
/* 41 */     return itemstack;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\inventory\inventory\HopperMenu.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */