/*    */ package ac.grim.grimac.events.packets;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerAbstract;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerPriority;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetCooldown;
/*    */ 
/*    */ public class PacketPlayerCooldown
/*    */   extends PacketListenerAbstract {
/*    */   public PacketPlayerCooldown() {
/* 14 */     super(PacketListenerPriority.HIGH);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketSend(PacketSendEvent event) {
/* 19 */     if (event.getPacketType() == PacketType.Play.Server.SET_COOLDOWN) {
/* 20 */       WrapperPlayServerSetCooldown cooldown = new WrapperPlayServerSetCooldown(event);
/*    */       
/* 22 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/* 23 */       if (player == null)
/*    */         return; 
/* 25 */       int lastTransactionSent = player.lastTransactionSent.get();
/*    */       
/* 27 */       if (cooldown.getCooldownTicks() == 0) {
/* 28 */         player.latencyUtils.addRealTimeTask(lastTransactionSent + 1, () -> player.checkManager.getCompensatedCooldown().removeCooldown(cooldown.getCooldownGroup()));
/*    */       } else {
/*    */         
/* 31 */         player.latencyUtils.addRealTimeTask(lastTransactionSent, () -> player.checkManager.getCompensatedCooldown().addCooldown(cooldown.getCooldownGroup(), cooldown.getCooldownTicks(), lastTransactionSent));
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\events\packets\PacketPlayerCooldown.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */