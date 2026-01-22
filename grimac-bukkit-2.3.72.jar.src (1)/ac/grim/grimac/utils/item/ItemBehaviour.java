/*    */ package ac.grim.grimac.utils.item;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.ComponentTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.FoodProperties;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemBlocksAttacks;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemConsumable;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemEquippable;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.InteractionHand;
/*    */ import ac.grim.grimac.utils.latency.CompensatedWorld;
/*    */ 
/*    */ public class ItemBehaviour
/*    */ {
/* 17 */   public static final ItemBehaviour INSTANCE = new ItemBehaviour();
/*    */   
/*    */   public boolean canUse(ItemStack item, CompensatedWorld world, GrimPlayer player, InteractionHand hand) {
/* 20 */     ItemConsumable consumable = (ItemConsumable)item.getComponentOr(ComponentTypes.CONSUMABLE, null);
/* 21 */     if (consumable != null) {
/* 22 */       return testConsumableComponent(item, world, player, hand, consumable);
/*    */     }
/* 24 */     if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21_5)) {
/* 25 */       ItemBlocksAttacks blocksAttacks = (ItemBlocksAttacks)item.getComponentOr(ComponentTypes.BLOCKS_ATTACKS, null);
/* 26 */       ItemEquippable equippable = (ItemEquippable)item.getComponentOr(ComponentTypes.EQUIPPABLE, null);
/*    */       
/* 28 */       return ((equippable == null || !equippable.isSwappable()) && blocksAttacks != null);
/*    */     } 
/*    */     
/* 31 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean testConsumableComponent(ItemStack item, CompensatedWorld world, GrimPlayer player, InteractionHand hand, ItemConsumable consumable) {
/* 36 */     if (!testFoodComponent(item, world, player, hand)) {
/* 37 */       return false;
/*    */     }
/*    */     
/* 40 */     return (consumable.getConsumeSeconds() * 20.0F > 0.0F);
/*    */   }
/*    */   
/*    */   protected boolean testFoodComponent(ItemStack item, CompensatedWorld world, GrimPlayer player, InteractionHand hand) {
/* 44 */     FoodProperties foodProperties = (FoodProperties)item.getComponentOr(ComponentTypes.FOOD, null);
/* 45 */     return (foodProperties != null) ? ((foodProperties.isCanAlwaysEat() || player.food < 20 || player.gamemode == GameMode.CREATIVE)) : true;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\item\ItemBehaviour.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */