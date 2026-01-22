/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import org.jspecify.annotations.NullMarked;
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
/*    */ @NullMarked
/*    */ public interface AttributeDisplay
/*    */ {
/*    */   static AttributeDisplay read(PacketWrapper<?> wrapper) {
/* 28 */     AttributeDisplayType<?> type = (AttributeDisplayType)wrapper.readMappedEntity((IRegistry)AttributeDisplayTypes.getRegistry());
/* 29 */     return (AttributeDisplay)type.read(wrapper);
/*    */   }
/*    */ 
/*    */   
/*    */   static void write(PacketWrapper<?> wrapper, AttributeDisplay display) {
/* 34 */     wrapper.writeMappedEntity(display.getType());
/* 35 */     display.getType().write(wrapper, display);
/*    */   }
/*    */   
/*    */   AttributeDisplayType<?> getType();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\attribute\AttributeDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */