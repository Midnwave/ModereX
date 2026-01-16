package com.gmail.olexorus.themis;

import java.util.Objects;

public class iH extends id implements cy {
   private final al H;

   public iH(al var1) {
      this((z2)null, var1);
   }

   public iH(z2 var1, al var2) {
      super(var1);
      this.H = var2;
   }

   public cy N(z2 var1) {
      return new iH(var1, this.H);
   }

   public al Z() {
      return this.H;
   }

   public boolean I(Object var1) {
      if (!(var1 instanceof iH)) {
         return false;
      } else if (!super.equals(var1)) {
         return false;
      } else {
         iH var2 = (iH)var1;
         return this.H.equals(var2.H);
      }
   }

   public int z() {
      return Objects.hash(new Object[]{super.hashCode(), this.H});
   }
}
