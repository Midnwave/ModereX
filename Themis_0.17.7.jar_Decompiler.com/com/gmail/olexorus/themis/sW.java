package com.gmail.olexorus.themis;

public class sw extends lm<sw> {
   private int v;
   private VC s;
   private int Y;
   private boolean x;

   public void t() {
      this.v = this.f();
      if (this.I.i(zZ.V_1_8)) {
         this.s = this.M();
      } else {
         this.s = new VC(this.f(), this.M() & 255, this.f());
      }

      this.Y = this.f();
      this.x = this.P();
   }

   public void d() {
      this.L(this.v);
      if (this.I.i(zZ.V_1_8)) {
         this.o(this.s);
      } else {
         this.L(this.s.Q);
         this.u(this.s.R & 255);
         this.L(this.s.e);
      }

      this.L(this.Y);
      this.I(this.x);
   }

   public void Z(sw var1) {
      this.v = var1.v;
      this.s = var1.s;
      this.Y = var1.Y;
      this.x = var1.x;
   }
}
