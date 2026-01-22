/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.reader.impl;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.ChatType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.ChatTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.ChatMessage;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_19;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.reader.ChatMessageProcessor;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
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
/*    */ public class ChatMessageProcessor_v1_19
/*    */   implements ChatMessageProcessor
/*    */ {
/*    */   public ChatMessage readChatMessage(@NotNull PacketWrapper<?> wrapper) {
/* 37 */     Component chatContent = wrapper.readComponent();
/* 38 */     Component unsignedChatContent = (Component)wrapper.readOptional(PacketWrapper::readComponent);
/* 39 */     ChatType type = (ChatType)wrapper.readMappedEntity((IRegistry)ChatTypes.getRegistry());
/* 40 */     UUID senderUUID = wrapper.readUUID();
/* 41 */     Component senderDisplayName = wrapper.readComponent();
/* 42 */     Component teamName = (Component)wrapper.readOptional(PacketWrapper::readComponent);
/* 43 */     Instant timestamp = wrapper.readTimestamp();
/* 44 */     long salt = wrapper.readLong();
/* 45 */     byte[] signature = wrapper.readByteArray();
/* 46 */     return (ChatMessage)new ChatMessage_v1_19(chatContent, unsignedChatContent, type, senderUUID, senderDisplayName, teamName, timestamp, salt, signature);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void writeChatMessage(@NotNull PacketWrapper<?> wrapper, @NotNull ChatMessage data) {
/* 52 */     ChatMessage_v1_19 newData = (ChatMessage_v1_19)data;
/* 53 */     wrapper.writeComponent(newData.getChatContent());
/* 54 */     wrapper.writeOptional(newData.getUnsignedChatContent(), PacketWrapper::writeComponent);
/* 55 */     wrapper.writeMappedEntity((MappedEntity)newData.getType());
/* 56 */     wrapper.writeUUID(newData.getSenderUUID());
/* 57 */     wrapper.writeComponent(newData.getSenderDisplayName());
/* 58 */     wrapper.writeOptional(newData.getTeamName(), PacketWrapper::writeComponent);
/* 59 */     wrapper.writeTimestamp(newData.getTimestamp());
/* 60 */     wrapper.writeLong(newData.getSalt());
/* 61 */     wrapper.writeByteArray(newData.getSignature());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\message\reader\impl\ChatMessageProcessor_v1_19.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */