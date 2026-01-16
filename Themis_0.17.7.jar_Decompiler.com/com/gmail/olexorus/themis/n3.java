package com.gmail.olexorus.themis;

import java.util.List;
import java.util.Set;

public final class N3 {
   private final boolean B;
   private final int k;
   private final VC q;
   private final List<Vz> A;
   private final Set<Vz> K;
   private final List<Vz> p;
   private final List<Vz> u;

   public N3(boolean var1, int var2, VC var3, List<Vz> var4, Set<Vz> var5, List<Vz> var6, List<Vz> var7) {
      this.B = var1;
      this.k = var2;
      this.q = var3;
      this.A = var4;
      this.K = var5;
      this.p = var6;
      this.u = var7;
   }

   public static N3 F(lm<?> var0) {
      boolean var1 = var0.P();
      int var2 = var0.f();
      VC var3 = var0.M();
      List var4 = var0.j(Vz::I);
      Set var5 = var0.u(Vz::I);
      List var6 = var0.j(Vz::I);
      List var7 = var0.j(Vz::I);
      return new N3(var1, var2, var3, var4, var5, var6, var7);
   }

   public static void v(lm<?> var0, N3 var1) {
      var0.I(var1.B);
      var0.L(var1.k);
      var0.o(var1.q);
      var0.D(var1.A, Vz::H);
      var0.P(var1.K, Vz::H);
      var0.D(var1.p, Vz::H);
      var0.D(var1.u, Vz::H);
   }
}
