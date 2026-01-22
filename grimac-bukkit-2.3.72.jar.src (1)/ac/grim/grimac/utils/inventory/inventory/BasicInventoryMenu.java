/*    */ package ac.grim.grimac.utils.inventory.inventory;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*    */ import ac.grim.grimac.utils.inventory.Inventory;
/*    */ import ac.grim.grimac.utils.inventory.InventoryStorage;
/*    */ import ac.grim.grimac.utils.inventory.slot.Slot;
/*    */ 
/*    */ public class BasicInventoryMenu extends AbstractContainerMenu {
/*    */   private final int rows;
/*    */   
/*    */   public BasicInventoryMenu(GrimPlayer player, Inventory playerInventory, int rows) {
/* 13 */     super(player, playerInventory);
/* 14 */     this.rows = rows;
/*    */     
/* 16 */     InventoryStorage containerStorage = new InventoryStorage(rows * 9);
/*    */     
/* 18 */     for (int i = 0; i < rows * 9; i++) {
/* 19 */       addSlot(new Slot(containerStorage, i));
/*    */     }
/*    */     
/* 22 */     addFourRowPlayerInventory();
/*    */   }
/*    */ 
/*    */   
/*    */   public ItemStack quickMoveStack(int slotID) {
/* 27 */     ItemStack itemstack = ItemStack.EMPTY;
/* 28 */     Slot slot = this.slots.get(slotID);
/* 29 */     if (slot != null && slot.hasItem()) {
/* 30 */       ItemStack itemstack1 = slot.getItem();
/* 31 */       itemstack = itemstack1.copy();
/* 32 */       if (slotID < this.rows * 9) {
/* 33 */         if (!moveItemStackTo(itemstack1, this.rows * 9, this.slots.size(), true)) {
/* 34 */           return ItemStack.EMPTY;
/*    */         }
/* 36 */       } else if (!moveItemStackTo(itemstack1, 0, this.rows * 9, false)) {
/* 37 */         return ItemStack.EMPTY;
/*    */       } 
/*    */       
/* 40 */       if (itemstack1.isEmpty()) {
/* 41 */         slot.set(ItemStack.EMPTY);
/*    */       }
/*    */     } 
/*    */     
/* 45 */     return itemstack;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\inventory\inventory\BasicInventoryMenu.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */