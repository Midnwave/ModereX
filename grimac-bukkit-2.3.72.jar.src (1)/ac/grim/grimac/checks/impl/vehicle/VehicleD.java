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
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
/*    */ 
/*    */ @CheckData(name = "VehicleD", experimental = true, description = "Jumped in a vehicle that cannot jump")
/*    */ public class VehicleD extends Check implements PacketCheck {
/*    */   public VehicleD(GrimPlayer player) {
/* 16 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 21 */     if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION && (new WrapperPlayClientEntityAction(event)).getAction() == WrapperPlayClientEntityAction.Action.START_JUMPING_WITH_HORSE) {
/* 22 */       EntityType vehicle = this.player.getVehicleType();
/*    */       
/* 24 */       if (!EntityTypes.isTypeInstanceOf(vehicle, EntityTypes.ABSTRACT_HORSE) && 
/* 25 */         flagAndAlert("vehicle=" + ((vehicle == null) ? "null" : vehicle.getName().getKey().toLowerCase())) && shouldModifyPackets()) {
/* 26 */         event.setCancelled(true);
/* 27 */         this.player.onPacketCancel();
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\vehicle\VehicleD.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */