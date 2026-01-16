package com.gmail.olexorus.themis;

import java.util.Objects;

public class nX {
   private boolean h;

   public nX() {
      this(true);
   }

   public nX(boolean var1) {
      this.h = var1;
   }

   public static nX A(lm<?> var0) {
      if (var0.R().i(zZ.V_1_21_5)) {
         return new nX();
      } else {
         boolean var1 = var0.P();
         return new nX(var1);
      }
   }

   public static void c(lm<?> var0, nX var1) {
      if (var0.R().m(zZ.V_1_21_5)) {
         var0.I(var1.h);
      }

   }

   public boolean c() {
      return this.h;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof nX)) {
         return false;
      } else {
         nX var2 = (nX)var1;
         return this.h == var2.h;
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.h);
   }
}
