package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum R2 implements H {
   NONE,
   FROZEN;

   public static final tw<R2> CODEC;
   public static final l3<String, R2> ID_INDEX;
   private final String G;
   private static final R2[] L;

   private R2(String var3) {
      this.G = var3;
   }

   public String o() {
      return this.G;
   }

   public String g() {
      return this.G;
   }

   private static R2[] f() {
      return new R2[]{NONE, FROZEN};
   }

   static {
      long var0 = kt.a(-5605611984168183002L, -5093364646500113375L, MethodHandles.lookup().lookupClass()).a(169186483129557L) ^ 10489613417924L;
      NONE = new R2("NONE", 0, "none");
      FROZEN = new R2("FROZEN", 1, "frozen");
      L = f();
      CODEC = g9.c(values());
      ID_INDEX = l3.Q(R2.class, R2::o);
   }
}
