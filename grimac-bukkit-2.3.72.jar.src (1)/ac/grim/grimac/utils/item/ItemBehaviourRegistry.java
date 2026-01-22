/*    */ package ac.grim.grimac.utils.item;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ public class ItemBehaviourRegistry
/*    */ {
/* 11 */   private static final Map<ItemType, ItemBehaviour> ITEM_MAPPING = Map.of(ItemTypes.GOAT_HORN, AlwaysUseItem.INSTANCE, ItemTypes.SHIELD, AlwaysUseItem.INSTANCE, ItemTypes.SPYGLASS, AlwaysUseItem.INSTANCE, ItemTypes.CROSSBOW, UnsupportedItem.INSTANCE, ItemTypes.BOW, UnsupportedItem.INSTANCE, ItemTypes.TRIDENT, TridentItem.INSTANCE);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public static ItemBehaviour getItemBehaviour(ItemType type) {
/* 21 */     return ITEM_MAPPING.getOrDefault(type, ItemBehaviour.INSTANCE);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\item\ItemBehaviourRegistry.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */