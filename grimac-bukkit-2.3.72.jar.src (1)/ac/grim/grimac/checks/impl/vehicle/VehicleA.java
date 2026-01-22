/*    */ package ac.grim.grimac.checks.impl.vehicle;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSteerVehicle;
/*    */ 
/*    */ @CheckData(name = "VehicleA", description = "Impossible input values")
/*    */ public class VehicleA extends Check implements PacketCheck {
/*    */   public VehicleA(GrimPlayer player) {
/* 14 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 19 */     if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
/* 20 */       WrapperPlayClientSteerVehicle packet = new WrapperPlayClientSteerVehicle(event);
/*    */       
/* 22 */       if ((Math.abs(packet.getForward()) > 0.98F || Math.abs(packet.getSideways()) > 0.98F) && 
/* 23 */         flagAndAlert("forwards=" + packet.getForward() + ", sideways=" + packet.getSideways()) && shouldModifyPackets()) {
/* 24 */         event.setCancelled(true);
/* 25 */         this.player.onPacketCancel();
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\vehicle\VehicleA.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */