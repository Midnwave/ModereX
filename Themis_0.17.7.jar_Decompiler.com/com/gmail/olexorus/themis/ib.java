package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public class iB<T> extends id implements bo<T> {
   private final MO<T> v;
   private final Gw<T> V;
   private static final long a = kt.a(-4739175074716805329L, -6805345884189383313L, MethodHandles.lookup().lookupClass()).a(234000434508213L);

   public iB(z2 var1, MO<T> var2, Gw<T> var3) {
      super(var1);
      this.v = var2;
      this.V = var3;
   }

   public T j(lm<?> var1) {
      long var2 = a ^ 56471544132712L;
      if (this.v == null) {
         throw new IllegalStateException(this + " doesn't support network reading");
      } else {
         return this.v.apply(var1);
      }
   }

   public void e(lm<?> var1, T var2) {
      long var3 = a ^ 11907611869732L;
      if (this.V == null) {
         throw new IllegalStateException(this + " doesn't support network writing");
      } else {
         this.V.accept(var1, var2);
      }
   }
}
