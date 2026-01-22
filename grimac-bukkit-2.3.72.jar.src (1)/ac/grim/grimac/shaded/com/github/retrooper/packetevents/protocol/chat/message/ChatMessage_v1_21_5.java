/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.ChatType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.LastSeenMessages;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.filter.FilterMask;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ChatMessage_v1_21_5
/*    */   extends ChatMessage_v1_19_3
/*    */ {
/*    */   int globalIndex;
/*    */   
/*    */   public ChatMessage_v1_21_5(int globalIndex, UUID senderUUID, int index, byte[] signature, String plainContent, Instant timestamp, long salt, LastSeenMessages.Packed lastSeenMessagesPacked, @Nullable Component unsignedChatContent, FilterMask filterMask, ChatType.Bound chatFormatting) {
/* 38 */     super(senderUUID, index, signature, plainContent, timestamp, salt, lastSeenMessagesPacked, unsignedChatContent, filterMask, chatFormatting);
/* 39 */     this.globalIndex = globalIndex;
/*    */   }
/*    */   
/*    */   public int getGlobalIndex() {
/* 43 */     return this.globalIndex;
/*    */   }
/*    */   
/*    */   public void setGlobalIndex(int globalIndex) {
/* 47 */     this.globalIndex = globalIndex;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\message\ChatMessage_v1_21_5.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */