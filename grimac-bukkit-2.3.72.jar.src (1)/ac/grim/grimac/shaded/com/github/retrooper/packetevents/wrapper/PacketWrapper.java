/*      */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper;
/*      */ 
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.ProtocolPacketEvent;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.VersionComparison;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.channel.ChannelHelper;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.PacketSide;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.ChatType;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.ChatTypes;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.LastSeenMessages;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.MessageSignature;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.Node;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.Parsers;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.RemoteChatSession;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.SignedCommandArgument;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.filter.FilterMask;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.filter.FilterMaskType;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.data.EntityData;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.data.EntityDataType;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.data.EntityMetadataProvider;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.villager.VillagerData;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.villager.profession.VillagerProfession;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.villager.profession.VillagerProfessions;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.villager.type.VillagerType;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.villager.type.VillagerTypes;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStackSerialization;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTEnd;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTLimiter;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.codec.NBTCodec;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.PublicProfileKey;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.data.MerchantItemCost;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.data.MerchantOffer;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.Dimension;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.WorldBlockPosition;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Either;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.KnownPack;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.MathUtil;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.StringUtil;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.adventure.AdventureSerializer;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.crypto.MinecraftEncryptionUtil;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.crypto.SaltSignature;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.crypto.SignatureData;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.GlobalRegistryHolder;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistryHolder;
/*      */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Experimental;
/*      */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*      */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*      */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*      */ import java.lang.reflect.Array;
/*      */ import java.nio.charset.StandardCharsets;
/*      */ import java.security.PublicKey;
/*      */ import java.time.Instant;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.BitSet;
/*      */ import java.util.Collection;
/*      */ import java.util.EnumSet;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Optional;
/*      */ import java.util.UUID;
/*      */ import java.util.function.BiConsumer;
/*      */ import java.util.function.BiFunction;
/*      */ import java.util.function.Function;
/*      */ import java.util.function.IntFunction;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class PacketWrapper<T extends PacketWrapper<T>>
/*      */ {
/*      */   @Nullable
/*      */   public Object buffer;
/*      */   @Internal
/*  115 */   public final Object bufferLock = new Object();
/*      */   
/*      */   protected ClientVersion clientVersion;
/*      */   
/*      */   protected ServerVersion serverVersion;
/*      */   
/*      */   private PacketTypeData packetTypeData;
/*      */   
/*      */   @Nullable
/*      */   protected User user;
/*      */   private static final int MODERN_MESSAGE_LENGTH = 262144;
/*      */   private static final int LEGACY_MESSAGE_LENGTH = 32767;
/*      */   
/*      */   public PacketWrapper(ClientVersion clientVersion, ServerVersion serverVersion, int packetID) {
/*  129 */     if (packetID == -1) {
/*  130 */       throw new IllegalArgumentException("Packet does not exist on this protocol version!");
/*      */     }
/*  132 */     this.clientVersion = clientVersion;
/*  133 */     this.serverVersion = serverVersion;
/*  134 */     this.buffer = null;
/*  135 */     this.packetTypeData = new PacketTypeData(null, packetID);
/*      */   }
/*      */   
/*      */   public PacketWrapper(PacketReceiveEvent event) {
/*  139 */     this(event, true);
/*      */   }
/*      */   
/*      */   public PacketWrapper(PacketReceiveEvent event, boolean readData) {
/*  143 */     this.clientVersion = event.getUser().getClientVersion();
/*  144 */     this.serverVersion = event.getServerVersion();
/*  145 */     this.user = event.getUser();
/*  146 */     this.buffer = event.getByteBuf();
/*  147 */     this.packetTypeData = new PacketTypeData(event.getPacketType(), event.getPacketId());
/*  148 */     if (readData) {
/*  149 */       readEvent((ProtocolPacketEvent)event);
/*      */     }
/*      */   }
/*      */   
/*      */   public PacketWrapper(PacketSendEvent event) {
/*  154 */     this(event, true);
/*      */   }
/*      */   
/*      */   public PacketWrapper(PacketSendEvent event, boolean readData) {
/*  158 */     this.clientVersion = event.getUser().getClientVersion();
/*  159 */     this.serverVersion = event.getServerVersion();
/*  160 */     this.buffer = event.getByteBuf();
/*  161 */     this.packetTypeData = new PacketTypeData(event.getPacketType(), event.getPacketId());
/*  162 */     this.user = event.getUser();
/*  163 */     if (readData) {
/*  164 */       readEvent((ProtocolPacketEvent)event);
/*      */     }
/*      */   }
/*      */   
/*      */   public PacketWrapper(int packetID, ClientVersion clientVersion) {
/*  169 */     this(clientVersion, PacketEvents.getAPI().getServerManager().getVersion(), packetID);
/*      */   }
/*      */   
/*      */   public PacketWrapper(int packetID) {
/*  173 */     if (packetID == -1) {
/*  174 */       throw new IllegalArgumentException("Packet does not exist on this protocol version!");
/*      */     }
/*  176 */     this.clientVersion = ClientVersion.UNKNOWN;
/*  177 */     this.serverVersion = PacketEvents.getAPI().getServerManager().getVersion();
/*  178 */     this.buffer = null;
/*  179 */     this.packetTypeData = new PacketTypeData(null, packetID);
/*      */   }
/*      */   
/*      */   public PacketWrapper(PacketTypeCommon packetType) {
/*  183 */     this.clientVersion = ClientVersion.UNKNOWN;
/*  184 */     this.serverVersion = PacketEvents.getAPI().getServerManager().getVersion();
/*  185 */     this.buffer = null;
/*  186 */     int id = packetType.getId(this.serverVersion.toClientVersion());
/*  187 */     this.packetTypeData = new PacketTypeData(packetType, id);
/*      */   }
/*      */   
/*      */   public static PacketWrapper<?> createDummyWrapper(ClientVersion version) {
/*  191 */     return new PacketWrapper(version, version.toServerVersion(), -2);
/*      */   }
/*      */   
/*      */   public static PacketWrapper<?> createUniversalPacketWrapper(Object byteBuf) {
/*  195 */     return createUniversalPacketWrapper(byteBuf, PacketEvents.getAPI().getServerManager().getVersion());
/*      */   }
/*      */   
/*      */   public static PacketWrapper<?> createUniversalPacketWrapper(Object byteBuf, ServerVersion version) {
/*  199 */     PacketWrapper<?> wrapper = new PacketWrapper(ClientVersion.UNKNOWN, version, -2);
/*  200 */     wrapper.buffer = byteBuf;
/*  201 */     return wrapper;
/*      */   }
/*      */   
/*      */   public static int getChunkX(long chunkKey) {
/*  205 */     return (int)(chunkKey & 0xFFFFFFFFL);
/*      */   }
/*      */   
/*      */   public static int getChunkZ(long chunkKey) {
/*  209 */     return (int)(chunkKey >>> 32L & 0xFFFFFFFFL);
/*      */   }
/*      */   
/*      */   public static long getChunkKey(int chunkX, int chunkZ) {
/*  213 */     return chunkX & 0xFFFFFFFFL | (chunkZ & 0xFFFFFFFFL) << 32L;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   @Internal
/*      */   public final void prepareForSend(Object channel, boolean outgoing, boolean proxy) {
/*  220 */     if (this.buffer == null || ByteBufHelper.refCnt(this.buffer) == 0) {
/*  221 */       this.buffer = ChannelHelper.pooledByteBuf(channel);
/*      */     }
/*      */ 
/*      */     
/*  225 */     if (proxy) {
/*  226 */       User user = PacketEvents.getAPI().getProtocolManager().getUser(channel);
/*  227 */       if (this.packetTypeData.getPacketType() == null)
/*      */       {
/*  229 */         this.packetTypeData.setPacketType(PacketType.getById(outgoing ? PacketSide.SERVER : PacketSide.CLIENT, user
/*  230 */               .getConnectionState(), this.serverVersion.toClientVersion(), this.packetTypeData.getNativePacketId()));
/*      */       }
/*      */       
/*  233 */       this.serverVersion = user.getClientVersion().toServerVersion();
/*  234 */       int id = this.packetTypeData.getPacketType().getId(user.getClientVersion());
/*  235 */       writeVarInt(id);
/*      */     } else {
/*  237 */       writeVarInt(this.packetTypeData.getNativePacketId());
/*      */     } 
/*  239 */     write();
/*      */   }
/*      */   
/*      */   @Internal
/*      */   public final void prepareForSend(Object channel, boolean outgoing) {
/*  244 */     prepareForSend(channel, outgoing, PacketEvents.getAPI().getInjector().isProxy());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void read() {}
/*      */ 
/*      */ 
/*      */   
/*      */   public void write() {}
/*      */ 
/*      */ 
/*      */   
/*      */   public void copy(T wrapper) {}
/*      */ 
/*      */ 
/*      */   
/*      */   public final void readEvent(ProtocolPacketEvent event) {
/*  263 */     PacketWrapper<?> last = event.getLastUsedWrapper();
/*  264 */     if (getClass().isInstance(last)) {
/*  265 */       copy((T)last);
/*      */     } else {
/*  267 */       read();
/*      */     } 
/*  269 */     event.setLastUsedWrapper(this);
/*      */   }
/*      */   
/*      */   public ClientVersion getClientVersion() {
/*  273 */     return this.clientVersion;
/*      */   }
/*      */   
/*      */   public void setClientVersion(ClientVersion clientVersion) {
/*  277 */     this.clientVersion = clientVersion;
/*      */   }
/*      */   
/*      */   public ServerVersion getServerVersion() {
/*  281 */     return this.serverVersion;
/*      */   }
/*      */   
/*      */   public void setServerVersion(ServerVersion serverVersion) {
/*  285 */     this.serverVersion = serverVersion;
/*      */   }
/*      */   
/*      */   public Object getBuffer() {
/*  289 */     return this.buffer;
/*      */   }
/*      */   
/*      */   public void setBuffer(Object buffer) {
/*  293 */     this.buffer = buffer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public int getPacketId() {
/*  304 */     return getNativePacketId();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void setPacketId(int packetID) {
/*  314 */     setNativePacketId(packetID);
/*      */   }
/*      */   
/*      */   public int getNativePacketId() {
/*  318 */     return this.packetTypeData.getNativePacketId();
/*      */   }
/*      */   
/*      */   public void setNativePacketId(int nativePacketId) {
/*  322 */     this.packetTypeData.setNativePacketId(nativePacketId);
/*      */   }
/*      */   
/*      */   @Internal
/*      */   public PacketTypeData getPacketTypeData() {
/*  327 */     return this.packetTypeData;
/*      */   }
/*      */   
/*      */   public int getMaxMessageLength() {
/*  331 */     return this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_13) ? 262144 : 32767;
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public void resetByteBuf() {
/*  336 */     ByteBufHelper.clear(this.buffer);
/*      */   }
/*      */   
/*      */   public void resetBuffer() {
/*  340 */     ByteBufHelper.clear(this.buffer);
/*      */   }
/*      */   
/*      */   public byte readByte() {
/*  344 */     return ByteBufHelper.readByte(this.buffer);
/*      */   }
/*      */   
/*      */   public void writeByte(int value) {
/*  348 */     ByteBufHelper.writeByte(this.buffer, value);
/*      */   }
/*      */   
/*      */   public short readUnsignedByte() {
/*  352 */     return ByteBufHelper.readUnsignedByte(this.buffer);
/*      */   }
/*      */   
/*      */   public boolean readBoolean() {
/*  356 */     return (readByte() != 0);
/*      */   }
/*      */   
/*      */   public void writeBoolean(boolean value) {
/*  360 */     writeByte(value ? 1 : 0);
/*      */   }
/*      */   
/*      */   public int readInt() {
/*  364 */     return ByteBufHelper.readInt(this.buffer);
/*      */   }
/*      */   
/*      */   public void writeInt(int value) {
/*  368 */     ByteBufHelper.writeInt(this.buffer, value);
/*      */   }
/*      */   
/*      */   public int readMedium() {
/*  372 */     return ByteBufHelper.readMedium(this.buffer);
/*      */   }
/*      */   
/*      */   public void writeMedium(int value) {
/*  376 */     ByteBufHelper.writeMedium(this.buffer, value);
/*      */   }
/*      */   
/*      */   public int readVarInt() {
/*  380 */     int value = 0;
/*  381 */     int length = 0;
/*      */     
/*      */     while (true) {
/*  384 */       byte currentByte = readByte();
/*  385 */       value |= (currentByte & Byte.MAX_VALUE) << length * 7;
/*  386 */       length++;
/*  387 */       if (length > 5) {
/*  388 */         throw new RuntimeException("VarInt is too large. Must be smaller than 5 bytes.");
/*      */       }
/*  390 */       if ((currentByte & 0x80) != 128) {
/*  391 */         return value;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeVarInt(int value) {
/*  399 */     if ((value & 0xFFFFFF80) == 0) {
/*  400 */       writeByte(value);
/*  401 */     } else if ((value & 0xFFFFC000) == 0) {
/*  402 */       int w = (value & 0x7F | 0x80) << 8 | value >>> 7;
/*  403 */       writeShort(w);
/*  404 */     } else if ((value & 0xFFE00000) == 0) {
/*  405 */       int w = (value & 0x7F | 0x80) << 16 | (value >>> 7 & 0x7F | 0x80) << 8 | value >>> 14;
/*  406 */       writeMedium(w);
/*  407 */     } else if ((value & 0xF0000000) == 0) {
/*  408 */       int w = (value & 0x7F | 0x80) << 24 | (value >>> 7 & 0x7F | 0x80) << 16 | (value >>> 14 & 0x7F | 0x80) << 8 | value >>> 21;
/*      */       
/*  410 */       writeInt(w);
/*      */     } else {
/*  412 */       int w = (value & 0x7F | 0x80) << 24 | (value >>> 7 & 0x7F | 0x80) << 16 | (value >>> 14 & 0x7F | 0x80) << 8 | value >>> 21 & 0x7F | 0x80;
/*      */       
/*  414 */       writeInt(w);
/*  415 */       writeByte(value >>> 28);
/*      */     } 
/*      */   }
/*      */   
/*      */   public <K, V> Map<K, V> readMap(Reader<K> keyFunction, Reader<V> valueFunction) {
/*  420 */     return readMap(keyFunction, valueFunction, 2147483647);
/*      */   }
/*      */   
/*      */   public <K, V> Map<K, V> readMap(Reader<K> keyFunction, Reader<V> valueFunction, int maxSize) {
/*  424 */     int size = readVarInt();
/*  425 */     if (size > maxSize) {
/*  426 */       throw new RuntimeException(size + " elements exceeded max size of: " + maxSize);
/*      */     }
/*      */     
/*  429 */     Map<K, V> map = new HashMap<>(size);
/*  430 */     for (int i = 0; i < size; i++) {
/*  431 */       K key = keyFunction.apply(this);
/*  432 */       V value = valueFunction.apply(this);
/*  433 */       map.put(key, value);
/*      */     } 
/*  435 */     return map;
/*      */   }
/*      */   
/*      */   public <K, V> void writeOptionalMap(Map<K, Optional<V>> map, Writer<K> keyConsumer, Writer<V> valueConsumer) {
/*  439 */     writeVarInt(map.size());
/*  440 */     for (Map.Entry<K, Optional<V>> entry : map.entrySet()) {
/*  441 */       K key = entry.getKey();
/*  442 */       Optional<V> value = entry.getValue();
/*  443 */       keyConsumer.accept(this, key);
/*  444 */       value.ifPresent(v -> valueConsumer.accept(this, v));
/*      */     } 
/*      */   }
/*      */   
/*      */   public <K, V> void writeMap(Map<K, V> map, Writer<K> keyConsumer, Writer<V> valueConsumer) {
/*  449 */     writeVarInt(map.size());
/*  450 */     for (Map.Entry<K, V> entry : map.entrySet()) {
/*  451 */       K key = entry.getKey();
/*  452 */       V value = entry.getValue();
/*  453 */       keyConsumer.accept(this, key);
/*  454 */       valueConsumer.accept(this, value);
/*      */     } 
/*      */   }
/*      */   
/*      */   public VillagerData readVillagerData() {
/*  459 */     VillagerType type = readMappedEntity((IRegistry<VillagerType>)VillagerTypes.getRegistry());
/*  460 */     VillagerProfession profession = readMappedEntity((IRegistry<VillagerProfession>)VillagerProfessions.getRegistry());
/*  461 */     int level = readVarInt();
/*  462 */     return new VillagerData(type, profession, level);
/*      */   }
/*      */   
/*      */   public void writeVillagerData(VillagerData data) {
/*  466 */     writeMappedEntity((MappedEntity)data.getType());
/*  467 */     writeMappedEntity((MappedEntity)data.getProfession());
/*  468 */     writeVarInt(data.getLevel());
/*      */   }
/*      */   
/*      */   public ItemStack readItemStackModern() {
/*  472 */     return ItemStackSerialization.readModern(this);
/*      */   }
/*      */   
/*      */   public ItemStack readPresentItemStack() {
/*  476 */     ItemStack itemStack = readItemStack();
/*  477 */     if (itemStack.isEmpty()) {
/*  478 */       throw new RuntimeException("Empty ItemStack not allowed");
/*      */     }
/*  480 */     return itemStack;
/*      */   }
/*      */   @NotNull
/*      */   public ItemStack readItemStack() {
/*  484 */     return ItemStackSerialization.read(this);
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeItemStackModern(ItemStack stack) {
/*  489 */     ItemStackSerialization.writeModern(this, stack);
/*      */   }
/*      */   
/*      */   public void writePresentItemStack(ItemStack itemStack) {
/*  493 */     if (itemStack == null || itemStack.isEmpty()) {
/*  494 */       throw new RuntimeException("Empty ItemStack not allowed");
/*      */     }
/*  496 */     writeItemStack(itemStack);
/*      */   }
/*      */   
/*      */   public void writeItemStack(ItemStack stack) {
/*  500 */     ItemStackSerialization.write(this, stack);
/*      */   }
/*      */   
/*      */   public NBTCompound readNBT() {
/*  504 */     return (NBTCompound)readNBTRaw();
/*      */   }
/*      */   @Nullable
/*      */   public NBT readNullableNBT() {
/*  508 */     NBT tag = readNBTRaw();
/*  509 */     return (tag == NBTEnd.INSTANCE) ? null : tag;
/*      */   }
/*      */   
/*      */   public NBT readNBTRaw() {
/*  513 */     return NBTCodec.readNBTFromBuffer(this.buffer, this.serverVersion);
/*      */   }
/*      */   
/*      */   public NBTCompound readUnlimitedNBT() {
/*  517 */     return (NBTCompound)readUnlimitedNBTRaw();
/*      */   }
/*      */   
/*      */   public NBT readUnlimitedNBTRaw() {
/*  521 */     return NBTCodec.readNBTFromBuffer(this.buffer, this.serverVersion, NBTLimiter.noop());
/*      */   }
/*      */   
/*      */   public void writeNBT(NBTCompound nbt) {
/*  525 */     writeNBTRaw((NBT)nbt);
/*      */   }
/*      */   
/*      */   public void writeNBTRaw(NBT nbt) {
/*  529 */     NBTCodec.writeNBTToBuffer(this.buffer, this.serverVersion, nbt);
/*      */   }
/*      */   
/*      */   public String readString() {
/*  533 */     return readString(32767);
/*      */   }
/*      */   
/*      */   public String readString(int maxLen) {
/*  537 */     int j = readVarInt();
/*      */     
/*  539 */     if (j > maxLen * 4)
/*  540 */       throw new RuntimeException("The received encoded string buffer length is longer than maximum allowed (" + j + " > " + (maxLen * 4) + ")"); 
/*  541 */     if (j < 0) {
/*  542 */       throw new RuntimeException("The received encoded string buffer length is less than zero! Weird string!");
/*      */     }
/*  544 */     String s = ByteBufHelper.toString(this.buffer, ByteBufHelper.readerIndex(this.buffer), j, StandardCharsets.UTF_8);
/*  545 */     ByteBufHelper.readerIndex(this.buffer, ByteBufHelper.readerIndex(this.buffer) + j);
/*  546 */     if (s.length() > maxLen) {
/*  547 */       throw new RuntimeException("The received string length is longer than maximum allowed (" + j + " > " + maxLen + ")");
/*      */     }
/*  549 */     return s;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public String readComponentJSON() {
/*  557 */     return getSerializers().asJson(readComponent());
/*      */   }
/*      */   
/*      */   public void writeString(String s) {
/*  561 */     writeString(s, 32767);
/*      */   }
/*      */   
/*      */   public void writeString(String s, int maxLen) {
/*  565 */     writeString(s, maxLen, true);
/*      */   }
/*      */   
/*      */   public void writeString(String s, int maxLen, boolean substr) {
/*  569 */     if (substr) {
/*  570 */       s = StringUtil.maximizeLength(s, maxLen);
/*      */     }
/*  572 */     byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
/*  573 */     if (!substr && bytes.length > maxLen) {
/*  574 */       throw new IllegalStateException("String too big (was " + bytes.length + " bytes encoded, max " + maxLen + ")");
/*      */     }
/*  576 */     writeVarInt(bytes.length);
/*  577 */     ByteBufHelper.writeBytes(this.buffer, bytes);
/*      */   }
/*      */ 
/*      */   
/*      */   public AdventureSerializer getSerializers() {
/*  582 */     return AdventureSerializer.serializer(this);
/*      */   }
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void writeComponentJSON(String json) {
/*  588 */     writeComponent(getSerializers().fromJson(json));
/*      */   }
/*      */   
/*      */   public Component readComponent() {
/*  592 */     return this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20_3) ? 
/*  593 */       readComponentAsNBT() : readComponentAsJSON();
/*      */   }
/*      */   
/*      */   public Component readComponentAsNBT() {
/*  597 */     return getSerializers().fromNbtTag(readNBTRaw(), this);
/*      */   }
/*      */   
/*      */   public Component readComponentAsJSON() {
/*  601 */     String jsonString = readString(getMaxMessageLength());
/*  602 */     return getSerializers().fromJson(jsonString);
/*      */   }
/*      */   
/*      */   public void writeComponent(Component component) {
/*  606 */     if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20_3)) {
/*  607 */       writeComponentAsNBT(component);
/*      */     } else {
/*  609 */       writeComponentAsJSON(component);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void writeComponentAsNBT(Component component) {
/*  614 */     writeNBTRaw(getSerializers().asNbtTag(component, this));
/*      */   }
/*      */   
/*      */   public void writeComponentAsJSON(Component component) {
/*  618 */     String jsonString = getSerializers().asJson(component);
/*  619 */     writeString(jsonString, getMaxMessageLength());
/*      */   }
/*      */   
/*      */   public Style readStyle() {
/*  623 */     return getSerializers().nbt().deserializeStyle(readNBT(), this);
/*      */   }
/*      */   
/*      */   public void writeStyle(Style style) {
/*  627 */     writeNBT(getSerializers().nbt().serializeStyle(style, this));
/*      */   }
/*      */   
/*      */   public ResourceLocation readIdentifier(int maxLen) {
/*  631 */     return new ResourceLocation(readString(maxLen));
/*      */   }
/*      */   
/*      */   public ResourceLocation readIdentifier() {
/*  635 */     return readIdentifier(32767);
/*      */   }
/*      */   
/*      */   public void writeIdentifier(ResourceLocation identifier, int maxLen) {
/*  639 */     writeString(identifier.toString(), maxLen);
/*      */   }
/*      */   
/*      */   public void writeIdentifier(ResourceLocation identifier) {
/*  643 */     writeIdentifier(identifier, 32767);
/*      */   }
/*      */   
/*      */   public int readUnsignedShort() {
/*  647 */     return ByteBufHelper.readUnsignedShort(this.buffer);
/*      */   }
/*      */   
/*      */   public short readShort() {
/*  651 */     return ByteBufHelper.readShort(this.buffer);
/*      */   }
/*      */   
/*      */   public void writeShort(int value) {
/*  655 */     ByteBufHelper.writeShort(this.buffer, value);
/*      */   }
/*      */   
/*      */   public int readVarShort() {
/*  659 */     int low = readUnsignedShort();
/*  660 */     int high = 0;
/*  661 */     if ((low & 0x8000) != 0) {
/*  662 */       low &= 0x7FFF;
/*  663 */       high = readUnsignedByte();
/*      */     } 
/*  665 */     return (high & 0xFF) << 15 | low;
/*      */   }
/*      */   
/*      */   public void writeVarShort(int value) {
/*  669 */     int low = value & 0x7FFF;
/*  670 */     int high = (value & 0x7F8000) >> 15;
/*  671 */     if (high != 0) {
/*  672 */       low |= 0x8000;
/*      */     }
/*  674 */     writeShort(low);
/*  675 */     if (high != 0) {
/*  676 */       writeByte(high);
/*      */     }
/*      */   }
/*      */   
/*      */   public long readLong() {
/*  681 */     return ByteBufHelper.readLong(this.buffer);
/*      */   }
/*      */   
/*      */   public void writeLong(long value) {
/*  685 */     ByteBufHelper.writeLong(this.buffer, value);
/*      */   }
/*      */   
/*      */   public long readVarLong() {
/*  689 */     long value = 0L;
/*  690 */     int size = 0;
/*      */     int b;
/*  692 */     while (((b = readByte()) & 0x80) == 128) {
/*  693 */       value |= (b & 0x7F) << size++ * 7;
/*      */     }
/*  695 */     return value | (b & 0x7F) << size * 7;
/*      */   }
/*      */   
/*      */   public void writeVarLong(long l) {
/*  699 */     while ((l & 0xFFFFFFFFFFFFFF80L) != 0L) {
/*  700 */       writeByte((int)(l & 0x7FL) | 0x80);
/*  701 */       l >>>= 7L;
/*      */     } 
/*      */     
/*  704 */     writeByte((int)l);
/*      */   }
/*      */   
/*      */   public float readFloat() {
/*  708 */     return ByteBufHelper.readFloat(this.buffer);
/*      */   }
/*      */   
/*      */   public void writeFloat(float value) {
/*  712 */     ByteBufHelper.writeFloat(this.buffer, value);
/*      */   }
/*      */   
/*      */   public double readDouble() {
/*  716 */     return ByteBufHelper.readDouble(this.buffer);
/*      */   }
/*      */   
/*      */   public void writeDouble(double value) {
/*  720 */     ByteBufHelper.writeDouble(this.buffer, value);
/*      */   }
/*      */   
/*      */   public byte[] readRemainingBytes() {
/*  724 */     return readBytes(ByteBufHelper.readableBytes(this.buffer));
/*      */   }
/*      */   
/*      */   public byte[] readBytes(int size) {
/*  728 */     byte[] bytes = new byte[size];
/*  729 */     ByteBufHelper.readBytes(this.buffer, bytes);
/*  730 */     return bytes;
/*      */   }
/*      */   
/*      */   public void writeBytes(byte[] array) {
/*  734 */     ByteBufHelper.writeBytes(this.buffer, array);
/*      */   }
/*      */   
/*      */   public byte[] readByteArray(int maxLength) {
/*  738 */     int len = readVarInt();
/*  739 */     if (len > maxLength) {
/*  740 */       throw new RuntimeException("The received byte array length is longer than maximum allowed (" + len + " > " + maxLength + ")");
/*      */     }
/*  742 */     return readBytes(len);
/*      */   }
/*      */   
/*      */   public byte[] readByteArray() {
/*  746 */     return readByteArray(ByteBufHelper.readableBytes(this.buffer));
/*      */   }
/*      */   
/*      */   public void writeByteArray(byte[] array) {
/*  750 */     writeVarInt(array.length);
/*  751 */     writeBytes(array);
/*      */   }
/*      */   
/*      */   public int[] readVarIntArray() {
/*  755 */     int readableBytes = ByteBufHelper.readableBytes(this.buffer);
/*  756 */     int size = readVarInt();
/*  757 */     if (size > readableBytes) {
/*  758 */       throw new IllegalStateException("VarIntArray with size " + size + " is bigger than allowed " + readableBytes);
/*      */     }
/*      */     
/*  761 */     int[] array = new int[size];
/*  762 */     for (int i = 0; i < size; i++) {
/*  763 */       array[i] = readVarInt();
/*      */     }
/*  765 */     return array;
/*      */   }
/*      */   
/*      */   public void writeVarIntArray(int[] array) {
/*  769 */     writeVarInt(array.length);
/*  770 */     for (int i : array) {
/*  771 */       writeVarInt(i);
/*      */     }
/*      */   }
/*      */   
/*      */   public long[] readLongArray(int size) {
/*  776 */     long[] array = new long[size];
/*      */     
/*  778 */     for (int i = 0; i < array.length; i++) {
/*  779 */       array[i] = readLong();
/*      */     }
/*  781 */     return array;
/*      */   }
/*      */   
/*      */   public byte[] readByteArrayOfSize(int size) {
/*  785 */     byte[] array = new byte[size];
/*  786 */     ByteBufHelper.readBytes(this.buffer, array);
/*  787 */     return array;
/*      */   }
/*      */   
/*      */   public void writeByteArrayOfSize(byte[] array) {
/*  791 */     ByteBufHelper.writeBytes(this.buffer, array);
/*      */   }
/*      */   
/*      */   public int[] readVarIntArrayOfSize(int size) {
/*  795 */     int[] array = new int[size];
/*  796 */     for (int i = 0; i < array.length; i++) {
/*  797 */       array[i] = readVarInt();
/*      */     }
/*  799 */     return array;
/*      */   }
/*      */   
/*      */   public void writeVarIntArrayOfSize(int[] array) {
/*  803 */     for (int i : array) {
/*  804 */       writeVarInt(i);
/*      */     }
/*      */   }
/*      */   
/*      */   public long[] readLongArray() {
/*  809 */     int readableBytes = ByteBufHelper.readableBytes(this.buffer) / 8;
/*  810 */     int size = readVarInt();
/*  811 */     if (size > readableBytes) {
/*  812 */       throw new IllegalStateException("LongArray with size " + size + " is bigger than allowed " + readableBytes);
/*      */     }
/*  814 */     long[] array = new long[size];
/*      */     
/*  816 */     for (int i = 0; i < array.length; i++) {
/*  817 */       array[i] = readLong();
/*      */     }
/*  819 */     return array;
/*      */   }
/*      */   
/*      */   public void writeLongArray(long[] array) {
/*  823 */     writeVarInt(array.length);
/*  824 */     for (long l : array) {
/*  825 */       writeLong(l);
/*      */     }
/*      */   }
/*      */   
/*      */   public UUID readUUID() {
/*  830 */     long mostSigBits = readLong();
/*  831 */     long leastSigBits = readLong();
/*  832 */     return new UUID(mostSigBits, leastSigBits);
/*      */   }
/*      */   
/*      */   public void writeUUID(UUID uuid) {
/*  836 */     writeLong(uuid.getMostSignificantBits());
/*  837 */     writeLong(uuid.getLeastSignificantBits());
/*      */   }
/*      */   
/*      */   public Vector3i readBlockPosition() {
/*  841 */     long val = readLong();
/*  842 */     return new Vector3i(val, this.serverVersion);
/*      */   }
/*      */   
/*      */   public void writeBlockPosition(Vector3i pos) {
/*  846 */     long val = pos.getSerializedPosition(this.serverVersion);
/*  847 */     writeLong(val);
/*      */   }
/*      */   
/*      */   public GameMode readGameMode() {
/*  851 */     return GameMode.getById(readByte());
/*      */   }
/*      */   
/*      */   public void writeGameMode(@Nullable GameMode mode) {
/*  855 */     int id = (mode == null) ? -1 : mode.getId();
/*  856 */     writeByte(id);
/*      */   }
/*      */ 
/*      */   
/*      */   public List<EntityData<?>> readEntityMetadata() {
/*  861 */     List<EntityData<?>> list = new ArrayList<>();
/*  862 */     if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_9)) {
/*  863 */       boolean v1_10 = this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_10);
/*      */       short index;
/*  865 */       while ((index = readUnsignedByte()) != 255) {
/*  866 */         int typeID = v1_10 ? readVarInt() : readUnsignedByte();
/*  867 */         EntityDataType<?> type = EntityDataTypes.getById(this.serverVersion.toClientVersion(), typeID);
/*  868 */         if (type == null) {
/*  869 */           throw new IllegalStateException("Unknown entity metadata type id: " + typeID + " version " + this.serverVersion.toClientVersion());
/*      */         }
/*  871 */         list.add(new EntityData(index, type, type.read(this)));
/*      */       } 
/*      */     } else {
/*  874 */       for (byte data = readByte(); data != Byte.MAX_VALUE; data = readByte()) {
/*  875 */         int typeID = (data & 0xE0) >> 5;
/*  876 */         int index = data & 0x1F;
/*  877 */         EntityDataType<?> type = EntityDataTypes.getById(this.serverVersion.toClientVersion(), typeID);
/*  878 */         EntityData<?> entityData = new EntityData(index, type, type.read(this));
/*  879 */         list.add(entityData);
/*      */       } 
/*      */     } 
/*  882 */     return list;
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeEntityMetadata(List<EntityData<?>> list) {
/*  887 */     if (list == null) {
/*  888 */       list = new ArrayList<>();
/*      */     }
/*  890 */     if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_9)) {
/*  891 */       boolean v1_10 = this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_10);
/*  892 */       for (EntityData<?> entityData : list) {
/*  893 */         writeByte(entityData.getIndex());
/*  894 */         if (v1_10) {
/*  895 */           writeVarInt(entityData.getType().getId(this.serverVersion.toClientVersion()));
/*      */         } else {
/*  897 */           writeByte(entityData.getType().getId(this.serverVersion.toClientVersion()));
/*      */         } 
/*  899 */         entityData.getType().write(this, entityData.getValue());
/*      */       } 
/*  901 */       writeByte(255);
/*      */     } else {
/*  903 */       for (EntityData<?> entityData : list) {
/*  904 */         int typeID = entityData.getType().getId(this.serverVersion.toClientVersion());
/*  905 */         int index = entityData.getIndex();
/*  906 */         int data = (typeID << 5 | index & 0x1F) & 0xFF;
/*  907 */         writeByte(data);
/*  908 */         entityData.getType().write(this, entityData.getValue());
/*      */       } 
/*  910 */       writeByte(127);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void writeEntityMetadata(EntityMetadataProvider metadata) {
/*  915 */     writeEntityMetadata(metadata.entityData(this.serverVersion.toClientVersion()));
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public Dimension readDimension() {
/*  920 */     if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20_5)) {
/*  921 */       return new Dimension(readVarInt());
/*      */     }
/*  923 */     if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_19) || this.serverVersion
/*  924 */       .isOlderThan(ServerVersion.V_1_16_2)) {
/*  925 */       Dimension dimension = new Dimension(new NBTCompound());
/*  926 */       dimension.setDimensionName(readIdentifier().toString());
/*  927 */       return dimension;
/*      */     } 
/*  929 */     return new Dimension(readNBT());
/*      */   }
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void writeDimension(Dimension dimension) {
/*  935 */     if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20_5)) {
/*  936 */       writeVarInt(dimension.getId());
/*      */       return;
/*      */     } 
/*  939 */     if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_19) || this.serverVersion
/*  940 */       .isOlderThan(ServerVersion.V_1_16_2)) {
/*  941 */       writeString(dimension.getDimensionName(), 32767);
/*      */     } else {
/*  943 */       writeNBT(dimension.getAttributes());
/*      */     } 
/*      */   }
/*      */   public SaltSignature readSaltSignature() {
/*      */     byte[] signature;
/*  948 */     long salt = readLong();
/*      */ 
/*      */     
/*  951 */     if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_19_3)) {
/*      */       
/*  953 */       if (readBoolean()) {
/*  954 */         signature = readBytes(256);
/*      */       } else {
/*  956 */         signature = new byte[0];
/*      */       } 
/*      */     } else {
/*  959 */       signature = readByteArray(256);
/*      */     } 
/*  961 */     return new SaltSignature(salt, signature);
/*      */   }
/*      */   
/*      */   public void writeSaltSignature(SaltSignature signature) {
/*  965 */     writeLong(signature.getSalt());
/*  966 */     if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_19_3)) {
/*  967 */       boolean present = ((signature.getSignature()).length != 0);
/*  968 */       writeBoolean(present);
/*  969 */       if (present) {
/*  970 */         writeBytes(signature.getSignature());
/*      */       }
/*      */     } else {
/*      */       
/*  974 */       writeByteArray(signature.getSignature());
/*      */     } 
/*      */   }
/*      */   
/*      */   public PublicKey readPublicKey() {
/*  979 */     return MinecraftEncryptionUtil.publicKey(readByteArray(512));
/*      */   }
/*      */   
/*      */   public void writePublicKey(PublicKey publicKey) {
/*  983 */     writeByteArray(publicKey.getEncoded());
/*      */   }
/*      */   
/*      */   public PublicProfileKey readPublicProfileKey() {
/*  987 */     Instant expiresAt = readTimestamp();
/*  988 */     PublicKey key = readPublicKey();
/*  989 */     byte[] keySignature = readByteArray(4096);
/*  990 */     return new PublicProfileKey(expiresAt, key, keySignature);
/*      */   }
/*      */   
/*      */   public void writePublicProfileKey(PublicProfileKey key) {
/*  994 */     writeTimestamp(key.getExpiresAt());
/*  995 */     writePublicKey(key.getKey());
/*  996 */     writeByteArray(key.getKeySignature());
/*      */   }
/*      */   
/*      */   public RemoteChatSession readRemoteChatSession() {
/* 1000 */     return new RemoteChatSession(readUUID(), readPublicProfileKey());
/*      */   }
/*      */   
/*      */   public void writeRemoteChatSession(RemoteChatSession chatSession) {
/* 1004 */     writeUUID(chatSession.getSessionId());
/* 1005 */     writePublicProfileKey(chatSession.getPublicProfileKey());
/*      */   }
/*      */   
/*      */   public Instant readTimestamp() {
/* 1009 */     return Instant.ofEpochMilli(readLong());
/*      */   }
/*      */   
/*      */   public void writeTimestamp(Instant timestamp) {
/* 1013 */     writeLong(timestamp.toEpochMilli());
/*      */   }
/*      */   
/*      */   public SignatureData readSignatureData() {
/* 1017 */     return new SignatureData(readTimestamp(), readPublicKey(), readByteArray(4096));
/*      */   }
/*      */   
/*      */   public void writeSignatureData(SignatureData signatureData) {
/* 1021 */     writeTimestamp(signatureData.getTimestamp());
/* 1022 */     writePublicKey(signatureData.getPublicKey());
/* 1023 */     writeByteArray(signatureData.getSignature());
/*      */   }
/*      */   
/*      */   public static <K> IntFunction<K> limitValue(IntFunction<K> function, int limit) {
/* 1027 */     return i -> {
/*      */         if (i > limit) {
/*      */           throw new RuntimeException("Value " + i + " is larger than limit " + limit);
/*      */         }
/*      */         return function.apply(i);
/*      */       };
/*      */   }
/*      */   
/*      */   public WorldBlockPosition readWorldBlockPosition() {
/* 1036 */     return new WorldBlockPosition(readIdentifier(), readBlockPosition());
/*      */   }
/*      */   
/*      */   public void writeWorldBlockPosition(WorldBlockPosition pos) {
/* 1040 */     writeIdentifier(pos.getWorld());
/* 1041 */     writeBlockPosition(pos.getBlockPosition());
/*      */   }
/*      */   
/*      */   public LastSeenMessages.Entry readLastSeenMessagesEntry() {
/* 1045 */     return new LastSeenMessages.Entry(readUUID(), readByteArray());
/*      */   }
/*      */   
/*      */   public void writeLastMessagesEntry(LastSeenMessages.Entry entry) {
/* 1049 */     writeUUID(entry.getUUID());
/* 1050 */     writeByteArray(entry.getLastVerifier());
/*      */   }
/*      */   
/*      */   public LastSeenMessages.Update readLastSeenMessagesUpdate() {
/* 1054 */     int signedMessages = readVarInt();
/* 1055 */     BitSet seen = BitSet.valueOf(readBytes(3));
/* 1056 */     byte checksum = this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_21_5) ? readByte() : 0;
/* 1057 */     return new LastSeenMessages.Update(signedMessages, seen, checksum);
/*      */   }
/*      */   
/*      */   public void writeLastSeenMessagesUpdate(LastSeenMessages.Update update) {
/* 1061 */     writeVarInt(update.getOffset());
/* 1062 */     writeBytes(Arrays.copyOf(update.getAcknowledged().toByteArray(), 3));
/* 1063 */     if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_21_5)) {
/* 1064 */       writeByte(update.getChecksum());
/*      */     }
/*      */   }
/*      */   
/*      */   public LastSeenMessages.LegacyUpdate readLegacyLastSeenMessagesUpdate() {
/* 1069 */     LastSeenMessages lastSeenMessages = readLastSeenMessages();
/* 1070 */     LastSeenMessages.Entry lastReceived = readOptional(PacketWrapper::readLastSeenMessagesEntry);
/* 1071 */     return new LastSeenMessages.LegacyUpdate(lastSeenMessages, lastReceived);
/*      */   }
/*      */   
/*      */   public void writeLegacyLastSeenMessagesUpdate(LastSeenMessages.LegacyUpdate legacyUpdate) {
/* 1075 */     writeLastSeenMessages(legacyUpdate.getLastSeenMessages());
/* 1076 */     writeOptional(legacyUpdate.getLastReceived(), PacketWrapper::writeLastMessagesEntry);
/*      */   }
/*      */   
/*      */   public MessageSignature readMessageSignature() {
/* 1080 */     if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_19_3)) return new MessageSignature(readBytes(256)); 
/* 1081 */     return new MessageSignature(readByteArray());
/*      */   }
/*      */   
/*      */   public void writeMessageSignature(MessageSignature messageSignature) {
/* 1085 */     writeBytes(messageSignature.getBytes());
/*      */   }
/*      */ 
/*      */   
/*      */   public MessageSignature.Packed readMessageSignaturePacked() {
/* 1090 */     int id = readVarInt() - 1;
/* 1091 */     if (id == -1) {
/* 1092 */       return new MessageSignature.Packed(new MessageSignature(readBytes(256)));
/*      */     }
/* 1094 */     return new MessageSignature.Packed(id);
/*      */   }
/*      */   
/*      */   public void writeMessageSignaturePacked(MessageSignature.Packed messageSignaturePacked) {
/* 1098 */     writeVarInt(messageSignaturePacked.getId() + 1);
/* 1099 */     if (messageSignaturePacked.getFullSignature().isPresent()) {
/* 1100 */       writeBytes(((MessageSignature)messageSignaturePacked.getFullSignature().get()).getBytes());
/*      */     }
/*      */   }
/*      */   
/*      */   public LastSeenMessages.Packed readLastSeenMessagesPacked() {
/* 1105 */     List<MessageSignature.Packed> packedMessageSignatures = readCollection(limitValue(ArrayList::new, 20), PacketWrapper::readMessageSignaturePacked);
/* 1106 */     return new LastSeenMessages.Packed(packedMessageSignatures);
/*      */   }
/*      */   
/*      */   public void writeLastSeenMessagesPacked(LastSeenMessages.Packed lastSeenMessagesPacked) {
/* 1110 */     writeCollection(lastSeenMessagesPacked.getPackedMessageSignatures(), PacketWrapper::writeMessageSignaturePacked);
/*      */   }
/*      */   
/*      */   public LastSeenMessages readLastSeenMessages() {
/* 1114 */     List<LastSeenMessages.Entry> entries = readCollection(limitValue(ArrayList::new, 5), PacketWrapper::readLastSeenMessagesEntry);
/*      */     
/* 1116 */     return new LastSeenMessages(entries);
/*      */   }
/*      */   
/*      */   public void writeLastSeenMessages(LastSeenMessages lastSeenMessages) {
/* 1120 */     writeCollection(lastSeenMessages.getEntries(), PacketWrapper::writeLastMessagesEntry);
/*      */   }
/*      */   
/*      */   public List<SignedCommandArgument> readSignedCommandArguments() {
/* 1124 */     return readCollection(limitValue(ArrayList::new, 8), _packet -> new SignedCommandArgument(readString(16), readMessageSignature()));
/*      */   }
/*      */   
/*      */   public void writeSignedCommandArguments(List<SignedCommandArgument> signedArguments) {
/* 1128 */     writeCollection(signedArguments, (_packet, argument) -> {
/*      */           writeString(argument.getArgument(), 16);
/*      */           writeMessageSignature(argument.getSignature());
/*      */         });
/*      */   }
/*      */   
/*      */   public BitSet readBitSet() {
/* 1135 */     return BitSet.valueOf(readLongArray());
/*      */   }
/*      */   
/*      */   public void writeBitSet(BitSet bitSet) {
/* 1139 */     writeLongArray(bitSet.toLongArray());
/*      */   } @FunctionalInterface
/*      */   public static interface Reader<T> extends Function<PacketWrapper<?>, T> {}
/*      */   public FilterMask readFilterMask() {
/* 1143 */     FilterMaskType type = FilterMaskType.getById(readVarInt());
/* 1144 */     switch (type) {
/*      */       case PARTIALLY_FILTERED:
/* 1146 */         return new FilterMask(readBitSet());
/*      */       case PASS_THROUGH:
/* 1148 */         return FilterMask.PASS_THROUGH;
/*      */       case FULLY_FILTERED:
/* 1150 */         return FilterMask.FULLY_FILTERED;
/*      */     } 
/* 1152 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeFilterMask(FilterMask filterMask) {
/* 1157 */     writeVarInt(filterMask.getType().getId());
/* 1158 */     if (filterMask.getType() == FilterMaskType.PARTIALLY_FILTERED)
/* 1159 */       writeBitSet(filterMask.getMask()); 
/*      */   }
/*      */   @FunctionalInterface
/*      */   public static interface Writer<T> extends BiConsumer<PacketWrapper<?>, T> {}
/*      */   public MerchantOffer readMerchantOffer() {
/* 1164 */     ItemStack buyItemPrimary = MerchantItemCost.readItem(this);
/* 1165 */     ItemStack sellItem = readItemStack();
/*      */ 
/*      */     
/* 1168 */     ItemStack buyItemSecondary = (getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_20_5) || getServerVersion().isOlderThan(ServerVersion.V_1_19)) ? readOptional(MerchantItemCost::readItem) : readItemStack();
/* 1169 */     boolean tradeDisabled = readBoolean();
/* 1170 */     int uses = readInt();
/* 1171 */     int maxUses = readInt();
/* 1172 */     int xp = readInt();
/* 1173 */     int specialPrice = readInt();
/* 1174 */     float priceMultiplier = readFloat();
/* 1175 */     int demand = readInt();
/* 1176 */     MerchantOffer data = MerchantOffer.of(buyItemPrimary, buyItemSecondary, sellItem, uses, maxUses, xp, specialPrice, priceMultiplier, demand);
/*      */     
/* 1178 */     if (tradeDisabled) {
/* 1179 */       data.setUses(data.getMaxUses());
/*      */     }
/* 1181 */     return data;
/*      */   }
/*      */   
/*      */   public void writeMerchantOffer(MerchantOffer data) {
/* 1185 */     MerchantItemCost.writeItem(this, data.getFirstInputItem());
/* 1186 */     writeItemStack(data.getOutputItem());
/* 1187 */     ItemStack buyItemSecondary = data.getSecondInputItem();
/*      */ 
/*      */     
/* 1190 */     if (buyItemSecondary != null && buyItemSecondary.isEmpty()) {
/* 1191 */       buyItemSecondary = null;
/*      */     }
/* 1193 */     if (getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_20_5) || 
/* 1194 */       getServerVersion().isOlderThan(ServerVersion.V_1_19)) {
/* 1195 */       writeOptional(buyItemSecondary, MerchantItemCost::writeItem);
/*      */     } else {
/* 1197 */       writeItemStack(buyItemSecondary);
/*      */     } 
/* 1199 */     writeBoolean((data.getUses() >= data.getMaxUses()));
/* 1200 */     writeInt(data.getUses());
/* 1201 */     writeInt(data.getMaxUses());
/* 1202 */     writeInt(data.getXp());
/* 1203 */     writeInt(data.getSpecialPrice());
/* 1204 */     writeFloat(data.getPriceMultiplier());
/* 1205 */     writeInt(data.getDemand());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public ChatType.Bound readChatTypeBoundNetwork() {
/* 1211 */     ChatType type = this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_21) ? readMappedEntityOrDirect((IRegistry<ChatType>)ChatTypes.getRegistry(), ChatType::readDirect) : readMappedEntity((IRegistry<ChatType>)ChatTypes.getRegistry());
/* 1212 */     Component name = readComponent();
/* 1213 */     Component targetName = readOptional(PacketWrapper::readComponent);
/* 1214 */     return new ChatType.Bound(type, name, targetName);
/*      */   }
/*      */   
/*      */   public void writeChatTypeBoundNetwork(ChatType.Bound chatFormatting) {
/* 1218 */     if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_21)) {
/* 1219 */       writeMappedEntityOrDirect(chatFormatting.getType(), ChatType::writeDirect);
/*      */     } else {
/* 1221 */       writeMappedEntity((MappedEntity)chatFormatting.getType());
/*      */     } 
/* 1223 */     writeComponent(chatFormatting.getName());
/* 1224 */     writeOptional(chatFormatting.getTargetName(), PacketWrapper::writeComponent);
/*      */   }
/*      */   
/*      */   public Node readNode() {
/* 1228 */     byte flags = readByte();
/* 1229 */     int nodeType = flags & 0x3;
/*      */     
/* 1231 */     List<Integer> children = readList(PacketWrapper::readVarInt);
/*      */     
/* 1233 */     int redirectNodeIndex = ((flags & 0x8) != 0) ? readVarInt() : 0;
/* 1234 */     if (nodeType == 2) {
/* 1235 */       String name = readString();
/*      */ 
/*      */       
/* 1238 */       Parsers.Parser parser = this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_19) ? readMappedEntity(Parsers::getById) : Parsers.getByName(readIdentifier().toString());
/* 1239 */       List<Object> properties = parser.readProperties(this).orElse(null);
/* 1240 */       ResourceLocation suggestionType = ((flags & 0x10) != 0) ? readIdentifier() : null;
/* 1241 */       return new Node(flags, children, redirectNodeIndex, name, parser, properties, suggestionType);
/* 1242 */     }  if (nodeType == 1) {
/* 1243 */       String name = readString();
/* 1244 */       return new Node(flags, children, redirectNodeIndex, name, (Parsers.Parser)null, null, null);
/*      */     } 
/* 1246 */     return new Node(flags, children, redirectNodeIndex, null, (Parsers.Parser)null, null, null);
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeNode(Node node) {
/* 1251 */     writeByte(node.getFlags());
/* 1252 */     writeList(node.getChildren(), PacketWrapper::writeVarInt);
/* 1253 */     if ((node.getFlags() & 0x8) != 0) {
/* 1254 */       writeVarInt(node.getRedirectNodeIndex());
/*      */     }
/* 1256 */     node.getName().ifPresent(this::writeString);
/* 1257 */     if (node.getParser().isPresent()) {
/* 1258 */       Parsers.Parser parser = node.getParser().get();
/* 1259 */       if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_19)) {
/* 1260 */         writeMappedEntity((MappedEntity)parser);
/*      */       } else {
/* 1262 */         writeIdentifier(parser.getName());
/*      */       } 
/* 1264 */       if (node.getProperties().isPresent()) {
/* 1265 */         parser.writeProperties(this, node.getProperties().get());
/*      */       }
/*      */     } 
/* 1268 */     node.getSuggestionsType().ifPresent(this::writeIdentifier);
/*      */   }
/*      */   
/*      */   public KnownPack readKnownPack() {
/* 1272 */     String namespace = readString();
/* 1273 */     String id = readString();
/* 1274 */     String version = readString();
/* 1275 */     return new KnownPack(namespace, id, version);
/*      */   }
/*      */   
/*      */   public void writeKnownPack(KnownPack knownPack) {
/* 1279 */     writeString(knownPack.getNamespace());
/* 1280 */     writeString(knownPack.getId());
/* 1281 */     writeString(knownPack.getVersion());
/*      */   }
/*      */   
/*      */   public <T extends Enum<T>> EnumSet<T> readEnumSet(Class<T> enumClazz) {
/* 1285 */     Enum[] arrayOfEnum = (Enum[])enumClazz.getEnumConstants();
/* 1286 */     byte[] bytes = new byte[-Math.floorDiv(-arrayOfEnum.length, 8)];
/* 1287 */     ByteBufHelper.readBytes(getBuffer(), bytes);
/* 1288 */     BitSet bitSet = BitSet.valueOf(bytes);
/* 1289 */     EnumSet<T> set = EnumSet.noneOf(enumClazz);
/* 1290 */     for (int i = 0; i < arrayOfEnum.length; i++) {
/* 1291 */       if (bitSet.get(i)) {
/* 1292 */         set.add((T)arrayOfEnum[i]);
/*      */       }
/*      */     } 
/* 1295 */     return set;
/*      */   }
/*      */   
/*      */   public <T extends Enum<T>> void writeEnumSet(EnumSet<T> set, Class<T> enumClazz) {
/* 1299 */     Enum[] arrayOfEnum = (Enum[])enumClazz.getEnumConstants();
/* 1300 */     BitSet bitSet = new BitSet(arrayOfEnum.length);
/* 1301 */     for (int i = 0; i < arrayOfEnum.length; i++) {
/* 1302 */       if (set.contains(arrayOfEnum[i])) {
/* 1303 */         bitSet.set(i);
/*      */       }
/*      */     } 
/* 1306 */     writeBytes(Arrays.copyOf(bitSet.toByteArray(), -Math.floorDiv(-arrayOfEnum.length, 8)));
/*      */   }
/*      */   
/*      */   @Experimental
/*      */   public <U, V, R> U readMultiVersional(VersionComparison version, ServerVersion target, Reader<V> first, Reader<R> second) {
/* 1311 */     if (this.serverVersion.is(version, target)) {
/* 1312 */       return (U)first.apply(this);
/*      */     }
/* 1314 */     return (U)second.apply(this);
/*      */   }
/*      */ 
/*      */   
/*      */   @Experimental
/*      */   public <V> void writeMultiVersional(VersionComparison version, ServerVersion target, V value, Writer<V> first, Writer<V> second) {
/* 1320 */     if (this.serverVersion.is(version, target)) {
/* 1321 */       first.accept(this, value);
/*      */     } else {
/* 1323 */       second.accept(this, value);
/*      */     } 
/*      */   }
/*      */   @Nullable
/*      */   public <R> R readOptional(Reader<R> reader) {
/* 1328 */     return readBoolean() ? reader.apply(this) : null;
/*      */   }
/*      */   
/*      */   public <V> void writeOptional(@Nullable V value, Writer<V> writer) {
/* 1332 */     if (value != null) {
/* 1333 */       writeBoolean(true);
/* 1334 */       writer.accept(this, value);
/*      */     } else {
/* 1336 */       writeBoolean(false);
/*      */     } 
/*      */   }
/*      */   
/*      */   public <R> Optional<R> readJavaOptional(Reader<R> reader) {
/* 1341 */     return readBoolean() ? Optional.<R>of(reader.apply(this)) : Optional.<R>empty();
/*      */   }
/*      */   
/*      */   public <V> void writeJavaOptional(Optional<V> value, Writer<V> writer) {
/* 1345 */     if (value.isPresent()) {
/* 1346 */       writeBoolean(true);
/* 1347 */       writer.accept(this, value.get());
/*      */     } else {
/* 1349 */       writeBoolean(false);
/*      */     } 
/*      */   }
/*      */   
/*      */   public <K, C extends Collection<K>> C readCollection(IntFunction<C> function, Reader<K> reader) {
/* 1354 */     int size = readVarInt();
/* 1355 */     return _readCollection(function, reader, size);
/*      */   }
/*      */   
/*      */   public <K, C extends Collection<K>> C readCollection(IntFunction<C> function, Reader<K> reader, int maxSize) {
/* 1359 */     int size = readVarInt();
/* 1360 */     if (size > maxSize) {
/* 1361 */       throw new RuntimeException(size + " elements exceeded max size of: " + maxSize);
/*      */     }
/* 1363 */     return _readCollection(function, reader, size);
/*      */   }
/*      */   
/*      */   private <K, C extends Collection<K>> C _readCollection(IntFunction<C> function, Reader<K> reader, int size) {
/* 1367 */     Collection<K> collection = (Collection<K>)function.apply(size);
/* 1368 */     for (int i = 0; i < size; i++) {
/* 1369 */       collection.add(reader.apply(this));
/*      */     }
/* 1371 */     return (C)collection;
/*      */   }
/*      */   
/*      */   public <K> void writeCollection(Collection<K> collection, Writer<K> writer) {
/* 1375 */     writeVarInt(collection.size());
/* 1376 */     for (K key : collection) {
/* 1377 */       writer.accept(this, key);
/*      */     }
/*      */   }
/*      */   
/*      */   public <K> List<K> readList(Reader<K> reader) {
/* 1382 */     return readCollection(ArrayList::new, reader);
/*      */   }
/*      */   
/*      */   public <K> List<K> readList(Reader<K> reader, int maxSize) {
/* 1386 */     return readCollection(ArrayList::new, reader, maxSize);
/*      */   }
/*      */   
/*      */   public <K> void writeList(List<K> list, Writer<K> writer) {
/* 1390 */     writeVarInt(list.size());
/* 1391 */     for (K key : list) {
/* 1392 */       writer.accept(this, key);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public <K> K[] readArray(Reader<K> reader, Class<K> clazz) {
/* 1398 */     int length = readVarInt();
/* 1399 */     K[] array = (K[])Array.newInstance(clazz, length);
/* 1400 */     for (int i = 0; i < length; i++) {
/* 1401 */       array[i] = reader.apply(this);
/*      */     }
/* 1403 */     return array;
/*      */   }
/*      */   
/*      */   public <K> void writeArray(K[] array, Writer<K> writer) {
/* 1407 */     writeVarInt(array.length);
/* 1408 */     for (K element : array) {
/* 1409 */       writer.accept(this, element);
/*      */     }
/*      */   }
/*      */   
/*      */   public <Z extends Enum<?>> Z readEnum(Class<Z> clazz) {
/* 1414 */     return readEnum(clazz.getEnumConstants());
/*      */   }
/*      */   
/*      */   public <Z extends Enum<?>> Z readEnum(Z[] values) {
/* 1418 */     return values[readVarInt()];
/*      */   }
/*      */   
/*      */   public void writeEnum(Enum<?> value) {
/* 1422 */     writeVarInt(value.ordinal());
/*      */   }
/*      */   
/*      */   public <Z extends MappedEntity> Z readMappedEntity(BiFunction<ClientVersion, Integer, Z> getter) {
/* 1426 */     int id = readVarInt();
/* 1427 */     MappedEntity mappedEntity = (MappedEntity)getter.apply(this.serverVersion.toClientVersion(), Integer.valueOf(id));
/* 1428 */     if (mappedEntity == null) {
/* 1429 */       throw new IllegalStateException("Can't find mapped entity with id " + id + " using " + getter);
/*      */     }
/* 1431 */     return (Z)mappedEntity;
/*      */   }
/*      */   
/*      */   public <Z extends MappedEntity> IRegistry<Z> replaceRegistry(IRegistry<Z> registry) {
/* 1435 */     return getRegistryHolder().getRegistryOr(registry, this.serverVersion.toClientVersion());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public IRegistryHolder getRegistryHolder() {
/* 1442 */     return (this.user != null) ? (IRegistryHolder)this.user : GlobalRegistryHolder.INSTANCE;
/*      */   }
/*      */ 
/*      */   
/*      */   public <Z extends MappedEntity> Z readMappedEntityOrDirect(BiFunction<ClientVersion, Integer, Z> getter, Reader<Z> directReader) {
/* 1447 */     int id = readVarInt();
/* 1448 */     if (id == 0) {
/* 1449 */       return directReader.apply(this);
/*      */     }
/* 1451 */     MappedEntity mappedEntity = (MappedEntity)getter.apply(this.serverVersion.toClientVersion(), Integer.valueOf(id - 1));
/* 1452 */     if (mappedEntity == null) {
/* 1453 */       throw new IllegalStateException("Can't find mapped entity with id " + id + " using " + getter);
/*      */     }
/* 1455 */     return (Z)mappedEntity;
/*      */   }
/*      */   
/*      */   public <Z extends MappedEntity> Z readMappedEntity(IRegistry<Z> registry) {
/* 1459 */     IRegistry<Z> replacedRegistry = getRegistryHolder().getRegistryOr(registry, this.serverVersion.toClientVersion());
/* 1460 */     return readMappedEntity((BiFunction)replacedRegistry);
/*      */   }
/*      */   
/*      */   public <Z extends MappedEntity> Z readMappedEntityOrDirect(IRegistry<Z> registry, Reader<Z> directReader) {
/* 1464 */     IRegistry<Z> replacedRegistry = getRegistryHolder().getRegistryOr(registry, this.serverVersion.toClientVersion());
/* 1465 */     return readMappedEntityOrDirect((BiFunction)replacedRegistry, directReader);
/*      */   }
/*      */   
/*      */   public void writeMappedEntity(MappedEntity entity) {
/* 1469 */     if (!entity.isRegistered()) {
/* 1470 */       throw new IllegalArgumentException("Can't write id of unregistered entity " + entity
/* 1471 */           .getName() + " (" + entity + ")");
/*      */     }
/* 1473 */     writeVarInt(entity.getId(this.serverVersion.toClientVersion()));
/*      */   }
/*      */   
/*      */   public <Z extends MappedEntity> void writeMappedEntityOrDirect(Z entity, Writer<Z> writer) {
/* 1477 */     if (!entity.isRegistered()) {
/* 1478 */       writeVarInt(0);
/* 1479 */       writer.accept(this, entity);
/*      */       return;
/*      */     } 
/* 1482 */     int id = entity.getId(this.serverVersion.toClientVersion());
/* 1483 */     writeVarInt(id + 1);
/*      */   }
/*      */   
/*      */   public int readContainerId() {
/* 1487 */     if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_21_2)) {
/* 1488 */       return readVarInt();
/*      */     }
/* 1490 */     return readUnsignedByte();
/*      */   }
/*      */   
/*      */   public void writeContainerId(int containerId) {
/* 1494 */     if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_21_2)) {
/* 1495 */       writeVarInt(containerId);
/*      */     } else {
/* 1497 */       writeByte(containerId);
/*      */     } 
/*      */   }
/*      */   
/*      */   public <L, R> Either<L, R> readEither(Reader<L> leftReader, Reader<R> rightReader) {
/* 1502 */     return readBoolean() ? 
/* 1503 */       Either.createLeft(leftReader.apply(this)) : 
/* 1504 */       Either.createRight(rightReader.apply(this));
/*      */   }
/*      */   
/*      */   public <L, R> void writeEither(Either<L, R> either, Writer<L> leftWriter, Writer<R> rightWriter) {
/* 1508 */     if (either.isLeft()) {
/* 1509 */       writeBoolean(true);
/* 1510 */       leftWriter.accept(this, (L)either.getLeft());
/*      */     } else {
/* 1512 */       writeBoolean(false);
/* 1513 */       rightWriter.accept(this, (R)either.getRight());
/*      */     } 
/*      */   }
/*      */   
/*      */   public void writeRotation(float rotation) {
/* 1518 */     writeByte((byte)MathUtil.floor(rotation * 256.0F / 360.0F));
/*      */   }
/*      */   
/*      */   public float readRotation() {
/* 1522 */     return (readByte() * 360) / 256.0F;
/*      */   }
/*      */   @Nullable
/*      */   public Integer readNullableVarInt() {
/* 1526 */     int i = readVarInt();
/* 1527 */     return (i == 0) ? null : Integer.valueOf(i - 1);
/*      */   }
/*      */   
/*      */   public void writeNullableVarInt(@Nullable Integer i) {
/* 1531 */     writeVarInt((i == null) ? 0 : (i.intValue() + 1));
/*      */   }
/*      */   
/*      */   public <Z> Z readLengthPrefixed(int maxLength, Reader<Z> reader) {
/* 1535 */     int length = readVarInt();
/* 1536 */     if (length > maxLength) {
/* 1537 */       throw new RuntimeException("Buffer size " + length + " is larger than allowed limit of " + maxLength);
/*      */     }
/* 1539 */     Object prevBuffer = this.buffer;
/*      */     
/*      */     try {
/* 1542 */       this.buffer = ByteBufHelper.readSlice(prevBuffer, length);
/* 1543 */       return reader.apply(this);
/*      */     } finally {
/* 1545 */       this.buffer = prevBuffer;
/*      */     } 
/*      */   }
/*      */   
/*      */   public <Z> void writeLengthPrefixed(Z value, Writer<Z> writer) {
/* 1550 */     Object payloadBuffer = ByteBufHelper.allocateNewBuffer(this.buffer);
/* 1551 */     Object prevBuffer = this.buffer;
/*      */     
/*      */     try {
/* 1554 */       this.buffer = payloadBuffer;
/* 1555 */       writer.accept(this, value);
/*      */     } finally {
/* 1557 */       this.buffer = prevBuffer;
/*      */     } 
/*      */     
/* 1560 */     writeVarInt(ByteBufHelper.readableBytes(payloadBuffer));
/* 1561 */     ByteBufHelper.writeBytes(prevBuffer, payloadBuffer);
/*      */   }
/*      */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\wrapper\PacketWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */