/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.data;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.Ingredient;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
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
/*    */ @Obsolete
/*    */ public class SmithingTrimRecipeData
/*    */   implements RecipeData
/*    */ {
/*    */   private final Ingredient template;
/*    */   private final Ingredient base;
/*    */   private final Ingredient addition;
/*    */   
/*    */   public SmithingTrimRecipeData(Ingredient template, Ingredient base, Ingredient addition) {
/* 36 */     this.template = template;
/* 37 */     this.base = base;
/* 38 */     this.addition = addition;
/*    */   }
/*    */   
/*    */   public static SmithingTrimRecipeData read(PacketWrapper<?> wrapper) {
/* 42 */     Ingredient template = Ingredient.read(wrapper);
/* 43 */     Ingredient base = Ingredient.read(wrapper);
/* 44 */     Ingredient addition = Ingredient.read(wrapper);
/* 45 */     return new SmithingTrimRecipeData(template, base, addition);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, SmithingTrimRecipeData data) {
/* 49 */     Ingredient.write(wrapper, data.template);
/* 50 */     Ingredient.write(wrapper, data.base);
/* 51 */     Ingredient.write(wrapper, data.addition);
/*    */   }
/*    */   
/*    */   public Ingredient getTemplate() {
/* 55 */     return this.template;
/*    */   }
/*    */   
/*    */   public Ingredient getBase() {
/* 59 */     return this.base;
/*    */   }
/*    */   
/*    */   public Ingredient getAddition() {
/* 63 */     return this.addition;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\data\SmithingTrimRecipeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */