/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.positionsource.builtin;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTFloat;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTInt;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTIntArray;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTNumber;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.positionsource.PositionSource;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.positionsource.PositionSourceTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.UniqueIdUtil;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import java.util.Optional;
/*     */ import java.util.UUID;
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
/*     */ public class EntityPositionSource
/*     */   extends PositionSource
/*     */ {
/*  38 */   private static final UUID EMPTY_UNIQUE_ID = new UUID(0L, 0L);
/*     */   
/*     */   private Optional<UUID> entityUniqueId;
/*     */   private int entityId;
/*     */   private float offsetY;
/*     */   
/*     */   public EntityPositionSource(int entityId) {
/*  45 */     this(entityId, 0.0F);
/*     */   }
/*     */   
/*     */   public EntityPositionSource(int entityId, float offsetY) {
/*  49 */     super(PositionSourceTypes.ENTITY);
/*  50 */     this.entityId = entityId;
/*  51 */     this.offsetY = offsetY;
/*     */   }
/*     */   
/*     */   public EntityPositionSource(Optional<UUID> entityUniqueId, float offsetY) {
/*  55 */     super(PositionSourceTypes.ENTITY);
/*  56 */     this.entityUniqueId = entityUniqueId;
/*  57 */     this.offsetY = offsetY;
/*     */   }
/*     */   
/*     */   public static EntityPositionSource read(PacketWrapper<?> wrapper) {
/*  61 */     int entityId = wrapper.readVarInt();
/*  62 */     float offsetY = 0.0F;
/*  63 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_19)) {
/*  64 */       offsetY = wrapper.readFloat();
/*     */     }
/*  66 */     return new EntityPositionSource(entityId, offsetY);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, EntityPositionSource source) {
/*  70 */     wrapper.writeVarInt(source.entityId);
/*  71 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_19)) {
/*  72 */       wrapper.writeFloat(source.offsetY);
/*     */     }
/*     */   }
/*     */   
/*     */   public static EntityPositionSource decodeSource(NBTCompound compound, ClientVersion version) {
/*  77 */     if (version.isNewerThanOrEquals(ClientVersion.V_1_19)) {
/*  78 */       int[] entityUniqueIdArr = ((NBTIntArray)compound.getTagOfTypeOrThrow("source_entity", NBTIntArray.class)).getValue();
/*  79 */       UUID entityUniqueId = UniqueIdUtil.fromIntArray(entityUniqueIdArr);
/*  80 */       NBTNumber offsetYTag = compound.getNumberTagOrNull("y_offset");
/*  81 */       float offsetY = (offsetYTag == null) ? 0.0F : offsetYTag.getAsFloat();
/*  82 */       return new EntityPositionSource(Optional.of(entityUniqueId), offsetY);
/*     */     } 
/*  84 */     int entityId = compound.getNumberTagOrThrow("source_entity_id").getAsInt();
/*  85 */     return new EntityPositionSource(entityId);
/*     */   }
/*     */   
/*     */   public static void encodeSource(EntityPositionSource source, ClientVersion version, NBTCompound compound) {
/*  89 */     if (version.isNewerThanOrEquals(ClientVersion.V_1_19)) {
/*  90 */       UUID uniqueId = source.entityUniqueId.orElse(EMPTY_UNIQUE_ID);
/*  91 */       compound.setTag("source_entity", (NBT)new NBTIntArray(UniqueIdUtil.toIntArray(uniqueId)));
/*  92 */       compound.setTag("y_offset", (NBT)new NBTFloat(source.offsetY));
/*     */     } else {
/*  94 */       compound.setTag("source_entity_id", (NBT)new NBTInt(source.entityId));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Optional<UUID> getEntityUniqueId() {
/* 102 */     return this.entityUniqueId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEntityUniqueId(Optional<UUID> entityUniqueId) {
/* 109 */     this.entityUniqueId = entityUniqueId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getEntityId() {
/* 116 */     return this.entityId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEntityId(int entityId) {
/* 123 */     this.entityId = entityId;
/*     */   }
/*     */   
/*     */   public float getOffsetY() {
/* 127 */     return this.offsetY;
/*     */   }
/*     */   
/*     */   public void setOffsetY(float offsetY) {
/* 131 */     this.offsetY = offsetY;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\positionsource\builtin\EntityPositionSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */