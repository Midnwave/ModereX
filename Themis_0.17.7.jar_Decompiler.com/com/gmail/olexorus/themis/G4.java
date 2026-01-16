package com.gmail.olexorus.themis;

import java.util.List;
import java.util.Objects;

public class g4 {
   private List<mn<String>> P;

   public g4(List<mn<String>> var1) {
      this.P = var1;
   }

   public static g4 M(lm<?> var0) {
      List var1 = var0.j(g4::lambda$read$1);
      return new g4(var1);
   }

   public static void H(lm<?> var0, g4 var1) {
      var0.D(var1.P, g4::lambda$write$3);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof g4)) {
         return false;
      } else {
         g4 var2 = (g4)var1;
         return this.P.equals(var2.P);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.P);
   }

   private static void lambda$write$3(lm var0, mn var1) {
      mn.k(var0, var1, g4::lambda$write$2);
   }

   private static void lambda$write$2(lm var0, String var1) {
      var0.a(var1, 1024);
   }

   private static mn lambda$read$1(lm var0) {
      return mn.F(var0, g4::lambda$read$0);
   }

   private static String lambda$read$0(lm var0) {
      return var0.m(1024);
   }
}
