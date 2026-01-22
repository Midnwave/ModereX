/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.display.slot;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import java.util.List;
/*    */ import java.util.Objects;
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
/*    */ public class CompositeSlotDisplay
/*    */   extends SlotDisplay<CompositeSlotDisplay>
/*    */ {
/*    */   private List<SlotDisplay<?>> contents;
/*    */   
/*    */   public CompositeSlotDisplay(List<SlotDisplay<?>> contents) {
/* 31 */     super(SlotDisplayTypes.COMPOSITE);
/* 32 */     this.contents = contents;
/*    */   }
/*    */   
/*    */   public static CompositeSlotDisplay read(PacketWrapper<?> wrapper) {
/* 36 */     List<SlotDisplay<?>> contents = wrapper.readList(SlotDisplay::read);
/* 37 */     return new CompositeSlotDisplay(contents);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, CompositeSlotDisplay display) {
/* 41 */     wrapper.writeList(display.contents, SlotDisplay::write);
/*    */   }
/*    */   
/*    */   public List<SlotDisplay<?>> getContents() {
/* 45 */     return this.contents;
/*    */   }
/*    */   
/*    */   public void setContents(List<SlotDisplay<?>> contents) {
/* 49 */     this.contents = contents;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 54 */     if (this == obj) return true; 
/* 55 */     if (!(obj instanceof CompositeSlotDisplay)) return false; 
/* 56 */     CompositeSlotDisplay that = (CompositeSlotDisplay)obj;
/* 57 */     return this.contents.equals(that.contents);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 62 */     return Objects.hashCode(this.contents);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 67 */     return "CompositeSlotDisplay{contents=" + this.contents + '}';
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\display\slot\CompositeSlotDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */