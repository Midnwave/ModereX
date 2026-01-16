package com.gmail.olexorus.themis;

public class iX<T extends gz> extends id implements Gu<T> {
   private final MO<T> r;
   private final Gw<T> E;

   public iX(MO<T> var1, Gw<T> var2) {
      this((z2)null, var1, var2);
   }

   public iX(z2 var1, MO<T> var2, Gw<T> var3) {
      super(var1);
      this.r = var2;
      this.E = var3;
   }

   public T P(lm<?> var1) {
      return (gz)this.r.apply(var1);
   }

   public void m(lm<?> var1, T var2) {
      this.E.accept(var1, var2);
   }
}
