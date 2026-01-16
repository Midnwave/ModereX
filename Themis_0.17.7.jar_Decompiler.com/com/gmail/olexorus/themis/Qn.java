package com.gmail.olexorus.themis;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QN extends lm<QN> {
   private boolean G;
   private List<zT> h;
   private Set<al> R;
   private Map<al, gJ> L;
   private boolean s;

   public void t() {
      this.G = this.P();
      this.h = this.j(zT::u);
      this.R = (Set)this.U(LinkedHashSet::new, al::c);
      this.L = this.U(al::c, gJ::S);
      this.s = this.I.m(zZ.V_1_21_5) || this.P();
   }

   public void d() {
      this.I(this.G);
      this.D(this.h, zT::U);
      this.d(this.R, al::C);
      this.o(this.L, al::C, gJ::L);
      if (this.I.i(zZ.V_1_21_5)) {
         this.I(this.s);
      }

   }

   public void w(QN var1) {
      this.G = var1.G;
      this.h = var1.h;
      this.R = var1.R;
      this.L = var1.L;
      this.s = var1.s;
   }
}
