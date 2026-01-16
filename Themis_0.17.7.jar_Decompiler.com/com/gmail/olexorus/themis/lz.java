package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import org.bukkit.Bukkit;

public class lZ {
   private static AX m;
   private static RF c;
   private static final long a = kt.a(-1237099788019158842L, -4615556536205717723L, MethodHandles.lookup().lookupClass()).a(219332711840509L);

   private static void j() {
      long var0 = a ^ 104383531799457L;
      if (c == null) {
         ClassLoader var2 = oS.J().g().getClass().getClassLoader();

         try {
            var2.loadClass("com.viaversion.viaversion.api.Via");
            c = new Mt();
         } catch (Exception var6) {
            try {
               var2.loadClass("us.myles.ViaVersion.api.Via");
               c = new T2();
            } catch (ClassNotFoundException var5) {
               c = null;
            }
         }
      }

   }

   public static void X() {
      long var0 = a ^ 63145423367410L;
      boolean var2 = Bukkit.getPluginManager().isPluginEnabled("ViaVersion");
      m = var2 ? AX.ENABLED : AX.DISABLED;
   }

   public static boolean b() {
      if (m == AX.UNKNOWN) {
         return d() != null;
      } else {
         return m == AX.ENABLED;
      }
   }

   public static RF d() {
      j();
      return c;
   }

   public static int o(Gs var0) {
      return d().L(var0);
   }

   static {
      m = AX.UNKNOWN;
   }
}
