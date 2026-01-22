/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.StaticComponentMap;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import java.util.Set;
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
/*    */ 
/*    */ 
/*    */ public interface ItemType
/*    */   extends MappedEntity
/*    */ {
/*    */   int getMaxAmount();
/*    */   
/*    */   int getMaxDurability();
/*    */   
/*    */   default boolean isMusicDisc() {
/* 38 */     return hasAttribute(ItemTypes.ItemAttribute.MUSIC_DISC);
/*    */   }
/*    */   
/*    */   ItemType getCraftRemainder();
/*    */   
/*    */   @Nullable
/*    */   StateType getPlacedType();
/*    */   
/*    */   Set<ItemTypes.ItemAttribute> getAttributes();
/*    */   
/*    */   default boolean hasAttribute(ItemTypes.ItemAttribute attribute) {
/* 49 */     return getAttributes().contains(attribute);
/*    */   }
/*    */   
/*    */   @Deprecated
/*    */   default StaticComponentMap getComponents() {
/* 54 */     return getComponents(PacketEvents.getAPI().getServerManager().getVersion().toClientVersion());
/*    */   }
/*    */   
/*    */   default StaticComponentMap getComponents(ClientVersion clientVersion) {
/* 58 */     return StaticComponentMap.EMPTY;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\type\ItemType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */