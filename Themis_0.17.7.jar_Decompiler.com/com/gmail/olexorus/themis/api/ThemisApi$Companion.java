package com.gmail.olexorus.themis.api;

import com.gmail.olexorus.themis.MH;
import com.gmail.olexorus.themis.Themis;
import com.gmail.olexorus.themis.Ww;
import com.gmail.olexorus.themis.kt;
import com.gmail.olexorus.themis.vh;
import java.lang.invoke.MethodHandles;
import org.bukkit.entity.Player;

public final class ThemisApi$Companion {
   private static vh[] F;
   private static final long a = kt.a(-7858613248834087776L, 5441430258763154189L, MethodHandles.lookup().lookupClass()).a(138921464291633L);

   private ThemisApi$Companion() {
   }

   public final double getViolationScore(Player var1, CheckType var2) {
      long var3 = a ^ 101067661138928L;
      long var5 = var3 ^ 42548503133446L;
      long var7 = var3 ^ 95603148700753L;
      return Ww.d(new Object[]{var5, var1, var2}).Z(new Object[]{var7});
   }

   public final double getTps() {
      long var1 = a ^ 12252903818412L;
      long var3 = var1 ^ 4845660701178L;
      long var5 = var1 ^ 36558237270347L;
      return Themis.g.n(new Object[]{var3}).s(new Object[]{var5});
   }

   public final Integer getPing(Player param1) {
      // $FF: Couldn't be decompiled
   }

   public ThemisApi$Companion(MH var1) {
      this();
   }

   public static void N(vh[] var0) {
      F = var0;
   }

   public static vh[] G() {
      return F;
   }

   static {
      if (G() == null) {
         N(new vh[5]);
      }

   }

   private static RuntimeException a(RuntimeException var0) {
      return var0;
   }
}
