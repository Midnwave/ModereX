package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component;

import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
import java.util.function.Function;

public interface ComponentType<T> extends MappedEntity {
  T read(PacketWrapper<?> paramPacketWrapper);
  
  void write(PacketWrapper<?> paramPacketWrapper, T paramT);
  
  T decode(NBT paramNBT, ClientVersion paramClientVersion);
  
  NBT encode(T paramT, ClientVersion paramClientVersion);
  
  @Internal
  <Z> ComponentType<Z> legacyMap(Function<T, Z> paramFunction, Function<Z, T> paramFunction1);
  
  public static interface Encoder<T> {
    NBT encode(T param1T, ClientVersion param1ClientVersion);
  }
  
  public static interface Decoder<T> {
    T decode(NBT param1NBT, ClientVersion param1ClientVersion);
  }
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\ComponentType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */