package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe;

import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;

@Obsolete
public interface RecipeSerializer<T extends ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.data.RecipeData> extends MappedEntity {
  @Deprecated
  RecipeType getLegacyType();
  
  T read(PacketWrapper<?> paramPacketWrapper);
  
  void write(PacketWrapper<?> paramPacketWrapper, T paramT);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\RecipeSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */