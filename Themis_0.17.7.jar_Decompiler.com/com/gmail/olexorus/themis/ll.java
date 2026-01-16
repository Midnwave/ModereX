package com.gmail.olexorus.themis;

import java.util.Objects;

public class lL {
   private ap o;

   public lL(ap var1) {
      this.o = var1;
   }

   public static lL I(lm<?> var0) {
      ap var1 = (ap)var0.y((VD)wV.B());
      return new lL(var1);
   }

   public static void a(lm<?> var0, lL var1) {
      var0.j((GL)var1.o);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof lL)) {
         return false;
      } else {
         lL var2 = (lL)var1;
         return this.o.equals(var2.o);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.o);
   }
}
