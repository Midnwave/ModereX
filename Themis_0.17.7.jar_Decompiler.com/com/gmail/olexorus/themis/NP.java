package com.gmail.olexorus.themis;

public class nP {
   private gL s;
   private byte B;
   private byte f;
   private byte G;
   private X n;

   public nP(gL var1, byte var2, byte var3, byte var4, X var5) {
      this.s = var1;
      this.B = var2;
      this.f = var3;
      this.G = var4;
      this.n = var5;
   }

   public static nP o(lm<?> var0) {
      boolean var1 = var0.R().i(zZ.V_1_13);
      byte var2 = var1 ? 0 : var0.M();
      gL var3 = !var1 ? WA.K(var0.R().u(), var2 >> 4 & 15) : (gL)var0.e(WA::K);
      byte var4 = var0.M();
      byte var5 = var0.M();
      byte var6 = (byte)((var1 ? var0.M() : var2) & 15);
      X var7 = var1 ? (X)var0.u(lm::a) : null;
      return new nP(var3, var4, var5, var6, var7);
   }

   public static void w(lm<?> var0, nP var1) {
      boolean var2 = var0.R().i(zZ.V_1_13);
      if (var2) {
         var0.j((GL)var1.m());
      } else {
         int var3 = var1.m().f(var0.R().u());
         var0.u((var3 & 15) << 4 | var1.s() & 15);
      }

      var0.u(var1.n());
      var0.u(var1.h());
      if (var2) {
         var0.u(var1.s());
         var0.l(var1.V(), lm::G);
      }

   }

   public gL m() {
      return this.s;
   }

   public byte n() {
      return this.B;
   }

   public byte h() {
      return this.f;
   }

   public byte s() {
      return this.G;
   }

   public X V() {
      return this.n;
   }
}
