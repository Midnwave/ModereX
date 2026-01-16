package com.gmail.olexorus.themis;

public class s_ extends lm<s_> {
   private static final RH f = new cf();
   private static final RH u = new TV();
   private static final RH J = new tU();
   private static final RH A = new gD();
   private static final RH B = new GF();
   private static final RH P = new C8();
   private wA Y;

   public void t() {
      this.Y = this.G().c(this);
   }

   public void d() {
      this.G().V(this, this.Y);
   }

   public void L(s_ var1) {
      this.Y = var1.Y;
   }

   protected RH G() {
      if (this.I.i(zZ.V_1_21_5)) {
         return P;
      } else if (this.I.i(zZ.V_1_19_3)) {
         return B;
      } else if (this.I.i(zZ.V_1_19_1)) {
         return A;
      } else if (this.I.i(zZ.V_1_19)) {
         return J;
      } else {
         return this.I.i(zZ.V_1_16) ? u : f;
      }
   }
}
