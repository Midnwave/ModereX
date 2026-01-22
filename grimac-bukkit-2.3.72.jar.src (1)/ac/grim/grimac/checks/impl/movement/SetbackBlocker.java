/*    */ package ac.grim.grimac.checks.impl.movement;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
/*    */ 
/*    */ public class SetbackBlocker extends Check implements PacketCheck {
/*    */   public SetbackBlocker(GrimPlayer playerData) {
/* 13 */     super(playerData);
/*    */   }
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 17 */     if (this.player.disableGrim) {
/*    */       return;
/*    */     }
/* 20 */     if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY && 
/* 21 */       (this.player.getSetbackTeleportUtil()).cheatVehicleInterpolationDelay > 0) {
/* 22 */       event.setCancelled(true);
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 27 */     if (this.player.packetStateData.lastPacketWasTeleport)
/*    */       return; 
/* 29 */     if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
/*    */       
/* 31 */       if (this.player.getSetbackTeleportUtil().shouldBlockMovement()) {
/* 32 */         event.setCancelled(true);
/*    */       }
/*    */ 
/*    */       
/* 36 */       if (this.player.inVehicle() && event.getPacketType() != PacketType.Play.Client.PLAYER_ROTATION && !this.player.packetStateData.lastPacketWasTeleport) {
/* 37 */         event.setCancelled(true);
/*    */       }
/*    */ 
/*    */       
/* 41 */       if (this.player.isInBed && (new Vector3d(this.player.x, this.player.y, this.player.z)).distanceSquared(this.player.bedPosition) > 1.0D) {
/* 42 */         event.setCancelled(true);
/*    */       }
/*    */ 
/*    */       
/* 46 */       if (this.player.compensatedEntities.self.isDead) {
/* 47 */         event.setCancelled(true);
/*    */       }
/*    */     } 
/*    */     
/* 51 */     if (event.getPacketType() == PacketType.Play.Client.VEHICLE_MOVE) {
/* 52 */       if (this.player.getSetbackTeleportUtil().shouldBlockMovement()) {
/* 53 */         event.setCancelled(true);
/*    */       }
/*    */ 
/*    */       
/* 57 */       if (!this.player.inVehicle()) {
/* 58 */         event.setCancelled(true);
/*    */       }
/*    */ 
/*    */       
/* 62 */       if (this.player.isInBed) {
/* 63 */         event.setCancelled(true);
/*    */       }
/*    */ 
/*    */       
/* 67 */       if (this.player.compensatedEntities.self.isDead)
/* 68 */         event.setCancelled(true); 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\movement\SetbackBlocker.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */