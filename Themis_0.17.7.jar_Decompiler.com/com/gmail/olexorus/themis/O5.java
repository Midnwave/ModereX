package com.gmail.olexorus.themis;

public class o5 implements RI {
   private final String w;
   private final Nl u;
   private final TU A;

   public o5(String var1, Nl var2, TU var3) {
      this.w = var1;
      this.u = var2;
      this.A = var3;
   }

   public static o5 u(lm<?> var0) {
      String var1 = var0.A();
      Nl var2 = Nl.v(var0);
      TU var3 = var0.u();
      return new o5(var1, var2, var3);
   }

   public static void Q(lm<?> var0, o5 var1) {
      var0.I(var1.w);
      Nl.F(var0, var1.u);
      var0.m(var1.A);
   }
}
