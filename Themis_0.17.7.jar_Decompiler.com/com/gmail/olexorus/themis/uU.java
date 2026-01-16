package com.gmail.olexorus.themis;

public class uu<T, A> {
   private final bA<T, A> M;
   private final mP<A> a;

   public uu(bA<T, A> var1, mP<A> var2) {
      this.M = var1;
      this.a = var2;
   }

   public static <T> tw<uu<T, ?>> S(Jf<T> var0) {
      tw var1 = var0.l().v();
      return (new m4(var1, var0)).I();
   }

   public static <T, A> bg<uu<T, A>> f(Jf<T> var0, bA<T, A> var1) {
      return mP.D(var1.F(var0)).J(uu::lambda$codec$0, uu::j);
   }

   public mP<A> j() {
      return this.a;
   }

   private static uu lambda$codec$0(bA var0, mP var1) {
      return new uu(var0, var1);
   }

   static bA d(uu var0) {
      return var0.M;
   }
}
