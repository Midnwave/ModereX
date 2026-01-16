package com.gmail.olexorus.themis;

public class sI extends lm<sI> {
   private CW m;
   private rU n;
   private int K;
   private float E;
   private float d;
   private long q;

   public void t() {
      this.m = this.I.i(zZ.V_1_19_3) ? CW.B(this) : b1.i(this.I.u(), this.Q());
      this.n = rU.G(this.Q());
      this.K = this.Q();
      this.E = this.L();
      if (this.I.i(zZ.V_1_10)) {
         this.d = this.L();
      } else {
         this.d = (float)this.h() / 63.5F;
      }

      if (this.I.i(zZ.V_1_19)) {
         this.q = this.k();
      }

   }

   public void d() {
      if (this.I.i(zZ.V_1_19_3)) {
         CW.O(this, this.m);
      } else {
         this.E(this.m.f(this.I.u()));
      }

      this.E(this.n.ordinal());
      this.E(this.K);
      this.S(this.E);
      if (this.I.i(zZ.V_1_10)) {
         this.S(this.d);
      } else {
         this.u((int)(this.d * 63.5F));
      }

      if (this.I.i(zZ.V_1_19)) {
         this.A(this.q);
      }

   }

   public void O(sI var1) {
      this.m = var1.m;
      this.n = var1.n;
      this.K = var1.K;
      this.E = var1.E;
      this.d = var1.d;
      this.q = var1.q;
   }
}
