package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public class y7 extends lm<y7> {
   private String R;
   private byte[] x;
   private static final long a = kt.a(2631896595879808764L, -1310530354411425435L, MethodHandles.lookup().lookupClass()).a(232133979558931L);

   public void t() {
      long var1 = a ^ 38048997277369L;
      if (this.I.i(zZ.V_1_13)) {
         this.R = this.A();
      } else {
         this.R = this.m(20);
      }

      if (this.I.R(zZ.V_1_7_10)) {
         short var3 = this.x();
      }

      if (NY.r(this.g) > 32767) {
         throw new RuntimeException("Payload may not be larger than 32767 bytes");
      } else {
         this.x = this.y();
      }
   }

   public void d() {
      if (this.I.i(zZ.V_1_13)) {
         this.I(this.R);
      } else {
         this.a(this.R, 20);
      }

      if (this.I.R(zZ.V_1_7_10)) {
         this.f(this.x.length);
      }

      this.d(this.x);
   }

   public void J(y7 var1) {
      this.R = var1.R;
      this.x = var1.x;
   }
}
