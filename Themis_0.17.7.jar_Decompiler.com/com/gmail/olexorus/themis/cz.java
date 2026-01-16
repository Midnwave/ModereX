package com.gmail.olexorus.themis;

import java.util.Objects;

public class cZ extends id implements CW {
   private final al v;
   private final Float h;

   public cZ(al var1, Float var2) {
      this((z2)null, var1, var2);
   }

   public cZ(z2 var1, al var2, Float var3) {
      super(var1);
      this.v = var2;
      this.h = var3;
   }

   public al l() {
      return this.v;
   }

   public Float S() {
      return this.h;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof cZ)) {
         return false;
      } else {
         cZ var2 = (cZ)var1;
         if (this.z()) {
            return super.equals(var1);
         } else {
            return !this.v.equals(var2.v) ? false : Objects.equals(this.h, var2.h);
         }
      }
   }

   public int hashCode() {
      return this.z() ? super.hashCode() : Objects.hash(new Object[]{this.v, this.h});
   }
}
