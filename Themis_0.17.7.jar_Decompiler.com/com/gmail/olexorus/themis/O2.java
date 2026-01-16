package com.gmail.olexorus.themis;

import java.util.Objects;

public class o2 {
   private BM J;

   public o2(BM var1) {
      this.J = var1;
   }

   public static o2 C(lm<?> var0) {
      BM var1 = (BM)var0.y((VD)g6.c());
      return new o2(var1);
   }

   public static void v(lm<?> var0, o2 var1) {
      var0.j((GL)var1.J);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof o2)) {
         return false;
      } else {
         o2 var2 = (o2)var1;
         return this.J.equals(var2.J);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.J);
   }
}
