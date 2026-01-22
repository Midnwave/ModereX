/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Experimental;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.Objects;
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
/*     */ @Experimental
/*     */ public final class MaybeMappedEntity<T extends MappedEntity>
/*     */ {
/*     */   @Nullable
/*     */   private final T entity;
/*     */   @Nullable
/*     */   private final ResourceLocation name;
/*     */   @Nullable
/*     */   private final IRegistry<T> registry;
/*     */   
/*     */   public MaybeMappedEntity(T entity) {
/*  43 */     this(entity, null, null);
/*     */   }
/*     */   
/*     */   public MaybeMappedEntity(ResourceLocation name) {
/*  47 */     this(name, (IRegistry<T>)null);
/*     */   }
/*     */   
/*     */   public MaybeMappedEntity(ResourceLocation name, @Nullable IRegistry<T> registry) {
/*  51 */     this(null, name, registry);
/*     */   }
/*     */   
/*     */   public MaybeMappedEntity(@Nullable T entity, @Nullable ResourceLocation name) {
/*  55 */     this(entity, name, null);
/*     */   }
/*     */   
/*     */   public MaybeMappedEntity(@Nullable T entity, @Nullable ResourceLocation name, @Nullable IRegistry<T> registry) {
/*  59 */     if (entity == null && name == null) {
/*  60 */       throw new IllegalArgumentException("Only one of entity and name is allowed to be null");
/*     */     }
/*  62 */     this.entity = entity;
/*  63 */     this.name = name;
/*  64 */     this.registry = registry;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T extends MappedEntity> MaybeMappedEntity<T> read(PacketWrapper<?> wrapper, IRegistry<T> registry, PacketWrapper.Reader<T> reader) {
/*  70 */     if (wrapper.readBoolean())
/*     */     {
/*  72 */       return new MaybeMappedEntity<>((T)reader.apply(wrapper));
/*     */     }
/*     */     
/*  75 */     ClientVersion version = wrapper.getServerVersion().toClientVersion();
/*  76 */     IRegistry<T> replacedRegistry = wrapper.getRegistryHolder().getRegistryOr(registry, version);
/*  77 */     return new MaybeMappedEntity<>(wrapper.readIdentifier(), replacedRegistry);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T extends MappedEntity> void write(PacketWrapper<?> wrapper, MaybeMappedEntity<T> entity, PacketWrapper.Writer<T> writer) {
/*  83 */     if (entity.entity != null) {
/*  84 */       wrapper.writeBoolean(true);
/*  85 */       writer.accept(wrapper, entity.entity);
/*     */     } else {
/*  87 */       wrapper.writeBoolean(false);
/*  88 */       wrapper.writeIdentifier(entity.name);
/*     */     } 
/*     */   }
/*     */   
/*     */   public T getValueOrThrow() {
/*  93 */     T value = getValue();
/*  94 */     if (value == null) {
/*  95 */       throw new IllegalStateException("Can't resolve entity by name " + this.name);
/*     */     }
/*  97 */     return value;
/*     */   }
/*     */   @Nullable
/*     */   public T getValue() {
/* 101 */     if (this.entity != null)
/* 102 */       return this.entity; 
/* 103 */     if (this.registry != null && this.name != null) {
/* 104 */       return (T)this.registry.getByName(this.name);
/*     */     }
/* 106 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public ResourceLocation getName() {
/* 111 */     if (this.name != null)
/* 112 */       return this.name; 
/* 113 */     if (this.entity != null) {
/* 114 */       return this.entity.getName();
/*     */     }
/* 116 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 122 */     if (!(obj instanceof MaybeMappedEntity)) return false; 
/* 123 */     MaybeMappedEntity<?> that = (MaybeMappedEntity)obj;
/* 124 */     if (!Objects.equals(this.entity, that.entity)) return false; 
/* 125 */     return Objects.equals(this.name, that.name);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 130 */     return Objects.hash(new Object[] { this.entity, this.name });
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\mapper\MaybeMappedEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */