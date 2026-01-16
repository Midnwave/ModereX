package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

final class BT extends BG implements G6 {
   private final long y;
   private static final long a = kt.a(967653879185108710L, 4083660208021776321L, MethodHandles.lookup().lookupClass()).a(40881628144544L);

   BT(long var1) {
      this.y = var1;
   }

   public long A() {
      return this.y;
   }

   public byte l() {
      return (byte)((int)(this.y & 255L));
   }

   public int p() {
      return (int)this.y;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         BT var2 = (BT)var1;
         return this.y == var2.y;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Long.hashCode(this.y);
   }

   public Stream<? extends rE> T() {
      long var1 = a ^ 134183111758201L;
      return Stream.of(rE.S("value", this.y));
   }
}
