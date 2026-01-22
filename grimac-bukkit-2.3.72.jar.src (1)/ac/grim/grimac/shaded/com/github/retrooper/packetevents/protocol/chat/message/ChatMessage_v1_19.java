/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.ChatType;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import java.time.Instant;
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
/*    */ public class ChatMessage_v1_19
/*    */   extends ChatMessage_v1_16
/*    */ {
/*    */   @Nullable
/*    */   private Component unsignedChatContent;
/*    */   private Component senderDisplayName;
/*    */   @Nullable
/*    */   private Component teamName;
/*    */   private Instant timestamp;
/*    */   private long salt;
/*    */   private byte[] signature;
/*    */   
/*    */   public ChatMessage_v1_19(Component chatContent, @Nullable Component unsignedChatContent, ChatType type, UUID senderUUID, Component senderDisplayName, @Nullable Component teamName, Instant timestamp, long salt, byte[] signature) {
/* 39 */     super(chatContent, type, senderUUID);
/* 40 */     this.unsignedChatContent = unsignedChatContent;
/* 41 */     this.senderDisplayName = senderDisplayName;
/* 42 */     this.teamName = teamName;
/* 43 */     this.timestamp = timestamp;
/* 44 */     this.salt = salt;
/* 45 */     this.signature = signature;
/*    */   }
/*    */   @Nullable
/*    */   public Component getUnsignedChatContent() {
/* 49 */     return this.unsignedChatContent;
/*    */   }
/*    */   
/*    */   public Component getSenderDisplayName() {
/* 53 */     return this.senderDisplayName;
/*    */   }
/*    */   @Nullable
/*    */   public Component getTeamName() {
/* 57 */     return this.teamName;
/*    */   }
/*    */   
/*    */   public Instant getTimestamp() {
/* 61 */     return this.timestamp;
/*    */   }
/*    */   
/*    */   public long getSalt() {
/* 65 */     return this.salt;
/*    */   }
/*    */   
/*    */   public byte[] getSignature() {
/* 69 */     return this.signature;
/*    */   }
/*    */   
/*    */   public void setUnsignedChatContent(@Nullable Component unsignedChatContent) {
/* 73 */     this.unsignedChatContent = unsignedChatContent;
/*    */   }
/*    */   
/*    */   public void setSenderDisplayName(Component senderDisplayName) {
/* 77 */     this.senderDisplayName = senderDisplayName;
/*    */   }
/*    */   
/*    */   public void setTeamName(@Nullable Component teamName) {
/* 81 */     this.teamName = teamName;
/*    */   }
/*    */   
/*    */   public void setTimestamp(Instant timestamp) {
/* 85 */     this.timestamp = timestamp;
/*    */   }
/*    */   
/*    */   public void setSalt(long salt) {
/* 89 */     this.salt = salt;
/*    */   }
/*    */   
/*    */   public void setSignature(byte[] signature) {
/* 93 */     this.signature = signature;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\message\ChatMessage_v1_19.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */