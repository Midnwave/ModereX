/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.protocol;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.channel.ChannelHelper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.ProtocolVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.PacketTransformationUtil;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ public interface ProtocolManager
/*     */ {
/*     */   @Internal
/*  37 */   public static final Map<UUID, Object> CHANNELS = new ConcurrentHashMap<>();
/*     */   
/*     */   @Internal
/*  40 */   public static final Map<Object, User> USERS = new ConcurrentHashMap<>();
/*     */   
/*     */   default Collection<User> getUsers() {
/*  43 */     return USERS.values();
/*     */   }
/*     */   
/*     */   default Collection<Object> getChannels() {
/*  47 */     return CHANNELS.values();
/*     */   }
/*     */ 
/*     */   
/*     */   ProtocolVersion getPlatformVersion();
/*     */ 
/*     */   
/*     */   void sendPacket(Object paramObject1, Object paramObject2);
/*     */ 
/*     */   
/*     */   void sendPacketSilently(Object paramObject1, Object paramObject2);
/*     */   
/*     */   void writePacket(Object paramObject1, Object paramObject2);
/*     */   
/*     */   void sendPackets(Object channel, Object... byteBuf) {
/*  62 */     for (Object buf : byteBuf)
/*  63 */       sendPacket(channel, buf); 
/*     */   } void writePacketSilently(Object paramObject1, Object paramObject2); void receivePacket(Object paramObject1, Object paramObject2);
/*     */   void receivePacketSilently(Object paramObject1, Object paramObject2);
/*     */   ClientVersion getClientVersion(Object paramObject);
/*     */   void sendPacketsSilently(Object channel, Object... byteBuf) {
/*  68 */     for (Object buf : byteBuf) {
/*  69 */       sendPacketSilently(channel, buf);
/*     */     }
/*     */   }
/*     */   
/*     */   void writePackets(Object channel, Object... byteBuf) {
/*  74 */     for (Object buf : byteBuf) {
/*  75 */       writePacket(channel, buf);
/*     */     }
/*     */   }
/*     */   
/*     */   void writePacketsSilently(Object channel, Object... byteBuf) {
/*  80 */     for (Object buf : byteBuf) {
/*  81 */       writePacketSilently(channel, buf);
/*     */     }
/*     */   }
/*     */   
/*     */   void receivePackets(Object channel, Object... byteBuf) {
/*  86 */     for (Object buf : byteBuf) {
/*  87 */       receivePacket(channel, buf);
/*     */     }
/*     */   }
/*     */   
/*     */   void receivePacketsSilently(Object channel, Object... byteBuf) {
/*  92 */     for (Object buf : byteBuf) {
/*  93 */       receivePacketSilently(channel, buf);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   default void setClientVersion(Object channel, ClientVersion version) {
/*  99 */     getUser(channel).setClientVersion(version);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   default Object[] transformWrappers(PacketWrapper<?> wrapper, Object channel, boolean outgoing) {
/* 106 */     PacketWrapper[] arrayOfPacketWrapper = PacketTransformationUtil.transform(wrapper);
/* 107 */     Object[] buffers = new Object[arrayOfPacketWrapper.length];
/* 108 */     for (int i = 0; i < arrayOfPacketWrapper.length; i++) {
/* 109 */       PacketWrapper<?> wrappper = arrayOfPacketWrapper[i];
/* 110 */       synchronized (wrappper.bufferLock) {
/* 111 */         wrappper.prepareForSend(channel, outgoing);
/* 112 */         buffers[i] = wrappper.buffer;
/*     */         
/* 114 */         wrappper.buffer = null;
/*     */       } 
/*     */     } 
/* 117 */     return buffers;
/*     */   }
/*     */   
/*     */   default void sendPacket(Object channel, PacketWrapper<?> wrapper) {
/* 121 */     Object[] transformed = transformWrappers(wrapper, channel, true);
/* 122 */     sendPackets(channel, transformed);
/*     */   }
/*     */   
/*     */   default void sendPacketSilently(Object channel, PacketWrapper<?> wrapper) {
/* 126 */     Object[] transformed = transformWrappers(wrapper, channel, true);
/* 127 */     sendPacketsSilently(channel, transformed);
/*     */   }
/*     */   
/*     */   default void writePacket(Object channel, PacketWrapper<?> wrapper) {
/* 131 */     Object[] transformed = transformWrappers(wrapper, channel, true);
/* 132 */     writePackets(channel, transformed);
/*     */   }
/*     */   
/*     */   default void writePacketSilently(Object channel, PacketWrapper<?> wrapper) {
/* 136 */     Object[] transformed = transformWrappers(wrapper, channel, true);
/* 137 */     writePacketsSilently(channel, transformed);
/*     */   }
/*     */   
/*     */   default void receivePacket(Object channel, PacketWrapper<?> wrapper) {
/* 141 */     Object[] transformed = transformWrappers(wrapper, channel, false);
/* 142 */     receivePackets(channel, transformed);
/*     */   }
/*     */   
/*     */   default void receivePacketSilently(Object channel, PacketWrapper<?> wrapper) {
/* 146 */     Object[] transformed = transformWrappers(wrapper, channel, false);
/* 147 */     receivePacketsSilently(channel, transformed);
/*     */   }
/*     */   
/*     */   default User getUser(Object channel) {
/* 151 */     Object pipeline = ChannelHelper.getPipeline(channel);
/* 152 */     return USERS.get(pipeline);
/*     */   }
/*     */   
/*     */   @Internal
/*     */   default User removeUser(Object channel) {
/* 157 */     Object pipeline = ChannelHelper.getPipeline(channel);
/* 158 */     return USERS.remove(pipeline);
/*     */   }
/*     */   
/*     */   @Internal
/*     */   default void setUser(Object channel, User user) {
/* 163 */     synchronized (channel) {
/* 164 */       Object pipeline = ChannelHelper.getPipeline(channel);
/* 165 */       USERS.put(pipeline, user);
/*     */     } 
/* 167 */     PacketEvents.getAPI().getInjector().updateUser(channel, user);
/*     */   }
/*     */   
/*     */   default Object getChannel(UUID uuid) {
/* 171 */     return CHANNELS.get(uuid);
/*     */   }
/*     */   
/*     */   @Internal
/*     */   default void setChannel(UUID uuid, Object channel) {
/* 176 */     CHANNELS.put(uuid, channel);
/*     */   }
/*     */   
/*     */   @Internal
/*     */   default void removeChannel(Object channel) {
/* 181 */     CHANNELS.values().remove(channel);
/*     */   }
/*     */   
/*     */   @Internal
/*     */   default void removeChannelById(UUID uuid) {
/* 186 */     CHANNELS.remove(uuid);
/*     */   }
/*     */   
/*     */   default boolean hasChannel(Object channel) {
/* 190 */     return CHANNELS.containsValue(channel);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\manager\protocol\ProtocolManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */