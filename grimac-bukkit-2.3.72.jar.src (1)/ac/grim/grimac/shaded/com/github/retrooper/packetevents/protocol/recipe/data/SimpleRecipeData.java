/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.data;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.CraftingCategory;
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
/*    */ public class SimpleRecipeData
/*    */   implements RecipeData
/*    */ {
/*    */   private final CraftingCategory category;
/*    */   
/*    */   public SimpleRecipeData(CraftingCategory category) {
/* 35 */     this.category = category;
/*    */   }
/*    */ 
/*    */   
/*    */   public static SimpleRecipeData read(PacketWrapper<?> wrapper) {
/* 40 */     CraftingCategory category = wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_19_3) ? (CraftingCategory)wrapper.readEnum((Enum[])CraftingCategory.values()) : CraftingCategory.MISC;
/* 41 */     return new SimpleRecipeData(category);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, SimpleRecipeData data) {
/* 45 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_19_3)) {
/* 46 */       wrapper.writeEnum((Enum)data.getCategory());
/*    */     }
/*    */   }
/*    */   
/*    */   public CraftingCategory getCategory() {
/* 51 */     return this.category;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\data\SimpleRecipeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */