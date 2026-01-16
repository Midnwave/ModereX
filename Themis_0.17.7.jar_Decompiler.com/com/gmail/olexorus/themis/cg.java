package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public final class CG {
   private static final N8<rs<?>> F;
   public static final rs<Jt> a;
   public static final rs<RX> d;

   public static <T extends An> rs<T> O(String var0, Ot<T> var1, ms<T> var2) {
      return (rs)F.h(var0, CG::lambda$define$0);
   }

   public static N8<rs<?>> C() {
      return F;
   }

   private static iw lambda$define$0(Ot var0, ms var1, z2 var2) {
      return new iw(var2, var0, var1);
   }

   static {
      long var0 = kt.a(2226635344277803491L, -8201667905174537428L, MethodHandles.lookup().lookupClass()).a(213900960531333L) ^ 59735561385427L;
      F = new N8("dialog_body_type");
      a = O("item", Jt::P, Jt::S);
      d = O("plain_message", RX::n, RX::Y);
      F.f();
   }
}
