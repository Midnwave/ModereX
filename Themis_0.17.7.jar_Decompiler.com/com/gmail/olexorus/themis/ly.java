package com.gmail.olexorus.themis;

public final class lY {
   private final b8<?> X;
   private final float N;
   private final float p;

   public lY(b8<?> var1, float var2, float var3) {
      this.X = var1;
      this.N = var2;
      this.p = var3;
   }

   public static lY O(lm<?> var0) {
      b8 var1 = b8.Z(var0);
      float var2 = var0.L();
      float var3 = var0.L();
      return new lY(var1, var2, var3);
   }

   public static void U(lm<?> var0, lY var1) {
      b8.M(var0, var1.X);
      var0.S(var1.N);
      var0.S(var1.p);
   }
}
