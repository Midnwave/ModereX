package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.stream.Stream;

final class M7 implements mG {
   private final int u;
   private final ne o;
   private static final long a = kt.a(2856282266181986852L, 4606405731884733730L, MethodHandles.lookup().lookupClass()).a(168981975219670L);

   M7(int var1, ne var2) {
      long var3 = a ^ 118451709742252L;
      super();
      this.u = var1;
      this.o = (ne)Objects.requireNonNull(var2, "type");
   }

   public int x() {
      return this.u;
   }

   public ne t() {
      return this.o;
   }

   public Stream<? extends rE> T() {
      long var1 = a ^ 11548083749744L;
      return Stream.of(rE.N("value", this.u), rE.E("type", this.o));
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof mG)) {
         return false;
      } else {
         mG var2 = (mG)var1;
         return this.x() == var2.x() && this.t() == var2.t();
      }
   }

   public int hashCode() {
      int var1 = this.u;
      var1 = 31 * var1 + this.o.hashCode();
      return var1;
   }

   public String toString() {
      return (this.o == ne.RELATIVE ? "~" : "") + this.u;
   }
}
