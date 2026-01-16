package com.gmail.olexorus.themis;

import java.util.Objects;

public class gS {
   private z_ K;

   public gS(z_ var1) {
      this.K = var1;
   }

   public static gS l(lm<?> var0) {
      z_ var1 = (z_)var0.y((VD)gr.g());
      return new gS(var1);
   }

   public static void M(lm<?> var0, gS var1) {
      var0.j((GL)var1.K);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof gS)) {
         return false;
      } else {
         gS var2 = (gS)var1;
         return this.K.equals(var2.K);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.K);
   }
}
