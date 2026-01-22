/*    */ package ac.grim.grimac.utils.inventory;
/*    */ 
/*    */ public class InventoryStorage {
/*    */   protected ItemStack[] items;
/*    */   
/*    */   @Generated
/*    */   public int getSize() {
/*  8 */     return this.size;
/*    */   }
/*    */   int size;
/*    */   public InventoryStorage(int size) {
/* 12 */     this.items = new ItemStack[size];
/* 13 */     this.size = size;
/*    */     
/* 15 */     for (int i = 0; i < size; i++) {
/* 16 */       this.items[i] = ItemStack.EMPTY;
/*    */     }
/*    */   }
/*    */   
/*    */   public void setItem(int item, ItemStack stack) {
/* 21 */     this.items[item] = (stack == null) ? ItemStack.EMPTY : stack;
/*    */   }
/*    */   
/*    */   public ItemStack getItem(int index) {
/* 25 */     return this.items[index];
/*    */   }
/*    */   
/*    */   public ItemStack removeItem(int slot, int amount) {
/* 29 */     return (slot >= 0 && slot < this.items.length && !this.items[slot].isEmpty() && amount > 0) ? this.items[slot].split(amount) : ItemStack.EMPTY;
/*    */   }
/*    */   
/*    */   public int getMaxStackSize() {
/* 33 */     return 64;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\inventory\InventoryStorage.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */