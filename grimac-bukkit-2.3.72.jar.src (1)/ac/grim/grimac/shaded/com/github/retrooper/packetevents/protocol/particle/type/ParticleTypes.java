/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.type;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.data.ParticleBlockStateData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.data.ParticleColorData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.data.ParticleData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.data.ParticleDustColorTransitionData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.data.ParticleDustData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.data.ParticleItemStackData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.data.ParticleSculkChargeData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.data.ParticleShriekData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.data.ParticleTrailData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.data.ParticleVibrationData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
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
/*     */ public final class ParticleTypes
/*     */ {
/*  43 */   private static final VersionedRegistry<ParticleType<?>> REGISTRY = new VersionedRegistry("particle_type");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static VersionedRegistry<ParticleType<?>> getRegistry() {
/*  49 */     return REGISTRY;
/*     */   }
/*     */   
/*     */   @Internal
/*     */   public static ParticleType<ParticleData> define(String name) {
/*  54 */     return define(name, wrapper -> ParticleData.emptyData(), null, (nbt, version) -> ParticleData.emptyData(), null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public static <T extends ParticleData> ParticleType<T> define(String name, PacketWrapper.Reader<T> reader, @Nullable PacketWrapper.Writer<T> writer, Decoder<T> decoder, @Nullable Encoder<T> encoder) {
/*  67 */     return (ParticleType<T>)REGISTRY.define(name, data -> new StaticParticleType<>(data, reader, writer, decoder, encoder));
/*     */   }
/*     */ 
/*     */   
/*     */   public static ParticleType<?> getByName(String name) {
/*  72 */     return (ParticleType)REGISTRY.getByName(name);
/*     */   }
/*     */   
/*     */   public static ParticleType<?> getById(ClientVersion version, int id) {
/*  76 */     return (ParticleType)REGISTRY.getById(version, id);
/*     */   }
/*     */   
/*     */   @Deprecated
/*  80 */   public static final ParticleType<ParticleData> AMBIENT_ENTITY_EFFECT = define("ambient_entity_effect");
/*  81 */   public static final ParticleType<ParticleData> ANGRY_VILLAGER = define("angry_villager");
/*  82 */   public static final ParticleType<ParticleBlockStateData> BLOCK = define("block", ParticleBlockStateData::read, ParticleBlockStateData::write, ParticleBlockStateData::decode, ParticleBlockStateData::encode);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*  89 */   public static final ParticleType<ParticleData> BARRIER = define("barrier");
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*  94 */   public static final ParticleType<ParticleData> LIGHT = define("light");
/*     */ 
/*     */ 
/*     */   
/*  98 */   public static final ParticleType<ParticleBlockStateData> BLOCK_MARKER = define("block_marker", ParticleBlockStateData::read, ParticleBlockStateData::write, ParticleBlockStateData::decode, ParticleBlockStateData::encode);
/*     */ 
/*     */   
/* 101 */   public static final ParticleType<ParticleData> BUBBLE = define("bubble");
/* 102 */   public static final ParticleType<ParticleData> CLOUD = define("cloud");
/* 103 */   public static final ParticleType<ParticleData> CRIT = define("crit");
/* 104 */   public static final ParticleType<ParticleData> DAMAGE_INDICATOR = define("damage_indicator");
/* 105 */   public static final ParticleType<ParticleData> DRAGON_BREATH = define("dragon_breath");
/* 106 */   public static final ParticleType<ParticleData> DRIPPING_LAVA = define("dripping_lava");
/* 107 */   public static final ParticleType<ParticleData> FALLING_LAVA = define("falling_lava");
/* 108 */   public static final ParticleType<ParticleData> LANDING_LAVA = define("landing_lava");
/* 109 */   public static final ParticleType<ParticleData> DRIPPING_WATER = define("dripping_water");
/* 110 */   public static final ParticleType<ParticleData> FALLING_WATER = define("falling_water");
/* 111 */   public static final ParticleType<ParticleDustData> DUST = define("dust", ParticleDustData::read, ParticleDustData::write, ParticleDustData::decode, ParticleDustData::encode);
/*     */ 
/*     */   
/* 114 */   public static final ParticleType<ParticleDustColorTransitionData> DUST_COLOR_TRANSITION = define("dust_color_transition", ParticleDustColorTransitionData::read, ParticleDustColorTransitionData::write, ParticleDustColorTransitionData::decode, ParticleDustColorTransitionData::encode);
/*     */ 
/*     */   
/* 117 */   public static final ParticleType<ParticleData> EFFECT = define("effect");
/* 118 */   public static final ParticleType<ParticleData> ELDER_GUARDIAN = define("elder_guardian");
/* 119 */   public static final ParticleType<ParticleData> ENCHANTED_HIT = define("enchanted_hit");
/* 120 */   public static final ParticleType<ParticleData> ENCHANT = define("enchant");
/* 121 */   public static final ParticleType<ParticleData> END_ROD = define("end_rod");
/* 122 */   public static final ParticleType<ParticleColorData> ENTITY_EFFECT = define("entity_effect", ParticleColorData::read, ParticleColorData::write, ParticleColorData::decode, ParticleColorData::encode);
/*     */ 
/*     */   
/* 125 */   public static final ParticleType<ParticleData> EXPLOSION_EMITTER = define("explosion_emitter");
/* 126 */   public static final ParticleType<ParticleData> EXPLOSION = define("explosion");
/* 127 */   public static final ParticleType<ParticleData> SONIC_BOOM = define("sonic_boom");
/* 128 */   public static final ParticleType<ParticleBlockStateData> FALLING_DUST = define("falling_dust", ParticleBlockStateData::read, ParticleBlockStateData::write, ParticleBlockStateData::decode, ParticleBlockStateData::encode);
/*     */ 
/*     */   
/* 131 */   public static final ParticleType<ParticleData> FIREWORK = define("firework");
/* 132 */   public static final ParticleType<ParticleData> FISHING = define("fishing");
/* 133 */   public static final ParticleType<ParticleData> FLAME = define("flame");
/* 134 */   public static final ParticleType<ParticleData> SCULK_SOUL = define("sculk_soul");
/* 135 */   public static final ParticleType<ParticleSculkChargeData> SCULK_CHARGE = define("sculk_charge", ParticleSculkChargeData::read, ParticleSculkChargeData::write, ParticleSculkChargeData::decode, ParticleSculkChargeData::encode);
/*     */ 
/*     */   
/* 138 */   public static final ParticleType<ParticleData> SCULK_CHARGE_POP = define("sculk_charge_pop");
/* 139 */   public static final ParticleType<ParticleData> SOUL_FIRE_FLAME = define("soul_fire_flame");
/* 140 */   public static final ParticleType<ParticleData> SOUL = define("soul");
/* 141 */   public static final ParticleType<ParticleData> FLASH = define("flash");
/* 142 */   public static final ParticleType<ParticleData> HAPPY_VILLAGER = define("happy_villager");
/* 143 */   public static final ParticleType<ParticleData> COMPOSTER = define("composter");
/* 144 */   public static final ParticleType<ParticleData> HEART = define("heart");
/* 145 */   public static final ParticleType<ParticleData> INSTANT_EFFECT = define("instant_effect");
/* 146 */   public static final ParticleType<ParticleItemStackData> ITEM = define("item", ParticleItemStackData::read, ParticleItemStackData::write, ParticleItemStackData::decode, ParticleItemStackData::encode);
/*     */ 
/*     */   
/* 149 */   public static final ParticleType<ParticleVibrationData> VIBRATION = define("vibration", ParticleVibrationData::read, ParticleVibrationData::write, ParticleVibrationData::decode, ParticleVibrationData::encode);
/*     */ 
/*     */   
/* 152 */   public static final ParticleType<ParticleData> ITEM_SLIME = define("item_slime");
/* 153 */   public static final ParticleType<ParticleData> ITEM_SNOWBALL = define("item_snowball");
/* 154 */   public static final ParticleType<ParticleData> LARGE_SMOKE = define("large_smoke");
/* 155 */   public static final ParticleType<ParticleData> LAVA = define("lava");
/* 156 */   public static final ParticleType<ParticleData> MYCELIUM = define("mycelium");
/* 157 */   public static final ParticleType<ParticleData> NOTE = define("note");
/* 158 */   public static final ParticleType<ParticleData> POOF = define("poof");
/* 159 */   public static final ParticleType<ParticleData> PORTAL = define("portal");
/* 160 */   public static final ParticleType<ParticleData> RAIN = define("rain");
/* 161 */   public static final ParticleType<ParticleData> SMOKE = define("smoke");
/* 162 */   public static final ParticleType<ParticleData> SNEEZE = define("sneeze");
/* 163 */   public static final ParticleType<ParticleData> SPIT = define("spit");
/* 164 */   public static final ParticleType<ParticleData> SQUID_INK = define("squid_ink");
/* 165 */   public static final ParticleType<ParticleData> SWEEP_ATTACK = define("sweep_attack");
/* 166 */   public static final ParticleType<ParticleData> TOTEM_OF_UNDYING = define("totem_of_undying");
/* 167 */   public static final ParticleType<ParticleData> UNDERWATER = define("underwater");
/* 168 */   public static final ParticleType<ParticleData> SPLASH = define("splash");
/* 169 */   public static final ParticleType<ParticleData> WITCH = define("witch");
/* 170 */   public static final ParticleType<ParticleData> BUBBLE_POP = define("bubble_pop");
/* 171 */   public static final ParticleType<ParticleData> CURRENT_DOWN = define("current_down");
/* 172 */   public static final ParticleType<ParticleData> BUBBLE_COLUMN_UP = define("bubble_column_up");
/* 173 */   public static final ParticleType<ParticleData> NAUTILUS = define("nautilus");
/* 174 */   public static final ParticleType<ParticleData> DOLPHIN = define("dolphin");
/* 175 */   public static final ParticleType<ParticleData> CAMPFIRE_COSY_SMOKE = define("campfire_cosy_smoke");
/* 176 */   public static final ParticleType<ParticleData> CAMPFIRE_SIGNAL_SMOKE = define("campfire_signal_smoke");
/* 177 */   public static final ParticleType<ParticleData> DRIPPING_HONEY = define("dripping_honey");
/* 178 */   public static final ParticleType<ParticleData> FALLING_HONEY = define("falling_honey");
/* 179 */   public static final ParticleType<ParticleData> LANDING_HONEY = define("landing_honey");
/* 180 */   public static final ParticleType<ParticleData> FALLING_NECTAR = define("falling_nectar");
/* 181 */   public static final ParticleType<ParticleData> FALLING_SPORE_BLOSSOM = define("falling_spore_blossom");
/* 182 */   public static final ParticleType<ParticleData> ASH = define("ash");
/* 183 */   public static final ParticleType<ParticleData> CRIMSON_SPORE = define("crimson_spore");
/* 184 */   public static final ParticleType<ParticleData> WARPED_SPORE = define("warped_spore");
/* 185 */   public static final ParticleType<ParticleData> SPORE_BLOSSOM_AIR = define("spore_blossom_air");
/* 186 */   public static final ParticleType<ParticleData> DRIPPING_OBSIDIAN_TEAR = define("dripping_obsidian_tear");
/* 187 */   public static final ParticleType<ParticleData> FALLING_OBSIDIAN_TEAR = define("falling_obsidian_tear");
/* 188 */   public static final ParticleType<ParticleData> LANDING_OBSIDIAN_TEAR = define("landing_obsidian_tear");
/* 189 */   public static final ParticleType<ParticleData> REVERSE_PORTAL = define("reverse_portal");
/* 190 */   public static final ParticleType<ParticleData> WHITE_ASH = define("white_ash");
/* 191 */   public static final ParticleType<ParticleData> SMALL_FLAME = define("small_flame");
/* 192 */   public static final ParticleType<ParticleData> SNOWFLAKE = define("snowflake");
/* 193 */   public static final ParticleType<ParticleData> DRIPPING_DRIPSTONE_LAVA = define("dripping_dripstone_lava");
/* 194 */   public static final ParticleType<ParticleData> FALLING_DRIPSTONE_LAVA = define("falling_dripstone_lava");
/* 195 */   public static final ParticleType<ParticleData> DRIPPING_DRIPSTONE_WATER = define("dripping_dripstone_water");
/* 196 */   public static final ParticleType<ParticleData> FALLING_DRIPSTONE_WATER = define("falling_dripstone_water");
/* 197 */   public static final ParticleType<ParticleData> GLOW_SQUID_INK = define("glow_squid_ink");
/* 198 */   public static final ParticleType<ParticleData> GLOW = define("glow");
/* 199 */   public static final ParticleType<ParticleData> WAX_ON = define("wax_on");
/* 200 */   public static final ParticleType<ParticleData> WAX_OFF = define("wax_off");
/* 201 */   public static final ParticleType<ParticleData> ELECTRIC_SPARK = define("electric_spark");
/* 202 */   public static final ParticleType<ParticleData> SCRAPE = define("scrape");
/* 203 */   public static final ParticleType<ParticleShriekData> SHRIEK = define("shriek", ParticleShriekData::read, ParticleShriekData::write, ParticleShriekData::decode, ParticleShriekData::encode);
/*     */ 
/*     */ 
/*     */   
/* 207 */   public static final ParticleType<ParticleData> DRIPPING_CHERRY_LEAVES = define("dripping_cherry_leaves");
/* 208 */   public static final ParticleType<ParticleData> FALLING_CHERRY_LEAVES = define("falling_cherry_leaves");
/* 209 */   public static final ParticleType<ParticleData> LANDING_CHERRY_LEAVES = define("landing_cherry_leaves");
/*     */ 
/*     */   
/* 212 */   public static final ParticleType<ParticleData> CHERRY_LEAVES = define("cherry_leaves");
/* 213 */   public static final ParticleType<ParticleData> EGG_CRACK = define("egg_crack");
/*     */ 
/*     */   
/* 216 */   public static final ParticleType<ParticleData> GUST = define("gust");
/*     */   @Deprecated
/* 218 */   public static final ParticleType<ParticleData> GUST_EMITTER = define("gust_emitter");
/* 219 */   public static final ParticleType<ParticleData> WHITE_SMOKE = define("white_smoke");
/* 220 */   public static final ParticleType<ParticleData> DUST_PLUME = define("dust_plume");
/* 221 */   public static final ParticleType<ParticleData> GUST_DUST = define("gust_dust");
/* 222 */   public static final ParticleType<ParticleData> TRIAL_SPAWNER_DETECTION = define("trial_spawner_detection");
/*     */ 
/*     */   
/* 225 */   public static final ParticleType<ParticleData> SMALL_GUST = define("small_gust");
/* 226 */   public static final ParticleType<ParticleData> GUST_EMITTER_LARGE = define("gust_emitter_large");
/* 227 */   public static final ParticleType<ParticleData> GUST_EMITTER_SMALL = define("gust_emitter_small");
/* 228 */   public static final ParticleType<ParticleData> INFESTED = define("infested");
/* 229 */   public static final ParticleType<ParticleData> ITEM_COBWEB = define("item_cobweb");
/* 230 */   public static final ParticleType<ParticleData> TRIAL_SPAWNER_DETECTION_OMINOUS = define("trial_spawner_detection_ominous");
/* 231 */   public static final ParticleType<ParticleData> VAULT_CONNECTION = define("vault_connection");
/* 232 */   public static final ParticleType<ParticleBlockStateData> DUST_PILLAR = define("dust_pillar", ParticleBlockStateData::read, ParticleBlockStateData::write, ParticleBlockStateData::decode, ParticleBlockStateData::encode);
/*     */ 
/*     */   
/* 235 */   public static final ParticleType<ParticleData> OMINOUS_SPAWNING = define("ominous_spawning");
/* 236 */   public static final ParticleType<ParticleData> RAID_OMEN = define("raid_omen");
/* 237 */   public static final ParticleType<ParticleData> TRIAL_OMEN = define("trial_omen");
/*     */ 
/*     */   
/* 240 */   public static final ParticleType<ParticleTrailData> TRAIL = define("trail", ParticleTrailData::read, ParticleTrailData::write, ParticleTrailData::decode, ParticleTrailData::encode);
/*     */ 
/*     */   
/* 243 */   public static final ParticleType<ParticleBlockStateData> BLOCK_CRUMBLE = define("block_crumble", ParticleBlockStateData::read, ParticleBlockStateData::write, ParticleBlockStateData::decode, ParticleBlockStateData::encode);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 250 */   public static final ParticleType<ParticleData> PALE_OAK_LEAVES = define("pale_oak_leaves");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 255 */   public static final ParticleType<ParticleColorData> TINTED_LEAVES = define("tinted_leaves", ParticleColorData::read, ParticleColorData::write, ParticleColorData::decode, ParticleColorData::encode);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 261 */   public static final ParticleType<ParticleData> FIREFLY = define("firefly");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Collection<ParticleType<?>> values() {
/* 269 */     return REGISTRY.getEntries();
/*     */   }
/*     */   
/*     */   static {
/* 273 */     REGISTRY.unloadMappings();
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   @Internal
/*     */   public static interface Decoder<T> {
/*     */     T decode(NBTCompound param1NBTCompound, ClientVersion param1ClientVersion);
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   @Internal
/*     */   public static interface Encoder<T> {
/*     */     void encode(T param1T, ClientVersion param1ClientVersion, NBTCompound param1NBTCompound);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\particle\type\ParticleTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */