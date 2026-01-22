/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.mapdecoration;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
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
/*     */ public final class MapDecorationTypes
/*     */ {
/*  33 */   private static final VersionedRegistry<MapDecorationType> REGISTRY = new VersionedRegistry("map_decoration_type");
/*     */   
/*     */   private static final int LIGHT_GRAY_COLOR = 10066329;
/*     */   private static final int COPPER_COLOR = 12741452;
/*     */   
/*     */   @Internal
/*     */   public static MapDecorationType define(String key, boolean showOnItemFrame, boolean trackCount) {
/*  40 */     return define(key, ResourceLocation.minecraft(key), showOnItemFrame, trackCount);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public static MapDecorationType define(String key, ResourceLocation assetId, boolean showOnItemFrame, boolean trackCount) {
/*  48 */     return define(key, assetId, showOnItemFrame, -1, false, trackCount);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public static MapDecorationType define(String key, boolean showOnItemFrame, int mapColor, boolean explorationMapElement, boolean trackCount) {
/*  58 */     return define(key, ResourceLocation.minecraft(key), showOnItemFrame, mapColor, explorationMapElement, trackCount);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public static MapDecorationType define(String key, ResourceLocation assetId, boolean showOnItemFrame, int mapColor, boolean explorationMapElement, boolean trackCount) {
/*  68 */     return (MapDecorationType)REGISTRY.define(key, data -> new StaticMapDecorationType(data, assetId, showOnItemFrame, mapColor, explorationMapElement, trackCount));
/*     */   }
/*     */ 
/*     */   
/*     */   public static VersionedRegistry<MapDecorationType> getRegistry() {
/*  73 */     return REGISTRY;
/*     */   }
/*     */   @Nullable
/*     */   public static MapDecorationType getByName(String name) {
/*  77 */     return (MapDecorationType)REGISTRY.getByName(name);
/*     */   }
/*     */   @Nullable
/*     */   public static MapDecorationType getById(int id, ClientVersion version) {
/*  81 */     return (MapDecorationType)REGISTRY.getById(version, id);
/*     */   }
/*     */   @Nullable
/*     */   public static MapDecorationType getById(ClientVersion version, int id) {
/*  85 */     return (MapDecorationType)REGISTRY.getById(version, id);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  92 */   public static final MapDecorationType PLAYER = define("player", false, true);
/*     */   
/*  94 */   public static final MapDecorationType FRAME = define("frame", true, true);
/*     */   
/*  96 */   public static final MapDecorationType RED_MARKER = define("red_marker", false, true);
/*     */   
/*  98 */   public static final MapDecorationType BLUE_MARKER = define("blue_marker", false, true);
/*     */   
/* 100 */   public static final MapDecorationType TARGET_X = define("target_x", true, false);
/*     */   
/* 102 */   public static final MapDecorationType TARGET_POINT = define("target_point", true, false);
/*     */   
/* 104 */   public static final MapDecorationType PLAYER_OFF_MAP = define("player_off_map", false, true);
/*     */   
/* 106 */   public static final MapDecorationType PLAYER_OFF_LIMITS = define("player_off_limits", false, true);
/*     */   
/* 108 */   public static final MapDecorationType MANSION = define("mansion", true, 5393476, true, false);
/*     */   
/* 110 */   public static final MapDecorationType MONUMENT = define("monument", true, 3830373, true, false);
/*     */   
/* 112 */   public static final MapDecorationType BANNER_WHITE = define("banner_white", 
/* 113 */       ResourceLocation.minecraft("white_banner"), true, true);
/* 114 */   public static final MapDecorationType BANNER_ORANGE = define("banner_orange", 
/* 115 */       ResourceLocation.minecraft("orange_banner"), true, true);
/* 116 */   public static final MapDecorationType BANNER_MAGENTA = define("banner_magenta", 
/* 117 */       ResourceLocation.minecraft("magenta_banner"), true, true);
/* 118 */   public static final MapDecorationType BANNER_LIGHT_BLUE = define("banner_light_blue", 
/* 119 */       ResourceLocation.minecraft("light_blue_banner"), true, true);
/* 120 */   public static final MapDecorationType BANNER_YELLOW = define("banner_yellow", 
/* 121 */       ResourceLocation.minecraft("yellow_banner"), true, true);
/* 122 */   public static final MapDecorationType BANNER_LIME = define("banner_lime", 
/* 123 */       ResourceLocation.minecraft("lime_banner"), true, true);
/* 124 */   public static final MapDecorationType BANNER_PINK = define("banner_pink", 
/* 125 */       ResourceLocation.minecraft("pink_banner"), true, true);
/* 126 */   public static final MapDecorationType BANNER_GRAY = define("banner_gray", 
/* 127 */       ResourceLocation.minecraft("gray_banner"), true, true);
/* 128 */   public static final MapDecorationType BANNER_LIGHT_GRAY = define("banner_light_gray", 
/* 129 */       ResourceLocation.minecraft("light_gray_banner"), true, true);
/* 130 */   public static final MapDecorationType BANNER_CYAN = define("banner_cyan", 
/* 131 */       ResourceLocation.minecraft("cyan_banner"), true, true);
/* 132 */   public static final MapDecorationType BANNER_PURPLE = define("banner_purple", 
/* 133 */       ResourceLocation.minecraft("purple_banner"), true, true);
/* 134 */   public static final MapDecorationType BANNER_BLUE = define("banner_blue", 
/* 135 */       ResourceLocation.minecraft("blue_banner"), true, true);
/* 136 */   public static final MapDecorationType BANNER_BROWN = define("banner_brown", 
/* 137 */       ResourceLocation.minecraft("brown_banner"), true, true);
/* 138 */   public static final MapDecorationType BANNER_GREEN = define("banner_green", 
/* 139 */       ResourceLocation.minecraft("green_banner"), true, true);
/* 140 */   public static final MapDecorationType BANNER_RED = define("banner_red", 
/* 141 */       ResourceLocation.minecraft("red_banner"), true, true);
/* 142 */   public static final MapDecorationType BANNER_BLACK = define("banner_black", 
/* 143 */       ResourceLocation.minecraft("black_banner"), true, true);
/* 144 */   public static final MapDecorationType RED_X = define("red_x", true, false);
/*     */ 
/*     */ 
/*     */   
/* 148 */   public static final MapDecorationType VILLAGE_DESERT = define("village_desert", ResourceLocation.minecraft("desert_village"), true, 10066329, true, false);
/*     */   
/* 150 */   public static final MapDecorationType VILLAGE_PLAINS = define("village_plains", ResourceLocation.minecraft("plains_village"), true, 10066329, true, false);
/*     */   
/* 152 */   public static final MapDecorationType VILLAGE_SAVANNA = define("village_savanna", ResourceLocation.minecraft("savanna_village"), true, 10066329, true, false);
/*     */   
/* 154 */   public static final MapDecorationType VILLAGE_SNOWY = define("village_snowy", ResourceLocation.minecraft("snowy_village"), true, 10066329, true, false);
/*     */   
/* 156 */   public static final MapDecorationType VILLAGE_TAIGA = define("village_taiga", ResourceLocation.minecraft("taiga_village"), true, 10066329, true, false);
/*     */   
/* 158 */   public static final MapDecorationType JUNGLE_TEMPLE = define("jungle_temple", true, 10066329, true, false);
/*     */   
/* 160 */   public static final MapDecorationType SWAMP_HUT = define("swamp_hut", true, 10066329, true, false);
/*     */ 
/*     */ 
/*     */   
/* 164 */   public static final MapDecorationType TRIAL_CHAMBERS = define("trial_chambers", true, 12741452, true, false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Collection<MapDecorationType> values() {
/* 173 */     return REGISTRY.getEntries();
/*     */   }
/*     */   
/*     */   static {
/* 177 */     REGISTRY.unloadMappings();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\mapdecoration\MapDecorationTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */