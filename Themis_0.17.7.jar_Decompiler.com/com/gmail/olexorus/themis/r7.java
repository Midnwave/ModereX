package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

public class R7 implements Iterable<Entry<mE, Integer>> {
   public static final R7 f = new Rg(Collections.emptyMap(), true);
   private Map<mE, Integer> N;
   private boolean W;
   private static final long a = kt.a(9120838801563966917L, -1313713934210916156L, MethodHandles.lookup().lookupClass()).a(83666107149016L);

   public R7(Map<mE, Integer> var1, boolean var2) {
      this.N = Collections.unmodifiableMap(var1);
      this.W = var2;
   }

   public static R7 w(lm<?> var0) {
      Map var1 = var0.U(R7::lambda$read$0, lm::Q);
      boolean var2 = var0.R().i(zZ.V_1_21_5) || var0.P();
      return new R7(var1, var2);
   }

   public static void J(lm<?> var0, R7 var1) {
      vL var2 = var0.R().u();
      var0.o(var1.A(), R7::lambda$write$1, lm::E);
      if (var0.R().m(zZ.V_1_21_5)) {
         var0.I(var1.n());
      }

   }

   public Map<mE, Integer> A() {
      return this.N;
   }

   public boolean n() {
      return this.W;
   }

   public Iterator<Entry<mE, Integer>> iterator() {
      return this.N.entrySet().iterator();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof R7)) {
         return false;
      } else {
         R7 var2 = (R7)var1;
         return this.W != var2.W ? false : this.N.equals(var2.N);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.N, this.W});
   }

   public String toString() {
      long var1 = a ^ 100441399086404L;
      return "ItemEnchantments{enchantments=" + this.N + ", showInTooltip=" + this.W + '}';
   }

   private static void lambda$write$1(vL var0, lm var1, mE var2) {
      var1.E(var2.f(var0));
   }

   private static mE lambda$read$0(lm var0, lm var1) {
      return (mE)var0.y((VD)EY.R());
   }
}
