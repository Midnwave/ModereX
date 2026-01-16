package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

class uG implements bg<Ed> {
   private static final long a = kt.a(-1945676291365758334L, -5162917715918275957L, MethodHandles.lookup().lookupClass()).a(237741853728491L);

   public Ed g(RT var1, lm<?> var2) {
      long var3 = a ^ 132957124162117L;
      CW var5 = (CW)var1.g("sound", CW.x, var2);
      double var6 = (Double)var1.g("tick_chance", g9.n, var2);
      return new Ed(var5, var6);
   }

   public void H(RT var1, lm<?> var2, Ed var3) {
      long var4 = a ^ 41411826074401L;
      var1.X("sound", Ed.j(var3), CW.x, var2);
      var1.X("tick_chance", Ed.c(var3), g9.n, var2);
   }
}
