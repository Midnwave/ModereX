package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public final class bj<V> {
   public static final bj<X> C;
   public static final bj<or> b;
   public static final bj<tY> P;
   public static final bj<String> A;
   public static final l3<String, bj<?>> T;
   private final String W;
   private final Class<V> e;
   private final boolean Q;
   private final AY<V> B;

   bj(String var1, Class<V> var2, boolean var3, AY<V> var4) {
      this.W = var1;
      this.e = var2;
      this.Q = var3;
      this.B = var4;
   }

   public Class<V> N() {
      return this.e;
   }

   public boolean A() {
      return this.Q;
   }

   public String toString() {
      return this.W;
   }

   private static String lambda$static$0(bj var0) {
      return var0.W;
   }

   static AY b(bj var0) {
      return var0.B;
   }

   static {
      long var0 = kt.a(-2264673968969834592L, 2395459697578096201L, MethodHandles.lookup().lookupClass()).a(175805509670245L) ^ 103631898442116L;
      C = new bj("show_text", X.class, true, new Gc());
      b = new bj("show_item", or.class, true, new mv());
      P = new bj("show_entity", tY.class, true, new ts());
      A = new bj("show_achievement", String.class, true, new as());
      T = l3.f(bj::lambda$static$0, C, b, P, A);
   }
}
