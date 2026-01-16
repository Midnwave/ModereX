package com.gmail.olexorus.themis;

public class nw extends n0 {
   private final long[] L;
   private final int p;
   private final int t;
   private final long I;

   public nw(int var1, long[] var2) {
      if (var1 < 4) {
         var1 = 4;
      }

      this.p = var1;
      this.L = var2;
      this.t = this.L.length * 64 / this.p;
      this.I = (1L << this.p) - 1L;
   }

   public int h(int var1) {
      int var2 = var1 * this.p;
      int var3 = var2 / 64;
      int var4 = ((var1 + 1) * this.p - 1) / 64;
      int var5 = var2 % 64;
      if (var3 == var4) {
         return (int)(this.L[var3] >>> var5 & this.I);
      } else {
         int var6 = 64 - var5;
         return (int)((this.L[var3] >>> var5 | this.L[var4] << var6) & this.I);
      }
   }

   public long[] n() {
      return this.L;
   }

   public int D() {
      return this.p;
   }
}
