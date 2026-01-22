/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import java.util.Objects;
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
/*    */ 
/*    */ @NullMarked
/*    */ public class OverrideAttributeDisplay
/*    */   implements AttributeDisplay
/*    */ {
/*    */   private final Component component;
/*    */   
/*    */   public OverrideAttributeDisplay(Component component) {
/* 33 */     this.component = component;
/*    */   }
/*    */   
/*    */   public static OverrideAttributeDisplay read(PacketWrapper<?> wrapper) {
/* 37 */     return new OverrideAttributeDisplay(wrapper.readComponent());
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, OverrideAttributeDisplay display) {
/* 41 */     wrapper.writeComponent(display.component);
/*    */   }
/*    */ 
/*    */   
/*    */   public AttributeDisplayType<?> getType() {
/* 46 */     return AttributeDisplayTypes.OVERRIDE;
/*    */   }
/*    */   
/*    */   public Component getComponent() {
/* 50 */     return this.component;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 55 */     if (!(obj instanceof OverrideAttributeDisplay)) return false; 
/* 56 */     OverrideAttributeDisplay that = (OverrideAttributeDisplay)obj;
/* 57 */     return this.component.equals(that.component);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 62 */     return Objects.hashCode(this.component);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\attribute\OverrideAttributeDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */