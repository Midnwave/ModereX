package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

public final class Vn {
   private boolean F;
   private boolean C;
   private static final long a = kt.a(8306276690554983969L, 3416316414223591151L, MethodHandles.lookup().lookupClass()).a(32622616091435L);

   public Vn() {
      this(false, false);
   }

   public Vn(boolean var1, boolean var2) {
      this.F = var1;
      this.C = var2;
   }

   public static Vn N(lm<?> var0) {
      boolean var1 = var0.P();
      boolean var2 = var0.P();
      return new Vn(var1, var2);
   }

   public static void L(lm<?> var0, Vn var1) {
      var0.I(var1.F);
      var0.I(var1.C);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Vn)) {
         return false;
      } else {
         Vn var2 = (Vn)var1;
         if (this.F != var2.F) {
            return false;
         } else {
            return this.C == var2.C;
         }
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.F, this.C});
   }

   public String toString() {
      long var1 = a ^ 7196336969353L;
      return "TypeState{open=" + this.F + ", filtering=" + this.C + '}';
   }
}
