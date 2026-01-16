package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.List;

class Mb implements bg<Mm> {
   final BY Q;
   private static final long a = kt.a(-1935055362885902655L, 8712219292511869601L, MethodHandles.lookup().lookupClass()).a(53338955805414L);

   Mb(BY var1) {
      this.Q = var1;
   }

   public Mm Y(RT var1, lm<?> var2) {
      long var3 = a ^ 77059351443920L;
      v2 var5 = (v2)var1.g("water_color", g9.j, var2);
      v2 var6 = (v2)var1.M("foliage_color", g9.j, var2);
      v2 var7 = (v2)var1.M("grass_color", g9.j, var2);
      bs var8 = (bs)var1.P("grass_color_modifier", bs.CODEC, bs.NONE, var2);
      v2 var9 = null;
      v2 var10 = null;
      v2 var11 = null;
      v2 var12 = null;
      t2 var13 = null;
      CW var14 = null;
      BI var15 = null;
      Ed var16 = null;
      float var17;
      vV var18;
      if (var2.R().i(zZ.V_1_21_11)) {
         var9 = (v2)var1.M("dry_foliage_color", g9.j, var2);
         if (this.Q != null) {
            var10 = (v2)this.Q.w(Bg.Z);
            var11 = (v2)this.Q.w(Bg.Q);
            var12 = (v2)this.Q.w(Bg.h);
            List var19 = (List)this.Q.w(Bg.H);
            var13 = var19.isEmpty() ? null : (t2)var19.get(0);
            bi var20 = (bi)this.Q.w(Bg.F);
            var14 = var20.m();
            var15 = var20.A();
            var16 = var20.V().isEmpty() ? null : (Ed)var20.V().get(0);
            var17 = (Float)this.Q.w(Bg.O);
            var18 = ((aN)this.Q.w(Bg.i)).z();
         } else {
            var17 = 1.0F;
            var18 = new vV();
         }
      } else {
         var10 = (v2)var1.g("fog_color", g9.j, var2);
         var11 = (v2)var1.g("water_fog_color", g9.j, var2);
         var12 = (v2)var1.g("sky_color", g9.j, var2);
         var13 = (t2)var1.M("particle", t2.b, var2);
         var14 = (CW)var1.M("ambient_sound", CW.x, var2);
         var15 = (BI)var1.M("mood_sound", BI.A, var2);
         var16 = (Ed)var1.M("additions_sound", Ed.Y, var2);
         if (var2.R().i(zZ.V_1_21_4)) {
            var17 = (Float)var1.P("music_volume", g9.x, 1.0F, var2);
            var18 = (vV)var1.E("music", gR.w, vV::new, var2);
         } else {
            gR var21 = (gR)var1.M("music", gR.O, var2);
            var18 = var21 != null ? new vV(var21, 1) : new vV();
            var17 = 1.0F;
         }
      }

      if (var10 == null) {
         var10 = Mm.X();
      }

      if (var11 == null) {
         var11 = Mm.n();
      }

      if (var12 == null) {
         var12 = Mm.a();
      }

      return new Mm(var10, var5, var11, var12, var6, var9, var7, var8, var13, var14, var15, var16, var18, var17);
   }

   public void X(RT var1, lm<?> var2, Mm var3) {
      long var4 = a ^ 33125056902216L;
      var1.X("water_color", Mm.Y(var3), g9.j, var2);
      if (Mm.N(var3) != null) {
         var1.X("foliage_color", Mm.N(var3), g9.j, var2);
      }

      if (Mm.j(var3) != null) {
         var1.X("grass_color", Mm.j(var3), g9.j, var2);
      }

      if (Mm.k(var3) != bs.NONE) {
         var1.X("grass_color_modifier", Mm.k(var3), bs.CODEC, var2);
      }

      if (var2.R().i(zZ.V_1_21_11)) {
         if (Mm.a(var3) != null) {
            var1.X("dry_foliage_color", Mm.a(var3), g9.j, var2);
         }
      } else {
         var1.X("fog_color", Mm.W(var3), g9.j, var2);
         var1.X("water_fog_color", Mm.y(var3), g9.j, var2);
         var1.X("sky_color", Mm.I(var3), g9.j, var2);
         if (Mm.d(var3) != null) {
            var1.X("particle", Mm.d(var3), t2.b, var2);
         }

         if (Mm.a(var3) != null) {
            var1.X("ambient_sound", Mm.a(var3), CW.x, var2);
         }

         if (Mm.W(var3) != null) {
            var1.X("mood_sound", Mm.W(var3), BI.A, var2);
         }

         if (Mm.Z(var3) != null) {
            var1.X("additions_sound", Mm.Z(var3), Ed.Y, var2);
         }

         if (var2.R().i(zZ.V_1_21_4)) {
            var1.X("music_volume", Mm.h(var3), g9.x, var2);
            var1.X("music", Mm.M(var3), gR.w, var2);
         } else if (!Mm.M(var3).o()) {
            rq var6 = (rq)Mm.M(var3).f().get(0);
            var1.X("music", (gR)var6.l(), gR.O, var2);
         }
      }

   }
}
