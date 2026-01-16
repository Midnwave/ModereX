package com.gmail.olexorus.themis;

public abstract class us<T extends us<?>> {
   protected final Wu<T> A;

   public us(Wu<T> var1) {
      this.A = var1;
   }

   public static us<?> e(lm<?> var0) {
      return ((Wu)var0.y((VD)vD.c())).j(var0);
   }

   public static <T extends us<?>> void N(lm<?> var0, T var1) {
      var0.j((GL)var1.Q());
      var1.Q().i(var0, var1);
   }

   public Wu<T> Q() {
      return this.A;
   }
}
