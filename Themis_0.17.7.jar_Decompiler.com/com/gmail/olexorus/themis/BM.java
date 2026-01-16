package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.PrimitiveIterator.OfInt;
import java.util.stream.Stream;

final class Bm extends BR implements N9 {
   final int[] k;
   private static final long a = kt.a(-1334499908278374837L, -2912449545743216561L, MethodHandles.lookup().lookupClass()).a(14043252448031L);

   Bm(int... var1) {
      this.k = Arrays.copyOf(var1, var1.length);
   }

   public int[] P() {
      return Arrays.copyOf(this.k, this.k.length);
   }

   public OfInt s() {
      return new RO(this);
   }

   public java.util.Spliterator.OfInt A() {
      return Arrays.spliterator(this.k);
   }

   static int[] W(N9 var0) {
      return var0 instanceof Bm ? ((Bm)var0).k : var0.P();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Bm var2 = (Bm)var1;
         return Arrays.equals(this.k, var2.k);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.k);
   }

   public Stream<? extends rE> T() {
      long var1 = a ^ 107313077832019L;
      return Stream.of(rE.l("value", this.k));
   }
}
