/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.data;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import java.util.List;
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
/*    */ 
/*    */ 
/*    */ public interface EntityMetadataProvider
/*    */ {
/*    */   List<EntityData<?>> entityData(ClientVersion paramClientVersion);
/*    */   
/*    */   @Deprecated
/*    */   default List<EntityData<?>> entityData() {
/* 33 */     return entityData(ClientVersion.getLatest());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\data\EntityMetadataProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */