package com.gmail.olexorus.themis;

public class sa extends lm<sa> {
   private int t;
   private int a;
   private int f;

   public void t() {
      if (this.I.R(zZ.V_1_7_10)) {
         this.t = this.f();
         this.a = this.f();
      } else {
         this.t = this.Q();
         this.a = this.Q();
         if (this.I.i(zZ.V_1_11)) {
            this.f = this.Q();
         }
      }

   }

   public void d() {
      if (this.I.R(zZ.V_1_7_10)) {
         this.L(this.t);
         this.L(this.a);
      } else {
         this.E(this.t);
         this.E(this.a);
         if (this.I.i(zZ.V_1_11)) {
            this.E(this.f);
         }
      }

   }

   public void I(sa var1) {
      this.t = var1.t;
      this.a = var1.a;
      this.f = var1.f;
   }
}
