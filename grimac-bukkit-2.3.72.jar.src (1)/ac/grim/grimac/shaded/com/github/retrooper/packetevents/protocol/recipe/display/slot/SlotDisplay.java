/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.display.slot;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*    */ public abstract class SlotDisplay<T extends SlotDisplay<?>>
/*    */ {
/*    */   protected final SlotDisplayType<T> type;
/*    */   
/*    */   public SlotDisplay(SlotDisplayType<T> type) {
/* 28 */     this.type = type;
/*    */   }
/*    */   
/*    */   public static SlotDisplay<?> read(PacketWrapper<?> wrapper) {
/* 32 */     return ((SlotDisplayType<SlotDisplay<?>>)wrapper.readMappedEntity((IRegistry)SlotDisplayTypes.getRegistry())).read(wrapper);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static <T extends SlotDisplay<?>> void write(PacketWrapper<?> wrapper, T display) {
/* 38 */     wrapper.writeMappedEntity(display.getType());
/* 39 */     display.getType().write(wrapper, display);
/*    */   }
/*    */   
/*    */   public SlotDisplayType<T> getType() {
/* 43 */     return this.type;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\display\slot\SlotDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */