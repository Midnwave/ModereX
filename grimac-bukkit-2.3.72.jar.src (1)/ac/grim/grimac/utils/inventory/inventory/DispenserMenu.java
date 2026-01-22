/*    */ package ac.grim.grimac.utils.inventory.inventory;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*    */ import ac.grim.grimac.utils.inventory.Inventory;
/*    */ import ac.grim.grimac.utils.inventory.InventoryStorage;
/*    */ import ac.grim.grimac.utils.inventory.slot.Slot;
/*    */ 
/*    */ public class DispenserMenu extends AbstractContainerMenu {
/*    */   public DispenserMenu(GrimPlayer player, Inventory playerInventory) {
/* 11 */     super(player, playerInventory);
/*    */     
/* 13 */     InventoryStorage containerStorage = new InventoryStorage(9);
/*    */     
/* 15 */     for (int i = 0; i < 9; i++) {
/* 16 */       addSlot(new Slot(containerStorage, i));
/*    */     }
/*    */     
/* 19 */     addFourRowPlayerInventory();
/*    */   }
/*    */ 
/*    */   
/*    */   public ItemStack quickMoveStack(int slotID) {
/* 24 */     ItemStack itemstack = ItemStack.EMPTY;
/* 25 */     Slot slot = this.slots.get(slotID);
/* 26 */     if (slot != null && slot.hasItem()) {
/* 27 */       ItemStack itemstack1 = slot.getItem();
/* 28 */       itemstack = itemstack1.copy();
/* 29 */       if (slotID < 9) {
/* 30 */         if (!moveItemStackTo(itemstack1, 9, 45, true)) {
/* 31 */           return ItemStack.EMPTY;
/*    */         }
/* 33 */       } else if (!moveItemStackTo(itemstack1, 0, 9, false)) {
/* 34 */         return ItemStack.EMPTY;
/*    */       } 
/*    */       
/* 37 */       if (itemstack1.isEmpty()) {
/* 38 */         slot.set(ItemStack.EMPTY);
/*    */       }
/*    */       
/* 41 */       if (itemstack1.getAmount() == itemstack.getAmount()) {
/* 42 */         return ItemStack.EMPTY;
/*    */       }
/*    */       
/* 45 */       slot.onTake(this.player, itemstack1);
/*    */     } 
/*    */     
/* 48 */     return itemstack;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\inventory\inventory\DispenserMenu.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */