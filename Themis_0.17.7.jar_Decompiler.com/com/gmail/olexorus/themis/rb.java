package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

public abstract class Rb<FT> {
   private final List<FT> g = new ArrayList();
   private static final long a = kt.a(-4828450226153642513L, -7814974998476286270L, MethodHandles.lookup().lookupClass()).a(45806823761745L);

   public Rb(FT... var1) {
      this.g.addAll(Arrays.asList(var1));
   }

   public FT W(int var1) {
      if (var1 > 0) {
         --var1;
      } else {
         var1 = 0;
      }

      Object var2 = this.g.get(var1);
      if (var2 == null) {
         var2 = this.R();
      }

      return var2;
   }

   public FT R() {
      return this.W(1);
   }

   abstract String Q(FT var1, String var2);

   public String g(int var1, String var2) {
      return this.Q(this.W(var1), var2);
   }

   public String j(String var1) {
      long var2 = a ^ 23421190013401L;
      String var4 = this.g(1, "");
      Matcher var5 = nQ.M.matcher(var1);
      StringBuffer var6 = new StringBuffer(var1.length());

      while(var5.find()) {
         Integer var7 = Oa.L(var5.group("color"), 1);
         String var8 = this.g(var7, var5.group("msg")) + var4;
         var5.appendReplacement(var6, Matcher.quoteReplacement(var8));
      }

      var5.appendTail(var6);
      return var4 + var6.toString();
   }
}
