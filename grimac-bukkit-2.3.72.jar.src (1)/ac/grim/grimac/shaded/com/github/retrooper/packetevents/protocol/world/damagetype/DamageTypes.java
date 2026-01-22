/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.damagetype;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
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
/*     */ public final class DamageTypes
/*     */ {
/*  29 */   private static final VersionedRegistry<DamageType> REGISTRY = new VersionedRegistry("damage_type");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public static DamageType define(String key, String messageId, float exhaustion) {
/*  36 */     return define(key, messageId, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, exhaustion, DamageEffects.HURT, DeathMessageType.DEFAULT);
/*     */   }
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public static DamageType define(String key, String messageId, float exhaustion, DamageEffects damageEffects) {
/*  42 */     return define(key, messageId, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, exhaustion, damageEffects, DeathMessageType.DEFAULT);
/*     */   }
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public static DamageType define(String key, String messageId, DamageScaling scaling, float exhaustion) {
/*  48 */     return define(key, messageId, scaling, exhaustion, DamageEffects.HURT, DeathMessageType.DEFAULT);
/*     */   }
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public static DamageType define(String key, String messageId, DamageScaling scaling, float exhaustion, DamageEffects damageEffects, DeathMessageType deathMessageType) {
/*  54 */     return (DamageType)REGISTRY.define(key, data -> new StaticDamageType(data, messageId, scaling, exhaustion, damageEffects, deathMessageType));
/*     */   }
/*     */ 
/*     */   
/*     */   public static DamageType getByName(String name) {
/*  59 */     return (DamageType)REGISTRY.getByName(name);
/*     */   }
/*     */   
/*     */   public static DamageType getById(ClientVersion version, int id) {
/*  63 */     return (DamageType)REGISTRY.getById(version, id);
/*     */   }
/*     */   
/*     */   public static VersionedRegistry<DamageType> getRegistry() {
/*  67 */     return REGISTRY;
/*     */   }
/*     */   
/*  70 */   public static final DamageType ARROW = define("arrow", "arrow", 0.1F);
/*  71 */   public static final DamageType BAD_RESPAWN_POINT = define("bad_respawn_point", "badRespawnPoint", DamageScaling.ALWAYS, 0.1F, DamageEffects.HURT, DeathMessageType.INTENTIONAL_GAME_DESIGN);
/*     */   
/*  73 */   public static final DamageType CACTUS = define("cactus", "cactus", 0.1F);
/*  74 */   public static final DamageType CAMPFIRE = define("campfire", "inFire", 0.1F, DamageEffects.BURNING);
/*  75 */   public static final DamageType CRAMMING = define("cramming", "cramming", 0.0F);
/*  76 */   public static final DamageType DRAGON_BREATH = define("dragon_breath", "dragonBreath", 0.0F);
/*  77 */   public static final DamageType DROWN = define("drown", "drown", 0.0F, DamageEffects.DROWNING);
/*  78 */   public static final DamageType DRY_OUT = define("dry_out", "dryout", 0.1F);
/*  79 */   public static final DamageType EXPLOSION = define("explosion", "explosion", DamageScaling.ALWAYS, 0.1F);
/*  80 */   public static final DamageType FALL = define("fall", "fall", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F, DamageEffects.HURT, DeathMessageType.FALL_VARIANTS);
/*     */   
/*  82 */   public static final DamageType FALLING_ANVIL = define("falling_anvil", "anvil", 0.1F);
/*  83 */   public static final DamageType FALLING_BLOCK = define("falling_block", "fallingBlock", 0.1F);
/*  84 */   public static final DamageType FALLING_STALACTITE = define("falling_stalactite", "fallingStalactite", 0.1F);
/*  85 */   public static final DamageType FELL_OUT_OF_WORLD = define("out_of_world", "outOfWorld", 0.0F);
/*  86 */   public static final DamageType FIREBALL = define("fireball", "fireball", 0.1F, DamageEffects.BURNING);
/*  87 */   public static final DamageType FIREWORKS = define("fireworks", "fireworks", 0.1F);
/*  88 */   public static final DamageType FLY_INTO_WALL = define("fly_into_wall", "flyIntoWall", 0.0F);
/*  89 */   public static final DamageType FREEZE = define("freeze", "freeze", 0.0F, DamageEffects.FREEZING);
/*  90 */   public static final DamageType GENERIC = define("generic", "generic", 0.0F);
/*  91 */   public static final DamageType GENERIC_KILL = define("generic_kill", "genericKill", 0.0F);
/*  92 */   public static final DamageType HOT_FLOOR = define("hot_floor", "hotFloor", 0.1F, DamageEffects.BURNING);
/*  93 */   public static final DamageType IN_FIRE = define("in_fire", "inFire", 0.1F, DamageEffects.BURNING);
/*  94 */   public static final DamageType IN_WALL = define("in_wall", "inWall", 0.0F);
/*  95 */   public static final DamageType INDIRECT_MAGIC = define("indirect_magic", "indirectMagic", 0.0F);
/*  96 */   public static final DamageType LAVA = define("lava", "lava", 0.1F, DamageEffects.BURNING);
/*  97 */   public static final DamageType LIGHTNING_BOLT = define("lightning_bolt", "lightningBolt", 0.1F);
/*  98 */   public static final DamageType MAGIC = define("magic", "magic", 0.0F);
/*  99 */   public static final DamageType MOB_ATTACK = define("mob_attack", "mob", 0.1F);
/* 100 */   public static final DamageType MOB_ATTACK_NO_AGGRO = define("mob_attack_no_aggro", "mob", 0.1F);
/* 101 */   public static final DamageType MOB_PROJECTILE = define("mob_projectile", "mob", 0.1F);
/* 102 */   public static final DamageType ON_FIRE = define("on_fire", "onFire", 0.0F, DamageEffects.BURNING);
/* 103 */   public static final DamageType OUTSIDE_BORDER = define("outside_border", "outsideBorder", 0.0F);
/* 104 */   public static final DamageType PLAYER_ATTACK = define("player_attack", "player", 0.1F);
/* 105 */   public static final DamageType PLAYER_EXPLOSION = define("player_explosion", "explosion.player", DamageScaling.ALWAYS, 0.1F);
/*     */   
/* 107 */   public static final DamageType SONIC_BOOM = define("sonic_boom", "sonic_boom", DamageScaling.ALWAYS, 0.0F);
/* 108 */   public static final DamageType SPIT = define("spit", "mob", 0.1F);
/* 109 */   public static final DamageType STALAGMITE = define("stalagmite", "stalagmite", 0.0F);
/* 110 */   public static final DamageType STARVE = define("starve", "starve", 0.0F);
/* 111 */   public static final DamageType STING = define("sting", "sting", 0.1F);
/* 112 */   public static final DamageType SWEET_BERRY_BUSH = define("sweet_berry_bush", "sweetBerryBush", 0.1F, DamageEffects.POKING);
/*     */   
/* 114 */   public static final DamageType THORNS = define("thorns", "thorns", 0.1F, DamageEffects.THORNS);
/* 115 */   public static final DamageType THROWN = define("thrown", "thrown", 0.1F);
/* 116 */   public static final DamageType TRIDENT = define("trident", "trident", 0.1F);
/* 117 */   public static final DamageType UNATTRIBUTED_FIREBALL = define("unattributed_fireball", "onFire", 0.1F, DamageEffects.BURNING);
/*     */   
/* 119 */   public static final DamageType WIND_CHARGE = define("wind_charge", "mob", 0.1F);
/* 120 */   public static final DamageType WITHER = define("wither", "wither", 0.0F);
/* 121 */   public static final DamageType WITHER_SKULL = define("wither_skull", "witherSkull", 0.1F);
/*     */ 
/*     */   
/* 124 */   public static final DamageType ENDER_PEARL = define("ender_pearl", "fall", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F, DamageEffects.HURT, DeathMessageType.FALL_VARIANTS);
/*     */   
/* 126 */   public static final DamageType MACE_SMASH = define("mace_smash", "mace_smash", 0.1F);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Collection<DamageType> values() {
/* 134 */     return REGISTRY.getEntries();
/*     */   }
/*     */   
/*     */   static {
/* 138 */     REGISTRY.unloadMappings();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\damagetype\DamageTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */