package com.gmail.olexorus.themis;

public class vl {
   public static boolean J(Throwable var0, Class<?> var1) {
      while(var0 != null) {
         if (var1.isAssignableFrom(var0.getClass())) {
            return true;
         }

         var0 = var0.getCause();
      }

      return false;
   }
}
