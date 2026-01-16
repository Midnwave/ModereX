package com.gmail.olexorus.themis;

import java.util.Objects;

public class zN implements t8 {
   private String W;

   public zN(String var1) {
      this.W = var1;
   }

   public static zN f(lm<?> var0) {
      return new zN(var0.A());
   }

   public static void c(lm<?> var0, zN var1) {
      var0.I(var1.W);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof zN)) {
         return false;
      } else {
         zN var2 = (zN)var1;
         return this.W.equals(var2.W);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.W);
   }
}
