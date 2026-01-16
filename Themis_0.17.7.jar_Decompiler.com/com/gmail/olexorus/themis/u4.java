package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;

final class u4 implements Vp {
   private char z = 167;
   private char o = '#';
   private GB C = null;
   private boolean d = false;
   private boolean F = false;
   private wb P = wb.Y();
   private t9 y;
   private static final long a = kt.a(2699481659060919492L, 1074552719878619713L, MethodHandles.lookup().lookupClass()).a(40143787924878L);

   u4() {
      this.y = t9.W;
      Wr.k.accept(this);
   }

   public Vp v() {
      this.d = true;
      return this;
   }

   public V2 x() {
      return new Wr(this.z, this.o, this.C, this.d, this.F, this.P, this.y);
   }

   private static lv lambda$extractUrls$0(WR var0, nh var1) {
      long var2 = a ^ 48257460813780L;
      String var4 = var1.Q();
      if (!Wr.J.matcher(var4).find()) {
         var4 = "http://" + var4;
      }

      try {
         new URI(var4);
         return (var0 == null ? var1 : (nh)var1.r(var0)).k(Eb.z(var4));
      } catch (URISyntaxException var6) {
         return var1;
      }
   }
}
