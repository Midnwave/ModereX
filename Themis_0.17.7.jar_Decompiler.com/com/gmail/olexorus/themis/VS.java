package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.UUID;

public class vS {
   private UUID i;
   private String A;
   private double G;
   private oF w;
   private static final long a = kt.a(8879465931767721069L, 97038216300863719L, MethodHandles.lookup().lookupClass()).a(204640024735476L);

   public vS(UUID var1, String var2, double var3, oF var5) {
      this.i = var1;
      this.A = var2;
      this.G = var3;
      this.w = var5;
   }

   public static vS r(lm<?> var0) {
      UUID var1;
      String var2;
      if (var0.R().i(zZ.V_1_21)) {
         al var3 = var0.R();
         var2 = var3.toString();
         var1 = lE.v(var3);
      } else {
         var1 = var0.V();
         var2 = var0.A();
      }

      double var6 = var0.o();
      oF var5 = (oF)var0.w((Enum[])oF.values());
      return new vS(var1, var2, var6, var5);
   }

   public static void s(lm<?> var0, vS var1) {
      if (var0.R().i(zZ.V_1_21)) {
         var0.T(new al(var1.A));
      } else {
         var0.y(var1.i);
         var0.I(var1.A);
      }

      var0.v(var1.G);
      var0.o((Enum)var1.w);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof vS)) {
         return false;
      } else {
         vS var2 = (vS)var1;
         if (Double.compare(var2.G, this.G) != 0) {
            return false;
         } else if (!this.i.equals(var2.i)) {
            return false;
         } else if (!this.A.equals(var2.A)) {
            return false;
         } else {
            return this.w == var2.w;
         }
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.i, this.A, this.G, this.w});
   }

   public String toString() {
      long var1 = a ^ 36055055579516L;
      return "Modifier{id=" + this.i + ", name='" + this.A + '\'' + ", value=" + this.G + ", operation=" + this.w + '}';
   }
}
