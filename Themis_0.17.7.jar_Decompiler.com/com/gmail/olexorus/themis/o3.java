package com.gmail.olexorus.themis;

import java.util.Collections;

public class O3 {
   public static lm<?>[] G(lm<?> var0) {
      int var2;
      lm[] var3;
      int var4;
      if (var0 instanceof sg) {
         sg var1 = (sg)var0;
         var2 = var1.e().length;
         if (((lm)var0).R() == zZ.V_1_17 && var2 > 1) {
            var3 = new lm[var2];

            for(var4 = 0; var4 < var2; ++var4) {
               int var5 = var1.e()[var4];
               var3[var4] = new sg(var5);
            }

            return var3;
         }
      } else if (var0 instanceof sj) {
         sj var6 = (sj)var0;
         var2 = var6.i().size();
         if (var6.R().m(zZ.V_1_16) && var2 > 1) {
            var3 = new lm[var2];

            for(var4 = 0; var4 < var2; ++var4) {
               os var10 = (os)var6.i().get(var4);
               var3[var4] = new sj(var6.v(), Collections.singletonList(var10));
            }

            return var3;
         }
      } else if (var0 instanceof sO) {
         sO var7 = (sO)var0;
         r_ var9 = var7.b();
         if (var7.R().m(zZ.V_1_18) && var9 != null) {
            var3 = new lm[]{new Qq(var7.M().d(), var7.M().Y(), var9), var7};
            return var3;
         }
      } else if (var0 instanceof Qw) {
         Qw var8 = (Qw)var0;
         if (var8.U() == -1) {
            if (((lm)var0).R().i(zZ.V_1_21_2)) {
               var0 = new Qg(var8.O());
            }
         } else if (var8.t() == -2 && ((lm)var0).R().i(zZ.V_1_21_2)) {
            var0 = new QK(var8.U(), var8.O());
         }
      }

      return new lm[]{(lm)var0};
   }
}
