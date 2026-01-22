/*    */ package ac.grim.grimac.checks.impl.elytra;
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
/*    */ @CheckData(name = "ElytraA", description = "Started gliding while already gliding", experimental = true)
/*    */ public class ElytraA extends Check implements PostPredictionCheck {
/*    */   private boolean setback;
/*    */   
/*    */   public ElytraA(GrimPlayer player) {
/* 18 */     super(player);
/*    */   }
/*    */   
/*    */   public void onStartGliding(PacketReceiveEvent event) {
/* 22 */     if (this.player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8)) {
/*    */       return;
/*    */     }
/*    */     
/* 26 */     if (this.player.isGliding && flagAndAlert()) {
/* 27 */       this.setback = true;
/* 28 */       if (shouldModifyPackets()) {
/* 29 */         event.setCancelled(true);
/* 30 */         this.player.onPacketCancel();
/* 31 */         this.player.resyncPose();
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 38 */     if (this.player.getClientVersion().isOlderThan(ClientVersion.V_1_15) && event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION && (new WrapperPlayClientEntityAction(event))
/* 39 */       .getAction() == WrapperPlayClientEntityAction.Action.START_FLYING_WITH_ELYTRA) {
/* 40 */       onStartGliding(event);
/*    */     }
/*    */   }
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 45 */     if (this.setback) {
/* 46 */       setbackIfAboveSetbackVL();
/* 47 */       this.setback = false;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\elytra\ElytraA.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */