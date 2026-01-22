/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.data;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.armadillo.ArmadilloState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.cat.CatVariant;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.chicken.ChickenVariant;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.cow.CowVariant;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.frog.FrogVariant;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.pig.PigVariant;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.pose.EntityPose;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.sniffer.SnifferState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.villager.VillagerData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.wolfvariant.WolfSoundVariant;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.wolfvariant.WolfVariant;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.Particle;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.WorldBlockPosition;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.painting.PaintingVariant;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Quaternion4f;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3f;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
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
/*     */ public final class EntityDataTypes
/*     */ {
/*  56 */   private static final VersionedRegistry<EntityDataType<?>> REGISTRY = new VersionedRegistry("entity_data_serializer");
/*     */   
/*  58 */   public static final EntityDataType<Byte> BYTE = define("byte", PacketWrapper::readByte, PacketWrapper::writeByte);
/*     */ 
/*     */   
/*  61 */   public static final EntityDataType<Short> SHORT = define("short", PacketWrapper::readShort, PacketWrapper::writeShort); public static final EntityDataType<Integer> INT;
/*     */   static {
/*  63 */     INT = define("int", wrapper -> wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_9) ? Integer.valueOf(wrapper.readVarInt()) : Integer.valueOf(wrapper.readInt()), (wrapper, value) -> {
/*     */           if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_9)) {
/*     */             wrapper.writeVarInt(value.intValue());
/*     */           } else {
/*     */             wrapper.writeInt(value.intValue());
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  77 */   public static final EntityDataType<Long> LONG = define("long", PacketWrapper::readVarLong, PacketWrapper::writeVarLong);
/*     */   
/*  79 */   public static final EntityDataType<Float> FLOAT = define("float", PacketWrapper::readFloat, PacketWrapper::writeFloat);
/*     */   
/*  81 */   public static final EntityDataType<String> STRING = define("string", PacketWrapper::readString, PacketWrapper::writeString);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*  87 */   public static final EntityDataType<String> COMPONENT = define("component", PacketWrapper::readComponentJSON, PacketWrapper::writeComponentJSON);
/*  88 */   public static final EntityDataType<Component> ADV_COMPONENT = define("component", PacketWrapper::readComponent, PacketWrapper::writeComponent);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*  94 */   public static final EntityDataType<Optional<String>> OPTIONAL_COMPONENT = define("optional_component", readOptionalComponentJSONDeserializer(), writeOptionalComponentJSONSerializer());
/*  95 */   public static final EntityDataType<Optional<Component>> OPTIONAL_ADV_COMPONENT = define("optional_component", readOptionalComponentDeserializer(), writeOptionalComponentSerializer());
/*     */   
/*  97 */   public static final EntityDataType<ItemStack> ITEMSTACK = define("itemstack", PacketWrapper::readItemStack, PacketWrapper::writeItemStack); public static final EntityDataType<Optional<ItemStack>> OPTIONAL_ITEMSTACK;
/*     */   static {
/*  99 */     OPTIONAL_ITEMSTACK = define("optional_itemstack", wrapper -> Optional.of(wrapper.readItemStack()), (wrapper, value) -> wrapper.writeItemStack(value.orElse(null)));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 104 */   public static final EntityDataType<Boolean> BOOLEAN = define("boolean", PacketWrapper::readBoolean, PacketWrapper::writeBoolean); public static final EntityDataType<Vector3f> ROTATION;
/*     */   static {
/* 106 */     ROTATION = define("rotation", wrapper -> new Vector3f(wrapper.readFloat(), wrapper.readFloat(), wrapper.readFloat()), (wrapper, value) -> {
/*     */           wrapper.writeFloat(value.x);
/*     */ 
/*     */           
/*     */           wrapper.writeFloat(value.y);
/*     */           
/*     */           wrapper.writeFloat(value.z);
/*     */         });
/*     */     
/* 115 */     BLOCK_POSITION = define("block_position", wrapper -> {
/*     */           if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_9))
/*     */             return wrapper.readBlockPosition(); 
/*     */           int x = wrapper.readInt();
/*     */           int y = wrapper.readInt();
/*     */           int z = wrapper.readInt();
/*     */           return new Vector3i(x, y, z);
/*     */         }(wrapper, blockPosition) -> {
/*     */           if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_9)) {
/*     */             wrapper.writeBlockPosition(blockPosition);
/*     */           } else {
/*     */             wrapper.writeInt(blockPosition.getX());
/*     */             wrapper.writeInt(blockPosition.getY());
/*     */             wrapper.writeInt(blockPosition.getZ());
/*     */           } 
/*     */         });
/*     */   }
/*     */   
/*     */   public static final EntityDataType<Vector3i> BLOCK_POSITION;
/* 134 */   public static final EntityDataType<Optional<Vector3i>> OPTIONAL_BLOCK_POSITION = define("optional_block_position", 
/* 135 */       readOptionalBlockPositionDeserializer(), writeOptionalBlockPositionSerializer()); public static final EntityDataType<BlockFace> BLOCK_FACE;
/*     */   static {
/* 137 */     BLOCK_FACE = define("block_face", wrapper -> {
/*     */           int id = wrapper.readVarInt();
/*     */           
/*     */           return BlockFace.getBlockFaceByValue(id);
/*     */         }(wrapper, value) -> wrapper.writeVarInt(value.getFaceValue()));
/*     */     
/* 143 */     OPTIONAL_UUID = define("optional_uuid", wrapper -> Optional.ofNullable((UUID)wrapper.readOptional(PacketWrapper::readUUID)), (wrapper, value) -> wrapper.writeOptional(value.orElse(null), PacketWrapper::writeUUID));
/*     */   }
/*     */ 
/*     */   
/*     */   public static final EntityDataType<Optional<UUID>> OPTIONAL_UUID;
/* 148 */   public static final EntityDataType<Integer> BLOCK_STATE = define("block_state", 
/* 149 */       readIntDeserializer(), writeIntSerializer());
/*     */   
/* 151 */   public static final EntityDataType<Integer> OPTIONAL_BLOCK_STATE = define("optional_block_state", readIntDeserializer(), writeIntSerializer());
/*     */ 
/*     */   
/* 154 */   public static final EntityDataType<NBTCompound> NBT = define("nbt", PacketWrapper::readNBT, PacketWrapper::writeNBT);
/*     */   
/* 156 */   public static final EntityDataType<Particle<?>> PARTICLE = define("particle", Particle::read, Particle::write);
/*     */   
/* 158 */   public static final EntityDataType<VillagerData> VILLAGER_DATA = define("villager_data", PacketWrapper::readVillagerData, PacketWrapper::writeVillagerData); public static final EntityDataType<Optional<Integer>> OPTIONAL_INT;
/*     */   static {
/* 160 */     OPTIONAL_INT = define("optional_int", wrapper -> {
/*     */           int i = wrapper.readVarInt();
/*     */ 
/*     */           
/*     */           return (i == 0) ? Optional.empty() : Optional.<Integer>of(Integer.valueOf(i - 1));
/*     */         }(wrapper, value) -> wrapper.writeVarInt(((Integer)value.orElse(Integer.valueOf(-1))).intValue() + 1));
/*     */     
/* 167 */     ENTITY_POSE = define("entity_pose", wrapper -> {
/*     */           int id = wrapper.readVarInt();
/*     */           return EntityPose.getById(wrapper.getServerVersion().toClientVersion(), id);
/*     */         }(wrapper, value) -> wrapper.writeVarInt(value.getId(wrapper.getServerVersion().toClientVersion())));
/*     */   }
/*     */ 
/*     */   
/*     */   public static final EntityDataType<EntityPose> ENTITY_POSE;
/*     */   
/*     */   @Deprecated
/* 177 */   public static final EntityDataType<Integer> CAT_VARIANT = define("cat_variant_type", readIntDeserializer(), writeIntSerializer());
/*     */   
/* 179 */   public static final EntityDataType<CatVariant> TYPED_CAT_VARIANT = define("cat_variant_type", CatVariant::read, CatVariant::write);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 186 */   public static final EntityDataType<Integer> FROG_VARIANT = define("frog_variant_type", readIntDeserializer(), writeIntSerializer());
/*     */   
/* 188 */   public static final EntityDataType<FrogVariant> TYPED_FROG_VARIANT = define("frog_variant_type", FrogVariant::read, FrogVariant::write); public static final EntityDataType<Optional<WorldBlockPosition>> OPTIONAL_GLOBAL_POSITION;
/*     */   static {
/* 190 */     OPTIONAL_GLOBAL_POSITION = define("optional_global_position", wrapper -> Optional.ofNullable((WorldBlockPosition)wrapper.readOptional(())), (wrapper, value) -> wrapper.writeOptional(value.orElse(null), ()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 202 */   public static final EntityDataType<Integer> PAINTING_VARIANT_TYPE = define("painting_variant_type", readIntDeserializer(), writeIntSerializer());
/*     */   
/* 204 */   public static final EntityDataType<PaintingVariant> PAINTING_VARIANT = define("painting_variant_type", PaintingVariant::read, PaintingVariant::write); public static final EntityDataType<SnifferState> SNIFFER_STATE;
/*     */   static {
/* 206 */     SNIFFER_STATE = define("sniffer_state", wrapper -> {
/*     */           int id = wrapper.readVarInt();
/*     */           
/*     */           return SnifferState.values()[id];
/*     */         }(wrapper, value) -> wrapper.writeVarInt(value.ordinal()));
/* 211 */     VECTOR3F = define("vector3f", wrapper -> new Vector3f(wrapper.readFloat(), wrapper.readFloat(), wrapper.readFloat()), (wrapper, value) -> {
/*     */           wrapper.writeFloat(value.x);
/*     */           
/*     */           wrapper.writeFloat(value.y);
/*     */           
/*     */           wrapper.writeFloat(value.z);
/*     */         });
/*     */     
/* 219 */     QUATERNION = define("quaternion", wrapper -> new Quaternion4f(wrapper.readFloat(), wrapper.readFloat(), wrapper.readFloat(), wrapper.readFloat()), (wrapper, value) -> {
/*     */           wrapper.writeFloat(value.getX());
/*     */ 
/*     */           
/*     */           wrapper.writeFloat(value.getY());
/*     */ 
/*     */           
/*     */           wrapper.writeFloat(value.getZ());
/*     */           
/*     */           wrapper.writeFloat(value.getW());
/*     */         });
/*     */     
/* 231 */     ARMADILLO_STATE = define("armadillo_state", wrapper -> ArmadilloState.values()[wrapper.readVarInt()], (wrapper, value) -> wrapper.writeVarInt(value.ordinal()));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 238 */     PARTICLES = define("particles", wrapper -> wrapper.readList(Particle::read), (wrapper, particles) -> wrapper.writeList(particles, Particle::write));
/*     */   }
/*     */ 
/*     */   
/*     */   public static final EntityDataType<Vector3f> VECTOR3F;
/*     */   
/*     */   public static final EntityDataType<Quaternion4f> QUATERNION;
/*     */   
/*     */   public static final EntityDataType<ArmadilloState> ARMADILLO_STATE;
/*     */   public static final EntityDataType<List<Particle<?>>> PARTICLES;
/*     */   @Deprecated
/* 249 */   public static final EntityDataType<Integer> WOLF_VARIANT = define("wolf_variant_type", readIntDeserializer(), writeIntSerializer());
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 254 */   public static final EntityDataType<WolfVariant> TYPED_WOLF_VARIANT = define("wolf_variant_type", WolfVariant::read, WolfVariant::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 259 */   public static final EntityDataType<CowVariant> COW_VARIANT = define("cow_variant_type", CowVariant::read, CowVariant::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 264 */   public static final EntityDataType<WolfSoundVariant> WOLF_SOUND_VARIANT = define("wolf_sound_variant_type", WolfSoundVariant::read, WolfSoundVariant::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 269 */   public static final EntityDataType<PigVariant> PIG_VARIANT = define("pig_variant_type", PigVariant::read, PigVariant::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 274 */   public static final EntityDataType<ChickenVariant> CHICKEN_VARIANT = define("chicken_variant_type", ChickenVariant::read, ChickenVariant::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static VersionedRegistry<EntityDataType<?>> getRegistry() {
/* 280 */     return REGISTRY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Collection<EntityDataType<?>> values() {
/* 289 */     return REGISTRY.getEntries();
/*     */   }
/*     */   
/*     */   public static EntityDataType<?> getById(ClientVersion version, int id) {
/* 293 */     return (EntityDataType)REGISTRY.getById(version, id);
/*     */   }
/*     */   
/*     */   public static EntityDataType<?> getByName(String name) {
/* 297 */     return (EntityDataType)REGISTRY.getByName(name);
/*     */   }
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public static <T, Z extends T> EntityDataType<Z> define(String name, PacketWrapper.Reader<Z> reader, PacketWrapper.Writer<T> writer)
/*     */   {
/* 304 */     return (EntityDataType<Z>)REGISTRY.define(name, data -> {
/*     */           Objects.requireNonNull(writer);
/*     */           return new EntityDataType(data, reader, writer::accept);
/*     */         }); } private static PacketWrapper.Reader<Integer> readIntDeserializer() {
/* 308 */     return wrapper -> Integer.valueOf(wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_9) ? wrapper.readVarInt() : wrapper.readInt());
/*     */   }
/*     */ 
/*     */   
/*     */   private static PacketWrapper.Writer<Number> writeIntSerializer() {
/* 313 */     return (wrapper, value) -> {
/*     */         if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_9)) {
/*     */           wrapper.writeVarInt(value.intValue());
/*     */         } else {
/*     */           wrapper.writeInt(value.intValue());
/*     */         } 
/*     */       };
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   private static PacketWrapper.Reader<Optional<String>> readOptionalComponentJSONDeserializer() {
/* 324 */     return wrapper -> wrapper.readJavaOptional(PacketWrapper::readComponentJSON);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   private static PacketWrapper.Writer<Optional<String>> writeOptionalComponentJSONSerializer() {
/* 329 */     return (wrapper, value) -> wrapper.writeJavaOptional(value, PacketWrapper::writeComponentJSON);
/*     */   }
/*     */   
/*     */   private static PacketWrapper.Reader<Optional<Component>> readOptionalComponentDeserializer() {
/* 333 */     return wrapper -> wrapper.readJavaOptional(PacketWrapper::readComponent);
/*     */   }
/*     */   
/*     */   private static PacketWrapper.Writer<Optional<Component>> writeOptionalComponentSerializer() {
/* 337 */     return (wrapper, value) -> wrapper.writeJavaOptional(value, PacketWrapper::writeComponent);
/*     */   }
/*     */   
/*     */   private static PacketWrapper.Reader<Optional<Vector3i>> readOptionalBlockPositionDeserializer() {
/* 341 */     return wrapper -> wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_9) ? wrapper.readJavaOptional(PacketWrapper::readBlockPosition) : wrapper.readJavaOptional(());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static PacketWrapper.Writer<Optional<Vector3i>> writeOptionalBlockPositionSerializer() {
/* 351 */     return (wrapper, value) -> {
/*     */         if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_9)) {
/*     */           wrapper.writeJavaOptional(value, PacketWrapper::writeBlockPosition);
/*     */         } else {
/*     */           wrapper.writeJavaOptional(value, ());
/*     */         } 
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/* 365 */     REGISTRY.unloadMappings();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\data\EntityDataTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */