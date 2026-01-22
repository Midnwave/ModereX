/*    */ package ac.grim.grimac.manager.init.start;
/*    */ import ac.grim.grimac.events.packets.PacketBlockAction;
/*    */ import ac.grim.grimac.events.packets.PacketEntityAction;
/*    */ import ac.grim.grimac.events.packets.PacketPlayerDigging;
/*    */ import ac.grim.grimac.events.packets.PacketPlayerJoinQuit;
/*    */ import ac.grim.grimac.events.packets.PacketPlayerSteer;
/*    */ import ac.grim.grimac.events.packets.PacketSelfMetadataListener;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerCommon;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ 
/*    */ public class PacketManager implements StartableInitable {
/*    */   public void start() {
/* 14 */     LogUtil.info("Registering packets...");
/*    */     
/* 16 */     PacketEvents.getAPI().getEventManager().registerListener((PacketListenerCommon)new PacketPlayerJoinQuit());
/* 17 */     PacketEvents.getAPI().getEventManager().registerListener((PacketListenerCommon)new PacketPingListener());
/* 18 */     PacketEvents.getAPI().getEventManager().registerListener((PacketListenerCommon)new PacketPlayerDigging());
/* 19 */     PacketEvents.getAPI().getEventManager().registerListener((PacketListenerCommon)new PacketPlayerAttack());
/* 20 */     PacketEvents.getAPI().getEventManager().registerListener((PacketListenerCommon)new PacketEntityAction());
/* 21 */     PacketEvents.getAPI().getEventManager().registerListener((PacketListenerCommon)new PacketBlockAction());
/* 22 */     PacketEvents.getAPI().getEventManager().registerListener((PacketListenerCommon)new PacketSelfMetadataListener());
/* 23 */     PacketEvents.getAPI().getEventManager().registerListener((PacketListenerCommon)new PacketServerTeleport());
/* 24 */     PacketEvents.getAPI().getEventManager().registerListener((PacketListenerCommon)new PacketPlayerCooldown());
/* 25 */     PacketEvents.getAPI().getEventManager().registerListener((PacketListenerCommon)new PacketPlayerRespawn());
/* 26 */     PacketEvents.getAPI().getEventManager().registerListener((PacketListenerCommon)new CheckManagerListener());
/* 27 */     PacketEvents.getAPI().getEventManager().registerListener((PacketListenerCommon)new PacketPlayerSteer());
/*    */     
/* 29 */     if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13)) {
/* 30 */       PacketEvents.getAPI().getEventManager().registerListener((PacketListenerCommon)new PacketServerTags());
/*    */     }
/*    */     
/* 33 */     if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_18)) {
/* 34 */       PacketEvents.getAPI().getEventManager().registerListener((PacketListenerCommon)new PacketWorldReaderEighteen());
/* 35 */     } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_8_8)) {
/* 36 */       PacketEvents.getAPI().getEventManager().registerListener((PacketListenerCommon)new PacketWorldReaderEight());
/*    */     } else {
/* 38 */       PacketEvents.getAPI().getEventManager().registerListener((PacketListenerCommon)new BasePacketWorldReader());
/*    */     } 
/*    */     
/* 41 */     PacketEvents.getAPI().getEventManager().registerListener((PacketListenerCommon)new ProxyAlertMessenger());
/* 42 */     PacketEvents.getAPI().getEventManager().registerListener((PacketListenerCommon)new PacketHidePlayerInfo());
/*    */     
/* 44 */     PacketEvents.getAPI().init();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\init\start\PacketManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */