package com.gmail.olexorus.themis;

public class yb extends lm<yb> {
   private VC L;
   private String[] B;
   private boolean J;

   public void t() {
      int var1;
      if (this.I.i(zZ.V_1_8)) {
         this.L = new VC(this.k(), this.I);
      } else {
         var1 = this.f();
         short var2 = this.x();
         int var3 = this.f();
         this.L = new VC(var1, var2, var3);
      }

      if (this.I.i(zZ.V_1_20)) {
         this.J = this.P();
      } else {
         this.J = true;
      }

      this.B = new String[4];

      for(var1 = 0; var1 < 4; ++var1) {
         this.B[var1] = this.m(384);
      }

   }

   public void d() {
      if (this.I.i(zZ.V_1_8)) {
         long var1 = this.L.E(this.I);
         this.A(var1);
      } else {
         this.L(this.L.Q);
         this.f(this.L.R);
         this.L(this.L.e);
      }

      if (this.I.i(zZ.V_1_20)) {
         this.I(this.J);
      }

      for(int var3 = 0; var3 < 4; ++var3) {
         this.I(this.B[var3]);
      }

   }

   public void n(yb var1) {
      this.L = var1.L;
      this.J = var1.J;
      this.B = var1.B;
   }
}
