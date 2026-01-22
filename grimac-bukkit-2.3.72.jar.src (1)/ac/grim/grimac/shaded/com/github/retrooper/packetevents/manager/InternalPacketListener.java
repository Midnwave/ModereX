/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerAbstract;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerPriority;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.ConnectionState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.UserProfile;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.LogManager;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.SynchronizedRegistriesHandler;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.configuration.server.WrapperConfigServerRegistryData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.handshaking.client.WrapperHandshakingClientHandshake;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.login.server.WrapperLoginServerLoginSuccess;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerJoinGame;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerRespawn;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
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
/*     */ @Internal
/*     */ public class InternalPacketListener
/*     */   extends PacketListenerAbstract
/*     */ {
/*     */   public InternalPacketListener() {
/*  46 */     this(PacketListenerPriority.LOWEST);
/*     */   }
/*     */   
/*     */   public InternalPacketListener(PacketListenerPriority priority) {
/*  50 */     super(priority);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onPacketSend(PacketSendEvent event) {
/*  55 */     User user = event.getUser();
/*  56 */     if (event.getPacketType() == PacketType.Login.Server.LOGIN_SUCCESS) {
/*  57 */       Object channel = event.getChannel();
/*     */       
/*  59 */       WrapperLoginServerLoginSuccess loginSuccess = new WrapperLoginServerLoginSuccess(event);
/*  60 */       UserProfile profile = loginSuccess.getUserProfile();
/*     */ 
/*     */       
/*  63 */       user.getProfile().setUUID(profile.getUUID());
/*  64 */       user.getProfile().setName(profile.getName());
/*     */       
/*  66 */       user.getProfile().setTextureProperties(profile.getTextureProperties());
/*     */ 
/*     */       
/*  69 */       synchronized (channel) {
/*  70 */         PacketEvents.getAPI().getProtocolManager().setChannel(profile.getUUID(), channel);
/*     */       } 
/*     */       
/*  73 */       if (PacketEvents.getAPI().getLogManager().isDebug()) {
/*  74 */         PacketEvents.getAPI().getLogManager().debug("Mapped player UUID with their channel " + profile.getUUID() + " " + channel);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*  79 */       boolean proxy = PacketEvents.getAPI().getInjector().isProxy();
/*  80 */       if (proxy ? event.getUser().getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_20_2) : event
/*  81 */         .getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
/*  82 */         user.setEncoderState(ConnectionState.CONFIGURATION);
/*     */       } else {
/*  84 */         user.setConnectionState(ConnectionState.PLAY);
/*     */       
/*     */       }
/*     */     
/*     */     }
/*  89 */     else if (event.getPacketType() == PacketType.Configuration.Server.REGISTRY_DATA) {
/*  90 */       WrapperConfigServerRegistryData packet = new WrapperConfigServerRegistryData(event);
/*     */       
/*  92 */       if (packet.getElements() != null) {
/*  93 */         SynchronizedRegistriesHandler.handleRegistry(user, (PacketWrapper)packet, packet
/*  94 */             .getRegistryKey(), packet.getElements());
/*     */       }
/*  96 */       if (packet.getRegistryData() != null) {
/*  97 */         SynchronizedRegistriesHandler.handleLegacyRegistries(user, (PacketWrapper)packet, packet.getRegistryData());
/*     */       
/*     */       }
/*     */     
/*     */     }
/* 102 */     else if (event.getPacketType() == PacketType.Play.Server.JOIN_GAME) {
/* 103 */       WrapperPlayServerJoinGame joinGame = new WrapperPlayServerJoinGame(event);
/* 104 */       user.setEntityId(joinGame.getEntityId());
/*     */       
/* 106 */       if (joinGame.getDimensionCodec() != null) {
/* 107 */         SynchronizedRegistriesHandler.handleLegacyRegistries(user, (PacketWrapper)joinGame, joinGame
/* 108 */             .getDimensionCodec());
/*     */       }
/*     */       
/* 111 */       user.setDimensionType(joinGame.getDimensionType());
/*     */ 
/*     */     
/*     */     }
/* 115 */     else if (event.getPacketType() == PacketType.Play.Server.RESPAWN) {
/* 116 */       WrapperPlayServerRespawn packet = new WrapperPlayServerRespawn(event);
/* 117 */       user.setDimensionType(packet.getDimensionType());
/* 118 */     } else if (event.getPacketType() == PacketType.Play.Server.CONFIGURATION_START) {
/* 119 */       user.setEncoderState(ConnectionState.CONFIGURATION);
/* 120 */     } else if (event.getPacketType() == PacketType.Configuration.Server.CONFIGURATION_END) {
/* 121 */       user.setEncoderState(ConnectionState.PLAY);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onPacketReceive(PacketReceiveEvent event) {
/* 127 */     User user = event.getUser();
/* 128 */     if (event.getPacketType() == PacketType.Handshaking.Client.HANDSHAKE) {
/* 129 */       WrapperHandshakingClientHandshake packet = new WrapperHandshakingClientHandshake(event);
/* 130 */       ClientVersion clientVersion = packet.getClientVersion();
/* 131 */       ConnectionState state = packet.getNextConnectionState();
/*     */       
/* 133 */       LogManager logger = PacketEvents.getAPI().getLogManager();
/* 134 */       if (logger.isDebug()) {
/* 135 */         logger.debug("Processed handshake for " + event.getAddress() + ": " + state
/* 136 */             .name() + " / " + packet.getClientVersion().getReleaseName());
/*     */       }
/*     */       
/* 139 */       user.setClientVersion(clientVersion);
/* 140 */       user.setConnectionState(state);
/* 141 */     } else if (event.getPacketType() == PacketType.Login.Client.LOGIN_SUCCESS_ACK) {
/* 142 */       user.setDecoderState(ConnectionState.CONFIGURATION);
/* 143 */     } else if (event.getPacketType() == PacketType.Play.Client.CONFIGURATION_ACK) {
/* 144 */       user.setDecoderState(ConnectionState.CONFIGURATION);
/* 145 */     } else if (event.getPacketType() == PacketType.Configuration.Client.CONFIGURATION_END_ACK) {
/* 146 */       user.setDecoderState(ConnectionState.PLAY);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\manager\InternalPacketListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */