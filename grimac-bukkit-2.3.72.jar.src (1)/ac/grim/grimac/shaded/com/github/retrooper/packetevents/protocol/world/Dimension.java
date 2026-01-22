/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.dimension.DimensionType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.dimension.DimensionTypeRef;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.dimension.DimensionTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
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
/*     */ @Deprecated
/*     */ public class Dimension
/*     */ {
/*     */   private int id;
/*     */   private NBTCompound attributes;
/*     */   
/*     */   @Deprecated
/*     */   public Dimension(DimensionType type) {
/*  42 */     this.id = type.getId();
/*  43 */     this.attributes = new NBTCompound();
/*     */   }
/*     */   
/*     */   public Dimension(int id) {
/*  47 */     this.id = id;
/*  48 */     this.attributes = new NBTCompound();
/*     */   }
/*     */   
/*     */   public Dimension(NBTCompound attributes) {
/*  52 */     this.attributes = attributes;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public Dimension(int id, NBTCompound attributes) {
/*  57 */     this.id = id;
/*  58 */     this.attributes = attributes;
/*     */   }
/*     */   
/*     */   public static Dimension fromDimensionTypeRef(DimensionTypeRef ref) {
/*  62 */     if (ref instanceof DimensionTypeRef.NameRef) {
/*  63 */       Dimension dimension = new Dimension(0);
/*  64 */       dimension.setDimensionName(ref.getName().toString());
/*  65 */       return dimension;
/*  66 */     }  if (ref instanceof DimensionTypeRef.IdRef)
/*  67 */       return new Dimension(ref.getId()); 
/*  68 */     if (ref instanceof DimensionTypeRef.DataRef)
/*  69 */       return new Dimension((NBTCompound)ref.getData()); 
/*  70 */     if (ref instanceof DimensionTypeRef.DirectRef) {
/*  71 */       return fromDimensionType(((DimensionTypeRef.DirectRef)ref).getDimensionType(), null, ((DimensionTypeRef.DirectRef)ref)
/*  72 */           .getVersion());
/*     */     }
/*  74 */     throw new UnsupportedOperationException("Unsupported DimensionTypeRef implementation: " + ref);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Dimension fromDimensionType(DimensionType dimensionType, @Nullable User user, @Nullable ClientVersion version) {
/*  82 */     if (version == null)
/*     */     {
/*  84 */       version = (user != null && PacketEvents.getAPI().getInjector().isProxy()) ? user.getClientVersion() : PacketEvents.getAPI().getServerManager().getVersion().toClientVersion();
/*     */     }
/*  86 */     NBTCompound encodedType = (NBTCompound)DimensionType.encode(dimensionType, version);
/*  87 */     return new Dimension(dimensionType.getId(version), encodedType);
/*     */   }
/*     */   
/*     */   public DimensionTypeRef asDimensionTypeRef() {
/*  91 */     if (this.attributes == null)
/*  92 */       return (DimensionTypeRef)new DimensionTypeRef.IdRef(this.id); 
/*  93 */     if (this.attributes.size() > 1)
/*     */     {
/*  95 */       return (DimensionTypeRef)new DimensionTypeRef.DataRef((NBT)this.attributes);
/*     */     }
/*  97 */     ResourceLocation dimensionName = new ResourceLocation(getDimensionName());
/*  98 */     return (DimensionTypeRef)new DimensionTypeRef.NameRef(dimensionName);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DimensionType asDimensionType(@Nullable User user, @Nullable ClientVersion version) {
/* 104 */     if (version == null)
/*     */     {
/* 106 */       version = (user != null && PacketEvents.getAPI().getInjector().isProxy()) ? user.getClientVersion() : PacketEvents.getAPI().getServerManager().getVersion().toClientVersion();
/*     */     }
/*     */     
/* 109 */     IRegistry<DimensionType> registry = (user != null) ? user.getRegistryOr((IRegistry)DimensionTypes.getRegistry(), version) : (IRegistry<DimensionType>)DimensionTypes.getRegistry();
/* 110 */     String dimName = getDimensionName();
/* 111 */     if (!dimName.isEmpty()) {
/* 112 */       return (DimensionType)registry.getByName(new ResourceLocation(dimName));
/*     */     }
/* 114 */     return (DimensionType)registry.getById(version, this.id);
/*     */   }
/*     */   
/*     */   public String getDimensionName() {
/* 118 */     return getAttributes().getStringTagValueOrDefault("effects", "");
/*     */   }
/*     */   
/*     */   public void setDimensionName(String name) {
/* 122 */     NBTCompound compound = getAttributes();
/* 123 */     compound.setTag("effects", (NBT)new NBTString(name));
/* 124 */     setAttributes(compound);
/*     */   }
/*     */   
/*     */   public int getId() {
/* 128 */     return this.id;
/*     */   }
/*     */   
/*     */   public void setId(int id) {
/* 132 */     this.id = id;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public DimensionType getType() {
/* 137 */     return DimensionType.getById(this.id);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void setType(DimensionType type) {
/* 142 */     this.id = type.getId();
/*     */   }
/*     */   
/*     */   public NBTCompound getAttributes() {
/* 146 */     return this.attributes;
/*     */   }
/*     */   
/*     */   public void setAttributes(NBTCompound attributes) {
/* 150 */     this.attributes = attributes;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\Dimension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */