package com.gmail.olexorus.themis;

public class l8<T extends l8<T>> extends lm<T> {
   private al m;
   private Rc E;

   public void t() {
      this.m = al.c(this);
      this.E = (Rc)this.y(65536, lm::Q);
   }

   public void d() {
      al.C(this, this.m);
      this.w(this.E, lm::b);
   }

   public void R(T var1) {
      this.m = var1.u();
      this.E = var1.A();
   }

   public al u() {
      return this.m;
   }

   public Rc A() {
      return this.E;
   }
}
