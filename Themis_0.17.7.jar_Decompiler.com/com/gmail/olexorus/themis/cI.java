package com.gmail.olexorus.themis;

import java.util.Optional;

public final class Ci {
   private final i x;
   private final int J;
   private final V1 h;

   public Ci(i var1, int var2, V1 var3) {
      this.x = var1;
      this.J = var2;
      this.h = var3;
   }

   public static Optional<Ci> T(lm<?> var0) {
      return Optional.ofNullable(f(var0));
   }

   public static Ci f(lm<?> var0) {
      if (!var0.P()) {
         return null;
      } else {
         i var1 = (i)var0.y((VD)z1.N());
         int var2 = var0.Q();
         V1 var3 = V1.V(var0);
         return new Ci(var1, var2, var3);
      }
   }

   public static void I(lm<?> var0, Optional<Ci> var1) {
      I(var0, (Ci)var1.orElse((Object)null));
   }

   public static void I(lm<?> var0, Ci var1) {
      if (var1 == null) {
         var0.I(false);
      } else {
         var0.I(true);
         var0.j((GL)var1.x);
         var0.E(var1.J);
         V1.D(var0, var1.h);
      }

   }
}
