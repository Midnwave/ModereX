package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.reader;

import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message.ChatMessage;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;

public interface ChatMessageProcessor {
  ChatMessage readChatMessage(@NotNull PacketWrapper<?> paramPacketWrapper);
  
  void writeChatMessage(@NotNull PacketWrapper<?> paramPacketWrapper, @NotNull ChatMessage paramChatMessage);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\message\reader\ChatMessageProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */