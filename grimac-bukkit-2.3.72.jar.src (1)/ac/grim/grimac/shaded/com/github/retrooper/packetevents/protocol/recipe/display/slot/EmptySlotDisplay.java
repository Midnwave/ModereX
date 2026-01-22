/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.display.slot;
/*    */ 
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
/*    */ public class EmptySlotDisplay
/*    */   extends SlotDisplay<EmptySlotDisplay>
/*    */ {
/* 25 */   public static final EmptySlotDisplay INSTANCE = new EmptySlotDisplay();
/*    */   
/*    */   private EmptySlotDisplay() {
/* 28 */     super(SlotDisplayTypes.EMPTY);
/*    */   }
/*    */   
/*    */   public static EmptySlotDisplay read(PacketWrapper<?> wrapper) {
/* 32 */     return INSTANCE;
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, EmptySlotDisplay display) {}
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\display\slot\EmptySlotDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */