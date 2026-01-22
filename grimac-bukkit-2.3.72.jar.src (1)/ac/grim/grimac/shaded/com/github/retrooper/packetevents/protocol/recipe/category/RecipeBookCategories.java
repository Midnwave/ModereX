/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.category;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*    */ import java.util.function.Function;
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
/*    */ public final class RecipeBookCategories
/*    */ {
/* 25 */   private static final VersionedRegistry<RecipeBookCategory> REGISTRY = new VersionedRegistry("recipe_book_category");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static RecipeBookCategory register(String id) {
/* 31 */     return (RecipeBookCategory)REGISTRY.define(id, StaticRecipeBookCategory::new);
/*    */   }
/*    */   
/*    */   public static VersionedRegistry<RecipeBookCategory> getRegistry() {
/* 35 */     return REGISTRY;
/*    */   }
/*    */   
/* 38 */   public static final RecipeBookCategory CRAFTING_BUILDING_BLOCKS = register("crafting_building_blocks");
/* 39 */   public static final RecipeBookCategory CRAFTING_REDSTONE = register("crafting_redstone");
/* 40 */   public static final RecipeBookCategory CRAFTING_EQUIPMENT = register("crafting_equipment");
/* 41 */   public static final RecipeBookCategory CRAFTING_MISC = register("crafting_misc");
/* 42 */   public static final RecipeBookCategory FURNACE_FOOD = register("furnace_food");
/* 43 */   public static final RecipeBookCategory FURNACE_BLOCKS = register("furnace_blocks");
/* 44 */   public static final RecipeBookCategory FURNACE_MISC = register("furnace_misc");
/* 45 */   public static final RecipeBookCategory BLAST_FURNACE_BLOCKS = register("blast_furnace_blocks");
/* 46 */   public static final RecipeBookCategory BLAST_FURNACE_MISC = register("blast_furnace_misc");
/* 47 */   public static final RecipeBookCategory SMOKER_FOOD = register("smoker_food");
/* 48 */   public static final RecipeBookCategory STONECUTTER = register("stonecutter");
/* 49 */   public static final RecipeBookCategory SMITHING = register("smithing");
/* 50 */   public static final RecipeBookCategory CAMPFIRE = register("campfire");
/*    */   
/*    */   static {
/* 53 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\category\RecipeBookCategories.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */