package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

final class Jy {
   public static final Jy w;
   public static final Method m;
   public static final Method J;

   private Jy() {
   }

   static {
      long var0 = kt.a(3490660370715997254L, -2964224998704443100L, MethodHandles.lookup().lookupClass()).a(53403146829329L) ^ 40354389902927L;
      w = new Jy();
      Class var2 = Throwable.class;
      Method[] var3 = var2.getMethods();
      Method[] var4 = var3;
      int var5 = 0;
      int var6 = var3.length;

      Method var10000;
      Method var7;
      boolean var9;
      while(true) {
         if (var5 >= var6) {
            var10000 = null;
            break;
         }

         var7 = var4[var5];
         var9 = false;
         if (bU.I(var7.getName(), "addSuppressed") && bU.I(Ej.w((Object[])var7.getParameterTypes()), var2)) {
            var10000 = var7;
            break;
         }

         ++var5;
      }

      m = var10000;
      var4 = var3;
      var5 = 0;
      var6 = var3.length;

      while(true) {
         if (var5 >= var6) {
            var10000 = null;
            break;
         }

         var7 = var4[var5];
         var9 = false;
         if (bU.I(var7.getName(), "getSuppressed")) {
            var10000 = var7;
            break;
         }

         ++var5;
      }

      J = var10000;
   }
}
