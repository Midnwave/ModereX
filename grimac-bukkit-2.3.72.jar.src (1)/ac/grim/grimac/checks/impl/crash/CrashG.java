/*    */ package ac.grim.grimac.checks.impl.crash;
/*    */ 
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.BlockPlaceCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUseItem;
/*    */ import ac.grim.grimac.utils.anticheat.update.BlockBreak;
/*    */ import ac.grim.grimac.utils.anticheat.update.BlockPlace;
/*    */ 
/*    */ @CheckData(name = "CrashG", description = "Sent negative sequence id")
/*    */ public class CrashG
/*    */   extends BlockPlaceCheck {
/*    */   public CrashG(GrimPlayer player) {
/* 19 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 24 */     if (event.getPacketType() == PacketType.Play.Client.USE_ITEM && isSupportedVersion()) {
/* 25 */       WrapperPlayClientUseItem use = new WrapperPlayClientUseItem(event);
/* 26 */       if (use.getSequence() < 0) {
/* 27 */         flagAndAlert();
/* 28 */         event.setCancelled(true);
/* 29 */         this.player.onPacketCancel();
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void onBlockBreak(BlockBreak blockBreak) {
/* 36 */     if (blockBreak.sequence < 0 && isSupportedVersion()) {
/* 37 */       flagAndAlert();
/* 38 */       blockBreak.cancel();
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void onBlockPlace(BlockPlace place) {
/* 44 */     if (place.sequence < 0 && isSupportedVersion()) {
/* 45 */       flagAndAlert();
/* 46 */       place.resync();
/*    */     } 
/*    */   }
/*    */   
/*    */   private boolean isSupportedVersion() {
/* 51 */     return (this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_19) && PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_19));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\crash\CrashG.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */