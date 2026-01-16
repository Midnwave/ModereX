package com.gmail.olexorus.themis;

import java.util.Objects;

public class Az {
   private Mx D;

   public Az(Mx var1) {
      this.D = var1;
   }

   public static Az V(lm<?> var0) {
      Mx var1 = (Mx)var0.y((VD)bI.H());
      return new Az(var1);
   }

   public static void X(lm<?> var0, Az var1) {
      var0.j((GL)var1.D);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Az)) {
         return false;
      } else {
         Az var2 = (Az)var1;
         return this.D.equals(var2.D);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.D);
   }
}
