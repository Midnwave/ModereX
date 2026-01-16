package com.gmail.olexorus.themis;

import java.util.ArrayList;
import java.util.List;

final class uL<T extends MC> implements T3<T> {
   private List<MC> x;
   private uX<? extends MC> z;

   uL() {
      this(tq.j);
   }

   uL(uX<? extends MC> var1) {
      this.z = var1;
   }

   public T3<T> r(MC var1) {
      Bq.j(var1);
      if (this.z == tq.j) {
         this.z = var1.y();
      }

      Bq.Z(var1, this.z);
      if (this.x == null) {
         this.x = new ArrayList();
      }

      this.x.add(var1);
      return this;
   }

   public zj P() {
      return (zj)(this.x == null ? zj.v() : new Bq(this.z, new ArrayList(this.x)));
   }
}
