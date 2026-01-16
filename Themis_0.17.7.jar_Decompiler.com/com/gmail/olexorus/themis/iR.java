package com.gmail.olexorus.themis;

import java.util.Objects;

public class ir extends id implements n {
   private final tb g;
   private final al x;

   public ir(tb var1, al var2) {
      this((z2)null, var1, var2);
   }

   public ir(z2 var1, tb var2, al var3) {
      super(var1);
      this.g = var2;
      this.x = var3;
   }

   public n w(z2 var1) {
      return new ir(var1, this.g, this.x);
   }

   public tb m() {
      return this.g;
   }

   public al K() {
      return this.x;
   }

   public boolean I(Object var1) {
      if (!(var1 instanceof ir)) {
         return false;
      } else if (!super.equals(var1)) {
         return false;
      } else {
         ir var2 = (ir)var1;
         return !this.g.equals(var2.g) ? false : this.x.equals(var2.x);
      }
   }

   public int z() {
      return Objects.hash(new Object[]{super.hashCode(), this.g, this.x});
   }
}
