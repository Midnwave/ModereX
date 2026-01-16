package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.function.Predicate;

class aP implements tw<T> {
   final Predicate v;
   final tw s;
   private static final long a = kt.a(-3909920165928222872L, -2789713133939833909L, MethodHandles.lookup().lookupClass()).a(40053458273049L);

   aP(tw var1, Predicate var2) {
      this.s = var1;
      this.v = var2;
   }

   public T n(Rc var1, lm<?> var2) {
      long var3 = a ^ 135454581670130L;
      Object var5 = this.s.n(var1, var2);
      if (!this.v.test(var5)) {
         throw new to("Decode predicate failed " + this.v);
      } else {
         return var5;
      }
   }

   public Rc j(lm<?> var1, T var2) {
      long var3 = a ^ 105688184257072L;
      if (!this.v.test(var2)) {
         throw new to("Encode predicate failed " + this.v);
      } else {
         return this.s.j(var1, var2);
      }
   }
}
