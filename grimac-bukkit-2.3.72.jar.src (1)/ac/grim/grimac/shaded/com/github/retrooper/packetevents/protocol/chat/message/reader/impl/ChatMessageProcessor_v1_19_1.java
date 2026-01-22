/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.reader.impl;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.ChatType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.LastSeenMessages;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.filter.FilterMask;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.ChatMessage;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_19_1;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.reader.ChatMessageProcessor;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.TextComponent;
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
/*    */ public class ChatMessageProcessor_v1_19_1
/*    */   implements ChatMessageProcessor
/*    */ {
/*    */   public ChatMessage readChatMessage(@NotNull PacketWrapper<?> wrapper) {
/*    */     TextComponent textComponent;
/* 38 */     byte[] previousSignature = (byte[])wrapper.readOptional(PacketWrapper::readByteArray);
/* 39 */     UUID senderUUID = wrapper.readUUID();
/* 40 */     byte[] signature = wrapper.readByteArray();
/* 41 */     String plainContent = wrapper.readString(256);
/* 42 */     Component chatContent = (Component)wrapper.readOptional(PacketWrapper::readComponent);
/* 43 */     if (chatContent == null && plainContent.isEmpty()) {
/* 44 */       textComponent = Component.empty();
/* 45 */     } else if (textComponent == null) {
/* 46 */       textComponent = Component.text(plainContent);
/*    */     } 
/* 48 */     Instant timestamp = wrapper.readTimestamp();
/* 49 */     long salt = wrapper.readLong();
/* 50 */     LastSeenMessages lastSeenMessages = wrapper.readLastSeenMessages();
/* 51 */     Component unsignedChatContent = (Component)wrapper.readOptional(PacketWrapper::readComponent);
/* 52 */     FilterMask filterMask = wrapper.readFilterMask();
/* 53 */     ChatType.Bound chatFormatting = wrapper.readChatTypeBoundNetwork();
/* 54 */     return (ChatMessage)new ChatMessage_v1_19_1(plainContent, (Component)textComponent, unsignedChatContent, senderUUID, chatFormatting, previousSignature, signature, timestamp, salt, lastSeenMessages, filterMask);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void writeChatMessage(@NotNull PacketWrapper<?> wrapper, @NotNull ChatMessage data) {
/* 60 */     ChatMessage_v1_19_1 newData = (ChatMessage_v1_19_1)data;
/* 61 */     wrapper.writeOptional(newData.getPreviousSignature(), PacketWrapper::writeByteArray);
/* 62 */     wrapper.writeUUID(newData.getSenderUUID());
/* 63 */     wrapper.writeByteArray(newData.getSignature());
/* 64 */     wrapper.writeString(newData.getPlainContent(), 256);
/* 65 */     wrapper.writeOptional(newData.getChatContent(), PacketWrapper::writeComponent);
/* 66 */     wrapper.writeTimestamp(newData.getTimestamp());
/* 67 */     wrapper.writeLong(newData.getSalt());
/* 68 */     wrapper.writeLastSeenMessages(newData.getLastSeenMessages());
/* 69 */     wrapper.writeOptional(newData.getUnsignedChatContent(), PacketWrapper::writeComponent);
/* 70 */     wrapper.writeFilterMask(newData.getFilterMask());
/* 71 */     wrapper.writeChatTypeBoundNetwork(newData.getChatFormatting());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\message\reader\impl\ChatMessageProcessor_v1_19_1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */