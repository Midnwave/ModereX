/*    */ package ac.grim.grimac.checks.impl.timer;
/*    */ 
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
/*    */ 
/*    */ @CheckData(name = "VehicleTimer", setback = 10.0D)
/*    */ public class VehicleTimer extends Timer {
/*    */   boolean isDummy = false;
/*    */   
/*    */   public VehicleTimer(GrimPlayer player) {
/* 13 */     super(player);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean shouldCountPacketForTimer(PacketTypeCommon packetType) {
/* 19 */     if (this.player.packetStateData.lastPacketWasTeleport) return false;
/*    */     
/* 21 */     if (packetType == PacketType.Play.Client.VEHICLE_MOVE) {
/* 22 */       this.isDummy = false;
/* 23 */       return true;
/*    */     } 
/*    */     
/* 26 */     if (packetType == PacketType.Play.Client.STEER_VEHICLE) {
/* 27 */       if (this.isDummy) {
/* 28 */         return true;
/*    */       }
/* 30 */       this.isDummy = true;
/*    */     } 
/*    */     
/* 33 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\timer\VehicleTimer.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */