/*    */ package ac.grim.grimac.checks.impl.elytra;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion.PotionTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ 
/*    */ @CheckData(name = "ElytraG", description = "Started gliding with levitation", experimental = true)
/*    */ public class ElytraG extends Check implements PostPredictionCheck {
/*    */   private boolean setback;
/*    */   
/*    */   public ElytraG(GrimPlayer player) {
/* 19 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 24 */     if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION && (new WrapperPlayClientEntityAction(event))
/* 25 */       .getAction() == WrapperPlayClientEntityAction.Action.START_FLYING_WITH_ELYTRA && this.player
/* 26 */       .getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_16) && this.player.compensatedEntities.self
/* 27 */       .hasPotionEffect(PotionTypes.LEVITATION) && 
/* 28 */       flagAndAlert()) {
/*    */       
/* 30 */       this.setback = true;
/* 31 */       if (shouldModifyPackets()) {
/* 32 */         event.setCancelled(true);
/* 33 */         this.player.onPacketCancel();
/* 34 */         this.player.resyncPose();
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 41 */     if (this.setback) {
/* 42 */       setbackIfAboveSetbackVL();
/* 43 */       this.setback = false;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\elytra\ElytraG.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */