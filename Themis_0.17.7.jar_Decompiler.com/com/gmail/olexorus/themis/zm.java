package com.gmail.olexorus.themis;

public final class zM {
   private final B0 j;
   private final boolean m;

   public zM(B0 var1, boolean var2) {
      this.j = var1;
      this.m = var2;
   }

   public static zM i(lm<?> var0) {
      B0 var1 = B0.u(var0);
      boolean var2 = var0.P();
      return new zM(var1, var2);
   }

   public static void C(lm<?> var0, zM var1) {
      B0.k(var0, var1.j);
      var0.I(var1.m);
   }
}
