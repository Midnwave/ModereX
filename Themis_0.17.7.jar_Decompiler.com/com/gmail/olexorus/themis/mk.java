package com.gmail.olexorus.themis;

import java.util.List;
import java.util.Objects;

public class MK {
   private List<TU> o;

   public MK(List<TU> var1) {
      this.o = var1;
   }

   public static MK m(lm<?> var0) {
      List var1 = var0.j(lm::c);
      return new MK(var1);
   }

   public static void i(lm<?> var0, MK var1) {
      var0.D(var1.o, lm::M);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof MK)) {
         return false;
      } else {
         MK var2 = (MK)var1;
         return this.o.equals(var2.o);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.o);
   }
}
