package com.gmail.olexorus.themis;

import java.util.List;
import java.util.Map;

public class sP extends lm<sP> {
   private ot<?>[] Y;
   private Map<al, Nd> J;
   private List<vU> w;

   public void t() {
      if (this.I.i(zZ.V_1_21_2)) {
         this.J = this.U(lm::R, Nd::R);
         this.w = this.j(vU::N);
      } else {
         this.Y = (ot[])this.t(ot::M, ot.class);
      }

   }

   public void d() {
      if (this.I.i(zZ.V_1_21_2)) {
         this.o(this.J, lm::T, Nd::Q);
         this.D(this.w, vU::f);
      } else {
         this.c(this.Y, ot::z);
      }

   }

   public void o(sP var1) {
      this.Y = var1.Y;
      this.J = var1.J;
      this.w = var1.w;
   }
}
