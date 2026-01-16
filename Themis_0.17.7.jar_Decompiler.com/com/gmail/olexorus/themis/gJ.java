package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

public final class Gj<T extends GL> {
   private final T M;
   private final al r;
   private final VD<T> y;
   private static final long a = kt.a(1203405935211484962L, 42602470167739553L, MethodHandles.lookup().lookupClass()).a(234232445755145L);

   public Gj(T var1) {
      this(var1, (al)null, (VD)null);
   }

   public Gj(al var1, VD<T> var2) {
      this((GL)null, var1, var2);
   }

   public Gj(T var1, al var2, VD<T> var3) {
      long var4 = a ^ 43473316611865L;
      super();
      if (var1 == null && var2 == null) {
         throw new IllegalArgumentException("Only one of entity and name is allowed to be null");
      } else {
         this.M = var1;
         this.r = var2;
         this.y = var3;
      }
   }

   public static <T extends GL> Gj<T> K(lm<?> var0, VD<T> var1, MO<T> var2) {
      if (var0.P()) {
         return new Gj((GL)var2.apply(var0));
      } else {
         vL var3 = var0.R().u();
         VD var4 = var0.T().W(var1, var3);
         return new Gj(var0.R(), var4);
      }
   }

   public static <T extends GL> void G(lm<?> var0, Gj<T> var1, Gw<T> var2) {
      if (var1.M != null) {
         var0.I(true);
         var2.accept(var0, var1.M);
      } else {
         var0.I(false);
         var0.T(var1.r);
      }

   }

   public T m() {
      long var1 = a ^ 75907347603472L;
      GL var3 = this.G();
      if (var3 == null) {
         throw new IllegalStateException("Can't resolve entity by name " + this.r);
      } else {
         return var3;
      }
   }

   public T G() {
      if (this.M != null) {
         return this.M;
      } else {
         return this.y != null && this.r != null ? this.y.u(this.r) : null;
      }
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Gj)) {
         return false;
      } else {
         Gj var2 = (Gj)var1;
         return !Objects.equals(this.M, var2.M) ? false : Objects.equals(this.r, var2.r);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.M, this.r});
   }
}
