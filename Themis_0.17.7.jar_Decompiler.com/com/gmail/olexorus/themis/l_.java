package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;

final class l_ extends E2 implements Vr {
   private final B9 h;
   private static final long c = kt.a(4748606773475799793L, -8704146287320359136L, MethodHandles.lookup().lookupClass()).a(120950261871048L);

   private l_(List<X> var1, WR var2, B9 var3) {
      super(var1, var2);
      this.h = var3;
   }

   public B9 N() {
      return this.h;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Vr)) {
         return false;
      } else if (!super.equals(var1)) {
         return false;
      } else {
         l_ var2 = (l_)var1;
         return Objects.equals(this.h, var2.N());
      }
   }

   public int hashCode() {
      int var1 = super.hashCode();
      var1 = 31 * var1 + this.h.hashCode();
      return var1;
   }

   public String toString() {
      return cH.M(this);
   }

   public A7 K() {
      return new Ao(this);
   }

   static l_ k(List<? extends lv> var0, WR var1, B9 var2) {
      long var3 = c ^ 124800452243875L;
      return new l_(lv.Z(var0, p), (WR)Objects.requireNonNull(var1, "style"), (B9)Objects.requireNonNull(var2, "contents"));
   }

   public Vr L(List<? extends lv> var1) {
      return k(var1, this.F, this.h);
   }

   public Vr x(WR var1) {
      return k(this.E, var1, this.h);
   }
}
