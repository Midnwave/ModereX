/*    */ package ac.grim.grimac.checks.impl.badpackets;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
/*    */ 
/*    */ @CheckData(name = "BadPacketsD", description = "Impossible pitch")
/*    */ public class BadPacketsD extends Check implements PacketCheck {
/*    */   public BadPacketsD(GrimPlayer player) {
/* 14 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 19 */     if (this.player.packetStateData.lastPacketWasTeleport)
/*    */       return; 
/* 21 */     if (event.getPacketType() == PacketType.Play.Client.PLAYER_ROTATION || event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION) {
/* 22 */       float pitch = (new WrapperPlayClientPlayerFlying(event)).getLocation().getPitch();
/* 23 */       if (pitch > 90.0F || pitch < -90.0F)
/*    */       {
/* 25 */         if (flagAndAlert("pitch=" + pitch) && 
/* 26 */           shouldModifyPackets()) {
/*    */           
/* 28 */           if (this.player.yRot > 90.0F) this.player.yRot = 90.0F; 
/* 29 */           if (this.player.yRot < -90.0F) this.player.yRot = -90.0F;
/*    */           
/* 31 */           event.setCancelled(true);
/* 32 */           this.player.onPacketCancel();
/*    */         } 
/*    */       }
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\badpackets\BadPacketsD.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */