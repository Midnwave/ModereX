/*    */ package ac.grim.grimac.checks.impl.sprint;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ 
/*    */ @CheckData(name = "SprintE", description = "Sprinting while colliding with a wall", setback = 5.0D, experimental = true)
/*    */ public class SprintE extends Check implements PostPredictionCheck {
/*    */   private boolean startedSprintingThisTick;
/*    */   
/*    */   public SprintE(GrimPlayer player) {
/* 18 */     super(player);
/*    */   }
/*    */   private boolean wasHardHorizontalCollision;
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 23 */     if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION && (
/* 24 */       new WrapperPlayClientEntityAction(event)).getAction() == WrapperPlayClientEntityAction.Action.START_SPRINTING) {
/* 25 */       this.startedSprintingThisTick = true;
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 32 */     if (!predictionComplete.isChecked())
/*    */       return; 
/* 34 */     if (this.wasHardHorizontalCollision && !this.startedSprintingThisTick && !this.player.uncertaintyHandler.isNearGlitchyBlock && 
/* 35 */       !this.player.inVehicle() && !this.player.uncertaintyHandler.lastVehicleSwitch.hasOccurredSince(0) && (!this.player.wasTouchingWater || this.player
/* 36 */       .getClientVersion().isOlderThan(ClientVersion.V_1_13)) && this.player.wasLastPredictionCompleteChecked)
/*    */     {
/* 38 */       if (this.player.isSprinting) {
/* 39 */         flagAndAlertWithSetback();
/*    */       } else {
/* 41 */         reward();
/*    */       } 
/*    */     }
/*    */     
/* 45 */     this.wasHardHorizontalCollision = (this.player.horizontalCollision && !this.player.softHorizontalCollision && this.player.wasLastPredictionCompleteChecked);
/* 46 */     this.startedSprintingThisTick = false;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\sprint\SprintE.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */