package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class uD {
   static final boolean J;
   private static Class<? extends Event> i;
   private static K Y;

   public static K r() {
      if (Y == null) {
         Y = new K();
      }

      return Y;
   }

   public static void J(Plugin var0, Runnable var1) {
      if (!J) {
         Bukkit.getScheduler().scheduleSyncDelayedTask(var0, var1);
      } else {
         Bukkit.getServer().getPluginManager().registerEvent(i, new OJ(), EventPriority.HIGHEST, uD::lambda$runTaskOnInit$0, var0);
      }
   }

   private static void lambda$runTaskOnInit$0(Runnable var0, Listener var1, Event var2) {
      var0.run();
   }

   static {
      long var0 = kt.a(3204128749784261708L, 7253545307453146343L, MethodHandles.lookup().lookupClass()).a(158196101900488L) ^ 22242658755728L;

      boolean var2;
      try {
         Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
         var2 = true;
         i = Class.forName("io.papermc.paper.threadedregions.RegionizedServerInitEvent");
      } catch (ClassNotFoundException var4) {
         var2 = false;
      }

      J = var2;
   }
}
