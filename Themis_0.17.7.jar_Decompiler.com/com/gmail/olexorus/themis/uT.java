package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

public class ut {
   private al b;
   private static final long a = kt.a(-2263983255139311584L, 678608728147621568L, MethodHandles.lookup().lookupClass()).a(28414133054428L);

   public ut(al var1) {
      this.b = var1;
   }

   public static ut I(lm<?> var0) {
      return new ut(var0.R());
   }

   public static void u(lm<?> var0, ut var1) {
      var0.T(var1.b);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ut)) {
         return false;
      } else {
         ut var2 = (ut)var1;
         return this.b.equals(var2.b);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.b);
   }

   public String toString() {
      long var1 = a ^ 69545219426973L;
      return "ItemModel{modelLocation=" + this.b + '}';
   }
}
