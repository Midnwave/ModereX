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
/*    */ @CheckData(name = "ElytraI", description = "Started gliding in water", experimental = true)
/*    */ public class ElytraI extends Check implements PostPredictionCheck {
/*    */   private boolean setback;
/*    */   
/*    */   public ElytraI(GrimPlayer player) {
/* 18 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 23 */     if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION && (new WrapperPlayClientEntityAction(event))
/* 24 */       .getAction() == WrapperPlayClientEntityAction.Action.START_FLYING_WITH_ELYTRA && this.player.wasTouchingWater && this.player
/*    */       
/* 26 */       .getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_15) && 
/* 27 */       flagAndAlert()) {
/*    */       
/* 29 */       this.setback = true;
/* 30 */       if (shouldModifyPackets()) {
/* 31 */         event.setCancelled(true);
/* 32 */         this.player.onPacketCancel();
/* 33 */         this.player.resyncPose();
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 40 */     if (this.setback) {
/* 41 */       setbackIfAboveSetbackVL();
/* 42 */       this.setback = false;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\elytra\ElytraI.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */