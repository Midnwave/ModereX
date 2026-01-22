/*    */ package ac.grim.grimac.events.packets;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.platform.api.player.PlatformPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerAbstract;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.UserConnectEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.UserDisconnectEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.UserLoginEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.channel.ChannelHelper;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.ConnectionState;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.utils.anticheat.LogUtil;
/*    */ import java.util.Objects;
/*    */ 
/*    */ public class PacketPlayerJoinQuit
/*    */   extends PacketListenerAbstract {
/*    */   public void onPacketSend(PacketSendEvent event) {
/* 19 */     if (event.getPacketType() == PacketType.Login.Server.LOGIN_SUCCESS)
/*    */     {
/* 21 */       event.getTasksAfterSend().add(() -> GrimAPI.INSTANCE.getPlayerDataManager().addUser(event.getUser()));
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void onUserConnect(UserConnectEvent event) {
/* 29 */     if (event.getUser().getConnectionState() == ConnectionState.PLAY && !(GrimAPI.INSTANCE.getPlayerDataManager()).exemptUsers.contains(event.getUser())) {
/* 30 */       event.setCancelled(true);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void onUserLogin(UserLoginEvent event) {
/* 36 */     Object nativePlayerObject = Objects.requireNonNull(event.getPlayer());
/*    */ 
/*    */ 
/*    */     
/* 40 */     PlatformPlayer platformPlayer = GrimAPI.INSTANCE.getPlatformPlayerFactory().getFromNativePlayerType(nativePlayerObject);
/*    */     
/* 42 */     if (GrimAPI.INSTANCE.getConfigManager().getConfig().getBooleanElse("debug-pipeline-on-join", false)) {
/* 43 */       LogUtil.info("Pipeline: " + ChannelHelper.pipelineHandlerNamesAsString(event.getUser().getChannel()));
/*    */     }
/* 45 */     if (platformPlayer.hasPermission("grim.alerts.enable-on-join") && platformPlayer.hasPermission("grim.alerts")) {
/* 46 */       GrimAPI.INSTANCE.getAlertManager().toggleAlerts(platformPlayer, platformPlayer.hasPermission("grim.alerts.enable-on-join.silent"));
/*    */     }
/* 48 */     if (platformPlayer.hasPermission("grim.verbose.enable-on-join") && platformPlayer.hasPermission("grim.verbose")) {
/* 49 */       GrimAPI.INSTANCE.getAlertManager().toggleVerbose(platformPlayer, platformPlayer.hasPermission("grim.verbose.enable-on-join.silent"));
/*    */     }
/* 51 */     if (platformPlayer.hasPermission("grim.brand.enable-on-join") && platformPlayer.hasPermission("grim.brand")) {
/* 52 */       GrimAPI.INSTANCE.getAlertManager().toggleBrands(platformPlayer, platformPlayer.hasPermission("grim.brand.enable-on-join.silent"));
/*    */     }
/* 54 */     if (platformPlayer.hasPermission("grim.spectate") && GrimAPI.INSTANCE.getConfigManager().getConfig().getBooleanElse("spectators.hide-regardless", false)) {
/* 55 */       GrimAPI.INSTANCE.getSpectateManager().onLogin(platformPlayer.getUniqueId());
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void onUserDisconnect(UserDisconnectEvent event) {
/* 61 */     GrimAPI.INSTANCE.getPlayerDataManager().onDisconnect(event.getUser());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\events\packets\PacketPlayerJoinQuit.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */