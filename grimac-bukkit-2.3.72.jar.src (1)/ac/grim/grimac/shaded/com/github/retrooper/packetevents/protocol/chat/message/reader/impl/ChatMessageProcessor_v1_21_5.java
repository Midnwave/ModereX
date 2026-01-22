/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.reader.impl;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.ChatType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.LastSeenMessages;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.filter.FilterMask;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.ChatMessage;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_21_5;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.reader.ChatMessageProcessor;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
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
/*    */ public class ChatMessageProcessor_v1_21_5
/*    */   implements ChatMessageProcessor
/*    */ {
/*    */   public ChatMessage readChatMessage(@NotNull PacketWrapper<?> wrapper) {
/* 38 */     int globalIndex = wrapper.readVarInt();
/* 39 */     UUID senderUUID = wrapper.readUUID();
/* 40 */     int index = wrapper.readVarInt();
/* 41 */     byte[] signature = (byte[])wrapper.readOptional(w -> w.readBytes(256));
/* 42 */     String plainContent = wrapper.readString(256);
/* 43 */     Instant timestamp = wrapper.readTimestamp();
/* 44 */     long salt = wrapper.readLong();
/* 45 */     LastSeenMessages.Packed lastSeenMessagesPacked = wrapper.readLastSeenMessagesPacked();
/* 46 */     Component unsignedChatContent = (Component)wrapper.readOptional(PacketWrapper::readComponent);
/* 47 */     FilterMask filterMask = wrapper.readFilterMask();
/* 48 */     ChatType.Bound chatType = wrapper.readChatTypeBoundNetwork();
/*    */     
/* 50 */     return (ChatMessage)new ChatMessage_v1_21_5(globalIndex, senderUUID, index, signature, plainContent, timestamp, salt, lastSeenMessagesPacked, unsignedChatContent, filterMask, chatType);
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeChatMessage(@NotNull PacketWrapper<?> wrapper, @NotNull ChatMessage data) {
/* 55 */     ChatMessage_v1_21_5 newData = (ChatMessage_v1_21_5)data;
/* 56 */     wrapper.writeVarInt(newData.getGlobalIndex());
/* 57 */     wrapper.writeUUID(newData.getSenderUUID());
/* 58 */     wrapper.writeVarInt(newData.getIndex());
/* 59 */     wrapper.writeOptional(newData.getSignature(), PacketWrapper::writeBytes);
/* 60 */     wrapper.writeString(newData.getPlainContent());
/* 61 */     wrapper.writeTimestamp(newData.getTimestamp());
/* 62 */     wrapper.writeLong(newData.getSalt());
/* 63 */     wrapper.writeLastSeenMessagesPacked(newData.getLastSeenMessagesPacked());
/* 64 */     wrapper.writeOptional(newData.getUnsignedChatContent().orElse(null), PacketWrapper::writeComponent);
/* 65 */     wrapper.writeFilterMask(newData.getFilterMask());
/* 66 */     wrapper.writeChatTypeBoundNetwork(newData.getChatFormatting());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\message\reader\impl\ChatMessageProcessor_v1_21_5.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */