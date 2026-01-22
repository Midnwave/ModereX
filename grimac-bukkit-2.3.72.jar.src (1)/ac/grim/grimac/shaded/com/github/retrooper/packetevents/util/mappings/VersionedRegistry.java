/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.VisibleForTesting;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.function.Function;
/*     */ import org.jspecify.annotations.NullMarked;
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
/*     */ @NullMarked
/*     */ public final class VersionedRegistry<T extends MappedEntity>
/*     */   implements IRegistry<T>
/*     */ {
/*     */   private final ResourceLocation registryKey;
/*     */   private final TypesBuilder typesBuilder;
/*  41 */   private final Map<String, T> typeMap = new HashMap<>();
/*  42 */   private final Map<Byte, Map<Integer, T>> typeIdMap = new HashMap<>();
/*     */   
/*     */   public VersionedRegistry(String registry) {
/*  45 */     this(registry, "registries/" + registry);
/*     */   }
/*     */   
/*     */   public VersionedRegistry(String registry, String mappingsPath) {
/*  49 */     this(new ResourceLocation(registry), mappingsPath);
/*     */   }
/*     */   
/*     */   public VersionedRegistry(ResourceLocation registryKey, String mappingsPath) {
/*  53 */     this.registryKey = registryKey;
/*  54 */     this.typesBuilder = new TypesBuilder(mappingsPath);
/*  55 */     this.typesBuilder.registry = this;
/*     */   }
/*     */   
/*     */   @Internal
/*     */   public <Z extends T> Z define(String name, Function<TypesBuilderData, Z> builder) {
/*  60 */     TypesBuilderData typeData = this.typesBuilder.define(name);
/*  61 */     MappedEntity mappedEntity = (MappedEntity)builder.apply(typeData);
/*  62 */     MappingHelper.registerMapping(this.typesBuilder, this.typeMap, this.typeIdMap, typeData, (T)mappedEntity);
/*  63 */     return (Z)mappedEntity;
/*     */   }
/*     */   
/*     */   @VisibleForTesting
/*     */   @Internal
/*     */   public TypesBuilder getTypesBuilder() {
/*  69 */     return this.typesBuilder;
/*     */   }
/*     */   
/*     */   @Internal
/*     */   public void unloadMappings() {
/*  74 */     this.typesBuilder.unloadFileMappings();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public T getByName(ResourceLocation name) {
/*  79 */     return this.typeMap.get(name.toString());
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public T getByName(String name) {
/*  85 */     return this.typeMap.get(ResourceLocation.normString(name));
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public T getById(ClientVersion version, int id) {
/*  90 */     int index = this.typesBuilder.getDataIndex(version);
/*  91 */     Map<Integer, T> idMap = this.typeIdMap.get(Byte.valueOf((byte)index));
/*  92 */     return idMap.get(Integer.valueOf(id));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getId(MappedEntity entity, ClientVersion version) {
/*  97 */     return entity.getId(version);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<T> getEntries() {
/* 105 */     return Collections.unmodifiableCollection(this.typeMap.values());
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 110 */     return this.typeMap.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public ResourceLocation getRegistryKey() {
/* 115 */     return this.registryKey;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 120 */     return "VersionedRegistry[" + this.registryKey + ']';
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\mappings\VersionedRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */