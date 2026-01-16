package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;

public class GN {
   private float u;
   private Gy L;
   private CW m;
   private boolean c;
   private List<WZ<?>> V;
   private static final long a = kt.a(1841068800428421388L, -4315678096857383637L, MethodHandles.lookup().lookupClass()).a(66722047103786L);

   public GN(float var1, Gy var2, CW var3, boolean var4, List<WZ<?>> var5) {
      this.u = var1;
      this.L = var2;
      this.m = var3;
      this.c = var4;
      this.V = var5;
   }

   public static GN h(lm<?> var0) {
      float var1 = var0.L();
      Gy var2 = (Gy)var0.w((Enum[])Gy.values());
      CW var3 = CW.B(var0);
      boolean var4 = var0.P();
      List var5 = var0.j(WZ::t);
      return new GN(var1, var2, var3, var4, var5);
   }

   public static void S(lm<?> var0, GN var1) {
      var0.S(var1.u);
      var0.o((Enum)var1.L);
      CW.O(var0, var1.m);
      var0.I(var1.c);
      var0.D(var1.V, WZ::r);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof GN)) {
         return false;
      } else {
         GN var2 = (GN)var1;
         if (Float.compare(var2.u, this.u) != 0) {
            return false;
         } else if (this.c != var2.c) {
            return false;
         } else if (this.L != var2.L) {
            return false;
         } else {
            return !this.m.equals(var2.m) ? false : this.V.equals(var2.V);
         }
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.u, this.L, this.m, this.c, this.V});
   }

   public String toString() {
      long var1 = a ^ 139136649944838L;
      return "ItemConsumable{consumeSeconds=" + this.u + ", animation=" + this.L + ", sound=" + this.m + ", consumeParticles=" + this.c + ", effects=" + this.V + '}';
   }
}
