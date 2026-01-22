/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.enchantment.type;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.serializer.SequentialNBTReader;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.MappingHelper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ public final class EnchantmentTypes
/*     */ {
/*  38 */   private static final Map<String, String> STRING_UPDATER = new HashMap<>();
/*     */ 
/*     */   
/*     */   static {
/*  42 */     STRING_UPDATER.put("minecraft:sweeping", "minecraft:sweeping_edge");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  49 */   private static final Map<ResourceLocation, NBTCompound> ENCHANTMENT_DATA = new HashMap<>(); static { 
/*  50 */     try { SequentialNBTReader.Compound dataTag = MappingHelper.decompress("mappings/data/enchantment"); 
/*  51 */       try { dataTag.skipOne();
/*  52 */         for (Map.Entry<String, NBT> entry : (Iterable<Map.Entry<String, NBT>>)dataTag.next().getValue()) {
/*  53 */           ResourceLocation enchantKey = new ResourceLocation(entry.getKey());
/*  54 */           ENCHANTMENT_DATA.put(enchantKey, ((SequentialNBTReader.Compound)entry.getValue()).readFully());
/*     */         } 
/*  56 */         if (dataTag != null) dataTag.close();  } catch (Throwable throwable) { if (dataTag != null) try { dataTag.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (IOException exception)
/*  57 */     { throw new RuntimeException("Error while reading enchantment type data", exception); }
/*     */      }
/*     */ 
/*     */   
/*  61 */   private static final VersionedRegistry<EnchantmentType> REGISTRY = new VersionedRegistry("enchantment");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public static EnchantmentType define(String key) {
/*  68 */     PacketWrapper<?> wrapper = PacketWrapper.createDummyWrapper(ClientVersion.getLatest());
/*  69 */     return (EnchantmentType)REGISTRY.define(key, data -> {
/*     */           NBTCompound dataTag = ENCHANTMENT_DATA.get(data.getName());
/*     */           if (dataTag == null) {
/*     */             throw new IllegalArgumentException("Can't define enchantment " + data.getName() + ", no data found");
/*     */           }
/*     */           return EnchantmentType.decode((NBT)dataTag, wrapper, data);
/*     */         });
/*     */   }
/*     */   
/*     */   public static VersionedRegistry<EnchantmentType> getRegistry() {
/*  79 */     return REGISTRY;
/*     */   }
/*     */   @Nullable
/*     */   public static EnchantmentType getByName(String name) {
/*  83 */     String fixedName = STRING_UPDATER.getOrDefault(name, name);
/*  84 */     return (EnchantmentType)REGISTRY.getByName(fixedName);
/*     */   }
/*     */   @Nullable
/*     */   public static EnchantmentType getById(ClientVersion version, int id) {
/*  88 */     return (EnchantmentType)REGISTRY.getById(version, id);
/*     */   }
/*     */   
/*  91 */   public static final EnchantmentType ALL_DAMAGE_PROTECTION = define("protection");
/*  92 */   public static final EnchantmentType FIRE_PROTECTION = define("fire_protection");
/*  93 */   public static final EnchantmentType FALL_PROTECTION = define("feather_falling");
/*  94 */   public static final EnchantmentType BLAST_PROTECTION = define("blast_protection");
/*  95 */   public static final EnchantmentType PROJECTILE_PROTECTION = define("projectile_protection");
/*  96 */   public static final EnchantmentType RESPIRATION = define("respiration");
/*  97 */   public static final EnchantmentType AQUA_AFFINITY = define("aqua_affinity");
/*  98 */   public static final EnchantmentType THORNS = define("thorns");
/*  99 */   public static final EnchantmentType DEPTH_STRIDER = define("depth_strider");
/* 100 */   public static final EnchantmentType FROST_WALKER = define("frost_walker");
/* 101 */   public static final EnchantmentType BINDING_CURSE = define("binding_curse");
/* 102 */   public static final EnchantmentType SOUL_SPEED = define("soul_speed");
/* 103 */   public static final EnchantmentType SWIFT_SNEAK = define("swift_sneak");
/* 104 */   public static final EnchantmentType SHARPNESS = define("sharpness");
/* 105 */   public static final EnchantmentType SMITE = define("smite");
/* 106 */   public static final EnchantmentType BANE_OF_ARTHROPODS = define("bane_of_arthropods");
/* 107 */   public static final EnchantmentType KNOCKBACK = define("knockback");
/* 108 */   public static final EnchantmentType FIRE_ASPECT = define("fire_aspect");
/* 109 */   public static final EnchantmentType MOB_LOOTING = define("looting");
/* 110 */   public static final EnchantmentType SWEEPING_EDGE = define("sweeping_edge");
/* 111 */   public static final EnchantmentType BLOCK_EFFICIENCY = define("efficiency");
/* 112 */   public static final EnchantmentType SILK_TOUCH = define("silk_touch");
/* 113 */   public static final EnchantmentType UNBREAKING = define("unbreaking");
/* 114 */   public static final EnchantmentType BLOCK_FORTUNE = define("fortune");
/* 115 */   public static final EnchantmentType POWER_ARROWS = define("power");
/* 116 */   public static final EnchantmentType PUNCH_ARROWS = define("punch");
/* 117 */   public static final EnchantmentType FLAMING_ARROWS = define("flame");
/* 118 */   public static final EnchantmentType INFINITY_ARROWS = define("infinity");
/* 119 */   public static final EnchantmentType FISHING_LUCK = define("luck_of_the_sea");
/* 120 */   public static final EnchantmentType FISHING_SPEED = define("lure");
/* 121 */   public static final EnchantmentType LOYALTY = define("loyalty");
/* 122 */   public static final EnchantmentType IMPALING = define("impaling");
/* 123 */   public static final EnchantmentType RIPTIDE = define("riptide");
/* 124 */   public static final EnchantmentType CHANNELING = define("channeling");
/* 125 */   public static final EnchantmentType MULTISHOT = define("multishot");
/* 126 */   public static final EnchantmentType QUICK_CHARGE = define("quick_charge");
/* 127 */   public static final EnchantmentType PIERCING = define("piercing");
/* 128 */   public static final EnchantmentType MENDING = define("mending");
/* 129 */   public static final EnchantmentType VANISHING_CURSE = define("vanishing_curse");
/*     */ 
/*     */   
/* 132 */   public static final EnchantmentType DENSITY = define("density");
/* 133 */   public static final EnchantmentType BREACH = define("breach");
/* 134 */   public static final EnchantmentType WIND_BURST = define("wind_burst");
/*     */   
/*     */   static {
/* 137 */     ENCHANTMENT_DATA.clear();
/* 138 */     REGISTRY.unloadMappings();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\enchantment\type\EnchantmentTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */