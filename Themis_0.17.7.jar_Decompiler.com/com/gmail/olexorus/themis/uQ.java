package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;

public class uq extends us<uq> {
   private List<tO<?>> s;
   private tO<?> l;
   private tO<?> w;
   private static final long a = kt.a(-8511699473392853683L, 680264594079126884L, MethodHandles.lookup().lookupClass()).a(165968059041831L);

   public uq(List<tO<?>> var1, tO<?> var2, tO<?> var3) {
      super(vD.I);
      this.s = var1;
      this.l = var2;
      this.w = var3;
   }

   public static uq s(lm<?> var0) {
      List var1 = var0.j(tO::X);
      tO var2 = tO.X(var0);
      tO var3 = tO.X(var0);
      return new uq(var1, var2, var3);
   }

   public static void i(lm<?> var0, uq var1) {
      var0.D(var1.s, tO::A);
      tO.A(var0, var1.l);
      tO.A(var0, var1.w);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof uq)) {
         return false;
      } else {
         uq var2 = (uq)var1;
         if (!this.s.equals(var2.s)) {
            return false;
         } else {
            return !this.l.equals(var2.l) ? false : this.w.equals(var2.w);
         }
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.s, this.l, this.w});
   }

   public String toString() {
      long var1 = a ^ 56033833184421L;
      return "ShapelessCraftingRecipeDisplay{ingredients=" + this.s + ", result=" + this.l + ", craftingStation=" + this.w + '}';
   }
}
