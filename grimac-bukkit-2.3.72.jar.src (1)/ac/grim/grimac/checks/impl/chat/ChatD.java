/*    */ package ac.grim.grimac.checks.impl.chat;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.common.client.WrapperCommonClientSettings;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSettings;
/*    */ 
/*    */ @CheckData(name = "ChatD", description = "Chatting while chat is hidden", experimental = true)
/*    */ public class ChatD extends Check implements PacketCheck {
/*    */   private boolean hidden;
/*    */   
/*    */   public ChatD(GrimPlayer player) {
/* 17 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 22 */     if ((event.getPacketType() == PacketType.Play.Client.CHAT_MESSAGE || event
/* 23 */       .getPacketType() == PacketType.Play.Client.CHAT_COMMAND_UNSIGNED || event
/* 24 */       .getPacketType() == PacketType.Play.Client.CHAT_COMMAND) && 
/* 25 */       this.hidden && flagAndAlert() && shouldModifyPackets()) {
/* 26 */       event.setCancelled(true);
/* 27 */       this.player.onPacketCancel();
/*    */     } 
/*    */ 
/*    */     
/* 31 */     if (event.getPacketType() == PacketType.Play.Client.CLIENT_SETTINGS || event.getPacketType() == PacketType.Configuration.Client.CLIENT_SETTINGS)
/* 32 */       this.hidden = ((new WrapperPlayClientSettings(event)).getChatVisibility() == WrapperCommonClientSettings.ChatVisibility.HIDDEN); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\chat\ChatD.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */