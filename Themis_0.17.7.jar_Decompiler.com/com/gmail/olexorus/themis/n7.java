package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

class N7 implements bg<BI> {
   private static final long a = kt.a(-3460140700078571829L, -7570513017523322170L, MethodHandles.lookup().lookupClass()).a(101778648662782L);

   public BI o(RT var1, lm<?> var2) {
      long var3 = a ^ 5608024687186L;
      CW var5 = (CW)var1.g("sound", CW.x, var2);
      int var6 = (Integer)var1.g("tick_delay", g9.B, var2);
      int var7 = (Integer)var1.g("block_search_extent", g9.B, var2);
      double var8 = (Double)var1.g("offset", g9.n, var2);
      return new BI(var5, var6, var7, var8);
   }

   public void X(RT var1, lm<?> var2, BI var3) {
      long var4 = a ^ 112480892373010L;
      var1.X("sound", BI.A(var3), CW.x, var2);
      var1.X("tick_delay", BI.M(var3), g9.B, var2);
      var1.X("block_search_extent", BI.Z(var3), g9.B, var2);
      var1.X("offset", BI.d(var3), g9.n, var2);
   }
}
