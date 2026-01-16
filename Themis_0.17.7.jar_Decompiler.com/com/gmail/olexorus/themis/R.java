package com.gmail.olexorus.themis;

public final class r {
   private final vM f;
   private final int g;
   private final int B;
   private final boolean s;

   public r(vM var1, int var2, int var3, boolean var4) {
      this.f = var1;
      this.g = var2;
      this.B = var3;
      this.s = var4;
   }

   public static r G(lm<?> var0) {
      vM var1 = ((cB)var0.y((VD)rS.k())).X();
      int var2 = var0.Q();
      int var3 = var0.Q();
      boolean var4 = var0.P();
      return new r(var1, var2, var3, var4);
   }

   public static void A(lm<?> var0, r var1) {
      var0.j((GL)var1.f.s());
      var0.E(var1.g);
      var0.E(var1.B);
      var0.I(var1.s);
   }
}
