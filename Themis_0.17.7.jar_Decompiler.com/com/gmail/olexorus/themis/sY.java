package com.gmail.olexorus.themis;

public class sy extends lm<sy> {
   private int D;
   private float w;
   private float b;
   private boolean s;

   public void t() {
      this.D = this.Q();
      this.w = (float)this.M() / 0.7111111F;
      this.b = (float)this.M() / 0.7111111F;
      this.s = this.P();
   }

   public void I(sy var1) {
      this.D = var1.D;
      this.w = var1.w;
      this.b = var1.b;
      this.s = var1.s;
   }

   public void d() {
      this.E(this.D);
      this.u((int)(this.w * 0.7111111F));
      this.u((int)(this.b * 0.7111111F));
      this.I(this.s);
   }
}
