package com.gmail.olexorus.themis;

import java.util.Objects;

public class i9 extends id implements Wb {
   private final rt b;
   private final al a;

   public i9(rt var1, al var2) {
      this((z2)null, var1, var2);
   }

   public i9(z2 var1, rt var2, al var3) {
      super(var1);
      this.b = var2;
      this.a = var3;
   }

   public Wb j(z2 var1) {
      return new i9(var1, this.b, this.a);
   }

   public rt N() {
      return this.b;
   }

   public al M() {
      return this.a;
   }

   public boolean I(Object var1) {
      if (!(var1 instanceof i9)) {
         return false;
      } else if (!super.equals(var1)) {
         return false;
      } else {
         i9 var2 = (i9)var1;
         return !this.b.equals(var2.b) ? false : this.a.equals(var2.a);
      }
   }

   public int z() {
      return Objects.hash(new Object[]{super.hashCode(), this.b, this.a});
   }
}
