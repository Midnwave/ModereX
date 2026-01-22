/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.data;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.CookingCategory;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.Ingredient;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
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
/*     */ @Obsolete
/*     */ public class CookedRecipeData
/*     */   implements RecipeData
/*     */ {
/*     */   private final String group;
/*     */   private final CookingCategory category;
/*     */   private final Ingredient ingredient;
/*     */   private final ItemStack result;
/*     */   private final float experience;
/*     */   private final int cookingTime;
/*     */   
/*     */   @Deprecated
/*     */   public CookedRecipeData(String group, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
/*  43 */     this(group, CookingCategory.MISC, ingredient, result, experience, cookingTime);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CookedRecipeData(String group, CookingCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
/*  50 */     this.group = group;
/*  51 */     this.category = category;
/*  52 */     this.ingredient = ingredient;
/*  53 */     this.result = result;
/*  54 */     this.experience = experience;
/*  55 */     this.cookingTime = cookingTime;
/*     */   }
/*     */   
/*     */   public static CookedRecipeData read(PacketWrapper<?> wrapper) {
/*  59 */     String group = wrapper.readString();
/*     */     
/*  61 */     CookingCategory category = wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_19_3) ? (CookingCategory)wrapper.readEnum((Enum[])CookingCategory.values()) : CookingCategory.MISC;
/*  62 */     Ingredient ingredient = Ingredient.read(wrapper);
/*  63 */     ItemStack result = wrapper.readItemStack();
/*  64 */     float experience = wrapper.readFloat();
/*  65 */     int cookingTime = wrapper.readVarInt();
/*  66 */     return new CookedRecipeData(group, category, ingredient, result, experience, cookingTime);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, CookedRecipeData data) {
/*  70 */     wrapper.writeString(data.group);
/*  71 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_19_3)) {
/*  72 */       wrapper.writeEnum((Enum)data.category);
/*     */     }
/*  74 */     Ingredient.write(wrapper, data.ingredient);
/*  75 */     wrapper.writeItemStack(data.result);
/*  76 */     wrapper.writeFloat(data.experience);
/*  77 */     wrapper.writeVarInt(data.cookingTime);
/*     */   }
/*     */   
/*     */   public String getGroup() {
/*  81 */     return this.group;
/*     */   }
/*     */   
/*     */   public CookingCategory getCategory() {
/*  85 */     return this.category;
/*     */   }
/*     */   
/*     */   public Ingredient getIngredient() {
/*  89 */     return this.ingredient;
/*     */   }
/*     */   
/*     */   public ItemStack getResult() {
/*  93 */     return this.result;
/*     */   }
/*     */   
/*     */   public float getExperience() {
/*  97 */     return this.experience;
/*     */   }
/*     */   
/*     */   public int getCookingTime() {
/* 101 */     return this.cookingTime;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\data\CookedRecipeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */