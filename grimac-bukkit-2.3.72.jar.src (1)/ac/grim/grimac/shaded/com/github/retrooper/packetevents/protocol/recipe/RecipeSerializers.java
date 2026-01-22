/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.data.CookedRecipeData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.data.RecipeData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.data.ShapedRecipeData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.data.ShapelessRecipeData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.data.SimpleRecipeData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.data.SmithingRecipeData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.data.SmithingTrimRecipeData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.data.StoneCuttingRecipeData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.Collection;
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
/*     */ public final class RecipeSerializers
/*     */ {
/*  43 */   private static final VersionedRegistry<RecipeSerializer<?>> REGISTRY = new VersionedRegistry("legacy_recipe_serializer");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static VersionedRegistry<RecipeSerializer<?>> getRegistry() {
/*  49 */     return REGISTRY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public static <T extends RecipeData> RecipeSerializer<T> define(String name, PacketWrapper.Reader<T> reader, PacketWrapper.Writer<T> writer) {
/*  58 */     return define(name, reader, writer, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public static <T extends RecipeData> RecipeSerializer<T> define(String name, PacketWrapper.Reader<T> reader, PacketWrapper.Writer<T> writer, @Nullable RecipeType legacyType) {
/*  68 */     return (RecipeSerializer<T>)REGISTRY.define(name, data -> new StaticRecipeSerializer<>(data, reader, writer, legacyType));
/*     */   }
/*     */ 
/*     */   
/*     */   public static RecipeSerializer<?> getByName(String name) {
/*  73 */     return (RecipeSerializer)REGISTRY.getByName(name);
/*     */   }
/*     */   
/*     */   public static RecipeSerializer<?> getById(ClientVersion version, int id) {
/*  77 */     return (RecipeSerializer)REGISTRY.getById(version, id);
/*     */   }
/*     */   
/*  80 */   public static final RecipeSerializer<ShapedRecipeData> CRAFTING_SHAPED = define("crafting_shaped", ShapedRecipeData::read, ShapedRecipeData::write, RecipeType.CRAFTING_SHAPED);
/*     */   
/*  82 */   public static final RecipeSerializer<ShapelessRecipeData> CRAFTING_SHAPELESS = define("crafting_shapeless", ShapelessRecipeData::read, ShapelessRecipeData::write, RecipeType.CRAFTING_SHAPELESS);
/*     */   
/*  84 */   public static final RecipeSerializer<SimpleRecipeData> CRAFTING_SPECIAL_ARMORDYE = define("crafting_special_armordye", SimpleRecipeData::read, SimpleRecipeData::write, RecipeType.CRAFTING_SPECIAL_ARMORDYE);
/*     */   
/*  86 */   public static final RecipeSerializer<SimpleRecipeData> CRAFTING_SPECIAL_BOOKCLONING = define("crafting_special_bookcloning", SimpleRecipeData::read, SimpleRecipeData::write, RecipeType.CRAFTING_SPECIAL_BOOKCLONING);
/*     */   
/*  88 */   public static final RecipeSerializer<SimpleRecipeData> CRAFTING_SPECIAL_MAPCLONING = define("crafting_special_mapcloning", SimpleRecipeData::read, SimpleRecipeData::write, RecipeType.CRAFTING_SPECIAL_MAPCLONING);
/*     */   
/*  90 */   public static final RecipeSerializer<SimpleRecipeData> CRAFTING_SPECIAL_MAPEXTENDING = define("crafting_special_mapextending", SimpleRecipeData::read, SimpleRecipeData::write, RecipeType.CRAFTING_SPECIAL_MAPEXTENDING);
/*     */   
/*  92 */   public static final RecipeSerializer<SimpleRecipeData> CRAFTING_SPECIAL_FIREWORK_ROCKET = define("crafting_special_firework_rocket", SimpleRecipeData::read, SimpleRecipeData::write, RecipeType.CRAFTING_SPECIAL_FIREWORK_ROCKET);
/*     */   
/*  94 */   public static final RecipeSerializer<SimpleRecipeData> CRAFTING_SPECIAL_FIREWORK_STAR = define("crafting_special_firework_star", SimpleRecipeData::read, SimpleRecipeData::write, RecipeType.CRAFTING_SPECIAL_FIREWORK_STAR);
/*     */   
/*  96 */   public static final RecipeSerializer<SimpleRecipeData> CRAFTING_SPECIAL_FIREWORK_STAR_FADE = define("crafting_special_firework_star_fade", SimpleRecipeData::read, SimpleRecipeData::write, RecipeType.CRAFTING_SPECIAL_FIREWORK_STAR_FADE);
/*     */   
/*  98 */   public static final RecipeSerializer<SimpleRecipeData> CRAFTING_SPECIAL_TIPPEDARROW = define("crafting_special_tippedarrow", SimpleRecipeData::read, SimpleRecipeData::write, RecipeType.CRAFTING_SPECIAL_TIPPEDARROW);
/*     */   
/* 100 */   public static final RecipeSerializer<SimpleRecipeData> CRAFTING_SPECIAL_BANNERDUPLICATE = define("crafting_special_bannerduplicate", SimpleRecipeData::read, SimpleRecipeData::write, RecipeType.CRAFTING_SPECIAL_BANNERDUPLICATE);
/*     */   
/* 102 */   public static final RecipeSerializer<SimpleRecipeData> CRAFTING_SPECIAL_SHIELDDECORATION = define("crafting_special_shielddecoration", SimpleRecipeData::read, SimpleRecipeData::write, RecipeType.CRAFTING_SPECIAL_SHIELDDECORATION);
/*     */   
/* 104 */   public static final RecipeSerializer<SimpleRecipeData> CRAFTING_SPECIAL_SHULKERBOXCOLORING = define("crafting_special_shulkerboxcoloring", SimpleRecipeData::read, SimpleRecipeData::write, RecipeType.CRAFTING_SPECIAL_SHULKERBOXCOLORING);
/*     */   
/* 106 */   public static final RecipeSerializer<SimpleRecipeData> CRAFTING_SPECIAL_SUSPICIOUSSTEW = define("crafting_special_suspiciousstew", SimpleRecipeData::read, SimpleRecipeData::write, RecipeType.CRAFTING_SPECIAL_SUSPICIOUSSTEW);
/*     */   
/* 108 */   public static final RecipeSerializer<SimpleRecipeData> CRAFTING_SPECIAL_REPAIRITEM = define("crafting_special_repairitem", SimpleRecipeData::read, SimpleRecipeData::write, RecipeType.CRAFTING_SPECIAL_REPAIRITEM);
/*     */   
/* 110 */   public static final RecipeSerializer<CookedRecipeData> SMELTING = define("smelting", CookedRecipeData::read, CookedRecipeData::write, RecipeType.SMELTING);
/*     */   
/* 112 */   public static final RecipeSerializer<CookedRecipeData> BLASTING = define("blasting", CookedRecipeData::read, CookedRecipeData::write, RecipeType.BLASTING);
/*     */   
/* 114 */   public static final RecipeSerializer<CookedRecipeData> SMOKING = define("smoking", CookedRecipeData::read, CookedRecipeData::write, RecipeType.SMOKING);
/*     */   
/* 116 */   public static final RecipeSerializer<CookedRecipeData> CAMPFIRE_COOKING = define("campfire_cooking", CookedRecipeData::read, CookedRecipeData::write, RecipeType.CAMPFIRE_COOKING);
/*     */   
/* 118 */   public static final RecipeSerializer<StoneCuttingRecipeData> STONECUTTING = define("stonecutting", StoneCuttingRecipeData::read, StoneCuttingRecipeData::write, RecipeType.STONECUTTING);
/*     */   
/*     */   static {
/* 121 */     SMITHING = define("smithing", ew -> SmithingRecipeData.read(ew, true), (ew, data) -> SmithingRecipeData.write(ew, data, true), RecipeType.SMITHING);
/*     */   }
/*     */ 
/*     */   
/* 125 */   public static final RecipeSerializer<SmithingRecipeData> SMITHING_TRANSFORM = define("smithing_transform", SmithingRecipeData::read, SmithingRecipeData::write, RecipeType.SMITHING); @Obsolete
/*     */   public static final RecipeSerializer<SmithingRecipeData> SMITHING;
/* 127 */   public static final RecipeSerializer<SmithingTrimRecipeData> SMITHING_TRIM = define("smithing_trim", SmithingTrimRecipeData::read, SmithingTrimRecipeData::write);
/*     */   
/* 129 */   public static final RecipeSerializer<SimpleRecipeData> CRAFTING_DECORATED_POT = define("crafting_decorated_pot", SimpleRecipeData::read, SimpleRecipeData::write);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Collection<RecipeSerializer<?>> values() {
/* 138 */     return REGISTRY.getEntries();
/*     */   }
/*     */   
/*     */   static {
/* 142 */     REGISTRY.unloadMappings();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\RecipeSerializers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */