package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;

public final class Bg {
   private static final N8<Jf<?>> P;
   public static final tw<Jf<?>> W;
   public static Jf<v2> Z;
   public static Jf<Float> o;
   public static Jf<Float> x;
   public static Jf<Float> v;
   public static Jf<Float> U;
   public static Jf<v2> Q;
   public static Jf<Float> z;
   public static Jf<Float> M;
   public static Jf<v2> h;
   public static Jf<vc> c;
   public static Jf<vc> d;
   public static Jf<Float> l;
   public static Jf<Float> T;
   public static Jf<Float> L;
   public static Jf<Float> y;
   public static Jf<be> B;
   public static Jf<Float> S;
   public static Jf<v2> G;
   public static Jf<Float> C;
   public static Jf<b8<?>> q;
   public static Jf<List<t2>> H;
   public static Jf<aN> i;
   public static Jf<Float> O;
   public static Jf<bi> F;
   public static Jf<Boolean> N;
   public static Jf<Float> w;
   public static Jf<Boolean> n;
   public static Jf<Boolean> R;
   public static Jf<?> g;
   public static Jf<Boolean> X;
   public static Jf<Boolean> Y;
   public static Jf<Boolean> b;
   public static Jf<Boolean> V;
   public static Jf<Om> r;
   public static Jf<Float> p;
   public static Jf<Boolean> t;
   public static Jf<Boolean> s;
   public static Jf<Boolean> j;
   public static Jf<Float> I;
   public static Jf<Float> m;
   public static Jf<Boolean> e;
   public static Jf<Boolean> a;
   public static Jf<Boolean> f;
   public static Jf<?> A;
   public static Jf<?> u;

   public static N8<Jf<?>> d() {
      return P;
   }

   public static <T> Jf<T> j(String var0) {
      return (Jf)P.h(var0, Bg::lambda$defineUnsynced$0);
   }

   public static <T> Jf<T> y(String var0, Wj<T> var1, T var2) {
      return (Jf)P.h(var0, Bg::lambda$define$1);
   }

   private static cD lambda$define$1(Wj var0, Object var1, z2 var2) {
      return new cD(var2, var0, var1);
   }

   private static cD lambda$defineUnsynced$0(z2 var0) {
      return new cD(var0, (Wj)null, (Object)null);
   }

   static {
      long var0 = kt.a(-3968014069136536670L, -2683788335660343519L, MethodHandles.lookup().lookupClass()).a(34369364864068L) ^ 95787080906807L;
      P = new N8("environment_attribute");
      W = g9.w(P);
      Z = y("visual/fog_color", co.j, v2.q);
      o = y("visual/fog_start_distance", co.w, 0.0F);
      x = y("visual/fog_end_distance", co.w, 1024.0F);
      v = y("visual/sky_fog_end_distance", co.w, 512.0F);
      U = y("visual/cloud_fog_end_distance", co.w, 2048.0F);
      Q = y("visual/water_fog_color", co.j, new v2(16448205));
      z = y("visual/water_fog_start_distance", co.w, -8.0F);
      M = y("visual/water_fog_end_distance", co.w, 96.0F);
      h = y("visual/sky_color", co.j, v2.q);
      c = y("visual/sunrise_sunset_color", co.Y, vc.t);
      d = y("visual/cloud_color", co.Y, vc.t);
      l = y("visual/cloud_height", co.w, 192.33F);
      T = y("visual/sun_angle", co.I, 0.0F);
      L = y("visual/moon_angle", co.I, 0.0F);
      y = y("visual/star_angle", co.I, 0.0F);
      B = y("visual/moon_phase", co.E, be.FULL_MOON);
      S = y("visual/star_brightness", co.w, 0.0F);
      G = y("visual/sky_light_color", co.j, v2.e);
      C = y("visual/sky_light_factor", co.w, 1.0F);
      q = y("visual/default_dripstone_particle", co.C, new b8(R3.v));
      H = y("visual/ambient_particles", co.k, Collections.emptyList());
      i = y("audio/background_music", co.N, aN.w);
      O = y("audio/music_volume", co.w, 1.0F);
      F = y("audio/ambient_sounds", co.b, bi.V);
      N = y("audio/firefly_bush_sounds", co.O, false);
      w = y("gameplay/sky_light_level", co.w, 15.0F);
      n = j("gameplay/can_start_raid");
      R = y("gameplay/water_evaporates", co.O, false);
      g = j("gameplay/bed_rule");
      X = j("gameplay/respawn_anchor_works");
      Y = j("gameplay/nether_portal_spawns_piglin");
      b = y("gameplay/fast_lava", co.O, false);
      V = j("gameplay/increased_fire_burnout");
      r = j("gameplay/eyeblossom_open");
      p = j("gameplay/turtle_egg_hatch_chance");
      t = y("gameplay/piglins_zombify", co.O, true);
      s = j("gameplay/snow_golem_melts");
      j = y("gameplay/creaking_active", co.O, false);
      I = j("gameplay/surface_slime_spawn_chance");
      m = j("gameplay/cat_waking_up_gift_chance");
      e = j("gameplay/bees_stay_in_hive");
      a = j("gameplay/monsters_burn");
      f = j("gameplay/can_pillager_patrol_spawn");
      A = j("gameplay/villager_activity");
      u = j("gameplay/baby_villager_activity");
      P.f();
   }
}
