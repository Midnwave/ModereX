package com.gmail.olexorus.themis;

import java.util.function.Function;

class tc implements bg<Z> {
   final Function F;
   final Function o;
   final bg V;

   tc(bg var1, Function var2, Function var3) {
      this.V = var1;
      this.F = var2;
      this.o = var3;
   }

   public Z U(RT var1, lm<?> var2) {
      return this.F.apply(this.V.U(var1, var2));
   }

   public void r(RT var1, lm<?> var2, Z var3) {
      this.V.r(var1, var2, this.o.apply(var3));
   }
}
