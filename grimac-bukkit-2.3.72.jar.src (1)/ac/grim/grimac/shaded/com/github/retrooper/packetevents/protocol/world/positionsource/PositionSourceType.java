package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.positionsource;

import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;

public interface PositionSourceType<T extends PositionSource> extends MappedEntity {
  T read(PacketWrapper<?> paramPacketWrapper);
  
  void write(PacketWrapper<?> paramPacketWrapper, T paramT);
  
  T decode(NBTCompound paramNBTCompound, ClientVersion paramClientVersion);
  
  void encode(T paramT, ClientVersion paramClientVersion, NBTCompound paramNBTCompound);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\positionsource\PositionSourceType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */