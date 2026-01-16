package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public class Nj implements R9 {
   private final String w;
   private static final long a = kt.a(7842939634212940491L, 1457382396282615886L, MethodHandles.lookup().lookupClass()).a(277049275595804L);

   public Nj(String var1) {
      this.w = var1;
   }

   public static Nj H(RT var0, lm<?> var1) {
      long var2 = a ^ 109113191716895L;
      boolean var4 = var1.R().i(zZ.V_1_21_5);
      String var5 = var0.N(var4 ? "url" : "value");
      return new Nj(var5);
   }

   public static void Z(RT var0, lm<?> var1, Nj var2) {
      long var3 = a ^ 131095896384242L;
      boolean var5 = var1.R().i(zZ.V_1_21_5);
      var0.j(var5 ? "url" : "value", new mZ(var2.w));
   }

   public Ov<?> w() {
      return vs.N;
   }
}
