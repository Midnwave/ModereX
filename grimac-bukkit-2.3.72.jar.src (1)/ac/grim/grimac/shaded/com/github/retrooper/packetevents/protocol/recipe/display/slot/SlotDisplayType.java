package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.display.slot;

import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;

public interface SlotDisplayType<T extends SlotDisplay<?>> extends MappedEntity {
  T read(PacketWrapper<?> paramPacketWrapper);
  
  void write(PacketWrapper<?> paramPacketWrapper, T paramT);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\display\slot\SlotDisplayType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */