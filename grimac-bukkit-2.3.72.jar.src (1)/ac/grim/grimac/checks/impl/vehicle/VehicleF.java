/*    */ package ac.grim.grimac.checks.impl.vehicle;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSteerBoat;
/*    */ import ac.grim.grimac.utils.data.KnownInput;
/*    */ import ac.grim.grimac.utils.data.packetentity.PacketEntity;
/*    */ 
/*    */ @CheckData(name = "VehicleF", experimental = true, description = "Sent incorrect boat paddle states")
/*    */ public class VehicleF extends Check implements PacketCheck {
/*    */   public VehicleF(GrimPlayer player) {
/* 16 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   private PacketEntity lastTickVehicle;
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 23 */     if (event.getPacketType() == PacketType.Play.Client.STEER_BOAT) {
/*    */       boolean expectedLeft, expectedRight;
/* 25 */       if (this.lastTickVehicle != this.player.getVehicle())
/*    */         return; 
/* 27 */       WrapperPlayClientSteerBoat packet = new WrapperPlayClientSteerBoat(event);
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 32 */       if (this.player.supportsEndTick()) {
/* 33 */         KnownInput input = this.player.packetStateData.knownInput;
/* 34 */         expectedLeft = (input.forward() || (!input.left() && input.right()));
/* 35 */         expectedRight = (input.forward() || (input.left() && !input.right()));
/*    */       } else {
/* 37 */         expectedLeft = (this.player.vehicleData.nextVehicleForward > 0.0F || this.player.vehicleData.nextVehicleHorizontal < 0.0F);
/* 38 */         expectedRight = (this.player.vehicleData.nextVehicleForward > 0.0F || this.player.vehicleData.nextVehicleHorizontal > 0.0F);
/*    */         
/* 40 */         if (this.player.vehicleData.nextVehicleForward == 0.0F && packet.isLeftPaddleTurning() && packet.isRightPaddleTurning()) {
/*    */           return;
/*    */         }
/*    */       } 
/*    */       
/* 45 */       if ((packet.isLeftPaddleTurning() != expectedLeft || packet.isRightPaddleTurning() != expectedRight) && 
/* 46 */         flagAndAlert("sent=(" + packet.isLeftPaddleTurning() + ", " + packet.isRightPaddleTurning() + "), expected=(" + expectedLeft + ", " + expectedRight + ")") && 
/* 47 */         shouldModifyPackets()) {
/* 48 */         packet.setLeftPaddleTurning(expectedLeft);
/* 49 */         packet.setRightPaddleTurning(expectedRight);
/* 50 */         event.markForReEncode(true);
/*    */       } 
/*    */     } 
/*    */ 
/*    */     
/* 55 */     if (isTickPacket(event.getPacketType()))
/* 56 */       this.lastTickVehicle = this.player.getVehicle(); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\vehicle\VehicleF.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */