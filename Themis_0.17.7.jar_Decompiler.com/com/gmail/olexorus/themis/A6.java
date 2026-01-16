package com.gmail.olexorus.themis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class a6 extends lm<a6> {
   private int E;
   private int U;
   private List<TU> q;
   private Optional<TU> d;

   public void t() {
      this.E = this.a();
      boolean var1 = this.I.i(zZ.V_1_17_1);
      if (var1) {
         this.U = this.Q();
      }

      int var2 = var1 ? this.Q() : this.x();
      this.q = new ArrayList(var2);

      for(int var3 = 0; var3 < var2; ++var3) {
         this.q.add(this.u());
      }

      if (this.I.i(zZ.V_1_17_1)) {
         this.d = Optional.of(this.u());
      } else {
         this.d = Optional.empty();
      }

   }

   public void d() {
      this.y(this.E);
      boolean var1 = this.I.i(zZ.V_1_17_1);
      if (var1) {
         this.E(this.U);
      }

      if (var1) {
         this.E(this.q.size());
      } else {
         this.f(this.q.size());
      }

      Iterator var2 = this.q.iterator();

      while(var2.hasNext()) {
         TU var3 = (TU)var2.next();
         this.m(var3);
      }

      if (var1) {
         this.m((TU)this.d.orElse(TU.B));
      }

   }

   public void L(a6 var1) {
      this.E = var1.E;
      this.U = var1.U;
      this.q = var1.q;
      this.d = var1.d;
   }
}
