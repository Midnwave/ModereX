/*    */ package ac.grim.grimac.events.packets;
/*    */ 
/*    */ import ac.grim.grimac.api.config.ConfigManager;
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerAbilities;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerAbilities;
/*    */ 
/*    */ 
/*    */ public class PacketPlayerAbilities
/*    */   extends Check
/*    */   implements PacketCheck
/*    */ {
/*    */   private boolean lastSentPlayerCanFly = false;
/* 19 */   private int maxFlyingPing = 1000;
/*    */   
/*    */   public PacketPlayerAbilities(GrimPlayer player) {
/* 22 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 27 */     if (event.getPacketType() == PacketType.Play.Client.PLAYER_ABILITIES) {
/* 28 */       WrapperPlayClientPlayerAbilities abilities = new WrapperPlayClientPlayerAbilities(event);
/* 29 */       this.player.isFlying = (abilities.isFlying() && this.player.canFly);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketSend(PacketSendEvent event) {
/* 35 */     if (event.getPacketType() == PacketType.Play.Server.PLAYER_ABILITIES) {
/* 36 */       WrapperPlayServerPlayerAbilities abilities = new WrapperPlayServerPlayerAbilities(event);
/* 37 */       this.player.sendTransaction();
/*    */       
/* 39 */       if (this.lastSentPlayerCanFly && !abilities.isFlightAllowed()) {
/* 40 */         int noFlying = this.player.lastTransactionSent.get();
/* 41 */         if (this.maxFlyingPing != -1) {
/* 42 */           this.player.runNettyTaskInMs(() -> { if (this.player.lastTransactionReceived.get() < noFlying) this.player.getSetbackTeleportUtil().executeViolationSetback();  }this.maxFlyingPing);
/*    */         }
/*    */       } 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 50 */       this.lastSentPlayerCanFly = abilities.isFlightAllowed();
/*    */       
/* 52 */       this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> {
/*    */             this.player.canFly = abilities.isFlightAllowed();
/*    */             this.player.isFlying = abilities.isFlying();
/*    */           });
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onReload(ConfigManager config) {
/* 62 */     this.maxFlyingPing = config.getIntElse("max-ping-out-of-flying", 1000);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\events\packets\PacketPlayerAbilities.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */