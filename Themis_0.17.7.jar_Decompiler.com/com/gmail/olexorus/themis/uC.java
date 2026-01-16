package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;

public class uc extends us<uc> {
   private int m;
   private int p;
   private List<tO<?>> r;
   private tO<?> I;
   private tO<?> s;
   private static final long a = kt.a(-2355022597534714104L, -9204159615072278681L, MethodHandles.lookup().lookupClass()).a(15300811693003L);

   public uc(int var1, int var2, List<tO<?>> var3, tO<?> var4, tO<?> var5) {
      super(vD.a);
      this.m = var1;
      this.p = var2;
      this.r = var3;
      this.I = var4;
      this.s = var5;
   }

   public static uc G(lm<?> var0) {
      int var1 = var0.Q();
      int var2 = var0.Q();
      List var3 = var0.j(tO::X);
      tO var4 = tO.X(var0);
      tO var5 = tO.X(var0);
      return new uc(var1, var2, var3, var4, var5);
   }

   public static void G(lm<?> var0, uc var1) {
      var0.E(var1.m);
      var0.E(var1.p);
      var0.D(var1.r, tO::A);
      tO.A(var0, var1.I);
      tO.A(var0, var1.s);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof uc)) {
         return false;
      } else {
         uc var2 = (uc)var1;
         if (this.m != var2.m) {
            return false;
         } else if (this.p != var2.p) {
            return false;
         } else if (!this.r.equals(var2.r)) {
            return false;
         } else {
            return !this.I.equals(var2.I) ? false : this.s.equals(var2.s);
         }
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.m, this.p, this.r, this.I, this.s});
   }

   public String toString() {
      long var1 = a ^ 1876456450346L;
      return "ShapedCraftingRecipeDisplay{width=" + this.m + ", height=" + this.p + ", ingredients=" + this.r + ", result=" + this.I + ", craftingStation=" + this.s + '}';
   }
}
