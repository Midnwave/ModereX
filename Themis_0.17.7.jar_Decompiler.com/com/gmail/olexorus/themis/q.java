package com.gmail.olexorus.themis;

import java.util.Objects;

public class Q {
   private EZ W;

   public Q(EZ var1) {
      this.W = var1;
   }

   public static Q W(lm<?> var0) {
      EZ var1 = EZ.b(var0);
      return new Q(var1);
   }

   public static void u(lm<?> var0, Q var1) {
      EZ.M(var0, var1.W);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Q)) {
         return false;
      } else {
         Q var2 = (Q)var1;
         return this.W.equals(var2.W);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.W);
   }
}
