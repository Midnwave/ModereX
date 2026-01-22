/*    */ package ac.grim.grimac.checks.impl.badpackets;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
/*    */ 
/*    */ @CheckData(name = "BadPacketsV", description = "Did not move far enough", experimental = true)
/*    */ public class BadPacketsV extends Check implements PacketCheck {
/*    */   private int noReminderTicks;
/*    */   
/*    */   public BadPacketsV(GrimPlayer player) {
/* 18 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 23 */     if (!this.player.canSkipTicks() && isTickPacket(event.getPacketType()))
/* 24 */       if (event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION || event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION) {
/* 25 */         int positionAtLeastEveryNTicks = this.player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8) ? 20 : 19;
/*    */         
/* 27 */         if (this.noReminderTicks < positionAtLeastEveryNTicks && !this.player.uncertaintyHandler.lastTeleportTicks.hasOccurredSince(1)) {
/*    */           
/* 29 */           double deltaSq = (new WrapperPlayClientPlayerFlying(event)).getLocation().getPosition().distanceSquared(new Vector3d(this.player.lastX, this.player.lastY, this.player.lastZ));
/* 30 */           if (deltaSq <= this.player.getMovementThreshold() * this.player.getMovementThreshold()) {
/* 31 */             flagAndAlert("delta=" + Math.sqrt(deltaSq));
/*    */           }
/*    */         } 
/*    */         
/* 35 */         this.noReminderTicks = 0;
/*    */       } else {
/* 37 */         this.noReminderTicks++;
/*    */       }  
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\badpackets\BadPacketsV.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */