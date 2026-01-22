package ac.grim.grimac.platform.api.scheduler;

import ac.grim.grimac.api.plugin.GrimPlugin;
import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
import java.util.concurrent.TimeUnit;

public interface AsyncScheduler {
  TaskHandle runNow(@NotNull GrimPlugin paramGrimPlugin, @NotNull Runnable paramRunnable);
  
  TaskHandle runDelayed(@NotNull GrimPlugin paramGrimPlugin, @NotNull Runnable paramRunnable, long paramLong, @NotNull TimeUnit paramTimeUnit);
  
  TaskHandle runAtFixedRate(@NotNull GrimPlugin paramGrimPlugin, @NotNull Runnable paramRunnable, long paramLong1, long paramLong2, @NotNull TimeUnit paramTimeUnit);
  
  TaskHandle runAtFixedRate(@NotNull GrimPlugin paramGrimPlugin, @NotNull Runnable paramRunnable, long paramLong1, long paramLong2);
  
  void cancel(@NotNull GrimPlugin paramGrimPlugin);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\api\scheduler\AsyncScheduler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */