package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public class Og implements Bi {
   private final int q;
   private final X j;
   private final boolean c;
   private final String s;
   private final int N;
   private final oo o;
   private static final long a = kt.a(-7267004883841637502L, -4608601988686597302L, MethodHandles.lookup().lookupClass()).a(25807050122304L);

   public Og(int var1, X var2, boolean var3, String var4, int var5, oo var6) {
      long var7 = a ^ 51987604889667L;
      super();
      if (var4.length() > var5) {
         throw new IllegalArgumentException("Default text length exceeds allowed size");
      } else {
         this.q = var1;
         this.j = var2;
         this.c = var3;
         this.s = var4;
         this.N = var5;
         this.o = var6;
      }
   }

   public static Og n(RT var0, lm<?> var1) {
      long var2 = a ^ 88137103136935L;
      int var4 = var0.L("width", 200).intValue();
      X var5 = (X)var0.g("label", h.z(var1), var1);
      boolean var6 = var0.v("label_visible", true);
      String var7 = var0.g("initial", "");
      int var8 = var0.L("max_length", 32).intValue();
      oo var9 = (oo)var0.M("multiline", oo::v, var1);
      return new Og(var4, var5, var6, var7, var8, var9);
   }

   public static void c(RT var0, lm<?> var1, Og var2) {
      long var3 = a ^ 29304456060724L;
      if (var2.q != 200) {
         var0.j("width", new mz(var2.q));
      }

      var0.X("label", var2.j, h.z(var1), var1);
      if (!var2.c) {
         var0.j("label_visible", new mA(false));
      }

      if (!var2.s.isEmpty()) {
         var0.j("initial", new mZ(var2.s));
      }

      if (var2.N != 32) {
         var0.j("max_length", new mz(var2.N));
      }

      if (var2.o != null) {
         var0.X("multiline", var2.o, oo::Z, var1);
      }

   }

   public wE<?> Q() {
      return E6.C;
   }
}
