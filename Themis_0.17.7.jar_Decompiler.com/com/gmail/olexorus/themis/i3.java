package com.gmail.olexorus.themis;

import java.util.Objects;

public class i3 extends id implements W7 {
   private final al j;

   public i3(al var1) {
      this((z2)null, var1);
   }

   public i3(z2 var1, al var2) {
      super(var1);
      this.j = var2;
   }

   public W7 G(z2 var1) {
      return new i3(var1, this.j);
   }

   public al R() {
      return this.j;
   }

   public boolean I(Object var1) {
      if (!(var1 instanceof i3)) {
         return false;
      } else if (!super.equals(var1)) {
         return false;
      } else {
         i3 var2 = (i3)var1;
         return this.j.equals(var2.j);
      }
   }

   public int z() {
      return Objects.hash(new Object[]{super.hashCode(), this.j});
   }
}
