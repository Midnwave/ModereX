package com.gmail.olexorus.themis;

import java.util.function.BiConsumer;
import java.util.function.Function;

final class J6 implements ll {
   private final Vv<X, NC> e;
   private Function<X, String> x;
   private int A;

   J6() {
      this.A = (Integer)ud.h.x(-1);
      this.e = wt.k().T(true);
   }

   public wb A() {
      return new nB((wt)this.e.u(), this.x, this.A);
   }

   public <T extends X> ll w(Class<T> var1, Function<T, String> var2) {
      this.e.A(var1, J6::lambda$mapper$0);
      return this;
   }

   private static void lambda$complexMapper$2(BiConsumer var0, nB var1, X var2, Tg var3, int var4, int var5) {
      var0.accept(var2, J6::lambda$complexMapper$1);
   }

   private static void lambda$complexMapper$1(nB var0, Tg var1, int var2, int var3, X var4) {
      nB.J(var0, var4, var1, var2, var3 + 1);
   }

   private static void lambda$mapper$0(Function var0, nB var1, X var2, Tg var3, int var4, int var5) {
      var3.B((String)var0.apply(var2));
   }
}
