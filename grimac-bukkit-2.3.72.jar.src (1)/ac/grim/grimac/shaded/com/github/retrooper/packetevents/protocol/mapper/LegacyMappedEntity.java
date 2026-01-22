package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper;

import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;

public interface LegacyMappedEntity {
  ResourceLocation getName();
  
  int getLegacyId(ClientVersion paramClientVersion);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\mapper\LegacyMappedEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */