package com.gmail.olexorus.themis;

import java.util.List;
import java.util.Objects;

public class wJ {
   private mn<String> W;
   private String P;
   private int v;
   private List<mn<X>> b;
   private boolean M;

   public wJ(mn<String> var1, String var2, int var3, List<mn<X>> var4, boolean var5) {
      this.W = var1;
      this.P = var2;
      this.v = var3;
      this.b = var4;
      this.M = var5;
   }

   public static wJ Q(lm<?> var0) {
      mn var1 = mn.F(var0, wJ::lambda$read$0);
      String var2 = var0.A();
      int var3 = var0.Q();
      List var4 = var0.j(wJ::lambda$read$1);
      boolean var5 = var0.P();
      return new wJ(var1, var2, var3, var4, var5);
   }

   public static void E(lm<?> var0, wJ var1) {
      mn.k(var0, var1.W, wJ::lambda$write$2);
      var0.I(var1.P);
      var0.E(var1.v);
      var0.D(var1.b, wJ::lambda$write$3);
      var0.I(var1.M);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof wJ)) {
         return false;
      } else {
         wJ var2 = (wJ)var1;
         if (this.v != var2.v) {
            return false;
         } else if (this.M != var2.M) {
            return false;
         } else if (!this.W.equals(var2.W)) {
            return false;
         } else {
            return !this.P.equals(var2.P) ? false : this.b.equals(var2.b);
         }
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.W, this.P, this.v, this.b, this.M});
   }

   private static void lambda$write$3(lm var0, mn var1) {
      mn.k(var0, var1, lm::G);
   }

   private static void lambda$write$2(lm var0, String var1) {
      var0.a(var1, 32);
   }

   private static mn lambda$read$1(lm var0) {
      return mn.F(var0, lm::a);
   }

   private static String lambda$read$0(lm var0) {
      return var0.m(32);
   }
}
