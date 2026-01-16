package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

public class u2 extends us<u2> {
   private tO<?> E;
   private tO<?> I;
   private tO<?> h;
   private tO<?> F;
   private int s;
   private float Z;
   private static final long a = kt.a(292970919532955397L, 2908398544048638084L, MethodHandles.lookup().lookupClass()).a(192550737083695L);

   public u2(tO<?> var1, tO<?> var2, tO<?> var3, tO<?> var4, int var5, float var6) {
      super(vD.J);
      this.E = var1;
      this.I = var2;
      this.h = var3;
      this.F = var4;
      this.s = var5;
      this.Z = var6;
   }

   public static u2 G(lm<?> var0) {
      tO var1 = tO.X(var0);
      tO var2 = tO.X(var0);
      tO var3 = tO.X(var0);
      tO var4 = tO.X(var0);
      int var5 = var0.Q();
      float var6 = var0.L();
      return new u2(var1, var2, var3, var4, var5, var6);
   }

   public static void K(lm<?> var0, u2 var1) {
      tO.A(var0, var1.E);
      tO.A(var0, var1.I);
      tO.A(var0, var1.h);
      tO.A(var0, var1.F);
      var0.E(var1.s);
      var0.S(var1.Z);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof u2)) {
         return false;
      } else {
         u2 var2 = (u2)var1;
         if (this.s != var2.s) {
            return false;
         } else if (Float.compare(var2.Z, this.Z) != 0) {
            return false;
         } else if (!this.E.equals(var2.E)) {
            return false;
         } else if (!this.I.equals(var2.I)) {
            return false;
         } else {
            return !this.h.equals(var2.h) ? false : this.F.equals(var2.F);
         }
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.E, this.I, this.h, this.F, this.s, this.Z});
   }

   public String toString() {
      long var1 = a ^ 91538563457012L;
      return "FurnaceRecipeDisplay{ingredient=" + this.E + ", fuel=" + this.I + ", result=" + this.h + ", craftingStation=" + this.F + ", duration=" + this.s + ", experience=" + this.Z + '}';
   }
}
