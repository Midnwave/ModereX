/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.data;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
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
/*    */ public class StoneCuttingRecipeData
/*    */   implements RecipeData
/*    */ {
/*    */   private final String group;
/*    */   private final Ingredient ingredient;
/*    */   private final ItemStack result;
/*    */   
/*    */   public StoneCuttingRecipeData(String group, Ingredient ingredient, ItemStack result) {
/* 37 */     this.group = group;
/* 38 */     this.ingredient = ingredient;
/* 39 */     this.result = result;
/*    */   }
/*    */   
/*    */   public static StoneCuttingRecipeData read(PacketWrapper<?> wrapper) {
/* 43 */     String group = wrapper.readString();
/* 44 */     Ingredient ingredient = Ingredient.read(wrapper);
/* 45 */     ItemStack result = wrapper.readItemStack();
/* 46 */     return new StoneCuttingRecipeData(group, ingredient, result);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, StoneCuttingRecipeData data) {
/* 50 */     wrapper.writeString(data.group);
/* 51 */     Ingredient.write(wrapper, data.ingredient);
/* 52 */     wrapper.writeItemStack(data.result);
/*    */   }
/*    */   
/*    */   public String getGroup() {
/* 56 */     return this.group;
/*    */   }
/*    */   
/*    */   public Ingredient getIngredient() {
/* 60 */     return this.ingredient;
/*    */   }
/*    */   
/*    */   public ItemStack getResult() {
/* 64 */     return this.result;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\data\StoneCuttingRecipeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */