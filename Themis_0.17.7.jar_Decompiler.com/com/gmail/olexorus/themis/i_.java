package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.List;

public final class i_ {
   private final X Q;
   private final X J;
   private final boolean M;
   private final boolean L;
   private final ak r;
   private final List<An> w;
   private final List<Ai> A;
   private static final long a = kt.a(4897773884247673892L, -3174207989123545812L, MethodHandles.lookup().lookupClass()).a(27546489738938L);

   public i_(X var1, X var2, boolean var3, boolean var4, ak var5, List<An> var6, List<Ai> var7) {
      long var8 = a ^ 8789192268834L;
      super();
      if (var4 && !var5.z()) {
         throw new IllegalArgumentException("Dialogs that pause the game must use after_action values that unpause it after user action!");
      } else {
         this.Q = var1;
         this.J = var2;
         this.M = var3;
         this.L = var4;
         this.r = var5;
         this.w = var6;
         this.A = var7;
      }
   }

   public static i_ H(RT var0, lm<?> var1) {
      long var2 = a ^ 101853392644131L;
      X var4 = (X)var0.g("title", h.z(var1), var1);
      X var5 = (X)var0.M("external_title", h.z(var1), var1);
      boolean var6 = var0.v("can_close_with_escape", true);
      boolean var7 = var0.v("pause", true);
      ak var8 = (ak)var0.P("after_action", ak::P, ak.CLOSE, var1);
      List var9 = var0.I("body", An::Q, var1);
      List var10 = var0.I("inputs", Ai::Z, var1);
      return new i_(var4, var5, var6, var7, var8, var9, var10);
   }

   public static void A(RT var0, lm<?> var1, i_ var2) {
      long var3 = a ^ 22471002686202L;
      var0.X("title", var2.Q, h.z(var1), var1);
      if (var2.J != null) {
         var0.X("external_title", var2.J, h.z(var1), var1);
      }

      if (!var2.M) {
         var0.j("can_close_with_escape", new mA(false));
      }

      if (!var2.L) {
         var0.j("pause", new mA(false));
      }

      if (var2.r != ak.CLOSE) {
         var0.X("after_action", var2.r, ak::u, var1);
      }

      if (!var2.w.isEmpty()) {
         var0.m("body", var2.w, An::S, var1);
      }

      if (!var2.A.isEmpty()) {
         var0.h("inputs", var2.A, Ai::e, var1);
      }

   }
}
