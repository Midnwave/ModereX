package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.function.BiConsumer;

public abstract class ue {
   private final String P;
   private static final long b = kt.a(-4100585080198808950L, -3546262822969947574L, MethodHandles.lookup().lookupClass()).a(123085992659669L);

   protected ue(String var1) {
      long var2 = b ^ 36003063236113L;
      super();
      if (var1 == null) {
         throw new IllegalArgumentException("chartId must not be null");
      } else {
         this.P = var1;
      }
   }

   public nl i(BiConsumer<String, Throwable> var1, boolean var2) {
      long var3 = b ^ 79486436127884L;
      t3 var5 = new t3();
      var5.X("chartId", this.P);

      try {
         nl var6 = this.Y();
         if (var6 == null) {
            return null;
         }

         var5.P("data", var6);
      } catch (Throwable var7) {
         if (var2) {
            var1.accept("Failed to get data for custom chart with id " + this.P, var7);
         }

         return null;
      }

      return var5.I();
   }

   protected abstract nl Y();
}
