package com.gmail.olexorus.themis;

public final class lf {
   private final VC L;
   private final On S;
   private final int t;

   public lf(VC var1, On var2, int var3) {
      this.L = var1;
      this.S = var2;
      this.t = var3;
   }

   public static lf I(lm<?> var0) {
      VC var1 = var0.M();
      On var2 = (On)var0.y((VD)RE.X());
      int var3 = var0.Q();
      return new lf(var1, var2, var3);
   }

   public static void k(lm<?> var0, lf var1) {
      var0.o(var1.L);
      var0.j((GL)var1.S);
      var0.E(var1.t);
   }
}
