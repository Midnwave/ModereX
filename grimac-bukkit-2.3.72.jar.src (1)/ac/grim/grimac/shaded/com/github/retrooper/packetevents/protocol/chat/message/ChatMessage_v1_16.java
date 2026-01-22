/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.ChatType;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import java.util.UUID;
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
/*    */ public class ChatMessage_v1_16
/*    */   extends ChatMessage
/*    */ {
/*    */   private UUID senderUUID;
/*    */   
/*    */   public ChatMessage_v1_16(Component chatContent, ChatType type, UUID senderUUID) {
/* 30 */     super(chatContent, type);
/* 31 */     this.senderUUID = senderUUID;
/*    */   }
/*    */   
/*    */   public UUID getSenderUUID() {
/* 35 */     return this.senderUUID;
/*    */   }
/*    */   
/*    */   public void setSenderUUID(UUID senderUUID) {
/* 39 */     this.senderUUID = senderUUID;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\message\ChatMessage_v1_16.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */