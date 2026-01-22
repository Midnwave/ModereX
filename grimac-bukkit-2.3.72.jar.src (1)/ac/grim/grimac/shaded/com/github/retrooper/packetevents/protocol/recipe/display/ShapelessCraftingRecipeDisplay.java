/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.display;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.display.slot.SlotDisplay;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ShapelessCraftingRecipeDisplay
/*    */   extends RecipeDisplay<ShapelessCraftingRecipeDisplay>
/*    */ {
/*    */   private List<SlotDisplay<?>> ingredients;
/*    */   private SlotDisplay<?> result;
/*    */   private SlotDisplay<?> craftingStation;
/*    */   
/*    */   public ShapelessCraftingRecipeDisplay(List<SlotDisplay<?>> ingredients, SlotDisplay<?> result, SlotDisplay<?> craftingStation) {
/* 37 */     super(RecipeDisplayTypes.CRAFTING_SHAPELESS);
/* 38 */     this.ingredients = ingredients;
/* 39 */     this.result = result;
/* 40 */     this.craftingStation = craftingStation;
/*    */   }
/*    */   
/*    */   public static ShapelessCraftingRecipeDisplay read(PacketWrapper<?> wrapper) {
/* 44 */     List<SlotDisplay<?>> ingredients = wrapper.readList(SlotDisplay::read);
/* 45 */     SlotDisplay<?> result = SlotDisplay.read(wrapper);
/* 46 */     SlotDisplay<?> craftingStation = SlotDisplay.read(wrapper);
/* 47 */     return new ShapelessCraftingRecipeDisplay(ingredients, result, craftingStation);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ShapelessCraftingRecipeDisplay display) {
/* 51 */     wrapper.writeList(display.ingredients, SlotDisplay::write);
/* 52 */     SlotDisplay.write(wrapper, display.result);
/* 53 */     SlotDisplay.write(wrapper, display.craftingStation);
/*    */   }
/*    */   
/*    */   public List<SlotDisplay<?>> getIngredients() {
/* 57 */     return this.ingredients;
/*    */   }
/*    */   
/*    */   public void setIngredients(List<SlotDisplay<?>> ingredients) {
/* 61 */     this.ingredients = ingredients;
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
/* 83 */     if (!(obj instanceof ShapelessCraftingRecipeDisplay)) return false; 
/* 84 */     ShapelessCraftingRecipeDisplay that = (ShapelessCraftingRecipeDisplay)obj;
/* 85 */     if (!this.ingredients.equals(that.ingredients)) return false; 
/* 86 */     if (!this.result.equals(that.result)) return false; 
/* 87 */     return this.craftingStation.equals(that.craftingStation);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 92 */     return Objects.hash(new Object[] { this.ingredients, this.result, this.craftingStation });
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 97 */     return "ShapelessCraftingRecipeDisplay{ingredients=" + this.ingredients + ", result=" + this.result + ", craftingStation=" + this.craftingStation + '}';
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\display\ShapelessCraftingRecipeDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */