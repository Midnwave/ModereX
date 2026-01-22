/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.display;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.display.slot.SlotDisplay;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StonecutterRecipeDisplay
/*    */   extends RecipeDisplay<StonecutterRecipeDisplay>
/*    */ {
/*    */   private SlotDisplay<?> input;
/*    */   private SlotDisplay<?> result;
/*    */   private SlotDisplay<?> craftingStation;
/*    */   
/*    */   public StonecutterRecipeDisplay(SlotDisplay<?> input, SlotDisplay<?> result, SlotDisplay<?> craftingStation) {
/* 37 */     super(RecipeDisplayTypes.STONECUTTER);
/* 38 */     this.input = input;
/* 39 */     this.result = result;
/* 40 */     this.craftingStation = craftingStation;
/*    */   }
/*    */   
/*    */   public static StonecutterRecipeDisplay read(PacketWrapper<?> wrapper) {
/* 44 */     SlotDisplay<?> input = SlotDisplay.read(wrapper);
/* 45 */     SlotDisplay<?> result = SlotDisplay.read(wrapper);
/* 46 */     SlotDisplay<?> craftingStation = SlotDisplay.read(wrapper);
/* 47 */     return new StonecutterRecipeDisplay(input, result, craftingStation);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, StonecutterRecipeDisplay display) {
/* 51 */     SlotDisplay.write(wrapper, display.input);
/* 52 */     SlotDisplay.write(wrapper, display.result);
/* 53 */     SlotDisplay.write(wrapper, display.craftingStation);
/*    */   }
/*    */   
/*    */   public SlotDisplay<?> getInput() {
/* 57 */     return this.input;
/*    */   }
/*    */   
/*    */   public void setInput(SlotDisplay<?> input) {
/* 61 */     this.input = input;
/*    */   }
/*    */   
/*    */   public SlotDisplay<?> getResult() {
/* 65 */     return this.result;
/*    */   }
/*    */   
/*    */   public void setResult(SlotDisplay<?> result) {
/* 69 */     this.result = result;
/*    */   }
/*    */   
/*    */   public SlotDisplay<?> getCraftingStation() {
/* 73 */     return this.craftingStation;
/*    */   }
/*    */   
/*    */   public void setCraftingStation(SlotDisplay<?> craftingStation) {
/* 77 */     this.craftingStation = craftingStation;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 82 */     if (this == obj) return true; 
/* 83 */     if (!(obj instanceof StonecutterRecipeDisplay)) return false; 
/* 84 */     StonecutterRecipeDisplay that = (StonecutterRecipeDisplay)obj;
/* 85 */     if (!this.input.equals(that.input)) return false; 
/* 86 */     if (!this.result.equals(that.result)) return false; 
/* 87 */     return this.craftingStation.equals(that.craftingStation);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 92 */     return Objects.hash(new Object[] { this.input, this.result, this.craftingStation });
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 97 */     return "StonecutterRecipeDisplay{input=" + this.input + ", result=" + this.result + ", craftingStation=" + this.craftingStation + '}';
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\display\StonecutterRecipeDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */