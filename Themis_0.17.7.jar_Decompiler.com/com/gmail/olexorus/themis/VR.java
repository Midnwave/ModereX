package com.gmail.olexorus.themis;

import java.util.Objects;

public class vR {
   private RA h;
   private EZ z;

   public vR(RA var1, EZ var2) {
      this.h = var1;
      this.z = var2;
   }

   public static vR v(lm<?> var0) {
      RA var1 = (RA)var0.i((VD)CV.C(), (MO)(RA::U));
      EZ var2 = EZ.b(var0);
      return new vR(var1, var2);
   }

   public static void n(lm<?> var0, vR var1) {
      var0.M(var1.h, RA::C);
      EZ.M(var0, var1.z);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof vR)) {
         return false;
      } else {
         vR var2 = (vR)var1;
         if (!this.h.equals(var2.h)) {
            return false;
         } else {
            return this.z == var2.z;
         }
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.h, this.z});
   }
}
