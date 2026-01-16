package com.gmail.olexorus.themis;

public class sr extends lm<sr> {
   private int z;
   private bG n;
   private Gi Q;
   private boolean U;

   public void t() {
      if (this.I.i(zZ.V_1_21_2)) {
         this.z = this.Q();
         this.n = bG.B(this);
         this.Q = new Gi(this.f());
      } else {
         this.z = this.I.i(zZ.V_1_8) ? this.Q() : this.f();
         V var1 = this.I.i(zZ.V_1_9) ? V.c(this) : new V((double)this.f() / 32.0D, (double)this.f() / 32.0D, (double)this.f() / 32.0D);
         float var2 = (float)this.M() / 0.7111111F;
         float var3 = (float)this.M() / 0.7111111F;
         this.n = new bG(var1, V.g(), var2, var3);
      }

      if (this.I.i(zZ.V_1_8)) {
         this.U = this.P();
      }

   }

   public void d() {
      if (this.I.i(zZ.V_1_21_2)) {
         this.E(this.z);
         bG.s(this, this.n);
         this.L(this.Q.a());
      } else {
         if (this.I.i(zZ.V_1_8)) {
            this.E(this.z);
         } else {
            this.L(this.z);
         }

         if (this.I.i(zZ.V_1_9)) {
            V.q(this, this.n.V());
         } else {
            V var1 = this.n.V();
            this.L(a8.J(var1.f * 32.0D));
            this.L(a8.J(var1.K * 32.0D));
            this.L(a8.J(var1.r * 32.0D));
         }

         this.u((int)(this.n.K() * 0.7111111F));
         this.u((int)(this.n.m() * 0.7111111F));
      }

      if (this.I.i(zZ.V_1_8)) {
         this.I(this.U);
      }

   }

   public void n(sr var1) {
      this.z = var1.z;
      this.n = var1.n;
      this.Q = var1.Q;
      this.U = var1.U;
   }
}
