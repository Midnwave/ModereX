/*    */ package ac.grim.grimac.checks.impl.badpackets;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
/*    */ 
/*    */ @CheckData(name = "BadPacketsE")
/*    */ public class BadPacketsE extends Check implements PacketCheck {
/*    */   private int noReminderTicks;
/* 17 */   private final int maxNoReminderTicks = this.player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8) ? 20 : 19;
/* 18 */   private final boolean isViaPleaseStopUsingProtocolHacksOnYourServer = (this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21_2) || PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_21_2));
/*    */   
/*    */   public BadPacketsE(GrimPlayer player) {
/* 21 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 26 */     if (event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION || event
/* 27 */       .getPacketType() == PacketType.Play.Client.PLAYER_POSITION) {
/* 28 */       this.noReminderTicks = 0;
/* 29 */     } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType()) && !this.player.packetStateData.lastPacketWasTeleport) {
/* 30 */       if (++this.noReminderTicks > this.maxNoReminderTicks) {
/* 31 */         flagAndAlert("ticks=" + this.noReminderTicks);
/*    */       }
/* 33 */     } else if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE || (this.isViaPleaseStopUsingProtocolHacksOnYourServer && this.player
/* 34 */       .inVehicle())) {
/* 35 */       this.noReminderTicks = 0;
/*    */     } 
/*    */   }
/*    */   
/*    */   public void handleRespawn() {
/* 40 */     this.noReminderTicks = 0;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\badpackets\BadPacketsE.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */