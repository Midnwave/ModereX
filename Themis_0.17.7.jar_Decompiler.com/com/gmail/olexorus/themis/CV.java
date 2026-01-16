package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

public class Cv {
   private TU T;
   private static final long a = kt.a(-4824393195416048690L, -4085369246826578079L, MethodHandles.lookup().lookupClass()).a(95397691302005L);

   public Cv(TU var1) {
      this.T = var1;
   }

   public static Cv M(lm<?> var0) {
      TU var1 = var0.u();
      return new Cv(var1);
   }

   public static void U(lm<?> var0, Cv var1) {
      var0.m(var1.T);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Cv)) {
         return false;
      } else {
         Cv var2 = (Cv)var1;
         return this.T.equals(var2.T);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.T);
   }

   public String toString() {
      long var1 = a ^ 110019783731909L;
      return "ItemUseRemainder{target=" + this.T + '}';
   }
}
