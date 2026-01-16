package com.gmail.olexorus.themis;

public class ca<T extends Mn> extends id implements aB<T> {
   private final MO<T> d;
   private final Gw<T> D;

   public ca(z2 var1, MO<T> var2, Gw<T> var3) {
      super(var1);
      this.d = var2;
      this.D = var3;
   }

   public int G() {
      zZ var1 = oS.J().g().w();
      return this.f(var1.u());
   }

   public T c(lm<?> var1) {
      return (Mn)this.d.apply(var1);
   }

   public void u(lm<?> var1, T var2) {
      this.D.accept(var1, var2);
   }
}
