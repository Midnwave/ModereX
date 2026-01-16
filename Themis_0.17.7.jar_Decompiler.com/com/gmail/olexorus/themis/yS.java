package com.gmail.olexorus.themis;

public class ys extends lm<ys> {
   private int f;
   private VC M;
   private byte t;

   public void t() {
      this.f = this.Q();
      if (this.I.R(zZ.V_1_7_10)) {
         int var1 = this.f();
         int var2 = this.f();
         int var3 = this.f();
         this.M = new VC(var1, var2, var3);
      } else {
         this.M = this.M();
      }

      this.t = (byte)this.h();
   }

   public void d() {
      this.E(this.f);
      if (this.I.R(zZ.V_1_7_10)) {
         this.L(this.M.Q);
         this.L(this.M.R);
         this.L(this.M.e);
      } else {
         this.o(this.M);
      }

      this.u(this.t);
   }

   public void B(ys var1) {
      this.f = var1.f;
      this.M = var1.M;
      this.t = var1.t;
   }
}
