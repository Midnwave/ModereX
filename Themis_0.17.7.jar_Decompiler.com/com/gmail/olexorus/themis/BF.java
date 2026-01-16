package com.gmail.olexorus.themis;

import com.gmail.olexorus.themis.api.CheckType;

// $FF: synthetic class
public final class Bf {
   public static final O4<CheckType> G;
   private static boolean L;

   static {
      if (!J()) {
         s(true);
      }

      G = Cz.x((Enum[])CheckType.values());
   }

   public static void s(boolean var0) {
      L = var0;
   }

   public static boolean J() {
      return L;
   }

   public static boolean V() {
      boolean var0 = J();

      try {
         return !var0;
      } catch (RuntimeException var1) {
         throw a(var1);
      }
   }

   private static RuntimeException a(RuntimeException var0) {
      return var0;
   }
}
