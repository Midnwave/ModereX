/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.reader.impl;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.ChatType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.ChatTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.ChatMessage;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.ChatMessageLegacy;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.reader.ChatMessageProcessor;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
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
/*    */ public class ChatMessageProcessorLegacy
/*    */   implements ChatMessageProcessor
/*    */ {
/*    */   public ChatMessage readChatMessage(@NotNull PacketWrapper<?> wrapper) {
/* 33 */     Component chatContent = wrapper.readComponent();
/* 34 */     ChatType type = (ChatType)wrapper.readMappedEntity((IRegistry)ChatTypes.getRegistry());
/* 35 */     return (ChatMessage)new ChatMessageLegacy(chatContent, type);
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeChatMessage(@NotNull PacketWrapper<?> wrapper, @NotNull ChatMessage data) {
/* 40 */     wrapper.writeComponent(data.getChatContent());
/* 41 */     wrapper.writeMappedEntity((MappedEntity)data.getType());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\message\reader\impl\ChatMessageProcessorLegacy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */