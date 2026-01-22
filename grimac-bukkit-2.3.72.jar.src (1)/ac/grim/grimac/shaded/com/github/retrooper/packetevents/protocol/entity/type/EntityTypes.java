/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.Collection;
/*     */ import java.util.Objects;
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
/*     */ public final class EntityTypes
/*     */ {
/*  30 */   private static final VersionedRegistry<EntityType> REGISTRY = new VersionedRegistry("entity_type");
/*  31 */   private static final VersionedRegistry<EntityType> LEGACY_SPAWN_REGISTRY = new VersionedRegistry("legacy_spawn_entity_type");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static VersionedRegistry<EntityType> getRegistry() {
/*  37 */     return REGISTRY;
/*     */   }
/*     */   
/*     */   @Obsolete
/*     */   public static VersionedRegistry<EntityType> getLegacySpawnRegistry() {
/*  42 */     return LEGACY_SPAWN_REGISTRY;
/*     */   }
/*     */   
/*     */   @Internal
/*     */   public static EntityType define(String name, @Nullable EntityType parent) {
/*  47 */     StaticEntityType type = (StaticEntityType)REGISTRY.define(name, data -> new StaticEntityType(data, parent));
/*     */     
/*  49 */     Objects.requireNonNull(type); return (EntityType)LEGACY_SPAWN_REGISTRY.define(name, type::setLegacyData);
/*     */   }
/*     */   
/*     */   public static boolean isTypeInstanceOf(EntityType type, EntityType parent) {
/*  53 */     return (type != null && type.isInstanceOf(parent));
/*     */   }
/*     */   
/*     */   public static EntityType getByName(String name) {
/*  57 */     return (EntityType)REGISTRY.getByName(name);
/*     */   }
/*     */   
/*     */   public static EntityType getById(ClientVersion version, int id) {
/*  61 */     return (EntityType)REGISTRY.getById(version, id);
/*     */   }
/*     */   
/*     */   @Obsolete
/*     */   public static EntityType getByLegacyId(ClientVersion version, int id) {
/*  66 */     if (version.isNewerThanOrEquals(ClientVersion.V_1_14)) {
/*  67 */       return null;
/*     */     }
/*  69 */     return (EntityType)LEGACY_SPAWN_REGISTRY.getById(version, id);
/*     */   }
/*     */ 
/*     */   
/*  73 */   public static final EntityType ENTITY = define("entity", null);
/*  74 */   public static final EntityType LIVINGENTITY = define("livingentity", ENTITY);
/*  75 */   public static final EntityType ABSTRACT_INSENTIENT = define("abstract_insentient", LIVINGENTITY);
/*  76 */   public static final EntityType ABSTRACT_CREATURE = define("abstract_creature", ABSTRACT_INSENTIENT);
/*  77 */   public static final EntityType ABSTRACT_AGEABLE = define("abstract_ageable", ABSTRACT_CREATURE);
/*  78 */   public static final EntityType ABSTRACT_ANIMAL = define("abstract_animal", ABSTRACT_AGEABLE);
/*  79 */   public static final EntityType ABSTRACT_TAMEABLE_ANIMAL = define("abstract_tameable_animal", ABSTRACT_ANIMAL);
/*  80 */   public static final EntityType ABSTRACT_PARROT = define("abstract_parrot", ABSTRACT_TAMEABLE_ANIMAL);
/*  81 */   public static final EntityType ABSTRACT_HORSE = define("abstract_horse", ABSTRACT_ANIMAL);
/*  82 */   public static final EntityType CHESTED_HORSE = define("chested_horse", ABSTRACT_HORSE);
/*  83 */   public static final EntityType ABSTRACT_GOLEM = define("abstract_golem", ABSTRACT_CREATURE);
/*  84 */   public static final EntityType ABSTRACT_FISHES = define("abstract_fishes", ABSTRACT_CREATURE);
/*  85 */   public static final EntityType ABSTRACT_MONSTER = define("abstract_monster", ABSTRACT_CREATURE);
/*  86 */   public static final EntityType ABSTRACT_PIGLIN = define("abstract_piglin", ABSTRACT_MONSTER);
/*  87 */   public static final EntityType ABSTRACT_ILLAGER_BASE = define("abstract_illager_base", ABSTRACT_MONSTER);
/*  88 */   public static final EntityType ABSTRACT_EVO_ILLU_ILLAGER = define("abstract_evo_illu_illager", ABSTRACT_ILLAGER_BASE);
/*  89 */   public static final EntityType ABSTRACT_SKELETON = define("abstract_skeleton", ABSTRACT_MONSTER);
/*  90 */   public static final EntityType ABSTRACT_FLYING = define("abstract_flying", ABSTRACT_INSENTIENT);
/*  91 */   public static final EntityType ABSTRACT_AMBIENT = define("abstract_ambient", ABSTRACT_INSENTIENT);
/*  92 */   public static final EntityType ABSTRACT_WATERMOB = define("abstract_watermob", ABSTRACT_INSENTIENT);
/*  93 */   public static final EntityType ABSTRACT_HANGING = define("abstract_hanging", ENTITY);
/*  94 */   public static final EntityType ABSTRACT_LIGHTNING = define("abstract_lightning", ENTITY);
/*  95 */   public static final EntityType ABSTRACT_ARROW = define("abstract_arrow", ENTITY);
/*  96 */   public static final EntityType ABSTRACT_FIREBALL = define("abstract_fireball", ENTITY);
/*  97 */   public static final EntityType PROJECTILE_ABSTRACT = define("projectile_abstract", ENTITY);
/*  98 */   public static final EntityType MINECART_ABSTRACT = define("minecart_abstract", ENTITY);
/*  99 */   public static final EntityType CHESTED_MINECART_ABSTRACT = define("chested_minecart_abstract", MINECART_ABSTRACT);
/* 100 */   public static final EntityType AREA_EFFECT_CLOUD = define("area_effect_cloud", ENTITY);
/* 101 */   public static final EntityType ARMOR_STAND = define("armor_stand", LIVINGENTITY);
/* 102 */   public static final EntityType ALLAY = define("allay", ABSTRACT_CREATURE);
/* 103 */   public static final EntityType ARROW = define("arrow", ABSTRACT_ARROW);
/* 104 */   public static final EntityType AXOLOTL = define("axolotl", ABSTRACT_ANIMAL);
/* 105 */   public static final EntityType BAT = define("bat", ABSTRACT_AMBIENT);
/* 106 */   public static final EntityType BEE = define("bee", ABSTRACT_INSENTIENT);
/* 107 */   public static final EntityType BLAZE = define("blaze", ABSTRACT_MONSTER);
/*     */ 
/*     */ 
/*     */   
/* 111 */   public static final EntityType BOAT = define("boat", ENTITY);
/*     */ 
/*     */ 
/*     */   
/* 115 */   public static final EntityType CHEST_BOAT = define("chest_boat", BOAT);
/* 116 */   public static final EntityType CAT = define("cat", ABSTRACT_TAMEABLE_ANIMAL);
/* 117 */   public static final EntityType CAMEL = define("camel", ABSTRACT_HORSE);
/* 118 */   public static final EntityType SPIDER = define("spider", ABSTRACT_MONSTER);
/* 119 */   public static final EntityType CAVE_SPIDER = define("cave_spider", SPIDER);
/* 120 */   public static final EntityType CHICKEN = define("chicken", ABSTRACT_ANIMAL);
/* 121 */   public static final EntityType COD = define("cod", ABSTRACT_FISHES);
/* 122 */   public static final EntityType COW = define("cow", ABSTRACT_ANIMAL);
/* 123 */   public static final EntityType CREEPER = define("creeper", ABSTRACT_MONSTER);
/* 124 */   public static final EntityType DOLPHIN = define("dolphin", ABSTRACT_INSENTIENT);
/* 125 */   public static final EntityType DONKEY = define("donkey", CHESTED_HORSE);
/* 126 */   public static final EntityType DRAGON_FIREBALL = define("dragon_fireball", ABSTRACT_FIREBALL);
/* 127 */   public static final EntityType ZOMBIE = define("zombie", ABSTRACT_MONSTER);
/* 128 */   public static final EntityType DROWNED = define("drowned", ZOMBIE);
/* 129 */   public static final EntityType GUARDIAN = define("guardian", ABSTRACT_MONSTER);
/* 130 */   public static final EntityType ELDER_GUARDIAN = define("elder_guardian", GUARDIAN);
/* 131 */   public static final EntityType END_CRYSTAL = define("end_crystal", ENTITY);
/* 132 */   public static final EntityType ENDER_DRAGON = define("ender_dragon", ABSTRACT_INSENTIENT);
/* 133 */   public static final EntityType ENDERMAN = define("enderman", ABSTRACT_MONSTER);
/* 134 */   public static final EntityType ENDERMITE = define("endermite", ABSTRACT_MONSTER);
/* 135 */   public static final EntityType EVOKER = define("evoker", ABSTRACT_EVO_ILLU_ILLAGER);
/* 136 */   public static final EntityType EVOKER_FANGS = define("evoker_fangs", ENTITY);
/* 137 */   public static final EntityType EXPERIENCE_ORB = define("experience_orb", ENTITY);
/* 138 */   public static final EntityType EYE_OF_ENDER = define("eye_of_ender", ENTITY);
/* 139 */   public static final EntityType FALLING_BLOCK = define("falling_block", ENTITY);
/* 140 */   public static final EntityType FIREWORK_ROCKET = define("firework_rocket", ENTITY);
/* 141 */   public static final EntityType FOX = define("fox", ABSTRACT_ANIMAL);
/* 142 */   public static final EntityType FROG = define("frog", ABSTRACT_ANIMAL);
/* 143 */   public static final EntityType GHAST = define("ghast", ABSTRACT_FLYING);
/* 144 */   public static final EntityType GIANT = define("giant", ABSTRACT_MONSTER);
/* 145 */   public static final EntityType ITEM_FRAME = define("item_frame", ABSTRACT_HANGING);
/* 146 */   public static final EntityType GLOW_ITEM_FRAME = define("glow_item_frame", ITEM_FRAME);
/* 147 */   public static final EntityType SQUID = define("squid", ABSTRACT_WATERMOB);
/* 148 */   public static final EntityType GLOW_SQUID = define("glow_squid", SQUID);
/* 149 */   public static final EntityType GOAT = define("goat", ABSTRACT_ANIMAL);
/* 150 */   public static final EntityType HOGLIN = define("hoglin", ABSTRACT_ANIMAL);
/* 151 */   public static final EntityType HORSE = define("horse", ABSTRACT_HORSE);
/* 152 */   public static final EntityType HUSK = define("husk", ZOMBIE);
/* 153 */   public static final EntityType ILLUSIONER = define("illusioner", ABSTRACT_EVO_ILLU_ILLAGER);
/* 154 */   public static final EntityType IRON_GOLEM = define("iron_golem", ABSTRACT_GOLEM);
/* 155 */   public static final EntityType ITEM = define("item", ENTITY);
/* 156 */   public static final EntityType FIREBALL = define("fireball", ABSTRACT_FIREBALL);
/* 157 */   public static final EntityType LEASH_KNOT = define("leash_knot", ABSTRACT_HANGING);
/* 158 */   public static final EntityType LIGHTNING_BOLT = define("lightning_bolt", ABSTRACT_LIGHTNING);
/* 159 */   public static final EntityType LLAMA = define("llama", CHESTED_HORSE);
/* 160 */   public static final EntityType LLAMA_SPIT = define("llama_spit", ENTITY);
/* 161 */   public static final EntityType SLIME = define("slime", ABSTRACT_INSENTIENT);
/* 162 */   public static final EntityType MAGMA_CUBE = define("magma_cube", SLIME);
/* 163 */   public static final EntityType MARKER = define("marker", ENTITY);
/* 164 */   public static final EntityType MINECART = define("minecart", MINECART_ABSTRACT);
/* 165 */   public static final EntityType CHEST_MINECART = define("chest_minecart", CHESTED_MINECART_ABSTRACT);
/* 166 */   public static final EntityType COMMAND_BLOCK_MINECART = define("command_block_minecart", MINECART_ABSTRACT);
/* 167 */   public static final EntityType FURNACE_MINECART = define("furnace_minecart", MINECART_ABSTRACT);
/* 168 */   public static final EntityType HOPPER_MINECART = define("hopper_minecart", CHESTED_MINECART_ABSTRACT);
/* 169 */   public static final EntityType SPAWNER_MINECART = define("spawner_minecart", MINECART_ABSTRACT);
/* 170 */   public static final EntityType TNT_MINECART = define("tnt_minecart", MINECART_ABSTRACT);
/* 171 */   public static final EntityType MULE = define("mule", CHESTED_HORSE);
/* 172 */   public static final EntityType MOOSHROOM = define("mooshroom", COW);
/* 173 */   public static final EntityType OCELOT = define("ocelot", ABSTRACT_TAMEABLE_ANIMAL);
/* 174 */   public static final EntityType PAINTING = define("painting", ABSTRACT_HANGING);
/* 175 */   public static final EntityType PANDA = define("panda", ABSTRACT_INSENTIENT);
/* 176 */   public static final EntityType PARROT = define("parrot", ABSTRACT_PARROT);
/* 177 */   public static final EntityType PHANTOM = define("phantom", ABSTRACT_FLYING);
/* 178 */   public static final EntityType PIG = define("pig", ABSTRACT_ANIMAL);
/* 179 */   public static final EntityType PIGLIN = define("piglin", ABSTRACT_PIGLIN);
/* 180 */   public static final EntityType PIGLIN_BRUTE = define("piglin_brute", ABSTRACT_PIGLIN);
/* 181 */   public static final EntityType PILLAGER = define("pillager", ABSTRACT_ILLAGER_BASE);
/* 182 */   public static final EntityType POLAR_BEAR = define("polar_bear", ABSTRACT_ANIMAL);
/* 183 */   public static final EntityType TNT = define("tnt", ENTITY);
/* 184 */   public static final EntityType PUFFERFISH = define("pufferfish", ABSTRACT_FISHES);
/* 185 */   public static final EntityType RABBIT = define("rabbit", ABSTRACT_ANIMAL);
/* 186 */   public static final EntityType RAVAGER = define("ravager", ABSTRACT_MONSTER);
/* 187 */   public static final EntityType SALMON = define("salmon", ABSTRACT_FISHES);
/* 188 */   public static final EntityType SHEEP = define("sheep", ABSTRACT_ANIMAL);
/* 189 */   public static final EntityType SHULKER = define("shulker", ABSTRACT_GOLEM);
/* 190 */   public static final EntityType SHULKER_BULLET = define("shulker_bullet", ENTITY);
/* 191 */   public static final EntityType SILVERFISH = define("silverfish", ABSTRACT_MONSTER);
/* 192 */   public static final EntityType SKELETON = define("skeleton", ABSTRACT_SKELETON);
/* 193 */   public static final EntityType SKELETON_HORSE = define("skeleton_horse", ABSTRACT_HORSE);
/* 194 */   public static final EntityType SMALL_FIREBALL = define("small_fireball", ABSTRACT_FIREBALL);
/* 195 */   public static final EntityType SNOW_GOLEM = define("snow_golem", ABSTRACT_GOLEM);
/* 196 */   public static final EntityType SNOWBALL = define("snowball", PROJECTILE_ABSTRACT);
/* 197 */   public static final EntityType SPECTRAL_ARROW = define("spectral_arrow", ABSTRACT_ARROW);
/* 198 */   public static final EntityType STRAY = define("stray", ABSTRACT_SKELETON);
/* 199 */   public static final EntityType STRIDER = define("strider", ABSTRACT_ANIMAL);
/* 200 */   public static final EntityType EGG = define("egg", PROJECTILE_ABSTRACT);
/* 201 */   public static final EntityType ENDER_PEARL = define("ender_pearl", PROJECTILE_ABSTRACT);
/* 202 */   public static final EntityType EXPERIENCE_BOTTLE = define("experience_bottle", PROJECTILE_ABSTRACT);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 207 */   public static final EntityType POTION = define("potion", PROJECTILE_ABSTRACT);
/* 208 */   public static final EntityType TADPOLE = define("tadpole", ABSTRACT_FISHES);
/*     */   @Deprecated
/* 210 */   public static final EntityType TIPPED_ARROW = define("tipped_arrow", ARROW);
/* 211 */   public static final EntityType TRIDENT = define("trident", ABSTRACT_ARROW);
/* 212 */   public static final EntityType TRADER_LLAMA = define("trader_llama", CHESTED_HORSE);
/* 213 */   public static final EntityType TROPICAL_FISH = define("tropical_fish", ABSTRACT_FISHES);
/* 214 */   public static final EntityType TURTLE = define("turtle", ABSTRACT_ANIMAL);
/* 215 */   public static final EntityType VEX = define("vex", ABSTRACT_MONSTER);
/* 216 */   public static final EntityType VILLAGER = define("villager", ABSTRACT_AGEABLE);
/* 217 */   public static final EntityType VINDICATOR = define("vindicator", ABSTRACT_ILLAGER_BASE);
/* 218 */   public static final EntityType WANDERING_TRADER = define("wandering_trader", ABSTRACT_AGEABLE);
/* 219 */   public static final EntityType WARDEN = define("warden", ABSTRACT_MONSTER);
/* 220 */   public static final EntityType WITCH = define("witch", ABSTRACT_MONSTER);
/* 221 */   public static final EntityType WITHER = define("wither", ABSTRACT_MONSTER);
/* 222 */   public static final EntityType WITHER_SKELETON = define("wither_skeleton", ABSTRACT_SKELETON);
/* 223 */   public static final EntityType WITHER_SKULL = define("wither_skull", ABSTRACT_FIREBALL);
/* 224 */   public static final EntityType WOLF = define("wolf", ABSTRACT_TAMEABLE_ANIMAL);
/* 225 */   public static final EntityType ZOGLIN = define("zoglin", ABSTRACT_MONSTER);
/* 226 */   public static final EntityType ZOMBIE_HORSE = define("zombie_horse", ABSTRACT_HORSE);
/* 227 */   public static final EntityType ZOMBIE_VILLAGER = define("zombie_villager", ZOMBIE);
/* 228 */   public static final EntityType ZOMBIFIED_PIGLIN = define("zombified_piglin", ZOMBIE);
/* 229 */   public static final EntityType PLAYER = define("player", LIVINGENTITY);
/* 230 */   public static final EntityType FISHING_BOBBER = define("fishing_bobber", ENTITY);
/* 231 */   public static final EntityType ENDER_SIGNAL = define("ender_signal", ENTITY);
/* 232 */   public static final EntityType THROWN_EXP_BOTTLE = define("thrown_exp_bottle", PROJECTILE_ABSTRACT);
/* 233 */   public static final EntityType PRIMED_TNT = define("primed_tnt", ENTITY);
/* 234 */   public static final EntityType FIREWORK = define("firework", ENTITY);
/* 235 */   public static final EntityType MINECART_COMMAND = define("minecart_command", MINECART_ABSTRACT);
/* 236 */   public static final EntityType MINECART_RIDEABLE = define("minecart_rideable", MINECART_ABSTRACT);
/* 237 */   public static final EntityType MINECART_CHEST = define("minecart_chest", MINECART_ABSTRACT);
/* 238 */   public static final EntityType MINECART_FURNACE = define("minecart_furnace", MINECART_ABSTRACT);
/* 239 */   public static final EntityType MINECART_TNT = define("minecart_tnt", MINECART_ABSTRACT);
/* 240 */   public static final EntityType MINECART_HOPPER = define("minecart_hopper", MINECART_ABSTRACT);
/* 241 */   public static final EntityType MINECART_MOB_SPAWNER = define("minecart_mob_spawner", MINECART_ABSTRACT);
/*     */ 
/*     */   
/* 244 */   public static final EntityType DISPLAY = define("display", ENTITY);
/* 245 */   public static final EntityType BLOCK_DISPLAY = define("block_display", DISPLAY);
/* 246 */   public static final EntityType ITEM_DISPLAY = define("item_display", DISPLAY);
/* 247 */   public static final EntityType TEXT_DISPLAY = define("text_display", DISPLAY);
/* 248 */   public static final EntityType INTERACTION = define("interaction", DISPLAY);
/* 249 */   public static final EntityType SNIFFER = define("sniffer", ABSTRACT_ANIMAL);
/*     */ 
/*     */   
/* 252 */   public static final EntityType BREEZE = define("breeze", ABSTRACT_MONSTER);
/* 253 */   public static final EntityType ABSTRACT_WIND_CHARGE = define("abstract_wind_charge", PROJECTILE_ABSTRACT);
/* 254 */   public static final EntityType WIND_CHARGE = define("wind_charge", ABSTRACT_WIND_CHARGE);
/*     */ 
/*     */   
/* 257 */   public static final EntityType ARMADILLO = define("armadillo", ABSTRACT_ANIMAL);
/* 258 */   public static final EntityType BOGGED = define("bogged", ABSTRACT_SKELETON);
/* 259 */   public static final EntityType BREEZE_WIND_CHARGE = define("breeze_wind_charge", ABSTRACT_WIND_CHARGE);
/* 260 */   public static final EntityType OMINOUS_ITEM_SPAWNER = define("ominous_item_spawner", ENTITY);
/*     */ 
/*     */   
/* 263 */   public static final EntityType ACACIA_BOAT = define("acacia_boat", BOAT);
/* 264 */   public static final EntityType ACACIA_CHEST_BOAT = define("acacia_chest_boat", CHEST_BOAT);
/* 265 */   public static final EntityType BAMBOO_CHEST_RAFT = define("bamboo_chest_raft", CHEST_BOAT);
/* 266 */   public static final EntityType BAMBOO_RAFT = define("bamboo_raft", BOAT);
/* 267 */   public static final EntityType BIRCH_BOAT = define("birch_boat", BOAT);
/* 268 */   public static final EntityType BIRCH_CHEST_BOAT = define("birch_chest_boat", CHEST_BOAT);
/* 269 */   public static final EntityType CHERRY_BOAT = define("cherry_boat", BOAT);
/* 270 */   public static final EntityType CHERRY_CHEST_BOAT = define("cherry_chest_boat", CHEST_BOAT);
/* 271 */   public static final EntityType CREAKING = define("creaking", ABSTRACT_MONSTER);
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/* 276 */   public static final EntityType CREAKING_TRANSIENT = define("creaking_transient", CREAKING);
/* 277 */   public static final EntityType DARK_OAK_BOAT = define("dark_oak_boat", BOAT);
/* 278 */   public static final EntityType DARK_OAK_CHEST_BOAT = define("dark_oak_chest_boat", CHEST_BOAT);
/* 279 */   public static final EntityType JUNGLE_BOAT = define("jungle_boat", BOAT);
/* 280 */   public static final EntityType JUNGLE_CHEST_BOAT = define("jungle_chest_boat", CHEST_BOAT);
/* 281 */   public static final EntityType MANGROVE_BOAT = define("mangrove_boat", BOAT);
/* 282 */   public static final EntityType MANGROVE_CHEST_BOAT = define("mangrove_chest_boat", CHEST_BOAT);
/* 283 */   public static final EntityType OAK_BOAT = define("oak_boat", BOAT);
/* 284 */   public static final EntityType OAK_CHEST_BOAT = define("oak_chest_boat", CHEST_BOAT);
/* 285 */   public static final EntityType PALE_OAK_BOAT = define("pale_oak_boat", BOAT);
/* 286 */   public static final EntityType PALE_OAK_CHEST_BOAT = define("pale_oak_chest_boat", CHEST_BOAT);
/* 287 */   public static final EntityType SPRUCE_BOAT = define("spruce_boat", BOAT);
/* 288 */   public static final EntityType SPRUCE_CHEST_BOAT = define("spruce_chest_boat", CHEST_BOAT);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 293 */   public static final EntityType SPLASH_POTION = define("splash_potion", POTION);
/*     */ 
/*     */ 
/*     */   
/* 297 */   public static final EntityType LINGERING_POTION = define("lingering_potion", POTION);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 302 */   public static final EntityType HAPPY_GHAST = define("happy_ghast", ABSTRACT_ANIMAL);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Collection<EntityType> values() {
/* 310 */     return REGISTRY.getEntries();
/*     */   }
/*     */   
/*     */   static {
/* 314 */     REGISTRY.unloadMappings();
/* 315 */     LEGACY_SPAWN_REGISTRY.unloadMappings();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\type\EntityTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */