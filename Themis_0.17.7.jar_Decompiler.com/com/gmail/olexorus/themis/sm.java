package com.gmail.olexorus.themis;

import java.util.List;

public class sM extends lm<sM> {
   private int T;
   private List<Tw<?>> u;

   public void t() {
      this.T = this.I.i(zZ.V_1_8) ? this.Q() : this.f();
      this.u = this.n();
   }

   public void d() {
      if (this.I.i(zZ.V_1_8)) {
         this.E(this.T);
      } else {
         this.L(this.T);
      }

      this.R(this.u);
   }

   public void y(sM var1) {
      this.T = var1.T;
      this.u = var1.u;
   }
}
