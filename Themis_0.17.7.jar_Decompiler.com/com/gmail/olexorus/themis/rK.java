package com.gmail.olexorus.themis;

import java.util.List;
import java.util.Objects;

public class RK {
   private List<zU> G;
   private boolean U;

   public RK(List<zU> var1, boolean var2) {
      this.G = var1;
      this.U = var2;
   }

   public static RK c(lm<?> var0) {
      List var1 = var0.j(zU::J);
      boolean var2 = var0.R().i(zZ.V_1_21_5) || var0.P();
      return new RK(var1, var2);
   }

   public static void z(lm<?> var0, RK var1) {
      var0.D(var1.G, zU::Z);
      if (var0.R().m(zZ.V_1_21_5)) {
         var0.I(var1.U);
      }

   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof RK)) {
         return false;
      } else {
         RK var2 = (RK)var1;
         return this.U != var2.U ? false : this.G.equals(var2.G);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.G, this.U});
   }
}
