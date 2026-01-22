/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class EnchantEffectComponentTypes
/*     */ {
/*  35 */   private static final VersionedRegistry<ComponentType<?>> REGISTRY = new VersionedRegistry("enchantment_effect_component_type"); public static ComponentType<NBT> DAMAGE_PROTECTION; public static ComponentType<NBT> DAMAGE_IMMUNITY; public static ComponentType<NBT> DAMAGE; public static ComponentType<NBT> SMASH_DAMAGE_PER_FALLEN_BLOCK; public static ComponentType<NBT> KNOCKBACK; public static ComponentType<NBT> ARMOR_EFFECTIVENESS; public static ComponentType<NBT> POST_ATTACK; public static ComponentType<NBT> HIT_BLOCK; public static ComponentType<NBT> ITEM_DAMAGE; public static ComponentType<NBT> ATTRIBUTES; public static ComponentType<NBT> EQUIPMENT_DROPS; public static ComponentType<NBT> LOCATION_CHANGED;
/*     */   public static ComponentType<NBT> TICK;
/*     */   public static ComponentType<NBT> AMMO_USE;
/*     */   public static ComponentType<NBT> PROJECTILE_PIERCING;
/*     */   
/*     */   @Internal
/*     */   public static <T> ComponentType<T> define(String key) {
/*  42 */     return define(key, null, null);
/*     */   }
/*     */   public static ComponentType<NBT> PROJECTILE_SPAWNED; public static ComponentType<NBT> PROJECTILE_SPREAD; public static ComponentType<NBT> PROJECTILE_COUNT; public static ComponentType<NBT> TRIDENT_RETURN_ACCELERATION; public static ComponentType<NBT> FISHING_TIME_REDUCTION; public static ComponentType<NBT> FISHING_LUCK_BONUS; public static ComponentType<NBT> BLOCK_EXPERIENCE; public static ComponentType<NBT> MOB_EXPERIENCE; public static ComponentType<NBT> REPAIR_WITH_XP; public static ComponentType<NBT> CROSSBOW_CHARGE_TIME; public static ComponentType<NBT> CROSSBOW_CHARGING_SOUNDS; public static ComponentType<NBT> TRIDENT_SOUND; public static ComponentType<NBT> PREVENT_EQUIPMENT_DROP; public static ComponentType<NBT> PREVENT_ARMOR_CHANGE; public static ComponentType<NBT> TRIDENT_SPIN_ATTACK_STRENGTH;
/*     */   @Internal
/*     */   public static <T> ComponentType<T> define(String key, @Nullable ComponentType.Decoder<T> reader, @Nullable ComponentType.Encoder<T> writer) {
/*  47 */     return (ComponentType<T>)REGISTRY.define(key, data -> new StaticComponentType(data, reader, writer));
/*     */   }
/*     */   
/*     */   public static VersionedRegistry<ComponentType<?>> getRegistry() {
/*  51 */     return REGISTRY;
/*     */   }
/*     */   
/*     */   static {
/*  55 */     DAMAGE_PROTECTION = define("damage_protection", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/*  57 */     DAMAGE_IMMUNITY = define("damage_immunity", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/*  59 */     DAMAGE = define("damage", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/*  61 */     SMASH_DAMAGE_PER_FALLEN_BLOCK = define("smash_damage_per_fallen_block", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/*  63 */     KNOCKBACK = define("knockback", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/*  65 */     ARMOR_EFFECTIVENESS = define("armor_effectiveness", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/*  67 */     POST_ATTACK = define("post_attack", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/*  69 */     HIT_BLOCK = define("hit_block", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/*  71 */     ITEM_DAMAGE = define("item_damage", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/*  73 */     ATTRIBUTES = define("attributes", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/*  75 */     EQUIPMENT_DROPS = define("equipment_drops", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/*  77 */     LOCATION_CHANGED = define("location_changed", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/*  79 */     TICK = define("tick", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/*  81 */     AMMO_USE = define("ammo_use", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/*  83 */     PROJECTILE_PIERCING = define("projectile_piercing", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/*  85 */     PROJECTILE_SPAWNED = define("projectile_spawned", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/*  87 */     PROJECTILE_SPREAD = define("projectile_spread", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/*  89 */     PROJECTILE_COUNT = define("projectile_count", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/*  91 */     TRIDENT_RETURN_ACCELERATION = define("trident_return_acceleration", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/*  93 */     FISHING_TIME_REDUCTION = define("fishing_time_reduction", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/*  95 */     FISHING_LUCK_BONUS = define("fishing_luck_bonus", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/*  97 */     BLOCK_EXPERIENCE = define("block_experience", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/*  99 */     MOB_EXPERIENCE = define("mob_experience", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/* 101 */     REPAIR_WITH_XP = define("repair_with_xp", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/* 103 */     CROSSBOW_CHARGE_TIME = define("crossbow_charge_time", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/* 105 */     CROSSBOW_CHARGING_SOUNDS = define("crossbow_charging_sounds", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/* 107 */     TRIDENT_SOUND = define("trident_sound", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/* 109 */     PREVENT_EQUIPMENT_DROP = define("prevent_equipment_drop", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/* 111 */     PREVENT_ARMOR_CHANGE = define("prevent_armor_change", (nbt, version) -> nbt, (val, version) -> val);
/*     */     
/* 113 */     TRIDENT_SPIN_ATTACK_STRENGTH = define("trident_spin_attack_strength", (nbt, version) -> nbt, (val, version) -> val);
/*     */ 
/*     */ 
/*     */     
/* 117 */     REGISTRY.unloadMappings();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\EnchantEffectComponentTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */