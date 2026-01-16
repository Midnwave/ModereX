package com.gmail.olexorus.themis;

import java.util.List;
import java.util.Objects;

public class mM {
   private List<zI> b;
   private float R;
   private int o;
   private boolean X;

   public mM(List<zI> var1, float var2, int var3, boolean var4) {
      this.b = var1;
      this.R = var2;
      this.o = var3;
      this.X = var4;
   }

   public static mM N(lm<?> var0) {
      List var1 = var0.j(zI::F);
      float var2 = var0.L();
      int var3 = var0.Q();
      boolean var4 = var0.R().m(zZ.V_1_21_5) || var0.P();
      return new mM(var1, var2, var3, var4);
   }

   public static void Q(lm<?> var0, mM var1) {
      var0.D(var1.b, zI::L);
      var0.S(var1.R);
      var0.E(var1.o);
      if (var0.R().i(zZ.V_1_21_5)) {
         var0.I(var1.X);
      }

   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof mM)) {
         return false;
      } else {
         mM var2 = (mM)var1;
         if (Float.compare(var2.R, this.R) != 0) {
            return false;
         } else if (this.o != var2.o) {
            return false;
         } else if (!this.b.equals(var2.b)) {
            return false;
         } else {
            return this.X == var2.X;
         }
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.b, this.R, this.o, this.X});
   }
}
