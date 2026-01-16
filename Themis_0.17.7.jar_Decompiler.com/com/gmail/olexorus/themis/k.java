package com.gmail.olexorus.themis;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class K {
   private BukkitScheduler p;

   protected K() {
      if (!uD.J) {
         this.p = Bukkit.getScheduler();
      }

   }

   public AV m(Entity var1, Plugin var2, Consumer<Object> var3, Runnable var4, long var5) {
      if (var5 < 1L) {
         var5 = 1L;
      }

      return !uD.J ? new AV(this.p.runTaskLater(var2, K::lambda$runDelayed$2, var5)) : new AV(var1.getScheduler().runDelayed(var2, K::lambda$runDelayed$3, var4, var5));
   }

   private static void lambda$runAtFixedRate$5(Consumer var0, ScheduledTask var1) {
      var0.accept((Object)null);
   }

   private static void lambda$runAtFixedRate$4(Consumer var0) {
      var0.accept((Object)null);
   }

   private static void lambda$runDelayed$3(Consumer var0, ScheduledTask var1) {
      var0.accept((Object)null);
   }

   private static void lambda$runDelayed$2(Consumer var0) {
      var0.accept((Object)null);
   }

   private static void lambda$run$1(Consumer var0, ScheduledTask var1) {
      var0.accept((Object)null);
   }

   private static void lambda$run$0(Consumer var0) {
      var0.accept((Object)null);
   }
}
