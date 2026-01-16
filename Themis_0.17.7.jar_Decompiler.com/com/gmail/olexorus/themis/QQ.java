package com.gmail.olexorus.themis;

public class Qq extends lm<Qq> {
   private int U;
   private int k;
   private r_ D;

   public Qq(int var1, int var2, r_ var3) {
      super((wC)rX.UPDATE_LIGHT);
      this.U = var1;
      this.k = var2;
      this.D = var3;
   }

   public void t() {
      this.U = this.Q();
      this.k = this.Q();
      this.D = r_.R(this);
   }

   public void d() {
      this.E(this.U);
      this.E(this.k);
      r_.Z(this, this.D);
   }

   public void L(Qq var1) {
      this.U = var1.U;
      this.k = var1.k;
      this.D = var1.D.a();
   }
}
