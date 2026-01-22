package ac.grim.grimac.platform.api.scheduler;

import ac.grim.grimac.api.plugin.GrimPlugin;
import ac.grim.grimac.platform.api.entity.GrimEntity;
import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;

public interface EntityScheduler {
  void execute(@NotNull GrimEntity paramGrimEntity, @NotNull GrimPlugin paramGrimPlugin, @NotNull Runnable paramRunnable1, @Nullable Runnable paramRunnable2, long paramLong);
  
  TaskHandle run(@NotNull GrimEntity paramGrimEntity, @NotNull GrimPlugin paramGrimPlugin, @NotNull Runnable paramRunnable1, @Nullable Runnable paramRunnable2);
  
  TaskHandle runDelayed(@NotNull GrimEntity paramGrimEntity, @NotNull GrimPlugin paramGrimPlugin, @NotNull Runnable paramRunnable1, @Nullable Runnable paramRunnable2, long paramLong);
  
  TaskHandle runAtFixedRate(@NotNull GrimEntity paramGrimEntity, @NotNull GrimPlugin paramGrimPlugin, @NotNull Runnable paramRunnable1, @Nullable Runnable paramRunnable2, long paramLong1, long paramLong2);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\api\scheduler\EntityScheduler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */