/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.event;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.exception.InvalidDisconnectPacketSend;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.exception.PacketProcessException;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.UnpooledByteBufAllocationHelper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.channel.ChannelHelper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.ConnectionState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.PacketSide;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public abstract class ProtocolPacketEvent
/*     */   extends PacketEvent
/*     */   implements PlayerEvent, CancellableEvent, UserEvent
/*     */ {
/*     */   private final Object channel;
/*     */   private final ConnectionState connectionState;
/*     */   private final User user;
/*     */   private Object player;
/*     */   private Object byteBuf;
/*     */   private final int packetID;
/*     */   private final PacketTypeCommon packetType;
/*     */   private ServerVersion serverVersion;
/*     */   private boolean cancel;
/*     */   private PacketWrapper<?> lastUsedWrapper;
/*  55 */   private List<Runnable> postTasks = null;
/*     */   private boolean cloned;
/*  57 */   private boolean needsReEncode = PacketEvents.getAPI().getSettings().reEncodeByDefault();
/*     */ 
/*     */ 
/*     */   
/*     */   public ProtocolPacketEvent(PacketSide packetSide, Object channel, User user, Object player, Object byteBuf, boolean autoProtocolTranslation) throws PacketProcessException {
/*  62 */     this.channel = channel;
/*  63 */     this.user = user;
/*  64 */     this.player = player;
/*     */     
/*  66 */     if (autoProtocolTranslation || user.getClientVersion() == null) {
/*  67 */       this.serverVersion = PacketEvents.getAPI().getServerManager().getVersion();
/*     */     } else {
/*     */       
/*  70 */       this.serverVersion = user.getClientVersion().toServerVersion();
/*     */     } 
/*     */     
/*  73 */     this.byteBuf = byteBuf;
/*  74 */     int size = ByteBufHelper.readableBytes(byteBuf);
/*  75 */     if (size == 0) {
/*  76 */       throw new PacketProcessException("Trying to process a packet, but it has no content. (Size=0)");
/*     */     }
/*     */     try {
/*  79 */       this.packetID = ByteBufHelper.readVarInt(byteBuf);
/*  80 */     } catch (Exception e) {
/*  81 */       throw new PacketProcessException("Failed to read the Packet ID of a packet. (Size: " + size + ")");
/*     */     } 
/*  83 */     ClientVersion version = this.serverVersion.toClientVersion();
/*  84 */     ConnectionState state = (packetSide == PacketSide.CLIENT) ? user.getDecoderState() : user.getEncoderState();
/*  85 */     this.packetType = PacketType.getById(packetSide, state, version, this.packetID);
/*     */     
/*  87 */     if (this.packetType == null) {
/*     */       
/*  89 */       if (PacketType.getById(packetSide, ConnectionState.PLAY, version, this.packetID) == PacketType.Play.Server.DISCONNECT) {
/*  90 */         throw new InvalidDisconnectPacketSend();
/*     */       }
/*  92 */       throw new PacketProcessException("Failed to map the Packet ID " + this.packetID + " to a PacketType constant. Bound: " + packetSide.getOpposite() + ", Connection state: " + user.getDecoderState() + ", Server version: " + this.serverVersion.getReleaseName());
/*     */     } 
/*  94 */     this.connectionState = state;
/*     */   }
/*     */ 
/*     */   
/*     */   public ProtocolPacketEvent(int packetID, PacketTypeCommon packetType, ServerVersion serverVersion, Object channel, User user, Object player, Object byteBuf) {
/*  99 */     this.channel = channel;
/* 100 */     this.user = user;
/* 101 */     this.player = player;
/* 102 */     this.serverVersion = serverVersion;
/* 103 */     this.byteBuf = byteBuf;
/* 104 */     this.packetID = packetID;
/* 105 */     this.packetType = packetType;
/*     */     
/* 107 */     this
/* 108 */       .connectionState = (packetType != null && packetType.getSide() == PacketSide.SERVER) ? user.getEncoderState() : user.getDecoderState();
/* 109 */     this.cloned = true;
/*     */   }
/*     */   
/*     */   public void markForReEncode(boolean needsReEncode) {
/* 113 */     this.needsReEncode = needsReEncode;
/*     */   }
/*     */   
/*     */   public boolean needsReEncode() {
/* 117 */     return this.needsReEncode;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isClone() {
/* 122 */     return this.cloned;
/*     */   }
/*     */   
/*     */   public Object getChannel() {
/* 126 */     return this.channel;
/*     */   }
/*     */   
/*     */   public SocketAddress getAddress() {
/* 130 */     return ChannelHelper.remoteAddress(this.channel);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InetSocketAddress getSocketAddress() {
/* 137 */     return (InetSocketAddress)getAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public User getUser() {
/* 142 */     return this.user;
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T getPlayer() {
/* 147 */     return (T)this.player;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   @Internal
/*     */   public void setPlayer(Object player) {
/* 153 */     this.player = player;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConnectionState getConnectionState() {
/* 160 */     return this.connectionState;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ClientVersion getClientVersion() {
/* 166 */     return this.user.getClientVersion();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void setClientVersion(@NotNull ClientVersion clientVersion) {
/* 171 */     PacketEvents.getAPI().getLogManager().debug("Setting client version with deprecated method " + clientVersion.getReleaseName());
/* 172 */     this.user.setClientVersion(clientVersion);
/*     */   }
/*     */   
/*     */   public ServerVersion getServerVersion() {
/* 176 */     return this.serverVersion;
/*     */   }
/*     */   
/*     */   public void setServerVersion(@NotNull ServerVersion serverVersion) {
/* 180 */     this.serverVersion = serverVersion;
/*     */   }
/*     */   
/*     */   public Object getByteBuf() {
/* 184 */     return this.byteBuf;
/*     */   }
/*     */   
/*     */   public void setByteBuf(Object byteBuf) {
/* 188 */     this.byteBuf = byteBuf;
/*     */   }
/*     */   
/*     */   public int getPacketId() {
/* 192 */     return this.packetID;
/*     */   }
/*     */   
/*     */   public PacketTypeCommon getPacketType() {
/* 196 */     return this.packetType;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public String getPacketName() {
/* 201 */     return ((Enum)this.packetType).name();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCancelled() {
/* 206 */     return this.cancel;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCancelled(boolean val) {
/* 211 */     this.cancel = val;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public PacketWrapper<?> getLastUsedWrapper() {
/* 216 */     return this.lastUsedWrapper;
/*     */   }
/*     */   
/*     */   public void setLastUsedWrapper(@Nullable PacketWrapper<?> lastUsedWrapper) {
/* 220 */     this.lastUsedWrapper = lastUsedWrapper;
/*     */   }
/*     */   
/*     */   public List<Runnable> getPostTasks() {
/* 224 */     if (this.postTasks == null) {
/* 225 */       this.postTasks = new ArrayList<>();
/*     */     }
/* 227 */     return this.postTasks;
/*     */   }
/*     */   
/*     */   public boolean hasPostTasks() {
/* 231 */     return (this.postTasks != null && !this.postTasks.isEmpty());
/*     */   }
/*     */ 
/*     */   
/*     */   public ProtocolPacketEvent clone() {
/* 236 */     return (this instanceof PacketReceiveEvent) ? ((PacketReceiveEvent)this).clone() : (
/* 237 */       (PacketSendEvent)this).clone();
/*     */   }
/*     */   
/*     */   public void cleanUp() {
/* 241 */     if (isClone()) {
/* 242 */       ByteBufHelper.release(this.byteBuf);
/*     */     }
/*     */   }
/*     */   
/*     */   public Object getFullBufferClone() {
/* 247 */     byte[] data = ByteBufHelper.copyBytes(getByteBuf());
/* 248 */     Object buffer = UnpooledByteBufAllocationHelper.buffer();
/* 249 */     ByteBufHelper.writeVarInt(buffer, getPacketId());
/* 250 */     ByteBufHelper.writeBytes(buffer, data);
/* 251 */     return buffer;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\event\ProtocolPacketEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */