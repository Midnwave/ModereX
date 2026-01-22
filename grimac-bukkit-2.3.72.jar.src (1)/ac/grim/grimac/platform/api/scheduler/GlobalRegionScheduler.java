package ac.grim.grimac.platform.api.scheduler;

import ac.grim.grimac.api.plugin.GrimPlugin;
import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;

public interface GlobalRegionScheduler {
  void execute(@NotNull GrimPlugin paramGrimPlugin, @NotNull Runnable paramRunnable);
  
  TaskHandle run(@NotNull GrimPlugin paramGrimPlugin, @NotNull Runnable paramRunnable);
  
  TaskHandle runDelayed(@NotNull GrimPlugin paramGrimPlugin, @NotNull Runnable paramRunnable, long paramLong);
  
  TaskHandle runAtFixedRate(@NotNull GrimPlugin paramGrimPlugin, @NotNull Runnable paramRunnable, long paramLong1, long paramLong2);
  
  void cancel(@NotNull GrimPlugin paramGrimPlugin);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\api\scheduler\GlobalRegionScheduler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */