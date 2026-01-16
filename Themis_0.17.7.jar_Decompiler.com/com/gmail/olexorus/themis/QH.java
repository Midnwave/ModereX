package com.gmail.olexorus.themis;

public class Qh extends lm<Qh> {
   private boolean z;
   private boolean v;
   private boolean T;
   private boolean w;
   private float h;
   private float n;

   public void t() {
      byte var1 = this.M();
      this.z = (var1 & 1) != 0;
      this.v = (var1 & 2) != 0;
      this.T = (var1 & 4) != 0;
      this.w = (var1 & 8) != 0;
      this.h = this.L();
      this.n = this.L();
   }

   public void d() {
      byte var1 = 0;
      if (this.z) {
         var1 = (byte)(var1 | 1);
      }

      if (this.v) {
         var1 = (byte)(var1 | 2);
      }

      if (this.T) {
         var1 = (byte)(var1 | 4);
      }

      if (this.w) {
         var1 = (byte)(var1 | 8);
      }

      this.u(var1);
      this.S(this.h);
      this.S(this.n);
   }

   public void I(Qh var1) {
      this.z = var1.z;
      this.v = var1.v;
      this.T = var1.T;
      this.w = var1.w;
      this.h = var1.h;
      this.n = var1.n;
   }
}
