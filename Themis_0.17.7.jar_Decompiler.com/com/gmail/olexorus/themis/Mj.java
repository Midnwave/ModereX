package com.gmail.olexorus.themis;

import java.util.Objects;

public class mj {
   private float B;

   public mj(float var1) {
      this.B = var1;
   }

   public static mj i(lm<?> var0) {
      float var1 = var0.L();
      return new mj(var1);
   }

   public static void u(lm<?> var0, mj var1) {
      var0.S(var1.B);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof mj)) {
         return false;
      } else {
         mj var2 = (mj)var1;
         return Float.compare(var2.B, this.B) == 0;
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.B);
   }
}
