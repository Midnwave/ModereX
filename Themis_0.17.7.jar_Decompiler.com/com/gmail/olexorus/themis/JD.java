package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public final class Jd {
   private static final N8<Wb> Q;
   public static final Wb E;
   public static final Wb D;
   public static final Wb G;
   private static final long a = kt.a(3970044933974175014L, 1372049737119208939L, MethodHandles.lookup().lookupClass()).a(100131854869856L);

   public static Wb l(String var0, rt var1, String var2) {
      long var3 = a ^ 118706827408353L;
      al var5 = new al("entity/chicken/" + var2);
      return (Wb)Q.h(var0, Jd::lambda$define$0);
   }

   public static N8<Wb> W() {
      return Q;
   }

   private static i9 lambda$define$0(rt var0, al var1, z2 var2) {
      return new i9(var2, var0, var1);
   }

   static {
      long var0 = a ^ 117493893852392L;
      Q = new N8("chicken_variant");
      E = l("cold", rt.COLD, "cold_chicken");
      D = l("temperate", rt.NORMAL, "temperate_chicken");
      G = l("warm", rt.NORMAL, "warm_chicken");
      Q.f();
   }
}
