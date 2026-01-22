/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.ChatType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.adventure.AdventureSerializer;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ChatMessage
/*    */ {
/*    */   private Component chatContent;
/*    */   private ChatType type;
/*    */   
/*    */   protected ChatMessage(Component chatContent, ChatType type) {
/* 31 */     this.chatContent = chatContent;
/* 32 */     this.type = type;
/*    */   }
/*    */   
/*    */   public Component getChatContent() {
/* 36 */     return this.chatContent;
/*    */   }
/*    */   
/*    */   public String getChatContentJson(ClientVersion version) {
/* 40 */     return AdventureSerializer.serializer(version).asJson(getChatContent());
/*    */   }
/*    */   
/*    */   public void setChatContent(Component chatContent) {
/* 44 */     this.chatContent = chatContent;
/*    */   }
/*    */   
/*    */   public void setChatContentJson(ClientVersion version, String json) {
/* 48 */     setChatContent(AdventureSerializer.serializer(version).fromJson(json));
/*    */   }
/*    */   
/*    */   public ChatType getType() {
/* 52 */     return this.type;
/*    */   }
/*    */   
/*    */   public void setType(ChatType type) {
/* 56 */     this.type = type;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\message\ChatMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */