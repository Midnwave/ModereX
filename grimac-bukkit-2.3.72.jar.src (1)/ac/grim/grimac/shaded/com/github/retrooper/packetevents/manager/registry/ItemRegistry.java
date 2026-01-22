package ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.registry;

import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;

public interface ItemRegistry {
  @Nullable
  ItemType getByName(String paramString);
  
  @Nullable
  ItemType getById(int paramInt);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\manager\registry\ItemRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */