package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util;

import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.jspecify.annotations.NullMarked;

@NullMarked
@FunctionalInterface
public interface NbtDecoder<T> {
  T decode(NBT paramNBT, PacketWrapper<?> paramPacketWrapper);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protoco\\util\NbtDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */