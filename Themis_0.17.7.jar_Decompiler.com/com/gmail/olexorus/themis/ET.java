package com.gmail.olexorus.themis;

public final class Et {
   public static String D(String var0, int var1) {
      if (var0.length() <= var1) {
         return var0;
      } else {
         return var0.charAt(var1 - 1) == 167 ? var0.substring(0, var1 - 1) : var0.substring(0, var1);
      }
   }
}
