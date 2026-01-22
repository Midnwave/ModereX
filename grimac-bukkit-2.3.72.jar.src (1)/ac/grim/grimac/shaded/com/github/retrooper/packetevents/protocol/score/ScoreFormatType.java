package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.score;

import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.StaticMappedEntity;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;

public interface ScoreFormatType<T extends ScoreFormat> extends StaticMappedEntity {
  @Deprecated
  int getId();
  
  T read(PacketWrapper<?> paramPacketWrapper);
  
  void write(PacketWrapper<?> paramPacketWrapper, T paramT);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\score\ScoreFormatType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */