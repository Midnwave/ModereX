package com.gmail.olexorus.themis;

import java.util.Objects;

public class zI {
   private gC<cB> s;
   private Float V;
   private Boolean p;

   public zI(gC<cB> var1, Float var2, Boolean var3) {
      this.s = var1;
      this.V = var2;
      this.p = var3;
   }

   public static zI F(lm<?> var0) {
      gC var1 = gC.P(var0, rS::P);
      Float var2 = (Float)var0.u(lm::L);
      Boolean var3 = (Boolean)var0.u(lm::P);
      return new zI(var1, var2, var3);
   }

   public static void L(lm<?> var0, zI var1) {
      gC.n(var0, var1.s);
      var0.l(var1.V, lm::S);
      var0.l(var1.p, lm::I);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof zI)) {
         return false;
      } else {
         zI var2 = (zI)var1;
         if (!this.s.equals(var2.s)) {
            return false;
         } else {
            return !Objects.equals(this.V, var2.V) ? false : Objects.equals(this.p, var2.p);
         }
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.s, this.V, this.p});
   }
}
