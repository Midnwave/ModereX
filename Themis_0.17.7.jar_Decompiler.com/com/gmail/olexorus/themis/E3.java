package com.gmail.olexorus.themis;

import java.util.Arrays;
import java.util.List;

class E3 extends Eo {
   public static final <T> List<T> J(T[] var0) {
      return OA.v(var0);
   }

   public static final <T> T[] n(T[] var0, T[] var1, int var2, int var3, int var4) {
      System.arraycopy(var0, var3, var1, var2, var4 - var3);
      return var1;
   }

   // $FF: synthetic method
   public static Object[] S(Object[] var0, Object[] var1, int var2, int var3, int var4, int var5, Object var6) {
      if ((var5 & 2) != 0) {
         var2 = 0;
      }

      if ((var5 & 4) != 0) {
         var3 = 0;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.length;
      }

      return Ej.n(var0, var1, var2, var3, var4);
   }

   public static final <T> void B(T[] var0, T var1, int var2, int var3) {
      Arrays.fill(var0, var2, var3, var1);
   }
}
