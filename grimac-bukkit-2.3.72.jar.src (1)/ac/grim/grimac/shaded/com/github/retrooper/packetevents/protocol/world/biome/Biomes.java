/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.biome;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.serializer.SequentialNBTReader;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.MappingHelper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Optional;
/*     */ import java.util.OptionalInt;
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
/*     */ public final class Biomes
/*     */ {
/*  38 */   private static final VersionedRegistry<Biome> REGISTRY = new VersionedRegistry("worldgen/biome");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  44 */   private static final Map<ResourceLocation, NBTCompound> BIOME_DATA = new HashMap<>(); static { 
/*  45 */     try { SequentialNBTReader.Compound dataTag = MappingHelper.decompress("mappings/data/worldgen/biome"); 
/*  46 */       try { dataTag.skipOne();
/*  47 */         for (Map.Entry<String, NBT> entry : (Iterable<Map.Entry<String, NBT>>)dataTag.next().getValue()) {
/*  48 */           ResourceLocation biomeKey = new ResourceLocation(entry.getKey());
/*  49 */           BIOME_DATA.put(biomeKey, ((SequentialNBTReader.Compound)entry.getValue()).readFully());
/*     */         } 
/*  51 */         if (dataTag != null) dataTag.close();  } catch (Throwable throwable) { if (dataTag != null) try { dataTag.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (IOException exception)
/*  52 */     { throw new RuntimeException("Error while reading biome data", exception); }
/*     */      }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public static Biome define(String key) {
/*  61 */     return define(key, false);
/*     */   }
/*     */   
/*     */   @Internal
/*     */   public static Biome define(String key, boolean allowNoData) {
/*  66 */     return (Biome)REGISTRY.define(key, data -> {
/*     */           NBTCompound dataTag = BIOME_DATA.get(data.getName());
/*     */           if (dataTag != null) {
/*     */             return Biome.decode((NBT)dataTag, ClientVersion.getLatest(), data);
/*     */           }
/*     */           if (allowNoData) {
/*     */             BiomeEffects effects = new BiomeEffects(12638463, 4159204, 329011, 7907327, OptionalInt.empty(), OptionalInt.empty(), BiomeEffects.GrassColorModifier.NONE, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
/*     */             return new StaticBiome(data, true, 0.8F, Biome.TemperatureModifier.NONE, 0.4F, effects);
/*     */           } 
/*     */           throw new IllegalArgumentException("Can't define biome " + data.getName() + ", no data found");
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static VersionedRegistry<Biome> getRegistry() {
/*  84 */     return REGISTRY;
/*     */   }
/*     */   
/*     */   @Obsolete
/*  88 */   public static final Biome SNOWY_MOUNTAINS = define("snowy_mountains", true);
/*     */   @Obsolete
/*  90 */   public static final Biome GIANT_SPRUCE_TAIGA = define("giant_spruce_taiga", true);
/*     */   @Obsolete
/*  92 */   public static final Biome BADLANDS_PLATEAU = define("badlands_plateau", true);
/*     */   @Obsolete
/*  94 */   public static final Biome DESERT_HILLS = define("desert_hills", true);
/*     */   @Obsolete
/*  96 */   public static final Biome SNOWY_TAIGA_HILLS = define("snowy_taiga_hills", true);
/*     */   @Obsolete
/*  98 */   public static final Biome DARK_FOREST_HILLS = define("dark_forest_hills", true);
/*     */   @Obsolete
/* 100 */   public static final Biome MUSHROOM_FIELD_SHORE = define("mushroom_field_shore", true);
/*     */   @Obsolete
/* 102 */   public static final Biome TALL_BIRCH_FOREST = define("tall_birch_forest", true);
/*     */   @Obsolete
/* 104 */   public static final Biome SNOWY_TAIGA_MOUNTAINS = define("snowy_taiga_mountains", true);
/*     */   @Obsolete
/* 106 */   public static final Biome TAIGA_MOUNTAINS = define("taiga_mountains", true);
/*     */   @Obsolete
/* 108 */   public static final Biome BAMBOO_JUNGLE_HILLS = define("bamboo_jungle_hills", true);
/*     */   @Obsolete
/* 110 */   public static final Biome WOODED_MOUNTAINS = define("wooded_mountains", true);
/*     */   @Obsolete
/* 112 */   public static final Biome TAIGA_HILLS = define("taiga_hills", true);
/*     */   @Obsolete
/* 114 */   public static final Biome MODIFIED_GRAVELLY_MOUNTAINS = define("modified_gravelly_mountains", true);
/*     */   @Obsolete
/* 116 */   public static final Biome MODIFIED_WOODED_BADLANDS_PLATEAU = define("modified_wooded_badlands_plateau", true);
/*     */   @Obsolete
/* 118 */   public static final Biome DEEP_WARM_OCEAN = define("deep_warm_ocean", true);
/*     */   @Obsolete
/* 120 */   public static final Biome GIANT_TREE_TAIGA = define("giant_tree_taiga", true);
/*     */   @Obsolete
/* 122 */   public static final Biome MODIFIED_JUNGLE = define("modified_jungle", true);
/*     */   @Obsolete
/* 124 */   public static final Biome TALL_BIRCH_HILLS = define("tall_birch_hills", true);
/*     */   @Obsolete
/* 126 */   public static final Biome WOODED_BADLANDS_PLATEAU = define("wooded_badlands_plateau", true);
/*     */   @Obsolete
/* 128 */   public static final Biome SNOWY_TUNDRA = define("snowy_tundra", true);
/*     */   @Obsolete
/* 130 */   public static final Biome MOUNTAINS = define("mountains", true);
/*     */   @Obsolete
/* 132 */   public static final Biome WOODED_HILLS = define("wooded_hills", true);
/*     */   @Obsolete
/* 134 */   public static final Biome GRAVELLY_MOUNTAINS = define("gravelly_mountains", true);
/*     */   @Obsolete
/* 136 */   public static final Biome GIANT_SPRUCE_TAIGA_HILLS = define("giant_spruce_taiga_hills", true);
/*     */   @Obsolete
/* 138 */   public static final Biome MODIFIED_BADLANDS_PLATEAU = define("modified_badlands_plateau", true);
/*     */   @Obsolete
/* 140 */   public static final Biome JUNGLE_HILLS = define("jungle_hills", true);
/*     */   @Obsolete
/* 142 */   public static final Biome JUNGLE_EDGE = define("jungle_edge", true);
/*     */   @Obsolete
/* 144 */   public static final Biome MODIFIED_JUNGLE_EDGE = define("modified_jungle_edge", true);
/*     */   @Obsolete
/* 146 */   public static final Biome SWAMP_HILLS = define("swamp_hills", true);
/*     */   @Obsolete
/* 148 */   public static final Biome GIANT_TREE_TAIGA_HILLS = define("giant_tree_taiga_hills", true);
/*     */   @Obsolete
/* 150 */   public static final Biome SHATTERED_SAVANNA = define("shattered_savanna", true);
/*     */   @Obsolete
/* 152 */   public static final Biome MOUNTAIN_EDGE = define("mountain_edge", true);
/*     */   @Obsolete
/* 154 */   public static final Biome DESERT_LAKES = define("desert_lakes", true);
/*     */   @Obsolete
/* 156 */   public static final Biome BIRCH_FOREST_HILLS = define("birch_forest_hills", true);
/*     */   @Obsolete
/* 158 */   public static final Biome SHATTERED_SAVANNA_PLATEAU = define("shattered_savanna_plateau", true);
/*     */   @Obsolete
/* 160 */   public static final Biome STONE_SHORE = define("stone_shore", true);
/*     */   @Obsolete
/* 162 */   public static final Biome NETHER = define("nether", true);
/*     */   
/* 164 */   public static final Biome BADLANDS = define("badlands");
/* 165 */   public static final Biome BAMBOO_JUNGLE = define("bamboo_jungle");
/* 166 */   public static final Biome BASALT_DELTAS = define("basalt_deltas");
/* 167 */   public static final Biome BEACH = define("beach");
/* 168 */   public static final Biome BIRCH_FOREST = define("birch_forest");
/* 169 */   public static final Biome CHERRY_GROVE = define("cherry_grove");
/* 170 */   public static final Biome COLD_OCEAN = define("cold_ocean");
/* 171 */   public static final Biome CRIMSON_FOREST = define("crimson_forest");
/* 172 */   public static final Biome DARK_FOREST = define("dark_forest");
/* 173 */   public static final Biome DEEP_COLD_OCEAN = define("deep_cold_ocean");
/* 174 */   public static final Biome DEEP_DARK = define("deep_dark");
/* 175 */   public static final Biome DEEP_FROZEN_OCEAN = define("deep_frozen_ocean");
/* 176 */   public static final Biome DEEP_LUKEWARM_OCEAN = define("deep_lukewarm_ocean");
/* 177 */   public static final Biome DEEP_OCEAN = define("deep_ocean");
/* 178 */   public static final Biome DESERT = define("desert");
/* 179 */   public static final Biome DRIPSTONE_CAVES = define("dripstone_caves");
/* 180 */   public static final Biome END_BARRENS = define("end_barrens");
/* 181 */   public static final Biome END_HIGHLANDS = define("end_highlands");
/* 182 */   public static final Biome END_MIDLANDS = define("end_midlands");
/* 183 */   public static final Biome ERODED_BADLANDS = define("eroded_badlands");
/* 184 */   public static final Biome FLOWER_FOREST = define("flower_forest");
/* 185 */   public static final Biome FOREST = define("forest");
/* 186 */   public static final Biome FROZEN_OCEAN = define("frozen_ocean");
/* 187 */   public static final Biome FROZEN_PEAKS = define("frozen_peaks");
/* 188 */   public static final Biome FROZEN_RIVER = define("frozen_river");
/* 189 */   public static final Biome GROVE = define("grove");
/* 190 */   public static final Biome ICE_SPIKES = define("ice_spikes");
/* 191 */   public static final Biome JAGGED_PEAKS = define("jagged_peaks");
/* 192 */   public static final Biome JUNGLE = define("jungle");
/* 193 */   public static final Biome LUKEWARM_OCEAN = define("lukewarm_ocean");
/* 194 */   public static final Biome LUSH_CAVES = define("lush_caves");
/* 195 */   public static final Biome MANGROVE_SWAMP = define("mangrove_swamp");
/* 196 */   public static final Biome MEADOW = define("meadow");
/* 197 */   public static final Biome MUSHROOM_FIELDS = define("mushroom_fields");
/* 198 */   public static final Biome NETHER_WASTES = define("nether_wastes");
/* 199 */   public static final Biome OCEAN = define("ocean");
/* 200 */   public static final Biome OLD_GROWTH_BIRCH_FOREST = define("old_growth_birch_forest");
/* 201 */   public static final Biome OLD_GROWTH_PINE_TAIGA = define("old_growth_pine_taiga");
/* 202 */   public static final Biome OLD_GROWTH_SPRUCE_TAIGA = define("old_growth_spruce_taiga");
/* 203 */   public static final Biome PLAINS = define("plains");
/* 204 */   public static final Biome RIVER = define("river");
/* 205 */   public static final Biome SAVANNA = define("savanna");
/* 206 */   public static final Biome SAVANNA_PLATEAU = define("savanna_plateau");
/* 207 */   public static final Biome SMALL_END_ISLANDS = define("small_end_islands");
/* 208 */   public static final Biome SNOWY_BEACH = define("snowy_beach");
/* 209 */   public static final Biome SNOWY_PLAINS = define("snowy_plains");
/* 210 */   public static final Biome SNOWY_SLOPES = define("snowy_slopes");
/* 211 */   public static final Biome SNOWY_TAIGA = define("snowy_taiga");
/* 212 */   public static final Biome SOUL_SAND_VALLEY = define("soul_sand_valley");
/* 213 */   public static final Biome SPARSE_JUNGLE = define("sparse_jungle");
/* 214 */   public static final Biome STONY_PEAKS = define("stony_peaks");
/* 215 */   public static final Biome STONY_SHORE = define("stony_shore");
/* 216 */   public static final Biome SUNFLOWER_PLAINS = define("sunflower_plains");
/* 217 */   public static final Biome SWAMP = define("swamp");
/* 218 */   public static final Biome TAIGA = define("taiga");
/* 219 */   public static final Biome THE_END = define("the_end");
/* 220 */   public static final Biome THE_VOID = define("the_void");
/* 221 */   public static final Biome WARM_OCEAN = define("warm_ocean");
/* 222 */   public static final Biome WARPED_FOREST = define("warped_forest");
/* 223 */   public static final Biome WINDSWEPT_FOREST = define("windswept_forest");
/* 224 */   public static final Biome WINDSWEPT_GRAVELLY_HILLS = define("windswept_gravelly_hills");
/* 225 */   public static final Biome WINDSWEPT_HILLS = define("windswept_hills");
/* 226 */   public static final Biome WINDSWEPT_SAVANNA = define("windswept_savanna");
/* 227 */   public static final Biome WOODED_BADLANDS = define("wooded_badlands");
/*     */ 
/*     */   
/* 230 */   public static final Biome PALE_GARDEN = define("pale_garden");
/*     */   
/*     */   static {
/* 233 */     BIOME_DATA.clear();
/* 234 */     REGISTRY.unloadMappings();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\biome\Biomes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */