package com.gmail.olexorus.themis;

import java.util.List;

public class WC extends WZ<WC> {
   private final List<zR> K;
   private final float l;

   public WC(List<zR> var1, float var2) {
      super(gi.l);
      this.K = var1;
      this.l = var2;
   }

   public static WC f(lm<?> var0) {
      List var1 = var0.j(zR::r);
      float var2 = var0.L();
      return new WC(var1, var2);
   }

   public static void T(lm<?> var0, WC var1) {
      var0.D(var1.K, zR::g);
      var0.S(var1.l);
   }
}
