package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public class e implements T9 {
   private final ur<?> h;
   private final R9 a;
   private static final long b = kt.a(4541664884374003076L, -2010661808274684447L, MethodHandles.lookup().lookupClass()).a(161309570859590L);

   public e(R9 var1) {
      long var2 = b ^ 885603488397L;
      super();
      if (!var1.w().H()) {
         throw new IllegalArgumentException("Can't create action for unreadable click event with action " + var1.w());
      } else {
         this.h = (ur)CY.z().M(var1.w().f());
         this.a = var1;
      }
   }

   public static e f(RT var0, lm<?> var1) {
      long var2 = b ^ 34500791569453L;
      String var4 = var0.N("type");
      Ov var5 = (Ov)vs.S().N(var4);
      R9 var6 = var5.E(var0, var1);
      return new e(var6);
   }

   public static void c(RT var0, lm<?> var1, e var2) {
      var2.a.w().D(var0, var1, var2.a);
   }

   public ur<?> W() {
      return this.h;
   }
}
