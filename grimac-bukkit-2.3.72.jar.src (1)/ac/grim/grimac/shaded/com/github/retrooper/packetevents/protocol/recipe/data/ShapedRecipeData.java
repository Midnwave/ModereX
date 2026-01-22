/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.data;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.CraftingCategory;
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
/*     */ 
/*     */ 
/*     */ @Obsolete
/*     */ public class ShapedRecipeData
/*     */   implements RecipeData
/*     */ {
/*     */   private final String group;
/*     */   private final CraftingCategory category;
/*     */   private final ItemStack result;
/*     */   private final boolean showNotification;
/*     */   private final int width;
/*     */   private final int height;
/*     */   private final Ingredient[] ingredients;
/*     */   
/*     */   @Deprecated
/*     */   public ShapedRecipeData(int width, int height, String group, Ingredient[] ingredients, ItemStack result) {
/*  46 */     this(group, CraftingCategory.MISC, result, true, width, height, ingredients);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ShapedRecipeData(String group, CraftingCategory category, ItemStack result, boolean showNotification, int width, int height, Ingredient[] ingredients) {
/*  53 */     if (width * height != ingredients.length) {
/*  54 */       throw new IllegalArgumentException("Illegal ingredients length, found " + ingredients.length + " but expected " + width + " * " + height);
/*     */     }
/*     */ 
/*     */     
/*  58 */     this.group = group;
/*  59 */     this.category = category;
/*  60 */     this.result = result;
/*  61 */     this.showNotification = showNotification;
/*  62 */     this.width = width;
/*  63 */     this.height = height;
/*  64 */     this.ingredients = ingredients;
/*     */   }
/*     */   
/*     */   public static ShapedRecipeData read(PacketWrapper<?> wrapper) {
/*  68 */     int width = 0, height = 0;
/*  69 */     if (wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_20_3)) {
/*  70 */       width = wrapper.readVarInt();
/*  71 */       height = wrapper.readVarInt();
/*     */     } 
/*  73 */     String group = wrapper.readString();
/*     */     
/*  75 */     CraftingCategory category = wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_19_3) ? (CraftingCategory)wrapper.readEnum((Enum[])CraftingCategory.values()) : CraftingCategory.MISC;
/*  76 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_20_3)) {
/*  77 */       width = wrapper.readVarInt();
/*  78 */       height = wrapper.readVarInt();
/*     */     } 
/*  80 */     Ingredient[] ingredients = new Ingredient[width * height];
/*  81 */     for (int i = 0; i < ingredients.length; i++) {
/*  82 */       ingredients[i] = Ingredient.read(wrapper);
/*     */     }
/*  84 */     ItemStack result = wrapper.readItemStack();
/*  85 */     boolean showNotification = true;
/*  86 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_19_4)) {
/*  87 */       showNotification = wrapper.readBoolean();
/*     */     }
/*  89 */     return new ShapedRecipeData(group, category, result, showNotification, width, height, ingredients);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, ShapedRecipeData data) {
/*  93 */     if (wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_20_3)) {
/*  94 */       wrapper.writeVarInt(data.width);
/*  95 */       wrapper.writeVarInt(data.height);
/*     */     } 
/*  97 */     wrapper.writeString(data.group);
/*  98 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_19_3)) {
/*  99 */       wrapper.writeEnum((Enum)data.category);
/*     */     }
/* 101 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_20_3)) {
/* 102 */       wrapper.writeVarInt(data.width);
/* 103 */       wrapper.writeVarInt(data.height);
/*     */     } 
/* 105 */     for (Ingredient ingredient : data.ingredients) {
/* 106 */       Ingredient.write(wrapper, ingredient);
/*     */     }
/* 108 */     wrapper.writeItemStack(data.result);
/* 109 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_19_4)) {
/* 110 */       wrapper.writeBoolean(data.showNotification);
/*     */     }
/*     */   }
/*     */   
/*     */   public String getGroup() {
/* 115 */     return this.group;
/*     */   }
/*     */   
/*     */   public CraftingCategory getCategory() {
/* 119 */     return this.category;
/*     */   }
/*     */   
/*     */   public ItemStack getResult() {
/* 123 */     return this.result;
/*     */   }
/*     */   
/*     */   public boolean isShowNotification() {
/* 127 */     return this.showNotification;
/*     */   }
/*     */   
/*     */   public int getWidth() {
/* 131 */     return this.width;
/*     */   }
/*     */   
/*     */   public int getHeight() {
/* 135 */     return this.height;
/*     */   }
/*     */   
/*     */   public Ingredient[] getIngredients() {
/* 139 */     return this.ingredients;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\data\ShapedRecipeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */