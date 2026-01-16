package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

public interface BX extends Comparable<BX>, nc, r9, rA, B5 {
   long b = kt.a(2091300949583535084L, 9169955796438449644L, MethodHandles.lookup().lookupClass()).a(172545974064233L);

   static BX C(int var0) {
      int var1 = var0 & 16777215;
      oT var2 = oT.j(var1);
      return (BX)(var2 != null ? var2 : new Ab(var1));
   }

   static BX u(String var0) {
      if (var0.startsWith("#")) {
         try {
            int var1 = Integer.parseInt(var0.substring(1), 16);
            return C(var1);
         } catch (NumberFormatException var2) {
            return null;
         }
      } else {
         return null;
      }
   }

   int Y();

   default String z() {
      StringBuilder var1 = new StringBuilder();
      var1.append("#");
      String var2 = Integer.toHexString(this.Y());

      for(int var3 = 0; var3 < 6 - var2.length(); ++var3) {
         var1.append('0');
      }

      var1.append(var2);
      return var1.toString().toUpperCase(Locale.ROOT);
   }

   default int k() {
      return this.Y() >> 16 & 255;
   }

   default int v() {
      return this.Y() >> 8 & 255;
   }

   default int D() {
      return this.Y() & 255;
   }

   static <C extends BX> C t(List<C> var0, BX var1) {
      long var2 = b ^ 23409214234656L;
      Objects.requireNonNull(var1, "color");
      float var4 = Float.MAX_VALUE;
      BX var5 = (BX)var0.get(0);
      int var6 = 0;

      for(int var7 = var0.size(); var6 < var7; ++var6) {
         BX var8 = (BX)var0.get(var6);
         float var9 = Ab.M(var1.q(), var8.q());
         if (var9 < var4) {
            var5 = var8;
            var4 = var9;
         }

         if (var9 == 0.0F) {
            break;
         }
      }

      return var5;
   }

   default int t(BX var1) {
      return Integer.compare(this.Y(), var1.Y());
   }

   default Stream<? extends rE> T() {
      long var1 = b ^ 73399490881119L;
      return Stream.of(rE.c("value", this.z()));
   }
}
