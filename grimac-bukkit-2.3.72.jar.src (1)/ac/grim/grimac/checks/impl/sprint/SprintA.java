/*    */ package ac.grim.grimac.checks.impl.sprint;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
/*    */ 
/*    */ @CheckData(name = "SprintA", description = "Sprinting with too low hunger", setback = 0.0D)
/*    */ public class SprintA
/*    */   extends Check implements PacketCheck {
/*    */   public SprintA(GrimPlayer player) {
/* 14 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 19 */     if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
/*    */       
/* 21 */       if (this.player.canFly)
/*    */         return; 
/* 23 */       if (this.player.food < 6.0F && this.player.isSprinting) {
/* 24 */         if (flagAndAlert("hunger=" + this.player.food)) {
/*    */           
/* 26 */           if (shouldModifyPackets()) {
/* 27 */             event.setCancelled(true);
/* 28 */             this.player.onPacketCancel();
/*    */           } 
/* 30 */           if (shouldSetback()) {
/* 31 */             this.player.getSetbackTeleportUtil().executeNonSimulatingSetback();
/*    */           }
/*    */         } 
/*    */       } else {
/* 35 */         reward();
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\sprint\SprintA.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */