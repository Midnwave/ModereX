/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
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
/*    */ public class Equipment
/*    */ {
/*    */   private EquipmentSlot slot;
/*    */   private ItemStack item;
/*    */   
/*    */   public Equipment(EquipmentSlot slot, ItemStack item) {
/* 28 */     this.slot = slot;
/* 29 */     this.item = item;
/*    */   }
/*    */   
/*    */   public EquipmentSlot getSlot() {
/* 33 */     return this.slot;
/*    */   }
/*    */   
/*    */   public void setSlot(EquipmentSlot slot) {
/* 37 */     this.slot = slot;
/*    */   }
/*    */   
/*    */   public ItemStack getItem() {
/* 41 */     return this.item;
/*    */   }
/*    */   
/*    */   public void setItem(ItemStack item) {
/* 45 */     this.item = item;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 50 */     if (obj instanceof Equipment) {
/* 51 */       Equipment equipment = (Equipment)obj;
/* 52 */       return (equipment.getSlot() == this.slot && equipment.getItem().equals(this.item));
/*    */     } 
/* 54 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\player\Equipment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */