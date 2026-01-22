/*    */ package ac.grim.grimac.checks.impl.elytra;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ 
/*    */ @CheckData(name = "ElytraB", description = "Started gliding without jumping", experimental = true)
/*    */ public class ElytraB extends Check implements PostPredictionCheck {
/*    */   private boolean glide;
/*    */   private boolean setback;
/*    */   
/*    */   public ElytraB(GrimPlayer player) {
/* 18 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 23 */     if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION && (new WrapperPlayClientEntityAction(event))
/* 24 */       .getAction() == WrapperPlayClientEntityAction.Action.START_FLYING_WITH_ELYTRA && this.player
/* 25 */       .supportsEndTick())
/*    */     {
/* 27 */       if (this.player.packetStateData.knownInput.jump()) {
/* 28 */         if (flagAndAlert("no release")) {
/* 29 */           this.setback = true;
/* 30 */           if (shouldModifyPackets()) {
/* 31 */             event.setCancelled(true);
/* 32 */             this.player.onPacketCancel();
/* 33 */             this.player.resyncPose();
/*    */           } 
/*    */         } 
/*    */       } else {
/* 37 */         this.glide = true;
/*    */       } 
/*    */     }
/*    */     
/* 41 */     if (isUpdate(event.getPacketType())) {
/* 42 */       if (this.glide && !this.player.packetStateData.knownInput.jump() && flagAndAlert("no jump")) {
/* 43 */         this.setback = true;
/*    */       }
/*    */       
/* 46 */       this.glide = false;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 52 */     if (this.setback) {
/* 53 */       this.setback = false;
/* 54 */       setbackIfAboveSetbackVL();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\elytra\ElytraB.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */