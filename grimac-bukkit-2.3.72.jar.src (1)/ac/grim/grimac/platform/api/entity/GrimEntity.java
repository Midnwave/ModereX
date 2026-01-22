package ac.grim.grimac.platform.api.entity;

import ac.grim.grimac.api.GrimIdentity;
import ac.grim.grimac.platform.api.world.PlatformWorld;
import ac.grim.grimac.utils.math.Location;
import java.util.concurrent.CompletableFuture;

public interface GrimEntity extends GrimIdentity {
  boolean eject();
  
  CompletableFuture<Boolean> teleportAsync(Location paramLocation);
  
  Object getNative();
  
  boolean isDead();
  
  PlatformWorld getWorld();
  
  Location getLocation();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\api\entity\GrimEntity.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */