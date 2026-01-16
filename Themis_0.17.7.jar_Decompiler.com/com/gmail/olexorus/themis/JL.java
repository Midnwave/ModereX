package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public class Jl implements RI {
   private final String i;
   private final rw J;
   private final TU I;
   private final boolean Y;
   private final int V;
   private final int s;
   private final Nl[] v;
   private static final long a = kt.a(-662891165575999577L, 4020346901425303137L, MethodHandles.lookup().lookupClass()).a(54498727793702L);

   public Jl(String var1, rw var2, TU var3, boolean var4, int var5, int var6, Nl[] var7) {
      long var8 = a ^ 23990425051627L;
      super();
      if (var5 * var6 != var7.length) {
         throw new IllegalArgumentException("Illegal ingredients length, found " + var7.length + " but expected " + var5 + " * " + var6);
      } else {
         this.i = var1;
         this.J = var2;
         this.I = var3;
         this.Y = var4;
         this.V = var5;
         this.s = var6;
         this.v = var7;
      }
   }

   public static Jl c(lm<?> var0) {
      int var1 = 0;
      int var2 = 0;
      if (var0.R().m(zZ.V_1_20_3)) {
         var1 = var0.Q();
         var2 = var0.Q();
      }

      String var3 = var0.A();
      rw var4 = var0.R().i(zZ.V_1_19_3) ? (rw)var0.w((Enum[])rw.values()) : rw.MISC;
      if (var0.R().i(zZ.V_1_20_3)) {
         var1 = var0.Q();
         var2 = var0.Q();
      }

      Nl[] var5 = new Nl[var1 * var2];

      for(int var6 = 0; var6 < var5.length; ++var6) {
         var5[var6] = Nl.v(var0);
      }

      TU var8 = var0.u();
      boolean var7 = true;
      if (var0.R().i(zZ.V_1_19_4)) {
         var7 = var0.P();
      }

      return new Jl(var3, var4, var8, var7, var1, var2, var5);
   }

   public static void u(lm<?> var0, Jl var1) {
      if (var0.R().m(zZ.V_1_20_3)) {
         var0.E(var1.V);
         var0.E(var1.s);
      }

      var0.I(var1.i);
      if (var0.R().i(zZ.V_1_19_3)) {
         var0.o((Enum)var1.J);
      }

      if (var0.R().i(zZ.V_1_20_3)) {
         var0.E(var1.V);
         var0.E(var1.s);
      }

      Nl[] var2 = var1.v;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Nl var5 = var2[var4];
         Nl.F(var0, var5);
      }

      var0.m(var1.I);
      if (var0.R().i(zZ.V_1_19_4)) {
         var0.I(var1.Y);
      }

   }
}
