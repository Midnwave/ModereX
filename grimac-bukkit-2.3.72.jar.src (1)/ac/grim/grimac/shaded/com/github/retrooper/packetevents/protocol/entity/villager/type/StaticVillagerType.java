/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.villager.type;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.AbstractMappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
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
/*    */ public class StaticVillagerType
/*    */   extends AbstractMappedEntity
/*    */   implements VillagerType
/*    */ {
/*    */   @Internal
/*    */   public StaticVillagerType(@Nullable TypesBuilderData data) {
/* 32 */     super(data);
/*    */   }
/*    */ 
/*    */   
/*    */   public int getId() {
/* 37 */     ServerVersion version = PacketEvents.getAPI().getServerManager().getVersion();
/* 38 */     return getId(version.toClientVersion());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\villager\type\StaticVillagerType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */