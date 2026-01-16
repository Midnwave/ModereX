package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public class ct<T extends oh> extends id implements VI<T> {
   private final MO<T> N;
   private final Gw<T> w;
   private final M4<T> V;
   private final td<T> m;
   private static final long a = kt.a(-593141461669861434L, -4341104319730135191L, MethodHandles.lookup().lookupClass()).a(212810796360899L);

   public ct(z2 var1, MO<T> var2, Gw<T> var3, M4<T> var4, td<T> var5) {
      super(var1);
      this.N = var2;
      this.w = var3;
      this.V = var4;
      this.m = var5;
   }

   public T J(lm<?> var1) {
      return (oh)this.N.apply(var1);
   }

   public void c(lm<?> var1, T var2) {
      long var3 = a ^ 120005025455907L;
      if (this.w != null) {
         this.w.accept(var1, var2);
      } else if (!var2.p()) {
         throw new UnsupportedOperationException("Trying to write non-empty data for " + this.f());
      }

   }

   public T h(RT var1, vL var2) {
      return (oh)this.V.Z(var1, var2);
   }

   public void t(T var1, vL var2, RT var3) {
      long var4 = a ^ 854062889598L;
      if (this.m != null) {
         this.m.W(var1, var2, var3);
      } else if (!var1.p()) {
         throw new UnsupportedOperationException("Trying to encode non-empty data for " + this.f());
      }

   }
}
