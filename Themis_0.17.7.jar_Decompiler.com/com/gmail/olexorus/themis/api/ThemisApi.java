package com.gmail.olexorus.themis.api;

import com.gmail.olexorus.themis.MH;
import org.bukkit.entity.Player;

public final class ThemisApi {
   public static final ThemisApi$Companion Companion;
   private static int R;

   public static final double getViolationScore(Player var0, CheckType var1) {
      return Companion.getViolationScore(var0, var1);
   }

   public static final double getTps() {
      return Companion.getTps();
   }

   public static final Integer getPing(Player var0) {
      return Companion.getPing(var0);
   }

   static {
      if (S() == 0) {
         N(63);
      }

      Companion = new ThemisApi$Companion((MH)null);
   }

   public static void N(int var0) {
      R = var0;
   }

   public static int R() {
      return R;
   }

   public static int S() {
      int var0 = R();

      try {
         return var0 == 0 ? 21 : 0;
      } catch (RuntimeException var1) {
         throw a(var1);
      }
   }

   private static RuntimeException a(RuntimeException var0) {
      return var0;
   }
}
