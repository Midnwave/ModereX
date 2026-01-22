/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ @NullMarked
/*     */ public final class SimpleRegistry<T extends MappedEntity>
/*     */   implements IRegistry<T>
/*     */ {
/*     */   private final ResourceLocation registryKey;
/*  37 */   private final Map<String, T> typeMap = new HashMap<>();
/*  38 */   private final Map<Integer, T> typeIdMap = new HashMap<>();
/*  39 */   private final Map<String, Integer> reverseTypeIdMap = new HashMap<>();
/*     */   
/*     */   public SimpleRegistry(String registryKey) {
/*  42 */     this(new ResourceLocation(registryKey));
/*     */   }
/*     */   
/*     */   public SimpleRegistry(ResourceLocation registryKey) {
/*  46 */     this.registryKey = registryKey;
/*     */   }
/*     */   
/*     */   @Internal
/*     */   public <Z extends T> Z define(String name, int id, Z instance) {
/*  51 */     return define(new ResourceLocation(name), id, instance);
/*     */   }
/*     */   
/*     */   @Internal
/*     */   public <Z extends T> Z define(ResourceLocation name, int id, Z instance) {
/*  56 */     String nameStr = name.toString();
/*  57 */     this.typeMap.put(nameStr, (T)instance);
/*  58 */     this.typeIdMap.put(Integer.valueOf(id), (T)instance);
/*  59 */     this.reverseTypeIdMap.put(nameStr, Integer.valueOf(id));
/*  60 */     return instance;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public T getByName(String name) {
/*  65 */     return this.typeMap.get(name);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public T getById(ClientVersion version, int id) {
/*  70 */     return this.typeIdMap.get(Integer.valueOf(id));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getId(String entityName, ClientVersion version) {
/*  75 */     return ((Integer)this.reverseTypeIdMap.getOrDefault(entityName, Integer.valueOf(-1))).intValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getId(MappedEntity entity, ClientVersion version) {
/*  80 */     return getId(entity.getName().toString(), version);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<T> getEntries() {
/*  88 */     return Collections.unmodifiableCollection(this.typeMap.values());
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/*  93 */     return this.typeMap.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public ResourceLocation getRegistryKey() {
/*  98 */     return this.registryKey;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 103 */     return "SimpleRegistry[" + this.registryKey + ']';
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\mappings\SimpleRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */