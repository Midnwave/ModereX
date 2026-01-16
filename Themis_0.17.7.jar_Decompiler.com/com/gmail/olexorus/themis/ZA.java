package com.gmail.olexorus.themis;

import java.util.List;

public abstract class Za<T extends Za<T>> extends lm<T> {
   private List<Jg> Q;

   public void t() {
      this.Q = this.j(Jg::n);
   }

   public void d() {
      this.D(this.Q, Jg::V);
   }

   public void w(T var1) {
      this.Q = var1.Y();
   }

   public List<Jg> Y() {
      return this.Q;
   }
}
