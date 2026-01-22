/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.display;
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
/*    */ public abstract class RecipeDisplay<T extends RecipeDisplay<?>>
/*    */ {
/*    */   protected final RecipeDisplayType<T> type;
/*    */   
/*    */   public RecipeDisplay(RecipeDisplayType<T> type) {
/* 28 */     this.type = type;
/*    */   }
/*    */   
/*    */   public static RecipeDisplay<?> read(PacketWrapper<?> wrapper) {
/* 32 */     return ((RecipeDisplayType<RecipeDisplay<?>>)wrapper.readMappedEntity((IRegistry)RecipeDisplayTypes.getRegistry())).read(wrapper);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static <T extends RecipeDisplay<?>> void write(PacketWrapper<?> wrapper, T display) {
/* 38 */     wrapper.writeMappedEntity(display.getType());
/* 39 */     display.getType().write(wrapper, display);
/*    */   }
/*    */   
/*    */   public RecipeDisplayType<T> getType() {
/* 43 */     return this.type;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\display\RecipeDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */