/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtDecoder;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtEncoder;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistryHolder;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.function.Supplier;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @NullMarked
/*     */ public interface MappedEntityRef<T extends MappedEntity>
/*     */   extends Supplier<T>
/*     */ {
/*     */   static <T extends MappedEntity> MappedEntityRef<T> decode(NBT tag, IRegistry<T> registry, NbtDecoder<T> decoder, PacketWrapper<?> wrapper) {
/*  44 */     if (tag instanceof NBTString) {
/*  45 */       ResourceLocation name = new ResourceLocation(((NBTString)tag).getValue());
/*  46 */       return new Named<>(wrapper, registry, name);
/*     */     } 
/*  48 */     return new Static<>((T)decoder.decode(tag, wrapper));
/*     */   }
/*     */   
/*     */   static <T extends MappedEntity> NBT encode(PacketWrapper<?> wrapper, NbtEncoder<T> encoder, MappedEntityRef<T> ref) {
/*  52 */     if (ref instanceof Named)
/*  53 */       return (NBT)new NBTString(((Named)ref).name.toString()); 
/*  54 */     if (ref instanceof Static) {
/*  55 */       return encoder.encode(wrapper, ((Static)ref).entity);
/*     */     }
/*  57 */     throw new UnsupportedOperationException("Unsupported MappedEntityRef implementation: " + ref);
/*     */   }
/*     */   
/*     */   T get();
/*     */   
/*     */   public static final class Static<T extends MappedEntity> implements MappedEntityRef<T> {
/*     */     private final T entity;
/*     */     
/*     */     public Static(T entity) {
/*  66 */       this.entity = entity;
/*     */     }
/*     */ 
/*     */     
/*     */     public T get() {
/*  71 */       return this.entity;
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class Named<T extends MappedEntity>
/*     */     implements MappedEntityRef<T> {
/*     */     private final WeakReference<IRegistryHolder> registryHolder;
/*     */     private final ClientVersion version;
/*     */     private final IRegistry<T> registry;
/*     */     private final ResourceLocation name;
/*     */     private volatile T entity;
/*     */     
/*     */     public Named(PacketWrapper<?> wrapper, IRegistry<T> registry, ResourceLocation name) {
/*  84 */       this(wrapper.getRegistryHolder(), wrapper.getServerVersion().toClientVersion(), registry, name);
/*     */     }
/*     */     
/*     */     public Named(IRegistryHolder registryHolder, ClientVersion version, IRegistry<T> registry, ResourceLocation name) {
/*  88 */       this.registryHolder = new WeakReference<>(registryHolder);
/*  89 */       this.version = version;
/*  90 */       this.registry = registry;
/*  91 */       this.name = name;
/*     */     }
/*     */     
/*     */     public T get() {
/*     */       MappedEntity mappedEntity;
/*  96 */       T entity = this.entity;
/*  97 */       if (entity == null) {
/*  98 */         synchronized (this) {
/*  99 */           entity = this.entity;
/* 100 */           if (entity == null) {
/*     */             
/* 102 */             IRegistryHolder registryHolder = this.registryHolder.get();
/* 103 */             if (registryHolder == null) {
/* 104 */               throw new IllegalStateException("Registry holder for " + this.registry + "/" + this.version + "/" + this.name + "has disappeared");
/*     */             }
/*     */ 
/*     */             
/* 108 */             IRegistry<T> registry = registryHolder.getRegistryOr(this.registry, this.version);
/* 109 */             mappedEntity = registry.getByNameOrThrow(this.name);
/* 110 */             this.entity = (T)mappedEntity;
/*     */           } 
/*     */         } 
/*     */       }
/* 114 */       return (T)mappedEntity;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\mapper\MappedEntityRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */