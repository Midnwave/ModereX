package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum w_ implements ou, gh {
   LEGACY_SERVER_LIST_RESPONSE;

   private final int A;
   private final Class<? extends lm<?>> c;
   private static final w_[] x;

   private w_(int var3, Class<? extends lm<?>> var4) {
      this.A = var3;
      this.c = var4;
   }

   public static wC g(int var0) {
      return var0 == 254 ? LEGACY_SERVER_LIST_RESPONSE : null;
   }

   public int d() {
      return this.A;
   }

   public RW R() {
      return RW.SERVER;
   }

   private static w_[] K() {
      return new w_[]{LEGACY_SERVER_LIST_RESPONSE};
   }

   static {
      long var0 = kt.a(-8245677831763035518L, -4135255906860870326L, MethodHandles.lookup().lookupClass()).a(158382247537606L) ^ 96295239198771L;
      LEGACY_SERVER_LIST_RESPONSE = new w_("LEGACY_SERVER_LIST_RESPONSE", 0, 254, (Class)null);
      x = K();
   }
}
