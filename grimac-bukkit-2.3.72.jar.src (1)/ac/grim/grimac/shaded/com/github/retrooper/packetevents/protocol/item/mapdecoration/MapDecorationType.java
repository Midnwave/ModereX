package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.mapdecoration;

import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;

public interface MapDecorationType extends MappedEntity {
  ResourceLocation getAssetId();
  
  boolean isShowOnItemFrame();
  
  int getMapColor();
  
  boolean isExplorationMapElement();
  
  boolean isTrackCount();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\mapdecoration\MapDecorationType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */