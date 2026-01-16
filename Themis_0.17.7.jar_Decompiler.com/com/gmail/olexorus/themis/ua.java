package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.regex.Pattern;

final class uA {
   static final Pattern R;
   static final Pattern a;

   static mG v(String var0, String var1) {
      int var2 = Integer.parseInt(var1);
      if (var0.equals("")) {
         return mG.L(var2);
      } else if (var0.equals("~")) {
         return mG.J(var2);
      } else {
         throw new AssertionError();
      }
   }

   static String y(double var0) {
      return "^" + var0;
   }

   static String g(mG var0) {
      return (var0.t() == ne.RELATIVE ? "~" : "") + var0.x();
   }

   static {
      long var0 = kt.a(-8566799937519552725L, 5279370905416534245L, MethodHandles.lookup().lookupClass()).a(115373126145212L) ^ 29473326773727L;
      R = Pattern.compile("^\\^(-?\\d+(\\.\\d+)?) \\^(-?\\d+(\\.\\d+)?) \\^(-?\\d+(\\.\\d+)?)$");
      a = Pattern.compile("^(~?)(-?\\d+) (~?)(-?\\d+) (~?)(-?\\d+)$");
   }
}
