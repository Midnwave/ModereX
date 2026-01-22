/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.ChatType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.ChatTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.Dialog;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.Dialogs;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.cat.CatVariant;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.cat.CatVariants;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.chicken.ChickenVariant;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.chicken.ChickenVariants;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.cow.CowVariant;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.cow.CowVariants;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.frog.FrogVariant;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.frog.FrogVariants;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.pig.PigVariant;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.pig.PigVariants;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.wolfvariant.WolfSoundVariant;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.wolfvariant.WolfSoundVariants;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.wolfvariant.WolfVariant;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.wolfvariant.WolfVariants;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.banner.BannerPattern;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.banner.BannerPatterns;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.instrument.Instrument;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.instrument.Instruments;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.jukebox.IJukeboxSong;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.jukebox.JukeboxSongs;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.trimmaterial.TrimMaterial;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.trimmaterial.TrimMaterials;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.trimpattern.TrimPattern;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.trimpattern.TrimPatterns;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.CopyableEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.DeepComparableEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.ResolvableEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTList;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.biome.Biome;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.biome.Biomes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.damagetype.DamageType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.damagetype.DamageTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.dimension.DimensionType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.dimension.DimensionTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.painting.PaintingVariant;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.painting.PaintingVariants;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.configuration.server.WrapperConfigServerRegistryData;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.stream.Stream;
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
/*     */ @Internal
/*     */ public final class SynchronizedRegistriesHandler
/*     */ {
/*  85 */   private static final boolean FORCE_PER_USER_REGISTRIES = Boolean.getBoolean("packetevents.force-per-user-registries");
/*  86 */   private static final Map<ResourceLocation, RegistryEntry<?>> REGISTRY_KEYS = new HashMap<>();
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*  91 */     Stream.<RegistryEntry>of(new RegistryEntry[] { new RegistryEntry<>(
/*  92 */             Biomes.getRegistry(), Biome::decode), new RegistryEntry<>(
/*  93 */             ChatTypes.getRegistry(), ChatType::decode), new RegistryEntry<>(
/*  94 */             TrimPatterns.getRegistry(), TrimPattern::decode), new RegistryEntry<>(
/*  95 */             TrimMaterials.getRegistry(), TrimMaterial::decode), new RegistryEntry<>(
/*  96 */             WolfVariants.getRegistry(), WolfVariant::decode), new RegistryEntry<>(
/*  97 */             WolfSoundVariants.getRegistry(), WolfSoundVariant::decode), new RegistryEntry<>(
/*  98 */             PigVariants.getRegistry(), PigVariant::decode), new RegistryEntry<>(
/*  99 */             FrogVariants.getRegistry(), FrogVariant::decode), new RegistryEntry<>(
/* 100 */             CatVariants.getRegistry(), CatVariant::decode), new RegistryEntry<>(
/* 101 */             CowVariants.getRegistry(), CowVariant::decode), new RegistryEntry<>(
/* 102 */             ChickenVariants.getRegistry(), ChickenVariant::decode), new RegistryEntry<>(
/* 103 */             PaintingVariants.getRegistry(), PaintingVariant::decode), new RegistryEntry<>(
/* 104 */             DimensionTypes.getRegistry(), DimensionType::decode), new RegistryEntry<>(
/* 105 */             DamageTypes.getRegistry(), DamageType::decode), new RegistryEntry<>(
/* 106 */             BannerPatterns.getRegistry(), BannerPattern::decode), new RegistryEntry<>(
/* 107 */             EnchantmentTypes.getRegistry(), EnchantmentType::decode), new RegistryEntry<>(
/* 108 */             JukeboxSongs.getRegistry(), IJukeboxSong::decode), new RegistryEntry<>(
/* 109 */             Instruments.getRegistry(), Instrument::decode), new RegistryEntry<>(
/* 110 */             Dialogs.getRegistry(), Dialog::decodeDirect)
/* 111 */         }).forEach(entry -> REGISTRY_KEYS.put(entry.getRegistryKey(), entry));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static RegistryEntry<?> getRegistryEntry(ResourceLocation registryKey) {
/* 118 */     return REGISTRY_KEYS.get(registryKey);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void handleRegistry(User user, PacketWrapper<?> wrapper, ResourceLocation registryName, List<WrapperConfigServerRegistryData.RegistryElement> elements) {
/* 126 */     Object cacheKey = PacketEvents.getAPI().getServerManager().getRegistryCacheKey(user, wrapper
/* 127 */         .getServerVersion().toClientVersion());
/* 128 */     handleRegistry(user, wrapper, registryName, elements, cacheKey);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void handleRegistry(User user, PacketWrapper<?> wrapper, ResourceLocation registryName, List<WrapperConfigServerRegistryData.RegistryElement> elements, Object cacheKey) {
/*     */     SimpleRegistry<?> syncedRegistry;
/* 137 */     RegistryEntry<?> registryData = REGISTRY_KEYS.get(registryName);
/* 138 */     if (registryData == null) {
/*     */       return;
/*     */     }
/*     */     
/* 142 */     if (FORCE_PER_USER_REGISTRIES || cacheKey == null) {
/* 143 */       syncedRegistry = registryData.createFromElements(elements, wrapper);
/*     */     } else {
/* 145 */       syncedRegistry = registryData.computeSyncedRegistry(cacheKey, () -> registryData.createFromElements(elements, wrapper));
/*     */     } 
/*     */     
/* 148 */     user.putRegistry(syncedRegistry);
/*     */ 
/*     */     
/* 151 */     for (MappedEntity entry : syncedRegistry.getEntries()) {
/* 152 */       if (entry instanceof ResolvableEntity) {
/* 153 */         ((ResolvableEntity)entry).doResolve(wrapper);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void handleLegacyRegistries(User user, PacketWrapper<?> wrapper, NBTCompound registryData) {
/* 162 */     Object cacheKey = PacketEvents.getAPI().getServerManager().getRegistryCacheKey(user, wrapper
/* 163 */         .getServerVersion().toClientVersion());
/* 164 */     for (NBT tag : registryData.getTags().values()) {
/*     */       
/* 166 */       if (tag instanceof NBTList) {
/* 167 */         NBTList<NBTCompound> list = (NBTList<NBTCompound>)tag;
/* 168 */         handleRegistry(user, wrapper, DimensionTypes.getRegistry().getRegistryKey(), 
/* 169 */             WrapperConfigServerRegistryData.RegistryElement.convertNbt(list), cacheKey);
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 174 */       NBTCompound compound = (NBTCompound)tag;
/*     */       
/* 176 */       ResourceLocation registryName = new ResourceLocation(compound.getStringTagValueOrThrow("type"));
/*     */       
/* 178 */       NBTList<NBTCompound> nbtElements = compound.getCompoundListTagOrNull("value");
/* 179 */       if (nbtElements != null)
/*     */       {
/* 181 */         handleRegistry(user, wrapper, registryName, 
/* 182 */             WrapperConfigServerRegistryData.RegistryElement.convertNbt(nbtElements), cacheKey); } 
/*     */     } 
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   @Internal
/*     */   public static interface NbtEntryDecoder<T> {
/*     */     T decode(NBT param1NBT, PacketWrapper<?> param1PacketWrapper, @Nullable TypesBuilderData param1TypesBuilderData); }
/*     */   
/*     */   @FunctionalInterface
/*     */   @Internal
/*     */   public static interface LegacyNbtEntryDecoder<T> {
/*     */     default SynchronizedRegistriesHandler.NbtEntryDecoder<T> upgrade() {
/* 195 */       return (nbt, wrapper, data) -> decode(nbt, wrapper.getServerVersion().toClientVersion(), data);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     T decode(NBT param1NBT, ClientVersion param1ClientVersion, @Nullable TypesBuilderData param1TypesBuilderData);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public static final class RegistryEntry<T extends MappedEntity & CopyableEntity<T> & DeepComparableEntity>
/*     */   {
/*     */     private final IRegistry<T> baseRegistry;
/*     */ 
/*     */ 
/*     */     
/*     */     private final SynchronizedRegistriesHandler.NbtEntryDecoder<T> decoder;
/*     */ 
/*     */     
/* 217 */     private final Map<Object, SimpleRegistry<T>> syncedRegistries = new ConcurrentHashMap<>(2);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public RegistryEntry(IRegistry<T> baseRegistry, SynchronizedRegistriesHandler.LegacyNbtEntryDecoder<T> decoder) {
/* 223 */       this(baseRegistry, decoder.upgrade());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public RegistryEntry(IRegistry<T> baseRegistry, SynchronizedRegistriesHandler.NbtEntryDecoder<T> decoder) {
/* 230 */       this.baseRegistry = baseRegistry;
/* 231 */       this.decoder = decoder;
/*     */     }
/*     */     @Nullable
/*     */     public SimpleRegistry<T> getSyncedRegistry(Object key) {
/* 235 */       return this.syncedRegistries.get(key);
/*     */     }
/*     */ 
/*     */     
/*     */     public SimpleRegistry<T> computeSyncedRegistry(Object key, Supplier<SimpleRegistry<?>> registry) {
/* 240 */       return this.syncedRegistries.computeIfAbsent(key, $ -> (SimpleRegistry)registry.get());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private void handleElement(SimpleRegistry<T> registry, WrapperConfigServerRegistryData.RegistryElement element, int id, PacketWrapper<?> wrapper) {
/* 249 */       ResourceLocation elementName = element.getId();
/* 250 */       T baseEntry = this.baseRegistry.getByName(elementName);
/*     */ 
/*     */       
/* 253 */       TypesBuilderData data = new SimpleTypesBuilderData(elementName, id);
/* 254 */       MappedEntity mappedEntity = (baseEntry == null) ? null : ((CopyableEntity)baseEntry).copy(data);
/*     */       
/* 256 */       if (element.getData() != null) {
/*     */         
/* 258 */         MappedEntity mappedEntity1 = (MappedEntity)this.decoder.decode(element.getData(), wrapper, data);
/* 259 */         if (!((DeepComparableEntity)mappedEntity1).deepEquals(mappedEntity)) {
/*     */ 
/*     */ 
/*     */           
/* 263 */           registry.define(elementName, id, mappedEntity1);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           return;
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 279 */       if (mappedEntity != null) {
/* 280 */         registry.define(elementName, id, mappedEntity);
/*     */ 
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 286 */       PacketEvents.getAPI().getLogger().warning("Unknown registry entry " + elementName + " for " + 
/* 287 */           getRegistryKey());
/*     */     }
/*     */     
/*     */     public SimpleRegistry<T> createFromElements(List<WrapperConfigServerRegistryData.RegistryElement> elements, PacketWrapper<?> wrapper) {
/* 291 */       SimpleRegistry<T> registry = (SimpleRegistry)new SimpleRegistry<>(getRegistryKey());
/* 292 */       for (int id = 0; id < elements.size(); id++) {
/* 293 */         WrapperConfigServerRegistryData.RegistryElement element = elements.get(id);
/* 294 */         handleElement(registry, element, id, wrapper);
/*     */       } 
/* 296 */       return registry;
/*     */     }
/*     */     
/*     */     public ResourceLocation getRegistryKey() {
/* 300 */       return this.baseRegistry.getRegistryKey();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\mappings\SynchronizedRegistriesHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */