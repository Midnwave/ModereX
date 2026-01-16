package com.gmail.olexorus.themis;

import java.util.Objects;

final class zV implements wT {
   private final v1 M;
   private final v1 i;

   zV(v1 var1, v1 var2) {
      this.M = var1;
      this.i = var2;
   }

   public v1 L() {
      return this.M;
   }

   public v1 E() {
      return this.i;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof wT)) {
         return false;
      } else {
         zV var2 = (zV)var1;
         return Objects.equals(this.M, var2.L()) && Objects.equals(this.i, var2.E());
      }
   }

   public int hashCode() {
      int var1 = this.M.hashCode();
      var1 = 31 * var1 + this.i.hashCode();
      return var1;
   }

   public String toString() {
      return cH.M(this);
   }
}
