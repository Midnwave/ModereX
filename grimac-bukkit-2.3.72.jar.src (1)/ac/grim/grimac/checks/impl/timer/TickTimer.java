/*    */ package ac.grim.grimac.checks.impl.timer;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
/*    */ 
/*    */ @CheckData(name = "TickTimer", setback = 1.0D)
/*    */ public class TickTimer
/*    */   extends Check
/*    */   implements PacketCheck {
/*    */   private boolean receivedTickEnd = true;
/* 16 */   private int flyingPackets = 0;
/*    */   
/*    */   public TickTimer(GrimPlayer player) {
/* 19 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 24 */     if (!this.player.supportsEndTick())
/* 25 */       return;  if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType()) && !this.player.packetStateData.lastPacketWasTeleport) {
/* 26 */       if (!this.receivedTickEnd && flagAndAlertWithSetback("type=flying, packets=" + this.flyingPackets)) {
/* 27 */         handleViolation();
/*    */       }
/* 29 */       this.receivedTickEnd = false;
/* 30 */       this.flyingPackets++;
/* 31 */     } else if (event.getPacketType() == PacketType.Play.Client.CLIENT_TICK_END) {
/* 32 */       this.receivedTickEnd = true;
/* 33 */       if (this.flyingPackets > 1 && flagAndAlertWithSetback("type=end, packets=" + this.flyingPackets)) {
/* 34 */         handleViolation();
/*    */       }
/* 36 */       this.flyingPackets = 0;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   private void handleViolation() {
/* 42 */     this.player.onPacketCancel();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\timer\TickTimer.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */