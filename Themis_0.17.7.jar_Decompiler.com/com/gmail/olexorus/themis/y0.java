package com.gmail.olexorus.themis;

import java.util.UUID;

public class y0 extends lm<y0> {
   private UUID W;
   private String m;
   private W5 s;

   public void t() {
      if (this.I.i(zZ.V_1_20_3)) {
         this.W = this.V();
      }

      if (this.I.m(zZ.V_1_10)) {
         this.m = this.m(40);
      } else {
         this.m = "";
      }

      int var1 = this.Q();
      this.s = W5.VALUES[var1];
   }

   public void d() {
      if (this.I.i(zZ.V_1_20_3)) {
         this.y(this.W);
      }

      if (this.I.m(zZ.V_1_10)) {
         this.a(this.m, 40);
      }

      this.E(this.s.ordinal());
   }

   public void g(y0 var1) {
      this.W = var1.W;
      this.m = var1.m;
      this.s = var1.s;
   }
}
