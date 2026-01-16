package com.gmail.olexorus.themis;

public class WM implements RI {
   private final String L;
   private final rw x;
   private final Nl[] o;
   private final TU N;

   public WM(String var1, rw var2, Nl[] var3, TU var4) {
      this.L = var1;
      this.x = var2;
      this.o = var3;
      this.N = var4;
   }

   public static WM w(lm<?> var0) {
      String var1 = var0.A();
      rw var2 = var0.R().i(zZ.V_1_19_3) ? (rw)var0.w((Enum[])rw.values()) : rw.MISC;
      Nl[] var3 = (Nl[])var0.t(Nl::v, Nl.class);
      TU var4 = var0.u();
      return new WM(var1, var2, var3, var4);
   }

   public static void H(lm<?> var0, WM var1) {
      var0.I(var1.L);
      if (var0.R().i(zZ.V_1_19_3)) {
         var0.o((Enum)var1.x);
      }

      var0.c(var1.o, Nl::F);
      var0.m(var1.N);
   }
}
