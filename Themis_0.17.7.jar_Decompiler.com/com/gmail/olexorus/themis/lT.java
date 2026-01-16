package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;

final class lt extends E2 implements uM {
   private final String Q;
   private final String m;
   private final String v;
   private static final long c = kt.a(4836502088088090675L, 8941087967242889889L, MethodHandles.lookup().lookupClass()).a(173214199903197L);

   static uM c(List<? extends lv> var0, WR var1, String var2, String var3, String var4) {
      long var5 = c ^ 8843735701555L;
      return new lt(lv.Z(var0, p), (WR)Objects.requireNonNull(var1, "style"), (String)Objects.requireNonNull(var2, "name"), (String)Objects.requireNonNull(var3, "objective"), var4);
   }

   lt(List<X> var1, WR var2, String var3, String var4, String var5) {
      super(var1, var2);
      this.Q = var3;
      this.m = var4;
      this.v = var5;
   }

   public String e() {
      return this.Q;
   }

   public String E() {
      return this.m;
   }

   public String B() {
      return this.v;
   }

   public uM k(List<? extends lv> var1) {
      return c(var1, this.F, this.Q, this.m, this.v);
   }

   public uM m(WR var1) {
      return c(this.E, var1, this.Q, this.m, this.v);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof uM)) {
         return false;
      } else if (!super.equals(var1)) {
         return false;
      } else {
         uM var2 = (uM)var1;
         return Objects.equals(this.Q, var2.e()) && Objects.equals(this.m, var2.E()) && Objects.equals(this.v, var2.B());
      }
   }

   public int hashCode() {
      int var1 = super.hashCode();
      var1 = 31 * var1 + this.Q.hashCode();
      var1 = 31 * var1 + this.m.hashCode();
      var1 = 31 * var1 + Objects.hashCode(this.v);
      return var1;
   }

   public String toString() {
      return cH.M(this);
   }

   public V_ a() {
      return new A3(this);
   }
}
