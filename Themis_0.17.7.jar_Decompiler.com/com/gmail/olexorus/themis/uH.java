package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

public final class uh<T> {
   private final nW<T> t;
   private final T j;
   private static final long a = kt.a(1235833683791218380L, -1890739728235560018L, MethodHandles.lookup().lookupClass()).a(272581394396935L);

   public uh(nW<T> var1, T var2) {
      this.t = var1;
      this.j = var2;
   }

   public static uh<?> Q(lm<?> var0) {
      nW var1 = (nW)var0.e(gZ::N);
      return t(var0, var1);
   }

   private static <T> uh<T> t(lm<?> var0, nW<T> var1) {
      return new uh(var1, var1.M(var0));
   }

   public static <T> void H(lm<?> var0, uh<T> var1) {
      var0.j((GL)var1.t);
      var1.t.r(var0, var1.j);
   }

   public nW<T> f() {
      return this.t;
   }

   public T Z() {
      return this.j;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof uh)) {
         return false;
      } else {
         uh var2 = (uh)var1;
         return !this.t.equals(var2.t) ? false : this.j.equals(var2.j);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.t, this.j});
   }

   public String toString() {
      long var1 = a ^ 98481316631772L;
      return "ComponentValue{type=" + this.t + ", value=" + this.j + '}';
   }
}
