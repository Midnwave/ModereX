package com.gmail.olexorus.themis;

public class Qc extends lm<Qc> {
   private float D;
   private int Y;
   private float H;

   public void t() {
      this.D = this.L();
      if (this.I.R(zZ.V_1_7_10)) {
         this.Y = this.x();
      } else {
         this.Y = this.Q();
      }

      this.H = this.L();
   }

   public void d() {
      this.S(this.D);
      if (this.I.R(zZ.V_1_7_10)) {
         this.f(this.Y);
      } else {
         this.E(this.Y);
      }

      this.S(this.H);
   }

   public void Q(Qc var1) {
      this.D = var1.D;
      this.Y = var1.Y;
      this.H = var1.H;
   }
}
