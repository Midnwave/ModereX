package com.gmail.olexorus.themis;

public class W8 {
   private al q;
   private VC v;
   private bZ f;
   private boolean t;
   private Ge I;
   private X A;

   public W8(al var1, VC var2, bZ var3, boolean var4, Ge var5, X var6) {
      this.q = var1;
      this.v = var2;
      this.f = var3;
      this.t = var4;
      this.I = var5;
      this.A = var6;
   }

   public static W8 P(lm<?> var0) {
      al var1 = (al)var0.u(al::c);
      VC var2 = VC.T(var0);
      bZ var3 = (bZ)var0.i(bZ.class);
      boolean var4 = var0.P();
      Ge var5 = (Ge)var0.i(Ge.class);
      X var6 = (X)var0.u(lm::a);
      return new W8(var1, var2, var3, var4, var5, var6);
   }

   public static void G(lm<?> var0, W8 var1) {
      var0.l(var1.q, al::C);
      VC.M(var0, var1.v);
      var0.o((Enum)var1.f);
      var0.I(var1.t);
      var0.o((Enum)var1.I);
      var0.l(var1.A, lm::G);
   }
}
