package com.gmail.olexorus.themis;

public class s1 extends lm<s1> {
   private int R;
   private V K;

   public s1(aE var1) {
      super(var1);
   }

   public void t() {
      this.R = this.I.m(zZ.V_1_8) ? this.f() : this.Q();
      if (this.I.i(zZ.V_1_21_9)) {
         this.K = aR.G(this);
      } else {
         double var1 = (double)this.x() / 8000.0D;
         double var3 = (double)this.x() / 8000.0D;
         double var5 = (double)this.x() / 8000.0D;
         this.K = new V(var1, var3, var5);
      }

   }

   public void d() {
      if (this.I.m(zZ.V_1_8)) {
         this.L(this.R);
      } else {
         this.E(this.R);
      }

      if (this.I.i(zZ.V_1_21_9)) {
         aR.M(this, this.K);
      } else {
         this.f((int)(this.K.f * 8000.0D + Math.copySign(1.0E-11D, this.K.f)));
         this.f((int)(this.K.K * 8000.0D + Math.copySign(1.0E-11D, this.K.K)));
         this.f((int)(this.K.r * 8000.0D + Math.copySign(1.0E-11D, this.K.r)));
      }

   }

   public void G(s1 var1) {
      this.R = var1.R;
      this.K = var1.K;
   }

   public int P() {
      return this.R;
   }

   public V w() {
      return this.K;
   }
}
