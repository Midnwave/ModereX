package com.gmail.olexorus.themis;

public class y1 extends lm<y1> {
   private VC Y;
   private al j;
   private al Q;
   private al f;
   private String K;
   private G4 y;
   private int C;
   private int D;

   public void t() {
      this.Y = this.M();
      this.j = this.R();
      if (this.a()) {
         this.Q = this.R();
      }

      this.f = this.R();
      this.K = this.A();
      if (this.a()) {
         this.y = (G4)G4.B(this.A()).orElse(G4.ALIGNED);
      }

      if (this.I.i(zZ.V_1_20_3)) {
         this.C = this.Q();
         this.D = this.Q();
      }

   }

   public void d() {
      this.o(this.Y);
      this.T(this.j);
      if (this.a()) {
         this.T(this.Q);
      }

      this.T(this.f);
      this.I(this.K);
      if (this.a()) {
         this.I(this.y.j());
      }

      if (this.I.i(zZ.V_1_20_3)) {
         this.E(this.C);
         this.E(this.D);
      }

   }

   public void F(y1 var1) {
      this.Y = var1.Y;
      this.j = var1.j;
      this.Q = var1.Q;
      this.f = var1.f;
      this.K = var1.K;
      this.y = var1.y;
      this.C = var1.C;
      this.D = var1.D;
   }

   private boolean a() {
      return this.I.i(zZ.V_1_16);
   }
}
