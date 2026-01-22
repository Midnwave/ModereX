/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.wolfvariant;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntitySet;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.biome.Biome;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.biome.Biomes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import java.util.Collections;
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
/*    */ public final class WolfVariants
/*    */ {
/* 32 */   private static final VersionedRegistry<WolfVariant> REGISTRY = new VersionedRegistry("wolf_variant");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static WolfVariant define(String name, MappedEntitySet<Biome> biomes) {
/* 39 */     return define(name, "wolf_" + name, biomes);
/*    */   }
/*    */   
/*    */   @Internal
/*    */   public static WolfVariant define(String name, String assetId, MappedEntitySet<Biome> biomes) {
/* 44 */     return define(name, ResourceLocation.minecraft("entity/wolf/" + assetId), 
/* 45 */         ResourceLocation.minecraft("entity/wolf/" + assetId + "_tame"), 
/* 46 */         ResourceLocation.minecraft("entity/wolf/" + assetId + "_angry"), biomes);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static WolfVariant define(String name, ResourceLocation wildTexture, ResourceLocation tameTexture, ResourceLocation angryTexture, MappedEntitySet<Biome> biomes) {
/* 57 */     return (WolfVariant)REGISTRY.define(name, data -> new StaticWolfVariant(data, wildTexture, tameTexture, angryTexture, biomes));
/*    */   }
/*    */ 
/*    */   
/*    */   public static VersionedRegistry<WolfVariant> getRegistry() {
/* 62 */     return REGISTRY;
/*    */   }
/*    */   
/* 65 */   public static final WolfVariant PALE = define("pale", "wolf", new MappedEntitySet(
/* 66 */         Collections.singletonList(Biomes.TAIGA)));
/* 67 */   public static final WolfVariant SPOTTED = define("spotted", new MappedEntitySet(
/* 68 */         ResourceLocation.minecraft("is_savanna")));
/* 69 */   public static final WolfVariant SNOWY = define("snowy", new MappedEntitySet(
/* 70 */         Collections.singletonList(Biomes.GROVE)));
/* 71 */   public static final WolfVariant BLACK = define("black", new MappedEntitySet(
/* 72 */         Collections.singletonList(Biomes.OLD_GROWTH_PINE_TAIGA)));
/* 73 */   public static final WolfVariant ASHEN = define("ashen", new MappedEntitySet(
/* 74 */         Collections.singletonList(Biomes.SNOWY_TAIGA)));
/* 75 */   public static final WolfVariant RUSTY = define("rusty", new MappedEntitySet(
/* 76 */         ResourceLocation.minecraft("is_jungle")));
/* 77 */   public static final WolfVariant WOODS = define("woods", new MappedEntitySet(
/* 78 */         Collections.singletonList(Biomes.FOREST)));
/* 79 */   public static final WolfVariant CHESTNUT = define("chestnut", new MappedEntitySet(
/* 80 */         Collections.singletonList(Biomes.OLD_GROWTH_SPRUCE_TAIGA)));
/* 81 */   public static final WolfVariant STRIPED = define("striped", new MappedEntitySet(
/* 82 */         ResourceLocation.minecraft("is_badlands")));
/*    */   
/*    */   static {
/* 85 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\wolfvariant\WolfVariants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */