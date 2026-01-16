package com.gmail.olexorus.themis;

import java.util.function.Function;

public final class r4<T, A> {
   private final A K;
   private final bA<T, A> D;

   public r4(A var1, bA<T, A> var2) {
      this.K = var1;
      this.D = var2;
   }

   public static <T> r4<T, T> D(T var0) {
      return new r4(var0, bA.v());
   }

   public static <T> tw<r4<T, ?>> V(Jf<T> var0) {
      tw var1 = var0.l().S();
      tw var2 = var0.l().v();
      return g9.Z(var1, (new zu(var2, var0)).I()).K(r4::lambda$codec$0, r4::lambda$codec$1);
   }

   public boolean L() {
      return this.D == bA.v();
   }

   public T W(T var1) {
      return this.D.J(var1, this.K);
   }

   private static E_ lambda$codec$1(r4 var0) {
      if (var0.L()) {
         Object var1 = var0.K;
         return E_.u(var1);
      } else {
         return E_.Y(var0);
      }
   }

   private static r4 lambda$codec$0(E_ var0) {
      return (r4)var0.R(r4::D, Function.identity());
   }

   static bA M(r4 var0) {
      return var0.D;
   }

   static Object q(r4 var0) {
      return var0.K;
   }
}
