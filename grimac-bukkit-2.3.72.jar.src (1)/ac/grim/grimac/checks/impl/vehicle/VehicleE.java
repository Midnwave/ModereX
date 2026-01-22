/*    */ package ac.grim.grimac.checks.impl.vehicle;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ 
/*    */ @CheckData(name = "VehicleE", experimental = true, description = "Sent boat paddle states while not in a boat")
/*    */ public class VehicleE extends Check implements PacketCheck {
/*    */   public VehicleE(GrimPlayer player) {
/* 15 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 20 */     if (event.getPacketType() == PacketType.Play.Client.STEER_BOAT) {
/* 21 */       EntityType vehicle = this.player.getVehicleType();
/*    */       
/* 23 */       if (!EntityTypes.isTypeInstanceOf(vehicle, EntityTypes.BOAT) && 
/* 24 */         flagAndAlert("vehicle=" + ((vehicle == null) ? "null" : vehicle.getName().getKey().toLowerCase())) && shouldModifyPackets()) {
/* 25 */         event.setCancelled(true);
/* 26 */         this.player.onPacketCancel();
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\vehicle\VehicleE.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */