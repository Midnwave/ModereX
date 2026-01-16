package com.gmail.olexorus.themis;

import java.util.Objects;

public class JZ {
   private Gj<a2> E;

   public JZ(Gj<a2> var1) {
      this.E = var1;
   }

   public static JZ w(lm<?> var0) {
      Gj var1;
      if (var0.R().i(zZ.V_1_21_5)) {
         var1 = Gj.K(var0, VX.W(), a2::R);
      } else {
         var1 = new Gj(a2.R(var0));
      }

      return new JZ(var1);
   }

   public static void f(lm<?> var0, JZ var1) {
      if (var0.R().i(zZ.V_1_21_5)) {
         Gj.G(var0, var1.E, a2::Y);
      } else {
         a2.Y(var0, (a2)var1.E.m());
      }

   }

   public Gj<a2> U() {
      return this.E;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof JZ)) {
         return false;
      } else {
         JZ var2 = (JZ)var1;
         return this.E.equals(var2.E);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.E);
   }
}
