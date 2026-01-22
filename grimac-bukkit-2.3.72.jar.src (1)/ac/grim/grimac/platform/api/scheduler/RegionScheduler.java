package ac.grim.grimac.platform.api.scheduler;

import ac.grim.grimac.api.plugin.GrimPlugin;
import ac.grim.grimac.platform.api.world.PlatformWorld;
import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
import ac.grim.grimac.utils.math.Location;

public interface RegionScheduler {
  void execute(@NotNull GrimPlugin paramGrimPlugin, @NotNull PlatformWorld paramPlatformWorld, int paramInt1, int paramInt2, @NotNull Runnable paramRunnable);
  
  void execute(@NotNull GrimPlugin paramGrimPlugin, @NotNull Location paramLocation, @NotNull Runnable paramRunnable);
  
  TaskHandle run(@NotNull GrimPlugin paramGrimPlugin, @NotNull PlatformWorld paramPlatformWorld, int paramInt1, int paramInt2, @NotNull Runnable paramRunnable);
  
  TaskHandle run(@NotNull GrimPlugin paramGrimPlugin, @NotNull Location paramLocation, @NotNull Runnable paramRunnable);
  
  TaskHandle runDelayed(@NotNull GrimPlugin paramGrimPlugin, @NotNull PlatformWorld paramPlatformWorld, int paramInt1, int paramInt2, @NotNull Runnable paramRunnable, long paramLong);
  
  TaskHandle runDelayed(@NotNull GrimPlugin paramGrimPlugin, @NotNull Location paramLocation, @NotNull Runnable paramRunnable, long paramLong);
  
  TaskHandle runAtFixedRate(@NotNull GrimPlugin paramGrimPlugin, @NotNull PlatformWorld paramPlatformWorld, int paramInt1, int paramInt2, @NotNull Runnable paramRunnable, long paramLong1, long paramLong2);
  
  TaskHandle runAtFixedRate(@NotNull GrimPlugin paramGrimPlugin, @NotNull Location paramLocation, @NotNull Runnable paramRunnable, long paramLong1, long paramLong2);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\api\scheduler\RegionScheduler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */