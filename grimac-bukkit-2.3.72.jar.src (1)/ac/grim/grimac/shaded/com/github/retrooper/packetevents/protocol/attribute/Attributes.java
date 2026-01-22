/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
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
/*     */ public final class Attributes
/*     */ {
/*  29 */   private static final VersionedRegistry<Attribute> REGISTRY = new VersionedRegistry("attribute");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Attribute define(String key, @Nullable String legacyPrefix, double def, double min, double max) {
/*  38 */     return (Attribute)REGISTRY.define(key, data -> new StaticAttribute(data, legacyPrefix, def, min, max));
/*     */   }
/*     */ 
/*     */   
/*     */   public static VersionedRegistry<Attribute> getRegistry() {
/*  43 */     return REGISTRY;
/*     */   }
/*     */ 
/*     */   
/*     */   public static Attribute getByName(String name) {
/*  48 */     String normedName = ResourceLocation.normString(name);
/*  49 */     if (normedName.startsWith("minecraft:generic.") || normedName
/*  50 */       .startsWith("minecraft:player.") || normedName
/*  51 */       .startsWith("minecraft:zombie.")) {
/*  52 */       normedName = normedName.substring(normedName.indexOf('.') + 1);
/*     */     }
/*  54 */     return (Attribute)REGISTRY.getByName(normedName);
/*     */   }
/*     */   
/*     */   public static Attribute getById(ClientVersion version, int id) {
/*  58 */     return (Attribute)REGISTRY.getById(version, id);
/*     */   }
/*     */   
/*  61 */   public static final Attribute ARMOR = define("armor", "generic", 0.0D, 0.0D, 30.0D);
/*     */   
/*  63 */   public static final Attribute ARMOR_TOUGHNESS = define("armor_toughness", "generic", 0.0D, 0.0D, 20.0D);
/*     */   
/*  65 */   public static final Attribute ATTACK_DAMAGE = define("attack_damage", "generic", 2.0D, 0.0D, 2048.0D);
/*     */   
/*  67 */   public static final Attribute ATTACK_KNOCKBACK = define("attack_knockback", "generic", 0.0D, 0.0D, 5.0D);
/*     */   
/*  69 */   public static final Attribute ATTACK_SPEED = define("attack_speed", "generic", 4.0D, 0.0D, 1024.0D);
/*     */   
/*  71 */   public static final Attribute FLYING_SPEED = define("flying_speed", "generic", 0.4D, 0.0D, 1024.0D);
/*     */   
/*  73 */   public static final Attribute FOLLOW_RANGE = define("follow_range", "generic", 32.0D, 0.0D, 2048.0D);
/*     */   
/*  75 */   public static final Attribute KNOCKBACK_RESISTANCE = define("knockback_resistance", "generic", 0.0D, 0.0D, 1.0D);
/*     */   
/*  77 */   public static final Attribute LUCK = define("luck", "generic", 0.0D, -1024.0D, 1024.0D);
/*     */   
/*  79 */   public static final Attribute MAX_HEALTH = define("max_health", "generic", 20.0D, 1.0D, 1024.0D);
/*     */   
/*  81 */   public static final Attribute MOVEMENT_SPEED = define("movement_speed", "generic", 0.7D, 0.0D, 1024.0D);
/*     */   
/*  83 */   public static final Attribute SPAWN_REINFORCEMENTS = define("spawn_reinforcements", "zombie", 0.0D, 0.0D, 1.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  89 */   public static final Attribute MAX_ABSORPTION = define("max_absorption", "generic", 0.0D, 0.0D, 2048.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  95 */   public static final Attribute BLOCK_BREAK_SPEED = define("block_break_speed", "player", 1.0D, 0.0D, 1024.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 100 */   public static final Attribute BLOCK_INTERACTION_RANGE = define("block_interaction_range", "player", 4.5D, 0.0D, 64.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 105 */   public static final Attribute ENTITY_INTERACTION_RANGE = define("entity_interaction_range", "player", 3.0D, 0.0D, 64.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 110 */   public static final Attribute FALL_DAMAGE_MULTIPLIER = define("fall_damage_multiplier", "generic", 1.0D, 0.0D, 100.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 115 */   public static final Attribute GRAVITY = define("gravity", "generic", 0.08D, -1.0D, 1.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 120 */   public static final Attribute JUMP_STRENGTH = define("jump_strength", "generic", 0.42D, 0.0D, 32.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 125 */   public static final Attribute SAFE_FALL_DISTANCE = define("safe_fall_distance", "generic", 3.0D, 0.0D, 1024.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 130 */   public static final Attribute SCALE = define("scale", "generic", 1.0D, 0.0625D, 16.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 135 */   public static final Attribute STEP_HEIGHT = define("step_height", "generic", 0.6D, 0.0D, 10.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 141 */   public static final Attribute BURNING_TIME = define("burning_time", "generic", 0.0D, 1.0D, 1024.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 146 */   public static final Attribute EXPLOSION_KNOCKBACK_RESISTANCE = define("explosion_knockback_resistance", "generic", 0.0D, 0.0D, 1.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 151 */   public static final Attribute MINING_EFFICIENCY = define("mining_efficiency", "player", 0.0D, 0.0D, 1024.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 156 */   public static final Attribute MOVEMENT_EFFICIENCY = define("movement_efficiency", "generic", 0.0D, 0.0D, 1.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 161 */   public static final Attribute OXYGEN_BONUS = define("oxygen_bonus", "generic", 0.0D, 0.0D, 1024.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 166 */   public static final Attribute SNEAKING_SPEED = define("sneaking_speed", "player", 0.3D, 0.0D, 1.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 171 */   public static final Attribute SUBMERGED_MINING_SPEED = define("submerged_mining_speed", "player", 0.2D, 0.0D, 20.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 176 */   public static final Attribute SWEEPING_DAMAGE_RATIO = define("sweeping_damage_ratio", "player", 0.0D, 0.0D, 1.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 181 */   public static final Attribute WATER_MOVEMENT_EFFICIENCY = define("water_movement_efficiency", "generic", 0.0D, 0.0D, 1.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 187 */   public static final Attribute TEMPT_RANGE = define("tempt_range", null, 10.0D, 0.0D, 2048.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 193 */   public static final Attribute CAMERA_DISTANCE = define("camera_distance", null, 4.0D, 0.0D, 32.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 198 */   public static final Attribute WAYPOINT_TRANSMIT_RANGE = define("waypoint_transmit_range", null, 0.0D, 0.0D, 6.0E7D);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 203 */   public static final Attribute WAYPOINT_RECEIVE_RANGE = define("waypoint_receive_range", null, 0.0D, 0.0D, 6.0E7D);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/* 210 */   public static final Attribute HORSE_JUMP_STRENGTH = define("horse.jump_strength", null, 0.7D, 0.0D, 2.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 217 */   public static final Attribute GENERIC_ARMOR = ARMOR;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 222 */   public static final Attribute GENERIC_ARMOR_TOUGHNESS = ARMOR_TOUGHNESS;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 227 */   public static final Attribute GENERIC_ATTACK_DAMAGE = ATTACK_DAMAGE;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 232 */   public static final Attribute GENERIC_ATTACK_KNOCKBACK = ATTACK_KNOCKBACK;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 237 */   public static final Attribute GENERIC_ATTACK_SPEED = ATTACK_SPEED;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 242 */   public static final Attribute GENERIC_FLYING_SPEED = FLYING_SPEED;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 247 */   public static final Attribute GENERIC_FOLLOW_RANGE = FOLLOW_RANGE;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 252 */   public static final Attribute GENERIC_KNOCKBACK_RESISTANCE = KNOCKBACK_RESISTANCE;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 257 */   public static final Attribute GENERIC_LUCK = LUCK;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 262 */   public static final Attribute GENERIC_MAX_HEALTH = MAX_HEALTH;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 267 */   public static final Attribute GENERIC_MOVEMENT_SPEED = MOVEMENT_SPEED;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 272 */   public static final Attribute ZOMBIE_SPAWN_REINFORCEMENTS = SPAWN_REINFORCEMENTS;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 277 */   public static final Attribute GENERIC_MAX_ABSORPTION = MAX_ABSORPTION;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 282 */   public static final Attribute PLAYER_BLOCK_BREAK_SPEED = BLOCK_BREAK_SPEED;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 287 */   public static final Attribute PLAYER_BLOCK_INTERACTION_RANGE = BLOCK_INTERACTION_RANGE;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 292 */   public static final Attribute PLAYER_ENTITY_INTERACTION_RANGE = ENTITY_INTERACTION_RANGE;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 297 */   public static final Attribute GENERIC_FALL_DAMAGE_MULTIPLIER = FALL_DAMAGE_MULTIPLIER;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 302 */   public static final Attribute GENERIC_GRAVITY = GRAVITY;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 307 */   public static final Attribute GENERIC_JUMP_STRENGTH = JUMP_STRENGTH;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 312 */   public static final Attribute GENERIC_SAFE_FALL_DISTANCE = SAFE_FALL_DISTANCE;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 317 */   public static final Attribute GENERIC_SCALE = SCALE;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 322 */   public static final Attribute GENERIC_STEP_HEIGHT = STEP_HEIGHT;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 327 */   public static final Attribute GENERIC_BURNING_TIME = BURNING_TIME;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 332 */   public static final Attribute GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE = EXPLOSION_KNOCKBACK_RESISTANCE;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 337 */   public static final Attribute PLAYER_MINING_EFFICIENCY = MINING_EFFICIENCY;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 342 */   public static final Attribute GENERIC_MOVEMENT_EFFICIENCY = MOVEMENT_EFFICIENCY;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 347 */   public static final Attribute GENERIC_OXYGEN_BONUS = OXYGEN_BONUS;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 352 */   public static final Attribute PLAYER_SNEAKING_SPEED = SNEAKING_SPEED;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 357 */   public static final Attribute PLAYER_SUBMERGED_MINING_SPEED = SUBMERGED_MINING_SPEED;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 362 */   public static final Attribute PLAYER_SWEEPING_DAMAGE_RATIO = SWEEPING_DAMAGE_RATIO;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 367 */   public static final Attribute GENERIC_WATER_MOVEMENT_EFFICIENCY = WATER_MOVEMENT_EFFICIENCY;
/*     */   
/*     */   static {
/* 370 */     REGISTRY.unloadMappings();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\attribute\Attributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */