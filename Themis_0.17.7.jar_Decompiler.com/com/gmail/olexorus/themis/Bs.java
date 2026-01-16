package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

class bS implements bg<Wa> {
   private static final long a = kt.a(7408486047387736422L, -2315385771677626136L, MethodHandles.lookup().lookupClass()).a(38105994482793L);

   public Wa F(RT var1, lm<?> var2) {
      long var3 = a ^ 28590714971515L;
      float var5 = var1.N("temperature").b();
      R2 var6 = (R2)var1.P("temperature_modifier", R2.CODEC, R2.NONE, var2);
      float var7 = var1.N("downfall").b();
      W9 var9 = null;
      Float var10 = null;
      Float var11 = null;
      boolean var8;
      if (var2.R().i(zZ.V_1_19_3)) {
         var8 = var1.w("has_precipitation");
      } else {
         var8 = var1.g("precipitation", ia.CODEC, var2) != ia.NONE;
         if (var2.R().m(zZ.V_1_19)) {
            var9 = (W9)var1.g("category", W9.CODEC, var2);
            if (var2.R().m(zZ.V_1_18)) {
               var10 = var1.N("depth").b();
               var11 = var1.N("scale").b();
            }
         }
      }

      BY var12;
      if (var2.R().i(zZ.V_1_21_11)) {
         var12 = (BY)var1.P("attributes", BY.Y, BY.x, var2);
      } else {
         var12 = BY.x;
      }

      Mm var13 = (Mm)var1.g("effects", Mm.N(var12), var2);
      return new cu((z2)null, var8, var5, var6, var7, var9, var10, var11, var13, var12);
   }

   public void s(RT var1, lm<?> var2, Wa var3) {
      long var4 = a ^ 59743977356434L;
      var1.j("temperature", new m6(var3.h()));
      if (var3.B() != R2.NONE) {
         var1.X("temperature_modifier", var3.B(), R2.CODEC, var2);
      }

      var1.j("downfall", new m6(var3.Z()));
      var1.X("effects", var3.K(), Mm.K, var2);
      if (var2.R().i(zZ.V_1_19_3)) {
         var1.j("has_precipitation", new mA(var3.l()));
         if (var2.R().i(zZ.V_1_21_11)) {
            var1.X("attributes", var3.R(), BY.Y, var2);
         }
      } else {
         var1.X("precipitation", var3.c(), ia.CODEC, var2);
         if (var2.R().m(zZ.V_1_19)) {
            if (var3.N() != null) {
               var1.X("category", var3.N(), W9.CODEC, var2);
            }

            if (var2.R().m(zZ.V_1_18)) {
               if (var3.L() != null) {
                  var1.j("depth", new m6(var3.L()));
               }

               if (var3.q() != null) {
                  var1.j("scale", new m6(var3.q()));
               }
            }
         }
      }

   }
}
