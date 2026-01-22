/*    */ package ac.grim.grimac.checks.impl.badpackets;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*    */ 
/*    */ @CheckData(name = "BadPacketsR", decay = 0.25D, experimental = true)
/*    */ public class BadPacketsR extends Check implements PacketCheck {
/* 13 */   private int positions = 0;
/* 14 */   private long clock = 0L;
/*    */   private long lastTransTime;
/* 16 */   private int oldTransId = 0;
/*    */   
/*    */   public BadPacketsR(GrimPlayer player) {
/* 19 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 24 */     if (isTransaction(event.getPacketType()) && this.player.packetStateData.lastTransactionPacketWasValid) {
/* 25 */       long ms = (this.player.getPlayerClockAtLeast() - this.clock) / 1000000L;
/* 26 */       long diff = System.currentTimeMillis() - this.lastTransTime;
/* 27 */       if (diff > 2000L && ms > 2000L) {
/* 28 */         if (this.positions == 0 && this.clock != 0L && this.player.gamemode != GameMode.SPECTATOR && !this.player.compensatedEntities.self.isDead) {
/* 29 */           flag("time=" + ms + "ms, lst=" + diff + "ms, positions=" + this.positions);
/*    */         } else {
/* 31 */           reward();
/*    */         } 
/* 33 */         this.player.compensatedEntities.entitiesRemovedThisTick.clear();
/* 34 */         this.player.compensatedWorld.removeInvalidPistonLikeStuff(this.oldTransId);
/* 35 */         this.positions = 0;
/* 36 */         this.clock = this.player.getPlayerClockAtLeast();
/* 37 */         this.lastTransTime = System.currentTimeMillis();
/* 38 */         this.oldTransId = this.player.lastTransactionSent.get();
/*    */       } 
/*    */     } 
/*    */     
/* 42 */     if ((event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION || event
/* 43 */       .getPacketType() == PacketType.Play.Client.PLAYER_POSITION) && !this.player.inVehicle()) {
/* 44 */       this.positions++;
/* 45 */     } else if ((event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE || event.getPacketType() == PacketType.Play.Client.VEHICLE_MOVE) && this.player
/* 46 */       .inVehicle()) {
/* 47 */       this.positions++;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\badpackets\BadPacketsR.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */