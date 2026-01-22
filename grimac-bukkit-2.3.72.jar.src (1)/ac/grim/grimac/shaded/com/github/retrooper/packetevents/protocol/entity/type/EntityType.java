package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type;

import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.LegacyMappedEntity;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
import java.util.Optional;

public interface EntityType extends MappedEntity, LegacyMappedEntity {
  boolean isInstanceOf(EntityType paramEntityType);
  
  Optional<EntityType> getParent();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\type\EntityType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */