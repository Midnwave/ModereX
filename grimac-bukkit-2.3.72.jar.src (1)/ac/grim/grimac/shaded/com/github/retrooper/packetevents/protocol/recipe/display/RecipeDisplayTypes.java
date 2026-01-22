/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.display;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*    */ public final class RecipeDisplayTypes
/*    */ {
/* 26 */   private static final VersionedRegistry<RecipeDisplayType<?>> REGISTRY = new VersionedRegistry("recipe_display");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static <T extends RecipeDisplay<?>> RecipeDisplayType<T> register(String id, PacketWrapper.Reader<T> reader, PacketWrapper.Writer<T> writer) {
/* 33 */     return (RecipeDisplayType<T>)REGISTRY.define(id, data -> new StaticRecipeDisplayType<>(data, reader, writer));
/*    */   }
/*    */   
/*    */   public static VersionedRegistry<RecipeDisplayType<?>> getRegistry() {
/* 37 */     return REGISTRY;
/*    */   }
/*    */   
/* 40 */   public static final RecipeDisplayType<ShapelessCraftingRecipeDisplay> CRAFTING_SHAPELESS = register("crafting_shapeless", ShapelessCraftingRecipeDisplay::read, ShapelessCraftingRecipeDisplay::write);
/*    */   
/* 42 */   public static final RecipeDisplayType<ShapedCraftingRecipeDisplay> CRAFTING_SHAPED = register("crafting_shaped", ShapedCraftingRecipeDisplay::read, ShapedCraftingRecipeDisplay::write);
/*    */   
/* 44 */   public static final RecipeDisplayType<FurnaceRecipeDisplay> FURNACE = register("furnace", FurnaceRecipeDisplay::read, FurnaceRecipeDisplay::write);
/*    */   
/* 46 */   public static final RecipeDisplayType<StonecutterRecipeDisplay> STONECUTTER = register("stonecutter", StonecutterRecipeDisplay::read, StonecutterRecipeDisplay::write);
/*    */   
/* 48 */   public static final RecipeDisplayType<SmithingRecipeDisplay> SMITHING = register("smithing", SmithingRecipeDisplay::read, SmithingRecipeDisplay::write);
/*    */ 
/*    */   
/*    */   static {
/* 52 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\display\RecipeDisplayTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */