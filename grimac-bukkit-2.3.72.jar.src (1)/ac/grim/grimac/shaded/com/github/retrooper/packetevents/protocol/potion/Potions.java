/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
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
/*     */ public final class Potions
/*     */ {
/*  35 */   private static final VersionedRegistry<Potion> REGISTRY = new VersionedRegistry("potion");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static VersionedRegistry<Potion> getRegistry() {
/*  41 */     return REGISTRY;
/*     */   }
/*     */   
/*     */   @Internal
/*     */   public static Potion define(String name) {
/*  46 */     return (Potion)REGISTRY.define(name, StaticPotion::new);
/*     */   }
/*     */   @Nullable
/*     */   public static Potion getByName(String name) {
/*  50 */     return (Potion)REGISTRY.getByName(name);
/*     */   }
/*     */   @Nullable
/*     */   public static Potion getById(ClientVersion version, int id) {
/*  54 */     return (Potion)REGISTRY.getById(version, id);
/*     */   }
/*     */ 
/*     */   
/*  58 */   public static final Potion WATER = define("water");
/*  59 */   public static final Potion MUNDANE = define("mundane");
/*  60 */   public static final Potion THICK = define("thick");
/*  61 */   public static final Potion AWKWARD = define("awkward");
/*  62 */   public static final Potion NIGHT_VISION = define("night_vision");
/*  63 */   public static final Potion LONG_NIGHT_VISION = define("long_night_vision");
/*  64 */   public static final Potion INVISIBILITY = define("invisibility");
/*  65 */   public static final Potion LONG_INVISIBILITY = define("long_invisibility");
/*  66 */   public static final Potion LEAPING = define("leaping");
/*  67 */   public static final Potion LONG_LEAPING = define("long_leaping");
/*  68 */   public static final Potion STRONG_LEAPING = define("strong_leaping");
/*  69 */   public static final Potion FIRE_RESISTANCE = define("fire_resistance");
/*  70 */   public static final Potion LONG_FIRE_RESISTANCE = define("long_fire_resistance");
/*  71 */   public static final Potion SWIFTNESS = define("swiftness");
/*  72 */   public static final Potion LONG_SWIFTNESS = define("long_swiftness");
/*  73 */   public static final Potion STRONG_SWIFTNESS = define("strong_swiftness");
/*  74 */   public static final Potion SLOWNESS = define("slowness");
/*  75 */   public static final Potion LONG_SLOWNESS = define("long_slowness");
/*  76 */   public static final Potion STRONG_SLOWNESS = define("strong_slowness");
/*  77 */   public static final Potion TURTLE_MASTER = define("turtle_master");
/*  78 */   public static final Potion LONG_TURTLE_MASTER = define("long_turtle_master");
/*  79 */   public static final Potion STRONG_TURTLE_MASTER = define("strong_turtle_master");
/*  80 */   public static final Potion WATER_BREATHING = define("water_breathing");
/*  81 */   public static final Potion LONG_WATER_BREATHING = define("long_water_breathing");
/*  82 */   public static final Potion HEALING = define("healing");
/*  83 */   public static final Potion STRONG_HEALING = define("strong_healing");
/*  84 */   public static final Potion HARMING = define("harming");
/*  85 */   public static final Potion STRONG_HARMING = define("strong_harming");
/*  86 */   public static final Potion POISON = define("poison");
/*  87 */   public static final Potion LONG_POISON = define("long_poison");
/*  88 */   public static final Potion STRONG_POISON = define("strong_poison");
/*  89 */   public static final Potion REGENERATION = define("regeneration");
/*  90 */   public static final Potion LONG_REGENERATION = define("long_regeneration");
/*  91 */   public static final Potion STRONG_REGENERATION = define("strong_regeneration");
/*  92 */   public static final Potion STRENGTH = define("strength");
/*  93 */   public static final Potion LONG_STRENGTH = define("long_strength");
/*  94 */   public static final Potion STRONG_STRENGTH = define("strong_strength");
/*  95 */   public static final Potion WEAKNESS = define("weakness");
/*  96 */   public static final Potion LONG_WEAKNESS = define("long_weakness");
/*  97 */   public static final Potion LUCK = define("luck");
/*  98 */   public static final Potion SLOW_FALLING = define("slow_falling");
/*  99 */   public static final Potion LONG_SLOW_FALLING = define("long_slow_falling");
/* 100 */   public static final Potion WIND_CHARGED = define("wind_charged");
/* 101 */   public static final Potion WEAVING = define("weaving");
/* 102 */   public static final Potion OOZING = define("oozing");
/* 103 */   public static final Potion INFESTED = define("infested");
/*     */   
/*     */   static {
/* 106 */     REGISTRY.unloadMappings();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\potion\Potions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */