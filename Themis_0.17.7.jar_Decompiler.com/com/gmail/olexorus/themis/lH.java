package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;

final class lh extends E2 implements gO {
   private final String h;
   private final X k;
   private static final long c = kt.a(7156378576667642530L, -544687100959703899L, MethodHandles.lookup().lookupClass()).a(61819043424460L);

   static gO g(List<? extends lv> var0, WR var1, String var2, lv var3) {
      long var4 = c ^ 19888217564197L;
      return new lh(lv.Z(var0, p), (WR)Objects.requireNonNull(var1, "style"), (String)Objects.requireNonNull(var2, "pattern"), lv.z(var3));
   }

   lh(List<X> var1, WR var2, String var3, X var4) {
      super(var1, var2);
      this.h = var3;
      this.k = var4;
   }

   public String b() {
      return this.h;
   }

   public X O() {
      return this.k;
   }

   public gO F(List<? extends lv> var1) {
      return g(var1, this.F, this.h, this.k);
   }

   public gO c(WR var1) {
      return g(this.E, var1, this.h, this.k);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof gO)) {
         return false;
      } else if (!super.equals(var1)) {
         return false;
      } else {
         gO var2 = (gO)var1;
         return Objects.equals(this.h, var2.b()) && Objects.equals(this.k, var2.O());
      }
   }

   public int hashCode() {
      int var1 = super.hashCode();
      var1 = 31 * var1 + this.h.hashCode();
      var1 = 31 * var1 + Objects.hashCode(this.k);
      return var1;
   }

   public String toString() {
      return cH.M(this);
   }

   public m1 n() {
      return new A9(this);
   }
}
