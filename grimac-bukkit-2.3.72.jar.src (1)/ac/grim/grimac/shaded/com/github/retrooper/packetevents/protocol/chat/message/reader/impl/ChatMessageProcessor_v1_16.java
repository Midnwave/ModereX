/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.reader.impl;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.ChatType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.ChatTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.ChatMessage;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_16;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.reader.ChatMessageProcessor;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
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
/*    */ public class ChatMessageProcessor_v1_16
/*    */   implements ChatMessageProcessor
/*    */ {
/*    */   public ChatMessage readChatMessage(@NotNull PacketWrapper<?> wrapper) {
/* 35 */     Component chatContent = wrapper.readComponent();
/* 36 */     ChatType type = (ChatType)wrapper.readMappedEntity((IRegistry)ChatTypes.getRegistry());
/* 37 */     UUID senderUUID = wrapper.readUUID();
/* 38 */     return (ChatMessage)new ChatMessage_v1_16(chatContent, type, senderUUID);
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeChatMessage(@NotNull PacketWrapper<?> wrapper, @NotNull ChatMessage data) {
/* 43 */     wrapper.writeComponent(data.getChatContent());
/* 44 */     wrapper.writeMappedEntity((MappedEntity)data.getType());
/* 45 */     wrapper.writeUUID(((ChatMessage_v1_16)data).getSenderUUID());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\message\reader\impl\ChatMessageProcessor_v1_16.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */