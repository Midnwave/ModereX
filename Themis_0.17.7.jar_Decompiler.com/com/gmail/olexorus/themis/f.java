package com.gmail.olexorus.themis;

import java.util.Objects;

public final class F<T> {
   private final bo<T> D;
   private final T g;

   public F(bo<T> var1, T var2) {
      this.D = var1;
      this.g = var2;
   }

   public static F<?> v(lm<?> var0) {
      bo var1 = (bo)var0.y((VD)Tj.Z());
      Objects.requireNonNull(var1);
      Object var2 = var0.u(var1::j);
      return new F(var1, var2);
   }

   public static <T> void T(lm<?> var0, F<T> var1) {
      var0.j((GL)var1.D);
      Object var10001 = var1.g;
      bo var10002 = var1.D;
      Objects.requireNonNull(var10002);
      var0.l(var10001, var10002::e);
   }
}
