package com.gmail.olexorus.themis;

import java.util.Objects;
import java.util.function.Function;

public class E_<L, R> {
   private final L D;
   private final R i;

   private E_(L var1, R var2) {
      this.D = var1;
      this.i = var2;
   }

   public static <L, R> E_<L, R> u(L var0) {
      return new E_(var0, (Object)null);
   }

   public static <L, R> E_<L, R> Y(R var0) {
      return new E_((Object)null, var0);
   }

   public static <T> T t(E_<T, ? extends T> var0) {
      return var0.D != null ? var0.D : var0.i;
   }

   public <T> T R(Function<L, T> var1, Function<R, T> var2) {
      return this.D != null ? var1.apply(this.D) : var2.apply(this.i);
   }

   public boolean m() {
      return this.D != null;
   }

   public L l() {
      return this.D;
   }

   public R J() {
      return this.i;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof E_)) {
         return false;
      } else {
         E_ var2 = (E_)var1;
         return !Objects.equals(this.D, var2.D) ? false : Objects.equals(this.i, var2.i);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.D, this.i});
   }
}
