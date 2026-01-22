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
/*    */ public class AnyFuelSlotDisplay
/*    */   extends SlotDisplay<AnyFuelSlotDisplay>
/*    */ {
/* 25 */   public static final AnyFuelSlotDisplay INSTANCE = new AnyFuelSlotDisplay();
/*    */   
/*    */   private AnyFuelSlotDisplay() {
/* 28 */     super(SlotDisplayTypes.ANY_FUEL);
/*    */   }
/*    */   
/*    */   public static AnyFuelSlotDisplay read(PacketWrapper<?> wrapper) {
/* 32 */     return INSTANCE;
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, AnyFuelSlotDisplay display) {}
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\display\slot\AnyFuelSlotDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */