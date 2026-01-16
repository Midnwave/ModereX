package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.regex.Matcher;

public interface Ma extends nc {
   long a = kt.a(7616154538899161187L, -3267342882132225457L, MethodHandles.lookup().lookupClass()).a(198186432794923L);

   static Ma b(String var0) {
      long var1 = a ^ 105630011107235L;
      Matcher var3 = uA.R.matcher(var0);
      if (var3.matches()) {
         return cF.F(Double.parseDouble(var3.group(1)), Double.parseDouble(var3.group(3)), Double.parseDouble(var3.group(5)));
      } else {
         Matcher var4 = uA.a.matcher(var0);
         if (var4.matches()) {
            return n8.k(uA.v(var4.group(1), var4.group(2)), uA.v(var4.group(3), var4.group(4)), uA.v(var4.group(5), var4.group(6)));
         } else {
            throw new IllegalArgumentException("Cannot convert position specification '" + var0 + "' into a position");
         }
      }
   }

   String G();
}
