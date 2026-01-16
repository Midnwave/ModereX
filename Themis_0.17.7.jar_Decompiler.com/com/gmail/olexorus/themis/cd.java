package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

final class CD implements Cd {
   private final char e;
   private final B5 C;
   private final boolean W;
   private static final long b = kt.a(4634022476771283618L, -4588412630603098810L, MethodHandles.lookup().lookupClass()).a(239959528449527L);

   CD(char var1, B5 var2, boolean var3) {
      long var4 = b ^ 52662226700974L;
      super();
      this.e = var1;
      this.C = (B5)Objects.requireNonNull(var2, "format");
      this.W = var3;
   }

   public char A() {
      return this.e;
   }

   public B5 T() {
      return this.C;
   }

   public boolean t() {
      return this.W;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof CD)) {
         return false;
      } else {
         CD var2 = (CD)var1;
         return this.e == var2.e && this.C.equals(var2.C) && this.W == var2.W;
      }
   }

   public int hashCode() {
      char var1 = this.e;
      int var2 = 31 * var1 + this.C.hashCode();
      var2 = 31 * var2 + Boolean.hashCode(this.W);
      return var2;
   }

   public String toString() {
      return cH.M(this);
   }
}
