package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public class oa extends oh implements wG {
   private TU O;
   private static final long a = kt.a(-1924453689217419032L, 3926844121737513484L, MethodHandles.lookup().lookupClass()).a(237797264967801L);

   public oa(TU var1) {
      this.O = var1;
   }

   public TU N() {
      return this.O;
   }

   public static oa Y(lm<?> var0) {
      return var0.R().i(zZ.V_1_13) ? new oa(var0.u()) : new oa(TU.g().s((i)z1.N().C(var0.K(), var0.Q())).j(var0).a());
   }

   public static void K(lm<?> var0, oa var1) {
      var0.m(var1.N());
   }

   public static oa K(RT var0, vL var1) {
      long var2 = a ^ 117991158010187L;
      String var4 = var1.K(vL.V_1_20_5) ? "item" : "value";
      TU var5 = TU.z(var0.W(var4), var1);
      return new oa(var5);
   }

   public static void F(oa var0, vL var1, RT var2) {
      long var3 = a ^ 49508139511523L;
      String var5 = var1.K(vL.V_1_20_5) ? "item" : "value";
      var2.j(var5, TU.r(var0.O, var1));
   }

   public boolean p() {
      return false;
   }

   public oA D(vL var1) {
      return oA.a(this.O.v().f(var1), this.O.U());
   }
}
