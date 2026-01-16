package com.gmail.olexorus.themis;

import java.util.Objects;

public class gT {
   private float s;

   public gT(float var1) {
      this.s = var1;
   }

   public static gT Z(lm<?> var0) {
      return new gT(var0.L());
   }

   public static void N(lm<?> var0, gT var1) {
      var0.S(var1.s);
   }

   public boolean equals(Object var1) {
      if (var1 != null && this.getClass() == var1.getClass()) {
         gT var2 = (gT)var1;
         return Float.compare(var2.s, this.s) == 0;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.s);
   }
}
