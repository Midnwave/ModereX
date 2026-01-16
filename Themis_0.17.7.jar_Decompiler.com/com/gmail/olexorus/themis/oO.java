package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public final class oo {
   private final Integer x;
   private final Integer Z;
   private static final long a = kt.a(5942488634960633131L, 1468498849006064932L, MethodHandles.lookup().lookupClass()).a(170610021474412L);

   public oo(Integer var1, Integer var2) {
      this.x = var1;
      this.Z = var2;
   }

   public static oo v(Rc var0, lm<?> var1) {
      long var2 = a ^ 96710987390364L;
      RT var4 = (RT)var0;
      Number var5 = var4.M("max_lines");
      Number var6 = var4.M("height");
      return new oo(var5 != null ? var5.intValue() : null, var6 != null ? var6.intValue() : null);
   }

   public static Rc Z(lm<?> var0, oo var1) {
      long var2 = a ^ 27815499075735L;
      RT var4 = new RT();
      if (var1.x != null) {
         var4.j("max_lines", new mz(var1.x));
      }

      if (var1.Z != null) {
         var4.j("height", new mz(var1.Z));
      }

      return var4;
   }
}
