/*    */ package ac.grim.grimac.utils.inventory.slot;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*    */ import ac.grim.grimac.utils.inventory.EquipmentType;
/*    */ import ac.grim.grimac.utils.inventory.InventoryStorage;
/*    */ 
/*    */ public class EquipmentSlot extends Slot {
/*    */   private final EquipmentType type;
/*    */   
/*    */   public EquipmentSlot(EquipmentType type, InventoryStorage menu, int slot) {
/* 14 */     super(menu, slot);
/* 15 */     this.type = type;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getMaxStackSize() {
/* 20 */     return 1;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean mayPlace(ItemStack itemStack) {
/* 25 */     return (this.type == EquipmentType.getEquipmentSlotForItem(itemStack));
/*    */   }
/*    */   
/*    */   public boolean mayPickup(GrimPlayer player) {
/* 29 */     ItemStack itemstack = getItem();
/* 30 */     return ((itemstack.isEmpty() || player.gamemode == GameMode.CREATIVE || itemstack.getEnchantmentLevel(EnchantmentTypes.BINDING_CURSE) == 0) && super.mayPickup(player));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\inventory\slot\EquipmentSlot.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */