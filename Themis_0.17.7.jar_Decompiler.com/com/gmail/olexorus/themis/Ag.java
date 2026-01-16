package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public final class ag {
   private static final N8<T<?>> i;
   public static final T<iv> J;
   public static final T<i5> g;
   public static final T<i7> F;
   public static final T<ig> U;
   public static final T<iP> d;

   public static N8<T<?>> M() {
      return i;
   }

   public static <T extends OC> T<T> V(String var0, Ot<T> var1, ms<T> var2) {
      return (T)i.h(var0, ag::lambda$define$0);
   }

   private static i0 lambda$define$0(Ot var0, ms var1, z2 var2) {
      return new i0(var2, var0, var1);
   }

   static {
      long var0 = kt.a(5646815223063127360L, -1129925477922473723L, MethodHandles.lookup().lookupClass()).a(47517653582696L) ^ 68976785898064L;
      i = new N8("dialog_type");
      J = V("notice", iv::R, iv::N);
      g = V("server_links", i5::B, i5::p);
      F = V("dialog_list", i7::j, i7::J);
      U = V("multi_action", ig::b, ig::W);
      d = V("confirmation", iP::q, iP::G);
      i.f();
   }
}
