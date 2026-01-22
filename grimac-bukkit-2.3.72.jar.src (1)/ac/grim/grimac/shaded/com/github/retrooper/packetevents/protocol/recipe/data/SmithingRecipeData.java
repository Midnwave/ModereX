/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.data;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
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
/*    */ 
/*    */ @Obsolete
/*    */ public class SmithingRecipeData
/*    */   implements RecipeData
/*    */ {
/*    */   private final Ingredient template;
/*    */   private final Ingredient base;
/*    */   private final Ingredient addition;
/*    */   private final ItemStack result;
/*    */   
/*    */   @Deprecated
/*    */   public SmithingRecipeData(Ingredient base, Ingredient addition, ItemStack result) {
/* 41 */     this(null, base, addition, result);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SmithingRecipeData(Ingredient template, Ingredient base, Ingredient addition, ItemStack result) {
/* 48 */     this.template = template;
/* 49 */     this.base = base;
/* 50 */     this.addition = addition;
/* 51 */     this.result = result;
/*    */   }
/*    */   
/*    */   public static SmithingRecipeData read(PacketWrapper<?> wrapper) {
/* 55 */     return read(wrapper, false);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static SmithingRecipeData read(PacketWrapper<?> wrapper, boolean legacy) {
/* 61 */     Ingredient template = (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_20) || (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_19_4) && !legacy)) ? Ingredient.read(wrapper) : null;
/* 62 */     Ingredient base = Ingredient.read(wrapper);
/* 63 */     Ingredient addition = Ingredient.read(wrapper);
/* 64 */     ItemStack result = wrapper.readItemStack();
/* 65 */     return new SmithingRecipeData(template, base, addition, result);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, SmithingRecipeData data) {
/* 69 */     write(wrapper, data, false);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, SmithingRecipeData data, boolean legacy) {
/* 73 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_20) || (wrapper
/* 74 */       .getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_19_4) && !legacy)) {
/* 75 */       Ingredient.write(wrapper, data.template);
/*    */     }
/* 77 */     Ingredient.write(wrapper, data.base);
/* 78 */     Ingredient.write(wrapper, data.addition);
/* 79 */     wrapper.writeItemStack(data.result);
/*    */   }
/*    */   
/*    */   public Ingredient getTemplate() {
/* 83 */     return this.template;
/*    */   }
/*    */   
/*    */   public Ingredient getBase() {
/* 87 */     return this.base;
/*    */   }
/*    */   
/*    */   public Ingredient getAddition() {
/* 91 */     return this.addition;
/*    */   }
/*    */   
/*    */   public ItemStack getResult() {
/* 95 */     return this.result;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\data\SmithingRecipeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */