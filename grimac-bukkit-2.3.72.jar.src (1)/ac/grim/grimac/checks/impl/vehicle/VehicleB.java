/*    */ package ac.grim.grimac.checks.impl.vehicle;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ 
/*    */ @CheckData(name = "VehicleB", description = "Claimed to be in a vehicle while not in a vehicle")
/*    */ public class VehicleB extends Check implements PacketCheck {
/*    */   public VehicleB(GrimPlayer player) {
/* 13 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 18 */     if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE && 
/* 19 */       !this.player.inVehicle() && 
/* 20 */       flagAndAlert() && shouldModifyPackets()) {
/* 21 */       event.setCancelled(true);
/* 22 */       this.player.onPacketCancel();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\vehicle\VehicleB.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */