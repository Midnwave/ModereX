/*    */ package ac.grim.grimac.checks.impl.badpackets;
/*    */ 
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.BlockPlaceCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.DiggingAction;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUseItem;
/*    */ import ac.grim.grimac.utils.anticheat.update.BlockBreak;
/*    */ import ac.grim.grimac.utils.anticheat.update.BlockPlace;
/*    */ 
/*    */ @CheckData(name = "BadPacketsH", description = "Sent unexpected sequence id", experimental = true)
/*    */ public class BadPacketsH extends BlockPlaceCheck {
/* 18 */   private final boolean isSupportedVersion = (this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_19) && PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_19)); private int lastSequence;
/*    */   
/*    */   public BadPacketsH(GrimPlayer player) {
/* 21 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 26 */     if (event.getPacketType() == PacketType.Play.Client.USE_ITEM && 
/* 27 */       shouldCancel((new WrapperPlayClientUseItem(event)).getSequence())) {
/* 28 */       event.setCancelled(true);
/* 29 */       this.player.onPacketCancel();
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void onBlockPlace(BlockPlace place) {
/* 35 */     if (shouldCancel(place.sequence) && shouldCancel()) {
/* 36 */       place.resync();
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void onBlockBreak(BlockBreak blockBreak) {
/* 42 */     switch (blockBreak.action) { case START_DIGGING:
/*    */       case FINISHED_DIGGING:
/* 44 */         if (shouldCancel(blockBreak.sequence)) {
/* 45 */           blockBreak.cancel();
/*    */         }
/*    */         break;
/*    */       case CANCELLED_DIGGING:
/* 49 */         if (blockBreak.sequence != 0 && flagAndAlert("expected=0, id=" + blockBreak.sequence) && shouldModifyPackets()) {
/* 50 */           blockBreak.cancel();
/*    */         }
/*    */         break; }
/*    */   
/*    */   }
/*    */   
/*    */   public boolean shouldCancel(int sequence) {
/* 57 */     if (this.isSupportedVersion && sequence != this.lastSequence + 1 && 
/* 58 */       flagAndAlert("expected=" + this.lastSequence + 1 + ", id=" + sequence) && shouldModifyPackets()) {
/* 59 */       this.lastSequence = sequence;
/* 60 */       return true;
/*    */     } 
/*    */ 
/*    */     
/* 64 */     this.lastSequence = sequence;
/* 65 */     return false;
/*    */   }
/*    */   
/*    */   public void onWorldChange() {
/* 69 */     this.lastSequence = 0;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\badpackets\BadPacketsH.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */