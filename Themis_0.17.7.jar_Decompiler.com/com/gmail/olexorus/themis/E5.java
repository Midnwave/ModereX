package com.gmail.olexorus.themis;

import java.util.Objects;

public class E5 {
   private Gj<md> f;

   public E5(Gj<md> var1) {
      this.f = var1;
   }

   public static E5 c(lm<?> var0) {
      Gj var1 = Gj.K(var0, V5.h(), md::B);
      return new E5(var1);
   }

   public static void O(lm<?> var0, E5 var1) {
      Gj.G(var0, var1.f, md::f);
   }

   public boolean equals(Object var1) {
      if (var1 != null && this.getClass() == var1.getClass()) {
         E5 var2 = (E5)var1;
         return this.f.equals(var2.f);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.f);
   }
}
