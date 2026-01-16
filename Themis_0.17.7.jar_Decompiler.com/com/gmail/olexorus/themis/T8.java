package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public interface t8 {
   long a = kt.a(-3528858485492531449L, 8704407135780038313L, MethodHandles.lookup().lookupClass()).a(116625593840371L);

   static t8 N(lm<?> var0) {
      return (t8)(var0.P() ? zN.f(var0) : ol.Y(var0));
   }

   static void H(lm<?> var0, t8 var1) {
      long var2 = a ^ 121715449220173L;
      if (var1 instanceof zN) {
         var0.I(true);
         zN.c(var0, (zN)var1);
      } else {
         if (!(var1 instanceof ol)) {
            throw new IllegalArgumentException("Illegal matcher implementation: " + var1);
         }

         var0.I(false);
         ol.D(var0, (ol)var1);
      }

   }
}
