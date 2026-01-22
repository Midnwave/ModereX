/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.reader.impl;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.ChatType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.LastSeenMessages;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.filter.FilterMask;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.ChatMessage;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_19_3;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.reader.ChatMessageProcessor;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import java.time.Instant;
/*    */ import java.util.UUID;
/*    */ 
/*    */ 
/*    */ public class ChatMessageProcessor_v1_19_3
/*    */   implements ChatMessageProcessor
/*    */ {
/*    */   public ChatMessage readChatMessage(@NotNull PacketWrapper<?> wrapper) {
/* 20 */     UUID senderUUID = wrapper.readUUID();
/* 21 */     int index = wrapper.readVarInt();
/* 22 */     byte[] signature = (byte[])wrapper.readOptional(w -> w.readBytes(256));
/* 23 */     String plainContent = wrapper.readString(256);
/* 24 */     Instant timestamp = wrapper.readTimestamp();
/* 25 */     long salt = wrapper.readLong();
/* 26 */     LastSeenMessages.Packed lastSeenMessagesPacked = wrapper.readLastSeenMessagesPacked();
/* 27 */     Component unsignedChatContent = (Component)wrapper.readOptional(PacketWrapper::readComponent);
/* 28 */     FilterMask filterMask = wrapper.readFilterMask();
/* 29 */     ChatType.Bound chatType = wrapper.readChatTypeBoundNetwork();
/*    */     
/* 31 */     return (ChatMessage)new ChatMessage_v1_19_3(senderUUID, index, signature, plainContent, timestamp, salt, lastSeenMessagesPacked, unsignedChatContent, filterMask, chatType);
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeChatMessage(@NotNull PacketWrapper<?> wrapper, @NotNull ChatMessage data) {
/* 36 */     ChatMessage_v1_19_3 newData = (ChatMessage_v1_19_3)data;
/* 37 */     wrapper.writeUUID(newData.getSenderUUID());
/* 38 */     wrapper.writeVarInt(newData.getIndex());
/* 39 */     wrapper.writeOptional(newData.getSignature(), PacketWrapper::writeBytes);
/* 40 */     wrapper.writeString(newData.getPlainContent());
/* 41 */     wrapper.writeTimestamp(newData.getTimestamp());
/* 42 */     wrapper.writeLong(newData.getSalt());
/* 43 */     wrapper.writeLastSeenMessagesPacked(newData.getLastSeenMessagesPacked());
/* 44 */     wrapper.writeOptional(newData.getUnsignedChatContent().orElse(null), PacketWrapper::writeComponent);
/* 45 */     wrapper.writeFilterMask(newData.getFilterMask());
/* 46 */     wrapper.writeChatTypeBoundNetwork(newData.getChatFormatting());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\message\reader\impl\ChatMessageProcessor_v1_19_3.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */