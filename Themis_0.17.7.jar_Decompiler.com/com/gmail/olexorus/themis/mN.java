package com.gmail.olexorus.themis;

import java.util.Objects;

public class mn<T> {
   private T J;
   private T g;

   public mn(T var1, T var2) {
      this.J = var1;
      this.g = var2;
   }

   public static <T> mn<T> F(lm<?> var0, MO<T> var1) {
      Object var2 = var1.apply(var0);
      Object var3 = var0.u(var1);
      return new mn(var2, var3);
   }

   public static <T> void k(lm<?> var0, mn<T> var1, Gw<T> var2) {
      var2.accept(var0, var1.J);
      var0.l(var1.g, var2);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof mn)) {
         return false;
      } else {
         mn var2 = (mn)var1;
         return !this.J.equals(var2.J) ? false : Objects.equals(this.g, var2.g);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.J, this.g});
   }
}
