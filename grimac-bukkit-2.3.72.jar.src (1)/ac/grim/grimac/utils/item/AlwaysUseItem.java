/*    */ package ac.grim.grimac.utils.item;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.InteractionHand;
/*    */ import ac.grim.grimac.utils.latency.CompensatedWorld;
/*    */ 
/*    */ public class AlwaysUseItem
/*    */   extends ItemBehaviour {
/* 10 */   public static final AlwaysUseItem INSTANCE = new AlwaysUseItem();
/*    */ 
/*    */   
/*    */   public boolean canUse(ItemStack item, CompensatedWorld world, GrimPlayer player, InteractionHand hand) {
/* 14 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\item\AlwaysUseItem.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */