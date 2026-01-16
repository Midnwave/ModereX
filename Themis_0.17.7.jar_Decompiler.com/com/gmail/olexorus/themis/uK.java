package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public class uk {
   private final Vy D;
   private final T9 J;
   private static final long a = kt.a(-2638190503224637532L, -1514190500239328789L, MethodHandles.lookup().lookupClass()).a(117029615998678L);

   public uk(Vy var1, T9 var2) {
      this.D = var1;
      this.J = var2;
   }

   public static uk x(Rc var0, lm<?> var1) {
      long var2 = a ^ 10728619236803L;
      RT var4 = (RT)var0;
      Vy var5 = Vy.r(var4, var1);
      T9 var6 = (T9)var4.M("action", T9::b, var1);
      return new uk(var5, var6);
   }

   public static Rc m(lm<?> var0, uk var1) {
      long var2 = a ^ 32567972646554L;
      RT var4 = new RT();
      Vy.i(var4, var0, var1.D);
      if (var1.J != null) {
         var4.X("action", var1.J, T9::y, var0);
      }

      return var4;
   }
}
