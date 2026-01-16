package com.gmail.olexorus.themis;

public final class G8<T> {
   private final bo<T> l;
   private final T Q;

   public G8(bo<T> var1, T var2) {
      this.l = var1;
      this.Q = var2;
   }

   public static G8<?> F(lm<?> var0) {
      bo var1 = (bo)var0.y((VD)Tj.Z());
      Object var2 = var1.j(var0);
      return new G8(var1, var2);
   }

   public static <T> void O(lm<?> var0, G8<T> var1) {
      var0.j((GL)var1.l);
      var1.l.e(var0, var1.Q);
   }
}
