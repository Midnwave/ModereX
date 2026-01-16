package com.gmail.olexorus.themis;

public class y2 extends lm<y2> {
   private VC X;
   private int w;

   public y2(aE var1) {
      super(var1);
   }

   public y2(VC var1, int var2) {
      super((wC)rX.BLOCK_CHANGE);
      this.X = var1;
      this.w = var2;
   }

   public void t() {
      if (this.I.R(zZ.V_1_7_10)) {
         this.X = new VC(this.f(), this.h(), this.f());
         int var1 = this.Q();
         short var2 = this.h();
         this.w = var1 | var2 << 12;
      } else {
         this.X = this.M();
         this.w = this.Q();
      }

   }

   public void d() {
      if (this.I.R(zZ.V_1_7_10)) {
         this.L(this.X.e());
         this.u(this.X.I());
         this.L(this.X.z());
         this.E(this.w & 255);
         this.u(this.w >> 12);
      } else {
         this.o(this.X);
         this.E(this.w);
      }

   }

   public void o(y2 var1) {
      this.X = var1.X;
      this.w = var1.w;
   }

   public VO e() {
      return VO.r(this.I.u(), this.w);
   }
}
