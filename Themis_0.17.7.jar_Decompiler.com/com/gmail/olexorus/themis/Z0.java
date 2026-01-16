package com.gmail.olexorus.themis;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;

public final class z0<A, B> implements Serializable {
   private final A H;
   private final B e;
   private static final long a = kt.a(1978223868993418603L, -6005036679192607095L, MethodHandles.lookup().lookupClass()).a(229011422881641L);

   public z0(A var1, B var2) {
      this.H = var1;
      this.e = var2;
   }

   public final A p() {
      return this.H;
   }

   public final B S() {
      return this.e;
   }

   public String toString() {
      long var1 = a ^ 100709321474411L;
      return "" + '(' + this.H + ", " + this.e + ')';
   }

   public final A A() {
      return this.H;
   }

   public final B k() {
      return this.e;
   }

   public int hashCode() {
      int var1 = this.H == null ? 0 : this.H.hashCode();
      var1 = var1 * 31 + (this.e == null ? 0 : this.e.hashCode());
      return var1;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof z0)) {
         return false;
      } else {
         z0 var2 = (z0)var1;
         if (!bU.I(this.H, var2.H)) {
            return false;
         } else {
            return bU.I(this.e, var2.e);
         }
      }
   }
}
