/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.display.slot;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*    */ public class WithRemainderSlotDisplay
/*    */   extends SlotDisplay<WithRemainderSlotDisplay>
/*    */ {
/*    */   private SlotDisplay<?> input;
/*    */   private SlotDisplay<?> remainder;
/*    */   
/*    */   public WithRemainderSlotDisplay(SlotDisplay<?> input, SlotDisplay<?> remainder) {
/* 31 */     super(SlotDisplayTypes.WITH_REMAINDER);
/* 32 */     this.input = input;
/* 33 */     this.remainder = remainder;
/*    */   }
/*    */   
/*    */   public static WithRemainderSlotDisplay read(PacketWrapper<?> wrapper) {
/* 37 */     SlotDisplay<?> input = SlotDisplay.read(wrapper);
/* 38 */     SlotDisplay<?> remainder = SlotDisplay.read(wrapper);
/* 39 */     return new WithRemainderSlotDisplay(input, remainder);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, WithRemainderSlotDisplay display) {
/* 43 */     SlotDisplay.write(wrapper, display.input);
/* 44 */     SlotDisplay.write(wrapper, display.remainder);
/*    */   }
/*    */   
/*    */   public SlotDisplay<?> getInput() {
/* 48 */     return this.input;
/*    */   }
/*    */   
/*    */   public void setInput(SlotDisplay<?> input) {
/* 52 */     this.input = input;
/*    */   }
/*    */   
/*    */   public SlotDisplay<?> getRemainder() {
/* 56 */     return this.remainder;
/*    */   }
/*    */   
/*    */   public void setRemainder(SlotDisplay<?> remainder) {
/* 60 */     this.remainder = remainder;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 65 */     if (this == obj) return true; 
/* 66 */     if (!(obj instanceof WithRemainderSlotDisplay)) return false; 
/* 67 */     WithRemainderSlotDisplay that = (WithRemainderSlotDisplay)obj;
/* 68 */     if (!this.input.equals(that.input)) return false; 
/* 69 */     return this.remainder.equals(that.remainder);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 74 */     return Objects.hash(new Object[] { this.input, this.remainder });
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 79 */     return "WithRemainderSlotDisplay{input=" + this.input + ", remainder=" + this.remainder + '}';
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\display\slot\WithRemainderSlotDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */