package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;

public abstract class E2 implements X {
   protected final List<X> E;
   protected final WR F;
   private static final long a = kt.a(5741729187864097992L, 4401966649599394410L, MethodHandles.lookup().lookupClass()).a(246091240308075L);

   protected E2(List<? extends lv> var1, WR var2) {
      this.E = lv.Z(var1, p);
      this.F = var2;
   }

   public final List<X> C() {
      return this.E;
   }

   public final WR o() {
      return this.F;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof E2)) {
         return false;
      } else {
         E2 var2 = (E2)var1;
         return Objects.equals(this.E, var2.E) && Objects.equals(this.F, var2.F);
      }
   }

   public int hashCode() {
      int var1 = this.E.hashCode();
      var1 = 31 * var1 + this.F.hashCode();
      return var1;
   }

   public abstract String toString();

   private static boolean lambda$debuggerString$0(rE var0) {
      long var1 = a ^ 78263319469453L;
      return !var0.X().equals("children");
   }
}
