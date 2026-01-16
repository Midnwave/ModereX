package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public class J implements R9 {
   private final al n;
   private final Rc w;
   private static final long a = kt.a(-6270707220674979734L, -154908416269220668L, MethodHandles.lookup().lookupClass()).a(189456310007388L);

   public J(al var1, Rc var2) {
      this.n = var1;
      this.w = var2;
   }

   public static J n(RT var0, lm<?> var1) {
      long var2 = a ^ 68384251188665L;
      al var4 = (al)var0.g("id", al::o, var1);
      Rc var5 = var0.M("payload");
      return new J(var4, var5);
   }

   public static void G(RT var0, lm<?> var1, J var2) {
      long var3 = a ^ 22960738588589L;
      var0.X("id", var2.n, al::c, var1);
      if (var2.w != null) {
         var0.j("payload", var2.w);
      }

   }

   public Ov<?> w() {
      return vs.c;
   }
}
