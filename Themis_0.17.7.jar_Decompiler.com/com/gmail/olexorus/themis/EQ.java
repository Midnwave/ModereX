package com.gmail.olexorus.themis;

import java.util.List;
import java.util.Objects;

abstract class Eq<C extends mm<C, B>, B extends zB<C, B>> extends E2 implements mm<C, B> {
   final String v;
   final boolean X;
   final X i;

   Eq(List<X> var1, WR var2, String var3, boolean var4, X var5) {
      super(var1, var2);
      this.v = var3;
      this.X = var4;
      this.i = var5;
   }

   public String n() {
      return this.v;
   }

   public boolean o() {
      return this.X;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof mm)) {
         return false;
      } else if (!super.equals(var1)) {
         return false;
      } else {
         mm var2 = (mm)var1;
         return Objects.equals(this.v, var2.n()) && this.X == var2.o() && Objects.equals(this.i, var2.V());
      }
   }

   public int hashCode() {
      int var1 = super.hashCode();
      var1 = 31 * var1 + this.v.hashCode();
      var1 = 31 * var1 + Boolean.hashCode(this.X);
      var1 = 31 * var1 + Objects.hashCode(this.i);
      return var1;
   }
}
