package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public class Q7 extends lm<Q7> {
   private al L;
   private int B;
   private static final long a = kt.a(4427101476546612387L, -7774550725031547730L, MethodHandles.lookup().lookupClass()).a(212062220673487L);

   public Q7(i var1, int var2) {
      this(var1.f(), var2);
   }

   public Q7(al var1, int var2) {
      super((wC)rX.SET_COOLDOWN);
      this.L = var1;
      this.B = var2;
   }

   public void t() {
      if (this.I.i(zZ.V_1_21_2)) {
         this.L = this.R();
      } else {
         i var1 = (i)this.y(z1.N());
         this.L = var1.f();
      }

      this.B = this.Q();
   }

   public void d() {
      if (this.I.i(zZ.V_1_21_2)) {
         this.T(this.L);
      } else {
         this.j(this.d());
      }

      this.E(this.B);
   }

   public void m(Q7 var1) {
      this.L = var1.L;
      this.B = var1.B;
   }

   public i d() {
      long var1 = a ^ 20075256807549L;
      i var3 = z1.x(this.L.toString());
      if (var3 == null) {
         throw new IllegalStateException("Can't get legacy cooldown item for cooldown group " + this.L);
      } else {
         return var3;
      }
   }
}
