package com.gmail.olexorus.themis;

public class QE extends lm<QE> {
   private int t;
   private double J;
   private double s;
   private double G;
   private short M;

   public void t() {
      this.t = this.Q();
      if (this.I.i(zZ.V_1_9)) {
         this.J = this.o();
         this.s = this.o();
         this.G = this.o();
      } else {
         this.J = (double)this.f() / 32.0D;
         this.s = (double)this.f() / 32.0D;
         this.G = (double)this.f() / 32.0D;
      }

      this.M = this.x();
   }

   public void d() {
      this.E(this.t);
      if (this.I.i(zZ.V_1_9)) {
         this.v(this.J);
         this.v(this.s);
         this.v(this.G);
      } else {
         this.L(a8.J(this.J * 32.0D));
         this.L(a8.J(this.s * 32.0D));
         this.L(a8.J(this.G * 32.0D));
      }

      this.f(this.M);
   }

   public void S(QE var1) {
      this.t = var1.t;
      this.J = var1.J;
      this.s = var1.s;
      this.G = var1.G;
      this.M = var1.M;
   }
}
