package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Bw {
   public static final Bw k = new BF(Collections.emptyList(), true);
   private List<Gb> r;
   private boolean N;
   private static final long a = kt.a(896874476334770891L, 8220152249086517703L, MethodHandles.lookup().lookupClass()).a(66142995791966L);

   public Bw(List<Gb> var1, boolean var2) {
      this.r = var1;
      this.N = var2;
   }

   public static Bw y(lm<?> var0) {
      List var1 = var0.j(Gb::t);
      boolean var2 = var0.R().i(zZ.V_1_21_5) || var0.P();
      return new Bw(var1, var2);
   }

   public static void J(lm<?> var0, Bw var1) {
      var0.D(var1.r, Gb::X);
      if (var0.R().m(zZ.V_1_21_5)) {
         var0.I(var1.N);
      }

   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Bw)) {
         return false;
      } else {
         Bw var2 = (Bw)var1;
         return this.N != var2.N ? false : this.r.equals(var2.r);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.r, this.N});
   }

   public String toString() {
      long var1 = a ^ 31663275718532L;
      return "ItemAttributeModifiers{modifiers=" + this.r + ", showInTooltip=" + this.N + '}';
   }
}
