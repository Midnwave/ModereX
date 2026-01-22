/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.display;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.display.slot.SlotDisplay;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ShapedCraftingRecipeDisplay
/*     */   extends RecipeDisplay<ShapedCraftingRecipeDisplay>
/*     */ {
/*     */   private int width;
/*     */   private int height;
/*     */   private List<SlotDisplay<?>> ingredients;
/*     */   private SlotDisplay<?> result;
/*     */   private SlotDisplay<?> craftingStation;
/*     */   
/*     */   public ShapedCraftingRecipeDisplay(int width, int height, List<SlotDisplay<?>> ingredients, SlotDisplay<?> result, SlotDisplay<?> craftingStation) {
/*  41 */     super(RecipeDisplayTypes.CRAFTING_SHAPED);
/*  42 */     this.width = width;
/*  43 */     this.height = height;
/*  44 */     this.ingredients = ingredients;
/*  45 */     this.result = result;
/*  46 */     this.craftingStation = craftingStation;
/*     */   }
/*     */   
/*     */   public static ShapedCraftingRecipeDisplay read(PacketWrapper<?> wrapper) {
/*  50 */     int width = wrapper.readVarInt();
/*  51 */     int height = wrapper.readVarInt();
/*  52 */     List<SlotDisplay<?>> ingredients = wrapper.readList(SlotDisplay::read);
/*  53 */     SlotDisplay<?> result = SlotDisplay.read(wrapper);
/*  54 */     SlotDisplay<?> craftingStation = SlotDisplay.read(wrapper);
/*  55 */     return new ShapedCraftingRecipeDisplay(width, height, ingredients, result, craftingStation);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, ShapedCraftingRecipeDisplay display) {
/*  59 */     wrapper.writeVarInt(display.width);
/*  60 */     wrapper.writeVarInt(display.height);
/*  61 */     wrapper.writeList(display.ingredients, SlotDisplay::write);
/*  62 */     SlotDisplay.write(wrapper, display.result);
/*  63 */     SlotDisplay.write(wrapper, display.craftingStation);
/*     */   }
/*     */   
/*     */   public int getWidth() {
/*  67 */     return this.width;
/*     */   }
/*     */   
/*     */   public void setWidth(int width) {
/*  71 */     this.width = width;
/*     */   }
/*     */   
/*     */   public int getHeight() {
/*  75 */     return this.height;
/*     */   }
/*     */   
/*     */   public void setHeight(int height) {
/*  79 */     this.height = height;
/*     */   }
/*     */   
/*     */   public List<SlotDisplay<?>> getIngredients() {
/*  83 */     return this.ingredients;
/*     */   }
/*     */   
/*     */   public void setIngredients(List<SlotDisplay<?>> ingredients) {
/*  87 */     this.ingredients = ingredients;
/*     */   }
/*     */   
/*     */   public SlotDisplay<?> getResult() {
/*  91 */     return this.result;
/*     */   }
/*     */   
/*     */   public void setResult(SlotDisplay<?> result) {
/*  95 */     this.result = result;
/*     */   }
/*     */   
/*     */   public SlotDisplay<?> getCraftingStation() {
/*  99 */     return this.craftingStation;
/*     */   }
/*     */   
/*     */   public void setCraftingStation(SlotDisplay<?> craftingStation) {
/* 103 */     this.craftingStation = craftingStation;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 108 */     if (this == obj) return true; 
/* 109 */     if (!(obj instanceof ShapedCraftingRecipeDisplay)) return false; 
/* 110 */     ShapedCraftingRecipeDisplay that = (ShapedCraftingRecipeDisplay)obj;
/* 111 */     if (this.width != that.width) return false; 
/* 112 */     if (this.height != that.height) return false; 
/* 113 */     if (!this.ingredients.equals(that.ingredients)) return false; 
/* 114 */     if (!this.result.equals(that.result)) return false; 
/* 115 */     return this.craftingStation.equals(that.craftingStation);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 120 */     return Objects.hash(new Object[] { Integer.valueOf(this.width), Integer.valueOf(this.height), this.ingredients, this.result, this.craftingStation });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 125 */     return "ShapedCraftingRecipeDisplay{width=" + this.width + ", height=" + this.height + ", ingredients=" + this.ingredients + ", result=" + this.result + ", craftingStation=" + this.craftingStation + '}';
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\display\ShapedCraftingRecipeDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */