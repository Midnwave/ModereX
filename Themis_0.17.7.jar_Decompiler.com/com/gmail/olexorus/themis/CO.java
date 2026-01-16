package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class co {
   private static final N8<Wj<?>> o;
   public static final Wj<Boolean> O;
   public static final Wj<Om> T;
   public static final Wj<Float> w;
   public static final Wj<Float> I;
   public static final Wj<v2> j;
   public static final Wj<vc> Y;
   public static final Wj<be> E;
   public static final Wj<?> S;
   public static final Wj<?> c;
   public static final Wj<b8<?>> C;
   public static final Wj<List<t2>> k;
   public static final Wj<aN> N;
   public static final Wj<bi> b;

   public static <T> Wj<T> j(String var0) {
      return k(var0, (tw)null, Collections.emptyMap());
   }

   public static <T> Wj<T> T(String var0, tw<T> var1) {
      return k(var0, var1, Collections.emptyMap());
   }

   public static <T> Wj<T> k(String var0, tw<T> var1, Map<bC, bA<T, ?>> var2) {
      return (Wj)o.h(var0, co::lambda$define$0);
   }

   public static <T> tw<bA<T, ?>> z(Map<bC, bA<T, ?>> var0) {
      HashMap var1 = new HashMap(var0.size() + 1);
      var1.put(bC.OVERRIDE, bA.v());
      var1.putAll(var0);
      HashMap var2 = new HashMap(var0.size() + 1);
      Iterator var3 = var1.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         var2.put((bA)var4.getValue(), (bC)var4.getKey());
      }

      return new t4(var1, var0, var2);
   }

   private static cC lambda$define$0(tw var0, Map var1, z2 var2) {
      return new cC(var2, var0, z(var1));
   }

   static {
      long var0 = kt.a(3654000112654235727L, -8077809646551088629L, MethodHandles.lookup().lookupClass()).a(180860877184426L) ^ 7478372273714L;
      o = new N8("attribute_type");
      O = k("boolean", g9.k, bA.e);
      T = j("tri_state");
      w = k("float", g9.x, bA.Z);
      I = k("angle_degrees", g9.x, bA.Z);
      j = k("rgb_color", g9.j, bA.u);
      Y = k("argb_color", g9.r, bA.l);
      E = T("moon_phase", be.CODEC);
      S = j("activity");
      c = j("bed_rule");
      C = T("particle", b8.p);
      k = T("ambient_particles", t2.b.c());
      N = T("background_music", aN.m);
      b = T("ambient_sounds", bi.X);
      o.f();
   }
}
