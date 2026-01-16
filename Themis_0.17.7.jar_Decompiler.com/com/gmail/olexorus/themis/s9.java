package com.gmail.olexorus.themis;

public class s9 extends lm<s9> {
   private int C;
   private te W;
   private int m;
   private int d;
   private byte X;
   private RT B;

   public void t() {
      this.C = this.Q();
      int var1;
      if (this.I.i(zZ.V_1_18_2)) {
         var1 = this.Q();
      } else {
         var1 = this.M();
      }

      this.W = NX.V(var1, this.I);
      this.m = this.I.i(zZ.V_1_20_5) ? this.Q() : this.M();
      this.d = this.Q();
      if (this.I.g(zZ.V_1_7_10)) {
         this.X = this.M();
      }

      if (this.I.i(zZ.V_1_19) && this.I.m(zZ.V_1_20_5)) {
         this.B = (RT)this.u(lm::u);
      }

   }

   public void d() {
      this.E(this.C);
      if (this.I.i(zZ.V_1_18_2)) {
         this.E(this.W.f(this.I.u()));
      } else {
         this.u(this.W.f(this.I.u()));
      }

      if (this.I.i(zZ.V_1_20_5)) {
         this.E(this.m);
      } else {
         this.u(this.m);
      }

      this.E(this.d);
      if (this.I.g(zZ.V_1_7_10)) {
         this.u(this.X);
      }

      if (this.I.i(zZ.V_1_19) && this.I.m(zZ.V_1_20_5)) {
         this.l(this.B, lm::G);
      }

   }

   public void l(s9 var1) {
      this.C = var1.C;
      this.W = var1.W;
      this.m = var1.m;
      this.d = var1.d;
      this.X = var1.X;
      this.B = var1.B;
   }
}
