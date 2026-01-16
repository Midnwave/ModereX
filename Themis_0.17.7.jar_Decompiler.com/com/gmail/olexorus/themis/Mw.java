package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.List;

public class mw<T> extends m5<T> {
   private final int k;
   private static final long a = kt.a(-526286238044230710L, -2626724759757939802L, MethodHandles.lookup().lookupClass()).a(103720301330023L);

   public mw(int var1, int var2) {
      super(var1);
      this.k = var2;
   }

   public int V() {
      return this.k;
   }

   public void V(List<T> var1) {
      var1.subList(this.T(), this.T() + this.k).clear();
   }

   public String toString() {
      long var1 = a ^ 89202490589843L;
      return "- " + this.T() + " : " + this.V();
   }
}
