package com.gmail.olexorus.themis;

public class QY extends lm<QY> {
   private al e;
   private VC E;
   private float y;
   private float f;

   public void t() {
      if (this.I.i(zZ.V_1_21_9)) {
         this.e = al.c(this);
      }

      this.E = this.I.i(zZ.V_1_8) ? this.M() : new VC(this.f(), this.f(), this.f());
      if (this.I.i(zZ.V_1_17)) {
         this.y = this.L();
      }

      if (this.I.i(zZ.V_1_21_9)) {
         this.f = this.L();
      }

   }

   public void d() {
      if (this.I.i(zZ.V_1_21_9)) {
         al.C(this, this.e);
      }

      if (this.I.i(zZ.V_1_8)) {
         this.o(this.E);
      } else {
         this.L(this.E.Q);
         this.L(this.E.R);
         this.L(this.E.e);
      }

      if (this.I.i(zZ.V_1_17)) {
         this.S(this.y);
      }

      if (this.I.i(zZ.V_1_21_9)) {
         this.S(this.f);
      }

   }

   public void w(QY var1) {
      this.e = var1.e;
      this.E = var1.E;
      this.y = var1.y;
      this.f = var1.f;
   }
}
