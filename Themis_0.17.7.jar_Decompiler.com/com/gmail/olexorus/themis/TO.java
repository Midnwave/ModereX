package com.gmail.olexorus.themis;

public abstract class tO<T extends tO<?>> {
   protected final rL<T> x;

   public tO(rL<T> var1) {
      this.x = var1;
   }

   public static tO<?> X(lm<?> var0) {
      return ((rL)var0.y((VD)B_.o())).I(var0);
   }

   public static <T extends tO<?>> void A(lm<?> var0, T var1) {
      var0.j((GL)var1.B());
      var1.B().R(var0, var1);
   }

   public rL<T> B() {
      return this.x;
   }
}
