package com.gmail.olexorus.themis;

import java.util.List;
import java.util.Set;

public final class AH {
   private final String P;
   private final String N;
   private final int b;
   private final float C;
   private final float d;
   private final String s;
   private final boolean Q;
   private final int X;
   private final List<String> i;
   private final List<String> t;
   private final List<String> r;
   private final List<String> O;
   private final Set<VC> g;
   private final Set<VC> S;

   public AH(String var1, String var2, int var3, float var4, float var5, String var6, boolean var7, int var8, List<String> var9, List<String> var10, List<String> var11, List<String> var12, Set<VC> var13, Set<VC> var14) {
      this.P = var1;
      this.N = var2;
      this.b = var3;
      this.C = var4;
      this.d = var5;
      this.s = var6;
      this.Q = var7;
      this.X = var8;
      this.i = var9;
      this.t = var10;
      this.r = var11;
      this.O = var12;
      this.g = var13;
      this.S = var14;
   }

   public static AH G(lm<?> var0) {
      String var1 = var0.A();
      String var2 = var0.A();
      int var3 = var0.f();
      float var4 = var0.L();
      float var5 = var0.L();
      String var6 = var0.A();
      boolean var7 = var0.P();
      int var8 = var0.f();
      List var9 = var0.j(lm::A);
      List var10 = var0.j(lm::A);
      List var11 = var0.j(lm::A);
      List var12 = var0.j(lm::A);
      Set var13 = var0.u(lm::M);
      Set var14 = var0.u(lm::M);
      return new AH(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14);
   }

   public static void m(lm<?> var0, AH var1) {
      var0.I(var1.P);
      var0.I(var1.N);
      var0.L(var1.b);
      var0.S(var1.C);
      var0.S(var1.d);
      var0.I(var1.s);
      var0.I(var1.Q);
      var0.L(var1.X);
      var0.D(var1.i, lm::I);
      var0.D(var1.t, lm::I);
      var0.D(var1.r, lm::I);
      var0.D(var1.O, lm::I);
      var0.P(var1.g, lm::o);
      var0.P(var1.S, lm::o);
   }
}
