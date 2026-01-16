package com.gmail.olexorus.themis;

public class y6 extends lm<y6> {
   private int d;
   private oj u;
   private int f;

   public void t() {
      boolean var1 = this.I.i(zZ.V_1_8);
      this.d = var1 ? this.Q() : this.f();
      int var2 = var1 ? this.Q() : this.M();
      this.u = oj.o(this.I, var2);
      this.f = var1 ? this.Q() : this.f();
   }

   public void d() {
      boolean var1 = this.I.i(zZ.V_1_8);
      int var2;
      if (var1) {
         this.E(this.d);
         var2 = this.u.T(this.I);
         this.E(var2);
         this.E(this.f);
      } else {
         this.L(this.d);
         var2 = this.u.T(this.I);
         this.u(var2);
         this.L(this.f);
      }

   }

   public void U(y6 var1) {
      this.d = var1.d;
      this.u = var1.u;
      this.f = var1.f;
   }
}
