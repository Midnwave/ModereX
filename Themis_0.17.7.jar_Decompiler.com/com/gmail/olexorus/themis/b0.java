package com.gmail.olexorus.themis;

public final class B0 {
   private final int E;
   private final int w;
   private final int h;
   private final int J;
   private final int L;
   private final int k;

   public B0(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.E = Math.min(var1, var4);
      this.w = Math.min(var2, var5);
      this.h = Math.min(var3, var6);
      this.J = Math.max(var4, var1);
      this.L = Math.max(var5, var2);
      this.k = Math.max(var6, var3);
   }

   public static B0 u(lm<?> var0) {
      VC var1 = var0.M();
      VC var2 = var0.M();
      return new B0(var1.Q, var1.R, var1.e, var2.Q, var2.R, var2.e);
   }

   public static void k(lm<?> var0, B0 var1) {
      var0.o(new VC(var1.E, var1.w, var1.h));
      var0.o(new VC(var1.J, var1.L, var1.k));
   }
}
