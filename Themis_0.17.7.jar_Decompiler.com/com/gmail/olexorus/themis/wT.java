package com.gmail.olexorus.themis;

import java.util.Objects;

public class WT {
   private Nf S;

   public WT(Nf var1) {
      this.S = var1;
   }

   public static WT F(lm<?> var0) {
      Nf var1 = (Nf)var0.y((VD)ge.B());
      return new WT(var1);
   }

   public static void L(lm<?> var0, WT var1) {
      var0.j((GL)var1.S);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof WT)) {
         return false;
      } else {
         WT var2 = (WT)var1;
         return this.S.equals(var2.S);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.S);
   }
}
