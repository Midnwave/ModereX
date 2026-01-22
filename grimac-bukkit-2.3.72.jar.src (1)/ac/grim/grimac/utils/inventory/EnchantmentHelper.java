/*    */ package ac.grim.grimac.utils.inventory;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentType;
/*    */ import ac.grim.grimac.utils.latency.CompensatedInventory;
/*    */ 
/*    */ public class EnchantmentHelper
/*    */ {
/*    */   public static int getMaximumEnchantLevel(CompensatedInventory inventory, EnchantmentType enchantmentType) {
/* 10 */     int maxEnchantLevel = 0;
/*    */     
/* 12 */     ItemStack helmet = inventory.getHelmet();
/* 13 */     if (helmet != ItemStack.EMPTY) {
/* 14 */       maxEnchantLevel = Math.max(maxEnchantLevel, helmet.getEnchantmentLevel(enchantmentType));
/*    */     }
/*    */     
/* 17 */     ItemStack chestplate = inventory.getChestplate();
/* 18 */     if (chestplate != ItemStack.EMPTY) {
/* 19 */       maxEnchantLevel = Math.max(maxEnchantLevel, chestplate.getEnchantmentLevel(enchantmentType));
/*    */     }
/*    */     
/* 22 */     ItemStack leggings = inventory.getLeggings();
/* 23 */     if (leggings != ItemStack.EMPTY) {
/* 24 */       maxEnchantLevel = Math.max(maxEnchantLevel, leggings.getEnchantmentLevel(enchantmentType));
/*    */     }
/*    */     
/* 27 */     ItemStack boots = inventory.getBoots();
/* 28 */     if (boots != ItemStack.EMPTY) {
/* 29 */       maxEnchantLevel = Math.max(maxEnchantLevel, boots.getEnchantmentLevel(enchantmentType));
/*    */     }
/*    */     
/* 32 */     return maxEnchantLevel;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\inventory\EnchantmentHelper.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */