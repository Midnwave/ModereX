package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

class Wo extends WF {
   private static final long a = kt.a(-447481607145953048L, 5440871085818211150L, MethodHandles.lookup().lookupClass()).a(204680526577392L);

   public static final G0 Y(int var0, int var1) {
      return G0.R.G(var0, var1, -1);
   }

   public static final GK d(int var0, int var1) {
      return var1 <= Integer.MIN_VALUE ? GK.F.u() : new GK(var0, var1 - 1);
   }

   public static final int u(int var0, int var1) {
      return var0 < var1 ? var1 : var0;
   }

   public static final int e(int var0, int var1) {
      return var0 > var1 ? var1 : var0;
   }

   public static final int S(int var0, int var1, int var2) {
      long var3 = a ^ 10825233921054L;
      if (var1 > var2) {
         throw new IllegalArgumentException("Cannot coerce value to an empty range: maximum " + var2 + " is less than minimum " + var1 + '.');
      } else if (var0 < var1) {
         return var1;
      } else {
         return var0 > var2 ? var2 : var0;
      }
   }
}
