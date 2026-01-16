package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public final class W4 {
   private static final N8<OU<?>> A;
   public static final OU<gm> s;
   public static final OU<g8> f;

   public static <T extends gn> OU<T> n(String var0, MO<T> var1, Gw<T> var2, VQ<T> var3, lj<T> var4) {
      return (OU)A.h(var0, W4::lambda$define$0);
   }

   public static OU<?> C(String var0) {
      return (OU)A.R(var0);
   }

   public static OU<?> n(vL var0, int var1) {
      return (OU)A.e(var0, var1);
   }

   private static cX lambda$define$0(MO var0, Gw var1, VQ var2, lj var3, z2 var4) {
      return new cX(var4, var0, var1, var2, var3);
   }

   static {
      long var0 = kt.a(-735339576673357399L, -27255905946779803L, MethodHandles.lookup().lookupClass()).a(216016200842429L) ^ 22665856322852L;
      A = new N8("position_source_type");
      s = n("block", gm::q, gm::p, gm::j, gm::a);
      f = n("entity", g8::f, g8::G, g8::x, g8::g);
      A.f();
   }
}
