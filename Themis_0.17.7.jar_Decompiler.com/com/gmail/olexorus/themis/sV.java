package com.gmail.olexorus.themis;

public class sv extends lm<sv> {
   private aX e;
   private boolean s;

   public void t() {
      if (this.I.i(zZ.V_1_21_6)) {
         this.e = (aX)this.i(aX.class);
      } else {
         this.e = aX.B(this.h());
      }

      if (this.I.i(zZ.V_1_14)) {
         this.s = this.P();
      }

   }

   public void d() {
      if (this.I.i(zZ.V_1_21_6)) {
         this.o(this.e);
      } else {
         this.u(this.e.W());
      }

      if (this.I.i(zZ.V_1_14)) {
         this.I(this.s);
      }

   }

   public void N(sv var1) {
      this.e = var1.e;
      this.s = var1.s;
   }
}
