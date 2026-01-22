/*    */ package ac.grim.grimac.utils.item;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.InteractionHand;
/*    */ import ac.grim.grimac.utils.latency.CompensatedWorld;
/*    */ 
/*    */ public class TridentItem
/*    */   extends ItemBehaviour {
/* 11 */   public static TridentItem INSTANCE = new TridentItem();
/*    */ 
/*    */   
/*    */   public boolean canUse(ItemStack item, CompensatedWorld world, GrimPlayer player, InteractionHand hand) {
/* 15 */     if (nextDamageWillBreak(item)) {
/* 16 */       return false;
/*    */     }
/*    */     
/* 19 */     return (item.getEnchantmentLevel(EnchantmentTypes.RIPTIDE) <= 0);
/*    */   }
/*    */   
/*    */   private boolean nextDamageWillBreak(ItemStack item) {
/* 23 */     return (item.isDamageableItem() && item.getDamageValue() >= item.getMaxDamage() - 1);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\item\TridentItem.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */