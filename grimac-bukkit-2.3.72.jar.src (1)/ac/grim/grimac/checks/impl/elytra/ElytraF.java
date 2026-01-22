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
/*    */ @CheckData(name = "ElytraF", description = "Started gliding while on ground", experimental = true)
/*    */ public class ElytraF extends Check implements PostPredictionCheck {
/*    */   private boolean setback;
/*    */   
/*    */   public ElytraF(GrimPlayer player) {
/* 18 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 23 */     if (this.player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8)) {
/*    */       return;
/*    */     }
/*    */     
/* 27 */     if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION && (new WrapperPlayClientEntityAction(event))
/* 28 */       .getAction() == WrapperPlayClientEntityAction.Action.START_FLYING_WITH_ELYTRA && this.player.clientClaimsLastOnGround && 
/*    */       
/* 30 */       flagAndAlert()) {
/*    */       
/* 32 */       this.setback = true;
/* 33 */       if (shouldModifyPackets()) {
/* 34 */         event.setCancelled(true);
/* 35 */         this.player.onPacketCancel();
/* 36 */         this.player.resyncPose();
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 43 */     if (this.setback) {
/* 44 */       setbackIfAboveSetbackVL();
/* 45 */       this.setback = false;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\elytra\ElytraF.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */