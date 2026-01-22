/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
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
/*    */ public interface MappedEntity
/*    */ {
/*    */   ResourceLocation getName();
/*    */   
/*    */   int getId(ClientVersion paramClientVersion);
/*    */   
/*    */   default boolean isRegistered() {
/* 31 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\mapper\MappedEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */