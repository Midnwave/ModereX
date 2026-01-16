package com.gmail.olexorus.themis;

public final class rq<T> {
   private final T d;
   private final int q;

   public rq(T var1, int var2) {
      this.d = var1;
      this.q = var2;
   }

   public static <T> tw<rq<T>> G(tw<T> var0) {
      return (new b5(var0)).I();
   }

   public T l() {
      return this.d;
   }

   static int O(rq var0) {
      return var0.q;
   }

   static Object w(rq var0) {
      return var0.d;
   }
}
