/*    */ package ac.grim.grimac.checks.impl.multiactions;
/*    */ 
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.BlockPlaceCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*    */ import ac.grim.grimac.utils.anticheat.update.BlockPlace;
/*    */ 
/*    */ @CheckData(name = "MultiActionsG", description = "Attacking or using items while rowing a boat", experimental = true)
/*    */ public class MultiActionsG extends BlockPlaceCheck {
/*    */   public MultiActionsG(GrimPlayer player) {
/* 16 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 21 */     if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY && isCheckActive() && 
/* 22 */       flagAndAlert("interact") && shouldModifyPackets()) {
/* 23 */       event.setCancelled(true);
/* 24 */       this.player.onPacketCancel();
/*    */     } 
/*    */     
/* 27 */     if (event.getPacketType() == PacketType.Play.Client.USE_ITEM && isCheckActive() && 
/* 28 */       flagAndAlert("use") && shouldModifyPackets()) {
/* 29 */       event.setCancelled(true);
/* 30 */       this.player.onPacketCancel();
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void onBlockPlace(BlockPlace place) {
/* 36 */     if (isCheckActive() && flagAndAlert((place.getFace() == BlockFace.OTHER) ? "use" : "place") && shouldModifyPackets() && shouldCancel()) {
/* 37 */       place.resync();
/*    */     }
/*    */   }
/*    */   
/*    */   public boolean isCheckActive() {
/* 42 */     return (this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) && !this.player.vehicleData.wasVehicleSwitch && this.player
/* 43 */       .inVehicle() && (this.player.compensatedEntities.self.getRiding()).type.isInstanceOf(EntityTypes.BOAT) && (this.player.vehicleData.nextVehicleForward != 0.0F || this.player.vehicleData.nextVehicleHorizontal != 0.0F));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\multiactions\MultiActionsG.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */