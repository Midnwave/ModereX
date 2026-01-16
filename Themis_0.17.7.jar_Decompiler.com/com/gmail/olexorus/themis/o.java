package com.gmail.olexorus.themis;

public class O {
   private final int W;
   private final int y;
   private final boolean u;
   private final boolean b;
   private final boolean m;
   private final O j;

   public O(int var1, int var2, boolean var3, boolean var4, boolean var5, O var6) {
      this.W = var1;
      this.y = var2;
      this.u = var3;
      this.b = var4;
      this.m = var5;
      this.j = var6;
   }

   public static O A(lm<?> var0) {
      int var1 = var0.Q();
      int var2 = var0.Q();
      boolean var3 = var0.P();
      boolean var4 = var0.P();
      boolean var5 = var0.P();
      O var6 = (O)var0.u(O::A);
      return new O(var1, var2, var3, var4, var5, var6);
   }

   public static void A(lm<?> var0, O var1) {
      var0.E(var1.W);
      var0.E(var1.y);
      var0.I(var1.u);
      var0.I(var1.b);
      var0.I(var1.m);
      var0.l(var1.j, O::A);
   }
}
