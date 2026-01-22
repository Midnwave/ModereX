/*    */ package ac.grim.grimac.events.packets;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerAbstract;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerPriority;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPong;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientWindowConfirmation;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPing;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowConfirmation;
/*    */ import ac.grim.grimac.utils.data.Pair;
/*    */ 
/*    */ public class PacketPingListener
/*    */   extends PacketListenerAbstract
/*    */ {
/*    */   public PacketPingListener() {
/* 20 */     super(PacketListenerPriority.LOWEST);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 26 */     if (event.getPacketType() == PacketType.Play.Client.WINDOW_CONFIRMATION) {
/* 27 */       WrapperPlayClientWindowConfirmation transaction = new WrapperPlayClientWindowConfirmation(event);
/* 28 */       short id = transaction.getActionId();
/*    */       
/* 30 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/* 31 */       if (player == null)
/* 32 */         return;  player.packetStateData.lastTransactionPacketWasValid = false;
/*    */ 
/*    */ 
/*    */       
/* 36 */       if (id <= 0 && player.addTransactionResponse(id)) {
/* 37 */         player.packetStateData.lastTransactionPacketWasValid = true;
/* 38 */         event.setCancelled(true);
/*    */       } 
/*    */     } 
/*    */     
/* 42 */     if (event.getPacketType() == PacketType.Play.Client.PONG) {
/* 43 */       WrapperPlayClientPong pong = new WrapperPlayClientPong(event);
/* 44 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/* 45 */       if (player == null)
/* 46 */         return;  player.packetStateData.lastTransactionPacketWasValid = false;
/*    */       
/* 48 */       int id = pong.getId();
/*    */ 
/*    */       
/* 51 */       if (id == (short)id) {
/* 52 */         short shortID = (short)id;
/* 53 */         if (player.addTransactionResponse(shortID)) {
/* 54 */           player.packetStateData.lastTransactionPacketWasValid = true;
/*    */           
/* 56 */           event.setCancelled(!GrimAPI.INSTANCE.getConfigManager().isDisablePongCancelling());
/*    */         } 
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketSend(PacketSendEvent event) {
/* 64 */     if (event.getPacketType() == PacketType.Play.Server.WINDOW_CONFIRMATION) {
/* 65 */       WrapperPlayServerWindowConfirmation confirmation = new WrapperPlayServerWindowConfirmation(event);
/* 66 */       short id = confirmation.getActionId();
/*    */       
/* 68 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/* 69 */       if (player == null)
/* 70 */         return;  player.packetStateData.lastServerTransWasValid = false;
/*    */       
/* 72 */       if (id <= 0 && 
/* 73 */         player.didWeSendThatTrans.remove(Short.valueOf(id))) {
/* 74 */         player.packetStateData.lastServerTransWasValid = true;
/* 75 */         player.transactionsSent.add(new Pair(Short.valueOf(id), Long.valueOf(System.nanoTime())));
/* 76 */         player.lastTransactionSent.getAndIncrement();
/*    */       } 
/*    */     } 
/*    */ 
/*    */     
/* 81 */     if (event.getPacketType() == PacketType.Play.Server.PING) {
/* 82 */       WrapperPlayServerPing pong = new WrapperPlayServerPing(event);
/* 83 */       int id = pong.getId();
/*    */       
/* 85 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/* 86 */       if (player == null)
/* 87 */         return;  player.packetStateData.lastServerTransWasValid = false;
/*    */       
/* 89 */       if (id == (short)id) {
/*    */         
/* 91 */         Short shortID = Short.valueOf((short)id);
/* 92 */         if (player.didWeSendThatTrans.remove(shortID)) {
/* 93 */           player.packetStateData.lastServerTransWasValid = true;
/* 94 */           player.transactionsSent.add(new Pair(shortID, Long.valueOf(System.nanoTime())));
/* 95 */           player.lastTransactionSent.getAndIncrement();
/*    */         } 
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\events\packets\PacketPingListener.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */