/*    */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.manager;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEventsAPI;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.InternalPacketListener;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.ConnectionState;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.LogManager;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.handshaking.client.WrapperHandshakingClientHandshake;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.login.server.WrapperLoginServerLoginSuccess;
/*    */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.injector.SpigotChannelInjector;
/*    */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.manager.player.PlayerManagerImpl;
/*    */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.protocolsupport.ProtocolSupportUtil;
/*    */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.viaversion.ViaVersionUtil;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import java.lang.ref.WeakReference;
/*    */ import java.util.Map;
/*    */ import java.util.UUID;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ @Internal
/*    */ public class InternalBukkitPacketListener
/*    */   extends InternalPacketListener
/*    */ {
/*    */   public void onPacketSend(PacketSendEvent event) {
/* 30 */     super.onPacketSend(event);
/*    */ 
/*    */     
/* 33 */     if (event.getPacketType() == PacketType.Login.Server.LOGIN_SUCCESS) {
/* 34 */       WrapperLoginServerLoginSuccess packet = new WrapperLoginServerLoginSuccess(event);
/* 35 */       tryUpdatePlayerReference(event, event.getUser(), packet.getUserProfile().getUUID());
/* 36 */     } else if (event.getPacketType() == PacketType.Play.Server.JOIN_GAME) {
/*    */       
/* 38 */       tryUpdatePlayerReference(event, event.getUser(), event.getUser().getUUID());
/*    */     } 
/*    */   }
/*    */   
/*    */   private void tryUpdatePlayerReference(PacketSendEvent event, User user, UUID playerId) {
/* 43 */     PacketEventsAPI<?> api = PacketEvents.getAPI();
/* 44 */     Map<UUID, WeakReference<Player>> map = ((PlayerManagerImpl)api.getPlayerManager()).joiningPlayers;
/* 45 */     WeakReference<Player> playerRef = map.remove(playerId);
/* 46 */     Player player = (playerRef != null) ? playerRef.get() : null;
/*    */ 
/*    */ 
/*    */     
/* 50 */     if (player != null) {
/* 51 */       ((SpigotChannelInjector)api.getInjector()).updatePlayer(user, player);
/* 52 */       if (api.getLogManager().isDebug()) {
/* 53 */         api.getLogManager().debug("Updated player reference on packet handling for " + player.getUniqueId());
/*    */       }
/*    */       
/* 56 */       event.setPlayer(player);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 62 */     if (event.getPacketType() == PacketType.Handshaking.Client.HANDSHAKE) {
/* 63 */       String feature; User user = event.getUser();
/* 64 */       WrapperHandshakingClientHandshake packet = new WrapperHandshakingClientHandshake(event);
/* 65 */       ClientVersion clientVersion = packet.getClientVersion();
/* 66 */       ConnectionState state = packet.getNextConnectionState();
/*    */ 
/*    */       
/* 69 */       if (!isPreVia()) {
/* 70 */         if (ViaVersionUtil.isAvailable()) {
/* 71 */           clientVersion = ClientVersion.getById(ViaVersionUtil.getProtocolVersion(user));
/* 72 */           feature = "ViaVersion";
/* 73 */         } else if (ProtocolSupportUtil.isAvailable()) {
/* 74 */           clientVersion = ClientVersion.getById(ProtocolSupportUtil.getProtocolVersion(user.getAddress()));
/* 75 */           feature = "ProtocolSupport";
/*    */         } else {
/* 77 */           feature = null;
/*    */         } 
/*    */       } else {
/* 80 */         feature = "Client Version Handshake";
/*    */       } 
/*    */       
/* 83 */       LogManager logger = PacketEvents.getAPI().getLogManager();
/* 84 */       if (logger.isDebug()) {
/* 85 */         logger.debug("Processed handshake for " + event.getAddress() + ": " + state
/* 86 */             .name() + " / " + packet.getClientVersion().getReleaseName() + (
/* 87 */             (feature != null) ? (" (using " + feature + ")") : ""));
/*    */       }
/*    */       
/* 90 */       user.setClientVersion(clientVersion);
/* 91 */       user.setConnectionState(state);
/*    */     } else {
/* 93 */       super.onPacketReceive(event);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\manager\InternalBukkitPacketListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */