package com.gmail.olexorus.themis;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.UUID;

final class lw implements ab {
   static final lw N = new lw();
   private static final oN L = oN.Z();
   private static final ro<B, String, IOException, IOException> l;
   private static final long a = kt.a(-5373613034936471328L, -8858589475555390656L, MethodHandles.lookup().lookupClass()).a(34062191016678L);

   private lw() {
   }

   public or T(X var1) {
      long var2 = a ^ 55428874049409L;
      a(var1);
      B var4 = (B)l.P(((Aa)var1).H());
      B var5 = var4.I("tag");
      return Tb.q(v1.t(var4.r("id")), var4.Z("Count", (byte)1), var5 == B.j() ? null : Ae.m(var5, l));
   }

   public X W(or var1) {
      long var2 = a ^ 89171972692112L;
      No var4 = (No)((No)B.S().l("id", var1.H().X())).h("Count", (byte)var1.z());
      Ae var5 = var1.i();
      if (var5 != null) {
         var4.q("tag", (MC)var5.p(l));
      }

      return X.N((String)l.p(var4.w()));
   }

   public tY j(X var1, zv<X, String, ? extends RuntimeException> var2) {
      long var3 = a ^ 12248524949978L;
      a(var1);
      B var5 = (B)l.P(((Aa)var1).H());
      return Tb.U(v1.t(var5.r("type")), UUID.fromString(var5.r("id")), (X)var2.F(var5.r("name")));
   }

   public X B(tY var1, zS<X, String, ? extends RuntimeException> var2) {
      long var3 = a ^ 128997020122752L;
      No var5 = (No)((No)B.S().l("id", var1.H().toString())).l("type", var1.L().X());
      X var6 = var1.u();
      if (var6 != null) {
         var5.l("name", (String)var2.m(var6));
      }

      return X.N((String)l.p(var5.w()));
   }

   private static void a(X var0) {
      long var1 = a ^ 86343032704984L;
      if (!(var0 instanceof Aa) || !var0.C().isEmpty()) {
         throw new IllegalArgumentException("Legacy events must be single Component instances");
      }
   }

   static {
      oN var10000 = L;
      Objects.requireNonNull(var10000);
      zv var0 = var10000::G;
      oN var10001 = L;
      Objects.requireNonNull(var10001);
      l = Tb.U(var0, var10001::d);
   }
}
