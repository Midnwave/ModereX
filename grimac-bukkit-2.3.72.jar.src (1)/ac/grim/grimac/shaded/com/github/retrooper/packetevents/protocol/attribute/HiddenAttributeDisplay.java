/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute;
/*    */ 
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
/*    */ public class HiddenAttributeDisplay
/*    */   implements AttributeDisplay
/*    */ {
/* 27 */   public static final HiddenAttributeDisplay INSTANCE = new HiddenAttributeDisplay();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static HiddenAttributeDisplay read(PacketWrapper<?> wrapper) {
/* 33 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, HiddenAttributeDisplay display) {}
/*    */ 
/*    */   
/*    */   public AttributeDisplayType<?> getType() {
/* 42 */     return AttributeDisplayTypes.HIDDEN;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\attribute\HiddenAttributeDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */