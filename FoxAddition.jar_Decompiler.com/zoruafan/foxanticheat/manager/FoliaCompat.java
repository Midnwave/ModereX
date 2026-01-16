package zoruafan.foxanticheat.manager;

import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

public class FoliaCompat {
   private static boolean folia;

   static {
      try {
         Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
         folia = true;
      } catch (ClassNotFoundException var1) {
         folia = false;
      }

   }

   public static boolean isFolia() {
      return folia;
   }

   public static void runTaskAsync(Plugin plugin, Runnable run) {
      if (!folia) {
         Bukkit.getScheduler().runTaskAsynchronously(plugin, run);
      } else {
         Executors.defaultThreadFactory().newThread(run).start();
      }
   }

   public static void runTaskTimerAsync(Plugin plugin, Consumer<Object> run, long delay, long period) {
      if (!folia) {
         Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            run.accept((Object)null);
         }, delay, period);
      } else {
         try {
            Method getSchedulerMethod = Server.class.getDeclaredMethod("getGlobalRegionScheduler");
            getSchedulerMethod.setAccessible(true);
            Object globalRegionScheduler = getSchedulerMethod.invoke(Bukkit.getServer());
            Class<?> schedulerClass = globalRegionScheduler.getClass();
            Method executeMethod = schedulerClass.getDeclaredMethod("runAtFixedRate", Plugin.class, Consumer.class, Long.TYPE, Long.TYPE);
            executeMethod.setAccessible(true);
            executeMethod.invoke(globalRegionScheduler, plugin, run, delay, period);
         } catch (Exception var10) {
            var10.printStackTrace();
         }

      }
   }

   public static void runTask(Plugin plugin, Consumer<Object> run) {
      if (!folia) {
         Bukkit.getScheduler().runTask(plugin, () -> {
            run.accept((Object)null);
         });
      } else {
         try {
            Method getSchedulerMethod = Server.class.getDeclaredMethod("getGlobalRegionScheduler");
            getSchedulerMethod.setAccessible(true);
            Object globalRegionScheduler = getSchedulerMethod.invoke(Bukkit.getServer());
            Class<?> schedulerClass = globalRegionScheduler.getClass();
            Method executeMethod = schedulerClass.getDeclaredMethod("run", Plugin.class, Consumer.class);
            executeMethod.setAccessible(true);
            executeMethod.invoke(globalRegionScheduler, plugin, run);
         } catch (Exception var6) {
            var6.printStackTrace();
         }

      }
   }
}
