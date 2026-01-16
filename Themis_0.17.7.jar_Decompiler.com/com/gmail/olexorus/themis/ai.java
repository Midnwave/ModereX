package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public class Ai {
   private final String N;
   private final Bi R;
   private static final long a = kt.a(7830647371562721985L, 1577193221407297634L, MethodHandles.lookup().lookupClass()).a(168794287662643L);

   public Ai(String var1, Bi var2) {
      this.N = var1;
      this.R = var2;
   }

   public static Ai Z(Rc var0, lm<?> var1) {
      long var2 = a ^ 23558377533924L;
      RT var4 = (RT)var0;
      String var5 = var4.N("key");
      Bi var6 = Bi.c(var4, var1);
      return new Ai(var5, var6);
   }

   public static Rc e(lm<?> var0, Ai var1) {
      long var2 = a ^ 33097745594913L;
      RT var4 = new RT();
      var4.j("key", new mZ(var1.N));
      Bi.M(var4, var0, var1.R);
      return var4;
   }
}
