package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

class gF implements bg<CO<T>> {
   final tw y;
   private static final long a = kt.a(1612620813178772687L, -3565766423377551645L, MethodHandles.lookup().lookupClass()).a(216599074846593L);

   gF(tw var1) {
      this.y = var1;
   }

   public CO<T> u(RT var1, lm<?> var2) {
      long var3 = a ^ 40277189282752L;
      int var5 = var1.N("ticks").P();
      Object var6 = var1.g("value", this.y, var2);
      return new CO(var5, var6);
   }

   public void B(RT var1, lm<?> var2, CO<T> var3) {
      long var4 = a ^ 43417674978332L;
      var1.j("ticks", new mz(CO.G(var3)));
      var1.X("value", CO.l(var3), this.y, var2);
   }
}
