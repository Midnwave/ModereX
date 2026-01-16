package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.PrimitiveIterator.OfLong;
import java.util.stream.Stream;

final class B8 extends BR implements bm {
   final long[] B;
   private static final long a = kt.a(382449496148156739L, -3926677257472521618L, MethodHandles.lookup().lookupClass()).a(38484488706889L);

   B8(long[] var1) {
      this.B = Arrays.copyOf(var1, var1.length);
   }

   public long[] F() {
      return Arrays.copyOf(this.B, this.B.length);
   }

   public OfLong c() {
      return new tW(this);
   }

   public java.util.Spliterator.OfLong T() {
      return Arrays.spliterator(this.B);
   }

   static long[] L(bm var0) {
      return var0 instanceof B8 ? ((B8)var0).B : var0.F();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         B8 var2 = (B8)var1;
         return Arrays.equals(this.B, var2.B);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.B);
   }

   public Stream<? extends rE> T() {
      long var1 = a ^ 42766663321554L;
      return Stream.of(rE.I("value", this.B));
   }
}
