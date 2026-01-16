package com.gmail.olexorus.themis;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;

public final class m9<A, B, C> implements Serializable {
   private final A B;
   private final B v;
   private final C y;
   private static final long a = kt.a(-807042877456854685L, 8581591116248343270L, MethodHandles.lookup().lookupClass()).a(10358160825440L);

   public m9(A var1, B var2, C var3) {
      this.B = var1;
      this.v = var2;
      this.y = var3;
   }

   public final A C() {
      return this.B;
   }

   public final B N() {
      return this.v;
   }

   public final C Y() {
      return this.y;
   }

   public String toString() {
      long var1 = a ^ 14917901614060L;
      return "" + '(' + this.B + ", " + this.v + ", " + this.y + ')';
   }

   public int hashCode() {
      int var1 = this.B == null ? 0 : this.B.hashCode();
      var1 = var1 * 31 + (this.v == null ? 0 : this.v.hashCode());
      var1 = var1 * 31 + (this.y == null ? 0 : this.y.hashCode());
      return var1;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof m9)) {
         return false;
      } else {
         m9 var2 = (m9)var1;
         if (!bU.I(this.B, var2.B)) {
            return false;
         } else if (!bU.I(this.v, var2.v)) {
            return false;
         } else {
            return bU.I(this.y, var2.y);
         }
      }
   }
}
