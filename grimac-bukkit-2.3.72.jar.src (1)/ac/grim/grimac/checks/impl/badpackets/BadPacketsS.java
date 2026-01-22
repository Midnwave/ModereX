/*    */ package ac.grim.grimac.checks.impl.badpackets;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientWindowConfirmation;
/*    */ 
/*    */ @CheckData(name = "BadPacketsS")
/*    */ public class BadPacketsS extends Check implements PacketCheck {
/*    */   public BadPacketsS(GrimPlayer player) {
/* 14 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 19 */     if (event.getPacketType() == PacketType.Play.Client.WINDOW_CONFIRMATION && 
/* 20 */       !(new WrapperPlayClientWindowConfirmation(event)).isAccepted() && flagAndAlert() && shouldModifyPackets()) {
/* 21 */       event.setCancelled(true);
/* 22 */       this.player.onPacketCancel();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\badpackets\BadPacketsS.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */