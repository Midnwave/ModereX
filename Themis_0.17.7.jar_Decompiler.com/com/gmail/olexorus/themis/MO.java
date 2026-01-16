package com.gmail.olexorus.themis;

import java.util.List;
import java.util.Objects;

public class mo {
   private List<tB> c;

   public mo(List<tB> var1) {
      this.c = var1;
   }

   public static mo A(lm<?> var0) {
      List var1 = var0.j(tB::K);
      return new mo(var1);
   }

   public static void v(lm<?> var0, mo var1) {
      var0.D(var1.c, tB::i);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof mo)) {
         return false;
      } else {
         mo var2 = (mo)var1;
         return this.c.equals(var2.c);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.c);
   }
}
