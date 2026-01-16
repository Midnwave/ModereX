package com.gmail.olexorus.themis;

import java.util.Objects;

public class c0 {
   private W7 P;

   public c0(W7 var1) {
      this.P = var1;
   }

   public static c0 i(lm<?> var0) {
      W7 var1 = (W7)var0.y((VD)G_.C());
      return new c0(var1);
   }

   public static void h(lm<?> var0, c0 var1) {
      var0.j((GL)var1.P);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof c0)) {
         return false;
      } else {
         c0 var2 = (c0)var1;
         return this.P.equals(var2.P);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.P);
   }
}
