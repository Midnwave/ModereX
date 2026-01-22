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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SmithingRecipeDisplay
/*     */   extends RecipeDisplay<SmithingRecipeDisplay>
/*     */ {
/*     */   private SlotDisplay<?> template;
/*     */   private SlotDisplay<?> base;
/*     */   private SlotDisplay<?> addition;
/*     */   private SlotDisplay<?> result;
/*     */   private SlotDisplay<?> craftingStation;
/*     */   
/*     */   public SmithingRecipeDisplay(SlotDisplay<?> template, SlotDisplay<?> base, SlotDisplay<?> addition, SlotDisplay<?> result, SlotDisplay<?> craftingStation) {
/*  41 */     super(RecipeDisplayTypes.SMITHING);
/*  42 */     this.template = template;
/*  43 */     this.base = base;
/*  44 */     this.addition = addition;
/*  45 */     this.result = result;
/*  46 */     this.craftingStation = craftingStation;
/*     */   }
/*     */   
/*     */   public static SmithingRecipeDisplay read(PacketWrapper<?> wrapper) {
/*  50 */     SlotDisplay<?> template = SlotDisplay.read(wrapper);
/*  51 */     SlotDisplay<?> base = SlotDisplay.read(wrapper);
/*  52 */     SlotDisplay<?> addition = SlotDisplay.read(wrapper);
/*  53 */     SlotDisplay<?> result = SlotDisplay.read(wrapper);
/*  54 */     SlotDisplay<?> craftingStation = SlotDisplay.read(wrapper);
/*  55 */     return new SmithingRecipeDisplay(template, base, addition, result, craftingStation);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, SmithingRecipeDisplay display) {
/*  59 */     SlotDisplay.write(wrapper, display.template);
/*  60 */     SlotDisplay.write(wrapper, display.base);
/*  61 */     SlotDisplay.write(wrapper, display.addition);
/*  62 */     SlotDisplay.write(wrapper, display.result);
/*  63 */     SlotDisplay.write(wrapper, display.craftingStation);
/*     */   }
/*     */   
/*     */   public SlotDisplay<?> getTemplate() {
/*  67 */     return this.template;
/*     */   }
/*     */   
/*     */   public void setTemplate(SlotDisplay<?> template) {
/*  71 */     this.template = template;
/*     */   }
/*     */   
/*     */   public SlotDisplay<?> getBase() {
/*  75 */     return this.base;
/*     */   }
/*     */   
/*     */   public void setBase(SlotDisplay<?> base) {
/*  79 */     this.base = base;
/*     */   }
/*     */   
/*     */   public SlotDisplay<?> getAddition() {
/*  83 */     return this.addition;
/*     */   }
/*     */   
/*     */   public void setAddition(SlotDisplay<?> addition) {
/*  87 */     this.addition = addition;
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
/* 109 */     if (!(obj instanceof SmithingRecipeDisplay)) return false; 
/* 110 */     SmithingRecipeDisplay that = (SmithingRecipeDisplay)obj;
/* 111 */     if (!this.template.equals(that.template)) return false; 
/* 112 */     if (!this.base.equals(that.base)) return false; 
/* 113 */     if (!this.addition.equals(that.addition)) return false; 
/* 114 */     if (!this.result.equals(that.result)) return false; 
/* 115 */     return this.craftingStation.equals(that.craftingStation);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 120 */     return Objects.hash(new Object[] { this.template, this.base, this.addition, this.result, this.craftingStation });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 125 */     return "SmithingRecipeDisplay{template=" + this.template + ", base=" + this.base + ", addition=" + this.addition + ", result=" + this.result + ", craftingStation=" + this.craftingStation + '}';
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\display\SmithingRecipeDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */