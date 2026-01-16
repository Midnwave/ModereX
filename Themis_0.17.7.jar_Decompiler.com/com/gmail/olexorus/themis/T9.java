package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class t9 {
   static final t9 W = e(Cd.E());
   final List<B5> V;
   final List<BX> u;
   final String I;
   private static final long a = kt.a(-2910533528524849760L, -6776169314324650751L, MethodHandles.lookup().lookupClass()).a(47154402026793L);

   static t9 e(List<Cd> var0) {
      long var1 = a ^ 11499245821050L;
      int var3 = var0.size();
      ArrayList var4 = new ArrayList();
      ArrayList var5 = new ArrayList(var3);
      StringBuilder var6 = new StringBuilder(var3);

      for(int var7 = 0; var7 < var3; ++var7) {
         Cd var8 = (Cd)var0.get(var7);
         char var9 = var8.A();
         B5 var10 = var8.T();
         boolean var11 = var10 instanceof BX;
         var6.append(var9);
         var5.add(var10);
         if (var11) {
            var4.add((BX)var10);
         }

         if (var8.t()) {
            boolean var12 = false;
            if (Character.isUpperCase(var9)) {
               var6.append(Character.toLowerCase(var9));
               var12 = true;
            } else if (Character.isLowerCase(var9)) {
               var6.append(Character.toUpperCase(var9));
               var12 = true;
            }

            if (var12) {
               var5.add(var10);
               if (var11) {
                  var4.add((BX)var10);
               }
            }
         }
      }

      if (var5.size() != var6.length()) {
         throw new IllegalStateException("formats length differs from characters length");
      } else {
         return new t9(Collections.unmodifiableList(var5), Collections.unmodifiableList(var4), var6.toString());
      }
   }

   t9(List<B5> var1, List<BX> var2, String var3) {
      this.V = var1;
      this.u = var2;
      this.I = var3;
   }
}
