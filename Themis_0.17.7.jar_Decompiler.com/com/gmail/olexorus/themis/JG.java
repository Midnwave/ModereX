package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

public final class Jg {
   private final O9 U;
   private final X B;
   private final String k;
   static final boolean t = !Za.class.desiredAssertionStatus();
   private static final long a = kt.a(-6001716887429354553L, 3868211463822548104L, MethodHandles.lookup().lookupClass()).a(269486657541441L);

   public Jg(O9 var1, X var2, String var3) {
      long var4 = a ^ 118261003639822L;
      super();
      if (var1 == null == (var2 == null)) {
         throw new IllegalStateException("Illegal state of both known type and custom type combined: " + var1 + " / " + var2);
      } else {
         this.U = var1;
         this.B = var2;
         this.k = var3;
      }
   }

   public static Jg n(lm<?> var0) {
      O9 var1;
      X var2;
      if (var0.P()) {
         var1 = (O9)var0.w((Enum[])O9.values());
         var2 = null;
      } else {
         var1 = null;
         var2 = var0.a();
      }

      String var3 = var0.A();
      return new Jg(var1, var2, var3);
   }

   public static void V(lm<?> var0, Jg var1) {
      if (var1.H() != null) {
         var0.I(true);
         var0.o((Enum)var1.H());
      } else {
         if (!t && var1.Q() == null) {
            throw new AssertionError();
         }

         var0.I(false);
         var0.G(var1.Q());
      }

      var0.I(var1.Z());
   }

   public O9 H() {
      return this.U;
   }

   public X Q() {
      return this.B;
   }

   public String Z() {
      return this.k;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Jg)) {
         return false;
      } else {
         Jg var2 = (Jg)var1;
         if (this.U != var2.U) {
            return false;
         } else {
            return !Objects.equals(this.B, var2.B) ? false : this.k.equals(var2.k);
         }
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.U, this.B, this.k});
   }

   public String toString() {
      long var1 = a ^ 14342376630694L;
      return "ServerLink{knownType=" + this.U + ", customType=" + this.B + ", url='" + this.k + '\'' + '}';
   }
}
