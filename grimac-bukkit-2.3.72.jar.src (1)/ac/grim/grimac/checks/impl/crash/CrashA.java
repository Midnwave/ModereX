/*    */ package ac.grim.grimac.checks.impl.crash;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
/*    */ 
/*    */ @CheckData(name = "CrashA")
/*    */ public class CrashA extends Check implements PacketCheck {
/*    */   private static final double HARD_CODED_BORDER = 2.9999999E7D;
/*    */   
/*    */   public CrashA(GrimPlayer player) {
/* 15 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 20 */     if (this.player.packetStateData.lastPacketWasTeleport)
/* 21 */       return;  if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
/* 22 */       WrapperPlayClientPlayerFlying packet = new WrapperPlayClientPlayerFlying(event);
/*    */       
/* 24 */       if (!packet.hasPositionChanged())
/*    */         return; 
/* 26 */       if (Math.abs(packet.getLocation().getX()) > 2.9999999E7D || Math.abs(packet.getLocation().getZ()) > 2.9999999E7D || Math.abs(packet.getLocation().getY()) > 2.147483647E9D) {
/* 27 */         flagAndAlert();
/* 28 */         this.player.getSetbackTeleportUtil().executeViolationSetback();
/* 29 */         event.setCancelled(true);
/* 30 */         this.player.onPacketCancel();
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\crash\CrashA.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */