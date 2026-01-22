/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEventsAPI;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.channel.ChannelHelper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.ConnectionState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.ChatType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.ChatTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.ChatMessage;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.ChatMessageLegacy;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_16;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTList;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.Dimension;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.dimension.DimensionType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.dimension.DimensionTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.adventure.AdventureSerializer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistryHolder;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCloseWindow;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetTitleSubtitle;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetTitleText;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetTitleTimes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTitle;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TextComponent;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ public class User
/*     */   implements IRegistryHolder
/*     */ {
/*     */   private final Object channel;
/*     */   private ConnectionState decoderState;
/*     */   private ConnectionState encoderState;
/*     */   private ClientVersion clientVersion;
/*     */   private final UserProfile profile;
/*  66 */   private int entityId = -1;
/*     */   
/*  68 */   private DimensionType dimensionType = DimensionTypes.OVERWORLD;
/*  69 */   private final Map<ResourceLocation, IRegistry<?>> registries = new HashMap<>();
/*     */ 
/*     */ 
/*     */   
/*     */   public User(Object channel, ConnectionState connectionState, ClientVersion clientVersion, UserProfile profile) {
/*  74 */     this.channel = channel;
/*  75 */     this.decoderState = connectionState;
/*  76 */     this.encoderState = connectionState;
/*  77 */     this.clientVersion = clientVersion;
/*  78 */     this.profile = profile;
/*     */   }
/*     */   
/*     */   @Internal
/*     */   @Nullable
/*     */   public IRegistry<?> getRegistry(ResourceLocation registryKey, ClientVersion version) {
/*  84 */     return this.registries.get(registryKey);
/*     */   }
/*     */   
/*     */   @Internal
/*     */   public void putRegistry(IRegistry<?> registry) {
/*  89 */     this.registries.put(registry.getRegistryKey(), registry);
/*     */   }
/*     */   
/*     */   public Object getChannel() {
/*  93 */     return this.channel;
/*     */   }
/*     */   
/*     */   public InetSocketAddress getAddress() {
/*  97 */     return (InetSocketAddress)ChannelHelper.remoteAddress(this.channel);
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
/*     */   
/*     */   @Obsolete
/*     */   public ConnectionState getConnectionState() throws IllegalStateException {
/* 111 */     ConnectionState decoderState = this.decoderState;
/* 112 */     ConnectionState encoderState = this.encoderState;
/* 113 */     if (decoderState != encoderState) {
/* 114 */       throw new IllegalStateException("Can't get common connection state: " + decoderState + " != " + encoderState);
/*     */     }
/* 116 */     return decoderState;
/*     */   }
/*     */   
/*     */   @Internal
/*     */   public void setConnectionState(ConnectionState connectionState) {
/* 121 */     setDecoderState(connectionState);
/* 122 */     setEncoderState(connectionState);
/*     */   }
/*     */   
/*     */   public ConnectionState getDecoderState() {
/* 126 */     return this.decoderState;
/*     */   }
/*     */   
/*     */   @Internal
/*     */   public void setDecoderState(ConnectionState decoderState) {
/* 131 */     this.decoderState = decoderState;
/* 132 */     PacketEvents.getAPI().getLogManager().debug("Transitioned " + 
/* 133 */         getName() + "'s decoder into " + decoderState + " state!");
/*     */   }
/*     */   
/*     */   public ConnectionState getEncoderState() {
/* 137 */     return this.encoderState;
/*     */   }
/*     */   
/*     */   @Internal
/*     */   public void setEncoderState(ConnectionState encoderState) {
/* 142 */     this.encoderState = encoderState;
/* 143 */     PacketEvents.getAPI().getLogManager().debug("Transitioned " + 
/* 144 */         getName() + "'s encoder into " + encoderState + " state!");
/*     */   }
/*     */   
/*     */   public ClientVersion getClientVersion() {
/* 148 */     return this.clientVersion;
/*     */   }
/*     */   
/*     */   public void setClientVersion(ClientVersion clientVersion) {
/* 152 */     this.clientVersion = clientVersion;
/*     */   }
/*     */   
/*     */   public UserProfile getProfile() {
/* 156 */     return this.profile;
/*     */   }
/*     */   
/*     */   public String getName() {
/* 160 */     return this.profile.getName();
/*     */   }
/*     */   
/*     */   public UUID getUUID() {
/* 164 */     return this.profile.getUUID();
/*     */   }
/*     */   
/*     */   public int getEntityId() {
/* 168 */     return this.entityId;
/*     */   }
/*     */   
/*     */   public void setEntityId(int entityId) {
/* 172 */     this.entityId = entityId;
/*     */   }
/*     */   
/*     */   public void sendPacket(Object buffer) {
/* 176 */     PacketEvents.getAPI().getProtocolManager().sendPacket(this.channel, buffer);
/*     */   }
/*     */   
/*     */   public void sendPacket(PacketWrapper<?> wrapper) {
/* 180 */     PacketEvents.getAPI().getProtocolManager().sendPacket(this.channel, wrapper);
/*     */   }
/*     */   
/*     */   public void sendPacketSilently(Object buffer) {
/* 184 */     PacketEvents.getAPI().getProtocolManager().sendPacketSilently(this.channel, buffer);
/*     */   }
/*     */   
/*     */   public void sendPacketSilently(PacketWrapper<?> wrapper) {
/* 188 */     PacketEvents.getAPI().getProtocolManager().sendPacketSilently(this.channel, wrapper);
/*     */   }
/*     */   
/*     */   public void writePacket(Object buffer) {
/* 192 */     PacketEvents.getAPI().getProtocolManager().writePacket(this.channel, buffer);
/*     */   }
/*     */   
/*     */   public void writePacket(PacketWrapper<?> wrapper) {
/* 196 */     PacketEvents.getAPI().getProtocolManager().writePacket(this.channel, wrapper);
/*     */   }
/*     */   
/*     */   public void writePacketSilently(Object buffer) {
/* 200 */     PacketEvents.getAPI().getProtocolManager().writePacketSilently(this.channel, buffer);
/*     */   }
/*     */   
/*     */   public void writePacketSilently(PacketWrapper<?> wrapper) {
/* 204 */     PacketEvents.getAPI().getProtocolManager().writePacketSilently(this.channel, wrapper);
/*     */   }
/*     */   
/*     */   public void receivePacket(Object buffer) {
/* 208 */     PacketEvents.getAPI().getProtocolManager().receivePacket(this.channel, buffer);
/*     */   }
/*     */   
/*     */   public void receivePacket(PacketWrapper<?> wrapper) {
/* 212 */     PacketEvents.getAPI().getProtocolManager().receivePacket(this.channel, wrapper);
/*     */   }
/*     */   
/*     */   public void receivePacketSilently(Object buffer) {
/* 216 */     PacketEvents.getAPI().getProtocolManager().receivePacketSilently(this.channel, buffer);
/*     */   }
/*     */   
/*     */   public void receivePacketSilently(PacketWrapper<?> wrapper) {
/* 220 */     PacketEvents.getAPI().getProtocolManager().receivePacketSilently(this.channel, wrapper);
/*     */   }
/*     */   
/*     */   public void flushPackets() {
/* 224 */     ChannelHelper.flush(this.channel);
/*     */   }
/*     */   
/*     */   public void closeConnection() {
/* 228 */     ChannelHelper.close(this.channel);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeInventory() {
/* 239 */     WrapperPlayServerCloseWindow closeWindow = new WrapperPlayServerCloseWindow(0);
/* 240 */     PacketEvents.getAPI().getProtocolManager().sendPacket(this.channel, (PacketWrapper)closeWindow);
/*     */   }
/*     */   
/*     */   public void sendMessage(String legacyMessage) {
/* 244 */     sendMessage(getSerializers().fromLegacy(legacyMessage));
/*     */   }
/*     */   
/*     */   public void sendMessage(Component component) {
/* 248 */     sendMessage(component, ChatTypes.CHAT);
/*     */   }
/*     */   public void sendMessage(Component component, ChatType type) {
/*     */     WrapperPlayServerChatMessage wrapperPlayServerChatMessage;
/* 252 */     ClientVersion version = getPacketVersion();
/*     */     
/* 254 */     if (version.isNewerThanOrEquals(ClientVersion.V_1_19)) {
/* 255 */       WrapperPlayServerSystemChatMessage wrapperPlayServerSystemChatMessage = new WrapperPlayServerSystemChatMessage(false, component);
/*     */     } else {
/*     */       ChatMessageLegacy chatMessageLegacy;
/* 258 */       if (version.isNewerThanOrEquals(ClientVersion.V_1_16)) {
/* 259 */         ChatMessage_v1_16 chatMessage_v1_16 = new ChatMessage_v1_16(component, type, new UUID(0L, 0L));
/*     */       } else {
/* 261 */         chatMessageLegacy = new ChatMessageLegacy(component, type);
/*     */       } 
/* 263 */       wrapperPlayServerChatMessage = new WrapperPlayServerChatMessage((ChatMessage)chatMessageLegacy);
/*     */     } 
/* 265 */     PacketEvents.getAPI().getProtocolManager().sendPacket(this.channel, (PacketWrapper)wrapperPlayServerChatMessage);
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendTitle(String legacyTitle, String legacySubtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
/* 270 */     LegacyComponentSerializer serializer = getSerializers().legacy();
/* 271 */     TextComponent textComponent1 = serializer.deserialize(legacyTitle);
/* 272 */     TextComponent textComponent2 = serializer.deserialize(legacySubtitle);
/* 273 */     sendTitle((Component)textComponent1, (Component)textComponent2, fadeInTicks, stayTicks, fadeOutTicks);
/*     */   }
/*     */   public void sendTitle(Component title, Component subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
/*     */     WrapperPlayServerTitle wrapperPlayServerTitle1, wrapperPlayServerTitle2, wrapperPlayServerTitle3;
/* 277 */     boolean modern = getPacketVersion().isNewerThanOrEquals(ClientVersion.V_1_17);
/*     */     
/* 279 */     PacketWrapper<?> setTitle = null;
/* 280 */     PacketWrapper<?> setSubtitle = null;
/* 281 */     if (modern) {
/* 282 */       WrapperPlayServerSetTitleTimes wrapperPlayServerSetTitleTimes = new WrapperPlayServerSetTitleTimes(fadeInTicks, stayTicks, fadeOutTicks);
/* 283 */       if (title != null) {
/* 284 */         WrapperPlayServerSetTitleText wrapperPlayServerSetTitleText = new WrapperPlayServerSetTitleText(title);
/*     */       }
/* 286 */       if (subtitle != null) {
/* 287 */         WrapperPlayServerSetTitleSubtitle wrapperPlayServerSetTitleSubtitle = new WrapperPlayServerSetTitleSubtitle(subtitle);
/*     */       }
/*     */     } else {
/* 290 */       wrapperPlayServerTitle1 = new WrapperPlayServerTitle(WrapperPlayServerTitle.TitleAction.SET_TIMES_AND_DISPLAY, (Component)null, null, null, fadeInTicks, stayTicks, fadeOutTicks);
/*     */ 
/*     */       
/* 293 */       if (title != null) {
/* 294 */         wrapperPlayServerTitle2 = new WrapperPlayServerTitle(WrapperPlayServerTitle.TitleAction.SET_TITLE, title, null, null, 0, 0, 0);
/*     */       }
/*     */ 
/*     */       
/* 298 */       if (subtitle != null) {
/* 299 */         wrapperPlayServerTitle3 = new WrapperPlayServerTitle(WrapperPlayServerTitle.TitleAction.SET_SUBTITLE, null, subtitle, null, 0, 0, 0);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 304 */     sendPacket((PacketWrapper<?>)wrapperPlayServerTitle1);
/* 305 */     if (wrapperPlayServerTitle2 != null) {
/* 306 */       sendPacket((PacketWrapper<?>)wrapperPlayServerTitle2);
/*     */     }
/* 308 */     if (wrapperPlayServerTitle3 != null) {
/* 309 */       sendPacket((PacketWrapper<?>)wrapperPlayServerTitle3);
/*     */     }
/*     */   }
/*     */   
/*     */   public ClientVersion getPacketVersion() {
/* 314 */     PacketEventsAPI<?> api = PacketEvents.getAPI();
/* 315 */     if (api.getInjector().isProxy()) {
/* 316 */       return getClientVersion();
/*     */     }
/* 318 */     return api.getServerManager().getVersion().toClientVersion();
/*     */   }
/*     */   
/*     */   public AdventureSerializer getSerializers() {
/* 322 */     return AdventureSerializer.serializer(getPacketVersion());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMinWorldHeight() {
/* 330 */     return getMinWorldHeight(null);
/*     */   }
/*     */   
/*     */   public int getMinWorldHeight(@Nullable ClientVersion version) {
/* 334 */     if (version == null)
/*     */     {
/* 336 */       version = PacketEvents.getAPI().getInjector().isProxy() ? getClientVersion() : PacketEvents.getAPI().getServerManager().getVersion().toClientVersion();
/*     */     }
/* 338 */     return this.dimensionType.getMinY(version);
/*     */   }
/*     */   
/*     */   public int getTotalWorldHeight() {
/* 342 */     return getTotalWorldHeight(null);
/*     */   }
/*     */   
/*     */   public int getTotalWorldHeight(@Nullable ClientVersion version) {
/* 346 */     if (version == null)
/*     */     {
/* 348 */       version = PacketEvents.getAPI().getInjector().isProxy() ? getClientVersion() : PacketEvents.getAPI().getServerManager().getVersion().toClientVersion();
/*     */     }
/* 350 */     return this.dimensionType.getHeight(version);
/*     */   }
/*     */   
/*     */   public DimensionType getDimensionType() {
/* 354 */     return this.dimensionType;
/*     */   }
/*     */   
/*     */   @Internal
/*     */   public void setDimensionType(DimensionType dimensionType) {
/* 359 */     this.dimensionType = dimensionType;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setMinWorldHeight(int minWorldHeight) {
/* 366 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void setTotalWorldHeight(int totalWorldHeight) {
/* 371 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void switchDimensionType(ServerVersion version, Dimension dimension) {
/* 376 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void setDefaultWorldHeights(ServerVersion version, Dimension dimension) {
/* 381 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void setDefaultWorldHeights(boolean extended) {
/* 386 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void setWorldNBT(NBTList<NBTCompound> worldNBT) {
/* 391 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public Dimension getDimension() {
/* 396 */     return Dimension.fromDimensionType(this.dimensionType, this, null);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void setDimension(Dimension dimension) {
/* 401 */     this.dimensionType = dimension.asDimensionType(this, null);
/*     */   }
/*     */   @Deprecated
/*     */   @Nullable
/*     */   public NBTCompound getWorldNBT(String worldName) {
/* 406 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   @Deprecated
/*     */   @Nullable
/*     */   public NBTCompound getWorldNBT(int worldId) {
/* 411 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   @Deprecated
/*     */   @Nullable
/*     */   public NBTCompound getWorldNBT(Dimension dimension) {
/* 416 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   @Deprecated
/*     */   @Nullable
/*     */   public String getWorldName(int worldId) {
/* 421 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public String getWorldName(Dimension dimension) {
/* 426 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\player\User.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */