/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.data;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.CraftingCategory;
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
/*    */ public class ShapelessRecipeData
/*    */   implements RecipeData
/*    */ {
/*    */   private final String group;
/*    */   private final CraftingCategory category;
/*    */   private final Ingredient[] ingredients;
/*    */   private final ItemStack result;
/*    */   
/*    */   @Deprecated
/*    */   public ShapelessRecipeData(String group, Ingredient[] ingredients, ItemStack result) {
/* 41 */     this(group, CraftingCategory.MISC, ingredients, result);
/*    */   }
/*    */   
/*    */   public ShapelessRecipeData(String group, CraftingCategory category, Ingredient[] ingredients, ItemStack result) {
/* 45 */     this.group = group;
/* 46 */     this.category = category;
/* 47 */     this.ingredients = ingredients;
/* 48 */     this.result = result;
/*    */   }
/*    */   
/*    */   public static ShapelessRecipeData read(PacketWrapper<?> wrapper) {
/* 52 */     String group = wrapper.readString();
/*    */     
/* 54 */     CraftingCategory category = wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_19_3) ? (CraftingCategory)wrapper.readEnum((Enum[])CraftingCategory.values()) : CraftingCategory.MISC;
/* 55 */     Ingredient[] ingredients = (Ingredient[])wrapper.readArray(Ingredient::read, Ingredient.class);
/* 56 */     ItemStack result = wrapper.readItemStack();
/* 57 */     return new ShapelessRecipeData(group, category, ingredients, result);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ShapelessRecipeData data) {
/* 61 */     wrapper.writeString(data.group);
/* 62 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_19_3)) {
/* 63 */       wrapper.writeEnum((Enum)data.category);
/*    */     }
/* 65 */     wrapper.writeArray((Object[])data.ingredients, Ingredient::write);
/* 66 */     wrapper.writeItemStack(data.result);
/*    */   }
/*    */   
/*    */   public String getGroup() {
/* 70 */     return this.group;
/*    */   }
/*    */   
/*    */   public CraftingCategory getCategory() {
/* 74 */     return this.category;
/*    */   }
/*    */   
/*    */   public Ingredient[] getIngredients() {
/* 78 */     return this.ingredients;
/*    */   }
/*    */   
/*    */   public ItemStack getResult() {
/* 82 */     return this.result;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\data\ShapelessRecipeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */