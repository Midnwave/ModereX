package com.gmail.olexorus.themis;

import java.util.Objects;

public class Ne {
   private al Z;

   public Ne(al var1) {
      this.Z = var1;
   }

   public static Ne S(lm<?> var0) {
      al var1 = var0.R();
      return new Ne(var1);
   }

   public static void X(lm<?> var0, Ne var1) {
      var0.T(var1.Z);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Ne)) {
         return false;
      } else {
         Ne var2 = (Ne)var1;
         return this.Z.equals(var2.Z);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.Z);
   }
}
