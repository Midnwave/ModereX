/*    */ package ac.grim.grimac.checks.impl.chat;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatCommand;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatCommandUnsigned;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage;
/*    */ 
/*    */ @CheckData(name = "ChatB", description = "Invalid chat message")
/*    */ public class ChatB
/*    */   extends Check
/*    */   implements PacketCheck {
/*    */   public ChatB(GrimPlayer player) {
/* 20 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 25 */     if (event.getPacketType() == PacketType.Play.Client.CHAT_MESSAGE) {
/* 26 */       String message = (new WrapperPlayClientChatMessage(event)).getMessage();
/* 27 */       if ((message.isEmpty() || !message.trim().equals(message) || (message.startsWith("/") && PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_19))) && 
/* 28 */         flagAndAlert("message=" + message)) {
/* 29 */         event.setCancelled(true);
/* 30 */         this.player.onPacketCancel();
/*    */       } 
/*    */     } 
/*    */ 
/*    */     
/* 35 */     if (event.getPacketType() == PacketType.Play.Client.CHAT_COMMAND_UNSIGNED) {
/* 36 */       String command = "/" + (new WrapperPlayClientChatCommandUnsigned(event)).getCommand();
/* 37 */       if (!command.stripTrailing().equals(command) && 
/* 38 */         flagAndAlert("command=" + command)) {
/* 39 */         event.setCancelled(true);
/* 40 */         this.player.onPacketCancel();
/*    */       } 
/*    */     } 
/*    */ 
/*    */     
/* 45 */     if (event.getPacketType() == PacketType.Play.Client.CHAT_COMMAND) {
/*    */       
/* 47 */       String command = "/" + (new WrapperPlayClientChatCommand(event)).getCommand();
/* 48 */       if (!command.trim().equals(command) && 
/* 49 */         flagAndAlert("command=" + command)) {
/* 50 */         event.setCancelled(true);
/* 51 */         this.player.onPacketCancel();
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\chat\ChatB.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */