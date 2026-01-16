package com.gmail.olexorus.themis;

public class Q5 extends lm<Q5> {
   private float h;
   private int y;
   private int S;

   public void t() {
      this.h = this.L();
      if (this.I.R(zZ.V_1_7_10)) {
         this.y = this.x();
         this.S = this.x();
      } else {
         this.y = this.Q();
         this.S = this.Q();
      }

   }

   public void d() {
      this.S(this.h);
      if (this.I.R(zZ.V_1_7_10)) {
         this.f(this.y);
         this.f(this.S);
      } else {
         this.E(this.y);
         this.E(this.S);
      }

   }

   public void r(Q5 var1) {
      this.h = var1.h;
      this.y = var1.y;
      this.S = var1.S;
   }
}
