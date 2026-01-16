package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Set;

public final class v9 {
   private static final Set<WH> Q = Je.R(WH.class);
   private static final long a = kt.a(-1589305125829521464L, -4328883034780728185L, MethodHandles.lookup().lookupClass()).a(255222253110266L);

   public static <O extends Cl> O D(Class<O> var0, v1 var1, Cl var2) {
      long var3 = a ^ 28350399443474L;
      if (var0.isInstance(var2)) {
         return (Cl)var0.cast(var2);
      } else {
         t0 var5 = TB.s(var2.getClass(), var0);
         if (var5 == null) {
            throw new IllegalArgumentException("There is no data holder converter registered to convert from a " + var2.getClass() + " instance to a " + var0 + " (on field " + var1 + ")");
         } else {
            try {
               return (Cl)var5.H.v(var1, var2);
            } catch (Exception var7) {
               throw new IllegalStateException("Failed to convert data component value of type " + var2.getClass() + " to type " + var0 + " due to an error in a converter provided by " + var5.A.X() + "!", var7);
            }
         }
      }
   }

   static Set S() {
      return Q;
   }
}
