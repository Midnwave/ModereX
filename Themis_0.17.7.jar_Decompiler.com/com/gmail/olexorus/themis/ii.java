package com.gmail.olexorus.themis;

import java.util.Objects;

public class iI extends id implements zl {
   private final gv v;
   private final al I;

   public iI(gv var1, al var2) {
      this((z2)null, var1, var2);
   }

   public iI(z2 var1, gv var2, al var3) {
      super(var1);
      this.v = var2;
      this.I = var3;
   }

   public zl e(z2 var1) {
      return new iI(var1, this.v, this.I);
   }

   public gv k() {
      return this.v;
   }

   public al t() {
      return this.I;
   }

   public boolean I(Object var1) {
      if (var1 != null && this.getClass() == var1.getClass()) {
         if (!super.equals(var1)) {
            return false;
         } else {
            iI var2 = (iI)var1;
            return this.v != var2.v ? false : this.I.equals(var2.I);
         }
      } else {
         return false;
      }
   }

   public int z() {
      return Objects.hash(new Object[]{super.hashCode(), this.v, this.I});
   }
}
