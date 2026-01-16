package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

final class A9 extends Av<gO, m1> implements m1 {
   private String H;
   private X T;
   private static final long b = kt.a(-8032324289778885675L, -4469513389964500849L, MethodHandles.lookup().lookupClass()).a(92727573229846L);

   A9() {
   }

   A9(gO var1) {
      super(var1);
      this.H = var1.b();
      this.T = var1.O();
   }

   public m1 Q(String var1) {
      long var2 = b ^ 79164464175050L;
      this.H = (String)Objects.requireNonNull(var1, "pattern");
      return this;
   }

   public m1 Z(lv var1) {
      this.T = lv.z(var1);
      return this;
   }

   public gO l() {
      long var1 = b ^ 25578093802323L;
      if (this.H == null) {
         throw new IllegalStateException("pattern must be set");
      } else {
         return lh.g(this.y, this.l(), this.H, this.T);
      }
   }
}
