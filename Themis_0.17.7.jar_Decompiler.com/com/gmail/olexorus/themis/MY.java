package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public class mY {
   private final X H;
   private final int Y;
   private static final long a = kt.a(-7162900934008612766L, 6082539044931114665L, MethodHandles.lookup().lookupClass()).a(181342617472503L);

   public mY(X var1, int var2) {
      this.H = var1;
      this.Y = var2;
   }

   public static mY V(Rc var0, lm<?> var1) {
      return Q((RT)var0, var1);
   }

   public static mY Q(RT var0, lm<?> var1) {
      long var2 = a ^ 108771100596603L;
      X var4 = (X)var0.g("contents", h.z(var1), var1);
      int var5 = var0.L("width", 200).intValue();
      return new mY(var4, var5);
   }

   public static Rc e(lm<?> var0, mY var1) {
      RT var2 = new RT();
      l(var2, var0, var1);
      return var2;
   }

   public static void l(RT var0, lm<?> var1, mY var2) {
      long var3 = a ^ 115574919126447L;
      var0.X("contents", var2.H, h.z(var1), var1);
      if (var2.Y != 200) {
         var0.j("width", new mz(var2.Y));
      }

   }
}
