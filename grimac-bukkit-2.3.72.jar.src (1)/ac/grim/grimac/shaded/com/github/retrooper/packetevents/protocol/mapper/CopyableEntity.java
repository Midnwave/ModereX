package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper;

import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;

public interface CopyableEntity<T extends MappedEntity> {
  T copy(@Nullable TypesBuilderData paramTypesBuilderData);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\mapper\CopyableEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */