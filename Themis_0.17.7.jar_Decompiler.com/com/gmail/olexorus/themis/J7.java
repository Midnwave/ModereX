package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public final class J7 {
   private static final N8<n> v;
   public static final n F;
   public static final n y;
   public static final n e;
   private static final long a = kt.a(-1479108544420856632L, 7650283583212579974L, MethodHandles.lookup().lookupClass()).a(270707536959490L);

   public static n O(String var0, tb var1, String var2) {
      long var3 = a ^ 19127732711378L;
      al var5 = new al("entity/pig/" + var2);
      return (n)v.h(var0, J7::lambda$define$0);
   }

   public static N8<n> S() {
      return v;
   }

   private static ir lambda$define$0(tb var0, al var1, z2 var2) {
      return new ir(var2, var0, var1);
   }

   static {
      long var0 = a ^ 28102833443758L;
      v = new N8("pig_variant");
      F = O("cold", tb.COLD, "cold_pig");
      y = O("temperate", tb.NORMAL, "temperate_pig");
      e = O("warm", tb.NORMAL, "warm_pig");
      v.f();
   }
}
