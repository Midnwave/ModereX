package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper;

import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Internal
public interface ResolvableEntity {
  void doResolve(PacketWrapper<?> paramPacketWrapper);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\mapper\ResolvableEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */