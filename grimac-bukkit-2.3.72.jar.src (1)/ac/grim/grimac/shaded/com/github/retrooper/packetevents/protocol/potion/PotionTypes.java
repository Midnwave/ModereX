/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.Collection;
/*     */ import java.util.function.Function;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class PotionTypes
/*     */ {
/*  38 */   private static final VersionedRegistry<PotionType> REGISTRY = new VersionedRegistry("mob_effect");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static VersionedRegistry<PotionType> getRegistry() {
/*  44 */     return REGISTRY;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public static PotionType define(String name, int ignoredId) {
/*  49 */     return define(name);
/*     */   }
/*     */   
/*     */   @Internal
/*     */   public static PotionType define(String name) {
/*  54 */     return (PotionType)REGISTRY.define(name, StaticPotionType::new);
/*     */   }
/*     */   @Nullable
/*     */   public static PotionType getByName(String name) {
/*  58 */     return (PotionType)REGISTRY.getByName(name);
/*     */   }
/*     */   @Deprecated
/*     */   @Nullable
/*     */   public static PotionType getById(int id) {
/*  63 */     return getById(id, PacketEvents.getAPI().getServerManager().getVersion().toClientVersion());
/*     */   }
/*     */   @Deprecated
/*     */   @Nullable
/*     */   public static PotionType getById(int id, ServerVersion version) {
/*  68 */     return getById(id, version.toClientVersion());
/*     */   }
/*     */   @Deprecated
/*     */   @Nullable
/*     */   public static PotionType getById(int id, ClientVersion version) {
/*  73 */     return getById(version, id);
/*     */   }
/*     */   @Nullable
/*     */   public static PotionType getById(ClientVersion version, int id) {
/*  77 */     return (PotionType)REGISTRY.getById(version, id);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  82 */   public static final PotionType SPEED = define("speed");
/*  83 */   public static final PotionType SLOWNESS = define("slowness");
/*  84 */   public static final PotionType HASTE = define("haste");
/*  85 */   public static final PotionType MINING_FATIGUE = define("mining_fatigue");
/*  86 */   public static final PotionType STRENGTH = define("strength");
/*  87 */   public static final PotionType INSTANT_HEALTH = define("instant_health");
/*  88 */   public static final PotionType INSTANT_DAMAGE = define("instant_damage");
/*  89 */   public static final PotionType JUMP_BOOST = define("jump_boost");
/*  90 */   public static final PotionType NAUSEA = define("nausea");
/*  91 */   public static final PotionType REGENERATION = define("regeneration");
/*  92 */   public static final PotionType RESISTANCE = define("resistance");
/*  93 */   public static final PotionType FIRE_RESISTANCE = define("fire_resistance");
/*  94 */   public static final PotionType WATER_BREATHING = define("water_breathing");
/*  95 */   public static final PotionType INVISIBILITY = define("invisibility");
/*  96 */   public static final PotionType BLINDNESS = define("blindness");
/*  97 */   public static final PotionType NIGHT_VISION = define("night_vision");
/*  98 */   public static final PotionType HUNGER = define("hunger");
/*  99 */   public static final PotionType WEAKNESS = define("weakness");
/* 100 */   public static final PotionType POISON = define("poison");
/*     */ 
/*     */   
/* 103 */   public static final PotionType WITHER = define("wither");
/*     */ 
/*     */   
/* 106 */   public static final PotionType HEALTH_BOOST = define("health_boost");
/* 107 */   public static final PotionType ABSORPTION = define("absorption");
/* 108 */   public static final PotionType SATURATION = define("saturation");
/*     */ 
/*     */   
/* 111 */   public static final PotionType GLOWING = define("glowing");
/* 112 */   public static final PotionType LEVITATION = define("levitation");
/* 113 */   public static final PotionType LUCK = define("luck");
/* 114 */   public static final PotionType UNLUCK = define("unluck");
/*     */ 
/*     */   
/* 117 */   public static final PotionType SLOW_FALLING = define("slow_falling");
/* 118 */   public static final PotionType CONDUIT_POWER = define("conduit_power");
/* 119 */   public static final PotionType DOLPHINS_GRACE = define("dolphins_grace");
/*     */ 
/*     */   
/* 122 */   public static final PotionType BAD_OMEN = define("bad_omen");
/* 123 */   public static final PotionType HERO_OF_THE_VILLAGE = define("hero_of_the_village");
/*     */ 
/*     */   
/* 126 */   public static final PotionType DARKNESS = define("darkness");
/*     */ 
/*     */   
/* 129 */   public static final PotionType TRIAL_OMEN = define("trial_omen");
/* 130 */   public static final PotionType RAID_OMEN = define("raid_omen");
/* 131 */   public static final PotionType WIND_CHARGED = define("wind_charged");
/* 132 */   public static final PotionType WEAVING = define("weaving");
/* 133 */   public static final PotionType OOZING = define("oozing");
/* 134 */   public static final PotionType INFESTED = define("infested");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Collection<PotionType> values() {
/* 142 */     return REGISTRY.getEntries();
/*     */   }
/*     */   
/*     */   static {
/* 146 */     REGISTRY.unloadMappings();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\potion\PotionTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */