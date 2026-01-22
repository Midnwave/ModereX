/*    */ package ac.grim.grimac.checks.impl.badpackets;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.DiggingAction;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
/*    */ import java.util.Locale;
/*    */ 
/*    */ @CheckData(name = "BadPacketsL", description = "Sent impossible dig packet")
/*    */ public class BadPacketsL
/*    */   extends Check
/*    */   implements PacketCheck {
/*    */   public BadPacketsL(GrimPlayer player) {
/* 19 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 24 */     if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
/* 25 */       WrapperPlayClientPlayerDigging packet = new WrapperPlayClientPlayerDigging(event);
/*    */       
/* 27 */       if (packet.getAction() == DiggingAction.START_DIGGING || packet.getAction() == DiggingAction.FINISHED_DIGGING || packet.getAction() == DiggingAction.CANCELLED_DIGGING) {
/*    */         return;
/*    */       }
/*    */ 
/*    */ 
/*    */       
/* 33 */       int expectedFace = (this.player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_7_10) && packet.getAction() == DiggingAction.RELEASE_USE_ITEM) ? 255 : 0;
/*    */       
/* 35 */       if (packet.getBlockFaceId() != expectedFace || packet
/* 36 */         .getBlockPosition().getX() != 0 || packet
/* 37 */         .getBlockPosition().getY() != 0 || packet
/* 38 */         .getBlockPosition().getZ() != 0 || packet
/* 39 */         .getSequence() != 0)
/*    */       {
/* 41 */         if (flagAndAlert("pos=" + packet
/* 42 */             .getBlockPosition().getX() + ", " + packet.getBlockPosition().getY() + ", " + packet.getBlockPosition().getZ() + ", face=" + 
/* 43 */             String.valueOf(packet.getBlockFace()) + ", sequence=" + packet
/* 44 */             .getSequence() + ", action=" + packet
/* 45 */             .getAction().toString().toLowerCase(Locale.ROOT)) && 
/* 46 */           shouldModifyPackets() && packet.getAction() != DiggingAction.RELEASE_USE_ITEM) {
/* 47 */           event.setCancelled(true);
/* 48 */           this.player.onPacketCancel();
/*    */         } 
/*    */       }
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\badpackets\BadPacketsL.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */