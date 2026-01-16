package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum wk implements ou, CA {
   REQUEST,
   PING;

   private final int l;
   private final Class<? extends lm<?>> s;
   private static final wk[] d;

   private wk(int var3, Class<? extends lm<?>> var4) {
      this.l = var3;
      this.s = var4;
   }

   public static wC g(int var0) {
      if (var0 == 0) {
         return REQUEST;
      } else {
         return var0 == 1 ? PING : null;
      }
   }

   public int d() {
      return this.l;
   }

   public RW R() {
      return RW.CLIENT;
   }

   private static wk[] f() {
      return new wk[]{REQUEST, PING};
   }

   static {
      long var0 = kt.a(8339604241265105768L, 1738951318798683939L, MethodHandles.lookup().lookupClass()).a(231185595220448L) ^ 14096204118752L;
      REQUEST = new wk("REQUEST", 0, 0, aJ.class);
      PING = new wk("PING", 1, 1, ad.class);
      d = f();
   }
}
