/*    */ package ac.grim.grimac.checks.impl.badpackets;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*    */ 
/*    */ @CheckData(name = "BadPacketsK", description = "Sent spectate packets while not in spectator mode")
/*    */ public class BadPacketsK extends Check implements PacketCheck {
/*    */   public BadPacketsK(GrimPlayer player) {
/* 14 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 19 */     if (event.getPacketType() == PacketType.Play.Client.SPECTATE && 
/* 20 */       this.player.gamemode != GameMode.SPECTATOR && 
/* 21 */       flagAndAlert() && shouldModifyPackets()) {
/* 22 */       event.setCancelled(true);
/* 23 */       this.player.onPacketCancel();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\badpackets\BadPacketsK.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */