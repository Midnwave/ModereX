package com.gmail.olexorus.themis;

import java.util.List;

public class O6 {
   private al Z;
   private nG M;
   private List<String> f;
   private List<List<String>> j;
   private boolean z;

   public O6(al var1, nG var2, List<String> var3, List<List<String>> var4, boolean var5) {
      this.Z = var1;
      this.M = var2;
      this.f = var3;
      this.j = var4;
      this.z = var5;
   }

   public static O6 I(lm<?> var0) {
      al var1 = (al)var0.u(al::c);
      nG var2 = (nG)var0.u(nG::P);
      List var3 = var0.R().m(zZ.V_1_20_2) ? var0.j(lm::A) : null;
      List var4 = var0.j(O6::lambda$read$0);
      boolean var5 = var0.R().i(zZ.V_1_20) && var0.P();
      return new O6(var1, var2, var3, var4, var5);
   }

   public static void M(lm<?> var0, O6 var1) {
      var0.l(var1.Z, al::C);
      var0.l(var1.M, nG::T);
      if (var0.R().m(zZ.V_1_20_2)) {
         var0.D(var1.f, lm::I);
      }

      var0.D(var1.G(), O6::lambda$write$1);
      if (var0.R().i(zZ.V_1_20)) {
         var0.I(var1.z);
      }

   }

   public List<List<String>> G() {
      return this.j;
   }

   private static void lambda$write$1(lm var0, List var1) {
      var0.D(var1, lm::I);
   }

   private static List lambda$read$0(lm var0, lm var1) {
      return var0.j(lm::A);
   }
}
