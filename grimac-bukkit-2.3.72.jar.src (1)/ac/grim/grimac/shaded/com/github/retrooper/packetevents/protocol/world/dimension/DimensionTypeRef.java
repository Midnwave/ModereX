/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.dimension;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*     */ public interface DimensionTypeRef
/*     */ {
/*     */   DimensionType resolve(IRegistry<DimensionType> paramIRegistry, ClientVersion paramClientVersion);
/*     */   
/*     */   default ResourceLocation getName() {
/*  36 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   default int getId() {
/*  40 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   default NBT getData() {
/*  44 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   static DimensionTypeRef read(PacketWrapper<?> wrapper) {
/*  48 */     ServerVersion version = wrapper.getServerVersion();
/*  49 */     if (version.isNewerThanOrEquals(ServerVersion.V_1_20_5)) {
/*  50 */       return new IdRef(wrapper.readVarInt());
/*     */     }
/*  52 */     boolean v1162 = version.isNewerThanOrEquals(ServerVersion.V_1_16_2);
/*  53 */     if (version.isNewerThanOrEquals(ServerVersion.V_1_19) || (!v1162 && version
/*  54 */       .isNewerThanOrEquals(ServerVersion.V_1_16)))
/*  55 */       return new NameRef(wrapper.readIdentifier()); 
/*  56 */     if (v1162) {
/*  57 */       return new DataRef(wrapper.readNBTRaw());
/*     */     }
/*  59 */     return new IdRef((
/*  60 */         wrapper instanceof ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerJoinGame && version.isOlderThan(ServerVersion.V_1_9_2)) ? 
/*  61 */         wrapper.readByte() : wrapper.readInt());
/*     */   }
/*     */ 
/*     */   
/*     */   static void write(PacketWrapper<?> wrapper, DimensionTypeRef ref) {
/*  66 */     ServerVersion version = wrapper.getServerVersion();
/*  67 */     if (version.isNewerThanOrEquals(ServerVersion.V_1_20_5)) {
/*  68 */       wrapper.writeVarInt(ref.getId());
/*     */       return;
/*     */     } 
/*  71 */     boolean v1162 = version.isNewerThanOrEquals(ServerVersion.V_1_16_2);
/*  72 */     if (version.isNewerThanOrEquals(ServerVersion.V_1_19) || (!v1162 && version
/*  73 */       .isNewerThanOrEquals(ServerVersion.V_1_16))) {
/*  74 */       wrapper.writeIdentifier(ref.getName());
/*  75 */     } else if (v1162) {
/*  76 */       wrapper.writeNBTRaw(ref.getData());
/*  77 */     } else if (wrapper instanceof ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerJoinGame && version
/*  78 */       .isOlderThan(ServerVersion.V_1_9_2)) {
/*  79 */       wrapper.writeByte(ref.getId());
/*     */     } else {
/*  81 */       wrapper.writeInt(ref.getId());
/*     */     } 
/*     */   }
/*     */   
/*     */   public static final class DirectRef
/*     */     implements DimensionTypeRef {
/*     */     private final DimensionType dimensionType;
/*     */     private final ClientVersion version;
/*     */     
/*     */     public DirectRef(DimensionType dimensionType, ClientVersion version) {
/*  91 */       this.dimensionType = dimensionType;
/*  92 */       this.version = version;
/*     */     }
/*     */ 
/*     */     
/*     */     public DimensionType resolve(IRegistry<DimensionType> registry, ClientVersion version) {
/*  97 */       if (this.version != version) {
/*  98 */         throw new IllegalArgumentException("Expected version " + this.version + ", received " + version + " for direct dimension type ref " + this.dimensionType);
/*     */       }
/*     */       
/* 101 */       return this.dimensionType;
/*     */     }
/*     */ 
/*     */     
/*     */     public ResourceLocation getName() {
/* 106 */       return this.dimensionType.getName();
/*     */     }
/*     */ 
/*     */     
/*     */     public int getId() {
/* 111 */       return this.dimensionType.getId(this.version);
/*     */     }
/*     */ 
/*     */     
/*     */     public NBT getData() {
/* 116 */       return DimensionType.encode(this.dimensionType, this.version);
/*     */     }
/*     */     
/*     */     public DimensionType getDimensionType() {
/* 120 */       return this.dimensionType;
/*     */     }
/*     */     
/*     */     public ClientVersion getVersion() {
/* 124 */       return this.version;
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class NameRef
/*     */     implements DimensionTypeRef {
/*     */     private final ResourceLocation name;
/*     */     
/*     */     public NameRef(ResourceLocation name) {
/* 133 */       this.name = name;
/*     */     }
/*     */ 
/*     */     
/*     */     public DimensionType resolve(IRegistry<DimensionType> registry, ClientVersion version) {
/* 138 */       return (DimensionType)registry.getByName(this.name);
/*     */     }
/*     */ 
/*     */     
/*     */     public ResourceLocation getName() {
/* 143 */       return this.name;
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class IdRef
/*     */     implements DimensionTypeRef {
/*     */     private final int id;
/*     */     
/*     */     public IdRef(int id) {
/* 152 */       this.id = id;
/*     */     }
/*     */ 
/*     */     
/*     */     public DimensionType resolve(IRegistry<DimensionType> registry, ClientVersion version) {
/* 157 */       return (DimensionType)registry.getById(version, this.id);
/*     */     }
/*     */ 
/*     */     
/*     */     public int getId() {
/* 162 */       return this.id;
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class DataRef
/*     */     implements DimensionTypeRef {
/*     */     private final NBT data;
/*     */     
/*     */     public DataRef(NBT data) {
/* 171 */       this.data = data;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public DimensionType resolve(IRegistry<DimensionType> registry, ClientVersion version) {
/* 183 */       ResourceLocation name = getNullableName();
/* 184 */       if (name != null) {
/* 185 */         DimensionType dimensionType = (DimensionType)registry.getByName(name);
/* 186 */         if (dimensionType != null) {
/* 187 */           return dimensionType;
/*     */         }
/*     */       } 
/* 190 */       return DimensionType.decode(this.data, version, (TypesBuilderData)null);
/*     */     }
/*     */     @Nullable
/*     */     public ResourceLocation getNullableName() {
/* 194 */       if (this.data instanceof NBTCompound) {
/* 195 */         String effectsName = ((NBTCompound)this.data).getStringTagValueOrNull("effects");
/* 196 */         if (effectsName != null) {
/* 197 */           return new ResourceLocation(effectsName);
/*     */         }
/*     */       } 
/* 200 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public ResourceLocation getName() {
/* 205 */       ResourceLocation name = getNullableName();
/* 206 */       return (name != null) ? name : super.getName();
/*     */     }
/*     */ 
/*     */     
/*     */     public NBT getData() {
/* 211 */       return this.data;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\dimension\DimensionTypeRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */