package com.gmail.olexorus.themis;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class JK {
   private final Plugin n;
   private final tF E;
   private static final long a = kt.a(-3849943514021314005L, 8972661504731459784L, MethodHandles.lookup().lookupClass()).a(58241719687919L);

   public JK(Plugin var1, int var2) {
      long var3 = a ^ 59870961174794L;
      super();
      this.n = var1;
      File var5 = new File(var1.getDataFolder().getParentFile(), "bStats");
      File var6 = new File(var5, "config.yml");
      YamlConfiguration var7 = YamlConfiguration.loadConfiguration(var6);
      if (!var7.isSet("serverUuid")) {
         var7.addDefault("enabled", true);
         var7.addDefault("serverUuid", UUID.randomUUID().toString());
         var7.addDefault("logFailedRequests", false);
         var7.addDefault("logSentData", false);
         var7.addDefault("logResponseStatusText", false);
         var7.options().header("bStats (https://bStats.org) collects some basic information for plugin authors, like how\nmany people use their plugin and their total player count. It's recommended to keep bStats\nenabled, but if you're not comfortable with this, you can turn this setting off. There is no\nperformance penalty associated with having metrics enabled, and data sent to bStats is fully\nanonymous.").copyDefaults(true);

         try {
            var7.save(var6);
         } catch (IOException var16) {
         }
      }

      boolean var8 = var7.getBoolean("enabled", true);
      String var9 = var7.getString("serverUuid");
      boolean var10 = var7.getBoolean("logFailedRequests", false);
      boolean var11 = var7.getBoolean("logSentData", false);
      boolean var12 = var7.getBoolean("logResponseStatusText", false);
      boolean var13 = false;

      try {
         var13 = Class.forName("io.papermc.paper.threadedregions.RegionizedServer") != null;
      } catch (Exception var15) {
      }

      Consumer var10007 = this::n;
      Consumer var10008 = this::h;
      Consumer var10009 = var13 ? null : JK::lambda$new$0;
      Objects.requireNonNull(var1);
      this.E = new tF("bukkit", var9, var2, var8, var10007, var10008, var10009, var1::isEnabled, this::lambda$new$1, this::lambda$new$2, var10, var11, var12, false);
   }

   public void S(ue var1) {
      this.E.w(var1);
   }

   private void n(t3 var1) {
      long var2 = a ^ 58557151143537L;
      var1.b("playerAmount", this.D());
      var1.b("onlineMode", Bukkit.getOnlineMode() ? 1 : 0);
      var1.X("bukkitVersion", Bukkit.getVersion());
      var1.X("bukkitName", Bukkit.getName());
      var1.X("javaVersion", System.getProperty("java.version"));
      var1.X("osName", System.getProperty("os.name"));
      var1.X("osArch", System.getProperty("os.arch"));
      var1.X("osVersion", System.getProperty("os.version"));
      var1.b("coreCount", Runtime.getRuntime().availableProcessors());
   }

   private void h(t3 var1) {
      long var2 = a ^ 60564725837584L;
      var1.X("pluginVersion", this.n.getDescription().getVersion());
   }

   private int D() {
      long var1 = a ^ 134822629033003L;

      try {
         Method var3 = Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers");
         return var3.getReturnType().equals(Collection.class) ? ((Collection)var3.invoke(Bukkit.getServer())).size() : ((Player[])var3.invoke(Bukkit.getServer())).length;
      } catch (Exception var4) {
         return Bukkit.getOnlinePlayers().size();
      }
   }

   private void lambda$new$2(String var1) {
      this.n.getLogger().log(Level.INFO, var1);
   }

   private void lambda$new$1(String var1, Throwable var2) {
      this.n.getLogger().log(Level.WARNING, var1, var2);
   }

   private static void lambda$new$0(Plugin var0, Runnable var1) {
      Bukkit.getScheduler().runTask(var0, var1);
   }
}
