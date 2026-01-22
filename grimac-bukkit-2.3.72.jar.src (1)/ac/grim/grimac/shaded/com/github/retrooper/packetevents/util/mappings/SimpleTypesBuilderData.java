/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings;
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
/*    */ public class SimpleTypesBuilderData
/*    */   extends TypesBuilderData
/*    */ {
/*    */   public SimpleTypesBuilderData(ResourceLocation name, int id) {
/* 27 */     super(null, name, new int[] { id });
/*    */   }
/*    */ 
/*    */   
/*    */   public int getId(ClientVersion version) {
/* 32 */     return this.data[0];
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\mappings\SimpleTypesBuilderData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */