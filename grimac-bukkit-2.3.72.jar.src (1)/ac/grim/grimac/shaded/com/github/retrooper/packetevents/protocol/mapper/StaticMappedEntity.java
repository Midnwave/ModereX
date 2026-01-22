/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
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
/*    */ public interface StaticMappedEntity
/*    */   extends MappedEntity
/*    */ {
/*    */   int getId();
/*    */   
/*    */   default int getId(ClientVersion version) {
/* 27 */     return getId();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\mapper\StaticMappedEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */