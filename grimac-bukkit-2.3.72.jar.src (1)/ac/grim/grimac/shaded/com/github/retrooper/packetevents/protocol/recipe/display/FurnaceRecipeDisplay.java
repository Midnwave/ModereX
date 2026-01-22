/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.display;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.display.slot.SlotDisplay;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*     */ public class FurnaceRecipeDisplay
/*     */   extends RecipeDisplay<FurnaceRecipeDisplay>
/*     */ {
/*     */   private SlotDisplay<?> ingredient;
/*     */   private SlotDisplay<?> fuel;
/*     */   private SlotDisplay<?> result;
/*     */   private SlotDisplay<?> craftingStation;
/*     */   private int duration;
/*     */   private float experience;
/*     */   
/*     */   public FurnaceRecipeDisplay(SlotDisplay<?> ingredient, SlotDisplay<?> fuel, SlotDisplay<?> result, SlotDisplay<?> craftingStation, int duration, float experience) {
/*  39 */     super(RecipeDisplayTypes.FURNACE);
/*  40 */     this.ingredient = ingredient;
/*  41 */     this.fuel = fuel;
/*  42 */     this.result = result;
/*  43 */     this.craftingStation = craftingStation;
/*  44 */     this.duration = duration;
/*  45 */     this.experience = experience;
/*     */   }
/*     */   
/*     */   public static FurnaceRecipeDisplay read(PacketWrapper<?> wrapper) {
/*  49 */     SlotDisplay<?> ingredient = SlotDisplay.read(wrapper);
/*  50 */     SlotDisplay<?> fuel = SlotDisplay.read(wrapper);
/*  51 */     SlotDisplay<?> result = SlotDisplay.read(wrapper);
/*  52 */     SlotDisplay<?> craftingStation = SlotDisplay.read(wrapper);
/*  53 */     int duration = wrapper.readVarInt();
/*  54 */     float experience = wrapper.readFloat();
/*  55 */     return new FurnaceRecipeDisplay(ingredient, fuel, result, craftingStation, duration, experience);
/*     */   }
/*     */ 
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, FurnaceRecipeDisplay display) {
/*  60 */     SlotDisplay.write(wrapper, display.ingredient);
/*  61 */     SlotDisplay.write(wrapper, display.fuel);
/*  62 */     SlotDisplay.write(wrapper, display.result);
/*  63 */     SlotDisplay.write(wrapper, display.craftingStation);
/*  64 */     wrapper.writeVarInt(display.duration);
/*  65 */     wrapper.writeFloat(display.experience);
/*     */   }
/*     */   
/*     */   public SlotDisplay<?> getIngredient() {
/*  69 */     return this.ingredient;
/*     */   }
/*     */   
/*     */   public void setIngredient(SlotDisplay<?> ingredient) {
/*  73 */     this.ingredient = ingredient;
/*     */   }
/*     */   
/*     */   public SlotDisplay<?> getFuel() {
/*  77 */     return this.fuel;
/*     */   }
/*     */   
/*     */   public void setFuel(SlotDisplay<?> fuel) {
/*  81 */     this.fuel = fuel;
/*     */   }
/*     */   
/*     */   public SlotDisplay<?> getResult() {
/*  85 */     return this.result;
/*     */   }
/*     */   
/*     */   public void setResult(SlotDisplay<?> result) {
/*  89 */     this.result = result;
/*     */   }
/*     */   
/*     */   public SlotDisplay<?> getCraftingStation() {
/*  93 */     return this.craftingStation;
/*     */   }
/*     */   
/*     */   public void setCraftingStation(SlotDisplay<?> craftingStation) {
/*  97 */     this.craftingStation = craftingStation;
/*     */   }
/*     */   
/*     */   public int getDuration() {
/* 101 */     return this.duration;
/*     */   }
/*     */   
/*     */   public void setDuration(int duration) {
/* 105 */     this.duration = duration;
/*     */   }
/*     */   
/*     */   public float getExperience() {
/* 109 */     return this.experience;
/*     */   }
/*     */   
/*     */   public void setExperience(float experience) {
/* 113 */     this.experience = experience;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 118 */     if (this == obj) return true; 
/* 119 */     if (!(obj instanceof FurnaceRecipeDisplay)) return false; 
/* 120 */     FurnaceRecipeDisplay that = (FurnaceRecipeDisplay)obj;
/* 121 */     if (this.duration != that.duration) return false; 
/* 122 */     if (Float.compare(that.experience, this.experience) != 0) return false; 
/* 123 */     if (!this.ingredient.equals(that.ingredient)) return false; 
/* 124 */     if (!this.fuel.equals(that.fuel)) return false; 
/* 125 */     if (!this.result.equals(that.result)) return false; 
/* 126 */     return this.craftingStation.equals(that.craftingStation);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 131 */     return Objects.hash(new Object[] { this.ingredient, this.fuel, this.result, this.craftingStation, Integer.valueOf(this.duration), Float.valueOf(this.experience) });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 136 */     return "FurnaceRecipeDisplay{ingredient=" + this.ingredient + ", fuel=" + this.fuel + ", result=" + this.result + ", craftingStation=" + this.craftingStation + ", duration=" + this.duration + ", experience=" + this.experience + '}';
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\display\FurnaceRecipeDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */