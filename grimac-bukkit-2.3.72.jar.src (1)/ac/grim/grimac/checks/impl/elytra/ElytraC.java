/*    */ package ac.grim.grimac.checks.impl.elytra;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ 
/*    */ @CheckData(name = "ElytraC", description = "Started gliding too frequently", experimental = true)
/*    */ public class ElytraC
/*    */   extends Check implements PostPredictionCheck {
/*    */   private boolean glideThisTick;
/*    */   private boolean glideLastTick;
/*    */   
/*    */   public ElytraC(GrimPlayer player) {
/* 21 */     super(player);
/*    */   }
/*    */   private boolean setback; private int flags; public boolean exempt;
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 26 */     if (this.player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8)) {
/*    */       return;
/*    */     }
/*    */     
/* 30 */     if (this.player.gamemode == GameMode.SPECTATOR) {
/* 31 */       this.glideThisTick = this.glideLastTick = false;
/*    */     }
/*    */     
/* 34 */     if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION && (new WrapperPlayClientEntityAction(event)).getAction() == WrapperPlayClientEntityAction.Action.START_FLYING_WITH_ELYTRA && !this.exempt) {
/* 35 */       if (this.glideThisTick || this.glideLastTick) {
/* 36 */         if (this.player.canSkipTicks()) {
/* 37 */           this.flags++;
/*    */         }
/* 39 */         else if (flagAndAlert()) {
/* 40 */           this.setback = true;
/* 41 */           if (shouldModifyPackets()) {
/* 42 */             event.setCancelled(true);
/* 43 */             this.player.onPacketCancel();
/* 44 */             this.player.resyncPose();
/*    */           } 
/*    */         } 
/*    */       }
/*    */ 
/*    */       
/* 50 */       this.glideThisTick = true;
/*    */     } 
/*    */     
/* 53 */     if (isTickPacket(event.getPacketType())) {
/* 54 */       this.glideLastTick = this.glideThisTick;
/* 55 */       this.glideThisTick = this.exempt = false;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 61 */     if (this.player.canSkipTicks()) {
/* 62 */       if (this.player.isTickingReliablyFor(3)) {
/* 63 */         for (; this.flags > 0; this.flags--) {
/* 64 */           flagAndAlert();
/*    */         }
/*    */       }
/*    */       
/* 68 */       this.flags = 0;
/* 69 */       this.setback = false;
/*    */     } 
/*    */     
/* 72 */     if (this.setback) {
/* 73 */       this.setback = false;
/* 74 */       setbackIfAboveSetbackVL();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\elytra\ElytraC.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */