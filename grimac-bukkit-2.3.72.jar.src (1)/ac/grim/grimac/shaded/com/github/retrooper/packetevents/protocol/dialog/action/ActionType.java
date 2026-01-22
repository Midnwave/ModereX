package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.action;

import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface ActionType<T extends Action> extends MappedEntity {
  T decode(NBTCompound paramNBTCompound, PacketWrapper<?> paramPacketWrapper);
  
  void encode(NBTCompound paramNBTCompound, PacketWrapper<?> paramPacketWrapper, T paramT);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\dialog\action\ActionType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */