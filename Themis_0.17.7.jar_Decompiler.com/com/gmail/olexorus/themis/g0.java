package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public class G0 implements Iterable<Integer>, lK {
   public static final vm R = new vm((MH)null);
   private final int p;
   private final int q;
   private final int z;
   private static final long a = kt.a(-2386370274458976731L, -7586848870323782033L, MethodHandles.lookup().lookupClass()).a(204790533142896L);

   public G0(int var1, int var2, int var3) {
      long var4 = a ^ 24274287095880L;
      super();
      if (var3 == 0) {
         throw new IllegalArgumentException("Step must be non-zero.");
      } else if (var3 == Integer.MIN_VALUE) {
         throw new IllegalArgumentException("Step must be greater than Int.MIN_VALUE to avoid overflow on negation.");
      } else {
         this.p = var1;
         this.q = WX.F(var1, var2, var3);
         this.z = var3;
      }
   }

   public final int Y() {
      return this.p;
   }

   public final int W() {
      return this.q;
   }

   public final int L() {
      return this.z;
   }

   public wD O() {
      return (wD)(new wL(this.p, this.q, this.z));
   }

   public boolean u() {
      return this.z > 0 ? this.p > this.q : this.p < this.q;
   }

   public boolean equals(Object var1) {
      return var1 instanceof G0 && (this.u() && ((G0)var1).u() || this.p == ((G0)var1).p && this.q == ((G0)var1).q && this.z == ((G0)var1).z);
   }

   public int hashCode() {
      return this.u() ? -1 : 31 * (31 * this.p + this.q) + this.z;
   }

   public String toString() {
      long var1 = a ^ 124933172560008L;
      return this.z > 0 ? this.p + ".." + this.q + " step " + this.z : this.p + " downTo " + this.q + " step " + -this.z;
   }
}
