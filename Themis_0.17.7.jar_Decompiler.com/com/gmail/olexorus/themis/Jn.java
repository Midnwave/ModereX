package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

class JN implements bg<t2> {
   private static final long a = kt.a(8515983152683879905L, -3458314055103337850L, MethodHandles.lookup().lookupClass()).a(213376333653769L);

   public t2 M(RT var1, lm<?> var2) {
      long var3 = a ^ 13819305411272L;
      String var5 = var2.R().i(zZ.V_1_21_11) ? "particle" : "options";
      b8 var6 = (b8)b8.p.n(var1.W(var5), var2);
      float var7 = var1.N("probability").b();
      return new t2(var6, var7);
   }

   public void l(RT var1, lm<?> var2, t2 var3) {
      long var4 = a ^ 132687504395150L;
      String var6 = var2.R().i(zZ.V_1_21_11) ? "particle" : "options";
      var1.X(var6, t2.P(var3), b8.p, var2);
      var1.j("probability", new m6(t2.j(var3)));
   }
}
