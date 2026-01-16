package com.gmail.olexorus.themis;

import java.util.Objects;

public class tP {
   private CW E;

   public tP(CW var1) {
      this.E = var1;
   }

   public static tP j(lm<?> var0) {
      CW var1 = CW.B(var0);
      return new tP(var1);
   }

   public static void N(lm<?> var0, tP var1) {
      CW.O(var0, var1.E);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof tP)) {
         return false;
      } else {
         tP var2 = (tP)var1;
         return this.E.equals(var2.E);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.E);
   }
}
