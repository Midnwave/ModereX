package com.gmail.olexorus.themis;

public class sK extends lm<sK> {
   private int y;
   private String V;

   public void t() {
      if (this.I.i(zZ.V_1_20_2)) {
         this.y = this.Q();
      } else {
         this.y = this.M();
      }

      if (this.I.i(zZ.V_1_18)) {
         this.V = this.A();
      } else {
         this.V = this.m(16);
      }

   }

   public void d() {
      if (this.I.i(zZ.V_1_20_2)) {
         this.E(this.y);
      } else {
         this.u(this.y);
      }

      if (this.I.i(zZ.V_1_18)) {
         this.I(this.V);
      } else {
         this.a(this.V, 16);
      }

   }

   public void i(sK var1) {
      this.y = var1.y;
      this.V = var1.V;
   }
}
