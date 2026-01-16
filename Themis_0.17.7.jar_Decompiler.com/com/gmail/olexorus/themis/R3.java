package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

public class r3 {
   public final float E;
   public final float j;
   public final float D;
   private static final long a = kt.a(7544156423678527106L, 5611934297403196172L, MethodHandles.lookup().lookupClass()).a(167896597359377L);

   public r3() {
      this.E = 0.0F;
      this.j = 0.0F;
      this.D = 0.0F;
   }

   public r3(float var1, float var2, float var3) {
      this.E = var1;
      this.j = var2;
      this.D = var3;
   }

   public float X() {
      return this.E;
   }

   public float c() {
      return this.j;
   }

   public float P() {
      return this.D;
   }

   public boolean equals(Object var1) {
      if (var1 instanceof r3) {
         r3 var4 = (r3)var1;
         return this.E == var4.E && this.j == var4.j && this.D == var4.D;
      } else if (var1 instanceof V) {
         V var3 = (V)var1;
         return (double)this.E == var3.f && (double)this.j == var3.K && (double)this.D == var3.r;
      } else if (!(var1 instanceof VC)) {
         return false;
      } else {
         VC var2 = (VC)var1;
         return (double)this.E == (double)var2.Q && (double)this.j == (double)var2.R && (double)this.D == (double)var2.e;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.E, this.j, this.D});
   }

   public String toString() {
      long var1 = a ^ 46764598083848L;
      return "X: " + this.E + ", Y: " + this.j + ", Z: " + this.D;
   }
}
