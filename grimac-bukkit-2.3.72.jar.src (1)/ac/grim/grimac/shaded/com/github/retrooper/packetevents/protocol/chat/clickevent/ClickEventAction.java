package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.clickevent;

import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface ClickEventAction<T extends ClickEvent> extends MappedEntity {
  boolean isAllowFromServer();
  
  T decode(NBTCompound paramNBTCompound, PacketWrapper<?> paramPacketWrapper);
  
  void encode(NBTCompound paramNBTCompound, PacketWrapper<?> paramPacketWrapper, T paramT);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\clickevent\ClickEventAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */