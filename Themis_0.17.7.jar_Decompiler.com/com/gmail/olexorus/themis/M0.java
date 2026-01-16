package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.List;

public class m0<T> extends m5<T> {
   private final List<T> H;
   private static final long a = kt.a(2201751534673237996L, -7526199481534032151L, MethodHandles.lookup().lookupClass()).a(248964519713205L);

   public m0(int var1, List<T> var2) {
      super(var1);
      this.H = var2;
   }

   public List<T> F() {
      return this.H;
   }

   public void V(List<T> var1) {
      var1.addAll(this.T(), this.F());
   }

   public String toString() {
      long var1 = a ^ 138143074318544L;
      return "+ " + this.T() + " : " + this.F();
   }
}
