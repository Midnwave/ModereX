/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.MathUtil;
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
/*    */ public interface Attribute
/*    */   extends MappedEntity
/*    */ {
/*    */   default ResourceLocation getName() {
/* 31 */     return getName(PacketEvents.getAPI().getServerManager().getVersion().toClientVersion());
/*    */   }
/*    */   
/*    */   ResourceLocation getName(ClientVersion paramClientVersion);
/*    */   
/*    */   default double sanitizeValue(double value) {
/* 37 */     return sanitizeValue(value, PacketEvents.getAPI().getServerManager().getVersion().toClientVersion());
/*    */   }
/*    */   
/*    */   default double sanitizeValue(double value, ClientVersion version) {
/* 41 */     if (!Double.isNaN(value)) {
/* 42 */       return MathUtil.clamp(value, getMinValue(), getMaxValue());
/*    */     }
/* 44 */     return getMinValue();
/*    */   }
/*    */   
/*    */   double getDefaultValue();
/*    */   
/*    */   double getMinValue();
/*    */   
/*    */   double getMaxValue();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\attribute\Attribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */