package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

public final class m {
   private static final Map<String, String> D;
   private static final N8<TY> b;
   public static final TY h;
   public static final TY B;
   public static final TY E;
   public static final TY u;
   public static final TY G;
   public static final TY Z;
   public static final TY l;
   public static final TY S;
   public static final TY I;
   public static final TY P;
   public static final TY W;
   public static final TY U;
   public static final TY R;
   public static final TY N;
   public static final TY V;
   public static final TY d;
   public static final TY m;
   public static final TY g;
   public static final TY a;
   public static final TY K;
   public static final TY p;
   public static final TY j;
   public static final TY s;
   public static final TY y;
   public static final TY Q;
   public static final TY L;
   public static final TY w;
   public static final TY e;

   private static TY f(String var0) {
      return (TY)b.h(var0, iN::new);
   }

   public static TY j(String var0) {
      return (TY)b.R((String)D.getOrDefault(var0, var0));
   }

   public static TY o(vL var0, int var1) {
      return (TY)b.e(var0, var1);
   }

   static {
      long var0 = kt.a(-1169177501518521856L, 306009145889051014L, MethodHandles.lookup().lookupClass()).a(232610782664356L) ^ 90283167474711L;
      D = new HashMap();
      D.put("turtle", "minecraft:turtle_scute");
      D.put("minecraft:turtle", "minecraft:turtle_scute");
      D.put("armadillo", "minecraft:armadillo_scute");
      D.put("minecraft:armadillo", "minecraft:armadillo_scute");
      b = new N8("equipment_asset");
      h = f("leather");
      B = f("chainmail");
      E = f("iron");
      u = f("gold");
      G = f("diamond");
      Z = f("turtle_scute");
      l = Z;
      S = f("netherite");
      I = f("armadillo_scute");
      P = I;
      W = f("elytra");
      U = f("white_carpet");
      R = f("orange_carpet");
      N = f("magenta_carpet");
      V = f("light_blue_carpet");
      d = f("yellow_carpet");
      m = f("lime_carpet");
      g = f("pink_carpet");
      a = f("gray_carpet");
      K = f("light_gray_carpet");
      p = f("cyan_carpet");
      j = f("purple_carpet");
      s = f("blue_carpet");
      y = f("brown_carpet");
      Q = f("green_carpet");
      L = f("red_carpet");
      w = f("black_carpet");
      e = f("trader_llama");
      b.f();
   }
}
