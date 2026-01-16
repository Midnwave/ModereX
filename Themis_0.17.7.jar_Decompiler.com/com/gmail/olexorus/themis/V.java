package com.gmail.olexorus.themis;

import java.util.Objects;

final class v implements mx {
   private final String R;
   private final String u;
   private final String r;

   v(String var1, String var2, String var3) {
      this.R = var1;
      this.u = var2;
      this.r = var3;
   }

   public String g() {
      return this.R;
   }

   public String l() {
      return this.u;
   }

   public String K() {
      return this.r;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof v)) {
         return false;
      } else {
         v var2 = (v)var1;
         return Objects.equals(this.R, var2.R) && Objects.equals(this.u, var2.u) && Objects.equals(this.r, var2.r);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.R, this.u, this.r});
   }

   public String toString() {
      return cH.M(this);
   }
}
