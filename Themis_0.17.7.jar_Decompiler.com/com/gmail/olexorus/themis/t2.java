package com.gmail.olexorus.themis;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class T2 implements RF {
   private Class<?> l;
   private Class<?> L;
   private Class<?> g;
   private Field n;
   private Method e;
   private Method A;
   private Class<?> d;
   private static final long a = kt.a(-1066846130514323801L, 7364096294301792815L, MethodHandles.lookup().lookupClass()).a(122084125020587L);

   private void O() {
      long var1 = a ^ 64347036402997L;
      ClassLoader var3;
      if (this.l == null) {
         try {
            var3 = oS.J().g().getClass().getClassLoader();
            this.l = var3.loadClass("us.myles.ViaVersion.api.Via");
            this.n = this.l.getDeclaredField("manager");
            this.L = var3.loadClass("us.myles.ViaVersion.bukkit.handlers.BukkitDecodeHandler");
            this.g = var3.loadClass("us.myles.ViaVersion.bukkit.handlers.BukkitEncodeHandler");
            Class var4 = var3.loadClass("us.myles.ViaVersion.api.ViaAPI");
            this.e = this.l.getMethod("getAPI");
            this.A = var4.getMethod("getPlayerVersion", Object.class);
         } catch (NoSuchMethodException | NoSuchFieldException | ClassNotFoundException var6) {
            var6.printStackTrace();
         }
      }

      if (this.d == null) {
         try {
            var3 = oS.J().g().getClass().getClassLoader();
            this.d = var3.loadClass("us.myles.ViaVersion.api.data.UserConnection");
         } catch (ClassNotFoundException var5) {
            var5.printStackTrace();
         }
      }

   }

   public int g(Player var1) {
      this.O();

      try {
         Object var2 = this.e.invoke((Object)null);
         return (Integer)this.A.invoke(var2, var1);
      } catch (InvocationTargetException | IllegalAccessException var3) {
         var3.printStackTrace();
         return -1;
      }
   }

   public int L(Gs var1) {
      long var2 = a ^ 53659548806291L;

      try {
         if (var1.e() != null) {
            Player var4 = Bukkit.getPlayer(var1.e());
            if (var4 != null) {
               int var5 = this.g(var4);
               if (var5 != -1) {
                  return var5;
               }
            }
         }

         ChannelHandler var9 = ((Channel)var1.b()).pipeline().get("via-encoder");
         Object var10 = rB.x(var9.getClass(), "connection").get(var9);
         Object var6 = rB.x(var10.getClass(), "protocolInfo").get(var10);
         Object var7 = rB.x(var6.getClass(), "protocolVersion").get(var6);
         return var7 instanceof Integer ? (Integer)var7 : ((ProtocolVersion)var7).getVersion();
      } catch (Exception var8) {
         oS.J().m().A("Unable to grab ViaVersion client version for player!");
         return -1;
      }
   }
}
