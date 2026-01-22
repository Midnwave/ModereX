/*    */ package ac.grim.grimac.checks.impl.badpackets;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.Combat;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClientStatus;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChangeGameState;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCombatEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDeathCombatEvent;
/*    */ 
/*    */ @CheckData(name = "BadPacketsM", description = "Tried to respawn while alive", experimental = true)
/*    */ public class BadPacketsM extends Check implements PacketCheck {
/*    */   public BadPacketsM(GrimPlayer player) {
/* 20 */     super(player);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private int exempt;
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 29 */     if (event.getPacketType() == PacketType.Play.Client.CLIENT_STATUS && (new WrapperPlayClientClientStatus(event)).getAction() == WrapperPlayClientClientStatus.Action.PERFORM_RESPAWN) {
/* 30 */       if (this.exempt > 0) {
/* 31 */         this.exempt--;
/*    */         
/*    */         return;
/*    */       } 
/* 35 */       if (!this.player.compensatedEntities.self.isDead && 
/* 36 */         flagAndAlert() && shouldModifyPackets()) {
/* 37 */         event.setCancelled(true);
/* 38 */         this.player.onPacketCancel();
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onPacketSend(PacketSendEvent event) {
/* 46 */     if (event.getPacketType() == PacketType.Play.Server.CHANGE_GAME_STATE && this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) && (new WrapperPlayServerChangeGameState(event))
/* 47 */       .getReason() == WrapperPlayServerChangeGameState.Reason.WIN_GAME) {
/* 48 */       this.player.addRealTimeTaskNow(() -> this.exempt++);
/*    */     }
/*    */     
/* 51 */     if (event.getPacketType() == PacketType.Play.Server.DEATH_COMBAT_EVENT && this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) && (
/* 52 */       new WrapperPlayServerDeathCombatEvent(event)).getPlayerId() == this.player.entityID) {
/* 53 */       this.player.addRealTimeTaskNow(() -> this.exempt++);
/*    */     }
/*    */ 
/*    */     
/* 57 */     if (event.getPacketType() == PacketType.Play.Server.COMBAT_EVENT && this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
/* 58 */       WrapperPlayServerCombatEvent packet = new WrapperPlayServerCombatEvent(event);
/* 59 */       if (packet.getCombat() == Combat.ENTITY_DEAD && packet.getPlayerId() == this.player.entityID)
/* 60 */         this.player.addRealTimeTaskNow(() -> this.exempt++); 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\badpackets\BadPacketsM.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */