package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public final class Wx {
   private final float n;
   private final float t;
   private final Float P;
   private final Float T;
   private static final long a = kt.a(367878767829047341L, -3623514839928309138L, MethodHandles.lookup().lookupClass()).a(203440194258433L);

   public Wx(float var1, float var2, Float var3, Float var4) {
      long var5 = a ^ 69816716598559L;
      super();
      if (var3 != null) {
         float var7 = Math.min(var1, var2);
         float var8 = Math.max(var1, var2);
         if (var3 < var7 || var3 > var8) {
            throw new IllegalArgumentException("Initial value " + var3 + " is outside of range [" + var7 + ", " + var8 + "]");
         }
      }

      this.n = var1;
      this.t = var2;
      this.P = var3;
      this.T = var4;
   }

   public static Wx X(RT var0, lm<?> var1) {
      long var2 = a ^ 29734250083448L;
      float var4 = var0.p("start").floatValue();
      float var5 = var0.p("end").floatValue();
      mh var6 = var0.r("initial");
      Float var7 = var6 != null ? var6.b() : null;
      mh var8 = var0.r("step");
      Float var9 = var8 != null ? var8.b() : null;
      return new Wx(var4, var5, var7, var9);
   }

   public static void u(RT var0, lm<?> var1, Wx var2) {
      long var3 = a ^ 130305188188859L;
      var0.j("start", new m6(var2.n));
      var0.j("end", new m6(var2.t));
      if (var2.P != null) {
         var0.j("initial", new m6(var2.P));
      }

      if (var2.T != null) {
         var0.j("step", new m6(var2.T));
      }

   }
}
