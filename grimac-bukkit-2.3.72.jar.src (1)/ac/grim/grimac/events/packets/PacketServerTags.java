/*    */ package ac.grim.grimac.events.packets;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerAbstract;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTags;
/*    */ 
/*    */ public class PacketServerTags
/*    */   extends PacketListenerAbstract
/*    */ {
/*    */   public void onPacketSend(PacketSendEvent event) {
/* 14 */     if (event.getPacketType() == PacketType.Play.Server.TAGS || event.getPacketType() == PacketType.Configuration.Server.UPDATE_TAGS) {
/* 15 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/* 16 */       if (player == null)
/*    */         return; 
/* 18 */       WrapperPlayServerTags tags = new WrapperPlayServerTags(event);
/* 19 */       boolean isPlay = (event.getPacketType() == PacketType.Play.Server.TAGS);
/* 20 */       if (isPlay) {
/* 21 */         player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> player.tagManager.handleTagSync(tags));
/*    */       } else {
/*    */         
/* 24 */         player.tagManager.handleTagSync(tags);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\events\packets\PacketServerTags.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */