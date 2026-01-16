package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public final class vf {
   private static final N8<BB> N;
   public static final BB F;
   public static final BB L;
   public static final BB k;
   public static final BB S;
   public static final BB G;
   public static final BB O;
   public static final BB u;
   public static final BB h;
   public static final BB d;
   public static final BB Y;
   public static final BB b;
   public static final BB c;
   public static final BB v;
   public static final BB a;
   public static final BB o;
   public static final BB T;
   public static final BB H;
   public static final BB A;
   private static final long e = kt.a(7512934909898714534L, -6976114773982341973L, MethodHandles.lookup().lookupClass()).a(206623410940729L);

   public static BB F(String var0) {
      long var1 = e ^ 22275744055530L;
      al var3 = al.z(var0);
      i var4 = z1.x(var3 + "_armor_trim_smithing_template");
      GY var5 = X.D("trim_pattern.minecraft." + var0);
      boolean var6 = false;
      return b(var0, var3, var4, var5, var6);
   }

   public static BB b(String var0, al var1, i var2, X var3, boolean var4) {
      return (BB)N.h(var0, vf::lambda$define$0);
   }

   public static N8<BB> z() {
      return N;
   }

   private static cW lambda$define$0(al var0, i var1, X var2, boolean var3, z2 var4) {
      return new cW(var4, var0, var1, var2, var3);
   }

   static {
      long var0 = e ^ 2333838879232L;
      N = new N8("trim_pattern");
      F = F("coast");
      L = F("dune");
      k = F("eye");
      S = F("rib");
      G = F("sentry");
      O = F("snout");
      u = F("spire");
      h = F("tide");
      d = F("vex");
      Y = F("ward");
      b = F("wild");
      c = F("raiser");
      v = F("host");
      a = F("silence");
      o = F("shaper");
      T = F("wayfinder");
      H = F("bolt");
      A = F("flow");
      N.f();
   }
}
