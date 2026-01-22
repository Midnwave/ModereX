/*    */ package ac.grim.grimac.checks.impl.badpackets;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerAbilities;
/*    */ 
/*    */ @CheckData(name = "BadPacketsI", description = "Claimed to be flying while unable to fly")
/*    */ public class BadPacketsI extends Check implements PacketCheck {
/*    */   public BadPacketsI(GrimPlayer player) {
/* 14 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 19 */     if (event.getPacketType() == PacketType.Play.Client.PLAYER_ABILITIES && (
/* 20 */       new WrapperPlayClientPlayerAbilities(event)).isFlying() && !this.player.canFly && 
/* 21 */       flagAndAlert() && shouldModifyPackets()) {
/* 22 */       event.setCancelled(true);
/* 23 */       this.player.onPacketCancel();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\badpackets\BadPacketsI.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */