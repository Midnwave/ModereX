package ac.grim.grimac.platform.api.player;

import java.util.Collection;
import java.util.UUID;

public interface PlatformPlayerFactory {
  OfflinePlatformPlayer getOfflineFromUUID(UUID paramUUID);
  
  OfflinePlatformPlayer getOfflineFromName(String paramString);
  
  PlatformPlayer getFromName(String paramString);
  
  PlatformPlayer getFromUUID(UUID paramUUID);
  
  PlatformPlayer getFromNativePlayerType(Object paramObject);
  
  void invalidatePlayer(UUID paramUUID);
  
  Collection<PlatformPlayer> getOnlinePlayers();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\api\player\PlatformPlayerFactory.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */