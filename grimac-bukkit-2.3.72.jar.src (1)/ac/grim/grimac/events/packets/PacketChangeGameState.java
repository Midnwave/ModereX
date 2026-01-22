/*    */ package ac.grim.grimac.events.packets;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChangeGameState;
/*    */ 
/*    */ public class PacketChangeGameState extends Check implements PacketCheck {
/*    */   public PacketChangeGameState(GrimPlayer playerData) {
/* 14 */     super(playerData);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketSend(PacketSendEvent event) {
/* 19 */     if (event.getPacketType() == PacketType.Play.Server.CHANGE_GAME_STATE) {
/* 20 */       WrapperPlayServerChangeGameState packet = new WrapperPlayServerChangeGameState(event);
/*    */       
/* 22 */       if (packet.getReason() == WrapperPlayServerChangeGameState.Reason.CHANGE_GAME_MODE) {
/* 23 */         this.player.sendTransaction();
/*    */         
/* 25 */         this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> {
/*    */               GameMode previous = this.player.gamemode;
/*    */               int gamemode = (int)packet.getValue();
/*    */               if (gamemode < 0 || gamemode >= (GameMode.values()).length) {
/*    */                 this.player.gamemode = GameMode.SURVIVAL;
/*    */               } else {
/*    */                 this.player.gamemode = GameMode.values()[gamemode];
/*    */               } 
/*    */               if (previous == GameMode.SPECTATOR && this.player.gamemode != GameMode.SPECTATOR)
/*    */                 GrimAPI.INSTANCE.getSpectateManager().handlePlayerStopSpectating(this.player.uuid); 
/*    */             });
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\events\packets\PacketChangeGameState.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */