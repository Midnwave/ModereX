package com.gmail.olexorus.themis;

import java.util.Objects;

public class tA {
   private int a;
   private boolean p;

   public tA(int var1, boolean var2) {
      this.a = var1;
      this.p = var2;
   }

   public static tA m(lm<?> var0) {
      int var1 = var0.f();
      boolean var2 = var0.R().i(zZ.V_1_21_5) || var0.P();
      return new tA(var1, var2);
   }

   public static void M(lm<?> var0, tA var1) {
      var0.L(var1.a);
      if (var0.R().m(zZ.V_1_21_5)) {
         var0.I(var1.p);
      }

   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof tA)) {
         return false;
      } else {
         tA var2 = (tA)var1;
         if (this.a != var2.a) {
            return false;
         } else {
            return this.p == var2.p;
         }
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.a, this.p});
   }
}
