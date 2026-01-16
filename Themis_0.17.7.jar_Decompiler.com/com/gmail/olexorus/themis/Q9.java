package com.gmail.olexorus.themis;

public class Q9 extends lm<Q9> {
   private CW K;
   private rU B;
   private VC E;
   private float n;
   private float L;
   private long b;

   public void t() {
      if (this.I.i(zZ.V_1_19_3)) {
         this.K = CW.B(this);
      } else if (this.I.i(zZ.V_1_9)) {
         this.K = b1.i(this.I.u(), this.Q());
      } else {
         al var1 = this.R();
         CW var2 = b1.N(var1.toString());
         this.K = (CW)(var2 == null ? new cZ(var1, (Float)null) : var2);
      }

      if (this.I.i(zZ.V_1_9)) {
         this.B = rU.G(this.Q());
      }

      this.E = new VC(this.f(), this.f(), this.f());
      this.n = this.L();
      if (this.I.i(zZ.V_1_10)) {
         this.L = this.L();
      } else {
         this.L = (float)this.h() / 63.5F;
      }

      if (this.I.i(zZ.V_1_19)) {
         this.b = this.k();
      }

   }

   public void d() {
      if (this.I.i(zZ.V_1_19_3)) {
         CW.O(this, this.K);
      } else if (this.I.i(zZ.V_1_9)) {
         this.E(this.K.f(this.I.u()));
      } else {
         this.I(this.K.l().R());
      }

      if (this.I.i(zZ.V_1_9)) {
         this.E(this.B.ordinal());
      }

      this.L(this.E.Q);
      this.L(this.E.R);
      this.L(this.E.e);
      this.S(this.n);
      if (this.I.i(zZ.V_1_10)) {
         this.S(this.L);
      } else {
         this.u((int)(this.L * 63.5F));
      }

      if (this.I.i(zZ.V_1_19)) {
         this.A(this.b);
      }

   }

   public void t(Q9 var1) {
      this.K = var1.K;
      this.B = var1.B;
      this.E = var1.E;
      this.n = var1.n;
      this.L = var1.L;
      this.b = var1.b;
   }
}
