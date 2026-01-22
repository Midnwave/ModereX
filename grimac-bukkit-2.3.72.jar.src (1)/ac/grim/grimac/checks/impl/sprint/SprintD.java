/*    */ package ac.grim.grimac.checks.impl.sprint;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion.PotionTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ 
/*    */ @CheckData(name = "SprintD", description = "Started sprinting while having blindness", setback = 5.0D, experimental = true)
/*    */ public class SprintD
/*    */   extends Check implements PostPredictionCheck {
/*    */   public boolean startedSprintingBeforeBlind = false;
/*    */   
/*    */   public SprintD(GrimPlayer player) {
/* 19 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 24 */     if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION && (
/* 25 */       new WrapperPlayClientEntityAction(event)).getAction() == WrapperPlayClientEntityAction.Action.START_SPRINTING) {
/* 26 */       this.startedSprintingBeforeBlind = false;
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 33 */     if (this.player.compensatedEntities.self.hasPotionEffect(PotionTypes.BLINDNESS))
/* 34 */       if (this.player.isSprinting && !this.startedSprintingBeforeBlind)
/* 35 */       { flagAndAlertWithSetback(); }
/* 36 */       else { reward(); }
/*    */        
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\sprint\SprintD.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */