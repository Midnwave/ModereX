package com.gmail.olexorus.themis;

public class yc extends lm<yc> {
   private VC V;
   private W2 C;
   private RT X;

   public void t() {
      this.V = this.M();
      int var1 = this.I.i(zZ.V_1_18) ? this.Q() : this.h();
      this.C = bN.A(this.I.u(), var1);
      this.X = this.u();
   }

   public void d() {
      this.o(this.V);
      int var1 = this.C.f(this.I.u());
      if (this.I.i(zZ.V_1_18)) {
         this.E(var1);
      } else {
         this.u(var1);
      }

      this.G(this.X);
   }

   public void P(yc var1) {
      this.V = var1.V;
      this.C = var1.C;
      this.X = var1.X;
   }
}
